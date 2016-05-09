package org.geogebra.web.web.gui.dialog;

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

public class InputDialogDilateW extends InputDialogW {
	GeoPointND[] points;
	GeoElement[] selGeos;

	private Kernel kernel;
	
	private EuclidianController ec;

	public InputDialogDilateW(AppW app, String title, InputHandler handler,
			GeoPointND[] points, GeoElement[] selGeos, Kernel kernel, EuclidianController ec) {
	
		super(app, app.getMenu("Dilate.Factor"), title, null, false, handler, false);
		
		inputHandler = handler;

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

		inputHandler.processInput(inputPanel.getText(), this,
				new AsyncOperation<Boolean>() {

					@Override
					public void callback(Boolean ok) {
						cons.setSuppressLabelCreation(oldVal);

						if (ok) {
							DialogManager
									.doDilate(kernel,
											((NumberInputHandler) inputHandler)
													.getNum(),
											points, selGeos, ec);
						}
						setVisible(!ok);
					}
				});





		/*
		 * 
		 * // avoid labeling of num Construction cons =
		 * kernel.getConstruction(); boolean oldVal =
		 * cons.isSuppressLabelsActive(); cons.setSuppressLabelCreation(true);
		 * 
		 * boolean success = inputHandler.processInput(inputPanel.getText());
		 * 
		 * cons.setSuppressLabelCreation(oldVal);
		 * 
		 * if (success) { NumberValue num =
		 * ((NumberInputHandler)inputHandler).getNum();
		 * 
		 * if (selGeos.length > 0) { // mirror all selected geos //GeoElement []
		 * selGeos = getSelectedGeos(); GeoPoint2 point = points[0];
		 * ArrayList<GeoElement> ret = new ArrayList<GeoElement>(); for (int
		 * i=0; i < selGeos.length; i++) { if (selGeos[i] != point) { if
		 * ((selGeos[i] instanceof Transformable) || selGeos[i].isGeoList())
		 * ret.addAll(Arrays.asList(kernel.Dilate(null, selGeos[i], num,
		 * point))); } } if (!ret.isEmpty()) {
		 * kernel.getApplication().getActiveEuclidianView
		 * ().getEuclidianController().memorizeJustCreatedGeos(ret);
		 * app.storeUndoInfo(); } return true; } } return false;
		 */

	}

}
