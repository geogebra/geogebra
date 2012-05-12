package geogebra.cas.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import geogebra.common.gui.SetLabels;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.common.util.Unicode;
import geogebra.gui.dialog.options.OptionsUtil;
import geogebra.main.Application;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CASControlPanel extends JPanel implements ActionListener,
		SetLabels {

	private Application app;
	private CASView view;
	private JPanel numPad;

	private String[][] numPadData = { { "9", "9" },
			{ "8", "8" }, { "7", "7" }, { "6", "6" }, { "5", "5" },
			{ "4", "4" }, { "3", "3" }, { "2", "2" }, 
			{ "1", "1" }, { ".", "." },{ "0", "0" }, {"EE", "E"}
	};
	
	
	private String[][] operatorData = { 
			 { "\u00F7", "/" }, { "\u00D7", "*" },{ "\u2212", "-" },{ "\u002B", "+" }
	};
	
	private String[][] specialCharData = { 
			 { "x", "x" }, { Unicode.PI_STRING, Unicode.PI_STRING },{ Unicode.EULER_STRING, Unicode.EULER_STRING },
			 { Unicode.IMAGINARY, Unicode.IMAGINARY }, {"\u221a", "sqrt()"}, {"^", "^"}, {"!", "!"}
	};
	
	
	
	
	
	private JButton btnNumeric;
	private JButton btnKeepInput;
	private JButton btnEvaluate;
	private JPanel enterPad;
	private JPanel operatorPad;
	private JButton btnClear;
	private JButton btnBack;
	private JPanel specialCharPad;

	public CASControlPanel(Application app, CASView view) {
		this.app = app;
		this.view = view;

		initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout());
		
		specialCharPad = createPad(specialCharData, 2,4, false);
		operatorPad = createPad(operatorData, 4,1, true);
		numPad = createPad(numPadData, 4,3, false);
		createEnterPad();
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
		p.add(numPad);
		p.add(Box.createHorizontalStrut(4));
		p.add(operatorPad);
		p.add(Box.createHorizontalStrut(10));
		p.add(specialCharPad);
		
		p.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		add(p, BorderLayout.WEST);
		
		//enterPad.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SystemColor.controlShadow));
		enterPad.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		add(enterPad, BorderLayout.SOUTH);
		
		this.setBorder(BorderFactory.createEtchedBorder());

	}

	

	
	private JPanel createPad(String[][] btnData, int rows, int columns, boolean isBold) {

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
				if (isBold) {
					btn.setFont(app.getBoldFont());
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
		
		return pad;
	}
	
	
	
	private void createEnterPad() {
		Font font = app.getBoldFont();
		Color fgColor = geogebra.awt.Color
				.getAwtColor(GeoGebraColorConstants.BLACK);
				
		btnNumeric = new JButton("\u2248");
		btnKeepInput = new JButton(app.getPlain("\u2713"));
		btnEvaluate = new JButton("=");
		btnClear = new JButton("\u2718");
		btnBack = new JButton("\u2190");

		btnNumeric.setFont(font);
		btnNumeric.setForeground(fgColor);
		btnKeepInput.setFont(font);
		btnKeepInput.setForeground(fgColor);
		btnEvaluate.setFont(font);
		btnEvaluate.setForeground(fgColor);
		
		btnNumeric.addActionListener(this);
		btnKeepInput.addActionListener(this);
		btnEvaluate.addActionListener(this);

		btnEvaluate.setPreferredSize(new Dimension(30, 30));
		btnNumeric.setPreferredSize(btnEvaluate.getPreferredSize());
		btnKeepInput.setPreferredSize(btnEvaluate.getPreferredSize());
		btnClear.setPreferredSize(btnEvaluate.getPreferredSize());
		btnBack.setPreferredSize(btnEvaluate.getPreferredSize());
		
		
		btnEvaluate.setMargin(new Insets(0,0,0,0));
		btnKeepInput.setMargin(new Insets(0,0,0,0));
		btnNumeric.setMargin(new Insets(0,0,0,0));
		
		
		JPanel westPanel = OptionsUtil.flowPanel(1, 1, 0, btnClear, btnBack);
		JPanel centerPanel = OptionsUtil.flowPanel(1, 1,0, btnEvaluate,
				btnNumeric, btnKeepInput);
		
		enterPad = new JPanel(new BorderLayout());
		enterPad.add(centerPanel, BorderLayout.WEST);
		//enterPad.add(westPanel, BorderLayout.WEST);

	}

	public void setLabels() {
		// TODO Auto-generated method stub

	}

	

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnKeepInput) {
			view.processInput("KeepInput", null);
		} else if (source == btnEvaluate) {
			view.processInput("Evaluate", null);
		} else if (source == btnNumeric) {
			view.processInput("Numeric", null);
		}

	}

	
	/***********************************************
	 * ControlButton class
	 *
	 **********************************************/
	public class ControlButton extends JButton {

		private String insertText;

		public ControlButton() {
			super();
			
			setMargin(new Insets(0,0,0,0));
			addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(!view.getConsoleTable().isEditing())
					{
						int row = view.getConsoleTable().getClickedRow();
						view.getConsoleTable().startEditingRow(row);
						view.getConsoleTable().getEditor().setInputSelectionEnd(0);
						
					}else{
						
					view.getConsoleTable().getEditor()
							.insertText(getInsertText());
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
	
	
}
