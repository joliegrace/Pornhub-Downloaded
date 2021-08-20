package com.minhnhatlpx.simplephdl;
import org.json.*;
import java.util.regex.*;
import java.util.*;

public class PornhubParser
{
	public static JSONArray getJsonArray(String html)
	{
		JSONArray jsonArray = null;
		String vid = null;
		String json = "[]";

		Pattern p_vid = Pattern.compile("(?is)vid=(.+?)&");
		Matcher matcher = p_vid.matcher(html); 
		
		while( matcher.find() )
		{
			vid = matcher.group(1);
		}
		
		Pattern p_vqualityItems = Pattern.compile("(?is)var qualityItems_"+vid+" = (.+?);");
		matcher = p_vqualityItems.matcher(html);
		while( matcher.find() )
		{
			json = matcher.group(1);
		}
		
		try
		{
			jsonArray = new JSONArray(json);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return jsonArray;
	}
	
	public static List<Pornhub>  getList(JSONArray jsonArray)
	{
		List<Pornhub> list = new ArrayList<>();
		
		for(int i = 0 ; i<jsonArray.length() ; i++)
		{
			try
			{
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				String text = jsonObject.getString("text");
				
				String url = jsonObject.getString("url");
				
				list.add(new Pornhub(text,url));
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	
}
