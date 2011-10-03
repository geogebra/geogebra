package geogebra.cas.view;

import geogebra.gui.inputfield.MyTextField;
import geogebra.kernel.GeoCasCell;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

public abstract class CASTableCell extends JPanel{

	protected CASInputPanel inputPanel;
	protected CASOutputPanel outputPanel;
	protected MyTextField dummyField; // dummy label used to get preferred size;
	protected Application app;
	protected CASView view;
	

	public CASTableCell(CASView view) {
		this.view = view;
		this.app = view.getApp();
	
		setLayout(new BorderLayout(5, 5));
		setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));		
		setBackground(Color.white);
		
		inputPanel = new CASInputPanel(app);
		dummyField = new MyTextField(app);
		
		// The inputPanel needs to have variable width so that it fits the JScrollPane
		// viewport when in editing mode but also can grow to the size of its
		// text content when not in editing mode. This way the horizontal scollbars
		// can be used to view long non-editing fields but the current editor field can 
		// be shown clipped and scrollable with arrow keys.
		//
		// Width is set with the setInputPanelWidth method. The cell editor calls this to adjust 
		// the width to fit the viewport. The cell renderer calls it to expand it to its maximum
		// width.
		//
		// To make this work, inputPanel is put in WEST where width can be controlled.
		// and an invisible dummy field is put in CENTER to get a preferred size.
		
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setBackground(this.getBackground());
		northPanel.add(dummyField, BorderLayout.CENTER);
		northPanel.add(inputPanel, BorderLayout.WEST);	
		dummyField.setVisible(false);
		
		outputPanel = new CASOutputPanel(view.getApp());
		
		add(northPanel, BorderLayout.NORTH);
		add(outputPanel, BorderLayout.CENTER);
		return;
	}

	/**
	 * Overrides getPreferredSize so that it reports the preferred size
	 * if this input label was completely drawn and not clipped for the editor.
	 */
	@Override
	public Dimension getPreferredSize(){
		Dimension d = super.getPreferredSize();
		d.width = Math.max(d.width, dummyField.getPreferredSize().width);
		return d;
	}
	
	
	public int getInputPanelHeight() {
		return inputPanel.getHeight();
	}
	
	public int getOutputPanelHeight() {
		return outputPanel.getHeight();
	}
	
	/**
	 * Sets the width of the input panel. 
	 * Use width = -1 to set width to the full input string length.
	 */
	public void setInputPanelWidth(int width){
		
		Dimension d = dummyField.getPreferredSize();
		// use the parameter width - 15 pixels to correct for border padding
		if(width > 0)
			d.width = width - 15;   
		
		inputPanel.setPreferredSize(d);
		
	}

	
	
	public void setValue(GeoCasCell cellValue) {		
		// set input panel
		String input = cellValue.getInput();
		inputPanel.setInput(input);
		dummyField.setText(inputPanel.getInput());
				
		// set output panel
		boolean showOutput = cellValue.showOutput();
		outputPanel.setVisible(showOutput);
		if (showOutput) {
			// show eval command (e.g. "Substitute") in output cell
			String evalCmd = cellValue.getEvalCommand();
			String evalCmdLocal = app.getCommand(evalCmd);
			
			if (input.startsWith(evalCmdLocal)) {
				// don't show command if it is already at beginning of input
				evalCmdLocal = "";
			}
			
			// eval comment (e.g. "x=5, y=8")
			String evalComment = cellValue.getEvalComment();
			if (evalComment.length() > 0) {
				if (evalCmdLocal.length() == 0) {
					evalCmdLocal = evalComment;
				} else {
					evalCmdLocal = evalCmdLocal + ", " + evalComment;
				}
			}
			
			outputPanel.setOutput(
					cellValue.getOutput(), 
					cellValue.getLaTeXOutput(), 
					evalCmdLocal, 
					cellValue.isError()
				);
		}	
	}

	
	void updateTableRowHeight(JTable table, int row) {
		if (isVisible()) {
			Dimension prefSize = getPreferredSize();

			if (prefSize != null) {
				setSize(prefSize);
				if (table.getRowHeight(row) != prefSize.height)
					table.setRowHeight(row, prefSize.height);
			}
		}
	}

	public String getInput() {
		return inputPanel.getInput();
	}

	public String getOutput() {
		return outputPanel.getOutput();
	}

	public void setInputAreaFocused() {
		inputPanel.setInputAreaFocused();
	}	
	
	public JTextComponent getInputArea() {
		return inputPanel.getInputArea();
	}	
	
	public void setFont(Font ft) {
		super.setFont(ft);
		if (inputPanel != null)
			inputPanel.setFont(ft);
		if (dummyField != null){
			dummyField.setFont(ft);
		}
		if (outputPanel != null)
			outputPanel.setFont(ft);
	}
	
	public void setLabels(){
		inputPanel.setLabels();
	}


}
