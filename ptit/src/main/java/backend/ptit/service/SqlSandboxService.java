package backend.ptit.service;

import backend.ptit.service.serviceImpl.SqlSandboxServiceImpl;

public interface SqlSandboxService {

    SqlSandboxServiceImpl.SandboxResult runInSandbox(String schemaSetupSql, String extraSetupSql, String userQuery);
}
