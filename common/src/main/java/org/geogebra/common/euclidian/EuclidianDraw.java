package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.draw.DrawAudio;
import org.geogebra.common.euclidian.draw.DrawBarGraph;
import org.geogebra.common.euclidian.draw.DrawBoolean;
import org.geogebra.common.euclidian.draw.DrawBoxPlot;
import org.geogebra.common.euclidian.draw.DrawButton;
import org.geogebra.common.euclidian.draw.DrawConic;
import org.geogebra.common.euclidian.draw.DrawConicPart;
import org.geogebra.common.euclidian.draw.DrawConicSection;
import org.geogebra.common.euclidian.draw.DrawDropDownList;
import org.geogebra.common.euclidian.draw.DrawEmbed;
import org.geogebra.common.euclidian.draw.DrawFormula;
import org.geogebra.common.euclidian.draw.DrawImage;
import org.geogebra.common.euclidian.draw.DrawImageResizable;
import org.geogebra.common.euclidian.draw.DrawImplicitCurve;
import org.geogebra.common.euclidian.draw.DrawInequality;
import org.geogebra.common.euclidian.draw.DrawInlineTable;
import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.euclidian.draw.DrawIntegral;
import org.geogebra.common.euclidian.draw.DrawIntegralFunctions;
import org.geogebra.common.euclidian.draw.DrawLine;
import org.geogebra.common.euclidian.draw.DrawList;
import org.geogebra.common.euclidian.draw.DrawLocus;
import org.geogebra.common.euclidian.draw.DrawPieChart;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.euclidian.draw.DrawPointPlot;
import org.geogebra.common.euclidian.draw.DrawPolyLine;
import org.geogebra.common.euclidian.draw.DrawPolygon;
import org.geogebra.common.euclidian.draw.DrawRay;
import org.geogebra.common.euclidian.draw.DrawSegment;
import org.geogebra.common.euclidian.draw.DrawSlider;
import org.geogebra.common.euclidian.draw.DrawSlope;
import org.geogebra.common.euclidian.draw.DrawSurface;
import org.geogebra.common.euclidian.draw.DrawText;
import org.geogebra.common.euclidian.draw.DrawTurtle;
import org.geogebra.common.euclidian.draw.DrawUpperLowerSum;
import org.geogebra.common.euclidian.draw.DrawVector;
import org.geogebra.common.euclidian.draw.DrawVideo;
import org.geogebra.common.kernel.AlgoCasCellInterface;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.algos.AlgoBarChart;
import org.geogebra.common.kernel.algos.AlgoBoxPlot;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoFunctionAreaSums;
import org.geogebra.common.kernel.algos.AlgoSlope;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.cas.AlgoIntegralDefinite;
import org.geogebra.common.kernel.cas.AlgoIntegralFunctions;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoFormula;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocusNDInterface;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoTurtle;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoRayND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesian2D;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.statistics.AlgoDotPlot;
import org.geogebra.common.kernel.statistics.GeoPieChart;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Factory class for drawables
 *
 */
