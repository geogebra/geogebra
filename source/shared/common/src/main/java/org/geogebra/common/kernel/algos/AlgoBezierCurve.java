package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoBezierCurve extends AlgoElement {
	private final GeoCurveCartesian result;
	private final GeoPointND[] points;
	private MyDouble[] xCoords = new MyDouble[4];
	private MyDouble[] yCoords = new MyDouble[4];

	/**
	 * @param c construction
	 * @param start start point
	 * @param control1 first control point
	 * @param control2 second control point
	 * @param end end point
	 */
	public AlgoBezierCurve(Construction c, GeoPointND start, GeoPointND control1,
			GeoPointND control2, GeoPointND end) {
		super(c);
		this.points = new GeoPointND[] {start, control1, control2, end};
		FunctionVariable fvar = new FunctionVariable(kernel, "t");
		for (int i = 0; i < 4; i++) {
			xCoords[i] = new MyDouble(kernel, 2);
			yCoords[i] = new MyDouble(kernel, 2);
		}
		Function curveX = buildFunction(xCoords, fvar);
		Function curveY = buildFunction(yCoords, fvar);
		result = new GeoCurveCartesian(c, curveX, curveY, null);
		result.setInterval(0, 1);
		setInputOutput();
		compute();
	}

	private Function buildFunction(MyDouble[] coords, FunctionVariable fvar) {
		ExpressionNode oneMinusT = new ExpressionNode(kernel, 1).subtract(fvar);
		return new Function(
				fvar.wrap().power(3).multiply(coords[0])
						.plus(fvar.wrap().power(2).multiply(oneMinusT).multiply(coords[1]))
						.plus(fvar.wrap().multiply(oneMinusT.power(2)).multiply(coords[2]))
						.plus(oneMinusT.power(3).multiply(coords[3])),
				fvar);
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[points.length];
		for (int i = 0; i < points.length; i++) {
			input[i] = points[i].toGeoElement();
		}
		setOnlyOutput(result);
		setDependencies();
	}

	@Override
	public void compute() {
		for (int i = 0; i < points.length; i++) {
			double coeff = i > 0 && i < 3 ? 3 : 1;
			xCoords[i].set(points[i].getInhomX() * coeff);
			yCoords[i].set(points[i].getInhomY() * coeff);
		}
	}

	@Override
	public GetCommand getClassName() {
		return Commands.BezierCurve;
	}

	public GeoPointND[] getPoints() {
		return points;
	}

	public GeoCurveCartesian getResult() {
		return result;
	}
}
