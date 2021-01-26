package cn.x5456.initializer.base;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yujx
 * @date 2021/01/26 10:27
 */
public abstract class AbstractInitInvoker {

    private final AbstractInitConfig initConfig;

    private final AbstractScriptExecutor scriptExecutor;

    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    public AbstractInitInvoker(AbstractInitConfig initConfig, AbstractScriptExecutor scriptExecutor) {
        this.initConfig = initConfig;
        this.scriptExecutor = scriptExecutor;
    }

    public void execute() {
        if (initConfig.canExecute()) {
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
                throw new RuntimeException(e.getMessage(), e);
            }
            resources.addAll(Arrays.asList(locationResources));
        }
        return resources;
    }

}
