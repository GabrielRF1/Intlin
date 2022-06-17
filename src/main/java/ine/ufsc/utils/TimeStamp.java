/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.utils;

/**
 *
 * @author Gabriel
 */
public class TimeStamp {

    final double hour;
    final double minute;
    final double second;
    final double millisecond;

    public TimeStamp(int hour, int minute, int second, int millisecond) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.millisecond = millisecond;
    }

    public double toMillis() {
        return millisecond + second * 1000 + minute * 60000 + hour * 3.6*Math.pow(10, -6);
    }

    @Override
    public String toString() {
        return String.format("%f:%f:%f,%f", hour, minute, second, millisecond);
    }
}
