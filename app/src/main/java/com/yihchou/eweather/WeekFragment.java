package com.yihchou.eweather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class WeekFragment extends Fragment {

    private ScheduleDatabaseHelper dbHelper;
    private TableLayout weekTable;
    private String[] weekDates = new String[7];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week, container, false);

        dbHelper = new ScheduleDatabaseHelper(getContext());
        weekTable = view.findViewById(R.id.weekTable);

        displayWeekSchedule();

        return view;
    }

    private void displayWeekSchedule() {
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // 获取本周的日期
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        for (int i = 0; i < 7; i++) {
            weekDates[i] = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }

        // 创建表头行
        TableRow headerRow = new TableRow(getContext());
        for (String day : daysOfWeek) {
            TextView textView = createHeaderCell(day);
            textView.setBackgroundColor(Color.LTGRAY);
            headerRow.addView(textView);
        }
        weekTable.addView(headerRow);

        // 创建表格内容行
        for (int i = 0; i < 6; i++) { // 每天最多6条日程
            TableRow tableRow = new TableRow(getContext());
            for (int j = 0; j < 7; j++) { // 一周7天
                String date = weekDates[j];

                if (!dbHelper.scheduleExists(date)) {
                    dbHelper.insertSchedule(date, "", "", "", "", "", "");
                }

                Cursor cursor = dbHelper.getSchedule(date);
                if (cursor != null && cursor.moveToFirst()) {
                    String schedule = cursor.getString(cursor.getColumnIndex("schedule" + (i + 1)));
                    TextView textView = createCell(schedule != null ? schedule : "", date, i + 1);
                    tableRow.addView(textView);
                    cursor.close();
                }
            }
            weekTable.addView(tableRow);
        }
    }

    private TextView createHeaderCell(String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(8, 8, 8, 8);
        textView.setBackgroundResource(R.drawable.border);
        textView.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1f));
        return textView;
    }

    private TextView createCell(String text, final String date, final int scheduleIndex) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(8, 8, 8, 8);
        textView.setBackgroundResource(R.drawable.border);
        textView.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                100, 1f)); // 设置高度为100dp
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setSingleLine(true);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(date, scheduleIndex, (TextView) v);
            }
        });

        return textView;
    }

    private void showEditDialog(final String date, final int scheduleIndex, final TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Schedule");

        final EditText input = new EditText(getContext());
        input.setText(textView.getText());
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newSchedule = input.getText().toString();
                textView.setText(newSchedule);
                updateScheduleInDatabase(date, scheduleIndex, newSchedule);
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

    private void updateScheduleInDatabase(String date, int scheduleIndex, String newSchedule) {
        Cursor cursor = dbHelper.getSchedule(date);
        if (cursor != null && cursor.moveToFirst()) {
            String[] schedules = new String[6];
            schedules[0] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE1));
            schedules[1] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE2));
            schedules[2] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE3));
            schedules[3] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE4));
            schedules[4] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE5));
            schedules[5] = cursor.getString(cursor.getColumnIndex(ScheduleDatabaseHelper.COLUMN_SCHEDULE6));
            schedules[scheduleIndex - 1] = newSchedule;

            dbHelper.updateSchedule(date, schedules[0], schedules[1], schedules[2], schedules[3], schedules[4], schedules[5]);
            cursor.close();
        }
    }
}
