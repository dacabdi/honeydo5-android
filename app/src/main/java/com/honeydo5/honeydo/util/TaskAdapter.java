package com.honeydo5.honeydo.util;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.honeydo5.honeydo.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{

    private Context context;
    private ArrayList<Task> taskList;

    private Calendar currentDate;

    public TaskAdapter(Context context, ArrayList<Task> taskList)
    {
        this.context = context;
        this.taskList = taskList;

        currentDate = GregorianCalendar.getInstance();
    }

    public void clearAll()
    {
        while(this.getItemCount() != 0)
            this.removeItem(0);
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(context);
        View view = inflator.inflate(R.layout.task_list_fragment, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {

        Task t = taskList.get(position);

        holder.title.setText(t.getName());
        holder.date.setText(android.text.format.DateFormat.format("MM/dd/yyyy", t.getDateAndTime()));
        holder.time.setText(android.text.format.DateFormat.format("hh:mm a", t.getDateAndTime()));

        // Underline priority tasks
        if(t.isPriority()) {
            holder.title.setPaintFlags(holder.title.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        }

        // Dull out previous tasks
        if(t.getDateAndTime().before(currentDate)) {
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.textOld));
            holder.date.setTextColor(ContextCompat.getColor(context, R.color.textOld));
            holder.time.setTextColor(ContextCompat.getColor(context, R.color.textOld));
        }

    }

    public void removeItem(int position) {
        Log.d("TASKADAPTER", "Removed item at " + position + " position");
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder{

        TextView title, date, time;
        LinearLayout layoutForeground, layoutBackgroundDelete, layoutBackgroundEdit;

        public TaskViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.taskListFragTextViewTitle);
            date = itemView.findViewById(R.id.taskListFragTextViewDate);
            time = itemView.findViewById(R.id.taskListFragTextViewTime);

            layoutForeground = itemView.findViewById(R.id.taskListFragForeground);
            layoutBackgroundDelete = itemView.findViewById(R.id.taskListFragBackgroundDelete);
            layoutBackgroundEdit = itemView.findViewById(R.id.taskListFragBackgroundEdit);
        }
    }
}
