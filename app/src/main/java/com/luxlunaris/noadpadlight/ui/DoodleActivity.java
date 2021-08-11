package com.luxlunaris.noadpadlight.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.luxlunaris.noadpadlight.R;

import java.io.File;

public class DoodleActivity extends AppCompatActivity {


    DoodleView doodleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_doodle);

        ConstraintLayout l = findViewById(R.id.doodle_activity_layout);

        doodleView = new DoodleView(this);

        l.addView(doodleView);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //inflate the menu's layout xml
        getMenuInflater().inflate(R.menu.doodle_activity_toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.done_doodling:
                File doodleFile = doodleView.getSnapshot();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("DOODLE_FILE", doodleFile);
                setResult(RESULT_OK, resultIntent);
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}