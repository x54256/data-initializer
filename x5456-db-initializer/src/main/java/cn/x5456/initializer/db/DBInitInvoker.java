package cn.x5456.initializer.db;

import cn.x5456.initializer.base.AbstractInitInvoker;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;

/**
 * @author yujx
 * @date 2021/01/25 15:34
 */
public class DBInitInvoker extends AbstractInitInvoker {

    public DBInitInvoker(DBInitConfig initConfig, ApplicationContext applicationContext) {
        super(initConfig,
                new DBScriptExecutor(applicationContext.getBean(initConfig.getDataSourceBeanName(), DataSource.class), initConfig));
    }
}
