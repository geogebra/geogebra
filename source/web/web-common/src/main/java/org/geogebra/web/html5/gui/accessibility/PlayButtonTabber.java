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

package org.geogebra.web.html5.gui.accessibility;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.FocusableComponent;

public class PlayButtonTabber implements FocusableComponent {
	private final EuclidianView view;

	public PlayButtonTabber(EuclidianView view) {
		this.view = view;
	}

	private boolean isPlayVisible() {
		return view.getKernel().needToShowAnimationButton()
				&& view.drawPlayButtonInThisView();
	}

	@Override
	public boolean focusIfVisible(boolean reverse) {
		if (isPlayVisible()) {
			view.setAnimationButtonSelected(true);
			return true;
		}
		return false;
	}

	@Override
	public boolean hasFocus() {
		return view.isAnimationButtonSelected();
	}

	@Override
	public boolean focusNext() {
		view.setAnimationButtonSelected(false);
		return false;
	}

	@Override
	public boolean focusPrevious() {
		view.setAnimationButtonSelected(false);
		return false;
	}

	@Override
	public AccessibilityGroup getAccessibilityGroup() {
		return AccessibilityGroup.getViewGroup(view.getViewID());
	}

	@Override
	public AccessibilityGroup.ViewControlId getViewControlId() {
		return AccessibilityGroup.ViewControlId.PLAY_BUTTON;
	}
}
