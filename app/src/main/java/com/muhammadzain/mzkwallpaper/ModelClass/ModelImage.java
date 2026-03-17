package com.muhammadzain.mzkwallpaper.ModelClass;

import java.io.Serializable;

public class ModelImage implements Serializable {
    private int id;
    private String photographerdetails,phographerlink,wallpaperUrl;
    private String orignalUrl,large,largex2,mediumurl,smallUrl;

    public ModelImage() {}

    public ModelImage(int id, String photographerdetails, String phographerlink, String wallpaperUrl, String orignalUrl, String large, String largex2, String mediumurl, String smallUrl) {
        this.id = id;
        this.photographerdetails = photographerdetails;
        this.phographerlink = phographerlink;
        this.wallpaperUrl = wallpaperUrl;
        this.orignalUrl = orignalUrl;
        this.large = large;
        this.largex2 = largex2;
        this.mediumurl = mediumurl;
        this.smallUrl = smallUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhotographerdetails() {
        return photographerdetails;
    }

    public void setPhotographerdetails(String photographerdetails) {
        this.photographerdetails = photographerdetails;
    }

    public String getPhographerlink() {
        return phographerlink;
    }

    public void setPhographerlink(String phographerlink) {
        this.phographerlink = phographerlink;
    }

    public String getWallpaperUrl() {
        return wallpaperUrl;
    }

    public void setWallpaperUrl(String wallpaperUrl) {
        this.wallpaperUrl = wallpaperUrl;
    }

    public String getOrignalUrl() {
        return orignalUrl;
    }

    public void setOrignalUrl(String orignalUrl) {
        this.orignalUrl = orignalUrl;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getLargex2() {
        return largex2;
    }

    public void setLargex2(String largex2) {
        this.largex2 = largex2;
    }

    public String getMediumurl() {
        return mediumurl;
    }

    public void setMediumurl(String mediumurl) {
        this.mediumurl = mediumurl;
    }

    public String getSmallUrl() {
        return smallUrl;
    }

    public void setSmallUrl(String smallUrl) {
        this.smallUrl = smallUrl;
    }
}
