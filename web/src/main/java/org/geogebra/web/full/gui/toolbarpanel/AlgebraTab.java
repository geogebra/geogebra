package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.layout.ViewCounter;
import org.geogebra.web.full.gui.view.algebra.AlgebraCanvasExporter;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.util.CustomScrollbar;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Panel;

import elemental2.dom.CanvasRenderingContext2D;

/**
 * Algebra tab of tool panel
 */
public class AlgebraTab extends ToolbarTab {

	private static final int SCROLLBAR_WIDTH = 8; // 8px in FF, 4px in Chrome => take 8px
	final private App app;
	private final ToolbarPanel toolbarPanel;
	private FlowPanel wrapper;
	/** Algebra view **/
	AlgebraViewW aview = null;
	private final LogoAndName logo;

	private final AlgebraViewScroller scroller;

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
		scroller = new AlgebraViewScroller(this, aview);
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
			aview.resetInputItemHeader();
			wrapper.add(aview);
			wrapper.add(logo);
			add(decorate(wrapper));
			CustomScrollbar.apply(this);
			addDomHandler(this::emptyAVclicked, ClickEvent.getType());
		}
	}

	private Panel decorate(Panel algebrap) {
		return getDecorator().decorate(this, algebrap, (AppW) app);
	}

	private DockPanelDecorator getDecorator() {
		return toolbarPanel.getDecorator();
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
		DockPanelDecorator decorator = getDecorator();
		decorator.onResize(aview, getTabHeight());
		resizeAlgebraView(tabWidth);

	}

	private void resizeAlgebraView(int tabWidth) {
		if (aview == null) {
			return;
		}
		aview.setUserWidth(tabWidth);
		aview.resize(tabWidth - SCROLLBAR_WIDTH);
		logo.onResize(aview, getTabHeight());
		scrollToActiveItem();
	}

	public int getTabHeight() {
		return getDecorator().getTabHeight(toolbarPanel.getTabHeight());
	}

	/**
	 * Scroll to make active item visible
	 */
	public void scrollToActiveItem() {
		scroller.toActiveItem();
	}

	/**
	 * Save scroll position
	 */
	public void saveScrollPosition() {
		scroller.save();
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
	public DockPanelData.TabIds getID() {
		return DockPanelData.TabIds.ALGEBRA;
	}

	@Override
	public void setLabels() {
		logo.setLabels();
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		if (app.getInputPosition() != App.InputPosition.algebraView) {
			return null;
		}
		return ((AlgebraViewW) app.getAlgebraView()).getActiveTreeItem();
	}

	@Override
	public void paintToCanvas(CanvasRenderingContext2D context2d,
			ViewCounter counter, int left, int top) {
		AlgebraCanvasExporter exporter = new AlgebraCanvasExporter(aview, context2d,
				getOffsetWidth());
		exporter.paintToCanvas(left, top);
		if (counter != null) {
			counter.decrement();
		}
	}
}