package geogebra.common.euclidian;

import geogebra.common.euclidian.draw.DrawBarGraph;
import geogebra.common.euclidian.draw.DrawBoolean;
import geogebra.common.euclidian.draw.DrawBoxPlot;
import geogebra.common.euclidian.draw.DrawButton;
import geogebra.common.euclidian.draw.DrawConic;
import geogebra.common.euclidian.draw.DrawConicPart;
import geogebra.common.euclidian.draw.DrawImage;
import geogebra.common.euclidian.draw.DrawImplicitPoly;
import geogebra.common.euclidian.draw.DrawInequality;
import geogebra.common.euclidian.draw.DrawIntegral;
import geogebra.common.euclidian.draw.DrawIntegralFunctions;
import geogebra.common.euclidian.draw.DrawLine;
import geogebra.common.euclidian.draw.DrawList;
import geogebra.common.euclidian.draw.DrawLocus;
import geogebra.common.euclidian.draw.DrawParametricCurve;
import geogebra.common.euclidian.draw.DrawPoint;
import geogebra.common.euclidian.draw.DrawPolyLine;
import geogebra.common.euclidian.draw.DrawPolygon;
import geogebra.common.euclidian.draw.DrawRay;
import geogebra.common.euclidian.draw.DrawSegment;
import geogebra.common.euclidian.draw.DrawSlider;
import geogebra.common.euclidian.draw.DrawSlope;
import geogebra.common.euclidian.draw.DrawText;
import geogebra.common.euclidian.draw.DrawTextField;
import geogebra.common.euclidian.draw.DrawTurtle;
import geogebra.common.euclidian.draw.DrawUpperLowerSum;
import geogebra.common.euclidian.draw.DrawVector;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.algos.AlgoBarChart;
import geogebra.common.kernel.algos.AlgoBoxPlot;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoFunctionAreaSums;
import geogebra.common.kernel.algos.AlgoIntegralFunctions;
import geogebra.common.kernel.algos.AlgoSlope;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.cas.AlgoIntegralDefinite;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.kernel.geos.GeoTurtle;
import geogebra.common.kernel.geos.ParametricCurve;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoRayND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.GeoVectorND;

/**
 *	Factory class for drawables
 *
 */
