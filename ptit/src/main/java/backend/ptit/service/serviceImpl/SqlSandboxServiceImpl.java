package backend.ptit.service.serviceImpl;

import backend.ptit.service.SqlSandboxService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;


import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

@Data
@Builder
@Slf4j
@Service
@RequiredArgsConstructor
public class SqlSandboxServiceImpl implements SqlSandboxService {
    private final DataSource dataSource;

    // tu khoa nguy hiem bi chan
    private static final Pattern DANGEROUS = Pattern.compile(
            "(?i)\\b(DROP|TRUNCATE|ALTER|CREATE|INSERT|UPDATE|DELETE|GRANT|REVOKE|EXEC|EXECUTE|CALL|LOAD|OUTFILE)\\b"
    );

    public SandboxResult runInSandbox(String schemaSetupSql,String extraSetupSql,String userQuery){
        if(DANGEROUS.matcher(userQuery).find()){
            return SandboxResult.error("Query chua nhieu tu khoa bi cam");
        }

        //tao schema name ngau nhien  de tranh xung dot
        String schemaName="sandbox_"+UUID.randomUUID().toString().replace("-","").substring(0,12);

        long start=System.currentTimeMillis();
        try(Connection connection=dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // 1. tao schema tam
                execute(connection,"CREATE SCHEMA`"+schemaName+"`");
                execute(connection,"USE`"+schemaName+"`");

                // 2. chay setup sql(tao bang insert data)
                runMultipleStatements(connection,schemaSetupSql);

                //3 chay extra setup neu co (data ring cua test case)

                if(extraSetupSql!=null &&!extraSetupSql.isBlank()){
                    runMultipleStatements(connection,extraSetupSql);
                }
                //4. chay query cua user - chi select duoc phep
                List<Map<String,Object>>rows=runSelectQuery(connection,userQuery);
                connection.rollback(); // rollback moi thu trong schema tam

                long elapsed=System.currentTimeMillis()-start;
                return SandboxResult.success(rows,elapsed);

            } catch (Exception e) {
                connection.rollback();
                return SandboxResult.error(e.getMessage());
            }
            finally {
                // luon xoa schema tam thoi du co loi

                try {
                    execute(connection,"DROP SCHEMA IF EXISTS`"+schemaName+"`");
                    connection.commit();

                } catch (Exception e) {
                   return SandboxResult.error("Loi ket noi database:"+e.getMessage());
                }
            }

        } catch (SQLException e) {
            return null;
        }
    }

    private void runMultipleStatements(Connection connection,String sql)throws SQLException{
        // tach cac cau sql bang dau [;]

        String [] statements=sql.split(";");
        for(String x:statements){
            String trimmed=x.trim();
            if(!trimmed.isEmpty()){
                execute(connection,trimmed);
            }
        }
    }
    private void execute(Connection connection,String sql)throws SQLException{


        try(Statement statement=connection.createStatement()){
            statement.execute(sql);
        }
    }

    private List<Map<String,Object>>runSelectQuery(Connection conn,String sql)throws SQLException {


        try(Statement stmt=conn.createStatement()){
            stmt.setMaxRows(500); // gioi han 500 dong code
            ResultSet rs=stmt.executeQuery(sql);
            return resultSetTolist(rs);
        }
    }
    public List<Map<String,Object>>resultSetTolist(ResultSet rs)throws SQLException{
        ResultSetMetaData metaData=rs.getMetaData();
        int colCount=metaData.getColumnCount();
        ArrayList<String>columns=new ArrayList<>();

        for(int i=1;i<=colCount;i++)columns.add(metaData.getColumnLabel(i));

        List<Map<String,Object>>rows=new ArrayList<>();
        while (rs.next()){
            Map<String,Object>row=new LinkedHashMap<>();
            for (int i=1;i<=colCount;i++){
                row.put(columns.get(i-1),rs.getObject(i));
            }
            rows.add(row);
        }
        return rows;
    }


    // inner result class
    @Builder
    @Data
    public static  class SandboxResult {
        private boolean success;
        private String errorMessage;
        private List<Map<String,Object>>rows;
        private long executionTimeMs;
        
        public static SandboxResult success(List<Map<String,Object>>rows,long ms){
            return SandboxResult.builder().success(true).rows(rows).executionTimeMs(ms).build();
        }
        public static SandboxResult error(String msg){
            return SandboxResult.builder().success(false).errorMessage(msg).build();
        }
        
    }
}
