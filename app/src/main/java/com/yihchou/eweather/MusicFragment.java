package com.yihchou.eweather;


import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;

public class MusicFragment extends Fragment {

    private static final int REQUEST_CODE_PLAYLIST = 2;
    private ImageView btnSongInfo, btnPlaylist, imgAlbumArt;
    private ImageView btnPrevious, btnRewind, btnPlayPause, btnForward, btnNext;
    private TextView tvSongInfo;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private Uri currentSongUri;
    private ArrayList<Uri> songUriList = new ArrayList<>();
    private int currentSongIndex = 0;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        // Initialize views
        btnSongInfo = view.findViewById(R.id.btn_song_info);
        btnPlaylist = view.findViewById(R.id.btn_playlist);
        imgAlbumArt = view.findViewById(R.id.img_album_art);
        tvSongInfo = view.findViewById(R.id.tv_song_info);
        seekBar = view.findViewById(R.id.seekbar);
        btnPrevious = view.findViewById(R.id.btn_previous);
        btnRewind = view.findViewById(R.id.btn_rewind);
        btnPlayPause = view.findViewById(R.id.btn_play_pause);
        btnForward = view.findViewById(R.id.btn_forward);
        btnNext = view.findViewById(R.id.btn_next);

        // Initialize MediaPlayer
        mediaPlayer = new   MediaPlayer();

        // Set click listeners
        btnSongInfo.setOnClickListener(v -> onSongInfoClicked());
        btnPlaylist.setOnClickListener(v -> openPlaylistActivity());
        btnPrevious.setOnClickListener(v -> onPreviousClicked());
        btnRewind.setOnClickListener(v -> onRewindClicked());
        btnPlayPause.setOnClickListener(v -> onPlayPauseClicked());
        btnForward.setOnClickListener(v -> onForwardClicked());
        btnNext.setOnClickListener(v -> onNextClicked());

        // Set up the seekbar listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // handle seekbar start tracking
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // handle seekbar stop tracking
            }
        });

        // Update seekbar progress
        handler.postDelayed(updateSeekBar, 1000);

        return view;
    }

    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
            handler.postDelayed(this, 1000);
        }
    };

    private void onSongInfoClicked() {
        showSongInfoDialog();
    }

    private void showSongInfoDialog() {
        if (currentSongUri == null) {
            Toast.makeText(getContext(), "当前没有正在播放的歌曲", Toast.LENGTH_SHORT).show();
            return;
        }

        String songInfo = getSongInfo(currentSongUri);
        new AlertDialog.Builder(getContext())
                .setTitle("歌曲信息")
                .setMessage(songInfo)
                .setPositiveButton("确定", null)
                .show();
    }

    private String getSongInfo(Uri uri) {
        StringBuilder result = new StringBuilder();
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    if (nameIndex != -1) {
                        String displayName = cursor.getString(nameIndex);
                        result.append("文件名: ").append(displayName).append("\n");
                    }
                    if (sizeIndex != -1) {
                        String size = cursor.getString(sizeIndex);
                        result.append("文件大小: ").append(size).append(" bytes\n");
                    }
                }
            }
        }
        return result.toString();
    }

    private void openPlaylistActivity() {
        Intent intent = new Intent(getActivity(), PlaylistActivity.class);
        intent.putParcelableArrayListExtra("SONG_URIS", songUriList);
        startActivityForResult(intent, REQUEST_CODE_PLAYLIST);
    }

    private void onPreviousClicked() {
        if (currentSongIndex > 0) {
            currentSongIndex--;
            playSong(songUriList.get(currentSongIndex));
        }
    }

    private void onRewindClicked() {
        int newPosition = mediaPlayer.getCurrentPosition() - 15000;
        mediaPlayer.seekTo(Math.max(newPosition, 0));
    }

    private void onPlayPauseClicked() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlayPause.setImageResource(R.drawable.ic_play_pause);
        } else {
            mediaPlayer.start();
            btnPlayPause.setImageResource(R.drawable.ic_play_pause);
        }
    }

    private void onForwardClicked() {
        int newPosition = mediaPlayer.getCurrentPosition() + 15000;
        mediaPlayer.seekTo(Math.min(newPosition, mediaPlayer.getDuration()));
    }

    private void onNextClicked() {
        if (currentSongIndex < songUriList.size() - 1) {
            currentSongIndex++;
            playSong(songUriList.get(currentSongIndex));
        }
    }

    private void playSong(Uri songUri) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getContext(), songUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            currentSongUri = songUri;
            btnPlayPause.setImageResource(R.drawable.ic_play_pause);
            tvSongInfo.setText(getFileName(songUri));
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PLAYLIST && resultCode == AppCompatActivity.RESULT_OK) {
            if (data != null) {
                Uri songUri = Uri.parse(data.getStringExtra("SONG_URI"));
                songUriList = data.getParcelableArrayListExtra("SONG_URIS");
                currentSongIndex = songUriList.indexOf(songUri);
                playSong(songUri);
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBar);
    }
}

