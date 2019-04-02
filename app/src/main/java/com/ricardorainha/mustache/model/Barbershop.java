package com.ricardorainha.mustache.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ricardorainha.mustache.network.BarbershopsDB;

import java.util.ArrayList;
import java.util.List;

public class Barbershop implements Parcelable
{

    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHours openingHours;
    @SerializedName("photos")
    @Expose
    private List<Photo> photos = new ArrayList<Photo>();
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("rating")
    @Expose
    private double rating;
    @SerializedName("user_ratings_total")
    @Expose
    private int userRatingsTotal;
    public final static Parcelable.Creator<Barbershop> CREATOR = new Creator<Barbershop>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Barbershop createFromParcel(Parcel in) {
            return new Barbershop(in);
        }

        public Barbershop[] newArray(int size) {
            return (new Barbershop[size]);
        }

    }
            ;

    protected Barbershop(Parcel in) {
        this.formattedAddress = ((String) in.readValue((String.class.getClassLoader())));
        this.geometry = ((Geometry) in.readValue((Geometry.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.openingHours = ((OpeningHours) in.readValue((OpeningHours.class.getClassLoader())));
        in.readList(this.photos, (com.ricardorainha.mustache.model.Photo.class.getClassLoader()));
        this.placeId = ((String) in.readValue((String.class.getClassLoader())));
        this.rating = ((double) in.readValue((double.class.getClassLoader())));
        this.userRatingsTotal = ((int) in.readValue((int.class.getClassLoader())));
    }

    public Barbershop() {
    }

    public Barbershop(String formattedAddress, Geometry geometry, String name, OpeningHours openingHours, List<Photo> photos, String placeId, double rating, int userRatingsTotal) {
        super();
        this.formattedAddress = formattedAddress;
        this.geometry = geometry;
        this.name = name;
        this.openingHours = openingHours;
        this.photos = photos;
        this.placeId = placeId;
        this.rating = rating;
        this.userRatingsTotal = userRatingsTotal;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getPhotoUrl() {
        if (getPhotos().size() > 0) {
            StringBuilder photoUrl = new StringBuilder()
                    .append(BarbershopsDB.API_BASE_URL).append("place/photo?")
                    .append("key=").append(BarbershopsDB.MAPS_API_KEY)
                    .append("&photoreference=").append(getPhotos().get(0).getPhotoReference())
                    .append("&maxwidth=1080");

            return photoUrl.toString();
        }

        return null;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getUserRatingsTotal() {
        return userRatingsTotal;
    }

    public void setUserRatingsTotal(int userRatingsTotal) {
        this.userRatingsTotal = userRatingsTotal;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(formattedAddress);
        dest.writeValue(geometry);
        dest.writeValue(name);
        dest.writeValue(openingHours);
        dest.writeList(photos);
        dest.writeValue(placeId);
        dest.writeValue(rating);
        dest.writeValue(userRatingsTotal);
    }

    public int describeContents() {
        return 0;
    }

}