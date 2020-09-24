package com.vdw.model;

import org.json.*;

import java.io.*;
import java.net.*;
import java.util.*;

import com.phill.libs.files.PhillFileUtils;
import com.phill.libs.time.PhillsDateUtils;
import com.phill.libs.time.PhillsDateParser;

/** Contains a reference to an media container in the JSON object.
 *  It's also the super class extended by {@link Video} and {@link Audio} objects.
 *  Here, the JSON object is the data, all other things are getters
 *  retrieving and formatting information directly from the given {@link JSONObject}.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 1.5 - 29/05/2020 */
public abstract class Media {
	
	protected final int index;
	protected final JSONObject mediaJSON;
	private final String mediaType;
	private File output;
	
	/** Main constructor setting parameters and defining media type. */
	protected Media(final JSONObject mediaJSON, final int index, final String mediaType) {
		this.index = index;
		this.mediaJSON = mediaJSON;
		this.mediaType = mediaType;
	}

	/************************* Custom Getters Section *************************************/
	
	/** Getter for the media base URL (RAW).
	 *  @return A string containing the media base URL. */
	public String getBaseURL() {
		return mediaJSON.getString("base_url");
	}
	
	/** Getter for the media base URL.
	 *  @return An {@link URL} containing the media base URL created using the given {@link JSONObject}.
	 *  @throws MalformedURLException when the URL could not be properly created. */
	public URL getBaseURL(JSONObject json) throws MalformedURLException {
		
		URL baseURL = new URL(json.getString("media_base_url"));
		
		return new URL(baseURL,getBaseURL());
	}
	
	/** Getter for the number of containing chunks.
	 *  @return Number of media chunks in integer number format. */
	protected int getChunkCount() {
		return this.mediaJSON.getJSONArray("segments").length();
	}
	
	/** Getter for media duration.
	 *  @return The media duration in seconds as long number format. */
	public long getDuration() {
		return this.mediaJSON.getLong("duration");
	}
	
	/** Getter for the init segment.
	 *  @return A binary data decoded from the {@link JSONObject} containing
	 *  the first bytes to be written to the media output file. */
	public byte[] getInitSegment() {
		return Base64.getDecoder().decode(mediaJSON.getString("init_segment"));
	}
	
	/** Getter for media size.
	 *  @return A string containing the approximate media size as long number format. */
	public long getMediaSize() {
		
		long size = getInitSegment().length;
		
		JSONArray segments = mediaJSON.getJSONArray("segments");
		
		for (int i=0; i<segments.length(); i++)
			size += segments.getJSONObject(i).getLong("size");
		
		return size;
	}
	
	/** Getter for media type.
	 *  @return A string containing the media type (audio or video). */
	public String getMediaType() {
		return this.mediaType;
	}
	
	/** Getter for media segments (chunks).
	 *  @return A {@link JSONArray} containing the media chunks. */
	public JSONArray getSegments() {
		return mediaJSON.getJSONArray("segments");
	}
	
	/** If the given parameter is set to 'true', this method creates a temporary file
	 *  and saves it to an internal variable in this class. Otherwise, the internal file
	 *  is returned. Note that if this method has never called with a 'true' parameter,
	 *  a null file will be returned.
	 *  @return A complete path to a temporary media file. */
	public File getTempFile(boolean isNewFile) {

		// If 'true', a new temporary file is created
		if (isNewFile) {
			
			final String tempDir  = System.getProperty("java.io.tmpdir");
			final String curdate  = PhillsDateUtils.now("YYMMdd_HHmmss");
			final String filename = String.format("%s/%s_%s.tmp",tempDir,this.mediaType,curdate);
			
			this.output = new File(filename);
			
		}
		
		return this.output;
	}
	
	/************************* JLabel Getters Section *************************************/
	
	/** Getter for media duration.
	 *  @return A formatted string containing the media duration. */
	public String getLabelDuration() {
		return PhillsDateParser.getHumanReadableTime(getDuration());
	}
	
	/** Getter for media size.
	 *  @return A formatted string containing the approximate media size. */
	public String getLabelSize() {
		return PhillFileUtils.humanReadableByteCount(getMediaSize());
	}
	
	/************************** Abstract Methods Section **********************************/
	
	/** Getter used to retrieve information to the UI comboBox.
	 *  @return A formatted string containing detailed information about the selected media. */
	public abstract String getComboInfo();
	
	/** Getter used to retrieve information to the UI confirm dialog (just before downloading media).
	 *  @return A formatted string containing detailed information about the selected media. */
	public abstract String getDialogInfo();
	
}
