package org.geogebra.desktop.javax.swing;

import javax.swing.Box;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.awt.GRectangleD;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;

/**
 * Wrapper for javax.swing.Box
 * 
 * @author Judit Elias
 */
public class GBoxD extends GBox {

	private Box impl = null;

	/**
	 * Creates new wrapper Box
	 * 
	 * @param box
	 *            box to be wrapped
	 */
	public GBoxD(Box box) {
		this.impl = box;
	}

	/**
	 * Returns the wrapped box
	 * 
	 * @return wrapped box
	 */
	public Box getImpl() {
		return this.impl;
	}

	@Override
	public void add(AutoCompleteTextField textField) {
		impl.add((AutoCompleteTextFieldD) textField);

	}

	@Override
	public void setVisible(boolean isVisible) {
		impl.setVisible(isVisible);
	}

	@Override
	public void setBounds(GRectangle rect) {
		impl.setBounds(GRectangleD.getAWTRectangle(rect));
	}

	@Override
	public GRectangle getBounds() {
		return new GRectangleD(impl.getBounds());
	}

	@Override
	public void revalidate() {
		impl.revalidate();
	}

	@Override
	public void repaint(GGraphics2D g) {
		g.translate(impl.getBounds().getX(), impl.getBounds().getY());
		impl.paint(GGraphics2DD.getAwtGraphics(g));
		g.translate(-impl.getBounds().getX(), -impl.getBounds().getY());
	}

	@Override
	public boolean isVisible() {
		return impl.isVisible();
	}

	// @Override
	// public geogebra.common.javax.swing.Box createHorizontalBox() {
	// return new Box(javax.swing.Box.createHorizontalBox());
	// }

}
