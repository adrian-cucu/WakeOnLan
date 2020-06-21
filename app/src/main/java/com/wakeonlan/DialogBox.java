package com.wakeonlan;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

public class DialogBox
        extends Dialog
        implements android.view.View.OnClickListener {

    public static final String TAG = "DialogBox";

    private String mTitle;
    private TextView mDialogTitle;
    private Button mButtonYes, mButtonNo;
    private DialogBox.OnClickListener mOnClickPositiveButtonListener;
    private DialogBox.OnClickListener mOnClickNegativeButtonListener;


    public DialogBox(@NonNull Context context, String title) {
//        super(context, R.style.Dialog);
        super(context);
        mTitle = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_box);

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        mDialogTitle = (TextView) findViewById(R.id.dialog_title);
        mDialogTitle.setText(mTitle);

        mButtonYes = (Button) findViewById(R.id.btn_yes);
        mButtonNo = (Button) findViewById(R.id.btn_no);

        mButtonYes.setOnClickListener(this);
        mButtonNo.setOnClickListener(this);

    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        mDialogTitle.setText(title);
    }

    public void setOnClickPositiveButtonListener(DialogBox.OnClickListener listener) {
        mOnClickPositiveButtonListener = listener;
    }

    public void setOnClickNegativeButtonListener(DialogBox.OnClickListener listener) {
        mOnClickNegativeButtonListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
//                c.finish();
                Log.d(TAG, "dialog box clicked YES");
                if (mOnClickPositiveButtonListener != null) {
                    mOnClickPositiveButtonListener.onClick(this, R.id.btn_no);
                }
                break;
            case R.id.btn_no:
//                Log.d(TAG, "dialog box clicked NO");
//                dismiss();
                if (mOnClickNegativeButtonListener != null) {
                    mOnClickNegativeButtonListener.onClick(this, R.id.btn_yes);
                }
                break;
            default:
                break;
        }
        dismiss();
    }

    public interface OnClickListener {
        void onClick(DialogBox dialog, int which);
    }

}
