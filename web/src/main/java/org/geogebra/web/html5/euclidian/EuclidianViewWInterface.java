package org.geogebra.web.html5.euclidian;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;

import com.google.gwt.canvas.client.Canvas;

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
	public Canvas getCanvas();

	public Hits getHits();

	public boolean isInFocus();

	public boolean hasStyleBar();

	public EuclidianStyleBar getStyleBar();

	public int getViewWidth();

	/**
	 * @param x
	 *            event x-coord
	 * @param y
	 *            event y-coord
	 * @param type
	 *            event type
	 * @return whether textfield was clicked
	 */
	public boolean textfieldClicked(int x, int y, PointerEventType type);

	@Override
	public int getViewID();

	public double getXZero();

	public double getYZero();

	@Override
	public double getXscale();

	public void setCoordSystem(double xZero, double yZero, double xscale,
	        double newRatioY);

	@Override
	public double getYscale();

	public void rememberOrigins();

	public void translateCoordSystemInPixels(int dx, int dy, int dz);

	public void setHits(GPoint gPoint, PointerEventType touch);

	public Previewable getPreviewDrawable();

	public void updatePreviewableForProcessMode();

	public int getAbsoluteLeft();

	public int getAbsoluteTop();

	public GGraphics2DWI getG2P();

	public void resetPointerEventHandler();

	public String getExportImageDataUrl(double scale, boolean transparent,
			ExportType format, boolean greyscale);

	public String getExportImageDataUrl(double scale, boolean transparent,
			boolean greyscale);

	public App getApplication();

	public String getCanvasBase64WithTypeString();

	public void requestFocus();

	public void updateFirstAndLast(boolean attach, boolean anyway);

	public void setAltText();

	public String getExportSVG(double scale, boolean transparency);

	public String getExportPDF(double scale);

	public int getExportWidth();

	public int getExportHeight();
}
