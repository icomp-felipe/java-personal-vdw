package com.vdw.model;

import org.json.JSONObject;

public class Video extends Media {
	
	public Video(final JSONObject videoJSON, final int index) {
		super(videoJSON,index,"video");
	}
	
	public int getHeight() {
		return super.mediaJSON.getInt("height");
	}
	
	public int getWidth() {
		return super.mediaJSON.getInt("width");
	}
	
	public double getFramerate() {
		return super.mediaJSON.getDouble("framerate");
	}
	
	public long getBitrate() {
		return super.mediaJSON.getLong("avg_bitrate");
	}
	

	@Override
	public String getComboInfo() {
		return String.format("%d: %dp @ %.2f fps [%d chunks]",index,getHeight(),getFramerate(),getChunkCount());
	}
	
	public String getResolution() {
		return String.format("%dx%d", getWidth(), getHeight());
	}
	
	public String getLabelBitrate() {
		return String.format("%d kb/s", getBitrate()/1000);
	}
	
	public String getLabelFramerate() {
		return String.format("%.2f fps", getFramerate());
	}

	@Override
	public String getDialogInfo() {
		return String.format("stream %d [%dp @ %.2f fps]",index,getHeight(),getFramerate());
	}

}
