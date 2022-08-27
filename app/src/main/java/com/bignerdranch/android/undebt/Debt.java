package com.bignerdranch.android.undebt;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

public class Debt {

    private UUID mId;
    private String mTitle;
    private Double mAmount = 0.00;
    private Date mDate;
    private boolean mSettled;

    public Debt() {
        this(UUID.randomUUID());
    }
    public Debt(UUID id){
        mId = id;
        mDate= new Date();
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Double getAmount() {
        return mAmount;
    }

    public void setAmount(Double amount) {
        mAmount = amount;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSettled() {
        return mSettled;
    }

    public void setSettled(boolean settled) {
        mSettled = settled;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }


}
