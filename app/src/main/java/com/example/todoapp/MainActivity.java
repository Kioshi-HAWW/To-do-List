package com.example.todoapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private ActivityResultLauncher<Intent> addTaskLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        todoList = new ArrayList<>();

        setupRecyclerView();

        addTaskLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadTasks();
                }
            });

        binding.fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            addTaskLauncher.launch(intent);
        });

        checkPermissions();
        loadTasks(); // Initial load
    }

    private void setupRecyclerView() {
        adapter = new TodoAdapter(todoList, new TodoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TodoItem item) {
                Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
                intent.putExtra("task_id", item.getId());
                addTaskLauncher.launch(intent);
            }

            @Override
            public void onCheckboxClick(TodoItem item, boolean isChecked) {
                item.setCompleted(isChecked);
                dbHelper.deleteTask(item);
                loadTasks();
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void loadTasks() {
        todoList.clear();
        todoList.addAll(dbHelper.getAllTasks());
        adapter.notifyDataSetChanged();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }
}
