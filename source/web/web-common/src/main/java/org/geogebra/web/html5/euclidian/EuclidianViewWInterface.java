/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.euclidian;

import java.util.function.Consumer;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.web.awt.GGraphics2DWI;
import org.geogebra.web.html5.gui.HasThumbnailURL;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.Widget;

/**
 * interface for EuclidianViewW / EuclidianView3DW
 * 
 * @author mathieu
 *
 */
public interface EuclidianViewWInterface extends EuclidianViewInterfaceCommon, HasThumbnailURL {

	/**
	 * 
	 * @return canvas element
	 */
	Element getCanvasElement();

	/**
	 * @param x
	 *            event x-coord
	 * @param y
	 *            event y-coord
	 * @param type
	 *            event type
	 * @return whether an input box was clicked
	 */
	boolean textfieldClicked(int x, int y, PointerEventType type);

	/**
	 * @return absolute left in the browser window
	 */
	int getAbsoluteLeft();

	/**
	 * @return absolute top in the browser window
	 */
	int getAbsoluteTop();

	/**
	 * @return the graphics
	 */
	GGraphics2DWI getG2P();

	/**
	 * Reset pointer event handler.
	 */
	void resetPointerEventHandler();

	/**
	 * TODO is this really used for PDF?
	 * @param scale export scale
	 * @param transparent whether to use transparent background
	 * @param format export format (PNG, WEBP)
	 * @param greyscale whether to use greyscale colors
	 * @return data URL in given format
	 */
	String getExportImageDataUrl(double scale, boolean transparent,
			ExportType format, boolean greyscale);

	/**
	 * @param scale export scale
	 * @param transparent whether to use transparent background
	 * @param greyscale whether to use greyscale colors
	 * @return data URL (PNG)
	 */
	String getExportImageDataUrl(double scale, boolean transparent,
			boolean greyscale);

	/**
	 * @param transparency whether to make the background transparent
	 * @param callback gets the base64 encoded SVG
	 */
	void getExportSVG(boolean transparency, Consumer<String> callback);

	/**
	 * @param scale scale
	 * @param dpi resolution
	 * @return PDF content as a string
	 */
	String getExportPDF(double scale, double dpi);

	/**
	 * @return whether this is attached to DOM
	 */
	boolean isAttached();

	/**
	 * Add a widget floating over the view.
	 * @param box widget
	 */
	void add(Widget box);

	/**
	 * @return canvas for export
	 */
	Object getExportCanvas();
}
