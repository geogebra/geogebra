package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.ScrollPanel;

public class AlgebraViewScroller {
	private int savedPosition;
	private final ScrollPanel panel;
	private final AlgebraViewW view;

	/**
	 *
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
		if (item == null) {
			return;
		}

		if (item.isInputTreeItem()) {
			Scheduler.get().scheduleDeferred(this::toBottom);
		} else {
			Scheduler.get().scheduleDeferred(this::setPositionToActiveItem);

		}
	}

	/**
	 * Save scroll position
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
