package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.database.GetAllTask;
import com.example.myapplication.database.GetDates;
import com.example.myapplication.database.RoutineDB;
import com.example.myapplication.recadapter.HistoryAdapter;
import com.example.myapplication.recadapter.HistoryDetailAdapter;
import com.google.android.material.navigation.NavigationView;

public class HistoryDetailActivity extends AppCompatActivity {

    private static final String TAG = "PeriodicTaskActivity";

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textView = findViewById(R.id.routine_title);
        textView.setText(getIntent().getStringExtra("taskName"));

        int id = getIntent().getIntExtra("taskId",0);

        RecyclerView dailyRecyclerView = findViewById(R.id.days_recycler_view);
        GridLayoutManager dailyGridLayoutManager = new GridLayoutManager(this, 2);
        dailyRecyclerView.setLayoutManager(dailyGridLayoutManager);
        HistoryDetailAdapter dailyAdapter = new HistoryDetailAdapter(GetDates.getTaskHistory(this, id));
        dailyRecyclerView.setHasFixedSize(true);
        dailyRecyclerView.setAdapter(dailyAdapter);
        Log.d(TAG, "onCreate: daily tasks recycler view set up");
    }
}