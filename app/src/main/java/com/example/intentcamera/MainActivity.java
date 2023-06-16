package com.example.intentcamera;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button orientationActivitySwitchButton = (Button) findViewById(R.id.button1);
        orientationActivitySwitchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplication(), Camera.class);
                startActivity(intent);
            }
        });
        Button locationActivitySwitchButton = (Button) findViewById(R.id.button2);
        locationActivitySwitchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {Intent intent = new Intent(getApplicationContext(),
                    Folder.class);startActivity(intent);
            }
        });
    }
}