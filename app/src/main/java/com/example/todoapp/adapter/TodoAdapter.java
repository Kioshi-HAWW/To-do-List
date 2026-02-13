package com.example.todoapp.adapter;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.databinding.ItemTodoBinding;
import com.example.todoapp.model.TodoItem;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<TodoItem> todoList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TodoItem item);
        void onCheckboxClick(TodoItem item, boolean isChecked);
    }

    public TodoAdapter(List<TodoItem> todoList, OnItemClickListener listener) {
        this.todoList = todoList;
        this.listener = listener;
    }

    public void updateData(List<TodoItem> newTodoList) {
        this.todoList = newTodoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTodoBinding binding = ItemTodoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TodoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        TodoItem item = todoList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    class TodoViewHolder extends RecyclerView.ViewHolder {
        private final ItemTodoBinding binding;

        public TodoViewHolder(ItemTodoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(todoList.get(position));
                }
            });

            binding.cbIsCompleted.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCheckboxClick(todoList.get(position), binding.cbIsCompleted.isChecked());
                }
            });
        }

        public void bind(TodoItem item) {
            binding.tvTitle.setText(item.getTitle());
            binding.cbIsCompleted.setChecked(item.isCompleted());

            if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                binding.tvDescription.setText(item.getDescription());
                binding.tvDescription.setVisibility(View.VISIBLE);
            } else {
                binding.tvDescription.setVisibility(View.GONE);
            }

            if (item.getDueDate() > 0) {
                CharSequence dateStr = DateFormat.format("MMM dd, yyyy h:mm a", item.getDueDate());
                binding.tvDueDate.setText("Due: " + dateStr);
                binding.tvDueDate.setVisibility(View.VISIBLE);
            } else {
                binding.tvDueDate.setVisibility(View.GONE);
            }

            if (item.hasReminder()) {
                binding.ivNotificationIcon.setVisibility(View.VISIBLE);
            } else {
                binding.ivNotificationIcon.setVisibility(View.GONE);
            }
        }
    }
}
