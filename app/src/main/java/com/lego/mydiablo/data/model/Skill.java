package com.lego.mydiablo.data.model;

import io.realm.RealmObject;

/**
 * @author Lego on 17.05.2016.
 */
public class Skill extends RealmObject {

    private Rune mRune;
    private String mSlug;
    private String mTitle;
    private String mDescription;
    private String mSimpleDescription;
    private String mImageUrl;

    public Skill(){
        //do nothing
    }

    public Rune getRune() {
        return mRune;
    }

    public void setRune(Rune rune) {
        mRune = rune;
    }

    public String getSlug() {
        return mSlug;
    }

    public void setSlug(String slug) {
        mSlug = slug;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getSimpleDescription() {
        return mSimpleDescription;
    }

    public void setSimpleDescription(String simpleDescription) {
        mSimpleDescription = simpleDescription;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}
