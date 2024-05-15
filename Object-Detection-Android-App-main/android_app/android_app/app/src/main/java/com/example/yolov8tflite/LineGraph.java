package com.example.yolov8tflite;

import androidx.appcompat.app.AppCompatActivity;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import android.graphics.Color;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LineGraph extends AppCompatActivity {

    private LineChart lineChart;

    private List<String> xValues;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linegraph);

        lineChart = findViewById(R.id.chart);

        Description description = new Description();
        description.setText("Emotions");
        description.setTextColor(Color.WHITE);
        description.setPosition(150f, 15f);
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);

        int[] labelArr = MainJava.getLabelArray();
        float[] confArr = MainJava.getConfArray();

        List<String> xValues = new ArrayList<>();
        for (int i = 0; i <= labelArr.length; i++) {
            xValues.add(String.valueOf(i));
        }


        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setLabelCount(labelArr.length);
        xAxis.setGranularity(1f);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0.001f);
        yAxis.setAxisMaximum(1f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);

        Legend legend = lineChart.getLegend();
        legend.setTextColor(Color.WHITE);



        List<Entry> Anger = new ArrayList<>();
        List<Entry> Disgust = new ArrayList<>();
        List<Entry> Fear = new ArrayList<>();
        List<Entry> Happiness = new ArrayList<>();
        List<Entry> Sadness = new ArrayList<>();
        List<Entry> Surprise = new ArrayList<>();
        int reload = 0;
        float[] AngerAvg = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        float[] DisgustAvg = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        float[] FearAvg = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        float[] HappyAvg = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        float[] SadAvg = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        float[] SurpriseAvg = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};



        for(int i = 0; i < labelArr.length; i++){
            if(reload > 9){
                reload = 0;
            }

            AngerAvg[reload] = 0;
            DisgustAvg[reload] = 0;
            FearAvg[reload] = 0;
            HappyAvg[reload] = 0;
            SadAvg[reload] = 0;
            SurpriseAvg[reload] = 0;

            float AngerSum = sumFloats(AngerAvg);
            float DisgustSum = sumFloats(DisgustAvg);
            float FearSum = sumFloats(FearAvg);
            float HappySum = sumFloats(HappyAvg);
            float SadSum = sumFloats(SadAvg);
            float SurpriseSum = sumFloats(SurpriseAvg);


            Anger.add(new Entry(i, AngerSum / 10));
            Disgust.add(new Entry(i, DisgustSum / 10));
            Fear.add(new Entry(i, FearSum / 10));
            Happiness.add(new Entry(i, HappySum / 10));
            Sadness.add(new Entry(i, SadSum / 10));
            Surprise.add(new Entry(i, SurpriseSum / 10));

            if(labelArr[i] == 0){
                Anger.remove(Anger.size() - 1);
                AngerAvg[reload] = confArr[i];
                AngerSum = sumFloats(AngerAvg);
                Anger.add(new Entry(i, AngerSum / 10));
            }

            if(labelArr[i] == 1){
                Disgust.remove(Disgust.size() - 1);
                DisgustAvg[reload] = confArr[i];
                DisgustSum = sumFloats(DisgustAvg);
                Disgust.add(new Entry(i, DisgustSum / 10));
            }

            if(labelArr[i] == 2){
                Fear.remove(Fear.size() - 1);
                FearAvg[reload] = confArr[i];
                FearSum = sumFloats(FearAvg);
                Fear.add(new Entry(i, FearSum / 10));
            }

            if(labelArr[i] == 3){
                Happiness.remove(Happiness.size() - 1);
                HappyAvg[reload] = confArr[i];
                HappySum = sumFloats(HappyAvg);
                Happiness.add(new Entry(i, HappySum / 10));
            }

            if(labelArr[i] == 4){
                Sadness.remove(Sadness.size() - 1);
                SadAvg[reload] = confArr[i];
                SadSum = sumFloats(SadAvg);
                Sadness.add(new Entry(i, SadSum / 10));
            }

            if(labelArr[i] == 5){
                Surprise.remove(Surprise.size() - 1);
                SurpriseAvg[reload] = confArr[i];
                SurpriseSum = sumFloats(SurpriseAvg);
                Surprise.add(new Entry(i, SurpriseSum / 10));
            }

            reload++;

        }


        LineDataSet dataSet0 = new LineDataSet(Anger, "Anger");
        dataSet0.setColor(Color.RED);

        LineDataSet dataSet1 = new LineDataSet(Disgust, "Disgust");
        dataSet1.setColor(Color.GREEN);

        LineDataSet dataSet2 = new LineDataSet(Fear, "Fear");
        dataSet2.setColor(Color.MAGENTA);

        LineDataSet dataSet3 = new LineDataSet(Happiness, "Happiness");
        dataSet3.setColor(Color.YELLOW);

        LineDataSet dataSet4 = new LineDataSet(Sadness, "Sadness");
        dataSet4.setColor(Color.BLUE);

        LineDataSet dataSet5 = new LineDataSet(Surprise, "Surprise");
        dataSet5.setColor(Color.CYAN);



        LineData lineData = new LineData(dataSet0, dataSet1, dataSet2, dataSet3, dataSet4, dataSet5);

        lineChart.setData(lineData);

        lineChart.invalidate();

    }

    private float sumFloats(float[] array){

        float sum = 0;
        for (float num : array) {
            sum += num;}

        return sum;
    }
}
