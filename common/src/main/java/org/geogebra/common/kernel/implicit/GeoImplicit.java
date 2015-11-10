package org.geogebra.common.kernel.implicit;

import java.util.TreeSet;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
public interface GeoImplicit extends GeoElementND {

	double[][] getCoeff();

	void setCoeff(double[][] coeff);

	void setDefined();

	int getDeg();

	boolean isOnScreen();

	GeoLocusND<? extends MyPoint> getLocus();

	public TreeSet<GeoElement> getAllChildren();

	double evalPolyAt(double evaluate, double x);

	int getDegX();


	int getDegY();

	void setInputForm();

	double evalDiffXPolyAt(double inhomX, double inhomY);

	double evalDiffYPolyAt(double inhomX, double inhomY);

	boolean isOnPath(GeoPointND r);

	void translate(double d, double e);

}
