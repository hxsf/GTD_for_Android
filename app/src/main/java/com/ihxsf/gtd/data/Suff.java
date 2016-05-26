package com.ihxsf.gtd.data;

import android.location.Location;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Required;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


/**
 * Created by hxsf on 16－05－23.
 */
public class Suff extends RealmObject {
    @Required
    private String title;
    private Date time;
    private Double latitude;
    private Double longitude;
    private int level;
    private RealmList<Suff> childSuff;
    @Ignore
    private long rank;

    public RealmList<Suff> getChildSuff() {
        return childSuff;
    }

    public void addChildSuff(Suff childSuff) {
        this.childSuff.add(childSuff);
    }
    public void removeChildSuff(Suff childSuff) {
        this.childSuff.remove(childSuff);
    }
    public void removeChildSuff(int index) {
        this.childSuff.remove(index);
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
    public void setLocation(Double latitude, Double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Suff() {
    }

    public Suff(String title, Date time, Location local, int level) {
        this.title = title;
        this.time = time;
        this.longitude = local.getLongitude();
        this.latitude = local.getLatitude();
        this.level = level;
    }
    private long calcLocale(Location l) {
        return (long) sqrt(pow(l.getLatitude() - latitude, 2) + pow(l.getLongitude() - longitude, 2)) * 10;
    }
    public long calcRank(Location l) {
        rank = ((System.currentTimeMillis() - time.getTime()) / 1000 + calcLocale(l)) / level;
        return rank;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public long getRank() {
        return rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }
}
