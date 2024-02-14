package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.gui.util.Domvas;
import org.geogebra.web.html5.gui.util.Dom;
import org.gwtproject.dom.client.Style;
import org.gwtproject.user.client.ui.ScrollPanel;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.EventListener;

/**
 * Base class for Toolbar Tabs-
 * @author Laszlo
 */
public abstract class ToolbarTab extends ScrollPanel implements ShowableTab, SetLabels {

	public static final int TAB_HEIGHT = 100;
	public static final String TAB_HEIGHT_PCT = TAB_HEIGHT + "%";

	/** Constructor */
	public ToolbarTab(ToolbarPanel parent) {
		setSize("100%", TAB_HEIGHT_PCT);
		setAlwaysShowScrollBars(false);

		EventListener onTransitionEnd = evt -> parent.setFadeTabs(false);
		Dom.addEventListener(this.getElement(), "transitionend",
				onTransitionEnd);
	}

	@Override
	public void onResize() {
		setHeight(TAB_HEIGHT_PCT);
		getContainerElement().getStyle().setHeight(TAB_HEIGHT, Style.Unit.PCT);
	}

	/**
	 * Set tab the active one.
	 * @param active to set.
	 */
	public void setActive(boolean active) {
		Dom.toggleClass(this, "tab", "tab-hidden", active);
		if (active) {
			onActive();
		}
	}

	/**
	 * Sets if tab should fade during animation or not.
	 * @param fade to set.
	 */
	public void setFade(boolean fade) {
		setStyleName("tabFade", fade);
	}

	/**
	 * Called when tab is activated.
	 */
	protected abstract void onActive();

	public boolean isActive() {
		return getElement().hasClassName("tab");
	}

	/**
	 * @param context2d context
	 * @param callback callback
	 * @param left horizontal offset
	 * @param top vertical offset
	 */
	public void paintToCanvas(CanvasRenderingContext2D context2d,
			Runnable callback, int left, int top) {
		getElement().addClassName("ggbScreenshot");
		Domvas.get().toImage(getElement(), (image) -> {
			context2d.drawImage(image, left, top);
			getElement().removeClassName("ggbScreenshot");
			callback.run();
		});
	}
}
