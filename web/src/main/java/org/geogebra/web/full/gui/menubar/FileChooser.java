package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.full.gui.browser.BrowseGUI;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FileUpload;

public class FileChooser extends FileUpload implements ChangeHandler {
	private BrowseGUI bg;

	/**
	 * Constructor
	 * @param app AppW
	 */
	public FileChooser(AppW app) {
		super();
		bg = new BrowseGUI(app, this);
		addChangeHandler(this);
		getElement().setAttribute("accept", ".ggs");
	}

	public void open() {
		click();
	}

	@Override
	public void onChange(ChangeEvent event) {
		bg.openFile(getSelectedFile());
		this.removeFromParent();
	}

	private native JavaScriptObject getSelectedFile()/*-{
			var files = $doc.querySelector('input[type=file]');
			var fileToHandle = files.files[0];
			files.value = [];
			return fileToHandle;
		}-*/;
}