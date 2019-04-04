package com.ricardorainha.mustache.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OpeningHours implements Parcelable
{

    @SerializedName("open_now")
    @Expose
    private boolean openNow;
    @SerializedName("weekday_text")
    @Expose
    private List<String> weekdayText = new ArrayList<String>();
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
        in.readList(this.weekdayText, (java.lang.String.class.getClassLoader()));
    }

    public OpeningHours() {
    }

    public OpeningHours(boolean openNow, List<String> weekdayText) {
        super();
        this.openNow = openNow;
        this.weekdayText = weekdayText;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public List<String> getWeekdayText() {
        return weekdayText;
    }

    public void setWeekdayText(List<String> weekdayText) {
        this.weekdayText = weekdayText;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(openNow);
        dest.writeList(weekdayText);
    }

    public int describeContents() {
        return 0;
    }

}