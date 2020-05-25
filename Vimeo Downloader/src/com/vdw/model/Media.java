package com.vdw.model;

import java.io.File;
import java.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import com.phill.libs.PhillFileUtils;
import com.phill.libs.TimeUtils;

public abstract class Media {
	
	protected final int index;
	protected final JSONObject mediaJSON;
	private final String mediaType;
	
	protected Media(final JSONObject mediaJSON, final int index, final String mediaType) {
		this.index = index;
		this.mediaJSON = mediaJSON;
		this.mediaType = mediaType;
	}

	public String getMediaType() {
		return this.mediaType;
	}
	
	public long getDuration() {
		return this.mediaJSON.getLong("duration");
	}
	
	public String getLabelDuration() {
		return TimeUtils.getFormattedTime(getDuration());
	}
	
	public String getLabelSize() {
		return PhillFileUtils.humanReadableByteCount(getMediaSize());
	}
	
	protected int getChunkCount() {
		return this.mediaJSON.getJSONArray("segments").length();
	}
	
	public long getMediaSize() {
		
		long size;
		
		JSONArray segments = mediaJSON.getJSONArray("segments");
		String header = mediaJSON.getString("init_segment");
		
		size = Base64.getDecoder().decode(header).length;
		
		for (int i=0; i<segments.length(); i++)
			size += segments.getJSONObject(i).getLong("size");
		
		return size;
	}
	
	public File getTempFile() {
		
		File output = null;
		final String tempDir = System.getProperty("java.io.tmpdir");
		
		try {
			
			final String filename = String.format("%s/%s.tmp",tempDir,this.mediaType);
			output = new File(filename);
			
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return output;
	}
	
	public abstract String getComboInfo();
	public abstract String getDialogInfo();

	public byte[] getInitSegment() {
		return Base64.getDecoder().decode(mediaJSON.getString("init_segment"));
	}

	public JSONArray getSegments() {
		return mediaJSON.getJSONArray("segments");
	}

	public String getBaseURL() {
		return mediaJSON.getString("base_url");
	}
	
}
