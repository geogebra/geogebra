package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.DomEvent;

/**
 * Dialog for dilate tool
 */
public class InputDialogDilateW extends InputDialogW {
	private GeoPointND[] points;
	private GeoElement[] selGeos;

	private Kernel kernel;
	
	private EuclidianController ec;

	/**
	 * @param app
	 *            application
	 * @param title
	 *            title
	 * @param handler
	 *            value handler
	 * @param points
	 *            input points
	 * @param selGeos
	 *            transformed geos
	 * @param kernel
	 *            kernel
	 * @param ec
	 *            euclidian controller
	 */
	public InputDialogDilateW(AppW app, String title, InputHandler handler,
			GeoPointND[] points, GeoElement[] selGeos, Kernel kernel, EuclidianController ec) {
	
		super(app, app.getLocalization().getMenu("Dilate.Factor"), title, null,
				false, handler, false);
		
		setInputHandler(handler);

		this.points = points;
		this.selGeos = selGeos;
		this.kernel = kernel;	
		this.ec = ec;
	}

	@Override
	protected void actionPerformed(DomEvent e) {
		Object source = e.getSource();

		try {
			if (source == btOK || sourceShouldHandleOK(source)) {
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

		getInputHandler().processInput(inputPanel.getText(), this,
				new AsyncOperation<Boolean>() {

					@Override
					public void callback(Boolean ok) {
						cons.setSuppressLabelCreation(oldVal);

						if (ok) {
							dilate();
						}
						setVisible(!ok);
					}
				});
	}

	/**
	 * Execute the dilation with input.
	 */
	protected void dilate() {
		DialogManager.doDilate(kernel,
				((NumberInputHandler) getInputHandler()).getNum(), points,
				selGeos, ec);
	}

}
