package com.yihchou.eweather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MonthFragment extends Fragment {

    private ScheduleDatabaseHelper dbHelper;
    private TextView monthTextView;
    private TableLayout monthTable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_month, container, false);

        dbHelper = new ScheduleDatabaseHelper(getContext());
        monthTextView = view.findViewById(R.id.monthTextView);
        monthTable = view.findViewById(R.id.monthTable);

        displayMonthCalendar();

        return view;
    }

    private void displayMonthCalendar() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // 设置当前月份
        monthTextView.setText(monthFormat.format(calendar.getTime()));

        // 获取当前月份的天数
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 默认星期天是第一天，减1调整为星期一是第一天

        // 创建表格行
        int numRows = (daysInMonth + firstDayOfWeek) / 7 + 1;
        for (int row = 0; row < numRows; row++) {
            TableRow tableRow = new TableRow(getContext());
            for (int col = 0; col < 7; col++) {
                int day = row * 7 + col - firstDayOfWeek + 1;
                if (day > 0 && day <= daysInMonth) {
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    String date = dateFormat.format(calendar.getTime());
                    TextView textView = createDayCell(String.valueOf(day), date);
                    tableRow.addView(textView);
                } else {
                    TextView textView = createEmptyCell();
                    tableRow.addView(textView);
                }
            }
            monthTable.addView(tableRow);
        }
    }

    private TextView createDayCell(String text, final String date) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(8, 8, 8, 8);
        textView.setBackgroundResource(R.drawable.border);
        textView.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT)); // 设置长宽为 wrap_content
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setSingleLine(true);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDayScheduleDialog(date);
            }
        });

        return textView;
    }

    private TextView createEmptyCell() {
        TextView textView = new TextView(getContext());
        textView.setText("");
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(8, 8, 8, 8);
        textView.setBackgroundResource(R.drawable.border);
        textView.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT)); // 设置长宽为 wrap_content
        return textView;
    }

    private void showDayScheduleDialog(final String date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Schedule for " + date);

        ScrollView scrollView = new ScrollView(getContext());
        TableLayout scheduleTable = new TableLayout(getContext());
        scrollView.addView(scheduleTable);

        Cursor cursor = dbHelper.getSchedule(date);
        if (cursor != null && cursor.moveToFirst()) {
            for (int i = 0; i < 6; i++) {
                String schedule = cursor.getString(cursor.getColumnIndex("schedule" + (i + 1)));
                TextView textView = createScheduleCell(schedule != null ? schedule : "", date, i + 1);
                scheduleTable.addView(textView);
            }
            cursor.close();
        }

        builder.setView(scrollView);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private TextView createScheduleCell(String text, final String date, final int scheduleIndex) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1f));
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
