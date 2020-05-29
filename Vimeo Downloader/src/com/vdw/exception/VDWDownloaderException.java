package com.vdw.exception;

public class VDWDownloaderException extends RuntimeException {

	private static final long serialVersionUID = 8739815612870764994L;
	
	public VDWDownloaderException(String message) {
		super(message);
	}

	public VDWDownloaderException(Throwable cause) {
		super(cause);
	}

}
