package geogebra.awt;

public class Dimension extends geogebra.common.awt.Dimension {
	private java.awt.Dimension impl;
	public Dimension(java.awt.Dimension dim){
		impl = dim;
	}
	public Dimension(int a,int b){
		impl = new java.awt.Dimension(a,b);
	}
	public Dimension() {
		impl = new java.awt.Dimension();
	}
	@Override
	public int getWidth() {
		return impl.width;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return impl.height;
	}
	
	/**
	 * @param d dimension, must be of the type geogebra.awt.Dimension
	 * @return AWT implementation wrapped in d
	 */
	public static java.awt.Dimension getAWTDimension(geogebra.common.awt.Dimension d){
		if(!(d instanceof Dimension))
			return null;
		return ((Dimension)d).impl;
	}

}
