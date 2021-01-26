package cn.x5456.initializer.db;

import cn.x5456.initializer.base.AbstractInitInvoker;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;

/**
 * @author yujx
 * @date 2021/01/25 15:34
 */
public class DBInitInvoker extends AbstractInitInvoker implements InitializingBean {

    public DBInitInvoker(DBInitConfig initConfig, ResourceLoader resourceLoader, ApplicationContext applicationContext) {
        super(initConfig,
                new DBScriptExecutor(applicationContext.getBean(initConfig.getDataSourceBeanName(), DataSource.class), initConfig),
                resourceLoader);
    }

    @Override
    public void afterPropertiesSet() {
        super.execute();
    }
}
