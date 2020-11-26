package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.DialogData;

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
	 * @param data
	 *            dialog data
	 * @param handler
	 *            input handler
	 * @param polys
	 *            selected polygons
	 * @param selGeos
	 *            selected geos
	 * @param ec
	 *            controller
	 */
	public InputDialogRotateW(AppW app, DialogData data,
			InputHandler handler, GeoPolygon[] polys, 
			GeoElement[] selGeos, EuclidianController ec) {
		super(app, app.getLocalization().getMenu("Angle"), data,
				DEFAULT_ROTATE_ANGLE, handler, false);
		this.polys = polys;
		this.selGeos = selGeos;
		this.ec = ec;
	}

	@Override
	public void processInput() {
		processInput(obj -> {
			if (obj == null) {
				getTextComponent().hideTablePopup();
			} else {
				hide();
			}
		});
	}

	/**
	 * @param op
	 *            callback
	 */
	protected abstract void processInput(AsyncOperation<String> op);
}