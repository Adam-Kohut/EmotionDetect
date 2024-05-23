package com.example.EmotionDetect.model;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.EmotionDetect.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieFileAdapter extends RecyclerView.Adapter<MovieFileAdapter.ViewHolder> implements Filterable{
    private List<MovieFile> movieFileList;
    private List<MovieFile> movieFileListFull;
    private OnFileCardClickListener listener;
    private OnFileEditAndRemoveListener editAndRemoveListener;

    public interface OnFileCardClickListener {
        /**
         * Function that will execute an onfilecard click callback where filename string has been passed.
         */
        void onFileCardClick(String fileName);
    }

    public interface OnFileEditAndRemoveListener {
        void onFileEdit(int listPosition, String newFilename);
        void onFileRemove(int listPosition);
    }


    // Definition of ViewHolder for adapter to hold the views.
    // interface to handle the on file click listener.
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private static final String EDIT_FILENAME_URL = "https://studev.groept.be/api/a23pt314/edit_filename";
        private static final String REMOVE_FILE_URL = "https://studev.groept.be/api/a23pt314/remove_file";
        public CardView file;
        public TextView fileName;
        public TextView fileDateCreated;
        public ImageButton btnTimeGraph;
        private int listPosition;
        MovieFileAdapter adapter; // required in order to update the file lists.
        private MovieFile movieFile; // file object used to extract data of specific fileItem for share intent.

        public ViewHolder(View fileView, MovieFileAdapter adapter) {
            super(fileView);
            file = (CardView) fileView;
            fileName = file.findViewById(R.id.fileName);
            fileDateCreated = file.findViewById(R.id.fileDateCreated);
            btnTimeGraph = file.findViewById(R.id.btnTimeGraph);
            this.adapter = adapter;
        }

        // note: one way to connected a listener is by implementing the interface, and defining the method.
            // then, you set the seton... with argument 'this'.
        // note: second way: just use a lambda expression to create this listener! (or anonymous class.
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            // Inflate the context menu
            MenuInflater inflater = new MenuInflater(v.getContext());
            inflater.inflate(R.menu.context_menu_file_view, menu);

            MenuItem itemEdit = menu.findItem(R.id.itemEdit);
            MenuItem itemDelete = menu.findItem(R.id.itemDelete);
            MenuItem itemShare = menu.findItem(R.id.itemShare);

            // setting icons (note: at this point not working for some reason. programmatically as well as via xml file!
            itemEdit.setIcon(R.drawable.ic_edit);
            itemDelete.setIcon(R.drawable.ic_delete);
            itemShare.setIcon(R.drawable.ic_share);

            // defining logic for the different options
            itemEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(@NonNull MenuItem item) {
                    editFileName();
                    return true;
                }
            });

            itemDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(@NonNull MenuItem item) {
                    deleteFile();
                    return true;
                }
            });

            itemShare.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(@NonNull MenuItem item) {
                    shareFileInfo();
                    return true;
                }
            });
        }

        private void deleteFile() {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(file.getContext(), R.style.ThemeOverlay_App_MaterialAlertDialog);
            builder.setTitle("Are you sure you want to delete this file?");
            builder.setMessage("This will delete the file permanently. You cannot undo this action.");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 1. remove file from DB.
                    removeFileDB();
                    // 2. update lists in adapter
                    adapter.updateArrayListsOnDelete(fileName.getText().toString(), listPosition);
                    // 3. dismiss the dialog.
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            // TODO: ADD RED BUTTON AND REMOVE TEXT BELOW
//            androidx.appcompat.app.AlertDialog dialog = builder.create();
//            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//                @Override
//                public void onShow(DialogInterface dialog) {
//                    Button deleteButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
//                    if (deleteButton != null) {
//                        deleteButton.setTextColor(Color.RED);
//                    }
//                }
//            });

            // Show the dialog
//            dialog.show();
        }

        private void removeFileDB() {
            RequestQueue queue = Volley.newRequestQueue(file.getContext());
            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    REMOVE_FILE_URL,
                    response -> {
                        Toast.makeText(file.getContext(), "File has been removed.", Toast.LENGTH_SHORT).show();
                        Log.d("Database", "file name has been successively changed in DB");
                    }, error -> {
                Toast.makeText(
                        file.getContext(),
                        "Unable to communicate with the server",
                        Toast.LENGTH_LONG).show();
                Log.d("Database", "Unable to communicate with server");
            }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    SharedPreferences sharedPrefs = file.getContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
                    params.put("userid", sharedPrefs.getString("userId", "-1"));
                    params.put("filename", fileName.getText().toString());
                    return params;
                }
            };

            queue.add(request);
        }

        private void shareFileInfo() {
            // Create an Intent with ACTION_SEND action
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            // Set the MIME type to "text/plain"
            sendIntent.setType("text/plain");
            // Put the text data as an extra in the Intent
            sendIntent.putExtra(Intent.EXTRA_TEXT, movieFile.toString());
            // Start the activity chooser for sharing the text
            String title = "Share Emotion Analysis for " + movieFile.getFileName();
            sendIntent.putExtra(Intent.EXTRA_TITLE, title);

            Intent shareIntent = Intent.createChooser(sendIntent,null );
            file.getContext().startActivity(shareIntent);
        }

        private void editFileName() {
            // make the edit file name possible. also check whether the filename has actually been edited.
                Context context = file.getContext();
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_filename, null);
                builder.setView(dialogView);

                EditText editTextNewFilename = dialogView.findViewById(R.id.editTextNewFilename);

                builder.setTitle("Edit Filename");
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 1. editing filename and sending new filename to update in mysql
                        String newFilename = editTextNewFilename.getText().toString();
                        ChangeFileNameDB(newFilename);
                        // 2. making a new request for the files such that new list will be fetched..
                        // or...
                        // 2. modify both of the arraylists of MovieFile objs to include the new filename instead.
                        adapter.updateArrayListsOnEdit(newFilename, listPosition);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setIcon(R.drawable.ic_edit);
                builder.show();
        }

        private void ChangeFileNameDB(String newFilename) {
            RequestQueue requestQueue = Volley.newRequestQueue(file.getContext());
            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    EDIT_FILENAME_URL,
                    response -> {
                        Toast.makeText(file.getContext(), "File name has been changed.", Toast.LENGTH_SHORT).show();
                        Log.d("Database", "file name has been successively changed in DB");
                    }, error -> {
                Toast.makeText(
                        file.getContext(),
                        "Unable to communicate with the server",
                        Toast.LENGTH_LONG).show();
                Log.d("Database", "Unable to communicate with server");
            }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("newfilename", newFilename);
                    params.put("oldfilename", fileName.getText().toString());
                    return params;
                }
            };

            requestQueue.add(request);
        }
        public void bindData(MovieFile movieFile, OnFileCardClickListener listener, int position) {
            fileName.setText(movieFile.getFileName());
            fileDateCreated.setText(movieFile.getDateCreated());

            btnTimeGraph.setOnClickListener(v -> {
                Toast.makeText(file.getContext(), "Opening time graph", Toast.LENGTH_SHORT).show();
                listener.onFileCardClick(fileName.getText().toString());
            });

            // assign moviefile in order to use it for share intent.
            this.movieFile = movieFile;
            // set up file long click listener
            // registering the context menu listener.
            this.listPosition = position; // setting up list position for list update.
            file.setOnCreateContextMenuListener(this);

            // set up file short click listener
            file.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create and show modal dialog with file information and progress bars
                    Context context = file.getContext();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_file_info, null);
                    builder.setView(dialogView);

                    // set filename to be selectable
                    fileName.setTextIsSelectable(true);
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

