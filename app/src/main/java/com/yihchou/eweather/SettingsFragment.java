package com.yihchou.eweather;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private TextView textViewUsername;
    private ImageView imageViewAvatar;
    private Button buttonLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        textViewUsername = view.findViewById(R.id.textViewUsername);
        imageViewAvatar = view.findViewById(R.id.imageViewAvatar);
        buttonLogout = view.findViewById(R.id.buttonLogout);

        // 从SharedPreferences中获取当前登录的账号
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "未登录");

        textViewUsername.setText(username);

        // 设置头像（这里假设你有一个默认的头像图片放在drawable文件夹中）
        imageViewAvatar.setImageResource(R.drawable.default_avatar);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 清除SharedPreferences中的账号信息
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // 退出应用
                getActivity().finishAffinity();
            }
        });

        return view;
    }
}
