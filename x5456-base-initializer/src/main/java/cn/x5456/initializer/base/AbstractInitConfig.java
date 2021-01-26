package cn.x5456.initializer.base;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInitConfig {

    /**
     * @return 初始化脚本文件
     */
    protected abstract List<String> getFiles();

    /**
     * @return 当前应用名（应对多应用单数据源情况）
     */
    protected abstract String getAppName();

    /**
     * @return 当前数据源名（应对单应用多数据源情况）
     */
    protected abstract String getDataSourceBeanName();

    /**
     * @return 由用户配置，是否执行
     */
    protected abstract boolean canExecute();

    /**
     * @return 需要执行的准备文件，为当前工具使用的表
     */
    protected List<String> getInitFiles() {
        return new ArrayList<>();
    }

    /**
     * @return 默认初始化脚本文件，如果 {@link #getFiles()} 返回的为空，则会使用这个
     */
    protected abstract List<String> getDefaultFiles();
}
