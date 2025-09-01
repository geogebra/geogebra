package org.geogebra.common.properties.impl.graphics;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoFractionText;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.NumericPropertyWithSuggestions;
import org.geogebra.common.util.DoubleUtil;

import com.himamis.retex.editor.share.util.Unicode;

public class GridAngleProperty extends NumericPropertyWithSuggestions {
	private final EuclidianView euclidianView;

	/**
	 * Constructs a grid distance property.
	 * @param processor algebra processor
	 * @param localization localization for the title
	 * @param euclidianView euclidian view
	 */
	public GridAngleProperty(AlgebraProcessor processor, Localization localization,
			EuclidianView euclidianView) {
		super(processor, localization, String.valueOf(Unicode.theta));
		this.euclidianView = euclidianView;
	}

	@Override
	public List<String> getSuggestions() {
		return List.of(
				Unicode.PI_STRING + "/12",
				Unicode.PI_STRING + "/6",
				Unicode.PI_STRING + "/4",
				Unicode.PI_STRING + "/3",
				Unicode.PI_STRING + "/2");
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		if (!Double.isFinite(value.getDouble())) {
			return;
		}
		double[] ticks = euclidianView.getGridDistances();
		ticks[2] = getGridTickAngle(value.getDouble());
		euclidianView.setGridDistances(ticks);
		euclidianView.updateBackground();
	}

	@Override
	public NumberValue getNumberValue() {
		return new MyDouble(euclidianView.getKernel(), euclidianView.getGridDistances(2));
	}

	@Override
	public String getValue() {
		double val = euclidianView.getGridDistances(2) / Math.PI;
		double[] frac = AlgoFractionText.decimalToFraction(val,
				Kernel.MAX_PRECISION);
		StringBuilder sb = new StringBuilder();
		if (frac[1] < 361) {
			if (!DoubleUtil.isEqual(1, frac[0])) {
				sb.append(Math.round(frac[0]));
			}
			sb.append(Unicode.pi);
			if (!DoubleUtil.isEqual(1, frac[1])) {
				sb.append("/");
				sb.append(Math.round(frac[1]));
			}
		} else {
			sb.append(euclidianView.getKernel().format(euclidianView.getGridDistances(2),
					StringTemplate.editTemplate));
		}
		return sb.toString();
	}

	private static double getGridTickAngle(double value) {
		return Math.PI
				/ Math.min(360, Math.round(Math.abs(Math.PI / value)));
	}

	@Override
	public boolean isEnabled() {
		return euclidianView.getSettings().getGridType() == EuclidianView.GRID_POLAR;
	}
}
