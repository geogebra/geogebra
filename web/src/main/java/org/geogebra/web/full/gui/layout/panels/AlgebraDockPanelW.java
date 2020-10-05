package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.layout.DockSplitPaneW;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.LatexTreeItemController;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Classic (no toolbar) dock panel for algebra
 */
public class AlgebraDockPanelW extends NavigableDockPanelW
		implements AlgebraPanelInterface {

	private ScrollPanel algebrap;
	private SimplePanel simplep;
	private AlgebraViewW aview = null;
	private int savedScrollPosition;
	private DockPanelDecorator decorator;

	/**
	 * Create new dockapanel for algebra
	 *
	 * @param decorator
	 *            panel decorator
	 * @param hasStyleBar
	 *            whether to add stylebar
	 */
	public AlgebraDockPanelW(DockPanelDecorator decorator, boolean hasStyleBar) {
		super(
				App.VIEW_ALGEBRA,	// view id
				"AlgebraWindow", 			// view title phrase
				null,						// toolbar string
				hasStyleBar, // style bar?
				2, 							// menu order
				'A'							// menu shortcut
			);
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
			aview.setInputPanel();
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
			if (aview != null && simplep != null) {
				simplep.remove(aview);
				algebrap.remove(simplep);
			}
			simplep = new SimplePanel(aview = av);
			algebrap.add(simplep);
			simplep.addStyleName("algebraSimpleP");
			algebrap.addStyleName("algebraPanel");
			algebrap.addStyleName("matAvDesign");
			algebrap.addDomHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					algebraPanelClicked(av, event);
				}
			}, ClickEvent.getType());
		}
	}

	/**
	 * @param av
	 *            algebra view
	 * @param event
	 *            click event
	 */
	protected void algebraPanelClicked(AlgebraViewW av, ClickEvent event) {
		int bt = simplep.getAbsoluteTop() + simplep.getOffsetHeight();
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
		if (getOffsetHeight() > 0) {
			if (aview != null) {
				aview.resize(0);
			}
		}
		if (decorator != null) {
			decorator.onResize();
		}
	}

	@Override
	public ResourcePrototype getIcon() {
		return getResources().menu_icon_algebra();
	}

	/**
	 * @param position
	 *            distance from top
	 */
	public void scrollTo(int position) {
		if (this.algebrap != null) {
			this.algebrap.setVerticalScrollPosition(position);
		}
	}

	@Override
	public void scrollAVToBottom() {
		if (this.algebrap != null) {
			this.algebrap.scrollToBottom();
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
					scrollAVToBottom();
				}
			});
			return;
		}
		doScrollToActiveItem();
	}

	/**
	 * Saves the current scroll position of the dock panel.
	 */
	@Override
	public void saveAVScrollPosition() {
		savedScrollPosition = algebrap.getVerticalScrollPosition();
	}

	private void doScrollToActiveItem() {
		final RadioTreeItem item = aview.getActiveTreeItem();
		int spH = algebrap.getOffsetHeight();
		int top = item.getElement().getOffsetTop();
		int relTop = top - savedScrollPosition;
		if (spH < relTop + item.getOffsetHeight()) {
			int pos = top + item.getOffsetHeight() - spH;
			algebrap.setVerticalScrollPosition(pos);
		}
	}

	@Override
	public int getInnerWidth() {
		if (simplep == null) {
			return super.getOffsetWidth();
		}
		return simplep.getOffsetWidth();
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
}
