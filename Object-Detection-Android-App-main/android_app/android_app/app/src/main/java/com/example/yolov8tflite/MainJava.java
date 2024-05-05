package com.example.yolov8tflite;

import java.util.ArrayList;
import java.util.List;

public class MainJava {

    private static String emotion;
    private static float confidenceValue;

    private static float[] confArray;

    private static int[] labelArray;

    private MainJava(){
    }


    public static void setData(String newValue, float testValue) {
        emotion = newValue;
        confidenceValue = testValue;
    }

    public static void setConfArray(float[] values){

        confArray = values;

    }

    public static void setLabelArray(int[] values){

        labelArray = values;

    }


    public static String getEmotion(){

        return emotion;
    }


    public static float getConf(){

        return confidenceValue;
    }


    public static float[] getConfArray(){

        return confArray;
    }


    public static int[] getLabelArray(){

        return labelArray;
    }






    public static void reset() {
        // Reset internal state here
        emotion = "";
        confidenceValue = -1;
        confArray = new float[0];
    }

}


