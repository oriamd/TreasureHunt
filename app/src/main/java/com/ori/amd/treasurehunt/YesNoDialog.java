package com.ori.amd.treasurehunt;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Ori on 2/27/2017.
 * Displaying simple Yes or No Dialog
 */

public class YesNoDialog extends Dialog {
    TextView textView ;
    Button yesButton;
    public YesNoDialog(Context context) {
        super(context);
        setContentView(R.layout.yes_no_dialog);
        this.textView = (TextView)findViewById(R.id.textView24);
        yesButton = (Button)findViewById(R.id.yesbutton);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ((Button)findViewById(R.id.nobutton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    /**
     * Setting the text that will be display in the dialog
     * @param text
     */
    public void setContentText(String text){
        this.textView.setText(text);
    }
    /**
     * Setting Event listener to yes button
     * @param listener
     */
    public void setYesListener(View.OnClickListener listener){
        yesButton.setOnClickListener((View.OnClickListener) listener);
    }
}
