package org.geogebra.web.phone.gui.container.header.simple;

import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.phone.gui.event.EventUtil;
import org.geogebra.web.phone.gui.event.ViewChangeEvent;
import org.geogebra.web.phone.gui.view.AbstractView;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SimpleHeaderView extends SimplePanel implements FastClickHandler {

	private AbstractView view;

	private StandardButton button;

	private boolean active;

	public SimpleHeaderView(AbstractView view) {
		this.view = view;
		setStyleName("tab");
		createButton();
	}

	public AbstractView getView() {
		return view;
	}

	private void createButton() {
		button = new StandardButton(view.getViewIcon());
		button.addStyleName("phoneHeaderButton");
		button.addFastClickHandler(this);
		add(button);
	}

	public void setActive(boolean active) {
		this.active = active;
		setStyleName("activeTab", active);
	}

	public void onClick(Widget source) {
		if (!active) {
			EventUtil.fireEvent(new ViewChangeEvent(view));
		}
	}

}
