package com.yihchou.eweather;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CalendarFragment extends Fragment {

    private Button buttonDay;
    private Button buttonWeek;
    private Button buttonMonth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        buttonDay = view.findViewById(R.id.button_day);
        buttonWeek = view.findViewById(R.id.button_week);
        buttonMonth = view.findViewById(R.id.button_month);

        buttonDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToDayView();
            }
        });

        buttonWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToWeekView();
            }
        });

        buttonMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToMonthView();
            }
        });

        // 默认显示日视图
        switchToDayView();

        return view;
    }

    private void switchToDayView() {
        // 这里加载日视图
        getChildFragmentManager().beginTransaction()
                .replace(R.id.calendar_container, new DayViewFragment())
                .commit();
    }

    private void switchToWeekView() {
        // 这里加载周视图
        getChildFragmentManager().beginTransaction()
                .replace(R.id.calendar_container, new WeekViewFragment())
                .commit();
    }

    private void switchToMonthView() {
        // 这里加载月视图
        getChildFragmentManager().beginTransaction()
                .replace(R.id.calendar_container, new MonthViewFragment())
                .commit();
    }
}

