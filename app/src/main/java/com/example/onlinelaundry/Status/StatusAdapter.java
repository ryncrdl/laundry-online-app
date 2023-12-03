package com.example.onlinelaundry.Status;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.onlinelaundry.R;
import com.example.onlinelaundry.Booking.BookingResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusAdapter extends RecyclerView.Adapter<StatusViewHolder> {
    List<BookingResponse> booking;
    public Context context;
    public StatusAdapter(Context context, List<BookingResponse> booking) {
        this.booking = booking;
        this.context = context;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StatusViewHolder(LayoutInflater.from(context).inflate(R.layout.account_booking_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BookingResponse book = booking.get(position);
        holder.date.setText(book.getDateOfBooking());
        holder.mod.setText(book.getPaymentMethod());
        holder.total.setText(book.getTotal());
        holder.service.setText(book.getService());
        holder.kg.setText(book.getKg());
        Boolean status = book.getStatus();
        String getStatus = status ? "Completed" : "Pending";
        holder.status.setText(getStatus);
        holder.option.setText(book.getPickUpOption());
    }
    @Override
    public int getItemCount() {
        return booking.size();
    }

}
