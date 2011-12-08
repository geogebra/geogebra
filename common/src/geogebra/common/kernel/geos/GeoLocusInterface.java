package geogebra.common.kernel.geos;

import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.PathMover;
import geogebra.common.kernel.kernelND.GeoPointND;

import java.util.ArrayList;

public interface GeoLocusInterface extends GeoElementInterface {

	// temporary methods, of course
	public ArrayList<MyPoint> getMyPointList();
	public void insertPoint(double x, double y, boolean lineTo);
	public ArrayList<MyPoint> getPoints();
	public void set(GeoElement geo);
	public void clearPoints();
	public void pathChanged(GeoPointND PI);
	public double getMinParameter();
	public double getMaxParameter();
	public boolean isClosedPath();
	public void pointChanged(GeoPointND PI);
	public PathMover createPathMover();
	public void setDefined(boolean flag);
	
}
