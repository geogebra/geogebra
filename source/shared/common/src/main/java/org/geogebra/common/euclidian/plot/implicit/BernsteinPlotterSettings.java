package org.geogebra.common.euclidian.plot.implicit;

public interface BernsteinPlotterSettings {
	boolean visualDebug();

	boolean isUpdateEnabled();

	default BernsteinImplicitAlgoSettings getAlgoSettings() {
		return new BernsteinImplicitAlgoSettingsImpl();
	}
}
