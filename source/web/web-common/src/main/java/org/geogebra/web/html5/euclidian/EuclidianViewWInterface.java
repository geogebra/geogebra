package org.geogebra.web.html5.euclidian;

import java.util.function.Consumer;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.Widget;

/**
 * interface for EuclidianViewW / EuclidianView3DW
 * 
 * @author mathieu
 *
 */
public interface EuclidianViewWInterface extends EuclidianViewInterfaceSlim {

	/**
	 * 
	 * @return canvas
	 */
	Element getCanvasElement();

	Hits getHits();

	boolean hasStyleBar();

	EuclidianStyleBar getStyleBar();

	int getViewWidth();

	/**
	 * @param x
	 *            event x-coord
	 * @param y
	 *            event y-coord
	 * @param type
	 *            event type
	 * @return whether textfield was clicked
	 */
	boolean textfieldClicked(int x, int y, PointerEventType type);

	@Override
	int getViewID();

	double getXZero();

	double getYZero();

	@Override
	double getXscale();

	/**
	 * @param xZero screen coordinate of x=0 in real world coords
	 * @param yZero screen coordinate of y=0 in real world coords
	 * @param xScale x-scale
	 * @param yScale y-scale
	 */
	void setCoordSystem(double xZero, double yZero, double xScale, double yScale);

	@Override
	double getYscale();

	void rememberOrigins();

	/**
	 * @param dx x-axis shift
	 * @param dy y-axis shift
	 * @param dz z-axis shift
	 */
	void translateCoordSystemInPixels(int dx, int dy, int dz);

	void setHits(GPoint gPoint, PointerEventType touch);

	Previewable getPreviewDrawable();

	void updatePreviewableForProcessMode();

	int getAbsoluteLeft();

	int getAbsoluteTop();

	GGraphics2DWI getG2P();

	void resetPointerEventHandler();

	/**
	 * TODO is this really used for PDF?
	 * @param scale export scale
	 * @param transparent whether to use transparent background
	 * @param format export format (PNG, WEBP)
	 * @param greyscale whether to use greyscale colors
	 * @return data URL in given formt
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

	App getApplication();

	String getCanvasBase64WithTypeString();

	void requestFocus();

	void getExportSVG(boolean transparency, Consumer<String> callback);

	/**
	 * @param scale scale
	 * @param dpi resolution
	 * @return PDF content as a string
	 */
	String getExportPDF(double scale, double dpi);

	int getExportWidth();

	int getExportHeight();

	boolean isAttached();

	void add(Widget box);

	Object getExportCanvas();
}
