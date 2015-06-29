package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.Unicode;

public class EuclidianOptionsModel {
	public interface IEuclidianOptionsListener  {

		GColor getEuclidianBackground(int viewNumber);

		void enableAxesRatio(boolean value);

		void setMinMaxText(String minX, String maxX, String minY,
				String maxY);

		void addTooltipItem(String item);

		void addGridTypeItem(String item);

		void updateAxes(GColor color, boolean isShown, boolean isBold);

		void updateBackgroundColor(GColor color);
		
		void updateGrid(GColor color, boolean isShown, boolean isBold, int gridType);

		void selectTooltipType(int index);

		void updateConsProtocolPanel(boolean isVisible);

		void updateBounds();

		void showMouseCoords(boolean value);

		void selectAxesStyle(int index);

		void updateGridTicks(boolean isAutoGrid,
				double[] gridTicks, int gridType);

		void enableLock(boolean zoomable);

		void selectGridStyle(int style);

		void addAngleOptionItem(String item);
	}
	public enum MinMaxType {
		minX,
		maxX,
		minY,
		maxY
	}	

	public static final int MAX_AXES_STYLE_COUNT = 5;

	public static final int X_AXIS = 0;
	public static final int Y_AXIS = 1;
	public static final int Z_AXIS = 2;

	private App app;
	private EuclidianView view;
	private IEuclidianOptionsListener listener;


	public EuclidianOptionsModel(App app, EuclidianView view, IEuclidianOptionsListener listener) {
		this.app = app;
		this.view = view;
		this.listener = listener;
	}

	public void setView(EuclidianView view) {
		this.view = view;
	}

