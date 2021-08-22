package com.minhnhatlpx.simplephdl;

public class General
{
	private String format;
	private String videoUrl;
	private String quality;

	public General(String format, String videoUrl, String quality)
	{
		this.format = format;
		this.videoUrl = videoUrl;
		this.quality = quality;
	}

	
	public String getFormat()
	{
		return format;
	}

	
	public String getVideoUrl()
	{
		return videoUrl;
	}

	
	public String getQuality()
	{
		return quality;
	}

	
}

	
