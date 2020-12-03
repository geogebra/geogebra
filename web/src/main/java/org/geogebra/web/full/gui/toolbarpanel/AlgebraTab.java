package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Algebra tab of tool panel
 */
public class AlgebraTab extends ToolbarPanel.ToolbarTab {

	private App app;
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
			addDomHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					emptyAVclicked(av, event.getClientY());
				}
			}, ClickEvent.getType());
		}
	}

	/**
	 * @param av
	 *            algebra view
	 * @param y
	 *            y-offset of the click
	 */
	protected void emptyAVclicked(AlgebraViewW av, int y) {
		int bt = simplep.getAbsoluteTop() + simplep.getOffsetHeight();
		if (y > bt) {
			app.getSelectionManager()
					.clearSelectedGeos();
			av.resetItems(true);
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