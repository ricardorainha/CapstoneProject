package com.ricardorainha.mustache.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.authentication.AuthManager;
import com.ricardorainha.mustache.model.Appointment;
import com.ricardorainha.mustache.utils.FirebaseUtils;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NextAppointmentWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        FirebaseUtils.requestUserInfo(AuthManager.getInstance().getUser().getUid(), user -> {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.next_appointment_widget);

            if (user != null) {
                Appointment nextAppointment = user.getNextAppointment();

                if (nextAppointment != null) {
                    Calendar date = Calendar.getInstance();
                    date.setTimeInMillis(nextAppointment.getTime());
                    DateFormat timeFormat = DateFormat.getTimeInstance(java.text.DateFormat.SHORT);

                    views.setTextViewText(R.id.widget_tv_month, date.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()).toUpperCase());
                    views.setTextViewText(R.id.widget_tv_day, String.valueOf(date.get(Calendar.DAY_OF_MONTH)));
                    views.setTextViewText(R.id.widget_tv_barbershop_name, nextAppointment.getBarbershop().getName());
                    views.setTextViewText(R.id.widget_tv_time, timeFormat.format(date.getTime()));
                    views.setTextViewText(R.id.widget_tv_status, context.getString(nextAppointment.isCanceled() ? R.string.status_canceled : nextAppointment.isConfirmed() ? R.string.status_confirmed : R.string.status_pending));
                    views.setTextColor(R.id.widget_tv_status, context.getColor(nextAppointment.isCanceled() ? android.R.color.holo_red_dark : nextAppointment.isConfirmed() ? android.R.color.holo_green_dark : android.R.color.black));
                }

                views.setViewVisibility(R.id.ll_appointment, (nextAppointment != null) ? View.VISIBLE : View.GONE);
                views.setViewVisibility(R.id.ll_no_appointment, (nextAppointment != null) ? View.GONE : View.VISIBLE);

                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        });
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}

