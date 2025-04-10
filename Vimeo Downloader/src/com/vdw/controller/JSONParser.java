package com.vdw.controller;

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.nio.charset.*;
import java.time.*;
import java.util.*;

import org.json.*;

import com.vdw.model.*;

/** Provides useful methods to handle with 'master.json' file.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 1.1, 10/APR/2025 */
public class JSONParser {
	
	/** Retrieves a {@link JSONObject} from a given uri.
	 *  @param uri - JSON URI
	 *  @return The {@link JSONObject} downloaded from URI.
	 *  @throws JSONException when, for some reason, the valid URI could not be reached or the given link doesn't provide a proper JSON.
	 *  @throws IOException when the attempt to connect to the URI fails. 
	 *  @throws InterruptedException if the connection operation is interrupted for some reason. */
	public static JSONObject getJSON(final URI uri) throws JSONException, IOException, InterruptedException {
		
		JSONObject jso = null;
		
		// Setting connection parameters
		HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();	// Connection timeout set to 5s
		HttpRequest request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(10)).build();	// Download timeout set to 10s
				
		// Connecting...
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
		
		// Getting the response
		switch (response.statusCode()) {
		
			// Expired link
			case 410:
				throw new JSONException("The provided 'master.json' link has expired!");
			
			// Not found
			case 404:
				throw new JSONException("The provided 'master.json' link is not online!");
			
			// Success
			case 200:
				
				// Creating JSON
				jso = new JSONObject(response.body());
				
				// Calculating the media base URI and...
				URI baseURI = uri.resolve(jso.getString("base_url"));
				
				// ...inserting into the newly created JSON
				jso.put("media_base_url", baseURI.toString());
				
				break;
		
		}
		
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
