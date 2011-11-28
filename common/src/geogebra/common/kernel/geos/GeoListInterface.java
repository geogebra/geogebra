package geogebra.common.kernel.geos;

public interface GeoListInterface {

	boolean isDefined();
	GeoElementInterface get(int i);
	int size();
	void setDefined(boolean b);
	void clear();
	void add(GeoElementInterface num);

}
