package com.vdw.exception;

public class VDWDownloaderException extends RuntimeException {

	private static final long serialVersionUID = 8739815612870764994L;
	
	public VDWDownloaderException(String message) {
		super(message);
	}

	public VDWDownloaderException(Throwable cause) {
		super(cause);
	}

	public VDWDownloaderException(String message, Throwable cause) {
		super(message,cause);
	}
	
	@Override
	public void printStackTrace() {
		
		if (!(getCause() instanceof VDWDownloaderException))
			getCause().printStackTrace();
		
	}

}
