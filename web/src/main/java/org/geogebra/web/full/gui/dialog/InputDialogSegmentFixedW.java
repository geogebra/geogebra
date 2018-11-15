package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.gui.dialog.handler.SegmentHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.DomEvent;

/**
 * Dialog for segment with fixed radius.
 */
public class InputDialogSegmentFixedW extends InputDialogW {
	private GeoPointND geoPoint1;
	private Kernel kernel;

	/**
	 * @param app
	 *            application
	 * @param title
	 *            title
	 * @param handler
	 *            input handler
	 * @param point1
	 *            startpoint
	 * @param kernel
	 *            kernel
	 */
	public InputDialogSegmentFixedW(AppW app, String title,
			InputHandler handler, GeoPointND point1, Kernel kernel) {
		super(app, app.getLocalization().getMenu("Length"), title, "", false,
				handler);
		this.kernel = kernel;
		geoPoint1 = point1;
	}

	@Override
	protected void actionPerformed(DomEvent<?> e) {
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
		new SegmentHandler(geoPoint1, kernel).doSegmentFixedAsync(
				inputPanel.getText(),
				(NumberInputHandler) getInputHandler(), this,
				new AsyncOperation<Boolean>() {

					@Override
					public void callback(Boolean ok) {
						setVisible(!ok);
					}
				});
	}

}
