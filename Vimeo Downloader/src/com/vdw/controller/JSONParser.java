package com.vdw.controller;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;
import org.apache.commons.io.*;
import com.vdw.model.*;

/** Provides useful methods to handle with 'master.json' file.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 1.0, 21/05/2020 */
public class JSONParser {
	
	/** Retrieves a {@link JSONObject} from a given url.
	 *  @param url - JSON URL
	 *  @return The {@link JSONObject} downloaded from URL.
	 *  @throws JSONException when, for some reason, the valid URL could not be reached or the given link doesn't provide a proper JSON.
	 *  @throws IOException when the attempt to connect to the URL fails. */
	public static JSONObject getJSON(final URL url) throws JSONException, IOException {
		
		// Connecting to the URL
		JSONObject jso = null;
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setConnectTimeout( 5000);	// Connection timeout set to 5s
		connection.setReadTimeout   (10000);	// Download timeout set to 10s
		
		// Getting the response
		switch (connection.getResponseCode()) {
		
			// Expired link
			case 410:
				throw new JSONException("The provided 'master.json' link has expired!");
			
			// Not found
			case 404:
				throw new JSONException("The provided 'master.json' link is not online!");
			
			// Success
			case 200:
				
				// Here I download the JSON string to a String and later to a proper JSONObject
				InputStream stream = connection.getInputStream();
				String json = IOUtils.toString(stream,"UTF-8");
				
				// Creating JSON
				jso = new JSONObject(json);
				
				// Closing web connection
				stream.close();
				
				// Calculating the media base URL and...
				String aux = jso.getString("base_url");
				URL baseURL = new URL(url,aux);
				
				// ...inserting into the newly created JSON
				jso.put("media_base_url", baseURL.toString());
				
				break;
		
		}
		
		connection.disconnect();
		
		return jso;
	}

	/** Gets a list of available videos from the given {@link JSONObject}.
	 *  @param json - proper 'master.json' object
	 *  @return List of {@link Video} streams available to download. */
	public static ArrayList<Video> getVideoList(final JSONObject json) {
		
		// Preparing variables
		ArrayList<Video> videoList = null;
		
		// Getting array of videos from JSON
		JSONArray videos = json.getJSONArray("video");
		final int length = videos.length();
		
		// If I have videos...
		if (length > 0)
			videoList = new ArrayList<Video>(length);
		
		// ...then I fill the list
		for (int i=0; i<length; i++) {

			JSONObject object = videos.getJSONObject(i);
			Video video = new Video(object,i);
			videoList.add(video);
			
		}
		
		return videoList;
	}
	
	/** Gets a list of available audios from the given {@link JSONObject}.
	 *  @param json - proper 'master.json' object
	 *  @return List of {@link Audio} streams available to download. */
	public static ArrayList<Audio> getAudioList(final JSONObject json) {
		
		// Preparing variables
		ArrayList<Audio> audioList = null;
		
		// Getting array of audios from JSON
		JSONArray audios = json.getJSONArray("audio");
		final int length = audios.length();
		
		// If I have audios...
		if (length > 0)
			audioList = new ArrayList<Audio>(length);
		
		// ...then I fill the list
		for (int i=0; i<length; i++) {

			JSONObject object = audios.getJSONObject(i);
			Audio audio= new Audio(object,i);
			audioList.add(audio);
			
		}
		
		return audioList;
	}
	
}
