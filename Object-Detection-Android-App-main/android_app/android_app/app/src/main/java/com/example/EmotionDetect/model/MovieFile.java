package com.example.EmotionDetect.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

public class MovieFile {
    private int userId; // will be used to fetch the files only for a specific user when login system is implemented.
    private String fileName;
    private float happinessPercentage;
    private float sadnessPercentage;
    private float fearPercentage;
    private float angerPercentage;
    private float disgustPercentage;
    private float surprisePercentage;
    private String dateCreated; //TODO: change to date format later.

    public MovieFile(String fileName, float happinessPercentage, float sadnessPercentage, float fearPercentage, float angerPercentage, float disgustPercentage, float surprisePercentage, Timestamp dateCreated) {
        this.userId = 1; // TODO: change later!
        this.fileName = fileName;
        this.dateCreated = String.valueOf(dateCreated);
        this.happinessPercentage = happinessPercentage;
        this.sadnessPercentage = sadnessPercentage;
        this.fearPercentage = fearPercentage;
        this.angerPercentage = angerPercentage;
        this.disgustPercentage = disgustPercentage;
        this.surprisePercentage = surprisePercentage;
        // note: id is automatically set in DB and userId is fetched from DB.
    }

    public String getFileName() {
        return fileName;
    }

    public float getHappinessPercentage() {
        return happinessPercentage;
    }

    public float getSadnessPercentage() {
        return sadnessPercentage;
    }

    public float getFearPercentage() {
        return fearPercentage;
    }

    public float getAngerPercentage() {
        return angerPercentage;
    }

    public float getDisgustPercentage() {
        return disgustPercentage;
    }

    public float getSurprisePercentage() {
        return surprisePercentage;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public MovieFile(JSONObject o) {
        try {
            // exception block.
            fileName = o.getString("file_name");
            dateCreated = o.getString("date");
        } catch (JSONException e) {
            Log.e("database", e.getLocalizedMessage(), e);
        }
    }

    /**
     * function to convert obj attributes to map to send with network request.
     * @return map with key (= attribute) and value (=value of attribute)
     */
    public void getPostParameters() {
        // TODO: DURING REQUESTS.
        return;
    }
}
