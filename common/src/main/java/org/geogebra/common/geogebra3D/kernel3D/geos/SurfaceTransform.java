package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class SurfaceTransform {

	public static void transform(FunctionNVar[] fun, Kernel kernel,
			CoordMatrix4x4 m) {
		// current expressions
		ExpressionNode[] expr = new ExpressionNode[3];
		for (int i = 0; i < 3; i++) {
			expr[i] = fun[i].deepCopy(kernel).getExpression();
		}

		for (int row = 0; row < 3; row++) {
			MyDouble[] coeff = new MyDouble[4];
			for (int i = 0; i < 4; i++) {
				coeff[i] = new MyDouble(kernel, m.get(row + 1, i + 1));
			}

			ExpressionNode trans = new ExpressionNode(kernel, coeff[3]);
			for (int i = 0; i < 3; i++) {
				trans = trans.plus(expr[i].multiply(coeff[i]));
			}

			fun[row].setExpression(trans);
		}

	}

	public static void mirror(FunctionNVar[] fun, Kernel kernel,
			GeoCoordSys2D plane) {
		CoordMatrix4x4 m = plane.getCoordSys().getMatrixOrthonormal();
		transform(fun, kernel,
				CoordMatrix4x4.PlaneSymetry(m.getVz(), m.getOrigin()));

	}

	public static void mirror(FunctionNVar[] fun, Kernel kernel,
			GeoLineND line) {
		transform(fun, kernel,
				CoordMatrix4x4.AxialSymetry(
						line.getDirectionInD3().normalized(),
						line.getStartInhomCoords()));

	}

	public static void rotate(FunctionNVar[] fun, Kernel kernel, NumberValue r,
			GeoPointND s, CoordMatrix4x4 tmpMatrix4x4) {
		CoordMatrix4x4.Rotation4x4(r.getDouble(), s.getInhomCoordsInD3(),
				tmpMatrix4x4);
		transform(fun, kernel, tmpMatrix4x4);

	}

	public static void rotate(FunctionNVar[] fun, Kernel kernel, NumberValue r,
			CoordMatrix4x4 tmpMatrix4x4) {
		CoordMatrix4x4.Rotation4x4(r.getDouble(), tmpMatrix4x4);
		transform(fun, kernel, tmpMatrix4x4);

	}

	public static void rotate(FunctionNVar[] fun, Kernel kernel, NumberValue r,
			GeoPointND s, GeoDirectionND orientation,
			CoordMatrix4x4 tmpMatrix4x4) {
		CoordMatrix4x4.Rotation4x4(orientation.getDirectionInD3().normalized(),
				r.getDouble(), s.getInhomCoordsInD3(), tmpMatrix4x4);
		transform(fun, kernel, tmpMatrix4x4);

	}

	public static void rotate(FunctionNVar[] fun, Kernel kernel, NumberValue r,
			GeoLineND line, CoordMatrix4x4 tmpMatrix4x4) {
		CoordMatrix4x4.Rotation4x4(line.getDirectionInD3().normalized(),
				r.getDouble(), line.getStartInhomCoords(), tmpMatrix4x4);
		transform(fun, kernel, tmpMatrix4x4);

	}

}
