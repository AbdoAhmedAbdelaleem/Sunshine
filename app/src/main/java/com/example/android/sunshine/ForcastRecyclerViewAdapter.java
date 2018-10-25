package com.example.android.sunshine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ForcastRecyclerViewAdapter extends RecyclerView.Adapter<ForcastRecyclerViewAdapter.ForcastViewHolder> {
    OnRecyclerViewItemClicked onRecyclerViewItemClicked;
    Context context;
    ArrayList<String> dataSource;

    public ForcastRecyclerViewAdapter(Context context, ArrayList<String> dataSource, OnRecyclerViewItemClicked onRecyclerViewItemClicked) {
        this.context = context;
        this.dataSource = dataSource;
        this.onRecyclerViewItemClicked = onRecyclerViewItemClicked;
    }

    @Override
    public ForcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.recycler_view_item, parent, false);
        return new ForcastViewHolder(view);
    }

    public void SetDataSource(ArrayList<String> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onBindViewHolder(ForcastViewHolder holder, int position) {
        holder.OnBind(dataSource.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public class ForcastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;

        public ForcastViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.id_item_textView);
        }

        public void OnBind(String s) {
            int position = getAdapterPosition();
            textView.setText(dataSource.get(position));
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onRecyclerViewItemClicked.OnRecyclerViewItemClicked(getAdapterPosition());
        }
    }

    interface OnRecyclerViewItemClicked {
        public void OnRecyclerViewItemClicked(int position);
    }
}
