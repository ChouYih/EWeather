package com.yihchou.eweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> songList;
    private LayoutInflater inflater;

    public SongAdapter(Context context, ArrayList<String> songList) {
        this.context = context;
        this.songList = songList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Object getItem(int position) {
        return songList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_song, parent, false);
            holder = new ViewHolder();
            holder.tvSongName = convertView.findViewById(R.id.tv_song_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String songName = songList.get(position);
        holder.tvSongName.setText(songName);

        return convertView;
    }

    static class ViewHolder {
        TextView tvSongName;
    }
}
