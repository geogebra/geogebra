package geogebra.common.gui.inputfield;

import geogebra.common.awt.Color;
import geogebra.common.awt.Font;
import geogebra.common.javax.swing.JLabel;
import geogebra.common.javax.swing.JTextField;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.common.util.TextObject;

public interface AutoCompleteTextField extends GeoElementSelectionListener, TextObject{

	void showPopupSymbolButton(boolean b);

	void setAutoComplete(boolean b);

    // inputfield.MyTextfield
	void enableColoring(boolean b);



	void setOpaque(boolean b);

    //javax.swing.JTextField
	void setFont(Font font);

    //javax.swing.JComponent
	void setForeground(Color color);
    
	//javax.swing.JComponent
	void setBackground(Color color);

	//java.awt.Component
	void setFocusable(boolean b);

	//javax.swing.text.JTextComponent
	void setEditable(boolean b);

	//javax.swing.JComponent
	void requestFocus();

	void setLabel(JLabel label);

	void setVisible(boolean b);

	void setColumns(int length);

}
