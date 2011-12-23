package geogebra.awt;

public class Shape implements geogebra.common.awt.Shape{
	private java.awt.Shape impl; 
	public boolean intersects(int i, int j, int k, int l) {
		// TODO Auto-generated method stub
		return false;
	}
	public static java.awt.Shape getAwtShape(geogebra.common.awt.Shape s){
		if(!(s instanceof Shape))
			return null;
		return ((Shape)s).impl;
	}
	
	public Shape(java.awt.Shape s){
		impl = s;
	}
}
