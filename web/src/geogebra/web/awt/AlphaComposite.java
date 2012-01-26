package geogebra.web.awt;

import geogebra.common.awt.Composite;
import geogebra.common.main.AbstractApplication;

public class AlphaComposite implements geogebra.common.awt.AlphaComposite  {

	public static Composite Src;
	private int srcOver;
	private float alpha;

	public AlphaComposite(int srcOver, float alpha) {
	    this.srcOver = srcOver;
	    this.alpha = alpha;
    }
	
	public float getAlpha() {
		return alpha;
	}

	public static AlphaComposite getInstance(int srcO, float a) {
	    return new AlphaComposite(srcO, a);
    }

}
