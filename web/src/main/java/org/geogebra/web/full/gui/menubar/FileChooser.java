package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.ChangeEvent;
import org.gwtproject.event.dom.client.ChangeHandler;
import org.gwtproject.user.client.ui.FileUpload;

import elemental2.dom.File;
import elemental2.dom.HTMLInputElement;
import jsinterop.base.Js;

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

	private File getSelectedFile() {
		HTMLInputElement fileInput = Js.uncheckedCast(getElement());
		File fileToHandle = fileInput.files.getAt(0);
		fileInput.value = "";
		return fileToHandle;
	}
}