package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.DomEvent;

public class InputDialogSegmentFixedW extends InputDialogW {
	private GeoPointND geoPoint1;

	private Kernel kernel;

	public InputDialogSegmentFixedW(AppW app, String title,
			InputHandler handler, GeoPointND point1, Kernel kernel) {
		super(app, app.getPlain("Length"), title, "", false, handler, null);

		geoPoint1 = point1;
		this.kernel = kernel;
	}

	
	@Override
	protected void actionPerformed(DomEvent e) {
		Object source = e.getSource();

		try {
			if (source == btOK || sourceShouldHandleOK(source)) {
				processInput();
			} else if (source == btApply) {
				processInput();
			} else if (source == btCancel) {
				setVisible(false);
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			setVisible(false);
		}
	}

	private void processInput() {

		// avoid labeling of num
		final Construction cons = kernel.getConstruction();
		final boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		inputHandler.processInput(inputPanel.getText(), this,
				new AsyncOperation<Boolean>() {

					@Override
					public void callback(Boolean ok) {
						cons.setSuppressLabelCreation(oldVal);
						if (ok) {
							DialogManager.doSegmentFixed(kernel, geoPoint1,
									((NumberInputHandler) inputHandler)
											.getNum());
						}
						setVisible(!ok);
					}
				});




	}

}
