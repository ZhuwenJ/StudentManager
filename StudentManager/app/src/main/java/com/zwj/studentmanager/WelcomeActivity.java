package com.zwj.studentmanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import me.wangyuwei.particleview.ParticleView;

public class WelcomeActivity extends AppCompatActivity {
    private ParticleView pv;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        pv = (ParticleView) findViewById(R.id.pv);
        pv.startAnim();

        pv.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                WelcomeActivity.this.startActivity(intent);
                finish();
            }
        });

    }
    }

