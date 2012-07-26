package geogebra.cas.view;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.gui.inputfield.MyTextField;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;
/**
 * CAS cell component
 */
public abstract class CASTableCell extends JPanel {

	private static final long serialVersionUID = 1L;
	/** input panel */
	protected CASInputPanel inputPanel;
	/** output panel */
	protected CASOutputPanel outputPanel;
	/** dummy label used to get preferred size; */
	protected MyTextField dummyField;
	/** application */
	protected AppD app;
	/** CAS view */
	protected CASViewD view;

	
	/** show hide option (also called plot tool) for this cell content*/
	protected JLabel showHideControl;
	protected boolean visible;	
	private ImageIcon iconShown, iconHidden;
	/**
	 * @param view CAS view
	 */
	public CASTableCell(CASViewD view) {
		this.view = view;
		this.app = view.getApp();

		setLayout(new BorderLayout());
		//setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
		setBackground(Color.white);

		inputPanel = new CASInputPanel(app);
		dummyField = new MyTextField(app);

		iconShown = app.getImageIcon("shown.gif");
		iconHidden = app.getImageIcon("hidden.gif");
		
		showHideControl = new JLabel(iconHidden);
		visible = false;
		
		// The inputPanel needs to have variable width so that it fits the
		// JScrollPane
		// viewport when in editing mode but also can grow to the size of its
		// text content when not in editing mode. This way the horizontal
		// scollbars
		// can be used to view long non-editing fields but the current editor
		// field can
		// be shown clipped and scrollable with arrow keys.
		//
		// Width is set with the setInputPanelWidth method. The cell editor
		// calls this to adjust
		// the width to fit the viewport. The cell renderer calls it to expand
		// it to its maximum
		// width.
		//
		// To make this work, inputPanel is put in WEST where width can be
		// controlled.
		// and an invisible dummy field is put in CENTER to get a preferred
		// size.

		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setBackground(this.getBackground());
		northPanel.add(dummyField, BorderLayout.CENTER);
		northPanel.add(inputPanel, BorderLayout.WEST);
		dummyField.setVisible(false);

		JPanel mainPanel = new JPanel(new BorderLayout(5,5));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
		mainPanel.setBackground(Color.white);
		
		outputPanel = new CASOutputPanel(view.getApp());

		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(outputPanel, BorderLayout.CENTER);
		
		
		add(showHideControl, BorderLayout.WEST);
		add(mainPanel, BorderLayout.CENTER);
		return;
	}

	/**
	 * Overrides getPreferredSize so that it reports the preferred size if this
	 * input label was completely drawn and not clipped for the editor.
	 */
	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		d.width = Math.max(d.width, dummyField.getPreferredSize().width);
		return d;
	}

	/**
	 * @return input panel height
	 */
	public int getInputPanelHeight() {
		return inputPanel.getHeight();
	}

	/**
	 * @return output panel height
	 */
	public int getOutputPanelHeight() {
		return outputPanel.getHeight();
	}
	
	/**
	 * returns the height of the show hide button
	 * @return control height
	 */
	public int getShowHideControlHeight() {
		return showHideControl.getHeight();
	}
	/**
	 * returns the width of the show hide button
	 * @return control width
	 */	
	public int getShowHideControlWidth(){
		return showHideControl.getWidth();
	}
	
	/**
	 * returns the x position of the showHideControl
	 * @return x position
	 */
	public int getShowHideControlX(){
		return showHideControl.getX();
	}
	
	/**
	 * returns the y position of the showHideControl
	 * @return y position
	 */
	public int getShowHideControlY(){
		return showHideControl.getY();
	}

	public void setEuclidianVisible(boolean v) {
		visible = v;
		if(v)
			showHideControl.setIcon(iconShown);
		else
			showHideControl.setIcon(iconHidden);
	}
	
	public boolean isSetEuclidianVisible() {
		return visible;
	}
	
	
	/**
	 * Sets the width of the input panel. Use width = -1 to set width to the
	 * full input string length.
	 * @param width desired width
	 */
	public void setInputPanelWidth(int width) {

		Dimension d = dummyField.getPreferredSize();
		// use the parameter width - 15 pixels to correct for border padding
		if (width > 0)
			d.width = width - getShowHideControlWidth() - 15;

		inputPanel.setPreferredSize(d);

	}

	/**
	 * Fill this component with input / output of given CAS cell
	 * @param cellValue CAS cell
	 */
	public void setValue(GeoCasCell cellValue) {
		// set input panel
		String input = cellValue.getInput(StringTemplate.defaultTemplate);
		inputPanel.setInput(input);
		if(cellValue.isUseAsText()){
			inputPanel.setCommentColor(geogebra.awt.GColorD
					.getAwtColor(cellValue.getObjectColor()));
		}
		else{
			inputPanel.setCommentColor(geogebra.awt.GColorD
				.getAwtColor(GeoGebraColorConstants.BLACK));
		}
		outputPanel.setForeground(cellValue.getAlgebraColor());
		dummyField.setText(inputPanel.getInput());

		
		if(cellValue.hasTwinGeo()){
			this.setEuclidianVisible(cellValue.getTwinGeo().isSetEuclidianVisible());
			showHideControl.setVisible(true);
		}
		else{
			this.setEuclidianVisible(false);
			showHideControl.setVisible(!input.equals(""));
		}
			
			
		
		
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
					cellValue.getOutput(StringTemplate.defaultTemplate),
					cellValue.getLaTeXOutput(), evalCmdLocal,
					cellValue.isError(),cellValue.getAlgebraColor());
		}
	}

	/**
	 * @param c color for input
	 */
	public void setInputColor(Color c) {
		inputPanel.setForeground(c);
	}

	/**
	 * @param table CAS table
	 * @param row row index
	 */
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

	/**
	 * @return input text
	 */
	public String getInput() {
		return inputPanel.getInput();
	}

	/**
	 * change directly the text in the Input field of the cell
	 * @param input the input string
	*/
	public void setInput(String input)	{
		if(input != null)
			inputPanel.setInput(input);
	}
	
	/**
	 * 
	 * @return true if the InputArea has been set focused successfully, false
	 *         otherwise
	 */
	public boolean setInputAreaFocused() {
		return inputPanel.setInputAreaFocused();
	}

	/**
	 * @return input component
	 */
	public JTextComponent getInputArea() {
		return inputPanel.getInputArea();
	}

	@Override
	public void setFont(Font ft) {
		super.setFont(ft);
		if (inputPanel != null) {
			inputPanel.setFont(ft);
		}
		if (dummyField != null) {
			dummyField.setFont(ft);
		}
		if (outputPanel != null)
			outputPanel.setFont(ft);
	}

	/**
	 * Updates autocomplete dictionary
	 */
	public void setLabels() {
		inputPanel.setLabels();
	}

}
