package org.geogebra.common.gui.dialog.options.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.EuclidianSettings;

import com.himamis.retex.editor.share.util.Unicode;

public class AxisModel {
	final static public int AXIS_X = 0;
	final static public int AXIS_Y = 1;
	final static public int AXIS_Z = 2;
	protected int axis;
	protected EuclidianView view;
	private IAxisModelListener listener;
	private App app;

	/******************************************************
	 * @param app
	 * @param view
	 * @param axis
	 */

	public AxisModel(App app, EuclidianView view, int axis,
			IAxisModelListener listener) {
		this.listener = listener;
		this.app = app;
		this.axis = axis;
		this.view = view;
	}

	public List<String> getTickOptions() {
		// ticks
		return Arrays.asList(" |  '  |  '  |",
				" |  \u00a0 | \u00a0  |", // only major
				" "); // no ticks
	}

	public String getAxisName() {
		switch (axis) {
		case AXIS_X:
		default:
			return "xAxis";
		case AXIS_Y:
			return "yAxis";
		case AXIS_Z:
			return "zAxis";
		}
	}

	/**
	 * convert string to value for tick distance
	 *
	 * @param str string
	 * @return value computed (may be null)
	 */
	public GeoNumberValue applyTickDistance(String str) {
		return applyTickDistance(str, true);
	}

	public GeoNumberValue applyTickDistance(String str, boolean fireChange) {
		GeoNumberValue value = null;
		final String text = str.trim();
		if (!"".equals(text)) {
			value = app.getKernel().getAlgebraProcessor()
					.evaluateToNumeric(text, ErrorHelper.silent());
		}
		if (value != null) {
			EuclidianSettings settings = getSettings();
			if (settings != null) {
				settings.setAxesNumberingDistance(value, axis, fireChange);
			} else {
				view.setAxesNumberingDistance(value, axis);
			}

			if (fireChange) {
				view.updateBackground();
			}
		}
		return value;
	}

	protected double parseDouble(String text) {
		if (text == null || "".equals(text)) {
			return Double.NaN;
		}
		return app.getKernel().getAlgebraProcessor().evaluateToDouble(text);
	}

	public List<String> getUnitLabelOptions() {
		return Arrays.asList(null,
			Unicode.DEGREE_STRING, // degrees
			Unicode.PI_STRING, // pi
			"mm",
			"cm",
			"m",
			"km",
			Unicode.CURRENCY_DOLLAR + "");
	}

	public void showAxis(boolean value) {
		EuclidianSettings settings = getSettings();
		if (settings != null) {
			settings.setShowAxis(axis, value);
		} else {
			view.setShowAxis(axis, value, true);
		}
		view.updateBackground();
	}

	public void showAxisNumbers(boolean value) {
		view.setShowAxisNumbers(axis, value);
		view.updateBackground();
	}

	public void applyTickDistance(boolean value) {
		applyTickDistance(value, true);
	}

	public void applyTickDistance(boolean value, boolean fireChange) {
		EuclidianSettings settings = getSettings();
		if (settings != null) {
			settings.setAutomaticAxesNumberingDistance(!value, axis,
					fireChange);
		} else {
			view.setAutomaticAxesNumberingDistance(!value, axis);
		}

		if (fireChange) {
			view.updateBackground();
		}
	}

	private EuclidianSettings getSettings() {
		if (app.getEuclidianView1() == view) {
			return app.getSettings().getEuclidian(1);
		} else if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1) == view) {
			return app.getSettings().getEuclidian(2);
		} else if (app.isEuclidianView3D(view)) {
			return app.getSettings().getEuclidian(3);
		}
		return null;
	}

	public void applyUnitLabel(String text) {
		EuclidianSettings settings = getSettings();
		if (settings != null) {
			settings.setAxisUnitLabel(axis, text);
		} else {
			String[] labels = view.getAxesUnitLabels();
			labels[axis] = text;
			view.setAxesUnitLabels(labels);
		}

		view.updateBackground();
		view.repaintView();
	}

	public boolean applyAxisLabel(String text) {
		return applyAxisLabel(text, true);
	}

	public boolean applyAxisLabel(String text, boolean fireChange) {

		boolean changed = false;

		EuclidianSettings settings = getSettings();
		if (settings != null) {
			changed = settings.setAxisLabel(axis, text, fireChange);
		} else {
			view.setAxisLabel(axis, text);
			changed = true;
		}

		if (fireChange) {
			view.updateBounds(true, true);
			view.updateBackground();
			view.repaintView();
		}
		return changed;
	}

	public void applyTickStyle(int type) {
		int[] styles = view.getAxesTickStyles();
		styles[axis] = type;

		EuclidianSettings settings = getSettings();
		if (settings != null) {
			settings.setAxisTickStyle(axis, type);
		} else {
			view.setAxesTickStyles(styles);
		}

		view.updateBackground();
	}

	public void applyPositiveAxis(boolean value) {
		EuclidianSettings settings = getSettings();
		if (settings != null) {
			settings.setPositiveAxis(axis, value);
		} else {
			view.setPositiveAxis(axis, value);
		}

		view.updateBackground();
	}

	public void applyDrawAtBorder(boolean value) {
		boolean[] border = view.getDrawBorderAxes();
		border[axis] = value;
		view.setDrawBorderAxes(border);
		if (!value) {
			view.setAxisCross(axis, 0.0);
		}

		view.updateBackground();
	}

	public void applyCrossing(String crossStr) {
		String str = crossStr;
		if ("".equals(str)) {
			str = "0";
		}
		double cross = parseDouble(str);
		if (!(Double.isInfinite(cross) || Double.isNaN(cross))) {
			double[] ac = view.getAxesCross();
			ac[axis] = cross;

			EuclidianSettings settings = getSettings();
			if (settings != null) {
				settings.setAxisCross(axis, cross);
			} else {
				view.setAxesCross(ac);
			}
		}

		view.updateBackground();

		listener.setCrossText("" + view.getAxesCross()[axis]);
	}

	public int getAxis() {
		return axis;
	}

	public List<String> getAxisLabelOptions() {
		ArrayList<String> labels = new ArrayList<>();
		labels.add("");
		String defaultLabel;
		switch (axis) {
		case AXIS_X:
			defaultLabel = "x";
			break;
		case AXIS_Y:
		default:
			defaultLabel = "y";
			break;
		case AXIS_Z:
			defaultLabel = "z";
			break;
		}
		labels.add(defaultLabel);
		GeoElement.addAddAllGreekLowerCaseNoPi(labels);
		return labels;
	}

	public void setView(EuclidianView view) {
		this.view = view;
	}

	public void applyAllowSelection(boolean value) {
		EuclidianSettings settings = getSettings();
		if (settings != null) {
			settings.setSelectionAllowed(axis, value);
		}
		view.updateBackground();
	}

	public boolean isSelectionAllowed() {
		EuclidianSettings settings = view.getSettings();
		if (settings != null) {
			return settings.isSelectionAllowed(axis);
		}

		return false;
	}

	public int getTickStyleIndex(int axis) {
		return view.getAxisTickStyle(axis);
	}

	public interface IAxisModelListener {

		void setCrossText(String text);
	}

	public String getAxisDistance() {
		GeoNumberValue dist = view.getAxesDistanceObjects()[axis];
		return dist == null ? ""
				: dist.getLabel(StringTemplate.editTemplate);
	}
}
