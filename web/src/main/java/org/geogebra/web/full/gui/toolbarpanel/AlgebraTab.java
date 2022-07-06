package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.util.CustomScrollbar;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Algebra tab of tool panel
 */
public class AlgebraTab extends ToolbarPanel.ToolbarTab {

	private static final int SCROLLBAR_WIDTH = 8; // 8px in FF, 4px in Chrome => take 8px
	final private App app;
	private final ToolbarPanel toolbarPanel;
	private FlowPanel wrapper;
	/** Algebra view **/
	AlgebraViewW aview = null;
	private final LogoAndName logo;
	private int savedScrollPosition;

	/**
	 * @param toolbarPanel
	 *            parent toolbar panel
	 */
	public AlgebraTab(ToolbarPanel toolbarPanel) {
		super(toolbarPanel);
		this.toolbarPanel = toolbarPanel;
		app = toolbarPanel.getApp();
		logo = new LogoAndName(app);
		setAlgebraView((AlgebraViewW) app.getAlgebraView());
		aview.setInputPanel();
	}

	/**
	 * @param av
	 *            algebra view
	 */
	public void setAlgebraView(final AlgebraViewW av) {
		if (av != aview) {
			if (aview != null && wrapper != null) {
				wrapper.remove(aview);
				remove(wrapper);
			}

			wrapper = new FlowPanel();
			aview = av;
			wrapper.add(aview);
			wrapper.add(logo);
			add(wrapper);
			addStyleName("algebraPanel");
			CustomScrollbar.apply(this);
			addDomHandler(this::emptyAVclicked, ClickEvent.getType());
		}
	}

	/**
	 * @param evt
	 *            click event
	 */
	protected void emptyAVclicked(ClickEvent evt) {
		int bt = wrapper.getAbsoluteTop() + wrapper.getOffsetHeight();
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
		toolbarPanel.close(false);
	}

	@Override
	public void onResize() {
		super.onResize();
		int tabWidth = this.toolbarPanel.getTabWidth();
		setWidth(tabWidth + "px");
		if (aview != null) {
			aview.setUserWidth(tabWidth);
			aview.resize(tabWidth - SCROLLBAR_WIDTH);
			logo.onResize(aview, toolbarPanel.getTabHeight());
			scrollToActiveItem();
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

	@Override
	public void setLabels() {
		logo.setLabels();
	}
}