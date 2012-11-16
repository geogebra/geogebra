package geogebra.javax.swing;

import geogebra.common.awt.GDimension;
import geogebra.common.awt.GRectangle;
import geogebra.common.gui.inputfield.AutoCompleteTextField;
import geogebra.common.javax.swing.AbstractJComboBox;

/**
 * Wrapper for javax.swing.Box
 * @author Judit Elias
 */
public class BoxD extends geogebra.common.javax.swing.GBox {
	
	private javax.swing.Box impl = null; 
	
	/**
	 * Creates new wrapper Box
	 * @param box box to be wrapped
	 */
	public BoxD(javax.swing.Box box) {
		this.impl = box;
	}
		
	/**
	 * Returns the wrapped box
	 * @return wrapped box
	 */
	public javax.swing.Box getImpl() {
		return this.impl;
	}

	@Override
	public void add(geogebra.common.javax.swing.GLabel label) {
		impl.add(((geogebra.javax.swing.GLabelD)label).getImpl());
	}

	@Override
	public void add(AutoCompleteTextField textField) {
		impl.add((geogebra.gui.inputfield.AutoCompleteTextFieldD)textField);
		
	}

	@Override
	public void setVisible(boolean isVisible) {
		impl.setVisible(isVisible);
	}

	@Override
	public void setBounds(GRectangle rect) {
		impl.setBounds(geogebra.awt.GRectangleD.getAWTRectangle(rect));
	}

	@Override
	public GDimension getPreferredSize() {
		return new geogebra.awt.GDimensionD(impl.getPreferredSize());
	}

	@Override
	public GRectangle getBounds() {
		return new geogebra.awt.GRectangleD(impl.getBounds());
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

//	@Override
//	public geogebra.common.javax.swing.Box createHorizontalBox() {
//		return new Box(javax.swing.Box.createHorizontalBox());
//	}

}
