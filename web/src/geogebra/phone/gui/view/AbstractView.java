package geogebra.phone.gui.view;

import geogebra.html5.main.AppW;

import com.google.gwt.resources.client.ImageResource;

public abstract class AbstractView implements View {

	protected ImageResource icon;
	protected ViewPanel viewPanel;
	protected HeaderPanel headerPanel;
	protected StyleBarPanel styleBarPanel;

	protected AppW app;

	public AbstractView(AppW app) {
		this.app = app;
	}

	public ImageResource getViewIcon() {
		if (icon == null) {
			icon = createViewIcon();
		}
		return icon;
	}

	public ViewPanel getViewPanel() {
		if (viewPanel == null) {
			viewPanel = createViewPanel();
		}
		return viewPanel;
	}

	public HeaderPanel getHeaderPanel() {
		if (headerPanel == null) {
			headerPanel = createHeaderPanel();
		}
		return headerPanel;
	}

	public StyleBarPanel getStyleBarPanel() {
		if (styleBarPanel == null) {
			styleBarPanel = createStyleBarPanel();
		}
		return styleBarPanel;
	}

	protected abstract ImageResource createViewIcon();

	protected abstract ViewPanel createViewPanel();

	protected abstract HeaderPanel createHeaderPanel();

	protected abstract StyleBarPanel createStyleBarPanel();
}
