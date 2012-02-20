/* 
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
import geogebra.common.kernel.kernelND.*;
import geogebra.common.main.AbstractApplication;

@SuppressWarnings("javadoc")
/**
 * Replacement for isInstance checks
 * @author kondr & Arpi
 *
 */
public enum Test {

	// true GeoElements

	GEOANGLE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoAngle;
		}
	},

	GEODUMMYVARIABLE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoDummyVariable;
		}
	},

	GEONUMERIC {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoNumeric;
		}
	},

	GEOAXIS {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoAxis;
		}
	},

	GEOSEGMENT {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoSegment;
		}
	},

	GEORAY {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoRay;
		}
	},

	GEOLINE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoLine;
		}
	},

	GEOVECTOR {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoVector;
		}
	},

	GEOBOOLEAN {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoBoolean;
		}
	},

	GEOTEXTFIELD {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoTextField;
		}
	},

	GEOBUTTON {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoButton;
		}
	},

	GEOCASCELL {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoButton;
		}
	},

	GEOCONICPART {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoConicPart;
		}
	},

	GEOCONIC {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoConic;
		}
	},

	GEOCURVECARTESIAN {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoCurveCartesian;
		}
	},

	GEOFUNCTIONCONDITIONAL {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoFunctionConditional;
		}
	},

	GEOINTERVAL {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoInterval;
		}
	},

	GEOFUNCTION {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoFunction;
		}
	},

	GEOFUNCTIONNVAR {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoFunctionNVar;
		}
	},

	GEOIMAGE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoImage;
		}
	},

	GEOLIST {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoList;
		}
	},

	GEOLOCUS {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoLocus;
		}
	},

	GEOPOINT2 {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoPoint2;
		}
	},

	GEOPOLYGON {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoPolygon;
		}
	},

	GEOPOLYLINE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoPolyLine;
		}
	},

	GEOSCRIPTACTION {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoScriptAction;
		}
	},

	GEOTEXT {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoText;
		}
	},

	GEOIMPLICITPOLY {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoImplicitPoly;
		}
	},

	GEOUSERINPUTELEMENT {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoUserInputElement;
		}
	},

	// abstract GeoElements

	GEOCONICND {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoConicND;
		}
	},

	GEOQUADRICND {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoQuadricND;
		}
	},

	MOVEABLE {
		@Override
		public boolean check(Object ob) {
			if (!(ob instanceof GeoElement))
				return false;
			if (((GeoElement)ob).isMoveable())
				return true;

			return false;
		}
	},

	ROTATEMOVEABLE {
		@Override
		public boolean check(Object ob) {
			if (!(ob instanceof GeoElement))
				return false;
			if (((GeoElement)ob).isRotateMoveable())
				return true;

			return false;
		}
	},

	GEOELEMENT {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoElement;
		}
	},

	// GeoElement-related interfaces

	GEOCOORDSYS2D {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoCoordSys2D;
		}
	},

	GEOSEGMENTND {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoSegmentND;
		}
	},

	GEOLINEND {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoLineND;
		}
	},

	GEOVECTORND {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoVectorND;
		}
	},

	GEODIRECTIONND {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoDirectionND;
		}
	},

	GEOPOINTND {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoPointND;
		}
	},

	NUMBERVALUE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof NumberValue;
		}
	},

	PATH {
		@Override
		public boolean check(Object ob) {
			return ob instanceof Path;
		}
	},

	TRANSLATEABLE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof Translateable;
		}
	},

	GEOQUADRIC3D {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoQuadric3DInterface;
		}
	},
	GEOPOLYGON3D {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoPolygon3DInterface;
		}
	},
	GEOCOORDSYS1D {
		@Override
		public boolean check(Object ob) {
			return ob instanceof GeoCoordSys1DInterface;
		}
	},
	TRANSFORMABLE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof Transformable;
		}
	},
	DILATEABLE {
		@Override
		public boolean check(Object ob) {
			return ob instanceof Dilateable;
		}
	},
	REGION3D {
		@Override
		public boolean check(Object ob) {
			return ob instanceof Region3D;
		}
	},

	OBJECT {
		@Override
		public boolean check(Object ob) {
			return true;
		}
	};

	public abstract boolean check(Object ob);

	public static Test getSpecificTest(Object obj) {
		for(Test t : Test.values()){
			if(t.check(obj)){
				AbstractApplication.debug(t);
				return t;
			}
		}
		return OBJECT;
	}
}
