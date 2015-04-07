package org.geogebra.desktop.javax.swing;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.AbstractJComboBox;

/**
 * Wrapper for javax.swing.Box
 * 
 * @author Judit Elias
 */
public class BoxD extends org.geogebra.common.javax.swing.GBox {

	private javax.swing.Box impl = null;

	/**
	 * Creates new wrapper Box
	 * 
	 * @param box
	 *            box to be wrapped
	 */
	public BoxD(javax.swing.Box box) {
		this.impl = box;
	}

	/**
	 * Returns the wrapped box
	 * 
	 * @return wrapped box
	 */
	public javax.swing.Box getImpl() {
		return this.impl;
	}

	@Override
	public void add(org.geogebra.common.javax.swing.GLabel label) {
		impl.add(((org.geogebra.desktop.javax.swing.GLabelD) label).getImpl());
	}

	@Override
	public void add(AutoCompleteTextField textField) {
		impl.add((org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD) textField);

	}

	@Override
	public void setVisible(boolean isVisible) {
		impl.setVisible(isVisible);
	}

	@Override
	public void setBounds(GRectangle rect) {
		impl.setBounds(org.geogebra.desktop.awt.GRectangleD.getAWTRectangle(rect));
	}

	@Override
	public GDimension getPreferredSize() {
		return new org.geogebra.desktop.awt.GDimensionD(impl.getPreferredSize());
	}

	@Override
	public GRectangle getBounds() {
		return new org.geogebra.desktop.awt.GRectangleD(impl.getBounds());
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
	public void add(AbstractJComboBox comboBox) {
		impl.add(GComboBoxD.getJComboBox(comboBox));

	}

	// @Override
	// public geogebra.common.javax.swing.Box createHorizontalBox() {
	// return new Box(javax.swing.Box.createHorizontalBox());
	// }

}
