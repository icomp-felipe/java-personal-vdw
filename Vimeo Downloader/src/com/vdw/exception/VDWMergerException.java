package com.vdw.exception;

/** Exception class used by the media merger thread.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 1.0 - 29/05/2020 */
public class VDWMergerException extends RuntimeException {

	private boolean forwardCause;
	private static final long serialVersionUID = 7155896521211239340L;
	
	/** Constructor used when the programmer already handled an exception.
	 *  When using this constructor, the <code>printStacktrace()</code>
	 *  method won't print anything. */
	public VDWMergerException(String message) {
		super(message); this.forwardCause = true;
	}

	/** Constructor using to forward an unhandled exception with a custom message. */
	public VDWMergerException(String message, Throwable cause) {
		super(message,cause);
	}
	
	/** Only prints the stack trace if this class was not created with
	 *  the constructor <code>VDWMergerException(String message)</code>. */
	@Override
	public void printStackTrace() {
		
		if (!forwardCause)
			getCause().printStackTrace();
		
	}

}
