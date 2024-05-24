package com.yihchou.eweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PlaylistActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_SONG = 1;
    private Button btnAddSong, btnClearPlaylist;
    private ListView lvSongs;
    private ArrayList<String> songList;
    private ArrayList<Uri> songUriList;
    private SongAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        btnAddSong = findViewById(R.id.btn_add_song);
        btnClearPlaylist = findViewById(R.id.btn_clear_playlist);
        lvSongs = findViewById(R.id.lv_songs);

        songList = new ArrayList<>();
        songUriList = new ArrayList<>();
        adapter = new SongAdapter(this, songList);
        lvSongs.setAdapter(adapter);

        loadPlaylist();

        btnAddSong.setOnClickListener(v -> openFilePicker());
        btnClearPlaylist.setOnClickListener(v -> clearPlaylist());

        lvSongs.setOnItemClickListener((parent, view, position, id) -> {
            Uri selectedSongUri = songUriList.get(position);
            Intent intent = new Intent();
            intent.putExtra("SONG_URI", selectedSongUri.toString());
            intent.putParcelableArrayListExtra("SONG_URIS", songUriList);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_SONG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_SONG && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                String songName = getFileName(uri);
                songList.add(songName);
                songUriList.add(uri);
                adapter.notifyDataSetChanged();
                savePlaylist();
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
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

    private void savePlaylist() {
        SharedPreferences sharedPreferences = getSharedPreferences("PlaylistPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> songNameSet = new HashSet<>(songList);
        Set<String> songUriSet = new HashSet<>();
        for (Uri uri : songUriList) {
            songUriSet.add(uri.toString());
        }

        editor.putStringSet("SongNames", songNameSet);
        editor.putStringSet("SongUris", songUriSet);
        editor.apply();
    }

    private void loadPlaylist() {
        SharedPreferences sharedPreferences = getSharedPreferences("PlaylistPrefs", Context.MODE_PRIVATE);

        Set<String> songNameSet = sharedPreferences.getStringSet("SongNames", new HashSet<>());
        Set<String> songUriSet = sharedPreferences.getStringSet("SongUris", new HashSet<>());

        songList.clear();
        songUriList.clear();

        if (songNameSet != null) {
            songList.addAll(songNameSet);
        }
        if (songUriSet != null) {
            for (String uriString : songUriSet) {
                songUriList.add(Uri.parse(uriString));
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void clearPlaylist() {
        songList.clear();
        songUriList.clear();
        adapter.notifyDataSetChanged();
        savePlaylist();
    }
}