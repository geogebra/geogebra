package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.web.gui.view.algebra.RadioTreeItem;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AlgebraDockPanelW extends DockPanelW {

	private static final int AUTOSCROLL_MARGIN = 10;
	ScrollPanel algebrap;
	SimplePanel simplep;
	AlgebraViewW aview = null;

	public AlgebraDockPanelW() {
		super(
				App.VIEW_ALGEBRA,	// view id 
				"AlgebraWindow", 			// view title phrase
				null,						// toolbar string
				true,						// style bar?
				2, 							// menu order
				'A'							// menu shortcut
			);
		setViewImage(getResources().styleBar_algebraView());
	}

	@Override
	protected Widget loadComponent() {
		if (algebrap == null) {
			algebrap = new ScrollPanel();//temporarily
			algebrap.setSize("100%", "100%");
			algebrap.setAlwaysShowScrollBars(false);
		}
		if (app != null) {
			// force loading the algebra view,
			// as loadComponent should only load when needed
			setAlgebraView((AlgebraViewW) app.getAlgebraView());
			aview.setInputPanel();
		}
		return algebrap;
	}

	@Override
	protected Widget loadStyleBar() {
		return aview.getStyleBar(true);
	}

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
			algebrap.addDomHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					int bt = simplep.getAbsoluteTop()
							+ simplep.getOffsetHeight();
					if (event.getClientY() > bt) {
						app.getSelectionManager().clearSelectedGeos();
						av.resetItems(true);
					}
				}
			}, ClickEvent.getType());
		}
	}

	public ScrollPanel getAbsolutePanel() {
	    return algebrap;
    }

	@Override
	public void onResize() {
		if (aview != null) {
			aview.resize();
		}
	}

	@Override
    public ResourcePrototype getIcon() {
		return getResources().menu_icon_algebra();
	}

	/**
	 * scrolls to a specific position of the panel
	 * 
	 * @param position
	 *            to scroll to.
	 */
	public void scrollTo(int position) {
		if (this.algebrap != null) {
			this.algebrap.setVerticalScrollPosition(position);
		}
	}

	/**
	 * scrolls to the bottom of the panel
	 */
	public void scrollToBottom(){
		if (this.algebrap != null) {
			this.algebrap.scrollToBottom();
		}
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		return ((AlgebraViewW) app.getAlgebraView()).getActiveTreeItem();
	}

	/**
	 * Scroll to the item that is selected.
	 */
	public void scrollToActiveItem() {

		final RadioTreeItem item = aview.getActiveTreeItem();
		if (item == null) {
			return;
		}

		if (item.isInputTreeItem()) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {

					algebrap.scrollToBottom();
				}
			});
			return;
		}

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				int spH = algebrap.getOffsetHeight();
				// int kH = (int) (app.getAppletFrame().getKeyboardHeight());
				int spTop = item.getAbsoluteTop() - aview.getAbsoluteTop();
				int itemTop = spTop
						- spH + item.getOffsetHeight() + AUTOSCROLL_MARGIN;
				//
				// int absTop = (spTop + algebrap.getVerticalScrollPosition())
				// % (spH + kH);
				//
				// Log.debug(
				// "[AVS] absTop: " + absTop + " scroll to: " + itemTop);
				//
				// if (spH < absTop) {
				// Log.debug("[AVS] scrollolololllollllllllllllll");
					scrollTo(itemTop);
				// }
			}
		});


	}
}
