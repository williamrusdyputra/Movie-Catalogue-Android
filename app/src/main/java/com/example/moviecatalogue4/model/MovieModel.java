package com.example.moviecatalogue4.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.moviecatalogue4.database.DatabaseContract;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("FieldCanBeLocal")
public class MovieModel implements Parcelable {
    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("adult")
    private boolean adult;

    @SerializedName("overview")
    private String overview;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("id")
    private Integer id;

    @SerializedName("title")
    private String title;

    @SerializedName("vote_average")
    private Double voteAverage;

    private byte[] poster;

    public byte[] getPoster() {
        return poster;
    }

    public void setPoster(byte[] poster) {
        this.poster = poster;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public MovieModel() {}

    public MovieModel(int id, String title, byte[] poster, String releaseDate, boolean adult, String overview) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.releaseDate = releaseDate;
        this.adult = adult;
        this.overview = overview;
    }

    public MovieModel(Cursor cursor) {
        this.id = DatabaseContract.getColumnInt(cursor, DatabaseContract.MovieColumns._ID);
        this.title = DatabaseContract.getColumnString(cursor, DatabaseContract.MovieColumns.TITLE);
        this.poster = DatabaseContract.getBlob(cursor, DatabaseContract.MovieColumns.IMAGE);
        this.adult = DatabaseContract.getColumnString(cursor, DatabaseContract.MovieColumns.RATE).equals("17+");
        this.releaseDate = DatabaseContract.getColumnString(cursor, DatabaseContract.MovieColumns.RELEASE_DATE);
        this.overview = DatabaseContract.getColumnString(cursor, DatabaseContract.MovieColumns.OVERVIEW);
    }

    private MovieModel(Parcel in) {
        posterPath = in.readString();
        adult = in.readByte() != 0;
        overview = in.readString();
        releaseDate = in.readString();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        title = in.readString();
        if (in.readByte() == 0) {
            voteAverage = null;
        } else {
            voteAverage = in.readDouble();
        }
    }

    public static final Creator<MovieModel> CREATOR = new Creator<MovieModel>() {
        @Override
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(posterPath);
        parcel.writeByte((byte) (adult ? 1 : 0));
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(title);
        if (voteAverage == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(voteAverage);
        }
    }
}
