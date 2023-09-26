package com.example.wanjing.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wanjing.Interface.ItemClickListner;
import com.example.wanjing.R;

public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView notificationTitle, notificationContent, notificationDate;
    public ImageView img_icon;
    public CardView notificationLayout;
    public ItemClickListner listner;

    public NotificationViewHolder(@NonNull View itemView) {
        super(itemView);

        notificationTitle = (TextView) itemView.findViewById(R.id.tv_notification);
        notificationContent = (TextView) itemView.findViewById(R.id.tv_notification_details);
        notificationDate = (TextView) itemView.findViewById(R.id.tv_notification_date);
        img_icon = (ImageView) itemView.findViewById(R.id.iv_notification_icon);
        notificationLayout = (CardView) itemView.findViewById(R.id.notification_layout);

    }

    public void setItemClickListner(ItemClickListner listner) {
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(), false);
    }
}
