package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Algebra tab of tool panel
 */
public class AlgebraTab extends ToolbarPanel.ToolbarTab {

	private static final int SCROLLBAR_WIDTH = 8; // 8px in FF, 4px in Chrome => take 8px
	final private App app;
	private final ToolbarPanel toolbarPanel;
	private SimplePanel simplep;
	/** Algebra view **/
	AlgebraViewW aview = null;

	private int savedScrollPosition;

	/**
	 * @param toolbarPanel
	 *            parent toolbar panel
	 */
	public AlgebraTab(ToolbarPanel toolbarPanel) {
		super(toolbarPanel);
		this.toolbarPanel = toolbarPanel;
		app = toolbarPanel.getApp();
		if (app != null) {
			setAlgebraView((AlgebraViewW) app.getAlgebraView());
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
			addStyleName("customScrollbar");
			addDomHandler(this::emptyAVclicked, ClickEvent.getType());
		}
	}

	/**
	 * @param evt
	 *            click event
	 */
	protected void emptyAVclicked(ClickEvent evt) {
		int bt = simplep.getAbsoluteTop() + simplep.getOffsetHeight();
		if (evt.getClientY() > bt && aview != null) {
			app.getSelectionManager()
					.clearSelectedGeos();
			aview.resetItems(true);
		}
	}

	@Override
	public void open() {
		toolbarPanel.openAlgebra(true);
	}

	@Override
	public void close() {
		toolbarPanel.close();
	}

	@Override
	public void onResize() {
		super.onResize();
		setWidth(this.toolbarPanel.getTabWidth() + "px");
		if (aview != null) {
			int w = this.toolbarPanel.getTabWidth();
			aview.setUserWidth(w);
			aview.resize(this.toolbarPanel.getTabWidth() - SCROLLBAR_WIDTH);
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
			Scheduler.get().scheduleDeferred(this::scrollToBottom);
			return;
		}
		doScrollToActiveItem();
	}

	/**
	 * Save scroll position
	 */
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

	/**
	 * Give focus to AV Input.
	 * 
	 * @return if focusing was successful.
	 */
	public boolean focusInput() {
		RadioTreeItem input = aview.getInputTreeItem();
		if (input == null || !input.isVisible()) {
			return false;
		}

		input.getLatexController().stopEdit();
		input.ensureEditing();
		return true;
	}

	@Override
	protected void onActive() {
		// unused
	}
}