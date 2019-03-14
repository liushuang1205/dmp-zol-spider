package com.sndo.dmp.ugame;

public enum UgameField {

    ID("id"),
    NAME("name"),
    CATEGORY_ID("category_id"), //分类
    ESSAY("essay"), //游戏短评
    DESC("desc"), //游戏简介
    WEB_SCORE("web_score"), //游戏评分 9.0-10 随机保留一位
    HOT_SCORE("hot_score"), //游戏热度 90-100 随机
    ANDROID("android"), // 安卓描述信息
    PROVIDER("provider"), // 开发者, 厂商
    UPTIME("uptime"), //游戏更新时间
    UPDATE_TIME("update_time"), //数据更新时间
    FILTER_FLAG("filter_flag"), //状态标识 0 正常 1 暂停 默认正常
    IS_VALID("is_valid"), //android游戏apk是否可以正常下载 0 不可下载 1 可下载 默认为 1
    IS_AD("is_ad"), //游戏广告类型 0 不是广告 1 后台上传的广告 2 第三方广告

    GAME_ANDROID_LOGO_URL("logo_url"),  // logo地址
    GAME_ANDROID_SIZE("size"),  // 大小
    GAME_ANDROID_DOWNLOAD_URL("download_url"),  // 下载地址
    GAME_ANDROID_DOWNLOAD_COUNT("download_count"),  // 下载次数
    GAME_ANDROID_IS_FREE("is_free"), // 是否免费
    GAME_ANDROID_VERSION("version"),    // 版本
    GAME_ANDROID_VERSION_DATE("version_date"),  // 版本更新时间
    GAME_ANDROID_GAME_CAPTURE("game_capture"),  // 游戏截屏
    GAME_ANDROID_GAME_CAPTURE_FLAG("game_capture_flag"), //截屏图片方向
    GAME_ANDROID_REQUIRE("require"),    // 系统要求
    GAME_ANDROID_ACCESS("access"); // 权限申明

    private String value;

    UgameField(String value){
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
