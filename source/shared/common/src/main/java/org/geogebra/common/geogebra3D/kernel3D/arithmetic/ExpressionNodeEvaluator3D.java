package org.geogebra.common.geogebra3D.kernel3D.arithmetic;

import org.geogebra.common.geogebra3D.kernel3D.geos.Geo3DVec;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

/**
 * @author ggb3D
 * 
 *         Evaluator for ExpressionNode (used in ExpressionNode.evaluate()) in
 *         3D mode
 * 
 */
public class ExpressionNodeEvaluator3D extends ExpressionNodeEvaluator {

	/**
	 * Creates a new expression node evaluator.
	 *
	 * @param l10n
	 *            localization for errors
	 * @param kernel
	 *            kernel
	 */
	public ExpressionNodeEvaluator3D(Localization l10n, Kernel kernel) {
		super(l10n, kernel);
	}

	@Override
	public ExpressionValue handleOp(Operation op, ExpressionValue lt,
			ExpressionValue rt, ExpressionValue left, ExpressionValue right,
			StringTemplate tpl, boolean holdsLaTeX) { // right tree
		MyDouble num;
		switch (op) {
		/*
		 * ARITHMETIC operations
		 */
		case PLUS:
			// 3D vector + 3D vector
			if (lt instanceof Vector3DValue) {
				if (rt instanceof Vector3DValue) {
					Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) lt)
							.getVector();
					Geo3DVec.add(vec3D,
							(Geo3DVec) ((Vector3DValue) rt).getVector(), vec3D);
					return vec3D;
				} else if (rt instanceof VectorValue) {
					Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) lt)
							.getVector();
					Geo3DVec.add(vec3D, ((VectorValue) rt).getVector(), vec3D);
					return vec3D;
				}
			} else if (lt instanceof VectorValue
					&& rt instanceof Vector3DValue) {
				Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) rt).getVector();
				Geo3DVec.add(vec3D, ((VectorValue) lt).getVector(), vec3D);
				return vec3D;
			}
			break;

		case MINUS:
			// 3D vector - 3D vector
			if (lt instanceof Vector3DValue) {
				if (rt instanceof Vector3DValue) {
					Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) lt)
							.getVector();
					Geo3DVec.sub(vec3D,
							(Geo3DVec) ((Vector3DValue) rt).getVector(), vec3D);
					return vec3D;
				} else if (rt instanceof VectorValue) {
					Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) lt)
							.getVector();
					Geo3DVec.sub(vec3D, ((VectorValue) rt).getVector(), vec3D);
					return vec3D;
				}
			} else if (lt instanceof VectorValue
					&& rt instanceof Vector3DValue) {
				Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) rt).getVector();
				Geo3DVec.sub(((VectorValue) lt).getVector(), vec3D, vec3D);
				return vec3D;
			}
			break;

		case DIVIDE:
			if (rt instanceof NumberValue) {
				// number * 3D vector
				if (lt instanceof Vector3DValue) {
					Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) lt)
							.getVector();
					Geo3DVec.div(vec3D, rt.evaluateDouble(), vec3D);
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

		}

		return super.handleOp(op, lt, rt, left, right, tpl, holdsLaTeX);

	}

	@Override
	protected ExpressionValue multiply(NumberValue en, VectorNDValue ev) {

		if (ev instanceof Vector3DValue) {
			Geo3DVec vec3D = (Geo3DVec) ((Vector3DValue) ev).getVector();
			Geo3DVec.mult(vec3D, en.getDouble(), vec3D);
			return vec3D;
		}

		return super.multiply(en, ev);
	}

	@Override
	protected ExpressionValue innerProduct(VectorNDValue ev1, VectorNDValue ev2,
			Kernel kernel1) {

		if (ev1 instanceof Vector3DValue || ev2 instanceof Vector3DValue) {
			MyDouble num = new MyDouble(kernel1);
			Geo3DVec.inner(ev1.getVector(), ev2.getVector(), num);
			return num;
		}

		// 2D vec * 2D vec
		return super.innerProduct(ev1, ev2, kernel1);
	}

	@Override
	protected ExpressionValue complexMult(VectorNDValue ev1, VectorNDValue ev2,
			Kernel kernel1) {

		if (ev1 instanceof Vector3DValue || ev2 instanceof Vector3DValue) {
			GeoVec2D vec = new GeoVec2D(kernel1);
			Geo3DVec.complexMultiply(ev1.getVector(), ev2.getVector(), vec);
			return vec;
		}

		// 2D vec * 2D vec
		return super.complexMult(ev1, ev2, kernel1);
	}

	@Override
	protected ExpressionValue vectorProduct(VectorNDValue v1,
			VectorNDValue v2) {

		if (v1.getToStringMode() == Kernel.COORD_CARTESIAN_3D
				|| v1.getToStringMode() == Kernel.COORD_SPHERICAL
				|| v2.getToStringMode() == Kernel.COORD_CARTESIAN_3D
				|| v2.getToStringMode() == Kernel.COORD_SPHERICAL) {

			// 3D vector product
			Geo3DVec vec3D = new Geo3DVec(this.kernel);
			Geo3DVec.vectorProduct(v1.getVector(), v2.getVector(), vec3D);
			return vec3D;

		}

		// 2D vector product (number)
		return super.vectorProduct(v1, v2);
	}

	@Override
	protected ExpressionValue multiply(MyList myList, VectorNDValue rt) {

		if (!myList.isMatrix()) {
			return null;
		}

		int rows = myList.getMatrixRows();
		int cols = myList.getMatrixCols();

		// 2D coords
		if (rt.getToStringMode() != Kernel.COORD_CARTESIAN_3D
				&& rt.getToStringMode() != Kernel.COORD_SPHERICAL) {
			if (rows == 3 && cols == 2) { // creates 3D vector from 2D coords
				Geo3DVec myVec = new Geo3DVec(this.kernel);
				// 3x2 matrix * 3D vector / point
				myVec.multiplyMatrix3x2(myList, rt);
				return myVec;
			}

			if (rt instanceof VectorValue) { // 2D vector / point
				return multiply2D(myList, rows, cols, (VectorValue) rt);
			}

			// 3D vector / point
			GeoVec2D myVec = new GeoVec2D(this.kernel);
			return multiply2D(myList, rows, cols, rt, myVec);

		}

		// 3D coords
		if (cols == 3) {
			if (rows == 3) { // creates 3D vector/point
				Geo3DVec myVec = new Geo3DVec(this.kernel);
				// 3x3 matrix * 3D vector / point
				myVec.multiplyMatrix3x3(myList, rt);
				return myVec;
			}

			if (rows == 2) { // creates 2D vector/point
				GeoVec2D myVec = new GeoVec2D(this.kernel);
				// 2x3 matrix * 3D vector / point
				Geo3DVec.multiplyMatrix(myList, rt, myVec);
				return myVec;
			}

		} else if (cols == 4) {
			if (rows == 4) { // affine multiplication
				Geo3DVec myVec = new Geo3DVec(this.kernel);
				// 3x3 matrix * 3D vector / point
				myVec.multiplyMatrix4x4(myList, rt);
				return myVec;
			}
		}

		return null;
	}

}
