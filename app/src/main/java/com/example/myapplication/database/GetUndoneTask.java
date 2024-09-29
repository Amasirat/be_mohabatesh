package com.example.myapplication.database;

import static com.example.myapplication.changer.BoolInt.intToBool;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.CheckBox;

import com.ali.uneversaldatetools.date.JalaliDateTime;
import com.example.myapplication.customwidget.MultiStateCheckBox;
import com.example.myapplication.model.Period;
import com.example.myapplication.model.PeriodicModel;
import com.example.myapplication.model.tasks.DeadLinedTask;
import com.example.myapplication.model.tasks.SimpleTask;
import com.example.myapplication.time.PeriodicCheckBoxReset;

import java.util.ArrayList;
import java.util.Calendar;

public class GetUndoneTask {

    private static final String TAG = "GetAllTask";

    static TaskDB db;
    static ArrayList<DeadLinedTask> today;
    static ArrayList<DeadLinedTask> future;
    static ArrayList<DeadLinedTask> past;
    static DeadLinedTask[] todayModels;
    static DeadLinedTask[] futureModels;
    static DeadLinedTask[] pastModels;

    // periodic task
    static RoutineDB routineDB;
    static ArrayList<PeriodicModel> dailyTasks;
    static ArrayList<PeriodicModel> weeklyTasks;
    static ArrayList<PeriodicModel> monthlyTasks;
    static PeriodicModel[] dailyModels;
    static PeriodicModel[] weeklyModels;
    static PeriodicModel[] monthlyModels;
    static PeriodicModel[] allRoutineModels;

    //Simple task
    static SimpleDB simpleDB;
    static ArrayList<SimpleTask> simpleTasksList;
    static SimpleTask[] simpleTasksArray;

    // public method
    public static DeadLinedTask[] todayTasks(Context context) {
        getNormalTasks(context);
        return todayModels;
    }

    public static DeadLinedTask[] futureTasks(Context context) {
        getNormalTasks(context);
        return futureModels;
    }

    public static DeadLinedTask[] pastTasks(Context context) {
        getNormalTasks(context);
        return pastModels;
    }

    public static PeriodicModel[] dailyTasks(Context context) {
        getPeriodicTasks(context);
        return dailyModels;
    }

    public static PeriodicModel[] weeklyTasks(Context context) {
        getPeriodicTasks(context);
        return weeklyModels;
    }

    public static PeriodicModel[] monthlyTasks(Context context) {
        getPeriodicTasks(context);
        return monthlyModels;
    }

    public static PeriodicModel[] allRoutineTasks(Context context) {
        getPeriodicTasks(context);
        allRoutineModels = new PeriodicModel[dailyModels.length + weeklyModels.length + monthlyModels.length];

        for (int i = 0; i < dailyModels.length + weeklyModels.length + monthlyModels.length; i++) {
            if (i < dailyModels.length) {
                for (PeriodicModel dailyModel : dailyModels) {
                    allRoutineModels[i] = dailyModel;
                }
            } else if (i < dailyModels.length + weeklyModels.length) {
                for (PeriodicModel weeklyModel : weeklyModels) {
                    allRoutineModels[i] = weeklyModel;
                }
            } else {
                for (PeriodicModel monthlyModel : monthlyModels) {
                    allRoutineModels[i] = monthlyModel;
                }
            }
        }


        for (int i = 0; i < dailyTasks.size(); i++) {
            allRoutineModels[i] = dailyTasks.get(i);
        }
        for (int i = dailyTasks.size(); i < weeklyTasks.size(); i++) {
            allRoutineModels[i] = weeklyTasks.get(i);
        }
        for (int i = weeklyTasks.size(); i < monthlyTasks.size(); i++) {
            allRoutineModels[i] = monthlyTasks.get(i);
        }
        return allRoutineModels;
    }

    public static SimpleTask[] simpleTasks(Context context) {
        getSimpleTasks(context);
        return simpleTasksArray;
    }


