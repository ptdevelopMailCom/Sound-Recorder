package com.example.ptdev.soundrecordercopy.adapter;

import android.content.Context;
import android.os.Environment;
import android.os.FileObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ptdev.soundrecordercopy.R;
import com.example.ptdev.soundrecordercopy.RecordItem;
import com.example.ptdev.soundrecordercopy.database.RecordingDbHelper;
import com.example.ptdev.soundrecordercopy.fragment.PlayRecordingDialogFragment;
import com.example.ptdev.soundrecordercopy.fragment.RecordOptionsDialogFragment;
import com.example.ptdev.soundrecordercopy.listener.onDatabaseChangedListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SoundRecordAdapter extends RecyclerView.Adapter<SoundRecordAdapter.RecordViewHolder>
        implements onDatabaseChangedListener {
    private static final String TAG = "SoundRecordAdapter";
    private RecordingDbHelper mDbHelper;
    private Context mContext;

    @Override
    public void onNewDatabaseEntryAdded() {
        Log.i("debugUsage", "it runs");
        notifyDataSetChanged();
        //notifyItemInserted(getItemCount()-1);
    }

    @Override
    public void onDatabaseEntryRenamed() {
        Log.i(TAG, "it runs");
        notifyDataSetChanged();
    }

    public SoundRecordAdapter(Context context){
        super();
        mContext = context;

        mDbHelper = new RecordingDbHelper(mContext);
        mDbHelper.setOnDatabaseChangedListener(this);
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.sound_record_view_holder, parent, false);

        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        final RecordItem recordItem = mDbHelper.getItemAt(position);
        int itemDuration = recordItem.getLength();

        //Log.i(TAG, String.valueOf(itemDuration));

        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration) -
                TimeUnit.MINUTES.toSeconds(minutes);

        holder.mLengthTextView.setText(String.format("%02d:%02d", minutes, seconds));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, hh:mm");
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(recordItem.getDate());
        holder.mDateTextView.setText(sdf.format(date));

        Log.i(TAG, sdf.format(date).toString());

        holder.mFileNameTextView.setText(recordItem.getFileName());

        holder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayRecordingDialogFragment fragment = PlayRecordingDialogFragment.newInstance(recordItem);
                FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();

                fragment.show(fm, PlayRecordingDialogFragment.TAG);
            }
        });

        holder.getCardView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                RecordOptionsDialogFragment fragment = RecordOptionsDialogFragment.newInstance(recordItem, mContext, SoundRecordAdapter.this);

                FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();

                fragment.show(fm, RecordOptionsDialogFragment.TAG);


                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDbHelper.getCount();
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder{
        private TextView mFileNameTextView;
        private TextView mLengthTextView;
        private TextView mDateTextView;
        private CardView mCardView;
        private View view;

        public RecordViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            mCardView = view.findViewById(R.id.sound_record_view_holder_card_view);
            mFileNameTextView = view.findViewById(R.id.sound_record_view_holder_text_view_file_name);
            mLengthTextView = view.findViewById(R.id.sound_record_view_holder_text_view_length);
            mDateTextView = view.findViewById(R.id.sound_record_view_holder_text_view_date);
        }

        public CardView getCardView() {
            return mCardView;
        }
    }
}
