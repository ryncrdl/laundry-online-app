package com.example.onlinelaundry.Utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatButton;
import com.example.onlinelaundry.R;

public class ModalMessage {
    private Dialog dialog;
    private TextView textViewMessage;
    private AppCompatButton BtnOkay;
    public ModalMessage(Context context) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_modal_message);
        dialog.setCancelable(false);
        textViewMessage = dialog.findViewById(R.id.message);
        BtnOkay = dialog.findViewById(R.id.BtnOkay);
    }

    public void setMessage(String message) {
        textViewMessage.setText(message);
    }

    public void setOkayButton(String text, View.OnClickListener onClickListener) {
        BtnOkay.setText(text);
        BtnOkay.setOnClickListener(onClickListener);
    }
    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
