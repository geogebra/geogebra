/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.cas.UsesCAS;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;


/**
 * Algo for intersection of a curve with a curve
 * 
 * either uses CAS Solve[] to get all points, or NSolve for one
 * 
 * adapted from AlgoIntersectLineCurve
 * @author Michael
 */
public class AlgoIntersectCurveCurve extends AlgoIntersectLineCurve implements UsesCAS {

	private GeoCurveCartesian curve2;
	private GeoNumberValue t1, t2;
	
	// numeric = false is too slow on MPReduce (ggb42). OK to set false for Giac
	private boolean numeric = false;
	
	/** 
	 * common constructor
	 * @param c Construction
	 * @param labels labels
	 * @param c1 curve 1
	 * @param c2 curve 2

	 */
	public AlgoIntersectCurveCurve(Construction c, String[] labels, GeoCurveCartesian c1, GeoCurveCartesian c2) {

		super(c);

		outputPoints=createOutputPoints();
				
		this.curve = c1;
		this.curve2 = c2;

		compute();

		setInputOutput(); // for AlgoElement

		setLabels(labels);

		update();    
	}

	/**
	 * @param c Construction
	 * @param labels labels
	 * @param c1 curve 1
	 * @param c2 curve 2
	 * @param t1 path parameter to start iteration from
	 * @param t2 path parameter to start iteration from
	 */
	public AlgoIntersectCurveCurve(Construction c, String[] labels, GeoCurveCartesian c1, GeoCurveCartesian c2, GeoNumberValue t1, GeoNumberValue t2) {

		super(c);

		outputPoints=createOutputPoints();
				
		this.curve = c1;
		this.curve2 = c2;
		
		this.t1 = t1;
		this.t2 = t2;
		numeric = true;

		compute();

		setInputOutput(); // for AlgoElement

		setLabels(labels);

		update();    
	}

