package org.geogebra.common.spy;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.CoordSystemAnimation;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.main.App;

class EuclidianViewSpy extends EuclidianView {

	EuclidianViewSpy(App app) {
		this.app = app;
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
	protected void initCursor() {

	}

	@Override
	protected void setStyleBarMode(int mode) {

	}

	@Override
	protected void updateSizeKeepDrawables() {

	}

	@Override
	public void repaint() {

	}

	@Override
	public GColor getBackgroundCommon() {
		return null;
	}

	@Override
	public boolean hitAnimationButton(int x, int y) {
		return false;
	}

	@Override
	public void setCursor(EuclidianCursor cursor) {

	}

	@Override
	public boolean requestFocusInWindow() {
		return false;
	}

	@Override
	public void setToolTipText(String plainTooltip) {

	}

	@Override
	public void paintBackground(GGraphics2D g2) {

	}

	@Override
	protected void drawResetIcon(GGraphics2D g) {

	}

	@Override
	public void setBackground(GColor bgColor) {

	}

	@Override
	public boolean suggestRepaint() {
		return false;
	}

	@Override
	public void clearView() {

	}

	@Override
	public boolean hasFocus() {
		return false;
	}

	@Override
	public void requestFocus() {

	}

	@Override
	public void closeDropdowns() {

	}

	@Override
	public boolean isShowing() {
		return false;
	}

	@Override
	public void setPreferredSize(GDimension preferredSize) {

	}

	@Override
	protected CoordSystemAnimation newZoomer() {
		return null;
	}

	@Override
	public void add(GBox box) {

	}

	@Override
	public void remove(GBox box) {

	}

	@Override
	public GGraphics2D getGraphicsForPen() {
		return null;
	}

	@Override
	protected EuclidianStyleBar newEuclidianStyleBar() {
		return null;
	}

	@Override
	protected void addDynamicStylebarToEV(EuclidianStyleBar dynamicStylebar) {

	}

	@Override
	protected EuclidianStyleBar newDynamicStyleBar() {
		return null;
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
	public EuclidianController getEuclidianController() {
		return null;
	}
}