	public void applyBackgroundColor() {
		if (view == app.getEuclidianView1()) {
			app.getSettings()
			.getEuclidian(1)
			.setBackground(listener.getEuclidianBackground(1));
			
		} else if (app.hasEuclidianView2EitherShowingOrNot(1) && app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).setBackground(listener.getEuclidianBackground(2));
		} else if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).setBackground(listener.getEuclidianBackground(3));

		} else {
			view.setBackground(view.getBackgroundCommon());
		}	
	}

	public void applyBackgroundColor(int viewIdx, GColor color) {
		if (viewIdx == 1 || viewIdx == 2 || viewIdx == 3) {
			app.getSettings()
			.getEuclidian(viewIdx)
			.setBackground(color);
			return;
		}

		EuclidianSettings settings = view.getSettings();
		if (settings != null) {
			settings.setBackground(color);
			return;
		}

		view.setBackground(view.getBackgroundCommon());
			
	}
	
	public void applyAxesColor(GColor col) {
		if (view == app.getEuclidianView1()) {
			app.getSettings().getEuclidian(1).setAxesColor(col);
			return;
		}

		if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).setAxesColor(col);
			return;
		}

		if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).setAxesColor(col);
			return;
		}

		EuclidianSettings settings = view.getSettings();
		if (settings != null) {
			settings.setAxesColor(col);
			return;
		}

		view.setAxesColor(col);
	}

	public void applyGridColor(GColor col) {
		if (view == app.getEuclidianView1()) {
			app.getSettings().getEuclidian(1).setGridColor(col);
			return;
		}

		if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).setGridColor(col);
			return;
		}

		if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).setGridColor(col);
			return;
		}

		EuclidianSettings settings = view.getSettings();
		if (settings != null) {
			settings.setGridColor(col);
			return;
		}

		view.setGridColor(col);

	}	

	public void applyTooltipMode(int mode) {
		if (mode == 0) {
			mode = EuclidianStyleConstants.TOOLTIPS_ON;
		} else if (mode == 1) {
			mode = EuclidianStyleConstants.TOOLTIPS_AUTOMATIC;
		} else if (mode == 2) {
			mode = EuclidianStyleConstants.TOOLTIPS_OFF;
		}

			if (view == app.getEuclidianView1()) {
				app.getSettings().getEuclidian(1).setAllowToolTips(mode);
			return;
				
		}

		if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1) == view) {
				app.getSettings().getEuclidian(2).setAllowToolTips(mode);
			return;
		}

		if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
				app.getSettings().getEuclidian(3).setAllowToolTips(mode);
			return;
		}

		EuclidianSettings settings = view.getSettings();
		if (settings != null) {
			settings.setAllowToolTips(mode);
			return;
		}


		view.setAllowToolTips(mode);


	}

	public void showAxes(boolean value) {
		if (app.getEuclidianView1() == view) {
			app.getSettings()
			.getEuclidian(1)
			.setShowAxes(value,
					value);
			
			return;

		}

		if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).setShowAxes(value,
					value);
			return;
		}

		if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).setShowAxes(value);
			return;
		}

		EuclidianSettings settings = view.getSettings();
		if (settings != null) {
			settings.setShowAxes(value);
			return;
		}

		view.setShowAxes(value, true);

	}

	public void applyBoldAxes(boolean isBold, boolean isVisible) {
		if (app.getEuclidianView1() == view) {
			app.getSettings().getEuclidian(1)
			.setBoldAxes(isBold);
			return;
		}

		if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).setBoldAxes(isBold);
			return;
		}

		if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).setBoldAxes(isBold);
			return;
		}

		EuclidianSettings settings = view.getSettings();
		if (settings != null) {
			settings.setBoldAxes(isBold);
			return;
		}

		view.setBoldAxes(isBold);

	}

	public void showGrid(boolean value) {
		if (app.getEuclidianView1() == view) {
			app.getSettings().getEuclidian(1)
			.showGrid(value);
			return;
		}

		if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).showGrid(value);
			return;
		}

		if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).showGrid(value);
			return;
		}

		EuclidianSettings settings = view.getSettings();
		if (settings != null) {
			settings.showGrid(value);
			return;
		}

		view.showGrid(value);
	}

	public void applyBoldGrid(boolean value) {

		if (app.getEuclidianView1() == view) {
			app.getSettings().getEuclidian(1)
			.setGridIsBold(value);
			return;
		}

		if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).setGridIsBold(value);
			return;
		}

		if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).setGridIsBold(value);
			return;
		}

		EuclidianSettings settings = view.getSettings();
		if (settings != null) {
			settings.setGridIsBold(value);
			return;
		}

		view.setGridIsBold(value);

	}

	public void applyMouseCoords(boolean value) {
		if (view == app.getEuclidianView1()) {
			app.getSettings()
			.getEuclidian(1)
			.setAllowShowMouseCoords(value);
			return;
		}

		if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).setAllowShowMouseCoords(value);
			return;
		}

		if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).setAllowShowMouseCoords(value);
			return;
		}

		EuclidianSettings settings = view.getSettings();
		if (settings != null) {
			settings.setAllowShowMouseCoords(value);
			return;
		}

		view.setAllowShowMouseCoords(value);
	}

	public void appyGridType(int type) {
		if (app.getEuclidianView1() == view) {
			app.getSettings().getEuclidian(1)
			.setGridType(type);
			return;
		}

		if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).setGridType(type);
			return;
		}

		if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).setGridType(type);
			return;
		}

		EuclidianSettings settings = view.getSettings();
		if (settings != null) {
			settings.setGridType(type);
			return;
		}

		view.setGridType(type);
		if (type == EuclidianView.GRID_POLAR) {
			view.updateBounds(true, true);
		}


	}

	public void appyAxesStyle(int style) {
		if (view == app.getEuclidianView1()) {
			app.getSettings().getEuclidian(1).setAxesLineStyle(style);
			return;
		}

		if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).setAxesLineStyle(style);
			return;
		}

		if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).setAxesLineStyle(style);
			return;
		}

		EuclidianSettings settings = view.getSettings();
		if (settings != null) {
			settings.setAxesLineStyle(style);
			return;
		}

		view.setAxesLineStyle(style);


	}

	public void appyGridStyle(int style) {
		if (app.getEuclidianView1() == view) {
			app.getSettings().getEuclidian(1).setGridLineStyle(style);
			return;
		}

		if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).setGridLineStyle(style);
			return;
		}

		if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).setGridLineStyle(style);
			return;
		}

		EuclidianSettings settings = view.getSettings();
		if (settings != null) {
			settings.setGridLineStyle(style);
			return;
		}

		view.setGridLineStyle(style);

	}

	public void appyGridManualTick(boolean value) {
		if (app.getEuclidianView1() == view) {
			app.getSettings()
			.getEuclidian(1)
			.setAutomaticGridDistance(
					!value, true);
			return;
		}

		if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1) == view) {
			app.getSettings().getEuclidian(2).setAutomaticGridDistance(
					!value, true);
			return;
		}

		if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
			app.getSettings().getEuclidian(3).setAutomaticGridDistance(
					!value, true);
			return;
		}

		EuclidianSettings settings = view.getSettings();
		if (settings != null) {
			settings.setAutomaticGridDistance(!value, true);
			return;
		}

		view.setAutomaticGridDistance(!value);

	}

	public void applyAxesRatio(double xval, double yval) {
		if (!(Double.isInfinite(xval) || Double.isNaN(xval)
				|| Double.isInfinite(yval) || Double.isNaN(yval))) {
			// ratio = xval / yval
					// xscale / yscale = ratio
					// => yscale = xscale * xval/yval
			view.setCoordSystem(view.getXZero(), view.getYZero(),
					view.getXscale(), view.getXscale() * xval / yval);
		}	
	}

	public void applyLockRatio(Double value) {
		view.setLockedAxesRatio(value);
		listener.enableAxesRatio(view.isZoomable()
				&& !view.isLockedAxesRatio());
	}

	public void applyMinMax(String text, MinMaxType type) {
		NumberValue minMax = app.getKernel().getAlgebraProcessor()
				.evaluateToNumeric(text, false);
		// not parsed to number => return all
		if (minMax == null) {
			listener.setMinMaxText(view.getXminObject().getLabel(
					StringTemplate.editTemplate),
					view.getXmaxObject().getLabel(
							StringTemplate.editTemplate),
							view.getYminObject().getLabel(
									StringTemplate.editTemplate),
									view.getYmaxObject().getLabel(
											StringTemplate.editTemplate));
		} else {
			switch (type) {
			case maxX:
				if (view == app.getEuclidianView1()) {
					app.getSettings().getEuclidian(1)
					.setXmaxObject(minMax, true);
					
				} else if (app.hasEuclidianView2EitherShowingOrNot(1) && app.getEuclidianView2(1) == view) {
					app.getSettings().getEuclidian(2).setXmaxObject(minMax, true);
				} else if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
					app.getSettings().getEuclidian(3).setXmaxObject(minMax, true);
					
				} else {
					EuclidianSettings settings = view.getSettings();
					if (settings != null) {
						settings.setXmaxObject(minMax, true);
					} else {
						view.setXmaxObject(minMax);
					}
				}
				break;
			case maxY:
				if (view == app.getEuclidianView1()) {
					app.getSettings().getEuclidian(1)
					.setYmaxObject(minMax, true);
					
				} else if (app.hasEuclidianView2EitherShowingOrNot(1) && app.getEuclidianView2(1) == view) {
					app.getSettings().getEuclidian(2).setYmaxObject(minMax, true);
				} else if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
					app.getSettings().getEuclidian(3).setYmaxObject(minMax, true);

				} else {
					EuclidianSettings settings = view.getSettings();
					if (settings != null) {
						settings.setYmaxObject(minMax, true);
					} else {
						view.setYmaxObject(minMax);
					}
				}
				break;
			case minX:
				if (view == app.getEuclidianView1()) {
					app.getSettings().getEuclidian(1)
					.setXminObject(minMax, true);
					
				} else if (app.hasEuclidianView2EitherShowingOrNot(1) && app.getEuclidianView2(1) == view) {
					app.getSettings().getEuclidian(2).setXminObject(minMax, true);
				} else if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
					app.getSettings().getEuclidian(3).setXminObject(minMax, true);

				} else {
					EuclidianSettings settings = view.getSettings();
					if (settings != null) {
						settings.setXminObject(minMax, true);
					} else {
						view.setXminObject(minMax);
					}
				}
				break;
			case minY:
				if (view == app.getEuclidianView1()) {
					app.getSettings().getEuclidian(1)
					.setYminObject(minMax, true);
					
				} else if (app.hasEuclidianView2EitherShowingOrNot(1) && app.getEuclidianView2(1) == view) {
					app.getSettings().getEuclidian(2).setYminObject(minMax, true);
				} else if (app.hasEuclidianView3D() && app.getEuclidianView3D() == view) {
					app.getSettings().getEuclidian(3).setYminObject(minMax, true);

				} else {
					EuclidianSettings settings = view.getSettings();
					if (settings != null) {
						settings.setYminObject(minMax, true);
					} else {
						view.setYminObject(minMax);
					}
				}
				break;
			default:
				break;
			}

			view.setXminObject(view.getXminObject());

			listener.enableAxesRatio((view.isZoomable()
					&& !view.isLockedAxesRatio()));

			view.updateBounds(true, true);
		}
	}

	public void fillTooltipCombo() {
		String[] tooltipItems = new String[] { app.getPlain("On"),
				app.getPlain("Automatic"), app.getPlain("Off") };
		for (String item: tooltipItems) {
			listener.addTooltipItem(item);
		}
	}
	
	public void updateProperties() {

		listener.updateAxes(view.getAxesColor(), view.getShowXaxis() && view.getShowYaxis(),
				view.areAxesBold());
		
		listener.updateGrid(view.getGridColor(), view.getShowGrid(), view.getGridIsBold(),
				view.getGridType());
		
		listener.updateBackgroundColor(getBackgroundColor());
		
		if (view instanceof EuclidianView) {
			int ind = ((EuclidianView) view).getAllowToolTips();
			int idx = -1;
			
			if (ind == EuclidianStyleConstants.TOOLTIPS_ON) {
				idx = 0;
			} else if (ind == EuclidianStyleConstants.TOOLTIPS_AUTOMATIC) {
				idx = 1;
			} else if (ind == EuclidianStyleConstants.TOOLTIPS_OFF) {
				idx = 2;
			}
			
			listener.selectTooltipType(idx);
		}

		listener.showMouseCoords(view.getAllowShowMouseCoords());

		listener.enableAxesRatio(view.isZoomable() && !view.isLockedAxesRatio());
		listener.enableLock(view.isZoomable());
		listener.updateBounds();

		// need style with bold removed for menu
		for (int i = 0; i < EuclidianStyleConstants.lineStyleOptions.length; i++) {
			if (EuclidianView.getBoldAxes(false, view.getAxesLineStyle()) == EuclidianStyleConstants.lineStyleOptions[i]) {
				listener.selectAxesStyle(i);
				break;
			}
		}

		listener.selectGridStyle(view.getGridLineStyle());

		listener.updateGridTicks(view.isAutomaticGridDistance(),
				view.getGridDistances(), view.getGridType());

		// cons protocol panel
		listener.updateConsProtocolPanel(app.showConsProtNavigation(view
				.getViewID()));
	}

	public static int getAxesStyleLength() {
		return MAX_AXES_STYLE_COUNT;
	}

	public void fillGridTypeCombo() {
		String[] gridTypes = new String[3];
		gridTypes[EuclidianView.GRID_CARTESIAN] = app.getMenu("Cartesian");
		gridTypes[EuclidianView.GRID_ISOMETRIC] = app.getMenu("Isometric");
		gridTypes[EuclidianView.GRID_POLAR] = app.getMenu("Polar");
		for (String item: gridTypes) {
			listener.addGridTypeItem(item);
		}
	}
	
	public void fillAngleOptions() {
		String[] angleOptions = { Unicode.PI_STRING + "/12",
			Unicode.PI_STRING + "/6", Unicode.PI_STRING + "/4",
			Unicode.PI_STRING + "/3", Unicode.PI_STRING + "/2", };
		for (String item: angleOptions) {
			listener.addAngleOptionItem(item);
		};
	}

	public void applyGridTicks(double value, int idx) {
		if (value > 0) {
			double[] ticks = view.getGridDistances();
			ticks[idx] = value;
			view.setGridDistances(ticks);
		}

	}

	public void applyGridTickAngle(int value) {
		if (value >= 0) {
			double[] ticks = view.getGridDistances();
			// val = 4 gives 5*PI/12, skip this and go to 6*Pi/2 = Pi/2
			if (value == 4)
				value = 5;
			ticks[2] = (value + 1) * Math.PI / 12;
			view.setGridDistances(ticks);
		}

	}

	public GColor getAxesColor() {
		return view.getAxesColor();
	}

	public GColor getGridColor() {
		return view.getGridColor();
	}
	
	public GColor getBackgroundColor() {
		return view.getBackgroundCommon();
	}
				
}

