package geogebra.web.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.gui.dialog.handler.NumberInputHandler;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.DialogManager;
import geogebra.html5.main.AppW;

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
