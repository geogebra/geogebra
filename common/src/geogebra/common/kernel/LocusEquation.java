package geogebra.common.kernel;

import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoIntersectConics;
import geogebra.common.kernel.algos.AlgoIntersectLineConic;
import geogebra.common.kernel.algos.AlgoIntersectLines;
import geogebra.common.kernel.algos.AlgoIntersectSingle;
import geogebra.common.kernel.algos.AlgoMidpoint;
import geogebra.common.kernel.algos.AlgoMidpointSegment;
import geogebra.common.kernel.algos.EquationElementInterface;
import geogebra.common.kernel.algos.EquationScopeInterface;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.CmdLocusEquation;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.elements.EquationAngularBisectorLines;
import geogebra.common.kernel.locusequ.elements.EquationAngularBisectorPoints;
import geogebra.common.kernel.locusequ.elements.EquationCirclePointRadius;
import geogebra.common.kernel.locusequ.elements.EquationCircleThreePoints;
import geogebra.common.kernel.locusequ.elements.EquationCircleTwoPoints;
import geogebra.common.kernel.locusequ.elements.EquationConicFivePoints;
import geogebra.common.kernel.locusequ.elements.EquationConicPartCircle;
import geogebra.common.kernel.locusequ.elements.EquationConicPartCircumcircle;
import geogebra.common.kernel.locusequ.elements.EquationDiameterLine;
import geogebra.common.kernel.locusequ.elements.EquationEllipseFociLength;
import geogebra.common.kernel.locusequ.elements.EquationEllipseFociPoint;
import geogebra.common.kernel.locusequ.elements.EquationHyperbolaFociLength;
import geogebra.common.kernel.locusequ.elements.EquationHyperbolaFociPoint;
import geogebra.common.kernel.locusequ.elements.EquationIntersectConicsRestriction;
import geogebra.common.kernel.locusequ.elements.EquationIntersectLineConicRestriction;
import geogebra.common.kernel.locusequ.elements.EquationIntersectLinesRestriction;
import geogebra.common.kernel.locusequ.elements.EquationIntersectSingleRestriction;
import geogebra.common.kernel.locusequ.elements.EquationJoinPoints;
import geogebra.common.kernel.locusequ.elements.EquationJoinPointsRay;
import geogebra.common.kernel.locusequ.elements.EquationJoinPointsSegment;
import geogebra.common.kernel.locusequ.elements.EquationLineBisector;
import geogebra.common.kernel.locusequ.elements.EquationLineBisectorSegment;
import geogebra.common.kernel.locusequ.elements.EquationLinePointLine;
import geogebra.common.kernel.locusequ.elements.EquationMidpointRestriction;
import geogebra.common.kernel.locusequ.elements.EquationMidpointSegmentRestriction;
import geogebra.common.kernel.locusequ.elements.EquationOrthoLinePointLine;
import geogebra.common.kernel.locusequ.elements.EquationParabolaPointLine;
import geogebra.common.kernel.locusequ.elements.EquationPointOnPathRestriction;
import geogebra.common.kernel.locusequ.elements.EquationPolarLine;
import geogebra.common.kernel.locusequ.elements.EquationPolygon;
import geogebra.common.kernel.locusequ.elements.EquationPolygonRegular;
import geogebra.common.kernel.locusequ.elements.EquationSemicircle;
import geogebra.common.kernel.locusequ.elements.EquationTangentPoint;

/**
 * needed to separate out LocusEquation stuff into the cas jar (so that minimal
 * applets work without it etc)
 * 
 * @author michael
 * 
 */
public class LocusEquation {

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnPolygon(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationPolygon(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnAngularBisectorLines(
			GeoElement geo, AlgoElement algo, EquationScopeInterface scope) {
		return new EquationAngularBisectorLines(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnAngularBisectorPoints(
			GeoElement geo, AlgoElement algo, EquationScopeInterface scope) {
		return new EquationAngularBisectorPoints(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnCirclePointRadius(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationCirclePointRadius(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnCircleThreePoints(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationCircleThreePoints(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnCircleTwoPoints(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationCircleTwoPoints(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnConicFivePoints(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationConicFivePoints(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnCircleArc(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {

		return new EquationConicPartCircle(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnCircumcircleArc(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {

		return new EquationConicPartCircumcircle(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnDiameterLine(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationDiameterLine(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnEllipseFociLength(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationEllipseFociLength(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnEllipseFociPoint(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationEllipseFociPoint(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnHyperbolaFociLength(
			GeoElement geo, AlgoElement algo, EquationScopeInterface scope) {
		return new EquationHyperbolaFociLength(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnHyperbolaFociPoint(
			GeoElement geo, AlgoElement algo, EquationScopeInterface scope) {
		return new EquationHyperbolaFociPoint(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnIntersectConics(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationIntersectConicsRestriction(geo,
				(AlgoIntersectConics) algo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnIntersectLineConic(
			GeoElement geo, AlgoElement algo, EquationScopeInterface scope) {
		return new EquationIntersectLineConicRestriction(geo,
				(AlgoIntersectLineConic) algo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnIntersectLines(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationIntersectLinesRestriction(geo,
				(AlgoIntersectLines) algo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnIntersectSingle(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationIntersectSingleRestriction(geo,
				(AlgoIntersectSingle) algo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnJoinPoints(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationJoinPoints(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnJoinPointsRay(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationJoinPointsRay(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnLineBisector(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationLineBisector(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnLineBisectorSegment(
			GeoElement geo, AlgoElement algo, EquationScopeInterface scope) {
		return new EquationLineBisectorSegment(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnLinePointLine(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationLinePointLine(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnMidpoint(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationMidpointRestriction(geo, (AlgoMidpoint) algo,
				(EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnMidpointSegment(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationMidpointSegmentRestriction(geo,
				(AlgoMidpointSegment) algo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnJoinPointsSegment(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationJoinPointsSegment(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnOrthoLinePointLine(
			GeoElement geo, AlgoElement algo, EquationScopeInterface scope) {
		return new EquationOrthoLinePointLine(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnParabolaPointLine(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationParabolaPointLine(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnPointOnPath(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationPointOnPathRestriction(geo, algo,
				(EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnPolarLine(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationPolarLine(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnSemicircle(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationSemicircle(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnPolygonRegular(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationPolygonRegular(geo, (EquationScope) scope);

	}

	/**
	 * @param geo
	 *            result
	 * @param algo
	 *            algorithm
	 * @param scope
	 *            scope
	 * @return equation element
	 */
	public static EquationElementInterface eqnTangentPoint(GeoElement geo,
			AlgoElement algo, EquationScopeInterface scope) {
		return new EquationTangentPoint(geo, (EquationScope) scope);

	}

	/**
	 * @param kernel
	 *            kernel
	 * @return processor for LocusEquation command
	 */
	public static CommandProcessor newCmdLocusEquation(Kernel kernel) {
		return new CmdLocusEquation(kernel);
	}

}