    // private method
    private static void getNormalTasks(Context context) {
        db = new TaskDB(context);
        today = new ArrayList<>();
        future = new ArrayList<>();
        past = new ArrayList<>();

        Cursor cursor = db.getAllRecords();
        if (cursor.moveToFirst()) {
            Log.d(TAG, "onCreate: fetching tasks from database");

            do {

                CheckBox checkBox = new CheckBox(context);
                checkBox.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                checkBox.setChecked(intToBool(cursor.getInt(cursor.getColumnIndexOrThrow("isdone"))));

                DeadLinedTask taskModel = new DeadLinedTask(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("isdone")),
                        new JalaliDateTime(
                                cursor.getInt(cursor.getColumnIndexOrThrow("deadyear")),
                                cursor.getInt(cursor.getColumnIndexOrThrow("deadmonth")),
                                cursor.getInt(cursor.getColumnIndexOrThrow("deadday")))
                );

                Calendar calendar = Calendar.getInstance();
                calendar.setFirstDayOfWeek(Calendar.SATURDAY);
                JalaliDateTime jalaliDateTime = JalaliDateTime.Now();

                int thisDay = jalaliDateTime.getDay();
                int thisMonth = jalaliDateTime.getMonth();
                int thisYear = jalaliDateTime.getYear();
                Log.d(TAG, "onClick: current date - day: " + today + ", month: " + thisMonth + ", year: " + thisYear);

                int deadDay = taskModel.getDeadDate().getDay();
                int deadMonth = taskModel.getDeadDate().getMonth();
                int deadYear = taskModel.getDeadDate().getYear();
                Log.d(TAG, "onClick: deadline date - day: " + deadDay + ", month: " + deadMonth + ", year: " + deadYear);

                if (!intToBool(taskModel.getIsDone())) {
                    if ((deadDay == thisDay) && (deadMonth == thisMonth) && (deadYear == thisYear)) {
                        today.add(taskModel);
                    } else if ((deadYear > thisYear) || ((deadYear == thisYear) && (deadMonth > thisMonth)) || ((deadYear == thisYear) && (deadMonth == thisMonth) && (deadDay > thisDay))) {
                        future.add(taskModel);
                    } else {
                        past.add(taskModel);
                    }
                }

            } while (cursor.moveToNext());
            Log.d(TAG, "noCreate: tasks loaded from database");

        } else {
            Log.d(TAG, "onCreate: no tasks found in database");
        }
        cursor.close();

        todayModels = new DeadLinedTask[today.size()];
        futureModels = new DeadLinedTask[future.size()];
        pastModels = new DeadLinedTask[past.size()];

