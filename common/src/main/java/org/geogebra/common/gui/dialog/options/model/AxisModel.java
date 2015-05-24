package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.util.TableSymbols;
import org.geogebra.common.main.App;
import org.geogebra.common.util.Unicode;

public class AxisModel {
	public interface IAxisModelListener {
		void addTickItem(String item);
		void addAxisLabelItem(String item);
		void addUnitLabelItem(String item);
		void setCrossText(String text);
	}
	
	private IAxisModelListener listener;
	
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
				
			} else if (app.hasEuclidianView2EitherShowingOrNot(1) && app.getEuclidianView2(1) == view) {
				app.getSettings().getEuclidian(2).setAxesNumberingDistance(value, axis);
			} else if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
				app.getSettings().getEuclidian(3).setAxesNumberingDistance(value, axis);

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
		listener.addUnitLabelItem(Unicode.DEGREE); // degrees
		listener.addUnitLabelItem(Unicode.PI_STRING); // pi
		listener.addUnitLabelItem("mm");
		listener.addUnitLabelItem("cm");
		listener.addUnitLabelItem("m");
		listener.addUnitLabelItem("km");

	}

	public void showAxis(boolean value) {
		if (app.getEuclidianView1() == view) {
			app.getSettings().getEuclidian(1)
					.setShowAxis(axis, value); 

		} else if (app.hasEuclidianView2EitherShowingOrNot(1) && app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).setShowAxis(axis, value);
		} else if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).setShowAxis(axis, value);
		
		}
		else {
			view.setShowAxis(axis, value, true);
		}

		view.updateBackground();
	}

	public void showAxisNumbers(boolean value) {
		view.setShowAxisNumbers(axis, value);
		view.updateBackground();	
	}

	public void applyTickDistance(boolean value) {

		if (app.getEuclidianView1() == view) {
			app.getSettings()
					.getEuclidian(1)
					.setAutomaticAxesNumberingDistance(
							!value, axis, true);


		} else if (app.hasEuclidianView2EitherShowingOrNot(1) && app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).setAutomaticAxesNumberingDistance(
					!value, axis, true);
		} else if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).setAutomaticAxesNumberingDistance(
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
		
		if (app.getEuclidianView1() == view) {
			app.getSettings().getEuclidian(1).setAxesUnitLabels(labels);


		} else if (app.hasEuclidianView2EitherShowingOrNot(1) && app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).setAxesUnitLabels(labels);
		} else if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).setAxesUnitLabels(labels);


		}
		else {
			view.setAxesUnitLabels(labels);
		}
		
		view.updateBackground();
		view.repaintView();
	}

	public void applyAxisLabel(String text) {
		
		if (app.getEuclidianView1() == view) {
			app.getSettings().getEuclidian(1).setAxisLabel(axis, text);


		} else if (app.hasEuclidianView2EitherShowingOrNot(1) && app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).setAxisLabel(axis, text);
		} else if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).setAxisLabel(axis, text);


		}
		else {
			view.setAxisLabel(axis, text);
		}
	
		view.updateBounds(true, true);
		view.updateBackground();
		view.repaintView();
	
	
	}

	public void applyTickStyle(int type) {
		int[] styles = view.getAxesTickStyles();
		styles[axis] = type;

		if (app.getEuclidianView1() == view) {
			app.getSettings().getEuclidian(1).setAxisTickStyle(axis, type);
		
		} else if (app.hasEuclidianView2EitherShowingOrNot(1) && app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).setAxisTickStyle(axis, type);
		} else if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).setAxisTickStyle(axis, type);
		
		} else {
			view.setAxesTickStyles(styles);
		}
		view.updateBackground();
	}

	public void applyPositiveAxis(boolean value) {
		if (view == app.getEuclidianView1()) {
			app.getSettings().getEuclidian(1)
					.setPositiveAxis(axis, value);
		
		} else if (app.hasEuclidianView2EitherShowingOrNot(1) && app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).setPositiveAxis(axis, value);
		} else if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).setPositiveAxis(axis, value);
		
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
				
			} else if (app.hasEuclidianView2EitherShowingOrNot(1) && app.getEuclidianView2(1) == view) {
				app.getSettings().getEuclidian(2).setAxisCross(axis, cross);
			} else if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
				app.getSettings().getEuclidian(3).setAxisCross(axis, cross);


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

	public void fillAxisCombo() {
		listener.addAxisLabelItem("");
		String defaultLabel;
		switch(axis){
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
		listener.addAxisLabelItem(defaultLabel);
		String[] greeks = TableSymbols.greekLowerCase;
		for (int i = 0; i < greeks.length; i++) {
			listener.addAxisLabelItem(greeks[i]);
		}
	
	}
}
