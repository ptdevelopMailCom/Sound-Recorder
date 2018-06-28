package com.example.ptdev.soundrecordercopy.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ptdev.soundrecordercopy.R;
import com.example.ptdev.soundrecordercopy.adapter.SoundRecordAdapter;

import org.w3c.dom.Text;

import java.io.File;

public class SavedRecordingsFragment extends Fragment {
    private RecyclerView mRecyclerView;

    public static SavedRecordingsFragment newInstance(){
        return new SavedRecordingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_recordings, container,false);
        mRecyclerView = view.findViewById(R.id.fragment_saved_recordings_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new SoundRecordAdapter(getActivity()));



        return view;
    }




}
