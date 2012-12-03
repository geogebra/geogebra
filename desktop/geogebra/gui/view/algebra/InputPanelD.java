

package geogebra.gui.view.algebra;

import geogebra.common.gui.VirtualKeyboardListener;
import geogebra.common.gui.view.algebra.DialogType;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.gui.DynamicTextInputPane;
import geogebra.gui.GuiManagerD;
import geogebra.gui.editor.GeoGebraEditorPane;
import geogebra.gui.inputfield.AutoCompleteTextFieldD;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.TextLineNumber;
import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
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
public class InputPanelD extends JPanel implements FocusListener, VirtualKeyboardListener {
	
	private static final long serialVersionUID = 1L;
	
	private AppD app;	
	private JTextComponent textComponent;	
	
	/** panel to hold the text field; needs to be a global to set the popup width */
	private JPanel tfPanel;  
	
	private boolean showSymbolPopup;

	/** JScrollpane for the textComponent */
	private JScrollPane scrollPane;
	
	
	//=====================================
	//Constructors
	
	public InputPanelD(String initText, AppD app, int columns, boolean autoComplete) {
		this(initText, app, 1, columns, true, true, null, DialogType.GeoGebraEditor);
		AutoCompleteTextFieldD atf = (AutoCompleteTextFieldD) textComponent;
		atf.setAutoComplete(autoComplete);
	}		


	public InputPanelD(String initText, AppD app, int rows, int columns, boolean showSymbolPopupIcon) {
		this(initText, app, rows, columns, showSymbolPopupIcon, false, null, DialogType.GeoGebraEditor);
		if (textComponent instanceof AutoCompleteTextFieldD) {
			AutoCompleteTextFieldD atf = (AutoCompleteTextFieldD) textComponent;
			atf.setAutoComplete(false);
		}
	}
	
	public InputPanelD(String initText, AppD app, int rows, int columns, boolean showSymbolPopupIcon, DialogType type) {
		this(initText, app, rows, columns, showSymbolPopupIcon, false, null, type);
	}
	
	public InputPanelD(String initText, AppD app, int rows, int columns, boolean showSymbolPopupIcon,
						boolean showSymbolButtons, KeyListener keyListener, DialogType type) {
		
		this.app = app;
		this.showSymbolPopup = showSymbolPopupIcon;
		
		// set up the text component: 
		// either a textArea, textfield or HTML textpane
		if (rows > 1) {
			
			switch (type) {
			case TextArea :
				textComponent = new JTextArea(rows, columns);
				setTextAreaLineWrap(true);
				break;
			case DynamicText :
				textComponent = new DynamicTextInputPane(app);
				break;
			case GeoGebraEditor:
				textComponent = new GeoGebraEditorPane(app, rows, columns);
				((GeoGebraEditorPane) textComponent).setEditorKit("geogebra");
				break;
			}
						
			
		} else{
			
			textComponent = new AutoCompleteTextFieldD(columns, app);	
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
			scrollPane = new JScrollPane(noWrapPanel); 
			scrollPane.setAutoscrolls(true);
			add(scrollPane, BorderLayout.CENTER);
				
		} 
		
		else { // JTextField
			setLayout(new BorderLayout(0,0));
			tfPanel = new JPanel(new BorderLayout(0,0));		
			tfPanel.add(textComponent, BorderLayout.CENTER);
			add(tfPanel, BorderLayout.CENTER);
		}		
	}
	
	
	/**
	 * Set line wrapping feature for JTextArea components
	 * 
	 * @param isWrapped true if line wrapping is supported
	 */
	public void setTextAreaLineWrap(boolean isWrapped){
		if(textComponent instanceof JTextArea){
			((JTextArea)textComponent).setLineWrap(isWrapped);
			((JTextArea)textComponent).setWrapStyleWord(isWrapped);
		}
	}
		
	/**
	 * Hide/show line numbering in the text component
	 */
	public void setShowLineNumbering(boolean showLineNumbers) {
			
		if (showLineNumbers) {
			scrollPane.setRowHeaderView(new TextLineNumber(textComponent));
		} 
		else 
		{
			scrollPane.setRowHeaderView(null);
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
		textComponent.selectAll();
	}
	
	public void setText(String text) {
		textComponent.setText(text);
	}
	
	/**
	 * Inserts string at current position of the input textfield and gives focus
	 * to the input textfield.
	 * @param str inserted string
	 */
	public void insertString(String str) {	
		textComponent.replaceSelection(str);	
		
		// make sure autocomplete works for the Virtual Keyboard
		if (textComponent instanceof AutoCompleteTextFieldD) {
			((AutoCompleteTextFieldD)textComponent).mergeKoreanDoubles();
			((AutoCompleteTextFieldD)textComponent).updateCurrentWord(false);
			((AutoCompleteTextFieldD)textComponent).startAutoCompletion();
		}
		
		textComponent.requestFocus();
	}		
	
	public void focusGained(FocusEvent e) {
		((GuiManagerD)app.getGuiManager()).setCurrentTextfield(this, true);
	}

	public void focusLost(FocusEvent e) {
		((GuiManagerD)app.getGuiManager()).setCurrentTextfield(null, !(e.getOppositeComponent() instanceof VirtualKeyboard));
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

		private static final long serialVersionUID = 1L;
		
		private Color bgColor;
		//private Color listSelectionBackground = MyTable.SELECTED_BACKGROUND_COLOR;
		private Color listBackground = Color.white;
		private Color rolloverBackground = Color.lightGray;
		private Border gridBorder = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, geogebra.awt.GColorD.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR)),
				BorderFactory.createEmptyBorder(2, 5, 2, 5));

				@Override
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
	
	
	public void updateFonts() {

		Font font = app.getPlainFont();

		if (textComponent instanceof GeoGebraEditorPane) {
			((GeoGebraEditorPane) textComponent).updateFont(font);
		} else {
			textComponent.setFont(font);
		}
		// tfPanel.setFont(font);
	}
}

