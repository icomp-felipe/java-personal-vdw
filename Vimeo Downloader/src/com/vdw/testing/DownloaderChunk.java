package com.vdw.testing;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

public class DownloaderChunk {

	public static void main(String[] args) throws Exception {
		
		for (int i=1; i<=52; i++) {
			
			URL url = new URL("https://skyfire.vimeocdn.com/1590294168-0xe58207047888d547148c95f8789239ba9051e562/111552936/sep/video/306477052/chop/segment-" + i + ".m4s");
			
			InputStream input = url.openStream();
			FileOutputStream output = new FileOutputStream("segment-" + i + ".m4s");
			
			IOUtils.copy(input, output);
			
			input.close();
			output.close();

			System.out.println("downloaded chunk " + i);
			
		}
		
		
		
	}

}
