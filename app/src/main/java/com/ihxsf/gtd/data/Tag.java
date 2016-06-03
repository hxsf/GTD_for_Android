package com.ihxsf.gtd.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by hxsf on 16－06－01.
 */
public class Tag extends RealmObject {
    @PrimaryKey
    private int id;
    @Required
    private String name;
    @Required
    private Long color;

    public Tag() {
    }
    public Tag (Tag tag) {
        this.name = tag.getName();
        this.color = tag.getColor();
    }
    public Tag(String name, Long color) {
        this.name = name;
        this.color = color;
    }

    public long getColor() {
        return color;
    }

    public void setColor(long color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
