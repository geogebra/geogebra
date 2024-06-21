/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.MyBoolean;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyStringBuffer;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.cas.AlgoDependentCasCell;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.kernelND.Geo3DVecInterface;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.debug.Log;

/**
 * List expression, e.g. with L1 = {3, 2, 1}, L2 = {5, 1, 7} such an expression
 * could be L1 + L2
 */
public class AlgoDependentListExpression extends AlgoElement
		implements DependentAlgo {

	private GeoList list; // output

	/**
	 * Creates new dependent list algo.
	 * 
	 * @param cons
	 *            construction
	 * @param root
	 *            expression deining the list
	 */

	public AlgoDependentListExpression(Construction cons, ExpressionNode root) {
		super(cons);
		list = new GeoList(cons);
		list.setDefinition(root);
		setInputOutput(); // for AlgoElement

		// compute value of dependent list
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.Expression;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		setInputFrom(list.getDefinition());
		setOnlyOutput(list);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the resulting list
	 * 
	 * @return resulting list
	 */
	public GeoList getList() {
		return list;
	}

	/**
	 * Returns the input expression
	 * 
	 * @return input expression
	 */
	@Override
	public ExpressionNode getExpression() {
		return list.getDefinition();
	}

	// evaluate the current value of the arithmetic tree
	@Override
	public final void compute() {

		// https://help.geogebra.org/topic/cannot-open-file-1
		if (list.getParentAlgorithm() instanceof AlgoDependentCasCell) {
			return;
		}

		// get resulting list of ExpressionNodes
		ExpressionValue evlist = list.getDefinition()
				.evaluate(StringTemplate.defaultTemplate);
		MyList myList = (evlist instanceof MyList) ? (MyList) evlist
				: ((GeoList) evlist).getMyList();

		if (!myList.isDefined()) {
			list.setUndefined();
			return;
		}

		list.setDefined(true);

		int evalListSize = myList.size();
		int cachedListSize = list.getCacheSize();

		list.clear();

		for (int i = 0; i < evalListSize; i++) {
			ExpressionValue element = myList.get(i)
					.evaluate(StringTemplate.defaultTemplate);
			GeoElement cached = null;
			if (i < cachedListSize) {
				cached = list.getCached(i);
			}
			GeoElementND geo = toGeo(element, cached, cons);
			if (geo != null) {
				list.add(geo);
			}
		}
	}

	private static GeoElementND toGeo(ExpressionValue element,
			GeoElement cachedGeo, Construction cons) {
		GeoElementND geo = null;

		// number result
		if (element instanceof NumberValue) {
			ExpressionNode definition = element.isGeoElement()
					? ((GeoElement) element).getDefinition() : null;
			double val = element.evaluateDouble();

			// try to use cached element of same type
			if (cachedGeo != null) {
				// the cached element is a number: set value
				if (cachedGeo.isGeoNumeric()) {
					((GeoNumeric) cachedGeo).setValue(val);
					geo = cachedGeo;
				}
			}

			// no cached number: create new one
			if (geo == null) {
				geo = new GeoNumeric(cons, val);
			}
			geo.setDefinition(definition);
			// add number to list
			return geo;
		}

		// point
		else if (element instanceof VectorValue) {
			if (element instanceof MyVecNode) {
				Function fx = isFunction(((MyVecNode) element).getX(), cons);
				Function fy = isFunction(((MyVecNode) element).getY(), cons);
				if (fx != null || fy != null) {
					if (fx == null) {
						fx = new Function(((MyVecNode) element).getX().wrap(),
								fy.getFunctionVariable()
										.deepCopy(cons.getKernel()));
					}
					if (fy == null) {
						fy = new Function(((MyVecNode) element).getY().wrap(),
								fx.getFunctionVariable()
										.deepCopy(cons.getKernel()));
					}
					fx.initFunction();
					fy.initFunction();
					// TODO use parametric processor to allow lines, conics
					GeoCurveCartesian curve = new GeoCurveCartesian(cons,
							fx, fy,
							null);
					cons.removeFromConstructionList(curve);
					curve.setInterval(-10, 10);
					return curve;
				}
			}
			GeoVec2D vec = ((VectorValue) element).getVector();

			// try to use cached element of same type
			if (cachedGeo != null) {
				// the cached element is a point: set value
				if (cachedGeo.isGeoPoint()) {
					((GeoPoint) cachedGeo).setCoords(vec);
					geo = cachedGeo;
				}
			}

			// no cached point: create new one
			if (geo == null) {
				GeoPoint point = new GeoPoint(cons);
				point.setCoords(vec);
				geo = point;
			}

			// add point to list
			return geo;
		}

		// point
		else if (element instanceof Vector3DValue) {
			Geo3DVecInterface vec = ((Vector3DValue) element).getVector();

			// try to use cached element of same type
			if (cachedGeo != null) {
				// the cached element is a point: set value
				if (cachedGeo.isGeoPoint()) {
					((GeoPointND) cachedGeo).setCoords(vec.getX(), vec.getY(),
							vec.getZ(), 1);
					geo = cachedGeo;
				}
			}

			// no cached point: create new one
			if (geo == null) {
				GeoPointND point = cons.getKernel().getGeoFactory().newPoint(3, cons);
				point.setCoords(vec.getX(), vec.getY(), vec.getZ(), 1);
				geo = point;
			}

			// add point to list
			return geo;
		}

		// needed for matrix multiplication
		// eg {{1,3,5},{2,4,6}}*{{11,14},{12,15},{13,a}}
		else if (element instanceof MyList) {
			MyList myList2 = (MyList) element;
			GeoList list2 = new GeoList(cons);
			list2.clear();

			/*
			 * removed Michael Borcherds 20080602 bug: 9PointCubic.ggb (matrix
			 * multiplication) // try to use cached element of type GeoList
			 * GeoList list2 = null; if (i < cachedListSize) { GeoElement
			 * cachedGeo = list.getCached(i);
			 * 
			 * // the cached element is a number: set value if
			 * (cachedGeo.isGeoList()) { list2 = (GeoList) cachedGeo; } }
			 * 
			 * if (list2 == null) { list2 = new GeoList(cons); }
			 */

			for (int j = 0; j < myList2.size(); j++) {
				ExpressionValue en = myList2.get(j);
				ExpressionValue ev = en
						.evaluate(StringTemplate.defaultTemplate);
				GeoElementND geo2 = toGeo(ev, null, cons);
				if (geo2 != null) {
					list2.add(geo2);
				}
			}

			return list2;
		} else if (element instanceof MyStringBuffer) {
			MyStringBuffer str = (MyStringBuffer) element;
			// try to use cached element of same type
			if (cachedGeo != null) {

				// the cached element is a point: set value
				if (cachedGeo.isGeoText()) {
					((GeoText) cachedGeo).setTextString(
							str.toValueString(StringTemplate.defaultTemplate));
					geo = cachedGeo;
				}
			}

			// no cached point: create new one
			if (geo == null) {
				GeoText text = new GeoText(cons);
				text.setTextString(
						str.toValueString(StringTemplate.defaultTemplate));
				geo = text;
			}

			// add point to list
			return geo;
		} else if (element instanceof MyBoolean) {
			MyBoolean bool = (MyBoolean) element;
			// try to use cached element of same type
			if (cachedGeo != null) {

				// the cached element is a point: set value
				if (cachedGeo.isGeoBoolean()) {
					((GeoBoolean) cachedGeo).setValue(bool.getBoolean());
					geo = cachedGeo;
				}
			}

			// no cached point: create new one
			if (geo == null) {
				GeoBoolean geoBool = new GeoBoolean(cons);
				geoBool.setValue(bool.getBoolean());
				geo = geoBool;
			}

			// add point to list
			return geo;
		} else if (element instanceof GeoFunction) {
			GeoFunction fun = (GeoFunction) element;
			return getFunction(fun, cachedGeo);
		} else if (element instanceof GeoText) {
			GeoText text = (GeoText) element;
			if (cachedGeo != null) {

				// the cached element is a point: set value
				if (cachedGeo.isGeoText()) {
					((GeoText) cachedGeo).set(text);
					geo = cachedGeo;
				}
			}

			// no cached text: create new one
			if (geo == null) {
				GeoText geoFun = new GeoText(cons);
				geoFun.set(text);
				geo = geoFun;
			}
			return geo;
		} else if (element instanceof Function) {
			GeoFunction fun = new GeoFunction(cons, (Function) element);
			return getFunction(fun, cachedGeo);
		} else if (element instanceof FunctionNVar) {
			GeoFunctionNVar fun = new GeoFunctionNVar(cons,
					(FunctionNVar) element);
			return toGeo(fun, cachedGeo, cons);
		} else if (element instanceof GeoElement) {
			GeoElement geo0 = (GeoElement) element;
			if (cachedGeo != null) {

				// the cached element is the same type: set value
				if (cachedGeo.getGeoClassType()
						.equals(geo0.getGeoClassType())) {
					cachedGeo.set(geo0);
					geo = cachedGeo;
				}
			}

			// no cached object: create new one
			if (geo == null) {
				geo = geo0.copy();
			}
			return geo;

		} else {
			Log.debug("unsupported list operation: " + element.getClass() + "");
			return null;
		}
	}

	private static Function isFunction(ExpressionValue val, Construction cons) {
		if (val.unwrap() instanceof Function) {
			return (Function) val.unwrap();
		}
		if (val.unwrap() instanceof GeoFunction) {
			return ((GeoFunction) val.unwrap()).getFunction()
					.deepCopy(cons.getKernel());
		}
		if (val.wrap().containsFreeFunctionVariable(null)) {

			FunctionNVar fun = cons.getKernel().getAlgebraProcessor()
					.makeFunctionNVar(val.wrap());
			if (fun instanceof Function) {
				return ((Function) fun).deepCopy(cons.getKernel());
			}
		}
		return null;
	}

	private static GeoElement getFunction(GeoFunction fun, GeoElement cachedGeo) {
		GeoElement geo = null;
		if (cachedGeo != null) {

			// the cached element is a point: set value
			if (cachedGeo.isGeoFunction()) {
				((GeoFunction) cachedGeo).set(fun);
				geo = cachedGeo;
			}
		}

		// no cached point: create new one
		if (geo == null) {
			GeoFunction geoFun = new GeoFunction(fun.getConstruction());
			geoFun.set(fun);
			geo = geoFun;
		}
		return geo;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// was defined as e.g. L = 3 * {a, b, c}
		return list.getDefinition().toString(tpl);
	}

}
