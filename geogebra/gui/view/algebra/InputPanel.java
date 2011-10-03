

package geogebra.gui.view.algebra;

import geogebra.gui.DynamicTextInputPane;
import geogebra.gui.VirtualKeyboardListener;
import geogebra.gui.editor.GeoGebraEditorPane;
import geogebra.gui.inputfield.AutoCompleteTextField;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.main.Application;
import geogebra.main.GeoGebraColorConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

/**
 * @author Markus Hohenwarter
 */
public class InputPanel extends JPanel implements FocusListener, VirtualKeyboardListener {
	
	private static final long serialVersionUID = 1L;
	
	private Application app;	
	private JTextComponent textComponent;	

	
	/** panel to hold the text field; needs to be a global to set the popup width */
	private JPanel tfPanel;  
	
	private boolean showSymbolPopup;
	
	
	//=====================================
	//Constructors
	
	public InputPanel(String initText, Application app, int columns, boolean autoComplete) {
		this(initText, app, 1, columns, true, true, null, false);
		AutoCompleteTextField atf = (AutoCompleteTextField) textComponent;
		atf.setAutoComplete(autoComplete);
	}		


	public InputPanel(String initText, Application app, int rows, int columns, boolean showSymbolPopupIcon) {
		this(initText, app, rows, columns, showSymbolPopupIcon, false, null, false);
		if (textComponent instanceof AutoCompleteTextField) {
			AutoCompleteTextField atf = (AutoCompleteTextField) textComponent;
			atf.setAutoComplete(false);
		}

	}
	
	public InputPanel(String initText, Application app, int rows, int columns, boolean showSymbolPopupIcon, boolean dynamic) {
		this(initText, app, rows, columns, showSymbolPopupIcon, false, null, dynamic);
	}
	
	public InputPanel(String initText, Application app, int rows, int columns, boolean showSymbolPopupIcon,
						boolean showSymbolButtons, KeyListener keyListener, boolean dynamic) {
		
		this.app = app;
		this.showSymbolPopup = showSymbolPopupIcon;

		// set up the text component: 
		// either a textArea, textfield or HTML textpane
		if (rows > 1) {
			
			if (!dynamic) {
				
				textComponent = new JTextArea(rows, columns);
				textComponent = new GeoGebraEditorPane(app, rows, columns);
				((GeoGebraEditorPane) textComponent).setEditorKit("geogebra");
				
			} else {
				
				textComponent = new DynamicTextInputPane(app);
			}
			
			
		} else{
			
			textComponent = new AutoCompleteTextField(columns, app);	
			((MyTextField)textComponent).setShowSymbolTableIcon(showSymbolPopup);
		}
		
		textComponent.addFocusListener(this);
		textComponent.setFocusable(true);	
		
		if (keyListener != null)
		textComponent.addKeyListener(keyListener);
		
		if (initText != null) textComponent.setText(initText);		
		
		
		// create the GUI
		
		if (rows > 1) { // JTextArea
			setLayout(new BorderLayout(5, 5));	
			// put the text pane in a border layout to prevent JTextPane's auto word wrap
			JPanel noWrapPanel = new JPanel(new BorderLayout());
			noWrapPanel.add(textComponent);
			JScrollPane sp = new JScrollPane(noWrapPanel); 
			sp.setAutoscrolls(true);
			add(sp, BorderLayout.CENTER);
				
		} 
		
		else { // JTextField
			setLayout(new BorderLayout(0,0));
			tfPanel = new JPanel(new BorderLayout(0,0));		
			tfPanel.add(textComponent, BorderLayout.CENTER);
			add(tfPanel, BorderLayout.CENTER);
		}		
		
	}

	

	
	public JTextComponent getTextComponent() {
		return textComponent;
	}
	
	public String getText() {
		return textComponent.getText();
	}
	
	public String getSelectedText() {
		return textComponent.getSelectedText();
	}
	
	public void selectText() { 					
		textComponent.setSelectionStart(0);
		textComponent.moveCaretPosition(textComponent.getText().length());
	}
	
	public void setText(String text) {
		textComponent.setText(text);
	}
	

	
	
	
	
	
	
	
	
	
	/**
	 * Inserts string at current position of the input textfield and gives focus
	 * to the input textfield.
	 * @param str: inserted string
	 */
	public void insertString(String str) {	
		textComponent.replaceSelection(str);	
		
		// make sure autocomplete works for the Virtual Keyboard
		if (textComponent instanceof AutoCompleteTextField) {
			((AutoCompleteTextField)textComponent).mergeKoreanDoubles();
			((AutoCompleteTextField)textComponent).updateCurrentWord(false);
			((AutoCompleteTextField)textComponent).startAutoCompletion();
		}
		
		textComponent.requestFocus();
	}		
	
	public void focusGained(FocusEvent e) {
		app.getGuiManager().setCurrentTextfield(this, true);
	}

	public void focusLost(FocusEvent e) {
		app.getGuiManager().setCurrentTextfield(null, !(e.getOppositeComponent() instanceof VirtualKeyboard));
	}

	

	
	
	//TODO  Hide/show popup button options
	public void showSpecialChars(boolean flag) {
		//popupTableButton.setVisible(flag);
		//for(int i=0; i < symbolButton.length; i++)
			//symbolButton[i].setVisible(false);	
	}

	
	/**
	 * custom cell renderer for the history list,
	 * draws grid lines and roll-over effect
	 *
	 */
	private class HistoryListCellRenderer extends DefaultListCellRenderer {

		private Color bgColor;
		//private Color listSelectionBackground = MyTable.SELECTED_BACKGROUND_COLOR;
		private Color listBackground = Color.white;
		private Color rolloverBackground = Color.lightGray;
		private Border gridBorder = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, GeoGebraColorConstants.TABLE_GRID_COLOR),
				BorderFactory.createEmptyBorder(2, 5, 2, 5));

				public Component getListCellRendererComponent(JList list, Object value, int index,
						boolean isSelected, boolean cellHasFocus) {

					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

					setText((String) value);
					setForeground(Color.black);
					setBorder(gridBorder);

					// paint roll-over row 
					Point point = list.getMousePosition();
					int mouseOver = point==null ? -1 : list.locationToIndex(point);
					if (index == mouseOver)
						bgColor = rolloverBackground;
					else
						bgColor = listBackground;
					setBackground(bgColor);


					return this;
				}
	} 
	/** end history list cell renderer **/
		
}

