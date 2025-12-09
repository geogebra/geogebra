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

package org.geogebra.web.full.gui.applet.panel;

import javax.annotation.CheckForNull;

import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.full.gui.applet.FrameWithHeaderAndKeyboard;
import org.geogebra.web.full.gui.layout.panels.AnimatingPanel;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.gwtproject.dom.style.shared.Position;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.Widget;

/**
 * Shows and hides a panel. If the panel is an AnimatingPanel then the transition (showing/hiding)
 * will be animated.
 */
public class PanelTransitioner {

	private static final int Z_INDEX = 100;

	private FrameWithHeaderAndKeyboard mainFrame;
	private MyHeaderPanel currentPanel;

	/**
	 * @param mainFrame The main on which the panel appears.
	 */
	public PanelTransitioner(FrameWithHeaderAndKeyboard mainFrame) {
		this.mainFrame = mainFrame;
	}

	/**
	 * @return Returns the panel that is currently visible.
	 * Returns null if there aren't any panels on top of the main frame.
	 */
	public @CheckForNull MyHeaderPanel getCurrentPanel() {
		return currentPanel;
	}

	/**
	 * @param panel Shows this full-screen panel.
	 */
	public void showPanel(MyHeaderPanel panel) {
		currentPanel = panel;
		prepareLayoutForShowingPanel();
		updateAnimateInStyle();
		mainFrame.add(panel);
		setupPanel();
	}

	private void prepareLayoutForShowingPanel() {
		hideNotNeededElements();
		if (willUseFadeAnimation()) {
			hideFrameElements();
		}
	}

	private boolean willUseFadeAnimation() {
		return currentPanel instanceof AnimatingPanel
				&& ((AnimatingPanel) currentPanel).willUseFadeAnimation();
	}

	private void hideNotNeededElements() {
		mainFrame.closeKeyboard();
		GeoGebraFrameW.hideAllTooltips();
	}

	private void hideFrameElements() {
		final int childCount = mainFrame.getWidgetCount();
		for (int i = 0; i < childCount; i++) {
			Widget w = mainFrame.getWidget(i);
			if (!w.getStyleName().contains("TabbedKeyBoard")) {
				w.addStyleName("temporarilyHidden");
			}
		}
	}

	private void updateAnimateInStyle() {
		if (currentPanel instanceof AnimatingPanel) {
			((AnimatingPanel) currentPanel).updateAnimateInStyle();
		}
	}

	private void setupPanel() {
		currentPanel.getElement().getStyle().setZIndex(Z_INDEX);
		currentPanel.getElement().getStyle().setTop(0, Unit.PX);
		currentPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		final int oldHeight = mainFrame.getOffsetHeight();
		final int oldWidth = mainFrame.getOffsetWidth();
		currentPanel.setHeight(oldHeight + "px");
		currentPanel.setWidth(oldWidth + "px");
		currentPanel.onResize();
		currentPanel.setVisible(true);
		currentPanel.setFrame(mainFrame);
	}

	/**
	 * Hide the full-sized GUI, e.g. material browser
	 *
	 * @param bg
	 *            full-sized GUI
	 */
	public void hidePanel(MyHeaderPanel bg) {
		if (currentPanel == null) {
			return; // MOW-394: childVisible is outdated, return
		}
		updateAnimateOutStyle();
		mainFrame.remove(bg == null ? currentPanel : bg);
		currentPanel = null;
		hideNotNeededElements();
		showFrameElements();
		mainFrame.onPanelHidden();
	}

	private void updateAnimateOutStyle() {
		if (currentPanel instanceof AnimatingPanel) {
			((AnimatingPanel) currentPanel).updateAnimateOutStyle();
		}
	}

	private void showFrameElements() {
		final int childCount = mainFrame.getWidgetCount();
		for (int i = 0; i < childCount; i++) {
			mainFrame.getWidget(i).removeStyleName("temporarilyHidden");
		}
	}
}
