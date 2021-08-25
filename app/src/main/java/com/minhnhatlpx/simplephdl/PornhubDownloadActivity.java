package com.minhnhatlpx.simplephdl;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.content.*;
import org.json.*;
import android.graphics.drawable.*;
import android.graphics.*;
import android.view.*;
import java.util.*;
import android.text.style.*;
import android.text.*;
import android.annotation.*;
import android.*;
import android.content.pm.*;
import android.view.View.*;
import android.provider.*;
import android.net.*;
import java.nio.file.*;


public class PornhubDownloadActivity extends Activity 
{
	private static final int PERMISSION_CODE = 111;
	private Bundle savedInstanceState;
    private String PORNHUB_URL;
	private int NEGATIVE_BUTTON = AlertDialog.BUTTON_NEGATIVE;
	private int ITEM_POSITION;
	private Handler handler;
	
	private ProgressBar progress;
	private ListView listview;
	private AlertDialog.Builder dialogBuilder;
	private AlertDialog dialog;
	private View dialogView;
	
	private List<General> pornhubList = new ArrayList<>();
	private SingleAdapter adapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			if(CheckAndRequestPermission())
			{
				initialz();
			}

		}else
		{
			initialz();
		}
		
    }

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		if(handler != null)
		{
			handler = null;
		}
	
	}

	@Override
	protected void onStop()
	{
	    super.onStop();
		
		if(dialog != null)
		{
			dialog.dismiss();
		}
	}
	
	
	
	
	private void initialz()
	{
		handler = new Handler();

		createDialog();

		if (savedInstanceState == null && Intent.ACTION_SEND.equals(getIntent().getAction())
			&& getIntent().getType() != null && "text/plain".equals(getIntent().getType())) {

            String url = getIntent().getStringExtra(Intent.EXTRA_TEXT);

            if (url != null
				&& (url.contains("pornhub.com/view_video.php?viewkey="))) {

				PORNHUB_URL = url;

				sendRequest(PORNHUB_URL);

            } else {

                MyUtils.showToast(this, "Invalid link !");

				finish();
            }

        } else if (savedInstanceState != null && PORNHUB_URL != null) {

			sendRequest(PORNHUB_URL);

        } else {
			
            finish();
			
        }
	}
	
	//Networking task, like AsyncTask but I hate AsyncTask so I using this :))
	private void sendRequest(final String url)
	{
		new Thread()
		{
			public void run()
			{
				final String html = HttpRetriever.retrieve(url);
				
				pornhubList = PornhubParser.getGeneralList(html);
				
				if(html!=null)
				{
					handler.post(new Runnable()
					{

						@Override
						public void run()
						{
						    listView();
						}
							
						
					});
					
				}else
				{
					handler.post(new Runnable()
					{

						@Override
						public void run()
						{
							MyUtils.showToast(PornhubDownloadActivity.this, "Error !");
							finish();
						}


					});
					
				}
			}
		}.start();
	}
	
	
	private void listView()
	{
		
		if(pornhubList.isEmpty())
		{
			MyUtils.showToast(this, "Cannot found any video URL !");
			finish();
		}else{
			adapter = new SingleAdapter(this, pornhubList);
			listview.setAdapter(adapter);
			listview.setOnItemClickListener(onItemClickListener());

			progress.setVisibility(View.GONE);
			
		}
		
	}
	
	private void createDialog()
	{
		dialogView = getLayoutInflater().inflate(R.layout.dialog_layout, null);
		progress = dialogView.findViewById(R.id.dialog_progressBar);
		listview = dialogView.findViewById(R.id.dialog_listView);

		String dialogTitle = getResources().getString(R.string.app_name);

		ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.colorAccent));
		SpannableStringBuilder ssBuilder = new SpannableStringBuilder(dialogTitle);
    	ssBuilder.setSpan(
			foregroundColorSpan,
			0,
			dialogTitle.length(),
			Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
		);
		
		dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
		dialogBuilder.setTitle(ssBuilder);
		dialogBuilder.setView(dialogView);
		dialogBuilder.setCancelable(false);
	    dialogBuilder.setPositiveButton("Cancel",new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialogInterface, int position)
				{
					dialogInterface.dismiss();
					finish();
				}


			}).setNegativeButton("Download", new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialogInterface, int position)
				{
					Download();
					dialogInterface.dismiss();
					finish();
				}


			});
		
		dialog = dialogBuilder.create();
		dialog.show();
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff202020")));
		dialog.getButton(NEGATIVE_BUTTON).setEnabled(false);
		
	}	
	private AdapterView.OnItemClickListener onItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                ITEM_POSITION = position;
				dialog.getButton(NEGATIVE_BUTTON).setEnabled(true);
				adapter.setSelectedIndex(position);
				adapter.notifyDataSetChanged();
				
            }
        };
    }

	
	private void Download()
	{
		String url = pornhubList.get(ITEM_POSITION).getVideoUrl();
	
		Uri uri = Uri.parse(url);
	
		String fileName = Paths.get(uri.getPath()).getFileName().toString();
		
		MyUtils.downloadFile(this, url, fileName);
		
		MyUtils.showToast(this, "Download has started.");
	}
	
	public boolean CheckAndRequestPermission()
	{
		String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
		
		if(checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED)
		{
			requestPermissions(new String[]{permission}, PERMISSION_CODE);
			
			return false;
		}else
		if
		(checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
		{
			return true;
		}
		
		return false;
    }

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(requestCode == PERMISSION_CODE)
		{
			if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
			{
				if(shouldShowRequestPermissionRationale(permissions[0]))
				{
					dialogPerm("", "This app need Storage Permission to downloading files.",
						"Grant", new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialogInterface, int i)
							{
								dialogInterface.dismiss();
								CheckAndRequestPermission();
							}


						},"Deny/Exit", new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialogInterface, int i)
							{
								dialogInterface.dismiss();
								finish();
							}


						},false);
				}
				else
				{
					dialogPerm("","You have denied permission, Allow permission at Application settings > Permissions",
						"Go to settings",
						new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialogInterface, int i)
							{
								dialogInterface.dismiss();

								Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
								finish();
							}


						},
						"Deny/Exit",
						new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialogInterface, int i)
							{
								dialogInterface.dismiss();
								finish();
							}


						}, false);

					
				}
			}else{
				initialz();
			}
			
		}
	}
	
	private AlertDialog dialogPerm(String title, String msg, String posiviteLabel, DialogInterface.OnClickListener positiveClick,
	                            String negativeLabel, DialogInterface.OnClickListener negativeClick,
								boolean isCancelAble)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this,android.R.style.Theme_Material_Light_Dialog)
		.setTitle(title)
		.setMessage(msg)
		.setCancelable(isCancelAble)
		.setPositiveButton(posiviteLabel, positiveClick)
		.setNegativeButton(negativeLabel, negativeClick);
		
		AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}
	
	
}
