package com.example.EmotionDetect.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DecimalFormat;

public class MovieFile {
    private int fileId;
    private int userId; // will be used to fetch the files only for a specific user when login system is implemented.
    private String fileName;
    private double happinessPercentage;
    private double sadnessPercentage;
    private double fearPercentage;
    private double angerPercentage;
    private double disgustPercentage;
    private double surprisePercentage;
    private String dateCreated; //TODO: change to date format later.

    public MovieFile(int fileId, int userId, String fileName, float happinessPercentage, float sadnessPercentage, float fearPercentage, float angerPercentage, float disgustPercentage, float surprisePercentage, Timestamp dateCreated) {
        this.fileId = fileId;
        this.userId = userId;
        this.fileName = fileName;
        this.dateCreated = String.valueOf(dateCreated);
        this.happinessPercentage = happinessPercentage;
        this.sadnessPercentage = sadnessPercentage;
        this.fearPercentage = fearPercentage;
        this.angerPercentage = angerPercentage;
        this.disgustPercentage = disgustPercentage;
        this.surprisePercentage = surprisePercentage;
    }

    public MovieFile(JSONObject o, int userId) {
        this.userId = userId;
        DecimalFormat df = new DecimalFormat("#.#");
        try {
            // exception block.
            this.fileId = o.getInt("id");
            this.fileName = o.getString("filename");
            this.dateCreated = o.getString("date");
            // percentage = * 100 and then other 10 is used to round of to 1 decimal places.
            this.happinessPercentage = Math.round(o.getDouble("happiness_percentage") * 1000) / 10.0;
            this.sadnessPercentage = Math.round(o.getDouble("sadness_percentage") * 1000) / 10.0;
            this.fearPercentage = Math.round(o.getDouble("fear_percentage") * 1000) / 10.0;
            this.angerPercentage = Math.round(o.getDouble("anger_percentage") * 1000) / 10.0;
            this.disgustPercentage = Math.round(o.getDouble("disgust_percentage") * 1000) / 10.0;
            this.surprisePercentage = Math.round(o.getDouble("surprise_percentage") * 1000) / 10.0;

        } catch (JSONException e) {
            Log.e("database", e.getLocalizedMessage(), e);
        }
    }

    public int getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public double getHappinessPercentage() {
        return happinessPercentage;
    }

    public double getSadnessPercentage() {
        return sadnessPercentage;
    }

    public double getFearPercentage() {
        return fearPercentage;
    }

    public double getAngerPercentage() {
        return angerPercentage;
    }

    public double getDisgustPercentage() {
        return disgustPercentage;
    }

    public double getSurprisePercentage() {
        return surprisePercentage;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public int getUserId() {
        return userId;
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
