/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.geos;

import java.util.function.Predicate;

import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement.HitType;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys1DInterface;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoImplicitSurfaceND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoPolygon3DInterface;
import org.geogebra.common.kernel.kernelND.GeoPolyhedronInterface;
import org.geogebra.common.kernel.kernelND.GeoQuadric3DInterface;
import org.geogebra.common.kernel.kernelND.GeoQuadric3DLimitedInterface;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.kernelND.Region3D;

/***
 * Replacement for isInstance checks
 * 
 * For Macro inputs, objects are tested in order so we must have eg GEOVECTOR
 * then GEOVECTORND then MOVEABLE otherwise the test will not work see #2398
 */
public enum TestGeo implements Predicate<Object> {

	// true GeoElements

	/** Test for GEOANGLE */
	GEOANGLE {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoAngle;
		}
	},

	/** Test for GEODUMMYVARIABLE */
	GEODUMMYVARIABLE {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoDummyVariable;
		}
	},

	/** Test for GEONUMERIC */
	GEONUMERIC {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoNumeric;
		}
	},

	/** Test for GEOAXIS */
	GEOAXIS {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoAxis;
		}
	},

	/** Test for GEOSEGMENT */
	GEOSEGMENT {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoSegment;
		}
	},

	/** Test for GEOSEGMENTND */
	GEOSEGMENTND {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoSegmentND;
		}
	},

	/** Test for GEORAY */
	GEORAY {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoRay;
		}
	},

	/** Test for GEOLINE */
	GEOLINE {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoLine;
		}
	},

	/** Test for GEOLINEND */
	GEOLINEND {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoLineND;
		}
	},

	/** Test for GEOVECTOR */
	GEOVECTOR {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoVector;
		}
	},

	/** Test for GEOVECTORND */
	GEOVECTORND {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoVectorND;
		}
	},

	/** Test for GEOBOOLEAN */
	GEOBOOLEAN {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoBoolean;
		}
	},

	/** Test for GEOTEXTFIELD */
	GEOTEXTFIELD {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoInputBox;
		}
	},

	/** Test for GEOBUTTON */
	GEOBUTTON {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoButton;
		}
	},

	/** Test for GEOCASCELL */
	GEOCASCELL {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoCasCell;
		}
	},

	/** Test for GEOCONICPART */
	GEOCONICPART {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoConicPart;
		}
	},

	/** Test for GEOCONIC */
	GEOCONIC {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoConic;
		}
	},

	/** Test for GEOCONICND */
	GEOCONICND {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoConicND;
		}
	},

	/** Test for GEOQUADRIC3D */
	GEOQUADRIC3D {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoQuadric3DInterface;
		}
	},

	/**
	 * Implicit surface
	 */
	GEOIMPLICITSURFACE {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoImplicitSurfaceND;
		}
	},

	/** Test for GEOQUADRIC3D */
	GEOQUADRIC3DLIMITED {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoQuadric3DLimitedInterface;
		}
	},

	/** Test for GEOQUADRICND */
	GEOQUADRICND {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoQuadricND;
		}
	},

	/** Test for GEOPOLYHEDRON */
	GEOPOLYHEDRON {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoPolyhedronInterface;
		}
	},

	/** Test for GEOCURVECARTESIAN */
	GEOCURVECARTESIAN {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoCurveCartesian;
		}
	},

	/** Test for GEOCURVECARTESIAN3D */
	GEOCURVECARTESIAN3D {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoCurveCartesian3DInterface;
		}
	},

	/** Test for GEOCURVECARTESIANND */
	GEOCURVECARTESIANND {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoCurveCartesianND;
		}
	},

	/** Test for GEOFUNCTION */
	GEOFUNCTION {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoFunction;
		}
	},

	/** Test for GEOFUNCTIONNVAR */
	GEOFUNCTIONNVAR {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoFunctionNVar;
		}
	},

	/** Test for GEOFUNCTION2VAR */
	GEOFUNCTION2VAR {
		@Override
		public boolean test(Object ob) {
			if (ob instanceof GeoFunctionNVar) {
				return ((GeoFunctionNVar) ob).isFun2Var();
			}
			return false;
		}
	},

	/** Test for GEOIMAGE */
	GEOIMAGE {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoImage;
		}
	},

	/** Test for GEOLIST */
	GEOLIST {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoList;
		}
	},

	/** Test for GEOLIST as combo box */
	GEOLIST_AS_COMBO {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoList && ((GeoList) ob).drawAsComboBox();
		}
	},

	/** Test for GEOLOCUS */
	GEOLOCUS {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoLocus;
		}
	},

	/** Test for GEOPOINT2 */
	GEOPOINT {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoPoint;
		}
	},

	/** Test for GEOPOINTND */
	GEOPOINTND {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoPointND;
		}
	},

	/** Test for GEOPOLYGON */
	GEOPOLYGON {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoPolygon;
		}
	},

	/** Test for Equilateral rectangle */
	GEOEQUILATERALTRIANGLE {
		@Override
		public boolean test(Object ob) {
			return checkIsRegularPolygon(ob, 3);
		}
	},

	/** Test for Equilateral rectangle */
	GEOSQUARE {
		@Override
		public boolean test(Object ob) {
			return checkIsRegularPolygon(ob, 4);
		}
	},

	/** Test for Regular pentagon */
	GEOREGULARPENTAGON {
		@Override
		public boolean test(Object ob) {
			return checkIsRegularPolygon(ob, 5);
		}
	},

	/** Test for GEOPOLYGON3D */
	GEOPOLYGON3D {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoPolygon3DInterface;
		}
	},

	/** Test for GEOPOLYLINE */
	GEOPOLYLINE {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoPolyLine;
		}
	},

	/** Test for GEOSCRIPTACTION */
	GEOSCRIPTACTION {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoScriptAction;
		}
	},

	/** Test for GEOTEXT */
	GEOTEXT {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoText;
		}
	},

	/** Test for GEOIMPLICITPOLY */
	GEOIMPLICIT {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoImplicit;
		}
	},

	/** Test for GEOCOORDSYS2D, not GEOPLANEND */
	GEOCOORDSYS2DNOTPLANE {
		@Override
		public boolean test(Object ob) {
			return TestGeo.GEOCOORDSYS2D.test(ob) && !TestGeo.GEOPLANEND.test(ob);
		}
	},

	/** Test for GEOPLANEND */
	GEOPLANEND {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoPlaneND;
		}
	},

	/** Test for GEOCOORDSYS2D */
	GEOCOORDSYS2D {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoCoordSys2D;
		}
	},

	/** Test for GEODIRECTIONND */
	GEODIRECTIONND {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoDirectionND;
		}
	},
	/** Test for GEOCOORDSYS1D */
	GEOCOORDSYS1D {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoCoordSys1DInterface;
		}
	},

	/** Test for NUMBERVALUE */
	NUMBERVALUE {
		@Override
		public boolean test(Object ob) {
			return ob instanceof NumberValue;
		}
	},

	/** Test for PATH */
	PATH {
		@Override
		public boolean test(Object ob) {
			return ob instanceof Path;
		}
	},

	/** Test for REGION3D */
	REGION3D {
		@Override
		public boolean test(Object ob) {
			if (ob instanceof Region3D) {
				return true;
			}
			return ((GeoElement) ob).isRegion3D();
		}
	},

	/** Test for DILATEABLE */
	DILATEABLE {
		@Override
		public boolean test(Object ob) {
			return ob instanceof Dilateable;
		}
	},

	/** Test for TRANSLATEABLE */
	TRANSLATEABLE {
		@Override
		public boolean test(Object ob) {
			return ob instanceof Translateable;
		}
	},

	/** Test for MOVEABLE */
	MOVEABLE {
		@Override
		public boolean test(Object ob) {
			if (!(ob instanceof GeoElement)) {
				return false;
			}
			return ((GeoElement) ob).isMoveable();
		}
	},

	/** Test for ROTATEMOVEABLE */
	ROTATEMOVEABLE {
		@Override
		public boolean test(Object ob) {
			if (!(ob instanceof GeoElement)) {
				return false;
			}
			return ((GeoElement) ob).isRotateMoveable();
		}
	},

	/** Test for TRANSFORMABLE */
	TRANSFORMABLE {
		@Override
		public boolean test(Object ob) {
			return ob instanceof Transformable;
		}
	},

	/** Test for ROTATABLE */
	ROTATABLE {
		@Override
		public boolean test(Object ob) {
			return ob instanceof Rotatable;
		}
	},

	/** Test for GEOELEMENT */
	GEOELEMENT {
		@Override
		public boolean test(Object ob) {
			return ob instanceof GeoElement;
		}
	},

	/** Test for OBJECT */
	OBJECT {
		@Override
		public boolean test(Object ob) {
			return true;
		}
	},
	/** path hit on boundary */
	PATH_NO_FILL_HIT {
		@Override
		public boolean test(Object ob) {
			if (ob instanceof GeoConicND) {
				return ((GeoConicND) ob)
						.getLastHitType() == HitType.ON_BOUNDARY;
			}
			return ob instanceof Path;
		}
	};

	/**
	 * 
	 * @param ob
	 *            geo
	 * @param vertexCount
	 *            number of vertices
	 * @return true if ob is regular polygon and has correct number of vertices
	 */
	static boolean checkIsRegularPolygon(Object ob, int vertexCount) {
		if (TestGeo.GEOPOLYGON.test(ob)) {
			GeoPolygon polygon = (GeoPolygon) ob;
			if (polygon.getPointsLength() != vertexCount) {
				return false;
			}
			return polygon.isRegular();
		}
		return false;
	}

	/**
	 * Checks whether given object passes this test
	 * 
	 * @param ob
	 *            object to test
	 * @return true if object passes
	 */
	public boolean check(Object ob) {
		return test(ob);
	}

	/**
	 * @param obj
	 *            object
	 * @return the most specific test this object can pass
	 */
	public static TestGeo getSpecificTest(Object obj) {
		for (TestGeo t : TestGeo.values()) {
			if (t.test(obj)) {
				return t;
			}
		}
		return OBJECT;
	}

	/**
	 * @param object
	 *            changed object
	 * @param setter
	 *            setter
	 * @return whether object.set(setter) will run OK
	 */
	public static boolean canSet(GeoElement object, GeoElement setter) {
		return getSpecificTest(setter).isSubtypeOrEqual(getSpecificTest(object));
	}

	private boolean isSubtypeOrEqual(TestGeo other) {
		if (other == this) {
			return true;
		}
		switch (other) {
		case GEONUMERIC:
			return isSubtypeOrEqual(GEOANGLE) || isSubtypeOrEqual(GEOBOOLEAN);
		case GEOFUNCTION:
			return isSubtypeOrEqual(GEONUMERIC) || isSubtypeOrEqual(GEOLINE);
		case GEOFUNCTIONNVAR:
			return isSubtypeOrEqual(GEOFUNCTION);
		case GEOCONIC:
		case GEOPLANEND:
			return isSubtypeOrEqual(GEOLINE);
		case GEOIMPLICIT:
			return isSubtypeOrEqual(GEOCONIC);
		case GEOPOINT:
			return isSubtypeOrEqual(GEONUMERIC);
		case GEOPOINTND:
			return isSubtypeOrEqual(GEOPOINT);
		default:
			break;
		}
		return false;
	}

}
