package com.honeydo5.honeydo.util;

import java.util.ArrayList;

public class TaskSystem {
    private static ArrayList<Task> taskList = new ArrayList<Task>();

    public static ArrayList<Task> getTaskList() {
        return taskList;
    }

    public static Task getTask(int i) { return taskList.get(i); }

    public static void addTask(Task t) {
        taskList.add(t);
    }
}
