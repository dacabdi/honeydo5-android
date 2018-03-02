package com.honeydo5.honeydo.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.honeydo5.honeydo.R;

import java.util.ArrayList;

/**
 * Created by aaron on 2/24/2018.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{

    private Context context;
    private ArrayList<Task> taskList;

    public TaskAdapter(Context context, ArrayList<Task> taskList)
    {
        this.context = context;
        this.taskList = taskList;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(context);
        View view = inflator.inflate(R.layout.task_list_fragment, null);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task t = taskList.get(position);

        holder.title.setText(t.getHeader());
        holder.date.setText("DATE");
        holder.time.setText("TIME" + position);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder{

        TextView title, date, time;

        public TaskViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.task_title);
            date = itemView.findViewById(R.id.task_date);
            time = itemView.findViewById(R.id.task_time);
        }
    }
}
