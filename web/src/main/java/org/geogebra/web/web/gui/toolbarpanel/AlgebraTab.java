package org.geogebra.web.web.gui.toolbarpanel;

import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.web.gui.view.algebra.RadioTreeItem;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.SimplePanel;

class AlgebraTab extends ToolbarPanel.ToolbarTab {
	/**
	 * 
	 */
	private final ToolbarPanel toolbarPanel;
	/** AV wrapper */
	SimplePanel simplep;
	/** Algebra view **/
	AlgebraViewW aview = null;

	private int savedScrollPosition;

	/**
	 * @param toolbarPanel
	 *            parent toolbar panel
	 */
	public AlgebraTab(ToolbarPanel toolbarPanel) {
		this.toolbarPanel = toolbarPanel;
		if (this.toolbarPanel.app != null) {
			setAlgebraView((AlgebraViewW) this.toolbarPanel.app.getAlgebraView());
			aview.setInputPanel();
		}
	}

	/**
	 * @param av
	 *            algebra view
	 */
	public void setAlgebraView(final AlgebraViewW av) {
		if (av != aview) {
			if (aview != null && simplep != null) {
				simplep.remove(aview);
				remove(simplep);
			}

			simplep = new SimplePanel(aview = av);
			add(simplep);
			simplep.addStyleName("algebraSimpleP");
			addStyleName("algebraPanel");
			addStyleName("matAvDesign");
			addDomHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					int bt = simplep.getAbsoluteTop()
							+ simplep.getOffsetHeight();
					if (event.getClientY() > bt) {
						AlgebraTab.this.toolbarPanel.app.getSelectionManager().clearSelectedGeos();
						av.resetItems(true);
					}
				}
			}, ClickEvent.getType());
		}
	}

	@Override
	public void onResize() {
		super.onResize();
		setWidth(this.toolbarPanel.getTabWidth() + "px");
		if (aview != null) {
			aview.resize(this.toolbarPanel.getTabWidth());
		}
	}

	/**
	 * Scroll to make active item visible
	 */
	public void scrollToActiveItem() {

		final RadioTreeItem item = aview == null ? null
				: aview.getActiveTreeItem();
		if (item == null) {
			return;
		}

		if (item.isInputTreeItem()) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {

					scrollToBottom();
				}
			});
			return;
		}
		doScrollToActiveItem();
	}

	public void saveScrollPosition() {
		savedScrollPosition = getVerticalScrollPosition();
	}

	private void doScrollToActiveItem() {
		final RadioTreeItem item = aview.getActiveTreeItem();

		int spH = getOffsetHeight();

		int top = item.getElement().getOffsetTop();

		int relTop = top - savedScrollPosition;

		if (spH < relTop + item.getOffsetHeight()) {
			int pos = top + item.getOffsetHeight() - spH;
			setVerticalScrollPosition(pos);
		}
	}

	@Override
	public void focusFirstElement() {
		aview.focusFirst();
	}

	@Override
	public void focusLastElement() {
		aview.getInputTreeItem().getElement().focus();
	}

}