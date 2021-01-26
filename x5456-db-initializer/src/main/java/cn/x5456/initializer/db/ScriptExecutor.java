package cn.x5456.initializer.db;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.x5456.initializer.base.ScriptException;
import cn.x5456.initializer.base.ScriptUtils;
import cn.x5456.initializer.base.VersionedScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author yujx
 * @date 2021/01/25 15:41
 */
public class ScriptExecutor {

    public static final String VERSION_TABLE = "db_script";
    public static final String FIELD_VERSION = "version";
    public static final String FIELD_APP = "app";
    private static final Logger log = LoggerFactory.getLogger(ScriptExecutor.class);

    private final DBInitConfig initConfig;

    private final Db dbConn;

    public ScriptExecutor(DataSource dataSource, DBInitConfig initConfig) {
        this.initConfig = initConfig;
        this.dbConn = Db.use(dataSource);
    }

    public void execute(Resource resource, boolean isInit) {
        List<VersionedScript> versionedScripts;
        try {
            versionedScripts = ScriptUtils.getVersionedScripts(new EncodedResource(resource, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        this.doExecute(versionedScripts, isInit);
    }

    private void doExecute(List<VersionedScript> versionedScriptList, boolean isInit) {

        // 构建历史记录表的 Entity，为下面 insert 操作做准备
        Entity versionTableEntity = Entity.create(VERSION_TABLE);
        versionTableEntity.set("app", initConfig.getAppName());

        for (VersionedScript versionedScript : versionedScriptList) {
            String version = versionedScript.getVersion();

            // 如果是用户指定的 sql 脚本文件，则检测其版本信息
            if (!isInit) {
                try {
                    if (this.versionIsExist(versionedScript)) {
                        log.info("跳过 DB 脚本，version = {}", version);
                        continue;
                    }
                } catch (SQLException e) {
                    throw new ScriptException(e.getMessage(), e);
                }
                log.info("执行 DB 脚本，version = {}", versionedScript.getVersion());
            }

            List<String> scripts = versionedScript.getScripts();
            for (int i = 0; i < scripts.size(); i++) {
                try {
                    dbConn.execute(scripts.get(i));

                    // 如果是用户指定的 sql 脚本文件，则向版本表格中插入历史记录
                    if (!isInit) {
                        versionTableEntity
                                .set("id", IdUtil.objectId())
                                .set("version", version)
                                .set("script", scripts.get(i))
                                .set("create_time", new Timestamp(System.currentTimeMillis()))
                                .set("remark", versionedScript.getRemark());
                        dbConn.insert(versionTableEntity);
                    }
                } catch (SQLException e) {
                    throw new ScriptException(
                            StrUtil.format("脚本执行错误【{}】【{}】【{}】【{}】",
                            initConfig.getAppName(), initConfig.getDataSourceBeanName(), versionedScript.getVersion(), i + 1),
                            e);
                }
            }

        }

    }

    private boolean versionIsExist(VersionedScript versionedScript) throws SQLException {

        // 构建查询条件
        Entity entity = Entity.create(VERSION_TABLE);
        entity.set(FIELD_VERSION, versionedScript.getVersion());
        if (StrUtil.isNotBlank(initConfig.getAppName())) {
            entity.set(FIELD_APP, initConfig.getAppName());
        }

        return dbConn.count(entity) > 0;
    }
}
