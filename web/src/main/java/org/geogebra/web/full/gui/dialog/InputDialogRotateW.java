package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.DomEvent;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Generic rotate dialog
 */
public abstract class InputDialogRotateW extends AngleInputDialogW {
	/** selcted polygons */
	GeoPolygon[] polys;
	/** selected geos */
	GeoElement[] selGeos;
	/** controller */
	protected EuclidianController ec;

	/** 45 degrees */
	final protected static String DEFAULT_ROTATE_ANGLE = Unicode.FORTY_FIVE_DEGREES_STRING;

	/**
	 * @param app
	 *            application
	 * @param title
	 *            title
	 * @param handler
	 *            input handler
	 * @param polys
	 *            selected polygons
	 * @param selGeos
	 *            selected geos
	 * @param ec
	 *            controller
	 */
	public InputDialogRotateW(AppW app, String title,
			InputHandler handler, GeoPolygon[] polys, 
			GeoElement[] selGeos, EuclidianController ec) {
		super(app, app.getLocalization().getMenu("Angle"), title,
				DEFAULT_ROTATE_ANGLE, false,
				handler, false);

		this.polys = polys;
		this.selGeos = selGeos;
		this.ec = ec;

	}

	@Override
	protected void actionPerformed(DomEvent<?> e) {
		Object source = e.getSource();

		try {
			if (source == btOK || sourceShouldHandleOK(source)) {
				//
				processInput(new AsyncOperation<String>() {

					@Override
					public void callback(String obj) {
						// FIXME setVisibleForTools(!processInput());
						if (obj == null) {
							// wrappedPopup.show();
							inputPanel.getTextComponent().hideTablePopup();
						} else {
							setVisible(false);

						}
					}
				});

			} else if (source == btCancel) {
				setVisible(false);

			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			setVisible(false);
		}
	}

	/**
	 * @param op
	 *            callback
	 */
	protected abstract void processInput(AsyncOperation<String> op);
}
