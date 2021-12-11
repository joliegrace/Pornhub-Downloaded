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
		//JSONArray qualityItems_jsonArray;
		
		p_var = Pattern.compile("var\\s+flashvars_\\d+\\s*=\\s*(.+?)\\};"); 
		matcher = p_var.matcher(html);
		while( matcher.find() )
		{
			json = matcher.group(1);
		}

		try
		{
			//Sometime json include invalid values got 'Unterminated string at character'
			json = MyUtils.removeBackSlash(json);
			
			String json_split = json.replaceAll("(?is)<iframe src=\"(.+?)</iframe>","")
				                    .replaceAll("(?is)<span class=\"(.+?)</span>","")
                                    .replaceAll("(?is)<a href=\"(.+?)</a>","") + "}";
			
			flashvars_jsonObject = new JSONObject(json_split);

			list = OfFlashvars(flashvars_jsonObject);
			
		} catch(JSONException e)
		{
			e.printStackTrace();
		}
		//Nothing var qualityItems anymore :)
//		if(list.isEmpty())
//		{
//			p_var = Pattern.compile("var\\s+qualityItems_\\d+\\s*=\\s*(.+?);");
//			
//			matcher = p_var.matcher(html);
//			while( matcher.find() )
//			{
//				json = matcher.group(1);
//			}
//
//			try
//			{
//				qualityItems_jsonArray = new JSONArray(json);
//
//				list = OfQualityItems(qualityItems_jsonArray);
//			} catch(JSONException e)
//			{
//				e.printStackTrace();
//			}
//			
//		}
			  
		return list;
	}
	
	
	
	public static List<General> OfFlashvars(JSONObject jsonObject)
	{
		List<General> list = new ArrayList<>();
		
		try
		{
			JSONArray mediaDefinitions = jsonObject.getJSONArray("mediaDefinitions");
			String md_videoUrl = null;
			for(int i = 0; i<mediaDefinitions.length(); i++)
			{
				JSONObject media = mediaDefinitions.getJSONObject(i);
				
				if(media.getString("format").equals("mp4"))
				{
					md_videoUrl = media.getString("videoUrl");
				}
			}
			
			
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
	
	//this is plan B if plan A(method OfFlashvars) failed =))) 
	
	//nothing var qualityItems anymore :)
//	public static List<General> OfQualityItems(JSONArray jsonArray)
//	{
//		List<General> list = new ArrayList<>();
//		
//		for(int i = 0; i<jsonArray.length(); i++)
//		{
//			try
//			{
//				JSONObject jsonObject = jsonArray.getJSONObject(i);
//				
//				String text = jsonObject.getString("text");
//				String url = jsonObject.getString("url");
//				
//				if(!url.isEmpty())
//				{
//					list.add(new General("MP4", url, text));
//				}
//				
//			}
//			catch (JSONException e)
//			{
//				e.printStackTrace();
//			}
//		}
//		
//		return list;
//	}
	
	
}
