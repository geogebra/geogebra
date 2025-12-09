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
import org.geogebra.common.gui.MayHaveFocus;
import org.geogebra.web.html5.euclidian.EuclidianViewW;

public class ResetButtonTabber implements MayHaveFocus {
	private final EuclidianView view;

	public ResetButtonTabber(EuclidianView view) {
		this.view = view;
	}

	private boolean isResetVisible() {
		return view.getApplication().showResetIcon();
	}

	@Override
	public boolean focusIfVisible(boolean reverse) {
		if (isResetVisible()) {
			((EuclidianViewW) view).focusResetIcon();
			return true;
		}
		return false;
	}

	@Override
	public boolean hasFocus() {
		return view.isResetIconSelected();
	}

	@Override
	public boolean focusNext() {
		view.setResetIconSelected(false);
		return false;
	}

	@Override
	public boolean focusPrevious() {
		view.setResetIconSelected(false);
		return false;
	}

	@Override
	public AccessibilityGroup getAccessibilityGroup() {
		return AccessibilityGroup.getViewGroup(view.getViewID());
	}

	@Override
	public AccessibilityGroup.ViewControlId getViewControlId() {
		return AccessibilityGroup.ViewControlId.RESET_BUTTON;
	}
}
