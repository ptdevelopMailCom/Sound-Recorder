package com.example.ptdev.soundrecordercopy.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ptdev.soundrecordercopy.R;
import com.example.ptdev.soundrecordercopy.RecordItem;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PlayRecordingDialogFragment extends DialogFragment {
    public static final String TAG = "PlayRecordingDialogFragment";
    public static final String ARG_KEY = "record_item";

    private TextView mFileNameTextView;
    private TextView mCurrentProgressTextView;
    private TextView mLengthTextView;
    private SeekBar mSeekBar;
    private FloatingActionButton mButton;

    private RecordItem mRecordItem;
    private Handler mHandler;

    private boolean isPlaying = false;
    private MediaPlayer mMediaPlayer;
    private Runnable mRunnable;

    public static PlayRecordingDialogFragment newInstance(Parcelable item){
        PlayRecordingDialogFragment fragment = new PlayRecordingDialogFragment();
        Bundle b = new Bundle();
        b.putParcelable(ARG_KEY, item);

        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecordItem = (RecordItem) getArguments().getParcelable(ARG_KEY);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer != null){
                    int currentPosition = mMediaPlayer.getCurrentPosition();
                    mSeekBar.setProgress(currentPosition);

                    long minute = TimeUnit.MILLISECONDS.toMinutes(currentPosition);
                    long second = TimeUnit.MILLISECONDS.toSeconds(currentPosition) - TimeUnit.MINUTES.toSeconds(minute);

                    mCurrentProgressTextView.setText(String.format("%02d:%02d", minute, second));

                    updateSeekBar();
                }
            }
        };

        mHandler = new Handler();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_fragment_play_recording, null);

        mFileNameTextView = (TextView) view.findViewById(R.id.fragment_play_recordings_text_view_file_name);
        mLengthTextView = (TextView) view.findViewById(R.id.fragment_play_recordings_text_view_file_length);
        mSeekBar = (SeekBar) view.findViewById(R.id.fragment_play_recordings_seek_bar);
        mCurrentProgressTextView = (TextView) view.findViewById(R.id.fragment_play_recordings_text_view_current_progress);
        mButton = (FloatingActionButton) view.findViewById(R.id.fragment_play_recordings_fab_play);


        int recordDuration = mRecordItem.getLength();
        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) recordDuration);
        final long second  = TimeUnit.MILLISECONDS.toSeconds(recordDuration) - TimeUnit.MINUTES.toSeconds(minutes);
        mLengthTextView.setText(String.format("%02d:%02d", minutes, second));

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(isPlaying);
                isPlaying = !isPlaying;
            }
        });

        ColorFilter filter = new LightingColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        mSeekBar.getProgressDrawable().setColorFilter(filter);
        mSeekBar.getThumb().setColorFilter(filter);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mMediaPlayer != null && fromUser){
                    mMediaPlayer.seekTo(progress);
                    mHandler.removeCallbacks(mRunnable);

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mMediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.getCurrentPosition())
                                    - TimeUnit.MINUTES.toSeconds(minutes);

                    mCurrentProgressTextView.setText(String.format("%02d:%02d", minutes, seconds));

                    updateSeekBar();
                }else if(mMediaPlayer == null && fromUser){
                    createMediaPlayerFromProgress(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        builder.setView(view);
        return builder.create();
    }

    private void createMediaPlayerFromProgress(SeekBar seekBar, int progress, boolean fromUser) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mRecordItem.getFilePath());
            mMediaPlayer.prepare();

            seekBar.setMax(mMediaPlayer.getDuration());
            mMediaPlayer.seekTo(progress);

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });

        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    private void onPlay(boolean isPlaying) {
        if (!isPlaying){
            if (mMediaPlayer == null){
                startPlaying();
            }else {
                resumePlaying();
            }
        }else {
            pausePlaying();
        }
    }

    private void pausePlaying() {
        mButton.setImageResource(R.drawable.ic_play);

        mHandler.removeCallbacks(mRunnable);

        mMediaPlayer.pause();

    }

    private void resumePlaying() {
        mButton.setImageResource(R.drawable.ic_pause);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.start();

        updateSeekBar();
    }

    private void stopPlaying() {
        mButton.setImageResource(R.drawable.ic_play);
        isPlaying = false;
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;

        mSeekBar.setProgress(mSeekBar.getMax());
        mCurrentProgressTextView.setText(mLengthTextView.getText());


    }

    private void startPlaying() {
        mButton.setImageResource(R.drawable.ic_pause);
        mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(mRecordItem.getFilePath());
            mMediaPlayer.prepare();


            mSeekBar.setMax(mMediaPlayer.getDuration());

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                    updateSeekBar();
                }
            });

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                    //Log.i(TAG, "running here");
                }
            });

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    private void updateSeekBar() {
        mHandler.postDelayed(mRunnable, 1000);
    }




}
