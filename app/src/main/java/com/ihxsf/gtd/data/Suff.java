package com.ihxsf.gtd.data;

import android.location.Location;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


/**
 * Created by hxsf on 16－05－23.
 */
public class Suff extends RealmObject {
    @PrimaryKey
    private int id;
    @Required
    private String title;
    private Date time;
    private Double latitude;
    private Double longitude;
    private int level;
    private int project;
    private boolean isDone;
    private String desc;
    private String ezLocation;
    private RealmList<Tag> tags;
    private long rank;

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
    public void setLocation(Double latitude, Double longitude) {
        this.setLongitude(longitude);
        this.setLatitude(latitude);
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Suff() {
        isDone= false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private long calcLocale(Location l) {
        return (long) sqrt(pow(l.getLatitude() - getLatitude(), 2) + pow(l.getLongitude() - getLongitude(), 2)) * 1000;
    }
    public long calcRank(Location l) {
        long rank = 0;
        if (getTime() != null) {
            rank += (System.currentTimeMillis() - getTime().getTime()) / 1000;
        }
        if (getLongitude() != null && l != null) {
            rank += calcLocale(l);
        }
        if (getLevel() ==0) {
            setLevel(1);
        }
        rank /= getLevel();
        this.setRank(rank);
        return rank;
    }

    public String getEzLocation() {
        return ezLocation;
    }

    public void setEzLocation(String ezLocation) {
        this.ezLocation = ezLocation;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getProject() {
        return project;
    }

    public void setProject(int project) {
        this.project = project;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public RealmList<Tag> getTags() {
        return tags;
    }

    public void setTags(RealmList<Tag> tags) {
        this.tags = tags;
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

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
