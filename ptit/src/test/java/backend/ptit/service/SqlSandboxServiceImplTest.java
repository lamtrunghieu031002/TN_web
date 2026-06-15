package backend.ptit.service;

import backend.ptit.service.serviceImpl.SqlSandboxServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SqlSandboxServiceImplTest {

    private SqlSandboxServiceImpl sandbox;
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        // Khong can DataSource that vi cac query bi cam se reject truoc khi cham DB
        dataSource = mock(DataSource.class);
        sandbox = new SqlSandboxServiceImpl(dataSource);
    }

    @Test
    void runInSandbox_dropQuery_isBlocked() throws Exception {
        SqlSandboxServiceImpl.SandboxResult r = sandbox.runInSandbox(
                "CREATE TABLE t(id INT);", "", "DROP TABLE users");

        assertFalse(r.isSuccess());
        assertNotNull(r.getErrorMessage());
        verify(dataSource, never()).getConnection();
    }

    @Test
    void runInSandbox_insertQuery_isBlocked() throws Exception {
        SqlSandboxServiceImpl.SandboxResult r = sandbox.runInSandbox(
                "CREATE TABLE t(id INT);", "", "INSERT INTO users VALUES(1)");

        assertFalse(r.isSuccess());
        verify(dataSource, never()).getConnection();
    }

    @Test
    void runInSandbox_dangerousKeywordCaseInsensitive_isBlocked() throws Exception {
        SqlSandboxServiceImpl.SandboxResult r = sandbox.runInSandbox(
                "", "", "delete from users where id=1");

        assertFalse(r.isSuccess());
        verify(dataSource, never()).getConnection();
    }
}
