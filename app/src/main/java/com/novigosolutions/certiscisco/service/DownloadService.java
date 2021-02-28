package com.novigosolutions.certiscisco.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.novigosolutions.certiscisco.activities.SplashActivity;
import com.novigosolutions.certiscisco.objects.Download;
import com.novigosolutions.certiscisco.webservices.CertisCISCOServer;
import com.novigosolutions.certiscisco.webservices.CertisCISCOServices;
import com.novigosolutions.certiscisco.webservices.UnsafeOkHttpClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by dhanrajk on 07-02-18.
 */

public class DownloadService extends IntentService {

    public DownloadService() {
        super("Download Service");
    }

    private int totalFileSize;

    @Override
    protected void onHandleIntent(Intent intent) {
        initDownload();
    }

    private void initDownload() {
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        httpClient.writeTimeout(30, TimeUnit.SECONDS);
//        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CertisCISCOServer.getIP(getApplicationContext()))
                .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                .build();
        CertisCISCOServices retrofitInterface = retrofit.create(CertisCISCOServices.class);

        Call<ResponseBody> request = retrofitInterface.downloadFile();
        try {
            downloadFile(request.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
            Download download = new Download();
            download.setProgress(-5);
            sendIntent(download);
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile(ResponseBody body) {
        try {
            int count;
            byte data[] = new byte[1024 * 4];
            long fileSize = body.contentLength();
            InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
            Log.e("path",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/CERTIS_CISCO");
            File folder=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/CERTIS_CISCO");
            if(!folder.exists()) folder.mkdirs();
            File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/CERTIS_CISCO", "ATM.apk");
            OutputStream output = new FileOutputStream(outputFile);
            long total = 0;
            long startTime = System.currentTimeMillis();
            int timeCount = 1;
            while ((count = bis.read(data)) != -1) {

                total += count;
                totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
                double current = Math.round(total / (Math.pow(1024, 2)));

                int progress = (int) ((total * 100) / fileSize);

                long currentTime = System.currentTimeMillis() - startTime;

                Download download = new Download();
                download.setTotalFileSize(totalFileSize);

                if (currentTime > 1000 * timeCount) {

                    download.setCurrentFileSize((int) current);
                    download.setProgress(progress);
                    sendIntent(download);
                    timeCount++;
                }

                output.write(data, 0, count);
            }
            onDownloadComplete();
            output.flush();
            output.close();
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
            Download download = new Download();
            download.setProgress(-5);
            sendIntent(download);

        }

    }

    private void sendIntent(Download download) {

        Intent intent = new Intent(SplashActivity.DOWNLOAD_PROGRESS);
        intent.putExtra("download", download);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete() {

        Download download = new Download();
        download.setProgress(100);
        sendIntent(download);
    }
}