package com.example.onlinelaundry.Status;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelaundry.R;

public class StatusViewHolder extends RecyclerView.ViewHolder {
    TextView date, mod, total, service, kg, status, option;

    public StatusViewHolder(@NonNull View view) {
        super(view);

        date = view.findViewById(R.id.date);
        mod = view.findViewById(R.id.mod);
        total = view.findViewById(R.id.price);
        service = view.findViewById(R.id.service);
        kg = view.findViewById(R.id.kg);
        status = view.findViewById(R.id.status);
        option = view.findViewById(R.id.pickup);
    }
}
