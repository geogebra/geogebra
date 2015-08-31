package org.geogebra.web.web.gui.util;

import org.geogebra.web.html5.gui.GPopupPanel;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * @author gabor
 * Creates A google File descriptors for showing file informations from Google.
 *
 */
public class GoogleFileDescriptors extends GPopupPanel {
	
	/**
	 * creates an instance of GoogleFileDescriptors
	 */
	
	private Label fileName = null;
	private Label fileLabel = null;
	private String descriptionTitle = null;
	private HorizontalPanel p = null;
	
	public GoogleFileDescriptors(Panel root) {
		super(root);
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
