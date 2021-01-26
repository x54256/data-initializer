package cn.x5456.initializer.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 存放相同版本的执行脚本
 */
public class VersionedScript {
    private String version;
    private String remark;
    private List<String> scripts = new ArrayList<>();

    public VersionedScript(String version, String remark) {
        this.version = version;
        this.remark = remark;
    }

    public void addScript(String script) {
        this.scripts.add(script);
    }

    public void addScripts(List<String> scripts) {
        this.scripts.addAll(scripts);
    }

    public String getVersion() {
        return this.version;
    }

    public String getRemark() {
        return this.remark;
    }

    public List<String> getScripts() {
        return this.scripts;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }

    public void setScripts(final List<String> scripts) {
        this.scripts = scripts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VersionedScript that = (VersionedScript) o;
        return Objects.equals(version, that.version) && Objects.equals(remark, that.remark) && Objects.equals(scripts, that.scripts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, remark, scripts);
    }

    @Override
    public String toString() {
        return "VersionedScript{" +
                "version='" + version + '\'' +
                ", remark='" + remark + '\'' +
                ", scripts=" + scripts +
                '}';
    }
}
