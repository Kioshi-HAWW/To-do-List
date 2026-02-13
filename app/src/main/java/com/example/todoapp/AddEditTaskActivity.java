package com.example.todoapp;

import static com.example.todoapp.Notification.AlarmReceiver.scheduleNotification;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.database.DatabaseHelper;
import com.example.todoapp.databinding.ActivityAddEditBinding;
import com.example.todoapp.model.TodoItem;

import java.text.DateFormat;
import java.util.Calendar;

public class AddEditTaskActivity extends AppCompatActivity {

    private ActivityAddEditBinding binding;
    private DatabaseHelper dbHelper;
    private TodoItem currentTask;
    private long selectedDueDate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        if (getIntent().hasExtra("task_id")) {
            int taskId = getIntent().getIntExtra("task_id", -1);
            currentTask = dbHelper.getTask(taskId);
            populateFields(currentTask);
        }

        binding.btnPickDate.setOnClickListener(v -> showDatePicker());
        binding.btnPickTime.setOnClickListener(v -> showTimePicker());

        binding.btnSave.setOnClickListener(v -> saveTask());
    }

    private void populateFields(TodoItem task) {
        binding.etTitle.setText(task.getTitle());
        binding.etDescription.setText(task.getDescription());
        if (task.getDueDate() > 0) {
            selectedDueDate = task.getDueDate();
            updateDateLabel();
        }
        binding.switchReminder.setChecked(task.hasReminder());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (selectedDueDate > 0) {
            calendar.setTimeInMillis(selectedDueDate);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    selectedDueDate = calendar.getTimeInMillis();
                    updateDateLabel();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        if (selectedDueDate > 0) {
            calendar.setTimeInMillis(selectedDueDate);
        }
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    selectedDueDate = calendar.getTimeInMillis();
                    updateDateLabel();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void updateDateLabel() {
        if (selectedDueDate > 0) {
            String dateStr = DateFormat.getDateTimeInstance().format(selectedDueDate);
            binding.tvSelectedDateTime.setText(dateStr);
        }
    }

    private void saveTask() {
        String title = binding.etTitle.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        boolean hasReminder = binding.switchReminder.isChecked();

        if (title.isEmpty()) {
            binding.tilTitle.setError("Title is required");
            return;
        }

        if (currentTask == null) {
            currentTask = new TodoItem();
        }

        currentTask.setTitle(title);
        currentTask.setDescription(description);
        currentTask.setDueDate(selectedDueDate);
        currentTask.setHasReminder(hasReminder);

        long id;
        if (currentTask.getId() > 0) {
            dbHelper.updateTask(currentTask);
            id = currentTask.getId();
        } else {
            id = dbHelper.addTask(currentTask);
            currentTask.setId((int) id);
        }

        if (hasReminder && selectedDueDate > System.currentTimeMillis()) {
            scheduleNotification(this, currentTask);
        }

        Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
