/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.view.consprotocol.ConstructionProtocolNavigationW;
import org.gwtproject.dom.style.shared.Overflow;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.Panel;
import org.gwtproject.user.client.ui.Widget;

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
		consProtNav = (ConstructionProtocolNavigationW) app.getGuiManager()
				.getConstructionProtocolNavigation(id);
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
}
