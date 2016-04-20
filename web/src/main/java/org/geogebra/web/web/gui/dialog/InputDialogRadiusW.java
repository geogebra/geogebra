package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.DomEvent;



public abstract class InputDialogRadiusW extends InputDialogW{

	/** current kernel */
	protected Kernel kernel;

	/**
	 * 
	 * @param app
	 * @param title
	 * @param handler
	 * @param kernel
	 */
	public InputDialogRadiusW(AppW app, String title, InputHandler handler,
			Kernel kernel) {
		super(app, app.getPlain("Radius"), title, "", false, handler, null);

		this.kernel = kernel;
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

		inputHandler.processInput(inputPanel.getText(),
				new AsyncOperation<Boolean>() {

					@Override
					public void callback(Boolean ok) {
						cons.setSuppressLabelCreation(oldVal);
						if (ok) {
							GeoElement circle = createOutput(((NumberInputHandler) inputHandler)
									.getNum());
							GeoElement[] geos = { circle };
							app.storeUndoInfoAndStateForModeStarting();
							kernel.getApplication().getActiveEuclidianView()
									.getEuclidianController().memorizeJustCreatedGeos(geos);
						}
						setVisible(!ok);
					}
				});

		

		

	}

	/**
	 * 
	 * @param num
	 * @return the circle
	 */
	abstract protected GeoElement createOutput(NumberValue num);

}
