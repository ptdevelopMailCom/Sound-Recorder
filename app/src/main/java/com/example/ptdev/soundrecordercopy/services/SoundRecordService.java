package com.example.ptdev.soundrecordercopy.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.util.Log;
import android.widget.Toast;

import com.example.ptdev.soundrecordercopy.MySharedPreferences;
import com.example.ptdev.soundrecordercopy.database.RecordingDbHelper;

import java.io.File;
import java.io.IOException;

public class SoundRecordService extends Service {
    public static final String TAG = "SoundRecordService";

    private MediaRecorder mRecorder;
    private RecordingDbHelper mDbHelper;
    private String mFileName;
    private String mFilePath;
    private long mStartingTimeMillis;
    private long mElapsedTimeMillis;

    @Override
    public void onCreate() {
        super.onCreate();
        mDbHelper = new RecordingDbHelper(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecord();
        return super.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecord();
    }

    public void startRecord(){
        setFileNameAndPath();

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setAudioChannels(1);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        if (MySharedPreferences.getPrefHighQuality(this)){
            mRecorder.setAudioSamplingRate(44100);
            mRecorder.setAudioEncodingBitRate(192000);
        }

        mRecorder.setOutputFile(mFilePath);

        //Log.i(TAG, mFilePath);

        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();

        } catch (IOException e) {
            //Log.e(TAG, "prepare() failed");
            e.printStackTrace();
        }
    }

    public void stopRecord(){
        mRecorder.stop();
        mElapsedTimeMillis = System.currentTimeMillis() - mStartingTimeMillis;
        mRecorder.release();

        Toast.makeText(getApplicationContext(), mFileName + "is saved in " + mFilePath, Toast.LENGTH_LONG).show();;

        mRecorder = null;
        long rowId = mDbHelper.add(mFileName, mFilePath, mElapsedTimeMillis);


    }

    public void setFileNameAndPath(){
        String externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = externalStoragePath + "/SoundRecorder/";

        File file;
        int count = 0;
        do{
            mFileName = "Recording_" + (mDbHelper.getCount() + count) + ".mp4";
            mFilePath = filePath + mFileName;
            file = new File(mFileName);
            count++;
        }while (file.exists() && !file.isDirectory());

    }
}
