package com.example.andro.realestate.data;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by andro on 2016/10/20.
 */

public class PropertySuggestion implements SearchSuggestion {

    private String mHint;
    private boolean mIsHistory = false;

    public PropertySuggestion(String suggestion) {this.mHint = suggestion;}

    public PropertySuggestion(Parcel source) {
        this.mHint = source.readString();
        this.mIsHistory = source.readInt() != 0;
    }


    public void setIsHistory(boolean isHistory) {
        this.mIsHistory = isHistory;
    }

    public boolean getIsHistory() {
        return this.mIsHistory;
    }

    public static final Creator<PropertySuggestion> CREATOR = new Creator<PropertySuggestion>() {
        @Override
        public PropertySuggestion createFromParcel(Parcel in) {
            return new PropertySuggestion(in);
        }

        @Override
        public PropertySuggestion[] newArray(int size) {
            return new PropertySuggestion[size];
        }
    };

    @Override
    public String getBody() {
        return mHint;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mHint);
        dest.writeInt(mIsHistory ? 1 : 0);
    }
}
