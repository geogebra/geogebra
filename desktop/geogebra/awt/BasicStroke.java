package geogebra.awt;

import geogebra.common.awt.Shape;
import geogebra.main.Application;




/**
 * @author kondr
 *
 */
public class BasicStroke implements geogebra.common.awt.BasicStroke {
	private java.awt.BasicStroke impl; 
	public BasicStroke(java.awt.BasicStroke basicStroke) {
		impl = basicStroke;
	}
	public BasicStroke(float f,int cap,int join) {
		impl = new java.awt.BasicStroke(f,cap,join);
	}
	public BasicStroke(float f) {
		impl = new java.awt.BasicStroke(f);
	}
	public static java.awt.BasicStroke getAwtStroke(geogebra.common.awt.BasicStroke s){
		if(!(s instanceof BasicStroke)){
			if (s!= null) Application.debug("other type");
			return null;
		}
		else return ((BasicStroke)s).impl;
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
	public Shape createStrokedShape(Shape shape) {
		return new geogebra.awt.GenericShape(impl.createStrokedShape(geogebra.awt.GenericShape.getAwtShape(shape)));
	}
	
}
