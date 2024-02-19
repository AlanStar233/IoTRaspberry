package com.alanstar.iotraspberry.recycler;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alanstar.iotraspberry.R;

import java.util.List;

public class SubscribeAdapter extends RecyclerView.Adapter<RecyclerViewHolder>{

    // 定义变量
    Context context;
    List<SubscribeItem> subscribeItems;

    // 构造器


    public SubscribeAdapter(Context context, List<SubscribeItem> subscribeItems) {
        this.context = context;
        this.subscribeItems = subscribeItems;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.subscribe_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        holder.iv_avatar.setImageResource(subscribeItems.get(position).getImage());
        holder.tv_topic.setText(subscribeItems.get(position).getTopic());
        holder.tv_broker.setText(subscribeItems.get(position).getBroker());

        switch (subscribeItems.get(position).getProtocol()) {
            case "mqtt" ->
                    holder.tv_protocol.setBackgroundColor(Color.parseColor("#2BCFB8"));
            case "mqtts" ->
                    holder.tv_protocol.setBackgroundColor(Color.parseColor("#05BCA2"));
            case "ws" ->
                    holder.tv_protocol.setBackgroundColor(Color.parseColor("#3E99D2"));
            case "wss" ->
                    holder.tv_protocol.setBackgroundColor(Color.parseColor("#2488C6"));
            default ->
                    holder.tv_protocol.setBackgroundColor(Color.parseColor("#F15C58"));
        }
        holder.tv_protocol.setText(subscribeItems.get(position).getProtocol());
    }

    @Override
    public int getItemCount() {
        return subscribeItems.size();
    }
}
