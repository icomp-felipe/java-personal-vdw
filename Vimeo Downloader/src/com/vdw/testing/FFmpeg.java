package com.vdw.testing;

import java.io.File;
import java.io.IOException;

public class FFmpeg {

	public static void main(String[] args) throws InterruptedException, IOException {
		
		File v = new File("/tmp/video.tmp");
		File a = new File("/tmp/audio.tmp");
		
		File outputFile = new File("/tmp/output cu.mkv");
		
		String[] cmd = {"ffmpeg","-i",v.getAbsolutePath(),"-i",a.getAbsolutePath(),"-c","copy",outputFile.getAbsolutePath(),"-y"}; 
		
		String command = String.format("ffmpeg -i %s -i %s -c copy -movflags +faststart -aspect 16:9 %s -y",v.getAbsolutePath(),a.getAbsolutePath(),outputFile.getAbsolutePath());
		
		
		System.out.println(Runtime.getRuntime().exec(cmd).waitFor());
		
	}

}
