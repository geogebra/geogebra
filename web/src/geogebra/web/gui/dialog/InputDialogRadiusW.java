package geogebra.web.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.gui.dialog.handler.NumberInputHandler;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
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
		super(app, app.getPlain("Radius"), title, "", false, handler);

		this.kernel = kernel;
	}

	/**
	 * Handles button clicks for dialog.
	 */	
	@Override
    public void onClick(ClickEvent e) {
		actionPerformed(e);
	}
	
	@Override
	protected void actionPerformed(DomEvent e) {
		Object source = e.getSource();

		try {
			if (source == btOK || source == inputPanel.getTextComponent().getTextField()) {
				setVisible(!processInput());
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

	private boolean processInput() {

		// avoid labeling of num
		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		boolean ret = inputHandler.processInput(inputPanel.getText());

		cons.setSuppressLabelCreation(oldVal);

		if (ret) {
			GeoElement circle = createOutput(((NumberInputHandler) inputHandler)
					.getNum());
			GeoElement[] geos = { circle };
			app.storeUndoInfo();
			kernel.getApplication().getActiveEuclidianView()
					.getEuclidianController().memorizeJustCreatedGeos(geos);
		}

		return ret;
	}

	/**
	 * 
	 * @param num
	 * @return the circle
	 */
	abstract protected GeoElement createOutput(NumberValue num);

}
