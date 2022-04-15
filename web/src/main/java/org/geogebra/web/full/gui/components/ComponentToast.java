package org.geogebra.web.full.gui.components;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.SimplePanel;

public class ComponentToast extends GPopupPanel {
	private SimplePanel content;

	/**
	 * constructor
	 * @param app - see {@link AppW}
	 * @param contentStr - content of the toast
	 */
	public ComponentToast(AppW app, String contentStr) {
		super(app.getPanel(), app);
		addStyleName("toast");
		buildGUI(contentStr);
		setPopupPosition(0, 0);
		app.getPanel().add(this);
	}

	private void buildGUI(String contentStr) {
		content = new SimplePanel();
		content.addStyleName("content");
		content.getElement().setInnerHTML(contentStr);
		add(content);
	}

	public void updateContent(String contentStr) {
		content.getElement().setInnerHTML(contentStr);
	}

	@Override
	public void show() {
		addStyleName("fadeIn");
	}

	@Override
	public void hide() {
		removeStyleName("fadeIn");
	}
}
