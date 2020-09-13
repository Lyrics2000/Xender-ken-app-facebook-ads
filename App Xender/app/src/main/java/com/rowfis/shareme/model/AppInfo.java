package com.rowfis.shareme.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Dan Chumo on 11/28/2017.
 */

public class AppInfo {

    private String apkPath;
    private String appLastModified;
    private long appLastModifiedValue;
    private String appSize;
    private long appSizeValue;
    private Drawable drawable;
    private boolean isChecked;
    private int lowest;
    private String name;
    private String packageName;
    private String softName;
    private String temp_apkPath;
    private int type;
    private String version;
    private int versionNum;



    public AppInfo() {

    }


    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    public String getAppLastModified() {
        return appLastModified;
    }

    public void setAppLastModified(String appLastModified) {
        this.appLastModified = appLastModified;
    }

    public long getAppLastModifiedValue() {
        return appLastModifiedValue;
    }

    public void setAppLastModifiedValue(long appLastModifiedValue) {
        this.appLastModifiedValue = appLastModifiedValue;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public long getAppSizeValue() {
        return appSizeValue;
    }

    public void setAppSizeValue(long appSizeValue) {
        this.appSizeValue = appSizeValue;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getLowest() {
        return lowest;
    }

    public void setLowest(int lowest) {
        this.lowest = lowest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSoftName() {
        return softName;
    }

    public void setSoftName(String softName) {
        this.name = softName;
        this.softName = softName;
    }

    public String getTemp_apkPath() {
        return temp_apkPath;
    }

    public void setTemp_apkPath(String temp_apkPath) {
        this.temp_apkPath = temp_apkPath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }
}
