package com.g1.contactapp.utils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.g1.contactapp.R;

public class AppointmentNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // Tạo thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Lịch hẹn sắp đến")
                .setContentText("Bạn có một cuộc hẹn sắp đến trong 30 phút.")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Hiển thị thông báo
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}
