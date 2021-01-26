package cn.x5456.initializer.base;

import org.springframework.core.io.support.EncodedResource;

public class ScriptParseException extends RuntimeException {
    public ScriptParseException(String message, EncodedResource resource) {
        super(buildMessage(message, resource));
    }

    private static String buildMessage(String message, EncodedResource resource) {
        return String.format("脚本资源转换失败 [%s]: %s", resource == null ? "<unknown>" : resource, message);
    }
}