public class EuclidianDraw {
	/**
	 * adds a GeoElement to ev view
	 * 
	 * @param ev
	 *            euclidian view
	 * 
	 * @param geo
	 *            GeoElement to be added
	 * @return drawable for given GeoElement
	 */
	public static DrawableND newDrawable(EuclidianView ev, GeoElementND geo) {
		DrawableND d = null;
		switch (geo.getGeoClassType()) {
		default:
			break;
		case BOOLEAN:
			d = new DrawBoolean(ev, (GeoBoolean) geo);
			break;

		case BUTTON:

			d = new DrawButton(ev, (GeoButton) geo);
			break;

		case TEXTFIELD:
			d = new DrawInputBox(ev, (GeoInputBox) geo);
			break;

		case POINT:
		case POINT3D:
			d = new DrawPoint(ev, (GeoPointND) geo);
			break;

		case SEGMENT:
		case SEGMENT3D:
			d = new DrawSegment(ev, (GeoSegmentND) geo);
			break;
		case PIECHART:
			d = new DrawPieChart(ev, (GeoPieChart) geo);
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
			// TODO remove this when Feature.PEN_IS_LOCUS is removed
			if (geo instanceof GeoLocusNDInterface) {
				d = new DrawLocus(ev, ((GeoLocusNDInterface) geo).getLocus(),
						CoordSys.XOY);
			} else {
				d = new DrawPolyLine(ev, (GeoPolyLine) geo);
			}
			break;
		case POLYLINE:
		case POLYLINE3D:
			d = new DrawPolyLine(ev, (GeoPolyLine) geo);
			break;

		case FUNCTION_NVAR:
			// create inequality drawable for *all* functions as a placeholder
			// x+y may later become x>y via SetValue / input box
			d = new DrawInequality(ev, (FunctionalNVar) geo);
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
					geo.setLineType(EuclidianStyleConstants.LINE_TYPE_FULL);
					if (!geo.isColorSet()) {
						geo.setLineThickness(
								GeoNumeric.DEFAULT_SLIDER_THICKNESS);
						geo.setLineType(EuclidianStyleConstants.LINE_TYPE_FULL);
					}
				}
				d = new DrawSlider(ev, (GeoNumeric) geo);
			} else {
				d = ev.getCompanion().newDrawAngle((GeoAngle) geo);
				if (geo.isDrawable()) {
					if (!geo.isColorSet()) {
						GColor col = geo.getConstruction()
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
					geo.setLineType(EuclidianStyleConstants.LINE_TYPE_FULL);
				}
				d = new DrawSlider(ev, (GeoNumeric) geo);
			} else if (algo instanceof AlgoSlope) {
				d = new DrawSlope(ev, (GeoNumeric) geo);
			} else if (algo instanceof AlgoIntegralDefinite) {
				d = new DrawIntegral(ev, (GeoNumeric) geo, false);
			} else if (algo instanceof AlgoIntegralFunctions) {
				d = new DrawIntegralFunctions(ev, (GeoNumeric) geo, false);
			} else if (algo instanceof AlgoFunctionAreaSums) {
				d = new DrawUpperLowerSum(ev, (GeoNumeric) geo);
			} else if (algo instanceof AlgoBoxPlot) {
				d = new DrawBoxPlot(ev, (GeoNumeric) geo);
			} else if (algo instanceof AlgoBarChart) {
				d = new DrawBarGraph(ev, (GeoNumeric) geo);
			} else if (algo instanceof AlgoCasCellInterface) {
				ValidExpression ve = ((AlgoCasCellInterface) algo).getCasCell()
						.getInputVE();
				if (ve.isTopLevelCommand()) {
					Command cmd = ve.getTopLevelCommand();
					String name = cmd.getName();
					if ("IntegralBetween".equals(name)
							&& cmd.getArgumentNumber() == 4) {
						d = new DrawIntegralFunctions(ev, (GeoNumeric) geo,
								true);
					} else if ("Integral".equals(name)
							&& cmd.getArgumentNumber() == 3) {
						d = new DrawIntegral(ev, (GeoNumeric) geo, true);
					}
				}
			}
			if (d != null) {
				if (!geo.isColorSet()) {
					ConstructionDefaults consDef = geo.getConstruction()
							.getConstructionDefaults();
					if (geo.isIndependent()) {
						GColor col = consDef
								.getDefaultGeo(
										ConstructionDefaults.DEFAULT_NUMBER)
								.getObjectColor();
						geo.setObjColor(col);
						geo.setLineThickness(
								GeoNumeric.DEFAULT_SLIDER_THICKNESS);
						geo.setLineType(EuclidianStyleConstants.LINE_TYPE_FULL);
					} else {
						GColor col = consDef
								.getDefaultGeo(
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
		case SURFACECARTESIAN:
		case SURFACECARTESIAN3D:
			if (geo instanceof GeoSurfaceCartesian2D) {
				d = new DrawSurface(ev, (GeoSurfaceCartesian2D) geo);
			}
			break;
		case CONICPART:
			d = new DrawConicPart(ev, (GeoConicPartND) geo);
			break;

		case CONIC:
		case CONIC3D:
			d = new DrawConic(ev, (GeoConicND) geo, false);
			break;

		case CONICSECTION:
			d = new DrawConicSection(ev, (GeoConicND) geo);
			break;

		case IMPLICIT_POLY:
			d = new DrawImplicitCurve(ev, (GeoImplicit) geo);
			break;
		case FUNCTION:
			if (((GeoFunction) geo).isBooleanFunction()) {
				d = new DrawInequality(ev, (FunctionalNVar) geo);
			} else {
				d = ev.getCompanion()
						.newDrawParametricCurve((ParametricCurve) geo);
			}
			break;
		case FORMULA:
			GeoFormula equation = (GeoFormula) geo;
			d = new DrawFormula(ev, equation);
			break;
		case TEXT:
			GeoText text = (GeoText) geo;
			d = new DrawText(ev, text);
			break;

		case IMAGE:
			d = createDrawImage(ev, (GeoImage) geo);
			break;

		case LOCUS:
			d = new DrawLocus(ev, ((GeoLocusNDInterface) geo).getLocus(),
					CoordSys.XOY);
			break;

		case CURVE_CARTESIAN:
		case CURVE_CARTESIAN3D:
			d = ev.getCompanion().newDrawParametricCurve((GeoCurveCartesianND) geo);
			break;

		case LIST:
			algo = geo.getParentAlgorithm();
			if (algo instanceof AlgoDotPlot) {
				d = new DrawPointPlot(ev, (GeoList) geo,
						DrawPointPlot.DrawType.DOT_PLOT);
			} else if (((GeoList) geo).drawAsComboBox()) {
				d = new DrawDropDownList(ev, (GeoList) geo);
			} else {
				d = new DrawList(ev, (GeoList) geo);
			}
			break;

		case TURTLE:
			d = new DrawTurtle(ev, (GeoTurtle) geo);
			break;

		case AUDIO:
			d = new DrawAudio(ev, (GeoAudio) geo);
			break;

		case VIDEO:
			d = new DrawVideo(ev, (GeoVideo) geo);
			break;

		case EMBED:
			d = new DrawEmbed(ev, (GeoEmbed) geo);
			break;
		case SYMBOLIC:
			d = new DrawSymbolic(ev, (GeoSymbolic) geo);
			break;
		case INLINE_TEXT:
			d = new DrawInlineText(ev, (GeoInlineText) geo);
			break;
		case TABLE:
			d = new DrawInlineTable(ev, (GeoInlineTable) geo);
		}
		return d;
	}

	private static DrawableND createDrawImage(EuclidianView ev, GeoImage geo) {
		DrawableND d;
		if (ev.getApplication().isWhiteboardActive()) {
			d = new DrawImageResizable(ev, geo);
		} else {
			d = new DrawImage(ev, geo);
		}
		d.update();
		return d;
	}

}
