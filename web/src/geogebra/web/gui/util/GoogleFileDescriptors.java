package geogebra.web.gui.util;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author gabor
 * Creates A google File descriptors for showing file informations from Google.
 *
 */
public class GoogleFileDescriptors extends PopupPanel {
	
	/**
	 * creates an instance of GoogleFileDescriptors
	 */
	
	private Label fileName = null;
	private Label fileLabel = null;
	private String descriptionTitle = null;
	private HorizontalPanel p = null;
	
	public GoogleFileDescriptors() {
		super();
		add(p = new HorizontalPanel());
		p.add(fileLabel = new Label("Opened file: "));
		fileLabel.addStyleName("fileLabel");
		p.add(fileName = new Label());
		fileName.addStyleName("fileName");
		addStyleName("GoogleFileDescriptors");
	}
	
	public void setFileName(String fn) {
		fileName.setText(fn);
	}
	
	public void setDescription(String ds) {
		fileName.setTitle(ds);
	}

}
