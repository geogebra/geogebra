package geogebra.cas.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import geogebra.common.awt.GColor;
import geogebra.common.factories.AwtFactory;
import geogebra.common.gui.SetLabels;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.common.util.Unicode;
import geogebra.gui.dialog.options.OptionsUtil;
import geogebra.gui.inputfield.MyTextField;
import geogebra.main.AppD;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class CASControlPanel extends JPanel implements ActionListener,
		SetLabels {

	private AppD app;
	private CASView view;
	private JPanel numPad;

	private String[][] numPadData = { { "9", "9" }, { "8", "8" }, { "7", "7" },
			{ "6", "6" }, { "5", "5" }, { "4", "4" }, { "3", "3" },
			{ "2", "2" }, { "1", "1" }, { ".", "." }, { "0", "0" },
			{ "EE", "E" } };

	private String[][] operatorData = { { "\u00F7", "/" }, { "\u00D7", "*" },
			{ "\u2212", "-" }, { "\u002B", "+" } };

	private String[][] specialCharData = { { "x", "x" },
			{ Unicode.PI_STRING, Unicode.PI_STRING },
			{ Unicode.EULER_STRING, Unicode.EULER_STRING },
			{ Unicode.IMAGINARY, Unicode.IMAGINARY }, { "\u221a", "sqrt()" },
			{ "^", "^" }, { "!", "!" } };

	private MyButton btnNumeric;
	private MyButton btnKeepInput;
	private MyButton btnEvaluate;
	private MyButton btnShowKeyboard;
	private JPanel topBarPanel;
	private JPanel operatorPad;
	private JButton btnClear;
	private JButton btnBack;
	private JPanel specialCharPad;
	private JPanel copyPad;
	private JPanel calculatorPanel;
	private JPanel evaluatePad;

	private JPanel bottomPanel;
	
	public CASControlPanel(AppD app, CASView view) {
		this.app = app;
		this.view = view;
		initGUI();

	}
	
	public void showCalculatorPanel(boolean isVisible){
		calculatorPanel.setVisible(isVisible);
	}
	
	public JPanel getControlBar(){
		return topBarPanel;
	}
	
	public JPanel getControlPanel(){
		if(bottomPanel == null){
			createBottomPanel();
		}
		return bottomPanel;
	}
	
	
	private void initGUI() {
		setLayout(new BorderLayout());

		createCalculatorPanel();
		calculatorPanel.setVisible(false);
		calculatorPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		
		createTopBarPanel();
		Border topBorder = (BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(1, 0, 1, 0, SystemColor.controlShadow),
				BorderFactory.createMatteBorder(1, 0, 0, 0,
						SystemColor.controlLtHighlight)));
		topBarPanel.setBorder(BorderFactory.createCompoundBorder(topBorder,
				BorderFactory.createEmptyBorder(4, 4, 4, 2)));

		//add(calculatorPanel, BorderLayout.CENTER);
		//add(topBarPanel, BorderLayout.NORTH);


	}
	
	public void setControlComponent(JComponent comp){
		BorderLayout layout = (BorderLayout) getLayout();
		remove(layout.getLayoutComponent(BorderLayout.CENTER));
		add(comp, BorderLayout.CENTER);
		revalidate();
	}
	
	public void restoreDefaultControlComponent(){
		setControlComponent(calculatorPanel);
	}

	
	public void createBottomPanel(){
		bottomPanel = new JPanel(new BorderLayout());
		setBottomPanelSOUTH(calculatorPanel);
	}
	
	public void setBottomPanelNORTH(JComponent comp){
		BorderLayout layout = (BorderLayout) bottomPanel.getLayout();
		remove(layout.getLayoutComponent(BorderLayout.NORTH));
		add(comp, BorderLayout.NORTH);
		revalidate();
	}
	
	public void setBottomPanelSOUTH(JComponent comp) {
		BorderLayout layout = (BorderLayout) bottomPanel.getLayout();
		if (layout.getLayoutComponent(BorderLayout.SOUTH) != null) {
			remove(layout.getLayoutComponent(BorderLayout.SOUTH));
		}
		bottomPanel.add(comp, BorderLayout.SOUTH);
		revalidate();
	}
	
	
	
	private JPanel createPad(String[][] btnData, int rows, int columns,
			boolean isBold, GColor textColor) {

		if (textColor == null) {
			textColor = GColor.BLACK;
		}
		JPanel padGrid = new JPanel(new GridLayout(rows, columns));
		GridLayout l = (GridLayout) padGrid.getLayout();
		l.setHgap(0);
		l.setVgap(0);
		for (int i = 0; i < btnData.length; i++) {
			if (btnData[i][0] == "null") {
				padGrid.add(new JLabel(" "));
			} else {
				final ControlButton btn = new ControlButton();
				btn.setPreferredSize(new Dimension(30, 30));
				btn.setForeground(geogebra.awt.GColorD.getAwtColor(textColor));
				if (isBold) {
					Font f = app.getBoldFont();
					btn.setFont(f.deriveFont((float) (f.getSize() + 2)));
				} else {
					btn.setFont(app.getPlainFont());
				}

				btn.setText(btnData[i][0]);
				btn.setInsertText(btnData[i][1]);
				padGrid.add(btn);
			}
		}
		JPanel pad = new JPanel(new BorderLayout());
		pad.add(padGrid, BorderLayout.NORTH);
		pad.setFocusable(false);
		return pad;
	}

	private void createTopBarPanel() {
		
		createCopyPad();
		JPanel westPanel = OptionsUtil.flowPanel(1, 1, 0, copyPad);		

		topBarPanel = new JPanel(new BorderLayout());
		topBarPanel.add(westPanel, BorderLayout.WEST);

	}
	
	
	private void createCalculatorPanel(){

		specialCharPad = createPad(specialCharData, 2, 4, false, null);
		operatorPad = createPad(operatorData, 4, 1, true,
				geogebra.common.main.GeoGebraColorConstants.MAROON);
		numPad = createPad(numPadData, 4, 3, false, null);
		
		calculatorPanel = new JPanel();
		calculatorPanel.setLayout(new BoxLayout(calculatorPanel, BoxLayout.X_AXIS));
		calculatorPanel.add(numPad);
		calculatorPanel.add(Box.createHorizontalStrut(0));
		calculatorPanel.add(operatorPad);
		calculatorPanel.add(Box.createHorizontalStrut(15));
		
		createEvaluatePad();
		
		JPanel specialCharEvalPanel = new JPanel(new BorderLayout());
		
		specialCharEvalPanel.add(specialCharPad, BorderLayout.NORTH);
		specialCharEvalPanel.add(evaluatePad, BorderLayout.SOUTH);
		
		calculatorPanel.add(specialCharEvalPanel);
		

	}
	
	private void createEvaluatePad(){
		
		btnNumeric = new MyButton(app.getImageIcon("cas-numeric24.png"));
		btnKeepInput = new MyButton(app.getImageIcon("cas-keepinput24.png"));
		btnEvaluate = new MyButton(app.getImageIcon("cas-evaluate24.png"));
		
		btnNumeric.addActionListener(this);
		btnKeepInput.addActionListener(this);
		btnEvaluate.addActionListener(this);

		// btnEvaluate.setPreferredSize(new Dimension(30, 30));
		// btnNumeric.setPreferredSize(btnEvaluate.getPreferredSize());
		// btnKeepInput.setPreferredSize(btnEvaluate.getPreferredSize());
		
		//btnClear = new JButton("\u232B");
				//btnBack = new JButton("\u23CE");
				//btnClear.setFont(font);
				//btnBack.setFont(font);
				//btnClear.setPreferredSize(new Dimension(30, 30));
		// btnClear = new JButton("\u2718");
				// btnBack = new JButton("\u2190");
		//btnClear.setPreferredSize(btnEvaluate.getPreferredSize());
		//btnBack.setPreferredSize(btnEvaluate.getPreferredSize());

		btnEvaluate.setMargin(new Insets(0, 0, 0, 0));
		btnKeepInput.setMargin(new Insets(0, 0, 0, 0));
		btnNumeric.setMargin(new Insets(0, 0, 0, 0));

		evaluatePad = OptionsUtil.flowPanelRight(4, 0, 0, btnKeepInput,
				Box.createHorizontalStrut(10), btnEvaluate, btnNumeric);

	}

	private void createCopyPad() {
		Font font = app.getBoldFont();
		java.awt.Color fgColor = geogebra.awt.GColorD.getAwtColor(GColor.BLACK);
	

		btnShowKeyboard = 	new MyButton(app.getImageIcon("cas-keyboard.png"));	
		btnShowKeyboard.addActionListener(this);
		
		
		btnShowKeyboard.setFocusable(false);
	
		
		btnShowKeyboard.setMargin(new Insets(0, 0, 0, 0));
		
		JPanel westPanel = OptionsUtil.flowPanel(4, 0, 0, btnShowKeyboard,
				Box.createHorizontalStrut(10));

		copyPad = new JPanel(new BorderLayout());
		copyPad.add(westPanel, BorderLayout.WEST);

	}

	public void setLabels() {
		// TODO Auto-generated method stub

	}

	public void actionPerformed(ActionEvent e) {
		
		Object source = e.getSource();
		
		System.out.println(e.getSource().getClass().getName());
		
		if (source == btnKeepInput) {
			view.processInput("KeepInput", null);
		} else if (source == btnEvaluate) {
			view.processInput("Evaluate", null);
		} else if (source == btnNumeric) {
			view.processInput("Numeric", null);

		} 
		
		else if (source == btnShowKeyboard) {
			calculatorPanel.setVisible(btnShowKeyboard.isSelected());
			view.repaint();
		}
		updateGUI();
	}

	private void updateGUI() {

		
	}

	/***********************************************
	 * ControlButton class
	 * 
	 **********************************************/
	public class ControlButton extends JButton {

		private String insertText;

		public ControlButton() {
			super();
			this.setFocusable(false);
			this.setRequestFocusEnabled(false);
			setMargin(new Insets(0, 0, 0, 0));
			addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int row = view.getConsoleTable().getSelectedRow();
					if (row == -1) {
						row = 0;
					}

					if (!view.getConsoleTable().isEditing()) {
						view.getConsoleTable().startEditingRow(row);
					}

					((MyTextField) view.getConsoleTable().getEditor()
							.getInputArea()).insertString(getInsertText());

					// move the caret inside last inserted bracket
					if (getInsertText().endsWith(")")) {
						// MyTextField.insertString uses a runnable to change
						// caret position
						// (fixes a Mac OS problem). Thus, another runnable is
						// needed here to
						// move the caret after insertString is done.
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								int l = ((MyTextField) view.getConsoleTable()
										.getEditor().getInputArea()).getText()
										.length();
								;
								((MyTextField) view.getConsoleTable()
										.getEditor().getInputArea())
										.setCaretPosition(l - 1);
							}
						});

					}

				}
			});
		}

		public String getInsertText() {
			return insertText;
		}

		public void setInsertText(String insertText) {
			this.insertText = insertText;
		}

	}
	
	/***********************************************
	 * ControlButton class
	 * 
	 **********************************************/
	private class MyButton extends JToggleButton {

		public MyButton(Icon icon) {
			super(icon);
			this.setFocusable(false);
			this.setRequestFocusEnabled(false);
			setMargin(new Insets(0, 0, 0, 0));
			//this.setBorderPainted(false);
			//this.setContentAreaFilled(false);
			this.setPreferredSize(new Dimension(20,20));
		}
	}
	

}
