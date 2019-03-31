package com.ricardorainha.mustache.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpeningHours implements Parcelable
{

    @SerializedName("open_now")
    @Expose
    private boolean openNow;
    public final static Parcelable.Creator<OpeningHours> CREATOR = new Creator<OpeningHours>() {


        @SuppressWarnings({
                "unchecked"
        })
        public OpeningHours createFromParcel(Parcel in) {
            return new OpeningHours(in);
        }

        public OpeningHours[] newArray(int size) {
            return (new OpeningHours[size]);
        }

    }
            ;

    protected OpeningHours(Parcel in) {
        this.openNow = ((boolean) in.readValue((boolean.class.getClassLoader())));
    }

    public OpeningHours() {
    }

    public OpeningHours(boolean openNow) {
        super();
        this.openNow = openNow;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(openNow);
    }

    public int describeContents() {
        return 0;
    }

}