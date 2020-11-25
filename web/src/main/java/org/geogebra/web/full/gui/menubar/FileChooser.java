package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FileUpload;

import elemental2.dom.File;

public class FileChooser extends FileUpload implements ChangeHandler {
	private AppW app;

	/**
	 * Constructor
	 * @param app AppW
	 */
	public FileChooser(AppW app) {
		super();
		this.app = app;
		addChangeHandler(this);
		getElement().setAttribute("accept", ".ggs");
	}

	public void open() {
		click();
	}

	@Override
	public void onChange(ChangeEvent event) {
		File selectedFile = getSelectedFile();
		app.checkSaved(success -> app.openFile(selectedFile));
		this.removeFromParent();
	}

	private native File getSelectedFile()/*-{
			var files = $doc.querySelector('input[type=file]');
			var fileToHandle = files.files[0];
			files.value = [];
			return fileToHandle;
		}-*/;
}