	/**
	 * 
	 * @return handler for output points
	 */
	@Override
	protected OutputHandler<GeoElement> createOutputPoints(){
		return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoPoint newElement() {
				GeoPoint p=new GeoPoint(cons);
				p.setCoords(0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectCurveCurve.this);
				return p;
			}
		});
	}

	@Override
	public Commands getClassName() {
        return Commands.Intersect;
    }

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		
		if (t1 != null) {
			input = new GeoElement[4];
			input[2] = t1.toGeoElement();
			input[3] = t2.toGeoElement();					
		} else {
			input = new GeoElement[2];			
		}

		input[0] = curve;
		input[1] = curve2;

		setDependencies(); // done by AlgoElement
	}

	@Override
	public void compute() { 	

		String fv1 = curve.getFunX().getFunctionVariable().toString(StringTemplate.defaultTemplate);
		String fv2 = curve2.getFunX().getFunctionVariable().toString(StringTemplate.defaultTemplate);
		
		// toString() returns with variables in
		// for most cases, toValueString() with values substituted is better because:
		// * can get two values returned, eg Solve[{3 + t - d=t2^2 - 1,7 / 2 - t=t2^2 - 2t2},{t,t2}] when d=8
		// * can get equations that we can't solve exactly
		
		// use StringTemplate that gives 3 not 3.00000000000000
		String c1X = curve.getFunX().toValueString(StringTemplate.fullFigures(StringType.GEOGEBRA_XML));
		String c1Y = curve.getFunY().toValueString(StringTemplate.fullFigures(StringType.GEOGEBRA_XML));
		String c2X = curve2.getFunX().toValueString(StringTemplate.fullFigures(StringType.GEOGEBRA_XML));
		String c2Y = curve2.getFunY().toValueString(StringTemplate.fullFigures(StringType.GEOGEBRA_XML));
		
		// it's likely that both curves have parameter 't'
		if (fv1.equals(fv2)) {
			fv2 = fv2+"2"; // eg t -> t2
			c2X = c2X.replaceAll(fv1, fv2);
			c2Y = c2Y.replaceAll(fv1, fv2);
		}
		
		
		
		//App.debug(c1X);
		//App.debug(c1Y);
		//App.debug(c2X);
		//App.debug(c2Y);
		
		// construct CAS command, eg
		// Solve[{1 + t= 1 + 3, -1 + 2= 3 - 2},{t,t2}]
		// Solve[{3 + t - d=t2^2 - 1,7 / 2 - t=t2^2 - 2t2},{t,t2}]
		
		StringBuilder sb = new StringBuilder();
		if (numeric) {
			// NSolve, not Solve
			sb.append('N');
		}
		sb.append("Solve[{");
		sb.append(c1X);
		sb.append('=');
		sb.append(c2X);
		sb.append(',');
		sb.append(c1Y);
		sb.append('=');
		sb.append(c2Y);
		sb.append("},{");
		sb.append(fv1);
		if (numeric) {
			// start value for iteration
			sb.append('=');
			if (t1 != null) {
				sb.append(t1.toValueString(StringTemplate.casTemplate));
			} else {
				sb.append((curve.getMinParameter() + curve.getMaxParameter())/2);
			}
		}
		sb.append(',');
		sb.append(fv2);
		if (numeric) {
			// start value for iteration
			sb.append('=');
			if (t2 != null) {
				sb.append(t2.toValueString(StringTemplate.casTemplate));
			} else {
				sb.append((curve2.getMinParameter() + curve2.getMaxParameter())/2);
			}
		}
		sb.append("}]");
		
		//App.debug(sb.toString());

		String result = "";
		try {
			result  = kernel.evaluateGeoGebraCAS(sb.toString(), null);
		} catch (Throwable e) {
			//other points are undefined
			for(int i = 0 ; i < outputPoints.size(); i++) {
				//App.debug("setting undefined "+i);
				outputPoints.getElement(i).setUndefined();
			}

			e.printStackTrace();
			return;
		}
		
		// make sure output is the same form
		if (numeric) {
			result = "{"+result+"}";
		}
		
		// eg {{ t = 3 / 2,  t2 = 1 / 2}}
		//App.debug(result);
		
		//App.debug(kernel.getGeoGebraCAS().getCASparser().parseGeoGebraCASInputAndResolveDummyVars(result).evaluate(StringTemplate.maxPrecision));
		//App.debug(kernel.getGeoGebraCAS().getCASparser().parseGeoGebraCASInput(result).evaluate(StringTemplate.maxPrecision));

		// result can have eg 1/2 or sqrt(5) in so needs parsing
		AlgebraProcessor ap = kernel.getAlgebraProcessor();

		int index = 0;
		int firstBrace = result.indexOf("{");		
		int currentBrace = result.indexOf("{", firstBrace + 1);
		
		
		while (currentBrace > -1) {
			int nextComma = result.indexOf(",", currentBrace + 1);
			int nextCloseBrace = result.indexOf("}", currentBrace + 1);
			
			if (nextComma > -1 && nextCloseBrace > -1) {
				

				outputPoints.adjustOutputSize(index + 1);
				GeoPoint point = (GeoPoint) outputPoints.getElement(index);
				index++;

				
				// eg t=3/2
				String s1 = result.substring(currentBrace + 1, nextComma).replaceAll(" ", "");
				// eg t2=1/2
				String s2 = result.substring(nextComma + 1, nextCloseBrace).replaceAll(" ", "");

				//App.debug(ap.evaluateToDouble(s1.substring(fv1.length() + 1), true));
				//App.debug(ap.evaluateToDouble(s2.substring(fv2.length() + 1), true));

				if (s1.startsWith(fv1+"=") && s2.startsWith(fv2+"=")) {
					double p1 = ap.evaluateToDouble(s1.substring(fv1.length() + 1), true);
					double p2 = ap.evaluateToDouble(s2.substring(fv2.length() + 1), true);
					
					//App.debug(p1+" "+ curve.getMinParameter()+" "+curve.getMaxParameter());
					//App.debug(p2+" "+ curve2.getMinParameter()+" "+curve2.getMaxParameter());
					
					checkPointInRange(p1,  p2,  point);
					


				} else if (s1.startsWith(fv2+"=") && s2.startsWith(fv1+"=")) {
					double p2 = ap.evaluateToDouble(s1.substring(fv2.length() + 1), true);
					double p1 = ap.evaluateToDouble(s2.substring(fv1.length() + 1), true);
					
					//App.debug(t1+" "+ curve1.getMinParameter()+" "+curve1.getMaxParameter());
					//App.debug(t2+" "+ curve2.getMinParameter()+" "+curve2.getMaxParameter());
					
					checkPointInRange(p1,  p2,  point);
					
				} else {
					App.debug("problem: "+s1+" "+s2);
					point.setUndefined();
				}
				
				// if this is -1, we're done so finish while() loop
				currentBrace = result.indexOf("{", currentBrace + 1);
			} else { 
				// something's gone wrong
				App.debug("problem with result");
				currentBrace = -1;
			}
		}

		//App.debug(index+" "+outputPoints.size());
		
		//other points are undefined
		for(; index<outputPoints.size(); index++) {
			//App.debug("setting undefined "+index);
			outputPoints.getElement(index).setUndefined();
		}
	}

	private void checkPointInRange(double p1, double p2, GeoPoint point) {
		// check parameters in range
		if (Kernel.isGreaterEqual(p1, curve.getMinParameter())
				&& Kernel.isGreaterEqual(curve.getMaxParameter(), p1)
				&& Kernel.isGreaterEqual(p2, curve2.getMinParameter())
				&& Kernel.isGreaterEqual(curve2.getMaxParameter(), p2)) {

			double x = curve.getFunX().evaluate(p1);
			double y = curve.getFunY().evaluate(p1);
			//App.debug("in range: ("+x+", "+y+")");

			point.setCoords(x, y, 1.0);

		} else {
			point.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return loc.getPlain("IntersectionPointOfAB",((GeoElement) curve).getLabel(tpl),
				((GeoElement)curve2).getLabel(tpl));
	}  

	// TODO Consider locusequability

}
