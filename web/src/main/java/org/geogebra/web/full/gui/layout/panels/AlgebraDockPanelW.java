package org.geogebra.web.full.gui.layout.panels;

import javax.annotation.CheckForNull;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.layout.DockSplitPaneW;
import org.geogebra.web.full.gui.toolbarpanel.AlgebraViewScroller;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.LatexTreeItemController;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Panel;
import org.gwtproject.user.client.ui.ScrollPanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Classic (no toolbar) dock panel for algebra
 */
public class AlgebraDockPanelW extends NavigableDockPanelW
		implements AlgebraPanelInterface {

	private ScrollPanel algebrap;
	private FlowPanel wrapper;
	private AlgebraViewW aview = null;

	private final DockPanelDecorator decorator;

	private @CheckForNull AlgebraViewScroller scroller = null;

	/**
	 * Create new dockapanel for algebra
	 *
	 * @param decorator
	 *            panel decorator
	 * @param hasStyleBar
	 *            whether to add stylebar
	 */
	public AlgebraDockPanelW(DockPanelDecorator decorator, boolean hasStyleBar) {
		super(App.VIEW_ALGEBRA, null, hasStyleBar);
		this.decorator = decorator;
	}

	@Override
	protected Panel getViewPanel() {
		if (algebrap == null) {
			algebrap = new ScrollPanel();
			algebrap.setWidth("100%");
			algebrap.setAlwaysShowScrollBars(false);
		}
		if (app != null) {
			// force loading the algebra view,
			// as loadComponent should only load when needed
			setAlgebraView((AlgebraViewW) app.getAlgebraView());
			if (app.showAlgebraInput()) {
				aview.setInputPanel();
			}
		}
		if (decorator == null) {
			return algebrap;
		}
		return decorator.decorate(algebrap, app);
	}

	@Override
	protected Widget loadStyleBar() {
		return aview.getStyleBar(true);
	}

	/**
	 * @param av
	 *            algebra view
	 */
	public void setAlgebraView(final AlgebraViewW av) {
		if (av != aview) {
			if (aview != null && wrapper != null) {
				wrapper.remove(aview);
				algebrap.remove(wrapper);
			}
			wrapper = new FlowPanel();
			aview = av;
			wrapper.add(aview);
			if (decorator != null) {
				decorator.addLogo(wrapper, app);
			}
			algebrap.add(wrapper);
			algebrap.addStyleName("algebraPanel");
			algebrap.addDomHandler(event -> algebraPanelClicked(av, event), ClickEvent.getType());
			scroller = new AlgebraViewScroller(algebrap, aview);
		}
	}

	/**
	 * @param av
	 *            algebra view
	 * @param event
	 *            click event
	 */
	protected void algebraPanelClicked(AlgebraViewW av, ClickEvent event) {
		int bt = wrapper.getAbsoluteTop() + wrapper.getOffsetHeight();
		if (event.getClientY() > bt) {
			app.getSelectionManager().clearSelectedGeos();
			av.resetItems(true);
			app.hideMenu();
		}
	}

	/**
	 * @return scroll panel
	 */
	public ScrollPanel getAbsolutePanel() {
		return algebrap;
	}

	@Override
	public void onResize() {
		DockSplitPaneW split = getParentSplitPane();
		if (split != null && split.isForcedLayout()) {
			if (aview != null
					&& split.getOrientation() == SwingConstants.HORIZONTAL_SPLIT) {
				int w = getOffsetWidth();
				aview.setUserWidth(w);
			}
		}
		if (getOffsetHeight() > 0 && aview != null) {
			aview.resize(0);
			scrollToActiveItem();
		}
		if (decorator != null && aview != null) {
			decorator.onResize(aview, getOffsetHeight());
		}
	}

	@Override
	public ResourcePrototype getIcon() {
		return getResources().menu_icon_algebra();
	}

	@Override
	public void scrollAVToBottom() {
		if (scroller != null) {
			scroller.toBottom();
		}
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		if (app.getInputPosition() != InputPosition.algebraView) {
			return null;
		}
		return ((AlgebraViewW) app.getAlgebraView()).getActiveTreeItem();
	}

	@Override
	public MathKeyboardListener updateKeyboardListener(MathKeyboardListener ml) {
		return updateKeyboardListenerForView(aview, ml);
	}

	@Override
	public int getNavigationRailWidth() {
		return 0;
	}

	@Override
	public void scrollToActiveItem() {
		if (scroller != null) {
			scroller.toActiveItem();
		}
	}

	/**
	 * Saves the current scroll position of the dock panel.
	 */
	@Override
	public void saveAVScrollPosition() {
		if (scroller != null) {
			scroller.save();
		}
	}

	@Override
	public int getInnerWidth() {
		if (wrapper == null) {
			return super.getOffsetWidth();
		}
		return wrapper.getOffsetWidth();
	}

	@Override
	public DockPanelData.TabIds getTabId() {
		return DockPanelData.TabIds.ALGEBRA;
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return getResources().styleBar_algebraView();
	}

	/**
	 * Start editing and return keyboard adapter.
	 *
	 * @param aview
	 *            algebra view
	 * @param ml
	 *            fallback
	 * @return keyboard listener for edited item or fallback
	 */
	public static MathKeyboardListener updateKeyboardListenerForView(
			AlgebraViewW aview, MathKeyboardListener ml) {
		if (aview != null && aview.getInputTreeItem() != ml) {
			return ml;
		}
		// if no retex editor yet
		if (!(ml instanceof RadioTreeItem)) {
			return ml;
		}
		LatexTreeItemController itemController = ((RadioTreeItem) ml)
				.getLatexController();
		itemController.initAndShowKeyboard(false);
		return itemController.getRetexListener();
	}

	@Override
	public void resizeContent(Panel content) {
		// no resize here, size is in %
	}

	@Override
	public double getMinVHeight(boolean keyboard) {
		RadioTreeItem inputTreeItem = aview.getInputTreeItem();
		if (inputTreeItem == null) {
			return 120;
		}
		return Math.max(inputTreeItem.getOffsetHeight(), 120);
	}

	@Override
	public void setLabels() {
		super.setLabels();
		if (decorator != null) {
			decorator.setLabels();
		}
	}
}
