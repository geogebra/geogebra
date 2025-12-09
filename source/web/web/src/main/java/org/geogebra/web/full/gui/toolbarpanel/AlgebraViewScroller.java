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

package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.ui.ScrollPanel;

public class AlgebraViewScroller {
	private int savedPosition;
	private final ScrollPanel panel;
	private final AlgebraViewW view;

	/**
	 * @param panel to scroll
	 * @param view the algebra view inside the panel.
	 */
	public AlgebraViewScroller(ScrollPanel panel, AlgebraViewW view) {
		this.panel = panel;
		this.view = view;
	}

	/**
	 * Scroll to make active item visible
	 */
	public void toActiveItem() {

		final RadioTreeItem item = view == null ? null
				: view.getActiveTreeItem();
		if (item == null || !item.hasFocus()) {
			return;
		}

		if (item.isInputTreeItem()) {
			Scheduler.get().scheduleDeferred(this::toBottom);
		} else {
			Scheduler.get().scheduleDeferred(this::setPositionToActiveItem);

		}
	}

	/**
	 * Save scroll position.
	 */
	public void save() {
		savedPosition = panel.getVerticalScrollPosition();
	}

	private void setPositionToActiveItem() {
		final RadioTreeItem item = view.getActiveTreeItem();

		int splitterHeight = panel.getOffsetHeight();

		int absoluteItemTop = item.getElement().getOffsetTop();

		int itemTop = absoluteItemTop - savedPosition;

		int pos = splitterHeight < itemTop + item.getOffsetHeight()
				? absoluteItemTop + item.getOffsetHeight() - splitterHeight
				: absoluteItemTop;
		panel.setVerticalScrollPosition(pos);
	}

	/**
	 * Scrolls the panel to the bottom.
	 */
	public void toBottom() {
		panel.scrollToBottom();
	}
}
