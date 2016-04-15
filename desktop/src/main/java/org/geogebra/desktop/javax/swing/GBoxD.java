package org.geogebra.desktop.javax.swing;

import javax.swing.Box;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.GComboBox;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.javax.swing.GLabel;
import org.geogebra.desktop.awt.GDimensionD;
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
	public void add(GLabel label) {
		impl.add(((GLabelD) label).getImpl());
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
	public GDimension getPreferredSize() {
		return new GDimensionD(impl.getPreferredSize());
	}

	@Override
	public GRectangle getBounds() {
		return new GRectangleD(impl.getBounds());
	}

	@Override
	public void validate() {
		impl.validate();
	}

	@Override
	public void revalidate() {
		impl.revalidate();
	}

	@Override
	public void add(GComboBox comboBox) {
		impl.add(GComboBoxD.getJComboBox(comboBox));

	}

	// @Override
	// public geogebra.common.javax.swing.Box createHorizontalBox() {
	// return new Box(javax.swing.Box.createHorizontalBox());
	// }

}
