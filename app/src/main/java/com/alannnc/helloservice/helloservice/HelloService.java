package com.alannnc.helloservice.helloservice;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

public class HelloService extends Service {

    private static final String TAG = "HelloService";

    private boolean isRunning  = false;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        final int id = 1;
        final NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Picture Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.ic_notification);


        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run() {

                int incr;
                // Do the "lengthy" operation 20 times
                for (incr = 0; incr <= 100; incr+=5) {
                    // Sets the progress indicator to a max value, the
                    // current completion percentage, and "determinate"
                    // state
                    mBuilder.setProgress(100, incr, false);
                    // Displays the progress bar for the first time.
                    mNotifyManager.notify(id, mBuilder.build());
                    // Sleeps the thread, simulating an operation
                    // that takes time
                    try {
                        // Sleep for 5 seconds
                        Thread.sleep(5*1000);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "sleep failure");
                    }
                }
                // When the loop is finished, updates the notification
                mBuilder.setContentText("Download complete")
                        // Removes the progress bar
                        .setProgress(0,0,false);
                mNotifyManager.notify(id, mBuilder.build());


                DownloaderTask downloaderTask = new DownloaderTask();
                downloaderTask.execute();

                //Stop service once it finishes its task
                stopSelf();
            }
        }).start();

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;

        Log.i(TAG, "Service onDestroy");
    }

    private class DownloaderTask extends AsyncTask<URL, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            Log.i("DEBUG", "Executing pre execute");
        }

        @Override
        protected Bitmap doInBackground(URL... params) {
            Log.i("DEBUG", "Doing background work");
            String imageURL = "http://i.ebayimg.com/images/i/400724539879-0-1/s-l1000.jpg";

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;

        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            Log.i("DEBUG", "Executing post execute");
        }
    }
}