        for (int i = 0; i < today.size(); i++) {
            todayModels[i] = today.get(i);
        }
        for (int i = 0; i < future.size(); i++) {
            futureModels[i] = future.get(i);
        }
        for (int i = 0; i < past.size(); i++) {
            pastModels[i] = past.get(i);
        }

    }

    private static void getPeriodicTasks(Context context) {
        routineDB = new RoutineDB(context);
        Log.d(TAG, "onCreate: database initialized");
        dailyTasks = new ArrayList<>();
        weeklyTasks = new ArrayList<>();
        monthlyTasks = new ArrayList<>();

        Cursor cursor = routineDB.getAllRecords(RoutineDB.ROUTINE_TABLE_NAME);
        if (cursor.moveToFirst()) {
            Log.d(TAG, "onCreate: fetching tasks from database");

            do {

                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));

                JalaliDateTime jalaliDateTime = JalaliDateTime.Now();
                Calendar calendar = Calendar.getInstance();
                calendar.setFirstDayOfWeek(Calendar.SATURDAY);

                int day = jalaliDateTime.getDay();
                int week = calendar.get(Calendar.WEEK_OF_YEAR);
                int month = jalaliDateTime.getMonth();
                int year = jalaliDateTime.getYear();

                MultiStateCheckBox checkBox = new MultiStateCheckBox(context);
                checkBox.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));

                String dbPeriod = cursor.getString(cursor.getColumnIndexOrThrow("period"));

                Period period = null;
                switch (dbPeriod) {
                    case "daily":
                        week = 0;
                        checkBox.setState(PeriodicCheckBoxReset.checkDay(id, day, week, month,year, context));
                        period = Period.daily;
                        break;
                    case "weekly":
                        day = 0;
                        month = 0;
                        checkBox.setState(PeriodicCheckBoxReset.checkDay(id, day, week, month,year, context));
                        period = Period.weekly;
                        break;
                    case "monthly":
                        day = 0;
                        week = 0;
                        checkBox.setState(PeriodicCheckBoxReset.checkDay(id, day, week, month,year, context));
                        period = Period.monthly;
                        break;
                    default:
                        break;
                }

                PeriodicModel periodicModel = new PeriodicModel(
                        checkBox,
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        period,
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        day, week, month, year);

                Log.d(TAG, "onCreate: periodicModel created:" +
                        " title: " + periodicModel.getCheckBox().getText() +
                        " descreption: " + periodicModel.getDescription() +
                        " period: " + periodicModel.getPeriod().toString() +
                        " id: " + periodicModel.getId() +
                        " day: " + periodicModel.getChangeDay() +
                        " month: " + periodicModel.getChangeMonth() +
                        " week: " + periodicModel.getChangeWeek() +
                        " year" + periodicModel.getChangeYear());

                if (periodicModel.getCheckBox().getState() == 0) {
                    switch (dbPeriod) {
                        case "daily":
                            dailyTasks.add(periodicModel);
                            break;

                        case "weekly":
                            weeklyTasks.add(periodicModel);
                            break;

                        case "monthly":
                            monthlyTasks.add(periodicModel);
                            break;

                        default:
                            break;
                    }
                }

            } while (cursor.moveToNext());
            Log.d(TAG, "onCreate: tasks loaded from database");
        } else {
            Log.d(TAG, "onCreate: no tasks found in database");
        }
        cursor.close();

        dailyModels = new PeriodicModel[dailyTasks.size()];
        weeklyModels = new PeriodicModel[weeklyTasks.size()];
        monthlyModels = new PeriodicModel[monthlyTasks.size()];

        for (int i = 0; i < dailyTasks.size(); i++) {
            dailyModels[i] = dailyTasks.get(i);

        }
        for (int i = 0; i < weeklyTasks.size(); i++) {
            weeklyModels[i] = weeklyTasks.get(i);

        }
        for (int i = 0; i < monthlyTasks.size(); i++) {
            monthlyModels[i] = monthlyTasks.get(i);

        }
    }

    private static void getSimpleTasks(Context context) {
        simpleDB = new SimpleDB(context);
        Log.d(TAG, "onCreate: database initialized");
        simpleTasksList = new ArrayList<>();


        Cursor cursor = simpleDB.getAllRecords();
        if (cursor.moveToFirst()) {
            Log.d(TAG, "onCreate: fetching tasks from database");

            do {

                SimpleTask simpleModel = new SimpleTask(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("isdone"))
                );

                Log.d(TAG, "onCreate: periodicModel created:" +
                        " title: " + simpleModel.getTitle() +
                        " isDone: " + simpleModel.getIsDone() +
                        " descreption: " + simpleModel.getDescription() +
                        " id: " + simpleModel.getId());

                if (simpleModel.getIsDone() == 0) {
                    simpleTasksList.add(simpleModel);
                }

            } while (cursor.moveToNext());
            Log.d(TAG, "onCreate: tasks loaded from database");
        } else {
            Log.d(TAG, "onCreate: no tasks found in database");
        }
        cursor.close();

        // List to simpleArray
        simpleTasksArray = new SimpleTask[simpleTasksList.size()];
        for (int i = 0; i < simpleTasksList.size(); i++) {
            simpleTasksArray[i] = simpleTasksList.get(i);
        }

    }

}

