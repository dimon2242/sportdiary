package com.app.dmitryteplyakov.sportdiary.Core.NutritionDay;

import java.util.Date;
import java.util.UUID;

/**
 * Created by dmitry21 on 12.08.17.
 */

public class NutritionDay implements Comparable<NutritionDay> {
    private UUID mId;
    private UUID mAssociatedDay;
    private Date mDate;
    private boolean mIsAssociatedWithDay;

    public NutritionDay(UUID id) {
        mId = id;
        mAssociatedDay = UUID.randomUUID();
        mDate = new Date();
    }
    public NutritionDay() {
        this(UUID.randomUUID());
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public UUID getAssociatedDay() {
        return mAssociatedDay;
    }

    public void setAssociatedDay(UUID associatedDay) {
        mAssociatedDay = associatedDay;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isAssociatedWithDay() {
        return mIsAssociatedWithDay;
    }

    public void setAssociatedWithDay(boolean associatedWithDay) {
        mIsAssociatedWithDay = associatedWithDay;
    }

    @Override
    public boolean equals(Object o) {
        if(this.getId().equals(((NutritionDay) o).getId()))
            return true;
        return false;
    }

    @Override
    public int compareTo(NutritionDay nutritionDay) {
        if (this.getDate().after(nutritionDay.getDate()))
            return -1;
        return 1;
    }
}
