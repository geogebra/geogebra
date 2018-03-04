package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.DomEvent;

/**
 * Circle or sphere dialog
 *
 */
public abstract class InputDialogRadiusW extends InputDialogW{

	/** current kernel */
	protected Kernel kernel;

	/**
	 * 
	 * @param app
	 *            application
	 * @param title
	 *            title
	 * @param handler
	 *            input handler
	 * @param kernel
	 *            kernel
	 */
	public InputDialogRadiusW(AppW app, String title, InputHandler handler,
			Kernel kernel) {
		super(app, app.getLocalization().getMenu("Radius"), title, "", false,
				handler);

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

		getInputHandler().processInput(inputPanel.getText(), this,
				new AsyncOperation<Boolean>() {

					@Override
					public void callback(Boolean ok) {
						cons.setSuppressLabelCreation(oldVal);
						if (ok) {
							GeoElement circle = createOutput(getNumber());
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
	 * @return input as number
	 */
	protected GeoNumberValue getNumber() {
		return ((NumberInputHandler) getInputHandler()).getNum();
	}

	/**
	 * 
	 * @param num
	 *            radius value
	 * @return the circle
	 */
	abstract protected GeoElement createOutput(GeoNumberValue num);

}
