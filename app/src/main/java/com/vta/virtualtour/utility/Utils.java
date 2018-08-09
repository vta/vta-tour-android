package com.vta.virtualtour.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;

/**
 * Created by tushar on 30-05-2018.
 */

public class Utils {

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    /**
     * Display Dialog
     **/
    public static void showDialog(final Context context, String message) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Alert!");
        builder.setMessage(message);
        builder.setNeutralButton("ok",null);
        builder.show();
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    public static Bitmap getBase64Bitmap(String base64String) {
        String base64Image = base64String.split(",")[1];
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public static boolean isConnectedToWifi(Context context) {
        ConnectivityManager connectivitymanager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] networkInfo = connectivitymanager.getAllNetworkInfo();

        for (NetworkInfo netInfo : networkInfo) {
            if (netInfo.isConnected()) {

                if (netInfo.getTypeName().equalsIgnoreCase("WIFI")) {
                    return true;
                }
            }
        }

        return false;
    }
}
