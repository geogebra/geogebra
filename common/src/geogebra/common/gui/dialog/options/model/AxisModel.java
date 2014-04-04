package geogebra.common.gui.dialog.options.model;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.main.App;

public class AxisModel {
	public interface IAxisModelListener {
		void addTickItem(String item);
		void addUnitLabelItem(String item);
		void setCrossText(String text);
	}
	
	private IAxisModelListener listener;
	
	public static final String PI_STR = "\u03c0";
	public static final String DEGREE_STR = "\u00b0";

	protected int axis;
	private App app;
	protected EuclidianView view;

	final static protected int AXIS_X = 0;
	final static protected int AXIS_Y = 1;
	final static protected int AXIS_Z = 2;

	/******************************************************
	 * @param app
	 * @param view
	 * @param axis
	 */
	
	public AxisModel(App app, EuclidianView view, int axis, IAxisModelListener listener) {
		this.listener = listener;
		this.app = app;
		this.axis = axis;
		this.view = view;
	}

	public void fillTicksCombo() {
		// ticks
		char big = '|';
		char small = '\'';
		listener.addTickItem(" " + big + "  " + small + "  " + big + "  "
				+ small + "  " + big); // major and minor ticks
		listener.addTickItem(" " + big + "     " + big + "     " + big); // major
																		// ticks
																		// only
		// must be " " not ""
		listener.addTickItem(" "); // no ticks

	}
	public String getAxisName() {
		switch (axis){
		case AXIS_X:
		default:
			return "xAxis";
		case AXIS_Y:
			return "yAxis";
		case AXIS_Z:	
			return "zAxis";
		}
	}

	public void applyTickDistance(double value) {
		if (value > 0) {
			if (app.getEuclidianView1() == view) {
				app.getSettings().getEuclidian(1)
				.setAxesNumberingDistance(value, axis);
			} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
				view.setAxesNumberingDistance(value, axis);
			} else if (app.getEuclidianView2() == view) {
				app.getSettings().getEuclidian(2)
				.setAxesNumberingDistance(value, axis);
			} else {
				view.setAxesNumberingDistance(value, axis);
			}
			
			view.updateBackground();
		}
	}

	protected double parseDouble(String text) {
		if (text == null || text.equals(""))
			return Double.NaN;
		return app.getKernel().getAlgebraProcessor().evaluateToDouble(text);
	}

	
	public void fillUnitLabel() {
		listener.addUnitLabelItem(null);
		listener.addUnitLabelItem(DEGREE_STR); // degrees
		listener.addUnitLabelItem(PI_STR); // pi
		listener.addUnitLabelItem("mm");
		listener.addUnitLabelItem("cm");
		listener.addUnitLabelItem("m");
		listener.addUnitLabelItem("km");

	}

	public void showAxis(boolean value) {
		if (app.getEuclidianView1() == view) {
			app.getSettings().getEuclidian(1)
					.setShowAxis(axis, value); 
		}
		else if (!app.hasEuclidianView2EitherShowingOrNot()) {
			view.setShowAxis(axis, value, true);
		}
		else if (app.getEuclidianView2() == view) {
			app.getSettings().getEuclidian(2)
					.setShowAxis(axis, value);
		}
		else {
			view.setShowAxis(axis, value, true);
		}

		view.updateBackground();
	}

	public void showAxisNumbers(boolean value) {
		boolean[] show = view.getShowAxesNumbers();
		show[axis] = value;
		view.setShowAxesNumbers(show);
		view.updateBackground();	
	}

	public void applyTickDistance(boolean value) {

		if (app.getEuclidianView1() == view) {
			app.getSettings()
					.getEuclidian(1)
					.setAutomaticAxesNumberingDistance(
							!value, axis, true);
		}
		else if (!app.hasEuclidianView2EitherShowingOrNot()) {
			view.setAutomaticAxesNumberingDistance(
					!value, axis);
		}
		else if (app.getEuclidianView2() == view) {
			app.getSettings()
					.getEuclidian(2)
					.setAutomaticAxesNumberingDistance(
							!value, axis, true);
		}
		else {
			view.setAutomaticAxesNumberingDistance(
					!value, axis);
		}
		view.updateBackground();

	}

	public void applyUnitLabel(String text) {
		String[] labels = view.getAxesUnitLabels();
		labels[axis] = text;
		view.setAxesUnitLabels(labels);
		view.updateBackground();
	}

	public void applyAxisLabel(String text) {
		
		if (app.getEuclidianView1() == view) {
			app.getSettings().getEuclidian(1).setAxisLabel(axis, text);
		}
		else if (!app.hasEuclidianView2EitherShowingOrNot()) {
			view.setAxisLabel(axis, text);
		}
		else if (app.getEuclidianView2() == view) { 
			app.getSettings().getEuclidian(2).setAxisLabel(axis, text);

		}
		else {
			view.setAxisLabel(axis, text);
		}
	
		view.updateBounds(true);
		view.updateBackground();
		view.repaintView();
	
	
	}

	public void applyTickStyle(int type) {
		int[] styles = view.getAxesTickStyles();
		styles[axis] = type;

		if (app.getEuclidianView1() == view) {
			app.getSettings().getEuclidian(1).setAxisTickStyle(axis, type);
		} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
			view.setAxesTickStyles(styles);
		} else if (app.getEuclidianView2() == view) {
			app.getSettings().getEuclidian(2).setAxisTickStyle(axis, type);
		} else {
			view.setAxesTickStyles(styles);
		}
		view.updateBackground();
	}

	public void applyPositiveAxis(boolean value) {
		if (view == app.getEuclidianView1()) {
			app.getSettings().getEuclidian(1)
					.setPositiveAxis(axis, value);
		} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
			view.setPositiveAxis(axis, value);
		} else if (view == app.getEuclidianView2()) {
			app.getSettings().getEuclidian(2)
					.setPositiveAxis(axis, value);
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
		if ("".equals(str))
			str = "0";
		double cross = parseDouble(str);
		if (!(Double.isInfinite(cross) || Double.isNaN(cross))) {
			double[] ac = view.getAxesCross();
			ac[axis] = cross;

			if (app.getEuclidianView1() == view) {
				app.getSettings().getEuclidian(1).setAxisCross(axis, cross);
			} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
				view.setAxesCross(ac);
			} else if (app.getEuclidianView2() == view) {
				app.getSettings().getEuclidian(2).setAxisCross(axis, cross);
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
}
