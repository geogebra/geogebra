package geogebra.awt;

import geogebra.common.awt.GShape;
import geogebra.main.AppD;




/**
 * @author kondr
 *
 */
public class GBasicStrokeD implements geogebra.common.awt.GBasicStroke {
	private java.awt.BasicStroke impl; 
	public GBasicStrokeD(java.awt.BasicStroke basicStroke) {
		impl = basicStroke;
	}
	public GBasicStrokeD(float f,int cap,int join) {
		impl = new java.awt.BasicStroke(f,cap,join);
	}
	public GBasicStrokeD(float f) {
		impl = new java.awt.BasicStroke(f);
	}
	public static java.awt.BasicStroke getAwtStroke(geogebra.common.awt.GBasicStroke s){
		if(!(s instanceof GBasicStrokeD)){
			if (s!= null) AppD.debug("other type");
			return null;
		}
		else return ((GBasicStrokeD)s).impl;
	}
	public int getEndCap(){
		return impl.getEndCap();
	}
	public float getMiterLimit(){
		return impl.getMiterLimit();
	}
	public int getLineJoin(){
		return impl.getLineJoin();
	}
	public GShape createStrokedShape(GShape shape) {
		return new geogebra.awt.GGenericShapeD(impl.createStrokedShape(geogebra.awt.GGenericShapeD.getAwtShape(shape)));
	}
	public float getLineWidth() {
		return impl.getLineWidth();
	}
	public float[] getDashArray() {
		return impl.getDashArray();
	}
	
}
