package org.geogebra.common.euclidian.plot.implicit;

public class BernsteinPlotterDebugSettings implements BernsteinPlotterSettings {

	@Override
	public boolean visualDebug() {
		return true;
	}

	@Override
	public boolean isUpdateEnabled() {
		return false;
	}
}