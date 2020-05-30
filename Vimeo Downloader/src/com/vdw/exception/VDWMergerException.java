package com.vdw.exception;

public class VDWMergerException extends RuntimeException {

	private static final long serialVersionUID = 7155896521211239340L;
	
	public VDWMergerException(String message) {
		super(message);
	}

	public VDWMergerException(String message, Throwable cause) {
		super(message,cause);
	}
	
	@Override
	public void printStackTrace() {
		
		if (!(getCause() instanceof VDWMergerException))
			getCause().printStackTrace();
		
	}

}
