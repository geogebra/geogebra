package geogebra.common.gui.inputfield;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.euclidian.event.FocusListener;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.javax.swing.GLabel;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.common.util.AutoCompleteDictionary;
import geogebra.common.util.TextObject;

public interface AutoCompleteTextField extends GeoElementSelectionListener, TextObject{

	void showPopupSymbolButton(boolean b);

	void setAutoComplete(boolean b);

    // inputfield.MyTextfield
	void enableColoring(boolean b);



	void setOpaque(boolean b);

    //javax.swing.JTextField
	void setFont(GFont font);

    //javax.swing.JComponent
	void setForeground(GColor color);
    
	//javax.swing.JComponent
	void setBackground(GColor color);

	//java.awt.Component
	void setFocusable(boolean b);

	//javax.swing.text.JTextComponent
	void setEditable(boolean b);

	//javax.swing.JComponent
	void requestFocus();

	void setLabel(GLabel label);

	void setVisible(boolean b);

	void setColumns(int length);

	void addFocusListener(FocusListener focusListener);

	void addKeyHandler(KeyHandler handler);

	int getCaretPosition();

	void setCaretPosition(int caretPos);
	
	void setDictionary(AutoCompleteDictionary dict);
	
	AutoCompleteDictionary getDictionary();

	void setFocusTraversalKeysEnabled(boolean b);

	void setUsedForInputBox(GeoTextField geoTextField);

	boolean hasFocus();

	boolean usedForInputBox();

}
