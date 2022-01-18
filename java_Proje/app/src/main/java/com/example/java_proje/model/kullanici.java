package com.example.java_proje.model;

public class kullanici {
    private String id;
    private String kullaniciadi;
    private String adi;
    private String resimurl;
    private String bio;

    public kullanici() {
    }

    public kullanici(String id, String kullaniciadi, String adi, String resimurl, String bio) {
        this.id = id;
        this.kullaniciadi = kullaniciadi;
        this.adi = adi;
        this.resimurl = resimurl;
        this.bio = bio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKullaniciadi() {
        return kullaniciadi;
    }

    public void setKullaniciadi(String kullaniciadi) {
        this.kullaniciadi = kullaniciadi;
    }

    public String getAdi() {
        return adi;
    }

    public void setAdi(String adi) {
        this.adi = adi;
    }

    public String getResimurl() {
        return resimurl;
    }

    public void setResimurl(String resimurl) {
        this.resimurl = resimurl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
