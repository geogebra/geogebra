package geogebra.kernel;


public interface MatrixTransformable {
	
	public void matrixTransform(double a00,double a01,double a10,double a11);
	public void matrixTransform(double a00,double a01,double a02,double a10,double a11,double a12,
			double a20,double a21,double a22);
	public GeoElement toGeoElement();

}
