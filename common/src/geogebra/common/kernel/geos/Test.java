/** 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra.common.kernel.geos;

import geogebra.common.kernel.Path;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoCoordSys1DInterface;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoPolygon3DInterface;
import geogebra.common.kernel.kernelND.GeoQuadric3DInterface;
import geogebra.common.kernel.kernelND.GeoQuadricND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.kernel.kernelND.Region3D;
import geogebra.common.main.App;


/***
 * Replacement for isInstance checks
 * 
 * For Macro inputs, objects are tested in order so we must have eg GEOVECTOR then GEOVECTORND then MOVEABLE otherwise the test will not work
 * see #2398
 * 
 * @author kondr & Arpi
 *
 */
public enum /** Test for Test */
Test {

	// true GeoElements

	/** Test for GEOANGLE */
	GEOANGLE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoAngle;
		}
	},

	/** Test for GEODUMMYVARIABLE */
	GEODUMMYVARIABLE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoDummyVariable;
		}
	},

	/** Test for GEONUMERIC */
	GEONUMERIC {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoNumeric;
		}
	},

	/** Test for GEOAXIS */
	GEOAXIS {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoAxis;
		}
	},

	/** Test for GEOSEGMENT */
	GEOSEGMENT {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoSegment;
		}
	},

	/** Test for GEOSEGMENTND */
	GEOSEGMENTND {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoSegmentND;
		}
	},

	/** Test for GEORAY */
	GEORAY {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoRay;
		}
	},

	/** Test for GEOLINE */
	GEOLINE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoLine;
		}
	},

	/** Test for GEOLINEND */
	GEOLINEND {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoLineND;
		}
	},

	/** Test for GEOVECTOR */
	GEOVECTOR {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoVector;
		}
	},

	/** Test for GEOVECTORND */
	GEOVECTORND {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoVectorND;
		}
	},

	/** Test for GEOBOOLEAN */
	GEOBOOLEAN {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoBoolean;
		}
	},

	/** Test for GEOTEXTFIELD */
	GEOTEXTFIELD {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoTextField;
		}
	},

	/** Test for GEOBUTTON */
	GEOBUTTON {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoButton;
		}
	},

	/** Test for GEOCASCELL */
	GEOCASCELL {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoButton;
		}
	},

	/** Test for GEOCONICPART */
	GEOCONICPART {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoConicPart;
		}
	},

	/** Test for GEOCONIC */
	GEOCONIC {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoConic;
		}
	},

	/** Test for GEOCONICND */
	GEOCONICND {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoConicND;
		}
	},

	/** Test for GEOQUADRIC3D */
	GEOQUADRIC3D {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoQuadric3DInterface;
		}
	},
	
	/** Test for GEOQUADRICND */
	GEOQUADRICND {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoQuadricND;
		}
	},

	/** Test for GEOCURVECARTESIAN */
	GEOCURVECARTESIAN {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoCurveCartesian;
		}
	},

	/** Test for GEOCURVECARTESIAN3D */
	GEOCURVECARTESIAN3D {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoCurveCartesian3DInterface;
		}
	},

	/** Test for GEOCURVECARTESIANND */
	GEOCURVECARTESIANND {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoCurveCartesianND;
		}
	},

	/** Test for GEOINTERVAL */
	GEOINTERVAL {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoInterval;
		}
	},

	/** Test for GEOFUNCTION */
	GEOFUNCTION {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoFunction;
		}
	},

	/** Test for GEOFUNCTIONNVAR */
	GEOFUNCTIONNVAR {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoFunctionNVar;
		}
	},

	/** Test for GEOIMAGE */
	GEOIMAGE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoImage;
		}
	},

	/** Test for GEOLIST */
	GEOLIST {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoList;
		}
	},

	/** Test for GEOLOCUS */
	GEOLOCUS {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoLocus;
		}
	},

	/** Test for GEOPOINT2 */
	GEOPOINT {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoPoint;
		}
	},

	/** Test for GEOPOINTND */
	GEOPOINTND {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoPointND;
		}
	},

	/** Test for GEOPOLYGON */
	GEOPOLYGON {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoPolygon;
		}
	},

	/** Test for GEOPOLYGON3D */
	GEOPOLYGON3D {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoPolygon3DInterface;
		}
	},

	/** Test for GEOPOLYLINE */
	GEOPOLYLINE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoPolyLine;
		}
	},

	/** Test for GEOSCRIPTACTION */
	GEOSCRIPTACTION {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoScriptAction;
		}
	},

	/** Test for GEOTEXT */
	GEOTEXT {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoText;
		}
	},

	/** Test for GEOIMPLICITPOLY */
	GEOIMPLICITPOLY {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoImplicitPoly;
		}
	},

	/** Test for GEOUSERINPUTELEMENT */
	GEOUSERINPUTELEMENT {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoUserInputElement;
		}
	},

	/** Test for GEODIRECTIONND */
	GEODIRECTIONND {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoDirectionND;
		}
	},

	/** Test for GEOCOORDSYS2D */
	GEOCOORDSYS2D {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoCoordSys2D;
		}
	},

	/** Test for GEOCOORDSYS1D */
	GEOCOORDSYS1D {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoCoordSys1DInterface;
		}
	},

	/** Test for NUMBERVALUE */
	NUMBERVALUE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof NumberValue;
		}
	},

	/** Test for PATH */
	PATH {
		@Override
		public boolean check(Object ob) {
			return ob instanceof Path;
		}
	},

	/** Test for REGION3D */
	REGION3D {
		@Override
		public boolean check(Object ob) {
			return ob instanceof Region3D;
		}
	},

	/** Test for DILATEABLE */
	DILATEABLE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof Dilateable;
		}
	},

	/** Test for TRANSLATEABLE */
	TRANSLATEABLE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof Translateable;
		}
	},

	/** Test for MOVEABLE */
	MOVEABLE {
		@Override
		public boolean check(Object ob) {
			if (!(ob instanceof GeoElement))
				return false;
			if (((GeoElement) ob).isMoveable())
				return true;

			return false;
		}
	},

	/** Test for ROTATEMOVEABLE */
	ROTATEMOVEABLE {
		@Override
		public boolean check(Object ob) {
			if (!(ob instanceof GeoElement))
				return false;
			if (((GeoElement) ob).isRotateMoveable())
				return true;

			return false;
		}
	},

	/** Test for TRANSFORMABLE */
	TRANSFORMABLE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof Transformable;
		}
	},
	
	/** Test for ROTATEABLE */
	ROTATEABLE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof Rotateable;
		}
	},

	/** Test for GEOELEMENT */
	GEOELEMENT {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoElement;
		}
	},

	/** Test for OBJECT */
	OBJECT {
		@Override
		public boolean check(Object ob) {
			return true;
		}
	};
	/** 
	 * Checks whether given object passes this test
	 * @param ob object to test
	 * @return true if object passes
	 */
	public abstract boolean check(Object ob);

	/**
	 * @param obj object
	 * @return the most specific test this object can pass
	 */
	public static Test getSpecificTest(Object obj) {
		for (Test t : Test.values()) {
			if (t.check(obj)) {
				App.debug(t);
				return t;
			}
		}
		return OBJECT;
	}
}
