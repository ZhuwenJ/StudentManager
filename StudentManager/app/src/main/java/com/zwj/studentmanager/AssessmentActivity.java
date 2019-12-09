package com.zwj.studentmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.RadioButton;

public class AssessmentActivity extends AppCompatActivity {
    Drawable drawable;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RadioButton id=findViewById(R.id.changes);
        drawable=this.getResources().getDrawable(R.drawable.ic_delete);
        id.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null);
    }

}
