package com.example.EmotionDetect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.Slider;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    private Spinner spinnerFilter;
    private Slider sliderHappiness, sliderSadness, sliderFear, sliderAnger, sliderDisgust, sliderSurprise;
    private Button btnApplyFilter;
    private Button btnResetFilter;

    private OnClickListener listener;

    FilterCriteria filterCriteria;

    public interface OnClickListener {
        void onFilterApplied(FilterCriteria filterCriteria);

        boolean isFirstTimeFiltering();

        void setFirstTimeFilteringFlag(boolean value);

//        boolean onResetBtnClick();
    }

    /**
     * function used to set up filterapplied click listener.
     * @param listener
     */
    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_bottom_sheet_layout, container, false);
        spinnerFilter = view.findViewById(R.id.spinnerFilterOrder);
        sliderHappiness = view.findViewById(R.id.sliderHappiness);
        sliderSadness = view.findViewById(R.id.sliderSadness);
        sliderFear = view.findViewById(R.id.sliderFear);
        sliderAnger = view.findViewById(R.id.sliderAnger);
        sliderDisgust = view.findViewById(R.id.sliderDisgust);
        sliderSurprise = view.findViewById(R.id.sliderSurprise);
        btnApplyFilter = view.findViewById(R.id.btnApplyFilter);
        btnResetFilter = view.findViewById(R.id.btnResetFilter);

        if (!listener.isFirstTimeFiltering())
            filterCriteria = getFilterValues();

        btnApplyFilter.setOnClickListener(v -> {
            String selectedSortCriteria = spinnerFilter.getSelectedItem().toString();
//        else {
//            filterCriteria = getFilterValues();
//        }
            filterCriteria = new FilterCriteria(
                    sliderHappiness.getValue(),
                    sliderSadness.getValue(),
                    sliderFear.getValue(),
                    sliderAnger.getValue(),
                    sliderDisgust.getValue(),
                    sliderSurprise.getValue(),
                    selectedSortCriteria
            );

            if (listener != null) {
                listener.onFilterApplied(filterCriteria);
            }

            // storing filter values in shared preferences.
                storeFilterValues();
                // setting firsttime flag to flase
            listener.setFirstTimeFilteringFlag(false);

                dismiss();
            });

     // reset filters and shared prefs when reset button is hit.
        btnResetFilter.setOnClickListener(v -> {
            resetFiltervalues();
            clearSharedPrefs();
        });
        return view;
    }

    private void resetFiltervalues() {
        sliderHappiness.setValue(0);
        sliderAnger.setValue(0);
        sliderDisgust.setValue(0);
        sliderFear.setValue(0);
        sliderSurprise.setValue(0);
        sliderSadness.setValue(0);
        spinnerFilter.setSelection(0);
    }

    private void clearSharedPrefs() {
        requireActivity().getPreferences(Context.MODE_PRIVATE).edit().clear().apply();
    }

    /**
     * get filter values from shared preference to be stored in FilterCriteria object
     * @params none
     * @return FilterCriteria object
     */
    private FilterCriteria getFilterValues() {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        float happinessValue = sharedPreferences.getFloat("happinessValue", 0);
        float sadnessValue = sharedPreferences.getFloat("sadnessValue", 0);
        float fearValue = sharedPreferences.getFloat("fearValue", 0);
        float angerValue = sharedPreferences.getFloat("angerValue", 0);
        float disgustValue = sharedPreferences.getFloat("disgustValue", 0);
        float surpriseValue = sharedPreferences.getFloat("surpriseValue", 0);
        String spinnerValue = sharedPreferences.getString("spinnerValue", "None");

        // Update UI elements with retrieved values
        sliderHappiness.setValue(happinessValue);
        sliderSadness.setValue(sadnessValue);
        sliderFear.setValue(fearValue);
        sliderAnger.setValue(angerValue);
        sliderDisgust.setValue(disgustValue);
        sliderSurprise.setValue(surpriseValue);

        // Set the selected item for the spinner based on the value
        ArrayAdapter<String> spinnerAdapter = (ArrayAdapter<String>) spinnerFilter.getAdapter();
        int spinnerPosition = spinnerAdapter.getPosition(spinnerValue);
        if (spinnerPosition != -1) {
            spinnerFilter.setSelection(spinnerPosition);
        }

        return new FilterCriteria(
                happinessValue,
                sadnessValue,
                fearValue,
                angerValue,
                disgustValue,
                surpriseValue,
                spinnerValue
        );
    }

    /**
     * Stores filter values in shared preferences.
     */
    private void storeFilterValues() {
        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("happinessValue", sliderHappiness.getValue());
        editor.putFloat("sadnessValue", sliderSadness.getValue());
        editor.putFloat("fearValue", sliderFear.getValue());
        editor.putFloat("angerValue", sliderAnger.getValue());
        editor.putFloat("disgustValue", sliderDisgust.getValue());
        editor.putFloat("surpriseValue", sliderSurprise.getValue());
        editor.putString("spinnerValue", spinnerFilter.getSelectedItem().toString());
        editor.apply();
    }
}

