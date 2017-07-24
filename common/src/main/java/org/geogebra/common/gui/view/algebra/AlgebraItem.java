package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.cas.AlgoSolve;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.main.Feature;

import com.himamis.retex.editor.share.util.Unicode;

public class AlgebraItem {
	private static String undefinedVariables;
	public static boolean toggleSymbolic(GeoElement geo) {

		if (geo instanceof HasSymbolicMode) {
			if (geo.getParentAlgorithm() instanceof AlgoSolve) {
				return !((AlgoSolve) geo.getParentAlgorithm()).toggleNumeric();
			}
			((HasSymbolicMode) geo).setSymbolicMode(
					!((HasSymbolicMode) geo).isSymbolicMode(), true);
			geo.updateRepaint();
			return ((HasSymbolicMode) geo).isSymbolicMode();

		}
		return false;
	}

	public static String getOutputPrefix(GeoElement geo) {
		if (geo instanceof HasSymbolicMode
				&& !((HasSymbolicMode) geo).isSymbolicMode()) {
			if (!(geo.getParentAlgorithm() instanceof AlgoSolve)
					|| ((AlgoSolve) geo.getParentAlgorithm())
							.getClassName() == Commands.NSolve) {
				return Unicode.CAS_OUTPUT_NUMERIC + "";
			}
			
		}

		return getSymbolicPrefix(geo.getKernel());
	}

	public static boolean isSymbolicDiffers(GeoElement geo) {
		if (!(geo instanceof HasSymbolicMode)) {
			return false;
		}

		if (geo.getParentAlgorithm() instanceof AlgoSolve) {
			return !allRHSareIntegers((GeoList) geo);
		}

		HasSymbolicMode sm = (HasSymbolicMode) geo;
		boolean orig = sm.isSymbolicMode();
		String text1 = geo.getLaTeXAlgebraDescription(true,
				StringTemplate.latexTemplate);
		sm.setSymbolicMode(!orig, false);
		String text2 = geo.getLaTeXAlgebraDescription(true,
				StringTemplate.latexTemplate);

		sm.setSymbolicMode(orig, false);
		if (text1 == null) {
			return true;
		}

		return !text1.equals(text2);

	}

	private static boolean allRHSareIntegers(GeoList geo) {
		for (int i = 0; i < geo.size(); i++) {
			if (geo.get(i) instanceof GeoLine
					&& !Kernel.isInteger(((GeoLine) geo.get(i)).getZ())) {
				return false;
			}
			if (geo.get(i) instanceof GeoList
					&& !allRHSareIntegers(((GeoList) geo.get(i)))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isGeoFraction(GeoElement geo) {
		return geo instanceof GeoNumeric && geo.getDefinition() != null
				&& geo.getDefinition().isFraction();
	}

	public static Suggestion getSuggestions(GeoElement geo) {
		Suggestion sug = null;
		if (geo != null && geo.getKernel().getApplication()
				.has(Feature.INPUT_BAR_SOLVE)) {
			sug = SuggestionSolve.get(geo);
			if (sug != null) {
				return sug;
			}

			sug = SuggestionRootExtremum.get(geo);
			if (sug != null) {
				return sug;
			}
		}
		if (geo != null
				&& geo.getKernel().getApplication().has(Feature.SHOW_STEPS)) {
			sug = SuggestionSteps.get(geo);

			if (sug != null) {
				return sug;
			}
		}
		if (undefinedVariables != null) {
			sug = SuggestionSlider.get();
			if (sug != null) {
				return sug;
			}
		}
		return null;
	}

	public static String getSymbolicPrefix(Kernel kernel) {
		return kernel.getLocalization().rightToLeftReadingOrder
				? Unicode.CAS_OUTPUT_PREFIX_RTL + ""
				: Unicode.CAS_OUTPUT_PREFIX + "";
	}

	public static boolean needsPacking(GeoElement geo) {
		return geo.getParentAlgorithm() != null
				&& geo.getParentAlgorithm().getOutput().length > 1
				&& geo.getKernel().getApplication().getSettings().getAlgebra()
						.getTreeMode() == SortMode.ORDER;
	}

	public static String getUndefinedValiables() {
		return undefinedVariables;
	}

	public static void setUndefinedValiables(String undefinedValiables) {
		AlgebraItem.undefinedVariables = undefinedValiables;
	}
}
