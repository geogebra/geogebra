package geogebra.web.gui.dialog;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.DialogManager;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
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
