package org.geogebra.ggbjdk.java.awt.geom.utils;

public class HashCode {
	int hashcode = 1837269811; 
		
	@Override
	public int hashCode() {
		return hashcode;
	}
	public void append(Double value) {
		hashcode += (value.hashCode() * 32);
	}
	public void append(Integer value) {
		hashcode += (value.hashCode() * 32);
	}
}
