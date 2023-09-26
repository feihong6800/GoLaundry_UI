package com.example.wanjing.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wanjing.Interface.ItemClickListner;
import com.example.wanjing.R;

public class NotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView notiTitle, notiContent, notiDate;
    public ItemClickListner listner;

    public NotViewHolder(@NonNull View itemView) {
        super(itemView);

        notiTitle = (TextView) itemView.findViewById(R.id.tv_notification);
        notiContent = (TextView) itemView.findViewById(R.id.tv_notification_details);
        notiDate = (TextView) itemView.findViewById(R.id.tv_notification_date);

    }

    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(), false);
    }
}

