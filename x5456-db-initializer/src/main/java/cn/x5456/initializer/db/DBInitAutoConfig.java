package cn.x5456.initializer.db;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author yujx
 * @date 2021/01/26 09:21
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = DBInitConfig.class)
public class DBInitAutoConfig {
}
