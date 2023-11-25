package com.example.onlinelaundry.Login;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class AccountResponse implements Parcelable{
    private String _id;
    private String fullName, username, email, contact, validId;
    protected AccountResponse(Parcel in) {
        _id = in.readString();
        fullName = in.readString();
        username = in.readString();
        email = in.readString();
        contact = in.readString();
        validId = in.readString();

    }

    public static final Parcelable.Creator<AccountResponse> CREATOR = new Parcelable.Creator<AccountResponse>() {
        @Override
        public AccountResponse createFromParcel(Parcel in) {
            return new AccountResponse(in);
        }

        @Override
        public AccountResponse[] newArray(int size) {
            return new AccountResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(fullName);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(contact);
        dest.writeString(validId);
    }

    public String get_id() {
        return _id;
    }

    public String getFullName() {
        return fullName;
    }
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public String getValidId() {
        return validId;
    }



}
