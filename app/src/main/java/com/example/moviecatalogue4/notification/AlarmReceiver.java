package com.example.moviecatalogue4.notification;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.moviecatalogue4.BuildConfig;
import com.example.moviecatalogue4.R;
import com.example.moviecatalogue4.model.MovieModel;
import com.example.moviecatalogue4.model.MovieResponse;
import com.example.moviecatalogue4.network.GetDataService;
import com.example.moviecatalogue4.network.RetrofitClientInstance;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String EXTRA_MESSAGE = "message";
    private static int notificationID = 1;

    public AlarmReceiver() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setRepeatingAlarm(Context context, String time, String message, int ID_REPEATING) {
        String TIME_FORMAT = "HH:mm";
        long currentTime = System.currentTimeMillis();
        if (isDateInvalid(time, TIME_FORMAT)) return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(EXTRA_MESSAGE, message);

        String []timeArray = time.split(":");

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
        calendar.set(Calendar.SECOND, 0);

        long alarmMillis = calendar.getTimeInMillis();
        if(currentTime > alarmMillis) alarmMillis += 86400000L;

        // used setExactAndAllowWhileIdle because API 23+ have doze mode
        // repeating alarm will be set again when onReceive gets called
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, 0);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmMillis, pendingIntent);
        }

    }

    public void cancelAlarm(Context context, String alarm) {
        //Cancelling the notification by creating the same pending intent to replace the old one and cancel it immediately
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        if(alarm.equals("daily")) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 101, intent, 0);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        }

        if(alarm.equals("release")) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 102, intent, 0);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }

    public boolean isDateInvalid(String date, String format) {
        try {
            DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
            df.setLenient(false);
            df.parse(date);
            return false;
        } catch (ParseException e) {
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra(EXTRA_MESSAGE);

        if (message != null) {
            PendingIntent pendingIntent;

            if(message.equals("RELEASE")) {
                getReleaseMovies(context);

                pendingIntent = PendingIntent.getBroadcast(context, 102, intent, 0);
            } else {
                showAlarmNotification(context, context.getResources().getString(R.string.app_name), message);

                pendingIntent = PendingIntent.getBroadcast(context, 101, intent, 0);
            }

            //when we setup the alarm again, it is already in the past which will trigger alarm over and over
            //we setup the alarm to next day by adding 86400000 ms (1 day)
            long alarmMillis = System.currentTimeMillis() + 86400000L;

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            //set next alarm clock manager, used setExactAndAllowWhileIdle to handle doze mode on API 23 and up
            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmMillis, pendingIntent);
            }
        }
    }

    private void getReleaseMovies(final Context context) {
        final ArrayList<MovieModel> movieList = new ArrayList<>();
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Call<MovieResponse> call = service.getReleasedTodayMovies(BuildConfig.API_KEY, currentDate, currentDate);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.body() != null) {
                    movieList.addAll(response.body().getResults());
                    sendNotif(movieList, context);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable throwable) {

            }
        });
    }

    private void sendNotif(ArrayList<MovieModel> movieList, Context context) {
        if(movieList.size() > 0) {
            for(MovieModel movie : movieList) {
                showAlarmNotification(context, movie.getTitle(), movie.getTitle() + " has been released today!");
            }
        } else {
            showAlarmNotification(context, context.getResources().getString(R.string.no_movie), context.getResources().getString(R.string.no_movie));
        }
    }

    private void showAlarmNotification(Context context, String title, String message) {
            String CHANNEL_ID = "Channel_1";
            String CHANNEL_NAME = "AlarmManager channel";

            NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_movie)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setContentText(message)
                    .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setSound(alarmSound);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
                builder.setChannelId(CHANNEL_ID);

                if (notificationManagerCompat != null) {
                    notificationManagerCompat.createNotificationChannel(channel);
                }
            }

            Notification notification = builder.build();
            if (notificationManagerCompat != null) {
                notificationManagerCompat.notify(++notificationID, notification);
            }
    }
}
