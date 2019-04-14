package com.ricardorainha.mustache.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.model.Appointment;
import com.ricardorainha.mustache.model.Barbershop;
import com.ricardorainha.mustache.model.Session;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import androidx.core.app.NotificationCompat;

public class AppointmentsUtils {

    public static void schedule(Calendar date, Barbershop barbershop) {
        Appointment appointment = new Appointment(date.getTimeInMillis(), System.currentTimeMillis(), barbershop, false, false);
        Session.getInstance().getUser().getValue().addAppointment(appointment);
        FirebaseUtils.updateUserInfo();
    }

    public static void confirm(String time) {
        Session.getInstance().getUser().getValue().confirmAppointment(time);
        FirebaseUtils.updateUserInfo();
    }

    public static void cancel(Appointment appointment) {
        Session.getInstance().getUser().getValue().cancelAppointment(appointment.getTimeString());
        FirebaseUtils.updateUserInfo();
    }

    public static void scheduleConfirmationNotification(Context context, Barbershop barbershop, Calendar date) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(context.getString(R.string.notification_channel_id),
                    context.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(context.getString(R.string.notification_channel_description));
            context.getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
                        .setSmallIcon(R.drawable.ic_mustache_white_24)
                        .setContentTitle(context.getString(R.string.notification_confirmation_title))
                        .setContentText(context.getString(R.string.notification_confirmation_text, barbershop.getName()))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_confirmation_text, barbershop.getName())))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

                context.getSystemService(NotificationManager.class).notify(753, builder.build());

                confirm(String.valueOf(date.getTimeInMillis()));
            }
        }, 60000);
    }

}
