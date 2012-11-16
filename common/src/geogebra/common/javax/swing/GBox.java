package geogebra.common.javax.swing;

import geogebra.common.awt.GDimension;
import geogebra.common.awt.GRectangle;
import geogebra.common.gui.inputfield.AutoCompleteTextField;

public abstract class GBox {

	public abstract void add(GLabel label);

	public abstract void add(AutoCompleteTextField textField);

	public abstract void setVisible(boolean isVisible);

	public abstract void setBounds(GRectangle labelRectangle);

	public abstract GDimension getPreferredSize();

	public abstract GRectangle getBounds();

	public abstract void validate();
	
	public abstract void revalidate();

	public abstract void add(AbstractJComboBox comboBox);

	public void doLayout() {
		// TODO Auto-generated method stub
		
	}

	public void remove(AbstractJComboBox comboBox) {
		// TODO Auto-generated method stub
		
	}

	//public abstract Box createHorizontalBox();

}
