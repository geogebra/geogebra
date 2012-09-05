/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoIntersectLines.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.PolyFunction;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.plugin.Operation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * Algo for intersection of a line with a curve
 * adapted from AlgoIntersectLinePolyLine
 * @author Michael
 * @version 
 */
public class AlgoIntersectLineCurve extends AlgoElement{

	protected GeoLine line; // input
	protected GeoCurveCartesian curve;
	protected OutputHandler<GeoElement> outputPoints; // output

	/** 
	 * common constructor
	 * @param c Construction
	 * @param labels labels
	 * @param l line
	 * @param p curve
	 */
	public AlgoIntersectLineCurve(Construction c, String[] labels, GeoLine l, GeoCurveCartesian p) {

		super(c);

		outputPoints=createOutputPoints();

		this.line = l;
		this.curve = p;

		compute();

		setInputOutput(); // for AlgoElement

		setLabels(labels);

		update();    
	}

	private void setLabels(String[] labels) {
		//if only one label (e.g. "A") for more than one output, new labels will be A_1, A_2, ...
		if (labels!=null &&
				labels.length==1 &&
				//outputPoints.size() > 1 &&
				labels[0]!=null &&
				!labels[0].equals("")) {
			outputPoints.setIndexLabels(labels[0]);
		} else {

			outputPoints.setLabels(labels);
			outputPoints.setIndexLabels(outputPoints.getElement(0).getLabel(StringTemplate.defaultTemplate));
		}	
	}

	/**
	 * 
	 * @return handler for output points
	 */
	protected OutputHandler<GeoElement> createOutputPoints(){
		return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoPoint newElement() {
				GeoPoint p=new GeoPoint(cons);
				p.setCoords(0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectLineCurve.this);
				return p;
			}
		});
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoIntersectLineCurve;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_INTERSECT;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = line;
		input[1] = curve;

		setDependencies(); // done by AlgoElement
	}

	@Override
	public void compute() { 	

		Coords coeffs = line.getCoords();

		ExpressionNode xFun = curve.getFunX().getExpression();
		ExpressionNode yFun = curve.getFunY().getExpression();

		FunctionVariable fv = curve.getFunX().getFunctionVariable();

		// substitute x = x(t), y=y(t) into
		// ax + by + c
		// Normalizing to (a/c)x + (b/c)y + 1 seems to work better
		ExpressionNode enx = new ExpressionNode(kernel, new MyDouble(kernel, coeffs.getX() / coeffs.getZ()), Operation.MULTIPLY, xFun);
		ExpressionNode eny = new ExpressionNode(kernel, new MyDouble(kernel, coeffs.getY() / coeffs.getZ()), Operation.MULTIPLY, yFun);
		enx = enx.plus(eny).plus(1);

		// wrap in a function
		Function f = new Function(enx, fv);  		
		GeoFunction geoFun = new GeoFunction(cons, f);

		double[] roots = null;
		int outputSize = -1;
		
		ArrayList<Double> polyRoots = new ArrayList<Double>();

		if (geoFun.isPolynomialFunction(true)) {
			//AbstractApplication.debug("trying polynomial");

			LinkedList<PolyFunction> factorList = f.getPolynomialFactors(false);

			if (factorList != null) {
				// compute the roots of every single factor
				Iterator<PolyFunction> it = factorList.iterator();
				while (it.hasNext()) {
					PolyFunction polyFun = it.next();

					if (polyFun.updateCoeffValues()) {
						// now let's compute the roots of this factor
						// compute all roots of polynomial polyFun
						roots = polyFun.getCoeffsCopy();
						int n = cons.getKernel().getEquationSolver().polynomialRoots(roots, true);
						
						for (int i = 0 ; i < n ; i++) {
							polyRoots.add(roots[i]);
						}
					} else {
						outputSize = -1;
						break;
					}

				}
			}

		}
		
		if (polyRoots.size() > 0) {
			
			outputSize = polyRoots.size();
			
			roots = new double[outputSize];
			
			for (int i = 0 ; i < outputSize ; i++) {
				roots[i] = polyRoots.get(i);
			}
		} else {
			// polynomial method hasn't worked
			//AbstractApplication.debug("trying non-polynomial");

			// solve a x(t) + b y(t) + c = 0 (for t)
			roots = AlgoRoots.findRoots(geoFun, curve.getMinParameter(), curve.getMaxParameter(), 100);

			outputSize = roots == null || roots.length == 0 ? 1 : roots.length;
			
		}

		//update and/or create points
		outputPoints.adjustOutputSize(outputSize);

		//affect new computed points
		int index = 0;
		if (roots != null) {
			for (index = 0 ; index < outputSize ; index++) {
				double paramVal = roots[index];
				GeoPoint point = (GeoPoint) outputPoints.getElement(index);

				// substitute parameter back into curve to get cartesian coords
				fv.set(paramVal);
				point.setCoords(xFun.evaluateNum().getDouble(), yFun.evaluateNum().getDouble(), 1.0);
				//AbstractApplication.debug(xFun.evaluateNum().getDouble()+","+ yFun.evaluateNum().getDouble());
			}	
		}

		//other points are undefined
		for(; index<outputPoints.size(); index++) {
			//AbstractApplication.debug("setting undefined "+index);
			outputPoints.getElement(index).setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return app.getPlain("IntersectionPointOfAB",((GeoElement) line).getLabel(tpl),
				((GeoElement)curve).getLabel(tpl));
	}  

	// TODO Consider locusequability

}
