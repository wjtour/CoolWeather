package com.example.user.coolweather.model;

/**
 * Created by USER on 2017-04-30 0030.
 */
public class County {
    private int id;
    private String countyName;
    private String CountyCode;
    private int cityId;

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCountyCode() {
        return CountyCode;
    }

    public void setCountyCode(String countyCode) {
        CountyCode = countyCode;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
