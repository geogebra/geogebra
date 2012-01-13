package geogebra.awt;

public class Composite implements geogebra.common.awt.Composite {
	private java.awt.Composite impl;
	public Composite(java.awt.Composite composite) {
		impl = composite;
	}
	public static java.awt.Composite getAwtComposite(geogebra.common.awt.Composite c){
		if(!(c instanceof Composite))
			return null;
		return ((Composite)c).impl;
	}

}
