package org.pepppt.sample.ui.utils;

public class PermissionInfo {
    public PermissionInfo(String _name, int _requestCode, String _explanation, int _version_code, int group_id) {
        name = _name;
        requestCode = _requestCode;
        explanation = _explanation;
        version_code = _version_code;
        this.group_id = group_id;
    }

    private String name;
    private int requestCode;
    private String explanation;
    private int version_code;
    private int group_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }
}
