package com.example.EmotionDetect;

public class MainJava {

    private static int emotion;
    private static float confidenceValue;

    private static float[] confArray;

    private static int[] labelArray;

    private MainJava(){
    }


    public static void setData(int newValue, float testValue) {
        emotion = newValue;
        confidenceValue = testValue;
    }

    public static void setConfArray(float[] values){

        confArray = values;

    }

    public static void setLabelArray(int[] values){

        labelArray = values;

    }


    public static int getEmotion(){

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
        emotion = -1;
        confidenceValue = -1;
        confArray = new float[0];
        labelArray = new int[0];
    }

}


