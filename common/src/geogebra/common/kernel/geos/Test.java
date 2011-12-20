package geogebra.common.kernel.geos;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.*;
import geogebra.common.main.AbstractApplication;
@SuppressWarnings("javadoc")
/**
 * Replacement for isInstance checks
 * @author kondr
 *
 */
public enum Test {
	MOVEABLE {
		@Override
		public boolean check(GeoElement ge) {
			return false;
		}
	},
	ROTATEMOVEABLE {
		@Override
		public boolean check(GeoElement ge) {
			return false;
		}
	},
	
	GEOVECTOR {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoVector;
		}
	},
	GEONUMERIC {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoNumeric;
		}
	},
	GEOLIST {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoList;
		}
	},
	GEOAXIS {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoAxis;
		}
	},
	GEOSEGMENT {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoSegment;
		}
	},
	GEOLINE {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoLine;
		}
	},
	GEOCONIC {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoConic;
		}
	},
	GEOINTERVAL {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoInterval;
		}
	},
	GEOFUNCTION {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoFunction;
		}
	},
	GEOPOLYGON {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoPolygon;
		}
	},
	GEOPOLYLINE {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoPolyLine;
		}
	},
	GEOPOINT2 {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoPoint2;
		}
	},
	GEOVECTORND {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoVectorND;
		}
	},
	GEOLINEND {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoLineND;
		}
	},
	GEOSEGMENTND {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoSegmentND;
		}
	},
	GEOIMPLICITPOLY {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoImplicitPoly;
		}
	},
	GEOCURVECARTESIAN {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoCurveCartesian;
		}
	},
	GEOIMAGE {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoImage;
		}
	},
	NUMBERVALUE {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof NumberValue;
		}
	},
	PATH {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof Path;
		}
	},
	TRANSLATEABLE {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof Translateable;
		}
	},
	DIRECTIONND {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoDirectionND;
		}
	},
	GEOCONICND {
		@Override
		public boolean check(GeoElement ge) {
			return false;
		}
	},
	GEOCOORDSYS2D {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoCoordSys2D;
		}
	},
	GEOQUADRICND {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoQuadricND;
		}
	},
	GEOQUADRIC3D {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoQuadric3DInterface;
		}
	},
	GEOPOLYGON3D {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoPolygon3DInterface;
		}
	},
	GEOCOORDSYS1D {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoCoordSys1DInterface;
		}
	},
	TRANSFORMABLE {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof Transformable;
		}
	},
	DILATEABLE {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof Dilateable;
		}
	},
	GEOPOINTND {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof GeoPointND;
		}
	},
	REGION3D {
		@Override
		public boolean check(GeoElement ge) {
			return ge instanceof Region3D;
		}	
	},
	GEOELEMENT {
		@Override
		public boolean check(GeoElement ge) {
			return true;
		}
	}
	;
	public abstract boolean check(GeoElement ge);
	public static Test getSpecificTest(GeoElement ge){
		for(Test t : Test.values()){
			if(t.check(ge)){
				AbstractApplication.debug(t);
				return t;
			}
		}
		return GEOELEMENT;
	}
}