package com.example.andro.realestate;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by andro on 2016/10/20.
 */

public class PropertySuggestion implements SearchSuggestion {

    private String mPropertyName;
    private boolean mIsHistory = false;

    public PropertySuggestion(String suggestion) {this.mPropertyName = suggestion.toLowerCase();}

    public PropertySuggestion(Parcel source) {
        this.mPropertyName = source.readString();
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
        return mPropertyName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPropertyName);
        dest.writeInt(mIsHistory ? 1 : 0);
    }
}
