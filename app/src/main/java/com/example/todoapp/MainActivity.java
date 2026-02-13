package com.example.todoapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.todoapp.adapter.TodoAdapter;
import com.example.todoapp.database.DatabaseHelper;
import com.example.todoapp.databinding.ActivityMainBinding;
import com.example.todoapp.model.TodoItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DatabaseHelper dbHelper;
    private TodoAdapter adapter;
    private List<TodoItem> todoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        todoList = new ArrayList<>();

        setupRecyclerView();

        binding.fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
        });

        checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void setupRecyclerView() {
        adapter = new TodoAdapter(todoList, new TodoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TodoItem item) {
                Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
                intent.putExtra("task_id", item.getId());
                startActivity(intent);
            }

            @Override
            public void onCheckboxClick(TodoItem item, boolean isChecked) {
                item.setCompleted(isChecked);
                dbHelper.updateTask(item);
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void loadTasks() {
        todoList = dbHelper.getAllTasks();
        adapter.updateData(todoList);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }
}
