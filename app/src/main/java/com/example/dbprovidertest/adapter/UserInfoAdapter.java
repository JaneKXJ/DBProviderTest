package com.example.dbprovidertest.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dbprovidertest.R;
import com.example.dbprovidertest.Utils.LogUtil;
import com.example.dbprovidertest.data.UserInfo;

import java.util.List;


public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.ViewHolder> {
    private static final String TAG = UserInfoAdapter.class.getSimpleName();
    private List<UserInfo> userInfos;

    public UserInfoAdapter(List<UserInfo> userInfos) {
        this.userInfos = userInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.info_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (userInfos != null && position >= userInfos.size()) {
            return;
        }
        LogUtil.i(TAG, "position: " + position + " userInfos.get(position).toString(): " + userInfos.get(position).toString());
        viewHolder.userInfo.setText(userInfos.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return userInfos == null ? 0 : userInfos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userInfo;

        public ViewHolder(View view) {
            super(view);
            userInfo = view.findViewById(R.id.data_info_item);
        }
    }
}
