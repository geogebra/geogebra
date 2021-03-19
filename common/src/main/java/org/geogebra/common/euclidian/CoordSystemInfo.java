package org.geogebra.common.euclidian;

public class CoordSystemInfo {
	private EuclidianView view;
	private boolean xAxisZoom = false;
	private boolean centerView;

	public CoordSystemInfo(EuclidianView view) {
		this.view = view;
	}

	public double deltaX() {
		return view.xZero - view.xZeroOld;
	}

	public double deltaY() {
		return view.yZero - view.yZeroOld;
	}

	public boolean isXAxisZoom() {
		return xAxisZoom;
	}

	public void setXAxisZoom(boolean xAxisZoom) {
		this.xAxisZoom = xAxisZoom;
	}

	@Override
	public String toString() {
		return "CoordSystemInfo{"
				+ "dx: " + deltaX()
				+ ", dy: " + deltaY()
				+ ", axisZoom: " + isXAxisZoom()
				+ '}';
	}

	public boolean isCenterVew() {
		return centerView;
	}

	public void setCenterView(boolean value) {
		this.centerView = value;
	}
}
