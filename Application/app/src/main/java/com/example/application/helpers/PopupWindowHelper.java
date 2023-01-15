package com.example.application.helpers;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.application.R;

public class PopupWindowHelper {

    public static void createPopup(LayoutInflater inflater, int buttonTextId) {
        View popupView = inflater.inflate(R.layout.popup_window, null);
        TextView text = popupView.findViewById(R.id.popupText);
        EditText enteredValue = popupView.findViewById(R.id.WaterEnteredValue);
        Button submitButton = popupView.findViewById(R.id.acceptWaterAmountButton);

        text.setText(buttonTextId);
        enteredValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
    }


}
