package com.shciri.rosapp.dmros.tool;

public class AirQualityEvent {
    private int co2;
    private int formaldehyde;
    private int tvoc;
    private int pm2_5;
    private int pm10;
    private float temperature;
    private float humidity;

    public static boolean readyPublish;

    public AirQualityEvent(int co2, int formaldehyde, int tvoc, int pm2_5, int pm10, float temperature, float humidity) {
        this.co2 = co2;
        this.formaldehyde = formaldehyde;
        this.tvoc = tvoc;
        this.pm2_5 = pm2_5;
        this.pm10 = pm10;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public int getCO2() {
        return co2;
    }
    public int getFormaldehyde() {
        return formaldehyde;
    }
    public int getTVOC() {
        return tvoc;
    }
    public int getPM2_5() {
        return pm2_5;
    }
    public int getPM10() {
        return pm10;
    }
    public float getTemperature() {
        return temperature;
    }
    public float getHumidity() {
        return humidity;
    }
}
