package com.honeydo5.honeydo.util;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.honeydo5.honeydo.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by aaron on 2/24/2018.
 */

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

        holder.title.setText(t.getHeader());
        holder.date.setText(android.text.format.DateFormat.format("MM/dd/yyyy", t.getDate()));
        holder.time.setText(android.text.format.DateFormat.format("hh:mm a", t.getDate()));

        // Underline priority tasks
        if(t.isPriority()) {
            holder.title.setPaintFlags(holder.title.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        }

        // Dull out previous tasks
        if(t.getDate().before(currentDate)) {
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.textOld));
            holder.date.setTextColor(ContextCompat.getColor(context, R.color.textOld));
            holder.time.setTextColor(ContextCompat.getColor(context, R.color.textOld));
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder{

        TextView title, date, time;

        public TaskViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.taskListFragTextViewTitle);
            date = itemView.findViewById(R.id.taskListFragTextViewDate);
            time = itemView.findViewById(R.id.taskListFragTextViewTime);
        }
    }
}
