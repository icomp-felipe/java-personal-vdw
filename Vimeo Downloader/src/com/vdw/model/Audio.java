package com.vdw.model;

import org.json.JSONObject;

/** Contains a reference to an audio container in the JSON object.
 *  Here, the JSON object is the data, all other things are getters
 *  retrieving and formatting information directly from the given {@link JSONObject}.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 1.5 - 29/05/2020 */
public class Audio extends Media {
	
	/** Main constructor setting parameters and internally defining media type. */
	public Audio(final JSONObject audioJSON, final int index) {
		super(audioJSON, index, "audio");
	}
	
	/************************* Custom Getters Section *************************************/
	
	/** Getter for bitrate.
	 *  @return Audio bitrate in long number format. */
	public long getBitrate() {
		return super.mediaJSON.getLong("bitrate");
	}
	
	/** Getter for audio channels.
	 * @return Audio channels in double number format. */
	public double getChannels() {
		return super.mediaJSON.getDouble("channels");
	}
	
	/** Getter for audio sample rate.
	 * @return Audio sample rate in integer number format. */
	public int getSampleRate() {
		return super.mediaJSON.getInt("sample_rate");
	}
	
	/************************* JLabel Getters Section *************************************/
	
	/** Getter for audio bitrate.
	 *  @return A formatted string containing the audio bitrate. */
	public String getLabelBitrate() {
		return String.format("%d kb/s", getBitrate()/1000);
	}
	
	/** Getter for audio channels.
	 *  @return A formatted string containing the audio channels. */
	public String getLabelChannels() {
		return String.format("%.1f ch", getChannels());
	}
	
	/** Getter for audio sample rate.
	 *  @return A formatted string containing the audio sample rate. */
	public String getLabelSamplerate() {
		return String.format("%d Hz", getSampleRate());
	}
	
	//************************** Override Getters Section *********************************/

	@Override
	public String getComboInfo() {
		return String.format("%d: %d kb/s @ %.1fch [%d chunks]", super.index, getBitrate()/1000, getChannels(), getChunkCount());
	}
	
	@Override
	public String getDialogInfo() {
		return String.format("stream %d [%d kb/s @ %.1fch]", super.index, getBitrate()/1000, getChannels());
	}
	
}