package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.DomEvent;

public class InputDialogRegularPolygonW extends InputDialogW{
	private GeoPointND geoPoint1, geoPoint2;
	private EuclidianController ec;

	public InputDialogRegularPolygonW(AppW app, EuclidianController ec, String title,
			InputHandler handler, GeoPointND point1, GeoPointND point2) {
		super(app, app.getPlain("Points"), title, "4", false, handler, true);

		geoPoint1 = point1;
		geoPoint2 = point2;
		
		this.ec = ec;
	}

	
	@Override
	protected void actionPerformed(DomEvent e) {
		Object source = e.getSource();
		try {
			if (source == btOK || sourceShouldHandleOK(source)) {
				if(processInput()){
					wrappedPopup.hide();
				}
//				setVisibleForTools(!processInput());
//			} else if (source == btApply) {  //There is no apply button.
//				processInput();
			} else if (source == btCancel) {
				wrappedPopup.hide();
//				setVisibleForTools(false);
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
//			setVisibleForTools(false);
		}
	}

	private boolean processInput() {

		return DialogManager.makeRegularPolygon(app, ec, inputPanel.getText(),
				geoPoint1, geoPoint2);
	}
}
