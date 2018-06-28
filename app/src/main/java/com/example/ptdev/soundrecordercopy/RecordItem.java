package com.example.ptdev.soundrecordercopy;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class RecordItem implements Parcelable{
    private static final String TAG = "RecordItem";
    private int mId;
    private String mFileName;
    private String mFilePath;
    private int mLength;
    private long mDate;

    public RecordItem(){}


    protected RecordItem(Parcel in) {
        mId = in.readInt();
        mFileName = in.readString();
        mFilePath = in.readString();
        mLength = in.readInt();
        mDate = in.readLong();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public int getLength() {
        return mLength;

    }

    public void setLength(int length) {
        mLength = length;

    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        Log.i(TAG, "The value of mDate is " + String.valueOf(date));
        mDate = date;
    }

    public static final Creator<RecordItem> CREATOR = new Creator<RecordItem>() {
        @Override
        public RecordItem createFromParcel(Parcel in) {
            return new RecordItem(in);
        }

        @Override
        public RecordItem[] newArray(int size) {
            return new RecordItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mFileName);
        dest.writeString(mFilePath);
        dest.writeInt(mLength);
        dest.writeLong(mDate);
    }
}
