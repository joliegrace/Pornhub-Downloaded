package com.minhnhatlpx.simplephdl;
import android.content.*;
import android.widget.*;
import android.app.*;
import java.io.*;
import android.os.*;
import android.net.*;

public class MyUtils
{
	
	public static void showToast(Context context, String str)
	{
		Toast.makeText(context, str , Toast.LENGTH_LONG).show();
	}
	
	public static String removeBackSlash(String str)
	{
		return str.replaceAll("\\","");
	}
	
	public static void downloadFile(Context context,String url, String fileName)
	{
		DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() +"/"+ fileName);
		DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url))
			.setTitle(fileName)
			.setDescription("Downloading")
			.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
			.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
			.setDestinationUri(Uri.fromFile(file))
			.setAllowedOverMetered(true)
			.setAllowedOverRoaming(true);

		downloadManager.enqueue(request);
	}
}
