package com.vdw.exception;

/** Exception class used by the media downloader thread.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 1.0 - 29/05/2020 */
public class VDWDownloaderException extends RuntimeException {

	private boolean forwardCause;
	private static final long serialVersionUID = 8739815612870764994L;
	
	/** Constructor used when the programmer already handled an exception.
	 *  When using this constructor, the <code>printStacktrace()</code>
	 *  method won't print anything. */
	public VDWDownloaderException(String message) {
		super(message);	this.forwardCause = true;
	}

	/** Constructor using to forward an unhandled exception. */
	public VDWDownloaderException(Throwable cause) {
		super(cause);
	}

	/** Constructor using to forward an unhandled exception with a custom message. */
	public VDWDownloaderException(String message, Throwable cause) {
		super(message,cause);
	}
	
	/** Only prints the stack trace if this class was not created with
	 *  the constructor <code>VDWDownloaderException(String message)</code>. */
	@Override
	public void printStackTrace() {
		
		if (!forwardCause)
			getCause().printStackTrace();
		
	}

}
