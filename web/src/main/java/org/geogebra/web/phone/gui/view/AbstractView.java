package org.geogebra.web.phone.gui.view;

import org.geogebra.web.html5.main.AppW;

import com.google.gwt.resources.client.ImageResource;

public abstract class AbstractView {

	protected ImageResource icon;
	protected ViewPanel viewPanel;
	protected HeaderPanel headerPanel;
	protected StyleBar styleBar;

	protected AppW app;

	public AbstractView(AppW app) {
		this.app = app;
	}

	/**
	 * @return the icon of the view
	 */
	public ImageResource getViewIcon() {
		if (icon == null) {
			icon = createViewIcon();
		}
		return icon;
	}

	/**
	 * @return the panel the view
	 */
	public ViewPanel getViewPanel() {
		if (viewPanel == null) {
			viewPanel = createViewPanel();
		}
		return viewPanel;
	}

	/**
	 * @return the header of the view if exists, null otherwise
	 */
	public HeaderPanel getHeaderPanel() {
		if (headerPanel == null) {
			headerPanel = createHeaderPanel();
		}
		return headerPanel;
	}

	/**
	 * @return the stylebar of the view, if exists, null otherwise
	 */
	public StyleBar getStyleBar() {
		if (styleBar == null) {
			styleBar = createStyleBar();
		}
		return styleBar;
	}

	protected abstract ImageResource createViewIcon();

	protected abstract ViewPanel createViewPanel();

	protected abstract HeaderPanel createHeaderPanel();

	protected abstract StyleBar createStyleBar();
}
