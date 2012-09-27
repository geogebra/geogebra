package org.rosuda.REngine;

/** REXPRaw represents a raw vector in R - essentially a sequence of bytes. */
public class REXPRaw extends REXPVector {
	private byte[] payload;
	
	/** create a new raw vector with the specified payload
	 *  @param load payload of the raw vector */
	public REXPRaw(byte[] load) {
		super();
		payload=(load==null)?new byte[0]:load;
	}

	/** create a new raw vector with the specified payload and attributes
	 *  @param load payload of the raw vector 
	 *  @param attr attributes for the resulting R object */
	public REXPRaw(byte[] load, REXPList attr) {
		super(attr);
		payload=(load==null)?new byte[0]:load;
	}
	
	public int length() { return payload.length; }

	public boolean isRaw() { return true; }

	public byte[] asBytes() { return payload; }

	public Object asNativeJavaObject() {
		return payload;
	}
}
