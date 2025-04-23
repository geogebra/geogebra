package org.geogebra.common.euclidian.plot.implicit;

/**
 * Bernstein polynomial plotter settings.
 */
public interface BernsteinPlotterSettings {
	boolean visualDebug();

	boolean isUpdateEnabled();

	default BernsteinImplicitAlgoSettings getAlgoSettings() {
		return new BernsteinImplicitAlgoSettingsImpl();
	}
}
