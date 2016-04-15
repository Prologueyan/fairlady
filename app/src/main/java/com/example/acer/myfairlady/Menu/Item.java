package com.example.acer.myfairlady.Menu;

/**
 * Created by acer on 2016/3/10.
 */
public class Item {
    String mTitle;
    int mIconid;

    public String getmTitle() {
        return mTitle;
    }

    public int getmIconid() {
        return mIconid;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmIconid(int mIconid) {
        this.mIconid = mIconid;
    }

    public Item(String mTitle, int mIconid) {
        this.mTitle = mTitle;
        this.mIconid = mIconid;

    }
}
