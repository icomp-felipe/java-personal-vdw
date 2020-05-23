package com.vdw.testing;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

public class DownloadTester {
	
	public static void main(String[] args) throws MalformedURLException, IOException {
		
		
		
		String jsonStr = IOUtils.toString(new URL("https://137vod-adaptive.akamaized.net/exp=1590008636~acl=%2F111552936%2F%2A~hmac=02c9b99b68ba0e8e233255abf29054d00b52915c2146ab1a9e611e63a494deeb/111552936/sep/video/306477053,306477052/master.json?base64_init=1"),"UTF-8");
		System.out.println(jsonStr);
		
		JSONObject jsonObject = new JSONObject(jsonStr);
		
		String init_segment = jsonObject.getJSONArray("video").getJSONObject(0).getString("init_segment");
		byte[] header = Base64.getDecoder().decode(init_segment);
		
		FileOutputStream stream = new FileOutputStream("video.mp4");
		stream.write(header);
		
		for (int i=1; i<=52; i++) {
			
			System.out.print("downloading chunk " + i + "...");
			URL url = new URL("https://137vod-adaptive.akamaized.net/exp=1590008636~acl=%2F111552936%2F%2A~hmac=02c9b99b68ba0e8e233255abf29054d00b52915c2146ab1a9e611e63a494deeb/111552936/sep/video/306477052/chop/segment-" + i + ".m4s");
			
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
	        int responseCode = httpConn.getResponseCode();
	        
	        System.out.println(responseCode);
	        
	        InputStream inputStream = httpConn.getInputStream();
	        
            byte[] buffer = new byte[4096];
            
            while (inputStream.read(buffer) != -1) {
            	stream.write(buffer);
                
            }
            inputStream.close();
            stream.flush();
            httpConn.disconnect();
		}
		
		
		stream.close();
		
		/*
		File json = new File("master.json");
		String jsonStr = PhillFileUtils.readFileToString(json);
		
		JSONObject jsonObject = new JSONObject(jsonStr);
		
		String init_segment = jsonObject.getJSONArray("video").getJSONObject(0).getString("init_segment");
		byte[] header = Base64.getDecoder().decode(init_segment);
		
		FileOutputStream stream = new FileOutputStream("video.mp4");
		stream.write(header);
		
		File chunk = new File("segment-1.m4s");
		FileInputStream fis = new FileInputStream(chunk);
		
		byte[] raw_data = new byte[(int) chunk.length()];
		
		fis.read(raw_data);
		stream.write(raw_data);
		
		stream.close();*/
		
	}
	
}
