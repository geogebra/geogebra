package geogebra.common.geogebra3D.kernel3D.arithmetic;

import geogebra.common.geogebra3D.kernel3D.geos.Geo3DVec;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.VectorNDValue;
import geogebra.common.kernel.arithmetic.VectorValue;
import geogebra.common.kernel.arithmetic3D.Vector3DValue;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.main.Localization;
import geogebra.common.plugin.Operation;

/**
 * @author ggb3D
 * 
 *         Evaluator for ExpressionNode (used in ExpressionNode.evaluate()) in
 *         3D mode
 * 
 */
public class ExpressionNodeEvaluator3D extends ExpressionNodeEvaluator {

	/**
	 * @param l10n localization for errors
	 */
	public ExpressionNodeEvaluator3D(Localization l10n) {
		super(l10n);
	}

	@Override
	public ExpressionValue handleOp(Operation op, ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
			ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX){ // right tree
		MyDouble num;
		switch (op) {
		/*
		 * ARITHMETIC operations
		 */
		case PLUS:
			// 3D vector + 3D vector
			if (lt instanceof Vector3DValue) {
				if (rt instanceof Vector3DValue) {
					Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) lt).getVector();
					Geo3DVec.add(vec3D,
							(Geo3DVec) ((Vector3DValue) rt).getVector(), vec3D);
					return vec3D;
				} else if (rt instanceof VectorValue) {
					Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) lt).getVector();
					Geo3DVec.add(vec3D,
							((VectorValue) rt).getVector(), vec3D);
					return vec3D;
				}
			} else if (lt instanceof VectorValue && rt instanceof Vector3DValue) {
				Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) rt).getVector();
				Geo3DVec.add(((VectorValue) lt).getVector(), vec3D,
						vec3D);
				return vec3D;
			}
			break;

		case MINUS:
			// 3D vector - 3D vector
			if (lt instanceof Vector3DValue) {
				if (rt instanceof Vector3DValue) {
					Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) lt).getVector();
					Geo3DVec.sub(vec3D,
							(Geo3DVec) ((Vector3DValue) rt).getVector(), vec3D);
					return vec3D;
				} else if (rt instanceof VectorValue) {
					Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) lt).getVector();
					Geo3DVec.sub(vec3D,
							((VectorValue) rt).getVector(), vec3D);
					return vec3D;
				}
			} else if (lt instanceof VectorValue && rt instanceof Vector3DValue) {
				Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) rt).getVector();
				Geo3DVec.sub(((VectorValue) lt).getVector(), vec3D,
						vec3D);
				return vec3D;
			}
			break;

		case DIVIDE:
			if (rt instanceof NumberValue) {
				// number * 3D vector
				if (lt instanceof Vector3DValue) {
					Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) lt).getVector();
					Geo3DVec.div(vec3D, ((NumberValue) rt).getDouble(), vec3D);
					return vec3D;
				}
			}
			break;

		case POWER:
			if (lt instanceof Vector3DValue && rt instanceof NumberValue) {
				num = ((NumberValue) rt).getNumber();
				Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) lt).getVector();
				if (num.getDouble() == 2.0) {
					Geo3DVec.inner(vec3D, vec3D, num);
				} else {
					num.set(Double.NaN);
				}
				return num;
			}
			break;

		case VECTORPRODUCT:
			// 3D vector * 3D Vector (inner/dot product)
			if (lt instanceof Vector3DValue && rt instanceof Vector3DValue) {
				Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) lt).getVector();
				Geo3DVec.vectorProduct(vec3D,
						(Geo3DVec) ((Vector3DValue) rt).getVector(), vec3D);
				return vec3D;
			}
		}
		
		return super.handleOp(op, lt, rt, left, right, tpl, holdsLaTeX);

	}
	
	
	
	@Override
	protected ExpressionValue multiply(NumberValue en, VectorNDValue ev){
		
		if (ev instanceof Vector3DValue){
			Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) ev).getVector();
			Geo3DVec.mult(vec3D, en.getDouble(), vec3D);
			return vec3D;
		}
		
		return super.multiply(en, ev);
	}
	
	@Override
	protected ExpressionValue innerProduct(VectorNDValue ev1, VectorNDValue ev2, Kernel kernel){
		
		if (ev1 instanceof Vector3DValue || ev2 instanceof Vector3DValue) { 
			MyDouble num = new MyDouble(kernel);
			Geo3DVec.inner(ev1.getVector(), ev2.getVector(), num);
			return num;
		}
			
		// 2D vec * 2D vec
		return super.innerProduct(ev1, ev2, kernel);
	}
	
	@Override
	protected ExpressionValue complexMult(VectorNDValue ev1, VectorNDValue ev2, Kernel kernel){
		
		if (ev1 instanceof Vector3DValue || ev2 instanceof Vector3DValue) { 
			GeoVec2D vec = new GeoVec2D(kernel);
			Geo3DVec.complexMultiply(ev1.getVector(), ev2.getVector(), vec);
			return vec;
		}

		
		// 2D vec * 2D vec
		return super.complexMult(ev1, ev2, kernel);
	}
	
}
