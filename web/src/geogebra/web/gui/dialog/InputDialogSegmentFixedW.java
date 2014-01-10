package geogebra.web.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.gui.dialog.handler.NumberInputHandler;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.DialogManager;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;

public class InputDialogSegmentFixedW extends InputDialogW {
	private GeoPoint geoPoint1;

	private Kernel kernel;

	public InputDialogSegmentFixedW(AppW app, String title,
			InputHandler handler, GeoPoint point1, Kernel kernel) {
		super(app, app.getPlain("Length"), title, "", false, handler, null);

		geoPoint1 = point1;
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
			DialogManager.doSegmentFixed(kernel, geoPoint1,
					((NumberInputHandler) inputHandler).getNum());
		}

		return ret;
	}

}
