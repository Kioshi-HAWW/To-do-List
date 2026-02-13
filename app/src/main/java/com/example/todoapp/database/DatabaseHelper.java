package com.example.todoapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todoapp.model.TodoItem;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo_db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TASKS = "tasks";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IS_COMPLETED = "is_completed";
    private static final String KEY_DUE_DATE = "due_date";
    private static final String KEY_HAS_REMINDER = "has_reminder";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TITLE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_IS_COMPLETED + " INTEGER,"
                + KEY_DUE_DATE + " INTEGER,"
                + KEY_HAS_REMINDER + " INTEGER" + ")";
        db.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    public long addTask(TodoItem task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, task.getTitle());
        values.put(KEY_DESCRIPTION, task.getDescription());
        values.put(KEY_IS_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(KEY_DUE_DATE, task.getDueDate());
        values.put(KEY_HAS_REMINDER, task.hasReminder() ? 1 : 0);

        long id = db.insert(TABLE_TASKS, null, values);
        db.close();
        return id;
    }

    public TodoItem getTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASKS, new String[]{KEY_ID, KEY_TITLE, KEY_DESCRIPTION, KEY_IS_COMPLETED, KEY_DUE_DATE, KEY_HAS_REMINDER}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        TodoItem task = new TodoItem(
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3) == 1,
                cursor.getLong(4),
                cursor.getInt(5) == 1);
        task.setId(cursor.getInt(0));
        
        cursor.close();
        return task;
    }

    public List<TodoItem> getAllTasks() {
        List<TodoItem> taskList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_TASKS + " ORDER BY " + KEY_DUE_DATE + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TodoItem task = new TodoItem();
                task.setId(cursor.getInt(0));
                task.setTitle(cursor.getString(1));
                task.setDescription(cursor.getString(2));
                task.setCompleted(cursor.getInt(3) == 1);
                task.setDueDate(cursor.getLong(4));
                task.setHasReminder(cursor.getInt(5) == 1);
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return taskList;
    }

    public int updateTask(TodoItem task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, task.getTitle());
        values.put(KEY_DESCRIPTION, task.getDescription());
        values.put(KEY_IS_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(KEY_DUE_DATE, task.getDueDate());
        values.put(KEY_HAS_REMINDER, task.hasReminder() ? 1 : 0);

        return db.update(TABLE_TASKS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
    }

    public void deleteTask(TodoItem task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
        db.close();
    }
}
