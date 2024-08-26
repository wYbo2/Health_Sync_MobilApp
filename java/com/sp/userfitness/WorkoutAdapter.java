package com.sp.userfitness;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private final List<Workout> workoutList;

    public WorkoutAdapter(List<Workout> workoutList) {
        this.workoutList = workoutList;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workoutList.get(position);
        holder.dateTextView.setText(workout.getDate());
        holder.activityNameTextView.setText(workout.getActivityName());
        holder.durationTextView.setText(workout.getFormattedDuration()); // Use formatted duration
        holder.caloriesBurntTextView.setText(String.valueOf(workout.getCaloriesBurnt()));
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView activityNameTextView;
        TextView durationTextView;
        TextView caloriesBurntTextView;

        WorkoutViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            activityNameTextView = itemView.findViewById(R.id.activityNameTextView);
            durationTextView = itemView.findViewById(R.id.durationTextView);
            caloriesBurntTextView = itemView.findViewById(R.id.caloriesBurntTextView);
        }
    }
}
