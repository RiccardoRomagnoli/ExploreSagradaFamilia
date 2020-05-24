package com.example.exploresagradafamilia;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Class which represents every sightplace with its information (image, position, title, description, archived)
 */
@Entity(tableName = "sightplace")
public class Sightplace {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sightplace_id")
    private int id;
    @ColumnInfo(name = "sightplace_image")
    private String image;
    @ColumnInfo(name = "sightplace_lat")
    private Double latitude;
    @ColumnInfo(name = "sightplace_lon")
    private Double longitude;
    @ColumnInfo(name = "sightplace_description")
    private String description;
    @ColumnInfo(name = "sightplace_title")
    private String title;
    @ColumnInfo(name = "sightplace_rating")
    private Double rating;
    @ColumnInfo(name = "sightplace_location")
    private String location;
    @ColumnInfo(name = "sightplace_archived")
    private boolean archived;
    @ColumnInfo(name = "sightplace_major")
    private int major;
    @ColumnInfo(name = "sightplace_minor")
    private int minor;

    /**
     * @param image    Base64 encoded image
     * @param major    Ibeacon major ID
     * @param minor    Ibeacon minor ID
     */
    public Sightplace(String image, Double latitude, Double longitude, String description, String title, Double rating, String location, int major, int minor) {
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.title = title;
        this.rating = rating;
        this.location = location;
        this.major = major;
        this.minor = minor;
        this.archived = false;
    }

    public String getImage() {
        return image;
    }

    public boolean getArchived() {
        return archived;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public Double getRating() {
        return rating;
    }

    public String getLocation() {
        return location;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
