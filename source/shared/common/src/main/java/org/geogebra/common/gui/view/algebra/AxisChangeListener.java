package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.SettingListener;

import com.google.j2objc.annotations.Weak;

public class AxisChangeListener implements SettingListener {

	@Weak
	private AlgebraView view;
	@Weak
	private Kernel kernel;
	private boolean isAnyAxisVisible;

	/**
	 * @param view
	 *            algebra view
	 * @param kernel
	 *            kernel
	 * @param defaultSetting
	 *            view settings (for initialization)
	 */
	public AxisChangeListener(AlgebraView view, Kernel kernel,
			EuclidianSettings defaultSetting) {
		this.view = view;
		this.kernel = kernel;
		this.isAnyAxisVisible = isAnyAxisVisible(defaultSetting);
	}

	private boolean isAnyAxisVisible(EuclidianSettings euclidianSettings) {
		return euclidianSettings.getShowAxis(0)
				|| euclidianSettings.getShowAxis(1);
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		if (settings instanceof EuclidianSettings) {
			EuclidianSettings euclidianSettings = (EuclidianSettings) settings;
			boolean anyAxisVisible = isAnyAxisVisible(euclidianSettings);
			if (anyAxisVisible != isAnyAxisVisible) {
				isAnyAxisVisible = anyAxisVisible;
				kernel.setAlgebraStyle(anyAxisVisible
						? Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE
						: Kernel.ALGEBRA_STYLE_DESCRIPTION);
				view.repaintView();
			}
		}
	}
}
