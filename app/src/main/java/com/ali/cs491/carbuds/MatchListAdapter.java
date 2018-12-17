package com.ali.cs491.carbuds;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MatchListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<ChatListUser> chatListUsers;

    public MatchListAdapter(Activity activity, List<ChatListUser> chatListUsers) {
        this.mInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        this.chatListUsers = chatListUsers;
    }

    @Override
    public int getCount() {
        return chatListUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return chatListUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;

        rowView = mInflater.inflate(R.layout.row_layout, null);
        TextView textView =
                (TextView) rowView.findViewById(R.id.name);
        ImageView imageView =
                (ImageView) rowView.findViewById(R.id.profile_pic);

        ChatListUser user = chatListUsers.get(position);

        textView.setText(user.getName());

        imageView.setImageResource(R.drawable.green);

        return rowView;
    }

}
