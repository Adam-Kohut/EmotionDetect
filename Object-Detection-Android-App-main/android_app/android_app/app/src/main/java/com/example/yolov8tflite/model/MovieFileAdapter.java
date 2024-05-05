package com.example.yolov8tflite.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yolov8tflite.R;

import java.util.List;

public class MovieFileAdapter extends RecyclerView.Adapter<MovieFileAdapter.ViewHolder> {
    private List<MovieFile> movieFileList;

    // definition of ViewHolder for adapter to hold the views.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View file;

        public ViewHolder(View fileView) {
            super(fileView);

            file = (View) fileView;
        }
    }

    public MovieFileAdapter(List<MovieFile> movieFileList) {
        this.movieFileList = movieFileList;
    }

    @Override
    public int getItemCount() {
        return movieFileList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // creating a layout inflater(makes it possible to create a view from a layout XML file)
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // make the filecard view to be put in recyclerview
        View fileView = layoutInflater.inflate(R.layout.file_view, parent, false);
        // create viewholder for filecard view (which acts as a wrapper containing behavior of each list element, i.e. of each fileview)
        ViewHolder myViewHolder = new ViewHolder(fileView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // binding data to view.
        // 1. get data element
        MovieFile movieFile = movieFileList.get(position);
        // set views in view of element in list. (from single view xml file)
        ((TextView) holder.file.findViewById(R.id.fileName)).setText(movieFile.getFileName());
        ((TextView) holder.file.findViewById(R.id.fileDateCreated)).setText(movieFile.getDateCreated());
    }
}
