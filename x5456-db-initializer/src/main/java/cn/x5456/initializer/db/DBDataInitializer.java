package cn.x5456.initializer.db;

import cn.hutool.core.collection.CollUtil;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

/**
 * @author yujx
 * @date 2021/01/26 13:10
 */
public class DBDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();

        DBInitConfig initConfig = applicationContext.getBean(DBInitConfig.class);

        if (CollUtil.isEmpty(initConfig.getMulti())) {
            this.executeInit(applicationContext, initConfig);
        } else {
            // 如果使用多数据源配置项（multi），则会忽略 initConfig 中配置的信息
            for (DBInitConfig config : initConfig.getMulti()) {
                this.executeInit(applicationContext, config);
            }
        }
    }

    private void executeInit(ApplicationContext applicationContext, DBInitConfig config) {
        DBInitInvoker invoker = new DBInitInvoker(config, applicationContext);
        invoker.execute();
    }
}
