package org.geogebra.desktop.main;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.MyZoomer;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.main.settings.EuclidianSettings;

public class EuclidianView3DNoGui extends EuclidianView3D
		implements EuclidianView3DInterface {

	public EuclidianView3DNoGui(EuclidianController3D ec,
			EuclidianSettings settings) {
		super(ec, settings);
	}


	public void repaint() {
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

	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean suggestRepaint() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isShowing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void createPanel() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Renderer createRenderer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setTransparentCursor() {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean getShiftDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void setDefault2DCursor() {
		// TODO Auto-generated method stub

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
	public void setPreferredSize(GDimension preferredSize) {
		// TODO Auto-generated method stub

	}

	@Override
	protected MyZoomer newZoomer() {
		// TODO Auto-generated method stub
		return null;
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
	protected EuclidianStyleBar newEuclidianStyleBar() {
		// TODO Auto-generated method stub
		return null;
	}



}
