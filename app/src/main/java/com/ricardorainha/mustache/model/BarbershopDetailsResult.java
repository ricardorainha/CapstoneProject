package com.ricardorainha.mustache.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BarbershopDetailsResult implements Parcelable
{

    @SerializedName("result")
    @Expose
    private Barbershop result;
    public final static Parcelable.Creator<BarbershopDetailsResult> CREATOR = new Creator<BarbershopDetailsResult>() {


        @SuppressWarnings({
                "unchecked"
        })
        public BarbershopDetailsResult createFromParcel(Parcel in) {
            return new BarbershopDetailsResult(in);
        }

        public BarbershopDetailsResult[] newArray(int size) {
            return (new BarbershopDetailsResult[size]);
        }

    };

    protected BarbershopDetailsResult(Parcel in) {
        this.result = ((Barbershop) in.readValue((Barbershop.class.getClassLoader())));
    }

    public BarbershopDetailsResult() {
    }

    public BarbershopDetailsResult(Barbershop result) {
        super();
        this.result = result;
    }

    public Barbershop getResult() {
        return result;
    }

    public void setResult(Barbershop result) {
        this.result = result;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(result);
    }

    public int describeContents() {
        return 0;
    }

}