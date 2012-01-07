package geogebra.awt;


public class Point2D extends geogebra.common.awt.Point2D {
	private java.awt.geom.Point2D.Double impl;
	public Point2D(){
		impl = new java.awt.geom.Point2D.Double();
	}
	public Point2D(double x, double y) {
		impl = new java.awt.geom.Point2D.Double(x,y);
	}	
	
	public Point2D(java.awt.geom.Point2D.Double point) {
		this();
		impl = point;
	}
	public static java.awt.geom.Point2D getAwtPoint2D(geogebra.common.awt.Point2D p) {
		if (p==null) return null;
		return new java.awt.geom.Point2D.Double(p.getX(),p.getY());
	}

	
	
	public double getX() {
		return impl.x;
	}

	
	public double getY() {
		return impl.y;
	}

	
	public void setX(double x) {
		impl.x=x;

	}

	
	public void setY(double y) {
		impl.y=y;
	}
	
	public double distance(geogebra.common.awt.Point2D q) {
		// TODO Auto-generated method stub
		return impl.distance(q.getX(),q.getY());
	}
	
	public double distance(double x, double y) {
		return impl.distance(x,y);
	}
	
	public boolean equals(Object o){
		if(!(o instanceof Point2D))
			return false;
		return impl.equals(((Point2D)o).impl);
	}
	
	public int hashcode(){
		return impl.hashCode(); 
	}


}