//            file.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    Context context = file.getContext();
//                    Toast.makeText(context, "Opening file page", Toast.LENGTH_LONG).show();
//                    listener.onFileCardClick(fileName.getText().toString());
//                    return true; // Return true to consume the long click event
//                }
//            });
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateArrayListsOnDelete(String fileName, int listPosition) {
        MovieFile fileItem = movieFileList.remove(listPosition);
        MovieFile fileItemFull = movieFileListFull.remove(listPosition);
        editAndRemoveListener.onFileRemove(listPosition);
        notifyDataSetChanged();
        notifyItemRemoved(listPosition); // Trigger the delete animation
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateArrayListsOnEdit(String newFilename, int listPosition) {
        if (newFilename!= null && !newFilename.isEmpty()) {
            MovieFile fileItem = movieFileList.get(listPosition);
            MovieFile fileItemFull = movieFileListFull.get(listPosition);
            fileItemFull.setFileName(newFilename);
            fileItem.setFileName(newFilename);
            editAndRemoveListener.onFileEdit(listPosition, newFilename);
            notifyDataSetChanged();
        }
    }


    public MovieFileAdapter(List<MovieFile> movieFileList, OnFileCardClickListener listener, OnFileEditAndRemoveListener editAndRemoveListener) {
        this.movieFileList = movieFileList;
        this.movieFileListFull = new ArrayList<>(movieFileList);
        // listener defining the onfilecard click callback method
        this.listener = listener;
        this.editAndRemoveListener = editAndRemoveListener;
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
        return new ViewHolder(fileView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Binding data to view.
        // Get data element
        MovieFile movieFile = movieFileList.get(position);
        // Set views in view of element in list. (from single view xml file)
        holder.bindData(movieFile, listener, position);
    }

    @SuppressLint("NotifyDataSetChanged")
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
