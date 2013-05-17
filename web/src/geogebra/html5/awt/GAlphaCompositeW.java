package geogebra.html5.awt;


public class GAlphaCompositeW implements geogebra.common.awt.GAlphaComposite  {

	public static GAlphaCompositeW Src = new GAlphaCompositeW(SRC, 1.0f);
	private int srcOver;
	private float alpha;

	public GAlphaCompositeW(int srcOver, float alpha) {
	    this.srcOver = srcOver;
	    this.alpha = alpha;
    }
	
	public float getAlpha() {
		return alpha;
	}

	public static GAlphaCompositeW getInstance(int srcO, float a) {
	    return new GAlphaCompositeW(srcO, a);
    }

}
