package geogebra.common.kernel.algos;

import geogebra.common.kernel.geos.GeoElement;
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
 * needed to separate out LocusEquation stuff into the cas jar (so that minimal applets work without it etc)
 * 
 * @author michael
 *
 */
public class BuildEquationElement {

	/**
	 * @param className className
	 * @param element element
	 * @param scope scope
	 * @return @return
	 */
	public static EquationElementInterface buildEquationElementForGeo(
			AlgoElement algo, GeoElement element, EquationScopeInterface scopeI) {
		
		EquationScope scope = (EquationScope)scopeI;
		
		switch (algo.getClassName()) {
		case AlgoPolygon: 
			return new EquationPolygon(element, scope);
			
		case AlgoAngularBisectorLines:
			return new EquationAngularBisectorLines(element, scope);
			
		case AlgoAngularBisectorPoints:
			return new EquationAngularBisectorPoints(element, scope);
			
		case AlgoCirclePointRadius:
			return new EquationCirclePointRadius(element, scope);

		case AlgoCircleThreePoints:
			return new EquationCircleThreePoints(element, scope);

		case AlgoCircleTwoPoints:
			return new EquationCircleTwoPoints(element, scope);

		case AlgoConicFivePoints:
			return new EquationConicFivePoints(element, scope);

		case AlgoCircleArc:
		case AlgoCircleSector:
			return new EquationConicPartCircle(element, scope);
			
		case AlgoCircumcircleArc:
		case AlgoCircumcircleSector:
			return new EquationConicPartCircumcircle(element, scope);

		case AlgoDiameterLine:
			return new EquationDiameterLine(element, scope);

		case AlgoEllipseFociLength:
			return new EquationEllipseFociLength(element, scope);

		case AlgoEllipseFociPoint:
			return new EquationEllipseFociPoint(element, scope);

		case AlgoHyperbolaFociLength:
			return new EquationHyperbolaFociLength(element, scope);

		case AlgoHyperbolaFociPoint:
			return new EquationHyperbolaFociPoint(element, scope);

		case AlgoIntersectConics:
			return new EquationIntersectConicsRestriction(element, (AlgoIntersectConics) algo, scope);

		case AlgoIntersectLineConic:
			return new EquationIntersectLineConicRestriction(element, (AlgoIntersectLineConic) algo, scope);

		case AlgoIntersectLines:
			return new EquationIntersectLinesRestriction(element, (AlgoIntersectLines) algo, scope);

		case AlgoIntersectSingle:
			return new EquationIntersectSingleRestriction(element, (AlgoIntersectSingle) algo, scope);
			
		case AlgoJoinPoints:
			return new EquationJoinPoints(element, scope);
			
		case AlgoJoinPointsRay:
			return new EquationJoinPointsRay(element, scope);

		case AlgoLineBisector:
			return new EquationLineBisector(element, scope);

		case AlgoLineBisectorSegment:
			return new EquationLineBisectorSegment(element, scope);

		case AlgoLinePointLine:
			return new EquationLinePointLine(element, scope);

		case AlgoMidpoint:
			return new EquationMidpointRestriction(element, (AlgoMidpoint) algo, scope);
			
		case AlgoMidpointSegment:
			return new EquationMidpointSegmentRestriction(element, (AlgoMidpointSegment) algo, scope);
			
		case AlgoJoinPointsSegment:
			return new EquationJoinPointsSegment(element, scope);

		case AlgoOrthoLinePointLine:
			return new EquationOrthoLinePointLine(element, scope);

		case AlgoParabolaPointLine:
			return new EquationParabolaPointLine(element, scope);
			
		case AlgoPointOnPath:	
			return new EquationPointOnPathRestriction(element, algo, scope);

		case AlgoPolarLine:
			return new EquationPolarLine(element, scope);

		case AlgoSemicircle:
			return new EquationSemicircle(element, scope);

		case AlgoPolygonRegular:
			return new EquationPolygonRegular(element, scope);

		case AlgoTangentPoint:
			return new EquationTangentPoint(element, scope);

			default:
				return null;
		}
	}

}
