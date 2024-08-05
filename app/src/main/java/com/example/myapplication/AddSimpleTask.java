package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.database.SimpleDB;
import com.example.myapplication.database.TaskDB;
import com.example.myapplication.model.SimpleModel;
import com.example.myapplication.model.TaskModel;

import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;

public class AddSimpleTask extends AppCompatActivity {

    String TAG = "AddSimpleTask";


    private SimpleDB db;
    private EditText addTitle, addDescription;
    private TextView addDate;
    private Button addButton, dateButton;

    private SimpleModel simpleModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_simple_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // initials
        addTitle = findViewById(R.id.task_title_edit_text);
        addDescription = findViewById(R.id.task_description_edit_text);
        addButton = findViewById(R.id.add_btn);
        Log.d(TAG, "onCreate: views initialized");

        // database
        db = new SimpleDB(this);
        Log.d(TAG, "onCreate: database initialized");

        addButton.setOnClickListener(v -> {
            Log.d(TAG, "onClick: add button clicked");

            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(addTitle.getText());
            checkBox.setChecked(false);

            simpleModel = new SimpleModel(checkBox, addDescription.getText().toString(), 0);

            Log.d(TAG, "onClick: title: " + simpleModel.getCheckBox().getText() + ", description: " + simpleModel.getDescription());


            db.insertRecord(simpleModel.getCheckBox().getText().toString() , simpleModel.getDescription(), 0);

            Log.d(TAG, "onClick: record inserted into database");

            Toast.makeText(AddSimpleTask.this, getString(R.string.add_successfully), Toast.LENGTH_LONG).show();
            Log.i(TAG, "onClick: task added successfully");

            Intent intent = new Intent(this, SimpleTaskActivity.class);
            startActivity(intent);
            finish();
            Log.d(TAG, "onClick: navigating to SimpleTaskActivity");
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "handleOnBackPressed: back pressed, navigating to PeriodicTaskActivity");
                Intent intent = new Intent(AddSimpleTask.this, NormalTaskActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}