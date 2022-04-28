package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.view.consprotocol.ConstructionProtocolNavigationW;
import org.gwtproject.resources.client.ResourcePrototype;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel other than EV that can have navigation bar
 */
public abstract class NavigableDockPanelW extends DockPanelW {
	private ConstructionProtocolNavigationW consProtNav;
	private InnerPanel innerPanel;

	/**
	 * @param id
	 *            view ID
	 * @param toolbar
	 *            toolbar definition
	 * @param hasStyleBar
	 *            whether to enable stylebar
	 */
	public NavigableDockPanelW(int id, String toolbar, boolean hasStyleBar) {
		super(id, toolbar, hasStyleBar);
	}

	@Override
	public final void updateNavigationBar() {
		if (app.getShowCPNavNeedsUpdate(id)) {
			app.setShowConstructionProtocolNavigation(
					app.showConsProtNavigation(id), id);
		}
		if (app.showConsProtNavigation(id) && consProtNav == null) {
			this.addNavigationBar();
		}
		if (consProtNav != null) {
			consProtNav.update();
			consProtNav.setVisible(app.showConsProtNavigation(id));
			onResize();
		}
	}

	@Override
	protected final Widget loadComponent() {
		setViewImage(getViewIcon());
		innerPanel = new InnerPanel(this, getViewPanel());

		addNavigationBar();
		return innerPanel;
	}

	@Override
	protected abstract ResourcePrototype getViewIcon();

	/**
	 * @return panel wrapping the view
	 */
	protected abstract Panel getViewPanel();

	@Override
	public int navHeight() {
		if (this.consProtNav != null
				&& this.consProtNav.getImpl().getOffsetHeight() != 0) {
			return this.consProtNav.getImpl().getOffsetHeight();
		}
		return 30;
	}

	/**
	 * Add construction navigation bar.
	 */
	public final void addNavigationBar() {
		consProtNav = (ConstructionProtocolNavigationW) (app.getGuiManager()
				.getConstructionProtocolNavigation(id));
		consProtNav.getImpl().addStyleName("consProtNav");
		if (innerPanel == null) {
			loadComponent();
		}
		innerPanel.add(consProtNav.getImpl()); // may be invisible, but
													// made
		// visible later
		updateNavigationBar();
	}

	@Override
	protected void initWidget(Widget widget) {
		if (widget != null) {
			widget.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		}
		super.initWidget(widget);
	}

	@Override
	protected void addZoomPanel(MyDockLayoutPanel dockLayoutPanel,
			InsertPanel controls) {
		if (zoomPanel != null) {
			// This causes EV overlap toolbar
			// dockPanel.getElement().getStyle().setProperty("minHeight",
			// zoomPanel.getMinHeight());
			dockLayoutPanel.addSouth(zoomPanel, 0);

		}
	}

}
