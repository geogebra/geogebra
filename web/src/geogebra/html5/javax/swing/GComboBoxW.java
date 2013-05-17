package geogebra.html5.javax.swing;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.euclidian.event.ActionListener;
import geogebra.common.javax.swing.AbstractJComboBox;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class GComboBoxW extends geogebra.common.javax.swing.AbstractJComboBox {

	private ListBox impl = null;

	/**
	 * Creates new wrapper Box
	 */
	public GComboBoxW() {
		this.impl = new ListBox();
	}
	
	@Override
    public void setVisible(boolean b) {
	    impl.setVisible(b);
	    
    }

	@Override
    public Object getItemAt(int i) {
	    return impl.getItemText(i);
    }

	@Override
    public void setFont(GFont font) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setForeground(GColor objectColor) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setBackground(GColor color) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setFocusable(boolean b) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setEditable(boolean b) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void addItem(String string) {
	    impl.addItem(string);
	    
    }

	@Override
    public void setSelectedIndex(int selectedIndex) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public int getSelectedIndex() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public void addActionListener(ActionListener newActionListener) {
	    // TODO Auto-generated method stub
	    
    }

	public static Widget getImpl(AbstractJComboBox comboBox) {
	    if(!(comboBox instanceof GComboBoxW))
	    	return null;
	    return ((GComboBoxW)comboBox).impl;
    }

	@Override
    public void removeAllItems() {
		impl.clear();
	    
    }

	@Override
    public int getItemCount() {
	    return impl.getItemCount();
	}

}
