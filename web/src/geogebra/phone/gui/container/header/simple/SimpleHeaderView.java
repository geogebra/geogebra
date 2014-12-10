package geogebra.phone.gui.container.header.simple;

import geogebra.html5.gui.FastClickHandler;
import geogebra.phone.gui.event.EventUtil;
import geogebra.phone.gui.event.ViewChangeEvent;
import geogebra.phone.gui.view.View;
import geogebra.web.gui.util.StandardButton;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SimpleHeaderView extends SimplePanel implements FastClickHandler {

	private View view;

	private StandardButton button;

	private boolean active;

	public SimpleHeaderView(View view) {
		this.view = view;
		setStyleName("tab");
		createButton();
	}

	public View getView() {
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
