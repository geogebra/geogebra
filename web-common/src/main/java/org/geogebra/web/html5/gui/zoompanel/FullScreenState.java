package org.geogebra.web.html5.gui.zoompanel;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.StyleInjector;
import org.gwtproject.dom.client.Element;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLStyleElement;
import elemental2.dom.ScrollToOptions;

public class FullScreenState {
	private HTMLStyleElement transformOverride;
	private final HashMap<String, String> containerProps = new HashMap<>();
	protected boolean fullScreenActive = false;
	protected boolean emulated;
	private GDimension oldSize;
	private double cssScale = 0;
	private double scrollTop;
	private double scrollLeft;

	/**
	 * @return css scale
	 */
	public double getCssScale() {
		return cssScale;
	}

	/**
	 * Resetting position and margins.
	 *
	 * @param container
	 *            to reset.
	 */
	protected void resetStyleAfterFullscreen(Element container, AppW app) {
		if (container != null) {
			for (Map.Entry<String, String> e : containerProps.entrySet()) {
				if (!StringUtil.empty(e.getValue())) {
					container.getStyle().setProperty(e.getKey(), e.getValue());
				} else {
					container.getStyle().clearProperty(e.getKey());
				}
			}
		}
		if (oldSize != null && app.isUnbundled()) {
			app.getGgbApi().setSize(oldSize.getWidth(), oldSize.getHeight());
		}
		ScrollToOptions scrollToOptions = ScrollToOptions.create();
		scrollToOptions.setLeft(scrollLeft);
		scrollToOptions.setTop(scrollTop);
		scrollToOptions.setBehavior("instant");
		DomGlobal.document.documentElement.scrollTo(scrollToOptions);
	}

	/**
	 * @param container
	 *            container
	 * @param propName
	 *            property name
	 * @param value
	 *            value of property
	 */
	public void setContainerProp(Element container, String propName,
			String value) {
		containerProps.put(propName,
				container.getStyle().getProperty(propName));
		container.getStyle().setProperty(propName, value);
	}

	/**
	 * @return True if full screen is active, false else
	 */
	public boolean isFullScreenActive() {
		return fullScreenActive;
	}

	protected void store(Element container, AppW app, double scale) {
		String containerPositionBefore = container.getStyle()
				.getPosition();
		if (StringUtil.empty(containerPositionBefore)) {
			containerPositionBefore = "static";
		}
		containerProps.clear();
		containerProps.put("position", containerPositionBefore);
		setContainerProp(container, "width", "100%");
		setContainerProp(container, "height", "100%");
		setContainerProp(container, "maxWidth", "100%");
		setContainerProp(container, "maxHeight", "100%");
		setContainerProp(container, "marginLeft", "0");
		setContainerProp(container, "marginTop", "0");
		oldSize = app.getPreferredSize();
		scrollTop = DomGlobal.document.documentElement.scrollTop;
		scrollLeft = DomGlobal.document.documentElement.scrollLeft;
		cssScale = scale;
		if (emulated) {
			overrideParentTransform();
			setContainerProp(container, "left", "0px");
			container.addClassName("GeoGebraFullscreenContainer");
		}
	}

	/**
	 * Remove the inline style for transform overriding
	 */
	protected void removeTransformOverride() {
		if (transformOverride != null) {
			transformOverride.remove();
		}
	}

	private void overrideParentTransform() {
		transformOverride = StyleInjector.injectStyleSheet(
				"*:not(.ggbTransform){transform: none !important;}");
	}
}
