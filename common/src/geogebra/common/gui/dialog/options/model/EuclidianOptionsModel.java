package geogebra.common.gui.dialog.options.model;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.main.App;
import geogebra.common.plugin.EuclidianStyleConstants;

public class EuclidianOptionsModel {
	public interface IEuclidianOptionsListener  {

		GColor getEuclidianBackground(int viewNumber);

		void enableAxesRatio(boolean value);

		void setMinMaxText(String minX, String maxX, String minY,
				String maxY);

	}
	public enum MinMaxType {
		minX,
		maxX,
		minY,
		maxY
	}	

	private App app;
	private EuclidianView view;
	private IEuclidianOptionsListener listener;


	public EuclidianOptionsModel(App app, EuclidianView view, IEuclidianOptionsListener listener) {
		this.app = app;
		this.view = view;
		this.listener = listener;
	}

	public void applyBackgroundColor() {
		if (view == app.getEuclidianView1()) {
			app.getSettings()
			.getEuclidian(1)
			.setBackground(listener.getEuclidianBackground(1));
		} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
			view.setBackground(view.getBackgroundCommon());
		} else if (view == app.getEuclidianView2()) {
			app.getSettings()
			.getEuclidian(2)
			.setBackground(listener.getEuclidianBackground(2));
		} else {
			view.setBackground(view.getBackgroundCommon());
		}	
	}

	public void applyAxesColor(GColor col) {
		if (view == app.getEuclidianView1()) {
			app.getSettings().getEuclidian(1).setAxesColor(col);
		} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
			view.setAxesColor(col);
		} else if (view == app.getEuclidianView2()) {
			app.getSettings().getEuclidian(2).setAxesColor(col);
		} else {
			view.setAxesColor(col);
		}	
	}

	public void applyGridColor(GColor col) {
		if (view == app.getEuclidianView1()) {
			app.getSettings().getEuclidian(1).setGridColor(col);
		} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
			view.setGridColor(col);
		} else if (view == app.getEuclidianView2()) {
			app.getSettings().getEuclidian(2).setGridColor(col);
		} else {
			view.setGridColor(col);
		}
	}	

	public void applyTooltipMode(int mode) {
		if (mode == 0) {
			mode = EuclidianStyleConstants.TOOLTIPS_ON;
		} else if (mode == 1) {
			mode = EuclidianStyleConstants.TOOLTIPS_AUTOMATIC;
		} else if (mode == 2) {
			mode = EuclidianStyleConstants.TOOLTIPS_OFF;
		}

		if (view instanceof EuclidianView) {
			if (view == app.getEuclidianView1()) {
				app.getSettings().getEuclidian(1).setAllowToolTips(mode);
			} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
				((EuclidianView) view).setAllowToolTips(mode);
			} else if (view == app.getEuclidianView2()) {
				app.getSettings().getEuclidian(2).setAllowToolTips(mode);
			} else {
				((EuclidianView) view).setAllowToolTips(mode);
			}
		}

	}

	public void showAxes(boolean value) {
		if (app.getEuclidianView1() == view) {
			app.getSettings()
			.getEuclidian(1)
			.setShowAxes(value,
					value);
		} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
			view.setShowAxes(value, true);
		} else if (app.getEuclidianView2() == view) {
			app.getSettings()
			.getEuclidian(2)
			.setShowAxes(value,
					value);
		} else {
			view.setShowAxes(value, true);
		}
	}

	public void applyBoldAxes(boolean isBold, boolean isVisible) {
		if (app.getEuclidianView1() == view) {
			app.getSettings().getEuclidian(1)
			.setBoldAxes(isBold);
		} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
			view.setShowAxes(isVisible, true);
		} else if (app.getEuclidianView2() == view) {
			app.getSettings().getEuclidian(2)
			.setBoldAxes(isBold);
		} else {
			view.setBoldAxes(isBold);
		}

	}

	public void showGrid(boolean value) {
		if (app.getEuclidianView1() == view) {
			app.getSettings().getEuclidian(1)
			.showGrid(value);
		} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
			view.showGrid(value);
		} else if (app.getEuclidianView2() == view) {
			app.getSettings().getEuclidian(2)
			.showGrid(value);
		} else {
			view.showGrid(value);
		}	
	}

	public void applyBoldGrid(boolean value) {

		if (app.getEuclidianView1() == view) {
			app.getSettings().getEuclidian(1)
			.setGridIsBold(value);
		} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
			view.setGridIsBold(value);
		} else if (app.getEuclidianView2() == view) {
			app.getSettings().getEuclidian(2)
			.setGridIsBold(value);
		} else {
			view.setGridIsBold(value);
		}	
	}

	public void applyMouseCoords(boolean value) {
		if (view == app.getEuclidianView1()) {
			app.getSettings()
			.getEuclidian(1)
			.setAllowShowMouseCoords(value);
		} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
			view.setAllowShowMouseCoords(value);
		} else if (view == app.getEuclidianView2()) {
			app.getSettings()
			.getEuclidian(2)
			.setAllowShowMouseCoords(value);
		} else {
			view.setAllowShowMouseCoords(value);
		}
	}

	public void appyGridType(int type) {
		if (app.getEuclidianView1() == view) {
			app.getSettings().getEuclidian(1)
			.setGridType(type);
		} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
			view.setGridType(type);
		} else if (app.getEuclidianView2() == view) {
			app.getSettings().getEuclidian(2)
			.setGridType(type);
		} else {
			view.setGridType(type);
		}

	}

	public void appyAxesStyle(int style) {
		if (view == app.getEuclidianView1()) {
			app.getSettings().getEuclidian(1).setAxesLineStyle(style);
		} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
			view.setAxesLineStyle(style);
		} else if (view == app.getEuclidianView2()) {
			app.getSettings().getEuclidian(2).setAxesLineStyle(style);
		} else {
			view.setAxesLineStyle(style);
		}

	}

	public void appyGridStyle(int style) {
		if (app.getEuclidianView1() == view) {
			app.getSettings().getEuclidian(1).setGridLineStyle(style);
		} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
			view.setGridLineStyle(style);
		} else if (app.getEuclidianView2() == view) {
			app.getSettings().getEuclidian(2).setGridLineStyle(style);
		} else {
			view.setGridLineStyle(style);
		}

	}

	public void appyGridManualTick(boolean value) {
		if (app.getEuclidianView1() == view) {
			app.getSettings()
			.getEuclidian(1)
			.setAutomaticGridDistance(
					!value, true);
		} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
			view.setAutomaticGridDistance(!value);
		} else if (app.getEuclidianView2() == view) {
			app.getSettings()
			.getEuclidian(2)
			.setAutomaticGridDistance(
					!value, true);
		} else {
			view.setAutomaticGridDistance(!value);
		}

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
				} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
					view.setXmaxObject(minMax);
				} else if (view == app.getEuclidianView2()) {
					app.getSettings().getEuclidian(2)
					.setXmaxObject(minMax, true);
				} else {
					view.setXmaxObject(minMax);
				}
				break;
			case maxY:
				if (view == app.getEuclidianView1()) {
					app.getSettings().getEuclidian(1)
					.setYmaxObject(minMax, true);
				} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
					view.setYmaxObject(minMax);
				} else if (view == app.getEuclidianView2()) {
					app.getSettings().getEuclidian(2)
					.setYmaxObject(minMax, true);
				} else {
					view.setYmaxObject(minMax);
				}
				break;
			case minX:
				if (view == app.getEuclidianView1()) {
					app.getSettings().getEuclidian(1)
					.setXminObject(minMax, true);
				} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
					view.setXminObject(minMax);
				} else if (view == app.getEuclidianView2()) {
					app.getSettings().getEuclidian(2)
					.setXminObject(minMax, true);
				} else {
					view.setXminObject(minMax);
				}
				break;
			case minY:
				if (view == app.getEuclidianView1()) {
					app.getSettings().getEuclidian(1)
					.setYminObject(minMax, true);
				} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
					view.setYminObject(minMax);
				} else if (view == app.getEuclidianView2()) {
					app.getSettings().getEuclidian(2)
					.setYminObject(minMax, true);
				} else {
					view.setYminObject(minMax);
				}
				break;
			default:
				break;
			}

			view.setXminObject(view.getXminObject());

			listener.enableAxesRatio((view.isZoomable()
					&& !view.isLockedAxesRatio()));

			view.updateBounds(true);
		}
	}
}
