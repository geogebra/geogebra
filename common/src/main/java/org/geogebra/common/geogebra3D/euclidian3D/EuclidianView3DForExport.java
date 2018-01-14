package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.CoordSystemAnimation;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.RendererForExport;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.Format;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.main.settings.EuclidianSettings;

/**
 * 3D view in the background (no display)
 *
 */
public class EuclidianView3DForExport extends EuclidianView3D {

	/**
	 * constructor
	 * 
	 * @param ec
	 *            controller
	 * @param settings
	 *            settings
	 */
	public EuclidianView3DForExport(EuclidianController3D ec, EuclidianSettings settings) {
		super(ec, settings);
		((RendererForExport) renderer).setGeometryManager();
	}

	@Override
	protected void logInited() {
		// no 3D view really inited
	}

	@Override
	protected Renderer createRenderer() {
		return new RendererForExport(this);
	}

	@Override
	public boolean drawsLabels() {
		return false;
	}

	/**
	 * 
	 * @param format
	 *            3D format
	 * @return 3D export
	 */
	public StringBuilder export3D(final Format format) {
		renderer.drawScene();
		ExportToPrinter3D exportToPrinter = new ExportToPrinter3D(this, renderer.getGeometryManager());
		return exportToPrinter.export(format);
	}

	@Override
	public void repaint() {
		// no need
	}

	@Override
	public void setToolTipText(String plainTooltip) {
		// no need
	}

	@Override
	public boolean hasFocus() {
		return false;
	}

	@Override
	public void requestFocus() {
		// no need
	}

	@Override
	public boolean isShowing() {
		return false;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public boolean suggestRepaint() {
		return false;
	}

	@Override
	protected void createPanel() {
		// no need
	}

	@Override
	protected void setTransparentCursor() {
		// no need
	}

	@Override
	protected boolean getShiftDown() {
		return false;
	}

	@Override
	protected void setDefault2DCursor() {
		// no need
	}

	@Override
	public GGraphics2D getTempGraphics2D(GFont fontForGraphics) {
		return null;
	}

	@Override
	public GFont getFont() {
		return null;
	}

	@Override
	protected void setHeight(int h) {
		// no need
	}

	@Override
	protected void setWidth(int h) {
		// no need
	}

	@Override
	protected void setStyleBarMode(int mode) {
		// no need
	}

	@Override
	protected void updateSizeKeepDrawables() {
		// no need
	}

	@Override
	public boolean requestFocusInWindow() {
		return false;
	}

	@Override
	public void setPreferredSize(GDimension preferredSize) {
		// no need
	}

	@Override
	protected CoordSystemAnimation newZoomer() {
		return null;
	}

	@Override
	public void add(GBox box) {
		// no need
	}

	@Override
	public void remove(GBox box) {
		// no need
	}

	@Override
	protected EuclidianStyleBar newEuclidianStyleBar() {
		return null;
	}

	@Override
	protected void addDynamicStylebarToEV(EuclidianStyleBar dynamicStylebar) {
		// no need
	}

	@Override
	protected EuclidianStyleBar newDynamicStyleBar() {
		return null;
	}

	@Override
	public void readText(String text) {
		// no need
	}

}
