package com.example.EmotionDetect;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class FileActivity extends AppCompatActivity {


    private TextView responseTextView;
    private RequestQueue requestQueue;
    private static final String URL = "https://studev.groept.be/api/a23PT314/getfiledetails";
    String ConfArrayString;
    String LabelArrayString;

    int[] LabelArray;
    float[] ConfArray;

    private LineChart lineChart;

    private List<String> xValues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_linegraph);


        Intent intent = getIntent();
        //Toast.makeText(this, "filename: " + String.valueOf(intent.getStringExtra("fileName")), Toast.LENGTH_SHORT).show();


        //responseTextView = findViewById(R.id.tempText);
        requestQueue = Volley.newRequestQueue(this);

        // Replace "your_condition_string" with your actual condition string
        String condition = intent.getStringExtra("fileName");

        // Make a GET request with Volley
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL + "/" + condition,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Handle response
                        String test = response.toString();
                        test = test.substring(2, test.length() - 2);
                        String parts[] = test.split(":");
                        String filename = parts[1].split(",")[0];
                        filename = filename.substring(1, filename.length() - 1);
                        String ConfArrayString = parts[2];
                        ConfArrayString = ConfArrayString.substring(2, ConfArrayString.length() - 16);
                        LabelArrayString = parts[3];
                        LabelArrayString = LabelArrayString.substring(2, LabelArrayString.length() - 2);


                        String[] LabelArrayStringArray = LabelArrayString.split(", ");
                        LabelArray = new int[LabelArrayStringArray.length];

                        for (int i = 0; i < LabelArrayStringArray.length; i++) {
                            LabelArray[i] = Integer.parseInt(LabelArrayStringArray[i]);
                        }


                        String[] ConfArrayStringArray = ConfArrayString.split(", ");
                        ConfArray = new float[ConfArrayStringArray.length];

                        for (int i = 0; i < ConfArrayStringArray.length; i++) {
                            ConfArray[i] = Float.parseFloat(ConfArrayStringArray[i]);
                        }

                        //responseTextView.setText(LabelArrayString);

                        lineChart = findViewById(R.id.chart);

                        Description description = new Description();
                        description.setText("Emotions");
                        description.setTextColor(Color.WHITE);
                        description.setPosition(150f, 15f);
                        lineChart.setDescription(description);

                        lineChart.getAxisRight().setDrawLabels(false);


                        List<String> xValues = new ArrayList<>();
                        for (int i = 0; i <= LabelArray.length; i++) {
                            xValues.add(String.valueOf(i));
                        }


                        XAxis xAxis = lineChart.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
                        xAxis.setLabelCount(LabelArray.length);
                        xAxis.setGranularity(1f);
                        xAxis.setTextColor(Color.WHITE);

                        YAxis yAxis = lineChart.getAxisLeft();
                        yAxis.setAxisMinimum(0.001f);
                        yAxis.setAxisMaximum(1f);
                        yAxis.setAxisLineWidth(2f);
                        yAxis.setTextColor(Color.WHITE);
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



                        for(int i = 0; i < LabelArray.length; i++){
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

                            if(LabelArray[i] == 0){
                                Anger.remove(Anger.size() - 1);
                                AngerAvg[reload] = ConfArray[i];
                                AngerSum = sumFloats(AngerAvg);
                                Anger.add(new Entry(i, AngerSum / 10));
                            }

                            if(LabelArray[i] == 1){
                                Disgust.remove(Disgust.size() - 1);
                                DisgustAvg[reload] = ConfArray[i];
                                DisgustSum = sumFloats(DisgustAvg);
                                Disgust.add(new Entry(i, DisgustSum / 10));
                            }

                            if(LabelArray[i] == 2){
                                Fear.remove(Fear.size() - 1);
                                FearAvg[reload] = ConfArray[i];
                                FearSum = sumFloats(FearAvg);
                                Fear.add(new Entry(i, FearSum / 10));
                            }

                            if(LabelArray[i] == 3){
                                Happiness.remove(Happiness.size() - 1);
                                HappyAvg[reload] = ConfArray[i];
                                HappySum = sumFloats(HappyAvg);
                                Happiness.add(new Entry(i, HappySum / 10));
                            }

                            if(LabelArray[i] == 4){
                                Sadness.remove(Sadness.size() - 1);
                                SadAvg[reload] = ConfArray[i];
                                SadSum = sumFloats(SadAvg);
                                Sadness.add(new Entry(i, SadSum / 10));
                            }

                            if(LabelArray[i] == 5){
                                Surprise.remove(Surprise.size() - 1);
                                SurpriseAvg[reload] = ConfArray[i];
                                SurpriseSum = sumFloats(SurpriseAvg);
                                Surprise.add(new Entry(i, SurpriseSum / 10));
                            }

                            reload++;

                        }


                        LineDataSet dataSet0 = new LineDataSet(Anger, "Anger");
                        dataSet0.setColor(Color.RED);
                        dataSet0.setDrawCircles(false);

                        LineDataSet dataSet1 = new LineDataSet(Disgust, "Disgust");
                        dataSet1.setColor(Color.GREEN);
                        dataSet1.setDrawCircles(false);

                        LineDataSet dataSet2 = new LineDataSet(Fear, "Fear");
                        dataSet2.setColor(Color.MAGENTA);
                        dataSet2.setDrawCircles(false);

                        LineDataSet dataSet3 = new LineDataSet(Happiness, "Happiness");
                        dataSet3.setColor(Color.YELLOW);
                        dataSet3.setDrawCircles(false);

                        LineDataSet dataSet4 = new LineDataSet(Sadness, "Sadness");
                        dataSet4.setColor(Color.BLUE);
                        dataSet4.setDrawCircles(false);

                        LineDataSet dataSet5 = new LineDataSet(Surprise, "Surprise");
                        dataSet5.setColor(Color.CYAN);
                        dataSet5.setDrawCircles(false);



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

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        responseTextView.setText("Error occurred: " + error.getMessage());
                    }
                }
        );

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);


    }

    }






