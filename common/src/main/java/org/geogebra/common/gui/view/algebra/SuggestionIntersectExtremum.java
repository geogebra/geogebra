package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.gui.view.algebra.scicalc.LabelHiderCallback;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.DoubleUtil;

/**
 * @author Mathieu
 *
 */
public class SuggestionIntersectExtremum extends Suggestion {

	private static Suggestion INSTANCE = new SuggestionIntersectExtremum();

	@Override
	public String getCommand(Localization loc) {
		return loc.getMenu("Suggestion.SpecialPoints");
	}

	@Override
	public void runCommands(GeoElementND geo) {
		// reported typecast error
		// reported NPE
		boolean[] neededAlgos = getNeededAlgos(geo);
		boolean isSymbolicMode = geo.getKernel().getSymbolicMode() == SymbolicMode.SYMBOLIC_AV;
		String cmd;

		new LabelController().ensureHasLabel(geo);
		checkDependentAlgo(geo, INSTANCE, neededAlgos);
		AlgebraProcessor algebraProcessor = geo.getKernel().getAlgebraProcessor();

		if (neededAlgos[0]) {
			intersect(geo);
		}

		if (neededAlgos[1]) {
			cmd = "Extremum[" + geo.getLabelSimple() + "]";
			processCommand(algebraProcessor, cmd, isSymbolicMode);
		}
		if (neededAlgos[2]) {
			cmd = "Intersect[" + geo.getLabelSimple() + ","
					+ geo.getKernel().getLocalization().getMenu("yAxis")
					+ "]";
			processCommand(algebraProcessor, cmd, isSymbolicMode);
		}
	}

	private void intersect(GeoElementND geo) {
		PolyFunction polynomial = getPolynomial(geo);
		AlgebraProcessor algebraProcessor = geo.getKernel().getAlgebraProcessor();
		StringBuilder sb = new StringBuilder();
		sb.append("Intersect[");
		sb.append(geo.getLabelSimple());
		sb.append(", ");
		if (polynomial != null) {
			sb.append(geo.getKernel().getLocalization().getMenu("xAxis"));
		} else {
			GRectangle bounds =
					geo.getKernel().getApplication().getActiveEuclidianView().getBounds();
			sb.append("y = 0, ");
			sb.append(bounds.getMinX());
			sb.append(", ");
			sb.append(bounds.getMaxX());
		}
		sb.append("]");
		processCommand(algebraProcessor, sb.toString(), false);

	}

	private PolyFunction getPolynomial(GeoElementND geo) {
		if (!(geo instanceof GeoFunctionable)) {
			return null;
		}
		Function function = ((GeoFunctionable) geo).getFunction();
		if (function == null) {
			return null;
		}

		PolyFunction poly = function.expandToPolyFunction(
				function.getFunctionExpression(), false, true);
		return poly;
	}

	protected void processCommand(AlgebraProcessor algebraProcessor, String cmd,
			boolean isSymbolicMode) {
		if (isSymbolicMode) {
			GeoElementND[] pointLists = algebraProcessor.processAlgebraCommand(
					cmd, false, new LabelHiderCallback());
			setPointsColorToGray(pointLists);
		} else {
			algebraProcessor.processAlgebraCommand(cmd, false);
		}
	}

	private void setPointsColorToGray(GeoElementND[] pointLists) {
		for (int i = 0; i < pointLists.length; i++) {
			pointLists[i].setObjColor(ConstructionDefaults.colDepPointG);
			pointLists[i].updateRepaint();
		}
	}

	private static boolean[] getNeededAlgos(GeoElementND geo) {
		// intersection with y needed always
		boolean[] algosMissing = { false, false, true };
		if (!(geo instanceof GeoFunctionable)) {
			return algosMissing;
		}
		Function function = ((GeoFunctionable) geo).getFunction();
		if (function == null) {
			return algosMissing;
		}

		PolyFunction poly = function.expandToPolyFunction(
				function.getFunctionExpression(), false, true);
		if (poly == null || poly.getDegree() > 0) {
			algosMissing[0] = true;
		}
		if (poly == null || poly.getDegree() > 1) {
			algosMissing[1] = true;
		}

		if ((geo instanceof GeoSymbolic && isVerticalLine(((GeoSymbolic) geo).getTwinGeo()))
				|| isVerticalLine(geo)) {
			algosMissing[2] = false;
		}
		return algosMissing;
	}

	private static boolean isVerticalLine(GeoElementND geo) {
		return Equation.isAlgebraEquation(geo) && geo instanceof GeoLine
				&& DoubleUtil.isZero(((GeoLine) geo).getY());
	}

	/**
	 * @param geo
	 *            construction element
	 * @return solve suggestion if applicable
	 */
	public static Suggestion get(GeoElement geo) {
		if (mayHaveSpecialPoints(geo)
				&& !checkDependentAlgo(geo, INSTANCE, getNeededAlgos(geo))) {
			return INSTANCE;
		}
		return null;
	}

	/**
	 * @param geo
	 *            element
	 * @return whether the element can be seen as a function (is a function,
	 *         line or conic)
	 */
	private static boolean mayHaveSpecialPoints(GeoElement geo) {
		return geo.isRealValuedFunction() && !geo.unwrapSymbolic().isNumberValue();
	}

	@Override
	protected boolean allAlgosExist(GetCommand className, GeoElement[] input,
			boolean[] algosMissing) {
		boolean withYAxis = containsLabel(input, "yAxis");
		if (className == Commands.Intersect && !withYAxis) {
			algosMissing[0] = false;
		}
		if (className == Commands.Extremum) {
			algosMissing[1] = false;
		}
		if (className == Commands.Intersect && withYAxis) {
			algosMissing[2] = false;
		}
		return !algosMissing[0] && !algosMissing[1] && !algosMissing[2];
	}

	private boolean containsLabel(GeoElement[] input, String axis) {
		for (int i = 0; i < input.length; i++) {
			if (axis.equals(input[i].getLabelSimple())) {
				return true;
			}
		}
		return false;
	}
}