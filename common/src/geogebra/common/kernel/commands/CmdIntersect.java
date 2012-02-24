package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;


/**
 * Intersect[ <GeoLine>, <GeoLine> ] Intersect[ <GeoLine>, <GeoPolygon> ] 
 * Intersect[ <GeoLine>, <GeoConic> ]
 * Intersect[ <GeoConic>, <GeoLine> ] Intersect[ <GeoConic>, <GeoConic> ]
 * Intersect[ <GeoFunction>, <GeoFunction> ] Intersect[ <GeoFunction>, <GeoLine> ]
 * Intersect[ <GeoImplicitPoly>, <GeoImplicitPoly> ] Intersect[ <GeoImplicitPoly>, <GeoLine> ]
 * Intersect[ <GeoImplicitPoly>, <GeoFunction(Polynomial)> ]
 * Intersect[ <GeoFunction>, <GeoFunction>, <NumberValue>, <NumberValue> ]
 */
public class CmdIntersect extends CommandProcessor {
	
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIntersect(Kernel kernel) {
		super(kernel);
	}
	
@Override
public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;
    
//    Application.debug(n,1);

    switch (n) {
        case 2 :
            arg = resArgs(c);
            // Line - Line
            if ((ok[0] = (arg[0] .isGeoLine()))
                && (ok[1] = (arg[1] .isGeoLine()))) {
                GeoElement[] ret =
                    {
                         (GeoElement) kernelA.IntersectLines(
                            c.getLabel(),
                            (GeoLine) arg[0],
                            (GeoLine) arg[1])};
                return ret;
            }
            // Line - PolyLine
            else if ((ok[0] = (arg[0] .isGeoLine()))
            		&& (ok[1] = (arg[1] instanceof GeoPolyLine))) {
                GeoElement[] ret =
                         kernelA.IntersectLinePolyLine(
                            c.getLabels(),
                            (GeoLine) arg[0],
                            (GeoPolyLine) arg[1]);
                return ret;
            } else if ((ok[0] = (arg[0] instanceof GeoPolyLine))
            		&& (ok[1] = (arg[1] .isGeoLine()))) {
                GeoElement[] ret =
                         kernelA.IntersectLinePolyLine(
                            c.getLabels(),
                            (GeoLine) arg[1],
                            (GeoPolyLine) arg[0]);
                return ret;
            }
            // Line - Polygon(as boudary)
            else if ((ok[0] = (arg[0] .isGeoLine()))
            		&& (ok[1] = (arg[1] .isGeoPolygon()))) {
                GeoElement[] ret =
                         kernelA.IntersectLinePolygon(
                            c.getLabels(),
                            (GeoLine) arg[0],
                            (GeoPolygon) arg[1]);
                return ret;
            } else if ((ok[0] = (arg[0] .isGeoPolygon()))
            		&& (ok[1] = (arg[1] .isGeoLine()))) {
                GeoElement[] ret =
                         kernelA.IntersectLinePolygon(
                            c.getLabels(),
                            (GeoLine) arg[1],
                            (GeoPolygon) arg[0]);
                return ret;
            }
            
            // Line - Polygon(as region)
            // ---- see CmdIntersectionPaths

            // Line - Conic
            else if (
                (ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoConic())))
				return kernelA.IntersectLineConic(
                    c.getLabels(),
                    (GeoLine) arg[0],
                    (GeoConic) arg[1]);
			else if (
                (ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isGeoLine())))
				return kernelA.IntersectLineConic(
                    c.getLabels(),
                    (GeoLine) arg[1],
                    (GeoConic) arg[0]);
         // Polynomial - Conic
            else if (
                (ok[0] = (arg[0] .isGeoFunction()))
                    && (ok[1] = (arg[1] .isGeoConic())))
				return kernelA.IntersectPolynomialConic(
                    c.getLabels(),
                    (GeoFunction) arg[0],
                    (GeoConic) arg[1]);
			else if (
                (ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isGeoFunction())))
				return kernelA.IntersectPolynomialConic(
                    c.getLabels(),
                    (GeoFunction) arg[1],
                    (GeoConic) arg[0]);
            // Line - Cubic
            else if (
                (ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isGeoConic())))
				return (GeoElement[]) kernelA.IntersectConics(
                    c.getLabels(),
                    (GeoConic) arg[0],
                    (GeoConic) arg[1]);
			else if (
                (ok[0] = (arg[0] .isGeoFunctionable()))
                    && (ok[1] = (arg[1] .isGeoLine())))
				return kernelA.IntersectPolynomialLine(
                    c.getLabels(),
                    ((GeoFunctionable) arg[0]).getGeoFunction(),
                    (GeoLine) arg[1]);
			else if (
                (ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoFunctionable())))
				return kernelA.IntersectPolynomialLine(
                    c.getLabels(),
                    ((GeoFunctionable) arg[1]).getGeoFunction(),
                    (GeoLine) arg[0]);
			else if ( // check after GeoLine as GeoLine is now GeoFunctionable
	                (ok[0] = (arg[0].isGeoFunctionable()))
	                    && (ok[1] = (arg[1].isGeoFunctionable())))
					return kernelA.IntersectPolynomials(
	                    c.getLabels(),
	                    ((GeoFunctionable) arg[0]).getGeoFunction(),
	                    ((GeoFunctionable) arg[1]).getGeoFunction());
            //implicit Poly - Polynomial
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
	                    && (ok[1] = (arg[1] .isGeoFunctionable())
	                    //&& (ok[1]=((GeoFunctionable) arg[1]).getGeoFunction().isPolynomialFunction(false))
	                    && (ok[1] = !(arg[1].isGeoLine()))))
					return kernelA.IntersectImplicitpolyPolynomial(
	                    c.getLabels(), (GeoImplicitPoly) arg[0],
	                    ((GeoFunctionable) arg[1]).getGeoFunction()
	                   );
			else if ((ok[0] = arg[0] .isGeoFunctionable())
	                    //&& (ok[0]=((GeoFunctionable) arg[0]).getGeoFunction().isPolynomialFunction(false))
						&& (ok[0] = !(arg[0].isGeoLine()))
	                    && (ok[1] = arg[1] .isGeoImplicitPoly()))
					return kernelA.IntersectImplicitpolyPolynomial(
	                    c.getLabels(), (GeoImplicitPoly) arg[1],
	                    ((GeoFunctionable) arg[0]).getGeoFunction()
	                   );
            //implicitPoly - Line
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
	                    && (ok[1] = (arg[1].isGeoLine())))
					return kernelA.IntersectImplicitpolyLine(
	                    c.getLabels(), (GeoImplicitPoly) arg[0],
	                    (GeoLine) arg[1] );
			else if (
	                (ok[1] = (arg[1] .isGeoImplicitPoly()))
	                    && (ok[0] = (arg[0].isGeoLine())))
					return kernelA.IntersectImplicitpolyLine(
	                    c.getLabels(), (GeoImplicitPoly) arg[1],
	                    (GeoLine) arg[0] );
            //implicitPoly - implicitPoly
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
	                    && (ok[1] = (arg[1].isGeoImplicitPoly())))
					return kernelA.IntersectImplicitpolys(
	                    c.getLabels(), (GeoImplicitPoly) arg[0],
	                    (GeoImplicitPoly) arg[1] );
            //implicitPoly-conic
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
                    && (ok[1] = (arg[1].isGeoConic())))
				return kernelA.IntersectImplicitpolyConic(
                    c.getLabels(), (GeoImplicitPoly) arg[0],
                    (GeoConic) arg[1] );
			else if (
	                (ok[1] = (arg[1] .isGeoImplicitPoly()))
                    && (ok[0] = (arg[0].isGeoConic())))
				return kernelA.IntersectImplicitpolyConic(
                    c.getLabels(), (GeoImplicitPoly) arg[1],
                    (GeoConic) arg[0] );
            /* moved to CmdIntersection to allow Intersect[List, List] to intersect list elements in the future
			// intersection of two lists
			else if (arg[0].isGeoList() && arg[1].isGeoList() ) {
				GeoElement[] ret = { 
						kernelA.Intersection(c.getLabel(),
						(GeoList) arg[0], (GeoList)arg[1] ) };
				return ret;
			} */
            
			
            
			else {
                if (!ok[0])
                    throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);
            }

        case 3 : // only one of the intersection points: the third argument
					 // states which one
            arg = resArgs(c);
            // Line - Conic
            if ((ok[0] = (arg[0] .isGeoLine()))
                && (ok[1] = (arg[1] .isGeoConic()))
                && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernelA.IntersectLineConicSingle(
                            c.getLabel(),
                            (GeoLine) arg[0],
                            (GeoConic) arg[1],
                            (NumberValue) arg[2])};
                return ret;
            } else if (
                (ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isGeoLine()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernelA.IntersectLineConicSingle(
                            c.getLabel(),
                            (GeoLine) arg[1],
                            (GeoConic) arg[0],
                            (NumberValue) arg[2])};
                return ret;
            }
            // Line - Conic with startPoint
            else if ((ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoConic()))
                    && (ok[2] = (arg[2] .isGeoPoint()))) {
            	GeoElement[] ret = {
            			kernelA.IntersectLineConicSingle(
                                c.getLabel(),
                                (GeoLine) arg[0],
                                (GeoConic) arg[1],
                                (GeoPoint2) arg[2])};
            	return ret;
            } else if (
                    (ok[0] = (arg[0] .isGeoConic()))
                        && (ok[1] = (arg[1] .isGeoLine()))
                        && (ok[2] = (arg[2] .isGeoPoint()))) {
            	GeoElement[] ret = {
            			kernelA.IntersectLineConicSingle(
                                c.getLabel(),
                                (GeoLine) arg[1],
                                (GeoConic) arg[0],
                                (GeoPoint2) arg[2])};
            	return ret;
            }
         // Conic - Conic with startPoint
            else if ((ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isGeoConic()))
                    && (ok[2] = (arg[2] .isGeoPoint()))) {
            	GeoElement[] ret = {
            			kernelA.IntersectConicsSingle(
                                c.getLabel(),
                                (GeoConic) arg[0],
                                (GeoConic) arg[1],
                                (GeoPoint2) arg[2])};
            	return ret;
            }
            // Conic - Conic
            else if (
                (ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isGeoConic()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernelA.IntersectConicsSingle(
                            c.getLabel(),
                            (GeoConic) arg[0],
                            (GeoConic) arg[1],
                            (NumberValue) arg[2])};
                return ret;
            }
            // Polynomial - Line with index of point
            // check before GeoFunctionable as GeoLine is now GeoFunctionable
            else if (
                (ok[0] = (arg[0] .isGeoFunctionable()))
                    && (ok[1] = (arg[1] .isGeoLine()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernelA.IntersectPolynomialLineSingle(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            (GeoLine) arg[1],
                            (NumberValue) arg[2])};
                return ret;
            }
            // Polynomial - Polynomial with index of point
            else if (
                (ok[0] = (arg[0] .isGeoFunctionable()))
                    && (ok[1] = (arg[1] .isGeoFunctionable()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernelA.IntersectPolynomialsSingle(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            ((GeoFunctionable) arg[1]).getGeoFunction(),
                            (NumberValue) arg[2])};
                return ret;
            }
            // Line - Polynomial with index of point
            else if (
                (ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoFunctionable()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernelA.IntersectPolynomialLineSingle(
                            c.getLabel(),
                            ((GeoFunctionable) arg[1]).getGeoFunction(),
                            (GeoLine) arg[0],
                            (NumberValue) arg[2])};
                return ret;
            }
            //Polynomial - Conic with index of point
            else if (
                    (ok[0] = (arg[0] .isGeoFunction()))
                        && (ok[1] = (arg[1] .isGeoConic()))
                        && (ok[2] = (arg[2] .isNumberValue())) )
    				return new GeoElement[]{kernelA.IntersectPolynomialConicSingle(
                        c.getLabel(),
                        (GeoFunction) arg[0],
                        (GeoConic) arg[1],(NumberValue)arg[2])};
    		else if (
                    (ok[0] = (arg[0] .isGeoConic()))
                        && (ok[1] = (arg[1] .isGeoFunction()))
                        && (ok[2] = (arg[2] .isNumberValue())))
    				return new GeoElement[]{kernelA.IntersectPolynomialConicSingle(
                        c.getLabel(),
                        (GeoFunction) arg[1],
                        (GeoConic) arg[0],(NumberValue)arg[2])};
            //ImplicitPoly - Functionable
    		else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
	                    && (ok[1] = (arg[1] .isGeoFunctionable())
	                    && (ok[1]=((GeoFunctionable) arg[1]).getGeoFunction().isPolynomialFunction(false)))
	                    && (ok[2] = (arg[2] .isNumberValue())))
					return new GeoElement[]{kernelA.IntersectImplicitpolyPolynomialSingle(
	                    c.getLabel(), (GeoImplicitPoly) arg[0],
	                    ((GeoFunctionable) arg[1]).getGeoFunction(),(NumberValue)arg[2]
	                   )};
			else if ((ok[0] =arg[0] .isGeoFunctionable())
	                    && (ok[0]=((GeoFunctionable) arg[0]).getGeoFunction().isPolynomialFunction(false))
	                    && (ok[1] = (arg[1] .isGeoImplicitPoly()))
	                    && (ok[2] = (arg[2] .isNumberValue())))
						return new GeoElement[]{kernelA.IntersectImplicitpolyPolynomialSingle(
			                    c.getLabel(), (GeoImplicitPoly) arg[0],
			                    ((GeoFunctionable) arg[1]).getGeoFunction(),(NumberValue)arg[2]
			                   )};
          //implicitPoly - Line
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
	                    && (ok[1] = (arg[1].isGeoLine()))
	                    && (ok[2] = (arg[2] .isNumberValue())) )
					return new GeoElement[]{kernelA.IntersectImplicitpolyLineSingle(
	                    c.getLabel(), (GeoImplicitPoly) arg[0],
	                    (GeoLine) arg[1] ,(NumberValue)arg[2])};
			else if (
	                (ok[1] = (arg[1] .isGeoImplicitPoly()))
	                    && (ok[0] = (arg[0].isGeoLine()))
	                    && (ok[2] = (arg[2] .isNumberValue())))
				return new GeoElement[]{kernelA.IntersectImplicitpolyLineSingle(
	                    c.getLabel(), (GeoImplicitPoly) arg[1],
	                    (GeoLine) arg[0] ,(NumberValue)arg[2])};
          //implicitPoly - implicitPoly
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
	                    && (ok[1] = (arg[1].isGeoImplicitPoly()))
	                    && (ok[2]=arg[2].isNumberValue()))
					return new GeoElement[]{kernelA.IntersectImplicitpolysSingle(
	                    c.getLabel(), (GeoImplicitPoly) arg[0],
	                    (GeoImplicitPoly) arg[1], (NumberValue) arg[2] )};
            //implicitPoly-conic
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
                    && (ok[1] = (arg[1].isGeoConic()))
                    && (ok[2]=arg[2].isNumberValue()))
				return new GeoElement[]{kernelA.IntersectImplicitpolyConicSingle(
                    c.getLabel(), (GeoImplicitPoly) arg[0],
                    (GeoConic) arg[1], (NumberValue) arg[2] )};
			else if (
	                (ok[1] = (arg[1] .isGeoImplicitPoly()))
                    && (ok[0] = (arg[0].isGeoConic()))
                    && (ok[2]=arg[2].isNumberValue()))
				return new GeoElement[]{kernelA.IntersectImplicitpolyConicSingle(
                    c.getLabel(), (GeoImplicitPoly) arg[1],
                    (GeoConic) arg[0], (NumberValue) arg[2] )};
            // Function - Line with startPoint
            // check before GeoFunctionable as GeoLine is now GeoFunctionable
            else if (
                (ok[0] = (arg[0] .isGeoFunctionable()))
                    && (ok[1] = (arg[1] .isGeoLine()))
                    && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernelA.IntersectFunctionLine(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            (GeoLine) arg[1],
                            (GeoPoint2) arg[2])};
                return ret;
            }
            // Function - Function with startPoint
            else if (
                (ok[0] = (arg[0] .isGeoFunctionable()))
                    && (ok[1] = (arg[1] .isGeoFunctionable()))
                    && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernelA.IntersectFunctions(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            ((GeoFunctionable) arg[1]).getGeoFunction(),
                            (GeoPoint2) arg[2])};
                return ret;
            }
            // Line - Function with startPoint
            else if (
                (ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoFunctionable()))
                    && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernelA.IntersectFunctionLine(
                            c.getLabel(),
                            ((GeoFunctionable) arg[1]).getGeoFunction(),
                            (GeoLine) arg[0],
                            (GeoPoint2) arg[2])};
                return ret;
            }
            // Syntax Error
            else {
                if (!ok[0])
                    throw argErr(app, c.getName(), arg[0]);
                else if (!ok[1])
                    throw argErr(app, c.getName(), arg[1]);
                else
                    throw argErr(app, c.getName(), arg[2]);
            }
            
        case 4:
            arg = resArgs(c);
            // Function - Function in interval [a,b]
            // Polynomial - Polynomial with index of point
            if (
                (ok[0] = (arg[0] .isGeoFunction()))
                    && (ok[1] = (arg[1] .isGeoFunction()))
                    && (ok[2] = (arg[2] .isNumberValue()))
                    && (ok[3] = (arg[3] .isNumberValue()))
                    ) {
                GeoElement[] ret =
                         kernelA.IntersectFunctions(
                            c.getLabels(),
                            (GeoFunction) arg[0],
                            (GeoFunction) arg[1],
                            (NumberValue) arg[2],
                            (NumberValue) arg[3]
                         );
                return ret;
            }
		throw argErr(app, c.getName(), getBadArg(ok,arg));
		
        default :
            throw argNumErr(app, c.getName(), n);
    }
}
}