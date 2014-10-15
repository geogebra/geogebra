package geogebra.web.gui;

import geogebra.html5.main.AppW;

public class CustomizeToolbarGUI extends MyHeaderPanel {

	private AppW app;
	private CustomizeToolbarHeaderPanel header;

	public CustomizeToolbarGUI(AppW app) {
		this.app = app;
		addHeader();
		addContent();
	}

	private void addContent() {
	    // TODO Auto-generated method stub
	    
    }

	private void addHeader() {
		header = new CustomizeToolbarHeaderPanel(app, this);
		setHeaderWidget(header);
		
	}

	public void setLabels() {
		if (header != null) {
			header.setLabels();
		}
	}

}
