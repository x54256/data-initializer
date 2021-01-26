package cn.x5456.initializer.db;

import cn.x5456.initializer.base.AbstractInitConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author yujx
 * @date 2021/01/25 14:59
 */
@ConfigurationProperties("x5456.data.init.db")
public class DBInitConfig extends AbstractInitConfig {

    private DataSourceInitializationMode mode = DataSourceInitializationMode.NEVER;

    private String dbType = "postgres";

    private List<String> files;

    private String appName;

    private String dataSourceBeanName = "dataSource";

    private List<DBInitConfig> multi;

    @Override
    protected boolean canExecute() {
        return mode == DataSourceInitializationMode.ALWAYS;
    }

    @Override
    protected List<String> getInitFiles() {
        return Collections.singletonList("classpath*:db-init-" + this.dbType + ".sql");
    }

    @Override
    protected List<String> getDefaultFiles() {
        return Arrays.asList("classpath*:db.sql", "classpath*:db-" + this.dbType + ".sql");
    }

    public DataSourceInitializationMode getMode() {
        return mode;
    }

    public void setMode(DataSourceInitializationMode mode) {
        this.mode = mode;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    @Override
    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    @Override
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public String getDataSourceBeanName() {
        return dataSourceBeanName;
    }

    public void setDataSourceBeanName(String dataSourceBeanName) {
        this.dataSourceBeanName = dataSourceBeanName;
    }

    public List<DBInitConfig> getMulti() {
        return multi;
    }

    public void setMulti(List<DBInitConfig> multi) {
        this.multi = multi;
    }
}
