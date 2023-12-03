package com.example.onlinelaundry.Booking;

import android.os.Parcel;
import android.os.Parcelable;

public class BookingResponse implements Parcelable{
    private String _id;
    private String dateOfBooking, paymentMethod, service,  kg, total,pickUpOption;
    private Boolean status;
    protected BookingResponse(Parcel in) {
        _id = in.readString();
        dateOfBooking = in.readString();
        paymentMethod = in.readString();
        service= in.readString();
        status = in.readBoolean();
        kg= in.readString();
        total= in.readString();
        pickUpOption= in.readString();
    }

    public static final Creator<BookingResponse> CREATOR = new Creator<BookingResponse>() {
        @Override
        public BookingResponse createFromParcel(Parcel in) {
            return new BookingResponse(in);
        }

        @Override
        public BookingResponse[] newArray(int size) {
            return new BookingResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(dateOfBooking);
        dest.writeString(paymentMethod);
        dest.writeString(service);
        dest.writeBoolean(status);
        dest.writeString(kg);
        dest.writeString(total);
        dest.writeString(pickUpOption);
    }

    public String get_id() {
        return _id;
    }

    public String getDateOfBooking() {
        return dateOfBooking;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getService() {
        return service;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getTotal() {
        return total;
    }
    public String getPickUpOption() {
        return pickUpOption;
    }
    public String getKg() {
        return kg;
    }

}
