package com.ricardorainha.mustache.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BarbershopsResult implements Parcelable
{

    @SerializedName("results")
    @Expose
    private List<Barbershop> barbershops = new ArrayList<Barbershop>();

    public final static Parcelable.Creator<BarbershopsResult> CREATOR = new Creator<BarbershopsResult>() {


        @SuppressWarnings({
                "unchecked"
        })
        public BarbershopsResult createFromParcel(Parcel in) {
            return new BarbershopsResult(in);
        }

        public BarbershopsResult[] newArray(int size) {
            return (new BarbershopsResult[size]);
        }

    }
            ;

    protected BarbershopsResult(Parcel in) {
        in.readList(this.barbershops, (Barbershop.class.getClassLoader()));
    }

    public BarbershopsResult() {
    }

    public BarbershopsResult(List<Barbershop> barbershops) {
        super();
        this.barbershops = barbershops;
    }

    public List<Barbershop> getBarbershops() {
        return barbershops;
    }

    public void setBarbershops(List<Barbershop> barbershops) {
        this.barbershops = barbershops;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(barbershops);
    }

    public int describeContents() {
        return 0;
    }

}