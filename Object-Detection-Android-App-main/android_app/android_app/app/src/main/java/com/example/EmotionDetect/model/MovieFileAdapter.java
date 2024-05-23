package com.example.EmotionDetect.model;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.EmotionDetect.R;

import java.util.ArrayList;
import java.util.List;

public class MovieFileAdapter extends RecyclerView.Adapter<MovieFileAdapter.ViewHolder> implements Filterable {
    private List<MovieFile> movieFileList;
    private List<MovieFile> movieFileListFull;
    private onFileCardClickListener listener;


    public interface onFileCardClickListener {
        /**
         * Function that will execute an onfilecard click callback where filename string has been passed.
         */
        void onFileCardClick(String fileName);
    }


    // Definition of ViewHolder for adapter to hold the views.
    // interface to handle the on file click listener.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView file;
        public TextView fileName;
        public TextView fileDateCreated;
        public TextView txtHappiness;
        public TextView txtSadness;
        public TextView txtFear;
        public TextView txtAnger;
        public TextView txtDisgust;
        public TextView txtSurprise;


        public ViewHolder(View fileView) {
            super(fileView);
            file = (CardView) fileView;
            fileName = file.findViewById(R.id.fileName);
            fileDateCreated = file.findViewById(R.id.fileDateCreated);
            txtHappiness = file.findViewById(R.id.txtHappiness);
            txtSadness = file.findViewById(R.id.txtSadness);
            txtFear = file.findViewById(R.id.txtFear);
            txtAnger = file.findViewById(R.id.txtAnger);
            txtDisgust = file.findViewById(R.id.txtDisgust);
            txtSurprise = file.findViewById(R.id.txtSurprise);
        }

        public void bindData(MovieFile movieFile, onFileCardClickListener listener) {
            fileName.setText(movieFile.getFileName());
            fileDateCreated.setText(movieFile.getDateCreated());
            txtHappiness.setText(String.valueOf(movieFile.getHappinessPercentage()));
            txtSadness.setText(String.valueOf(movieFile.getSadnessPercentage()));
            txtFear.setText(String.valueOf(movieFile.getFearPercentage()));
            txtAnger.setText(String.valueOf(movieFile.getAngerPercentage()));
            txtDisgust.setText(String.valueOf(movieFile.getDisgustPercentage()));
            txtSurprise.setText(String.valueOf(movieFile.getSurprisePercentage()));

            file.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create and show modal dialog with file information and progress bars
                    Context context = file.getContext();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_file_info, null);
                    builder.setView(dialogView);

                    // Set background color of the dialog view
                    dialogView.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey));

                    TextView dialogFileName = dialogView.findViewById(R.id.dialogFileName);
                    TextView dialogFileDateCreated = dialogView.findViewById(R.id.dialogFileDateCreated);
                    TextView dialogTxtHappiness = dialogView.findViewById(R.id.txtProgressHappinessPercentage);
                    TextView dialogTxtSadness = dialogView.findViewById(R.id.txtProgressSadnessPercentage);
                    TextView dialogTxtFear = dialogView.findViewById(R.id.txtProgressFearPercentage);
                    TextView dialogTxtAnger = dialogView.findViewById(R.id.txtProgressAngerPercentage);
                    TextView dialogTxtDisgust = dialogView.findViewById(R.id.txtProgressDisgustPercentage);
                    TextView dialogTxtSurprise = dialogView.findViewById(R.id.txtProgressSurprisePercentage);
                    ProgressBar progressBarHappiness = dialogView.findViewById(R.id.circularProgressHappiness);
                    ProgressBar progressBarSadness = dialogView.findViewById(R.id.circularProgressSadness);
                    ProgressBar progressBarFear = dialogView.findViewById(R.id.circularProgressFear);
                    ProgressBar progressBarAnger = dialogView.findViewById(R.id.circularProgressAnger);
                    ProgressBar progressBarDisgust = dialogView.findViewById(R.id.circularProgressDisgust);
                    ProgressBar progressBarSurprise = dialogView.findViewById(R.id.circularProgressSurprise);

                    // Setting text views in dialog
                    dialogFileName.setText(fileName.getText());
                    dialogFileDateCreated.setText(fileDateCreated.getText());
                    dialogTxtHappiness.setText(String.valueOf(Math.round(movieFile.getHappinessPercentage())) + "%");
                    dialogTxtSadness.setText(String.valueOf(Math.round(movieFile.getSadnessPercentage())) + "%");
                    dialogTxtFear.setText(String.valueOf(Math.round(movieFile.getFearPercentage())) + "%");
                    dialogTxtAnger.setText(String.valueOf(Math.round(movieFile.getAngerPercentage())) + "%");
                    dialogTxtDisgust.setText(String.valueOf(Math.round(movieFile.getDisgustPercentage())) + "%");
                    dialogTxtSurprise.setText(String.valueOf(Math.round(movieFile.getSurprisePercentage())) + "%");

                    // Set progress for each emotion
                    progressBarHappiness.setProgress((int) movieFile.getHappinessPercentage());
                    progressBarSadness.setProgress((int) movieFile.getSadnessPercentage());
                    progressBarFear.setProgress((int) movieFile.getFearPercentage());
                    progressBarAnger.setProgress((int) movieFile.getAngerPercentage());
                    progressBarDisgust.setProgress((int) movieFile.getDisgustPercentage());
                    progressBarSurprise.setProgress((int) movieFile.getSurprisePercentage());

                    builder.create().show();
                }
            });

            file.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Context context = file.getContext();
                    Toast.makeText(context, "Opening file page", Toast.LENGTH_LONG).show();
                    listener.onFileCardClick(fileName.getText().toString());
                    return true; // Return true to consume the long click event
                }
            });
        }
    }

    public MovieFileAdapter(List<MovieFile> movieFileList, onFileCardClickListener listener) {
        this.movieFileList = movieFileList;
        this.movieFileListFull = new ArrayList<>(movieFileList);
        // listener defining the onfilecard click callback method
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return movieFileList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creating a layout inflater (makes it possible to create a view from a layout XML file)
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Make the filecard view to be put in recyclerview
        View fileView = layoutInflater.inflate(R.layout.file_view, parent, false);
        // Create viewholder for filecard view (which acts as a wrapper containing behavior of each list element, i.e. of each fileview)
        return new ViewHolder(fileView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Binding data to view.
        // Get data element
        MovieFile movieFile = movieFileList.get(position);
        // Set views in view of element in list. (from single view xml file)
        holder.bindData(movieFile, listener);
    }

    public void setFilteredList(List<MovieFile> list) {
        this.movieFileList = list;
        notifyDataSetChanged();
    }

    // filter created for being able to filter and search the data.
    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<MovieFile> movieFileListFiltered = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                movieFileListFiltered.addAll(movieFileListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (MovieFile file : movieFileListFull) {
                    if (file.getFileName().toLowerCase().contains(filterPattern))
                        movieFileListFiltered.add(file);
                }
            }

            FilterResults results = new FilterResults();
            results.values = movieFileListFiltered;
            results.count = movieFileListFiltered.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            movieFileList.clear();
            movieFileList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };

    public void setMovieFileList(List<MovieFile> movieFileList) {
        this.movieFileList = movieFileList;
    }

    public void setMovieFileListFull(List<MovieFile> movieFileListFull) {
        this.movieFileListFull = movieFileListFull;
    }
}
