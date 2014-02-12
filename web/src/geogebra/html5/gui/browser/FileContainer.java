package geogebra.html5.gui.browser;

import geogebra.html5.gui.ResizeListener;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FileContainer extends VerticalPanel implements ResizeListener {

	private FlowPanel fileControlPanel;
	private final VerticalMaterialPanel filePanel;
	private HorizontalPanel filePages;
	private Label heading = new Label();

	public FileContainer(String headingName,
			final VerticalMaterialPanel filePanel) {
		this.filePanel = filePanel;
		this.addHeading(headingName);
		this.add(filePanel);
	}

	private void addHeading(String headingName) {
		this.heading.setText(headingName);
		this.heading.setStyleName("filePanelTitle");
		this.add(this.heading);
	}

	public void setHeading(String headingName) {
		this.heading.setText(headingName);
	}

	@Override
	public void onResize() {
		int contentHeight = Window.getClientHeight() - BrowseGUI.HEADING_HEIGHT;
		this.setHeight(contentHeight + "px");
		this.filePanel.setHeight(contentHeight - BrowseGUI.HEADING_HEIGHT
				- 10 + "px");
	}
}

