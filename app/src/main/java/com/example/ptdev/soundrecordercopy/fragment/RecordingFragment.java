package com.example.ptdev.soundrecordercopy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ptdev.soundrecordercopy.R;
import com.example.ptdev.soundrecordercopy.services.SoundRecordService;

import java.io.File;


public class RecordingFragment extends Fragment {
    public static final String TAG = "RecordingFragment";

    private TextView mTextView;
    private Chronometer mChronometer;
    private FloatingActionButton mFloatingActionButton;
    private String externalStoragePath;
    private Intent recordIntent;

    private boolean startRecording = false;
    private int tickCount = 0;

    public static RecordingFragment newInstance(){
        return new RecordingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recording, container, false);

        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.fragment_recording_fab);
        mChronometer = (Chronometer) view.findViewById(R.id.fragment_recording_chronometer);
        mTextView = (TextView) view.findViewById(R.id.fragment_recording_text_view);
        externalStoragePath = Environment.getExternalStorageDirectory().getPath();


        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording = !startRecording;
                onRecord(startRecording);

            }
        });


        return view;
    }

    public void onRecord(boolean start){
        //stop service
        if (!start && recordIntent != null) {
            mFloatingActionButton.setImageResource(R.drawable.ic_mic);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());


            getActivity().stopService(recordIntent);
            mTextView.setText(getResources().getString(R.string.record_fragment_text));
            return;
        }

        recordIntent = new Intent(getActivity(), SoundRecordService.class);

        Toast.makeText(getActivity(), "Recording Started", Toast.LENGTH_LONG).show();
        File file = new File(externalStoragePath + "/SoundRecorder");
        if (!file.exists()){ file.mkdir(); }

        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                switch (tickCount % 3){
                    case 0:
                        mTextView.setText("Recording");
                        tickCount++;
                        break;
                    case 1:
                        mTextView.setText("Recording.");
                        tickCount++;
                        break;
                    case 2:
                        mTextView.setText("Recording..");
                        tickCount=0;
                        break;
                }

            }
        });

        getActivity().startService(recordIntent);
        mFloatingActionButton.setImageResource(R.drawable.ic_stop);
    }
}
