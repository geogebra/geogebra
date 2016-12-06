package org.geogebra.desktop.main;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MyZoomer;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.desktop.euclidian.MyZoomerD;

/** no GUI implementation of EV */
public class EuclidianViewNoGui extends EuclidianView {

	private GColor backgroundColor = GColor.WHITE;

	/**
	 * @param ec
	 *            controller
	 * @param viewNo
	 *            view number
	 * @param settings
	 *            settings
	 */
	public EuclidianViewNoGui(EuclidianController ec, int viewNo,
			EuclidianSettings settings) {
		super(ec, viewNo, settings);
		setAxesColor(GColor.BLACK);
		setGridColor(GColor.GRAY);
	}

	public void repaint() {
		// TODO Auto-generated method stub

	}

	@Override
	public final GColor getBackgroundCommon() {
		return backgroundColor;
	}

	@Override
	public final void setBackground(GColor bgColor) {
		if (bgColor != null)
			backgroundColor = GColor.newColor(bgColor.getRed(),
					bgColor.getGreen(), bgColor.getBlue(), bgColor.getAlpha());
	}

	public boolean hitAnimationButton(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setCursor(EuclidianCursor cursor) {
		// TODO Auto-generated method stub

	}

	public void setToolTipText(String plainTooltip) {
		// TODO Auto-generated method stub

	}

	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	public void requestFocus() {
		// TODO Auto-generated method stub

	}

	public void closeDropdowns() {
		// TODO Auto-generated method stub

	}

	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	public EuclidianController getEuclidianController() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean suggestRepaint() {
		// TODO Auto-generated method stub
		return false;
	}

	public void clearView() {
		// TODO Auto-generated method stub

	}

	public boolean isShowing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GGraphics2D getTempGraphics2D(GFont fontForGraphics) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GFont getFont() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setHeight(int h) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setWidth(int h) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initCursor() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setStyleBarMode(int mode) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateSizeKeepDrawables() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean requestFocusInWindow() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void paintBackground(GGraphics2D g2) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void drawResetIcon(GGraphics2D g) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPreferredSize(GDimension preferredSize) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doDrawPoints(GeoImage gi, List<GPoint> penPoints2,
			GColor penColor, int penLineStyle, int penSize) {
		// TODO Auto-generated method stub

	}

	@Override
	protected MyZoomer newZoomer() {
		return new MyZoomerD(this);
	}

	@Override
	public void add(GBox box) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(GBox box) {
		// TODO Auto-generated method stub

	}

	@Override
	public GGraphics2D getGraphicsForPen() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EuclidianStyleBar newEuclidianStyleBar() {
		// TODO Auto-generated method stub
		return null;
	}

}
