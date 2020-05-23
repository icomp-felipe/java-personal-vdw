package com.vdw.model;

import org.json.JSONObject;

public class Audio extends Media {
	
	public Audio(final JSONObject audioJSON, final int index) {
		super(audioJSON,index,"audio");
	}
	
	public long getBitrate() {
		return super.mediaJSON.getLong("bitrate");
	}
	
	public double getChannels() {
		return super.mediaJSON.getDouble("channels");
	}
	
	public int getChunkCount() {
		return super.mediaJSON.getJSONArray("segments").length();
	}
	
	public int getSampleRate() {
		return super.mediaJSON.getInt("sample_rate");
	}
	
	@Override
	public String getComboInfo() {
		return String.format("%d: %d kb/s @ %.1fch [%d chunks]",index,getBitrate()/1000,getChannels(),getChunkCount());
	}

	public String getLabelBitrate() {
		return String.format("%d kb/s", getBitrate()/1000);
	}
	
	public String getLabelSamplerate() {
		return String.format("%d Hz", getSampleRate());
	}
	
	public String getLabelChannels() {
		return String.format("%.1f ch", getChannels());
	}
	
}
