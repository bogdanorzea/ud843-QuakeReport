package com.example.android.quakereport;

public class Earthquake {
    double mMagnitude;
    String mLocation;
    String mDate;

    public Earthquake(double mMagnitude, String mLocation, String mDate) {
        this.mMagnitude = mMagnitude;
        this.mLocation = mLocation;
        this.mDate = mDate;
    }
}
