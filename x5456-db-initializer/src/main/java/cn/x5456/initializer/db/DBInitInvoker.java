package cn.x5456.initializer.db;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yujx
 * @date 2021/01/25 15:34
 */
public class DBInitInvoker implements InitializingBean {

    private final DBInitConfig initConfig;

    private final ResourcePatternResolver resourcePatternResolver;

    private final ApplicationContext applicationContext;

    public DBInitInvoker(DBInitConfig initConfig, ResourceLoader resourceLoader, ApplicationContext applicationContext) {
        this.initConfig = initConfig;
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        this.execute();
    }

    public void execute() {
        if (initConfig.canExecute()) {
            DataSource dataSource = applicationContext.getBean(initConfig.getDataSourceBeanName(), DataSource.class);
            ScriptExecutor scriptExecutor = new ScriptExecutor(dataSource, initConfig);

            List<Resource> initFileResourceList = this.getResource(initConfig.getInitFiles());
            for (Resource resource : initFileResourceList) {
                scriptExecutor.execute(resource, true);
            }

            List<String> customLocations = initConfig.getFiles() != null ? initConfig.getFiles() : initConfig.getDefaultFiles();
            for (Resource resource : this.getResource(customLocations)) {
                scriptExecutor.execute(resource, false);
            }
        }
    }

    private List<Resource> getResource(List<String> locations) {
        List<Resource> resources = new ArrayList<>();
        for (String location : locations) {
            Resource[] locationResources;
            try {
                locationResources = resourcePatternResolver.getResources(location);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            resources.addAll(Arrays.asList(locationResources));
        }
        return resources;
    }
}
