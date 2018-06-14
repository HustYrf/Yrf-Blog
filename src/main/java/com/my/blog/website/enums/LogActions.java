package com.my.blog.website.enums;

public enum  LogActions {
    LOGIN("登陆后台"),UP_INFO("修改个人信息"),UP_PWD("修改密码"),DELETE("删除文章"),UP_PAGE("修改page")
    ,DELE_ARTICLE("删除文章");

    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    LogActions(String action) {
        this.action = action;
    }
}