public class EuclidianDraw {
	/**
	 * adds a GeoElement to ev view
	 * @param ev euclidian view
	 * 
	 * @param geo
	 *            GeoElement to be added
	 * @return drawable for given GeoElement
	 */
	public static DrawableND newDrawable(EuclidianView ev, GeoElement geo) {
		
		Drawable d = null;
		switch (geo.getGeoClassType()) {
		case BOOLEAN:
			d = new DrawBoolean(ev, (GeoBoolean) geo);
			break;

		case BUTTON:

			d = new DrawButton(ev, (GeoButton) geo);
			break;

		case TEXTFIELD:

			d = new DrawTextField(ev, (GeoTextField) geo);
			break;

		case POINT:
		case POINT3D:
			d = new DrawPoint(ev, (GeoPointND) geo);
			break;

		case SEGMENT:
		case SEGMENT3D:
			d = new DrawSegment(ev, (GeoSegmentND) geo);
			break;

		case RAY:
		case RAY3D:
			d = new DrawRay(ev, (GeoRayND) geo);
			break;

		case LINE:
		case LINE3D:
			d = new DrawLine(ev, (GeoLineND) geo);
			break;

		case POLYGON:
		case POLYGON3D:
			d = new DrawPolygon(ev, (GeoPolygon) geo);
			break;

		case PENSTROKE:
		case POLYLINE:
			d = new DrawPolyLine(ev, (GeoPolyLine) geo);
			break;

		case FUNCTION_NVAR:
			if (((GeoFunctionNVar) geo).isBooleanFunction()) {
				d = new DrawInequality(ev, (GeoFunctionNVar) geo);
			}
			break;
		case INTERVAL:
			if (((GeoFunction) geo).isBooleanFunction()) {
				d = new DrawInequality(ev, (GeoFunction) geo);
			}
			break;

		case ANGLE:
			if (geo.isIndependent()) {
				// independent number may be shown as slider
				if (geo.isEuclidianVisible()) {
					// make sure min/max initialized properly on redefinition
					// eg f(x)=x^2
					// f = 1
					geo.setEuclidianVisible(false);
					geo.setEuclidianVisible(true);
				}
				d = new DrawSlider(ev, (GeoNumeric) geo);
			} else {
				d = ev.newDrawAngle((GeoAngle) geo);
				if (geo.isDrawable()) {
					if (!geo.isColorSet()) {
						geogebra.common.awt.GColor col = geo
								.getConstruction()
								.getConstructionDefaults()
								.getDefaultGeo(
										ConstructionDefaults.DEFAULT_ANGLE)
										.getObjectColor();
						geo.setObjColor(col);
					}
				}
			}
			break;

		case NUMERIC:
			AlgoElement algo = geo.getDrawAlgorithm();
			if (algo == null) {
				// independent number may be shown as slider
				if (geo.isEuclidianVisible()) {
					// make sure min/max initialized properly on redefinition
					// eg f(x)=x^2
					// f = 1
					geo.setEuclidianVisible(false);
					geo.setEuclidianVisible(true);
				}
				d = new DrawSlider(ev, (GeoNumeric) geo);
			} else if (algo instanceof AlgoSlope) {
				d = new DrawSlope(ev, (GeoNumeric) geo);
			} else if (algo instanceof AlgoIntegralDefinite) {
				d = new DrawIntegral(ev, (GeoNumeric) geo);
			} else if (algo instanceof AlgoIntegralFunctions) {
				d = new DrawIntegralFunctions(ev, (GeoNumeric) geo);
			} else if (algo instanceof AlgoFunctionAreaSums) {
				d = new DrawUpperLowerSum(ev, (GeoNumeric) geo);
			} else if (algo instanceof AlgoBoxPlot) {
				d = new DrawBoxPlot(ev, (GeoNumeric) geo);
			} else if (algo instanceof AlgoBarChart) {
				d = new DrawBarGraph(ev, (GeoNumeric) geo);
			}
			if (d != null) {
				if (!geo.isColorSet()) {
					ConstructionDefaults consDef = geo.getConstruction()
							.getConstructionDefaults();
					if (geo.isIndependent()) {
						geogebra.common.awt.GColor col = consDef.getDefaultGeo(
								ConstructionDefaults.DEFAULT_NUMBER)
								.getObjectColor();
						geo.setObjColor(col);
					} else {
						geogebra.common.awt.GColor col = consDef.getDefaultGeo(
								ConstructionDefaults.DEFAULT_POLYGON)
								.getObjectColor();
						geo.setObjColor(col);
					}
				}
			}
			break;

		case VECTOR:
		case VECTOR3D:
			d = new DrawVector(ev, (GeoVectorND) geo);
			break;

		case CONICPART:
			d = new DrawConicPart(ev, (GeoConicPart) geo);
			break;

		case CONIC:
		case CONIC3D:
			d = new DrawConic(ev, (GeoConicND) geo);
			break;

		case IMPLICIT_POLY:
			d = new DrawImplicitPoly(ev, (GeoImplicitPoly) geo);
			break;

		case FUNCTION:
		case FUNCTIONCONDITIONAL:
			if (((GeoFunction) geo).isBooleanFunction()) {
				d = new DrawInequality(ev, (FunctionalNVar) geo);
			} else {
				d = new DrawParametricCurve(ev, (ParametricCurve) geo);
			}
			break;

		case TEXT:
			GeoText text = (GeoText) geo;
			d = new DrawText(ev, text);
			break;

		case IMAGE:
			d = new DrawImage(ev, (GeoImage) geo);
			break;

		case LOCUS:
			d = new DrawLocus(ev, (GeoLocus) geo);
			break;

		case CURVE_CARTESIAN:
			d = new DrawParametricCurve(ev, (GeoCurveCartesian) geo);
			break;

		case LIST:
			d = new DrawList(ev, (GeoList) geo);
			break;

		case TURTLE:
			d = new DrawTurtle(ev, (GeoTurtle) geo);
			break;
		}

		return d;
	}	

}
