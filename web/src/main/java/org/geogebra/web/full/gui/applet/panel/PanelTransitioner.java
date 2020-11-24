package org.geogebra.web.full.gui.applet.panel;

import javax.annotation.Nullable;

import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.full.gui.applet.FrameWithHeaderAndKeyboard;
import org.geogebra.web.full.gui.layout.panels.AnimatingPanel;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;

import com.google.gwt.dom.client.Style;

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
	@Nullable
	public MyHeaderPanel getCurrentPanel() {
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
		mainFrame.keyBoardNeeded(false, null);
		ToolTipManagerW.hideAllToolTips();
	}

	private void hideFrameElements() {
		final int childCount = mainFrame.getWidgetCount();
		for (int i = 0; i < childCount; i++) {
			mainFrame.getWidget(i).addStyleName("temporarilyHidden");
		}
	}

	private void updateAnimateInStyle() {
		if (currentPanel instanceof AnimatingPanel) {
			((AnimatingPanel) currentPanel).updateAnimateInStyle();
		}
	}

	private void setupPanel() {
		currentPanel.getElement().getStyle().setZIndex(Z_INDEX);
		currentPanel.getElement().getStyle().setTop(0, Style.Unit.PX);
		currentPanel.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
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
