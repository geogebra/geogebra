package org.geogebra.common.euclidian.plot.implicit;

class BernsteinImplicitAlgoSettingsImpl
		implements BernsteinImplicitAlgoSettings {
	private int boxSize = 10;

	@Override
	public int minBoxWidthInPixels() {
		return boxSize;
	}

	@Override
	public int minBoxHeightInPixels() {
		return boxSize;
	}

	public void setBoxSize(int size) {
		this.boxSize = size;
	}
}
