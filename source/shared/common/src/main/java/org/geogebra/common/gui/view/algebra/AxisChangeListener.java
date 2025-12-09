/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.AlgebraStyle;
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
				kernel.getApplication().getSettings().getAlgebra().setStyle(anyAxisVisible
						? AlgebraStyle.DEFINITION_AND_VALUE
						: AlgebraStyle.DESCRIPTION);
				view.repaintView();
			}
		}
	}
}
