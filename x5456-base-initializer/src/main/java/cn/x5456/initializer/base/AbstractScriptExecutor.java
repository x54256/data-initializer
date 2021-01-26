package cn.x5456.initializer.base;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author yujx
 * @date 2021/01/26 10:33
 */
public abstract class AbstractScriptExecutor {

    public static final String VERSION_TABLE = "db_script";
    public static final String FIELD_VERSION = "version";
    public static final String FIELD_APP = "app";

    public void execute(Resource resource, boolean isInit) {
        List<VersionedScript> versionedScripts;
        try {
            versionedScripts = ScriptUtils.getVersionedScripts(new EncodedResource(resource, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        this.doExecute(versionedScripts, isInit);
    }

    /**
     * 执行解析出来的脚本
     */
    protected abstract void doExecute(List<VersionedScript> versionedScripts, boolean isInit);

    /**
     * 检查脚本的版本是否已经执行过
     */
    protected abstract boolean versionIsExist(VersionedScript versionedScript) throws Exception;
}
