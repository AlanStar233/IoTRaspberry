package com.alanstar.iotraspberry.recycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alanstar.iotraspberry.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder{

    ImageView iv_avatar;
    TextView tv_topic,tv_broker,tv_protocol;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        // 注册组件
        iv_avatar = itemView.findViewById(R.id.iv_avatar);
        tv_topic =  itemView.findViewById(R.id.tv_topic);
        tv_broker = itemView.findViewById(R.id.tv_broker);
        tv_protocol = itemView.findViewById(R.id.tv_protocol);
    }
}
