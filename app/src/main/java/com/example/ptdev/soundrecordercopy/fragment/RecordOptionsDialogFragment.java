package com.example.ptdev.soundrecordercopy.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ptdev.soundrecordercopy.MainActivity;
import com.example.ptdev.soundrecordercopy.R;
import com.example.ptdev.soundrecordercopy.RecordItem;
import com.example.ptdev.soundrecordercopy.adapter.SoundRecordAdapter;
import com.example.ptdev.soundrecordercopy.database.RecordingDbHelper;
import com.example.ptdev.soundrecordercopy.listener.onDatabaseChangedListener;

import java.io.File;
import java.util.ArrayList;

public class RecordOptionsDialogFragment extends DialogFragment {
    public static final String TAG = "RecordOptionsDialogFragment";
    public static final String ARG_KEY = "parcelableItem";
    private ArrayList<String> mStringArrayList = new ArrayList<>();
    private RecordItem mRecordItem;
    private static Context mContext;
    private static onDatabaseChangedListener mListener;
    private RecordingDbHelper mDbHelper;



    public static RecordOptionsDialogFragment newInstance(RecordItem item, Context context, onDatabaseChangedListener listener){
        RecordOptionsDialogFragment fragment = new RecordOptionsDialogFragment();
        mContext = context;
        mListener = listener;

        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_KEY, item);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDbHelper = new RecordingDbHelper(mContext);
        mDbHelper.setOnDatabaseChangedListener(mListener);
        Log.i(TAG, "attached");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(super.getContext());


        mRecordItem = getArguments().getParcelable(ARG_KEY);

        mStringArrayList.add(getString(R.string.dialog_file_share));
        mStringArrayList.add(getString(R.string.dialog_file_rename));
        mStringArrayList.add(getString(R.string.dialog_file_delete));

        CharSequence[] items =  mStringArrayList.toArray(new CharSequence[mStringArrayList.size()]);

        //Log.i(TAG, items[0].toString());

        builder.setTitle(R.string.dialog_file_title);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    shareFileDialog();
                }else if (which == 1){
                    renameFileDialog();
                }else if (which == 2){
                    deleteFileDialog();
                }

            }
        });


        return builder.create();
    }

    private void renameFileDialog() {
        AlertDialog.Builder renameDialog = new AlertDialog.Builder(getActivity());


        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_fragment_rename, null, true);
        final EditText editText = view.findViewById(R.id.dialog_fragment_rename_edit_text);

        renameDialog.setView(view);
        renameDialog.setTitle("Rename File");
        renameDialog.setCancelable(true);
        renameDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String value = editText.getText().toString().trim() + ".mp4";
                rename(value);

            }
        });



        renameDialog.show();
    }

    private void rename(String filename){
        String originalPath = mRecordItem.getFilePath();
        String newPath = originalPath.substring(0, originalPath.lastIndexOf("/")+1) + filename;
        //Log.i(TAG, newPath);

        File oldFile = new File(originalPath);
        File newFile = new File(newPath);
        if (!newFile.exists() && !newFile.isDirectory()){
            oldFile.renameTo(newFile);
            mDbHelper.rename(mRecordItem, filename, newPath);
            Toast.makeText(mContext, "file is renamed", Toast.LENGTH_LONG).show();

        }else {
            Toast.makeText(getActivity(), "File exists already", Toast.LENGTH_LONG).show();
        }

    }

    private void deleteFileDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_fragment_delete, null);

        builder.setTitle(getResources().getString(R.string.dialog_file_delete));
        builder.setView(view);

        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file = new File(mRecordItem.getFilePath());
                file.delete();

                mDbHelper.delete(mRecordItem);
                Toast.makeText(mContext, "File is deleted", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        builder.show();
    }


    private void shareFileDialog(){
        File file = new File(mRecordItem.getFilePath());
        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("audio/mp4");

        Intent chooser = Intent.createChooser(intent,getResources().getString(R.string.dialog_file_intent_title));
        getActivity().startActivity(chooser);
    }


}
