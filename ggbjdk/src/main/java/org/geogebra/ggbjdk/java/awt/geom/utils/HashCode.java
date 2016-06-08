package org.geogebra.ggbjdk.java.awt.geom.utils;

public class HashCode {
	int hashcode =  1837269851; 
		
	@Override
	public int hashCode() {
		return hashcode;
	}
	
	// http://stackoverflow.com/questions/892618/create-a-hashcode-of-two-numbers
	public void append(Double value) {
		hashcode += hashcode * 31 + value.hashCode();
	}
	public void append(Integer value) {
		hashcode += hashcode * 31 + value.hashCode();
	}
}
