package com.minhnhatlpx.simplephdl;
import org.json.*;
import java.util.regex.*;
import java.util.*;
import android.util.*;

public class PornhubParser
{
	public static List<General> getGeneralList(String html) 
	{
		List<General> list = new ArrayList<>();
		
		Pattern p_var;
		Matcher matcher;
		
		String json = null;
		JSONObject flashvars_jsonObject;
		JSONArray qualityItems_jsonArray;
		
	    p_var = Pattern.compile("var\\s+flashvars_\\d+\\s*=\\s*(.+?);");

		matcher = p_var.matcher(html);
		while( matcher.find() )
		{
			json = matcher.group(1);
		}

		try
		{
			flashvars_jsonObject = new JSONObject(json);

			list = OfFlashvars(flashvars_jsonObject);
			
		} catch(JSONException e)
		{
			e.printStackTrace();
		}
		
		Log.i("CaiDitMeMay", String.valueOf(list.size()));
		if(list.size() <= 0)
		{
			p_var = Pattern.compile("var\\s+qualityItems_\\d+\\s*=\\s*(.+?);");

			matcher = p_var.matcher(html);
			while( matcher.find() )
			{
				json = matcher.group(1);
			}

			try
			{
				//Some time json include invalid values got 'Unterminated string at character'
				String json_split = json.replaceAll("(?is)<iframe src=\"(.+?)</iframe>","")
					                    .replaceAll("(?is)<span class=\"(.+?)</span>","");
				
				qualityItems_jsonArray = new JSONArray(json_split);

				list = OfQualityItems(qualityItems_jsonArray);
			} catch(JSONException e)
			{
				e.printStackTrace();
			}
			
		}
			  
		return list;
	}
	
	
	
	public static List<General> OfFlashvars(JSONObject jsonObject)
	{
		List<General> list = new ArrayList<>();
		
		try
		{
			JSONArray mediaDefinitions = jsonObject.getJSONArray("mediaDefinitions");
			//We need MP4 format, not HLS so index 0 is MP4 format and we get it
			JSONObject media_0 = mediaDefinitions.getJSONObject(0);
			
			String md_videoUrl = media_0.getString("videoUrl");
			
			if(md_videoUrl != null || !md_videoUrl.isEmpty())
			{
				String videoUrl_json = HttpRetriever.retrieve(md_videoUrl);
				
				JSONArray videoUrl_jsonArray = new JSONArray(videoUrl_json);
				
				for(int i = 0; i<videoUrl_jsonArray.length(); i++)
				{
					JSONObject videoUrl_jsonObject = videoUrl_jsonArray.getJSONObject(i);
					
					String format = videoUrl_jsonObject.getString("format");
					String videoUrl = videoUrl_jsonObject.getString("videoUrl");
					String quality = videoUrl_jsonObject.getString("quality");
					
					if(!videoUrl.isEmpty())
					{
						list.add(new General(format, videoUrl, quality));
					}
				}
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	public static List<General> OfQualityItems(JSONArray jsonArray)
	{
		List<General> list = new ArrayList<>();
		
		for(int i = 0; i<jsonArray.length(); i++)
		{
			try
			{
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				String text = jsonObject.getString("text");
				String url = jsonObject.getString("url");
				
				if(!url.isEmpty())
				{
					list.add(new General("MP4", url, text));
				}
				
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	
}
