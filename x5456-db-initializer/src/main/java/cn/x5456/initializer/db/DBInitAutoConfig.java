package cn.x5456.initializer.db;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

/**
 * @author yujx
 * @date 2021/01/26 09:21
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = DBInitConfig.class)
public class DBInitAutoConfig {

    @Bean
    public DBInitInvoker dbInitInvoker(DBInitConfig initConfig, ResourceLoader resourceLoader, ApplicationContext applicationContext) {
        return new DBInitInvoker(initConfig, resourceLoader, applicationContext);
    }

}
