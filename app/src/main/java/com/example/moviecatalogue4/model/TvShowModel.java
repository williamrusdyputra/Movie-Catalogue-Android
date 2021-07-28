package com.example.moviecatalogue4.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TvShowModel implements Parcelable {
    @SerializedName("name")
    private String name;

    @SerializedName("first_air_date")
    private String firstAirDate;

    @SerializedName("id")
    private Integer id;

    @SerializedName("vote_average")
    private Double voteAverage;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    public TvShowModel() {}

    private TvShowModel(Parcel in) {
        name = in.readString();
        firstAirDate = in.readString();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            voteAverage = null;
        } else {
            voteAverage = in.readDouble();
        }
        overview = in.readString();
        posterPath = in.readString();
    }

    public static final Creator<TvShowModel> CREATOR = new Creator<TvShowModel>() {
        @Override
        public TvShowModel createFromParcel(Parcel in) {
            return new TvShowModel(in);
        }

        @Override
        public TvShowModel[] newArray(int size) {
            return new TvShowModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public Double getVoteAverage() { return voteAverage; }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(firstAirDate);
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        if (voteAverage == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(voteAverage);
        }
        parcel.writeString(overview);
        parcel.writeString(posterPath);
    }
}
