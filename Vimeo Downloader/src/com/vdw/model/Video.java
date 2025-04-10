package com.vdw.model;

import org.json.JSONObject;

/** Contains a reference to a video container in the JSON object.
 *  Here, the JSON object is the data, all other things are getters
 *  retrieving and formatting information directly from the given {@link JSONObject}.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 1.6 - 10/APR/2025 */
public class Video extends Media {
	
	/** Main constructor setting parameters and internally defining media type. */
	public Video(final JSONObject videoJSON, final int index) {
		super(videoJSON, index, "video");
	}
	
	/************************* Custom Getters Section *************************************/
	
	/** Getter for video bitrate.
	 *  @return Video bitrate in long number format. */
	public long getBitrate() {
		return super.mediaJSON.getLong("avg_bitrate");
	}
	
	/** Getter for video frame rate.
	 *  @return Video frame rate in double number format. */
	public double getFramerate() {
		return super.mediaJSON.getDouble("framerate");
	}
	
	/** Getter for video height.
	 *  @return Video height in integer number format. */
	public int getHeight() {
		return super.mediaJSON.getInt("height");
	}
	
	/** Getter for video width.
	 *  @return Video width in integer number format. */
	public int getWidth() {
		return super.mediaJSON.getInt("width");
	}
	
	/************************* JLabel Getters Section *************************************/
	
	/** Getter for video resolution.
	 *  @return A formatted string containing the video resolution. */
	public String getResolution() {
		return String.format("%dx%d", getWidth(), getHeight());
	}
	
	/** Getter for video bitrate.
	 *  @return A formatted string containing the video bitrate. */
	public String getLabelBitrate() {
		return String.format("%d kb/s", getBitrate()/1000);
	}
	
	/** Getter for video framerate.
	 *  @return A formatted string containing the video framerate. */
	public String getLabelFramerate() {
		return String.format("%.2f fps", getFramerate());
	}
	
	//************************** Override Getters Section *********************************/

	@Override
	public String toString() {
		return String.format("%d: %dp @ %.2f fps [%d chunks]", super.index, getHeight(), getFramerate(), getChunkCount());
	}
	
	@Override
	public String getDialogInfo() {
		return String.format("stream %d [%dp @ %.2f fps]", super.index, getHeight(), getFramerate());
	}
	
}