package cn.x5456.initializer.base;

import org.springframework.core.io.support.EncodedResource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yujx
 * @date 2021/01/25 16:23
 */
public class ScriptUtils {
    private static final String SEPARATOR = ";";
    private static final String LINE_COMMENT_PREFIX = "--";
    private static final String BLOCK_COMMENT_START_DELIMITER = "/*";
    private static final String CODE_BLOCK_COMMENT_TAG = "--code";
    private static final String BLOCK_COMMENT_END_DELIMITER = "*/";

    public static List<VersionedScript> getVersionedScripts(EncodedResource resource) throws ScriptException, IOException {
        List<VersionedScript> versionedScripts = new ArrayList<>();
        StringBuilder currentScript = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inEscape = false;
        VersionedScript currentVersionedScript = null;
        String script = getScriptContent(resource);

        for (int i = 0; i < script.length(); ++i) {
            char c = script.charAt(i);
            if (inEscape) {
                inEscape = false;
                currentScript.append(c);
            } else if (c == '\\') {
                inEscape = true;
                currentScript.append(c);
            } else {
                if (!inDoubleQuote && c == '\'') {
                    inSingleQuote = !inSingleQuote;
                } else if (!inSingleQuote && c == '"') {
                    inDoubleQuote = !inDoubleQuote;
                }

                if (!inSingleQuote && !inDoubleQuote) {
                    if (script.startsWith(";", i)) {
                        if (currentScript.length() > 0) {
                            if (currentVersionedScript == null) {
                                throw new ScriptParseException("缺少版本注释: " + currentScript.toString().trim(), resource);
                            }

                            currentVersionedScript.addScript(currentScript.toString().trim());
                            currentScript = new StringBuilder();
                        }

                        i += ";".length() - 1;
                        continue;
                    }

                    int commentEndIndex;
                    if (script.startsWith("--code", i)) {
                        commentEndIndex = script.indexOf("\n--code", i + 1);
                        if (commentEndIndex <= i) {
                            throw new ScriptParseException("缺少代码块注释结束符: --code", resource);
                        }

                        String codeScript = script.substring(i + "--code".length(), commentEndIndex);
                        if (currentVersionedScript == null) {
                            throw new ScriptParseException("缺少版本注释: " + codeScript, resource);
                        }

                        if (StringUtils.hasText(currentScript)) {
                            throw new ScriptParseException("脚本块前存在未结束脚本: " + codeScript, resource);
                        }

                        currentVersionedScript.addScript(codeScript);
                        i = commentEndIndex + "\n--code".length() - 1;
                        continue;
                    }

                    if (script.startsWith("--", i)) {
                        commentEndIndex = script.indexOf(10, i);
                        if (commentEndIndex <= i) {
                            break;
                        }

                        i = commentEndIndex;
                        continue;
                    }

                    if (script.startsWith("/*", i)) {
                        commentEndIndex = script.indexOf("*/", i);
                        if (commentEndIndex <= i) {
                            throw new ScriptParseException("缺少块注释结束符: */", resource);
                        }

                        VersionedScript newVersionedScript = getVersionedScript(script.substring(i + "/*".length(), commentEndIndex).trim());
                        if (newVersionedScript != null) {
                            currentVersionedScript = newVersionedScript;
                            versionedScripts.add(newVersionedScript);
                        }

                        i = commentEndIndex + "*/".length() - 1;
                        continue;
                    }
                }

                currentScript.append(c);
            }
        }

        if (StringUtils.hasText(currentScript)) {
            if (currentVersionedScript == null) {
                throw new ScriptParseException("缺少版本注释: " + currentScript.toString().trim(), resource);
            }

            currentVersionedScript.addScript(currentScript.toString().trim());
        }

        return mergeAndSortScripts(versionedScripts);
    }

    private static String getScriptContent(EncodedResource resource) throws IOException {
        LineNumberReader reader = new LineNumberReader(resource.getReader());
        Throwable var2 = null;

        try {
            String statement = reader.readLine();

            StringBuilder scriptBuilder;
            for (scriptBuilder = new StringBuilder(); statement != null; statement = reader.readLine()) {
                if ("--code".equals(statement) || !statement.startsWith("--")) {
                    if (scriptBuilder.length() > 0) {
                        scriptBuilder.append('\n');
                    }

                    scriptBuilder.append(statement);
                }
            }

            return scriptBuilder.toString();
        } catch (Throwable var14) {
            var2 = var14;
            throw var14;
        } finally {
            if (var2 != null) {
                try {
                    reader.close();
                } catch (Throwable var13) {
                    var2.addSuppressed(var13);
                }
            } else {
                reader.close();
            }
        }
    }

    private static List<VersionedScript> mergeAndSortScripts(List<VersionedScript> versionedScripts) {
        Map<String, VersionedScript> versionedScriptMap = new HashMap<>();
        versionedScripts.forEach((versionedScript) -> {
            VersionedScript existVersionedScript = versionedScriptMap.get(versionedScript.getVersion());
            if (existVersionedScript == null) {
                versionedScriptMap.put(versionedScript.getVersion(), versionedScript);
            } else {
                existVersionedScript.addScripts(versionedScript.getScripts());
            }

        });
        return versionedScriptMap.values().stream().sorted(Comparator.comparing(VersionedScript::getVersion, String::compareTo)).collect(Collectors.toList());
    }

    private static VersionedScript getVersionedScript(String blockComment) {
        VersionedScript versionedScript = null;
        int separatorIndex = blockComment.indexOf("\n");
        if (separatorIndex > -1) {
            Map<String, String> metaMap = new HashMap<>();
            toMetaMap(blockComment.substring(0, separatorIndex).trim(), metaMap);
            toMetaMap(blockComment.substring(separatorIndex + 1).trim(), metaMap);
            String version = metaMap.get("version");
            if (!StringUtils.isEmpty(version)) {
                versionedScript = new VersionedScript(version, metaMap.get("remark"));
            }
        }

        return versionedScript;
    }

    private static void toMetaMap(String line, Map<String, String> metaMap) {
        int separatorIndex = line.indexOf("=");
        if (separatorIndex > -1) {
            String key = line.substring(0, separatorIndex).trim();
            String value = line.substring(separatorIndex + 1).trim();
            if (!StringUtils.isEmpty(key)) {
                metaMap.put(key, value);
            }
        }

    }
}
