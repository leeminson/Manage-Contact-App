package com.g1.contactapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AppointmentManager {
    public void scheduleNotification(Context context, long appointmentTimeMillis) {
        // Thời gian hiện tại
        long currentTimeMillis = System.currentTimeMillis();

        // Thời gian cần đặt báo thức
        long alarmTimeMillis = appointmentTimeMillis - (30 * 60 * 1000); // 30 phút trước

        // Kiểm tra nếu thời gian đã qua
        if (alarmTimeMillis <= currentTimeMillis) {
            return;
        }

        // Tạo một Intent để gửi tới BroadcastReceiver
        Intent notificationIntent = new Intent(context, AppointmentNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Đặt báo thức bằng AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
    }
}
