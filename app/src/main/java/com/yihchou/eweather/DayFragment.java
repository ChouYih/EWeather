package com.yihchou.eweather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DayFragment extends Fragment {

    private ScheduleDatabaseHelper dbHelper;
    private TextView dateTextView;
    private TextView[] scheduleTextViews = new TextView[6];
    private String currentDate = "2024-05-25"; // 获取当前日期，可以使用Date类

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, container, false);

        dateTextView = view.findViewById(R.id.dateTextView);
        scheduleTextViews[0] = view.findViewById(R.id.schedule1TextView);
        scheduleTextViews[1] = view.findViewById(R.id.schedule2TextView);
        scheduleTextViews[2] = view.findViewById(R.id.schedule3TextView);
        scheduleTextViews[3] = view.findViewById(R.id.schedule4TextView);
        scheduleTextViews[4] = view.findViewById(R.id.schedule5TextView);
        scheduleTextViews[5] = view.findViewById(R.id.schedule6TextView);

        dbHelper = new ScheduleDatabaseHelper(getContext());

        // 获取并显示当天的日程信息
        displaySchedule(currentDate);

        for (int i = 0; i < scheduleTextViews.length; i++) {
            final int index = i;
            scheduleTextViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditDialog(index);
                }
            });
        }

        return view;
    }

    private void displaySchedule(String date) {
        if (!dbHelper.scheduleExists(date)) {
            dbHelper.insertSchedule(date, "", "", "", "", "", "");
        }

        Cursor cursor = dbHelper.getSchedule(date);
        if (cursor != null && cursor.moveToFirst()) {
            String[] schedules = new String[6];
            schedules[0] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE1));
            schedules[1] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE2));
            schedules[2] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE3));
            schedules[3] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE4));
            schedules[4] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE5));
            schedules[5] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE6));

            dateTextView.setText(date);
            for (int i = 0; i < schedules.length; i++) {
                scheduleTextViews[i].setText(schedules[i] != null ? schedules[i] : "");
            }
        } else {
            Log.e("DayFragment", "Failed to load schedule from database.");
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    private void showEditDialog(final int scheduleIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Schedule");

        final EditText input = new EditText(getContext());
        input.setText(scheduleTextViews[scheduleIndex].getText());
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newSchedule = input.getText().toString();
                scheduleTextViews[scheduleIndex].setText(newSchedule);
                updateScheduleInDatabase(scheduleIndex, newSchedule);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void updateScheduleInDatabase(int scheduleIndex, String newSchedule) {
        Cursor cursor = dbHelper.getSchedule(currentDate);
        if (cursor != null && cursor.moveToFirst()) {
            String[] schedules = new String[6];
            schedules[0] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE1));
            schedules[1] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE2));
            schedules[2] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE3));
            schedules[3] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE4));
            schedules[4] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE5));
            schedules[5] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE6));
            schedules[scheduleIndex] = newSchedule;

            dbHelper.updateSchedule(currentDate, schedules[0], schedules[1], schedules[2], schedules[3], schedules[4], schedules[5]);
            cursor.close();
            Toast.makeText(getContext(), "Schedule updated", Toast.LENGTH_SHORT).show();
        }
    }
}
