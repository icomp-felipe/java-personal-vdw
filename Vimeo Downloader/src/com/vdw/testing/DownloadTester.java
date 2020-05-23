package com.vdw.testing;

import java.io.BufferedOutputStream;
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
		
		
		
		String jsonStr = IOUtils.toString(new URL("http://localhost/master.json"),"UTF-8");
		System.out.println(jsonStr);
		
		JSONObject jsonObject = new JSONObject(jsonStr);
		
		String init_segment = jsonObject.getJSONArray("video").getJSONObject(0).getString("init_segment");
		byte[] header = Base64.getDecoder().decode(init_segment);
		
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream("/tmp/video.mp4",true));
		stream.write(header);
		
		for (int i=1; i<=52; i++) {
			
			System.out.print("downloading chunk " + i + "...");
			URL url = new URL("http://localhost/segment-" + i + ".mp4");
			
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
	        int responseCode = httpConn.getResponseCode();
	        
	        System.out.println(responseCode);
	        
	        InputStream inputStream = httpConn.getInputStream();
	        
	        IOUtils.copy(inputStream, stream);
	        
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
