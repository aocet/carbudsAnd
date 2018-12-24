package com.ali.cs491.carbuds.Messages;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ali.cs491.carbuds.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

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

        Glide.with(imageView)
                .load("http://35.205.45.78/get_user_image?user_image_id="+user.getId())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .into(imageView);

        return rowView;
    }

}
