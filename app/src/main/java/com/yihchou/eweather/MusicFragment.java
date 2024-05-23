package com.yihchou.eweather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicFragment extends Fragment {

    private static final int REQUEST_PERMISSION_STORAGE = 100;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private SeekBar seekBar;
    private TextView textViewCurrentTime, textViewTotalTime, textViewSongTitle;
    private Button buttonPlayPause, buttonNext, buttonPrevious, buttonFastForward, buttonRewind;
    private List<File> songList;
    private int currentSongIndex = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        seekBar = view.findViewById(R.id.seekBar);
        textViewCurrentTime = view.findViewById(R.id.textView_current_time);
        textViewTotalTime = view.findViewById(R.id.textView_total_time);
        textViewSongTitle = view.findViewById(R.id.textView_song_title);
        buttonPlayPause = view.findViewById(R.id.button_play_pause);
        buttonNext = view.findViewById(R.id.button_next);
        buttonPrevious = view.findViewById(R.id.button_previous);
        buttonFastForward = view.findViewById(R.id.button_fast_forward);
        buttonRewind = view.findViewById(R.id.button_rewind);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
        } else {
            loadSongs();
        }

        return view;
    }

    private void loadSongs() {
        songList = getSongsFromExternalStorage();

        if (!songList.isEmpty()) {
            setupMediaPlayer();
            setupButtons();
        } else {
            Toast.makeText(getContext(), "No songs found on the device.", Toast.LENGTH_SHORT).show();
        }
    }

    private List<File> getSongsFromExternalStorage() {
        List<File> songs = new ArrayList<>();
        File musicDir = new File("/storage/emulated/0/Music"); // 确保路径正确
        if (musicDir.exists() && musicDir.isDirectory()) {
            File[] files = musicDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".mp3")) {
                        songs.add(file);
                    }
                }
            }
        }
        return songs;
    }

    private void setupMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        playSong(songList.get(currentSongIndex));

        mediaPlayer.setOnCompletionListener(mp -> {
            if (currentSongIndex < songList.size() - 1) {
                currentSongIndex++;
                playSong(songList.get(currentSongIndex));
            } else {
                mediaPlayer.seekTo(0);
                mediaPlayer.pause();
                buttonPlayPause.setText("Play");
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    textViewCurrentTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void setupButtons() {
        buttonPlayPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                buttonPlayPause.setText("Play");
            } else {
                mediaPlayer.start();
                buttonPlayPause.setText("Pause");
            }
        });

        buttonNext.setOnClickListener(v -> {
            if (currentSongIndex < songList.size() - 1) {
                currentSongIndex++;
                playSong(songList.get(currentSongIndex));
            }
        });

        buttonPrevious.setOnClickListener(v -> {
            if (currentSongIndex > 0) {
                currentSongIndex--;
                playSong(songList.get(currentSongIndex));
            }
        });

        buttonFastForward.setOnClickListener(v -> mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000));

        buttonRewind.setOnClickListener(v -> mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000));
    }

    private void playSong(File song) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(song.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();

            seekBar.setMax(mediaPlayer.getDuration());
            textViewTotalTime.setText(formatTime(mediaPlayer.getDuration()));
            textViewSongTitle.setText(song.getName());

            buttonPlayPause.setText("Pause");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            } else {
                Toast.makeText(getContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
