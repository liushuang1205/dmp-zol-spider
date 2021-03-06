package com.sndo.dmp.dome;

public class LoaderConfig {

    private String onlineDB;
    private String onlineInrcCol;
    private String onlineGameCol;
    private String onlinePicDir;

    private String srcDB;
    private String srcCol;
    private String srcPicDir;

    public String getOnlineDB() {
        return onlineDB;
    }

    public void setOnlineDB(String onlineDB) {
        this.onlineDB = onlineDB;
    }

    public String getOnlineIncrCol() {
        return onlineInrcCol;
    }

    public void setOnlineInrcCol(String onlineIncrCol) {
        this.onlineInrcCol = onlineIncrCol;
    }

    public String getOnlineGameCol() {
        return onlineGameCol;
    }

    public void setOnlineGameCol(String onlineGameCol) {
        this.onlineGameCol = onlineGameCol;
    }

    public String getOnlinePicDir() {
        return onlinePicDir;
    }

    public void setOnlinePicDir(String onlinePicDir) {
        this.onlinePicDir = onlinePicDir;
    }

    public String getSrcDB() {
        return srcDB;
    }

    public void setSrcDB(String srcDB) {
        this.srcDB = srcDB;
    }

    public String getSrcCol() {
        return srcCol;
    }

    public void setSrcGameCol(String srcCol) {
        this.srcCol = srcCol;
    }

    public String getSrcPicDir() {
        return srcPicDir;
    }

    public void setSrcPicDir(String srcPicDir) {
        this.srcPicDir = srcPicDir;
    }
}
