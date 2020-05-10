package com.example.exploresagradafamilia;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.util.Base64;

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
    @ColumnInfo(name = "sightplace_position")
    private Location position;
    @ColumnInfo(name = "sightplace_description")
    private String description;
    @ColumnInfo(name = "sightplace_title")
    private String title;
    @ColumnInfo(name = "sightplace_archived")
    private boolean archived;

    /**
     * @param position object containing lat and lon of the sightpalce
     * @param image    Base64 encoded image
     */
    public Sightplace(String image, Location position, String description, String title) {
        this.image = image;
        this.position = position;
        this.description = description;
        this.title = title;
        this.archived = false;
    }

    public Bitmap getImage() {
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public Location getPosition() {
        return position;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setArchived() {
        this.archived = true;
    }
}
