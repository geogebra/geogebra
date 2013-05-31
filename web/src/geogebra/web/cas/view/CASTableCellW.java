package geogebra.web.cas.view;

import geogebra.common.awt.GColor;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.main.DrawEquationWeb;

import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Graphical representation of CAS cells in Web 
 * @author Zbynek Konecny
 *
 */
public class CASTableCellW extends VerticalPanel {
	private GeoCasCell casCell;
	private Label inputPanel;
	private HorizontalPanel outputPanel;
	private String textBeforeEdit;
	private AutoCompleteTextFieldW textField;
	/**
	 * Creates new graphical representation of CAS cell
	 * @param casCell cas cell value
	 */
	public CASTableCellW(GeoCasCell casCell) {
		this.casCell = casCell;
		inputPanel = new Label();
		if(casCell!=null){
			inputPanel.setText(casCell.getInput(StringTemplate.defaultTemplate));
			inputPanel.getElement().getStyle().setPadding(2, Style.Unit.PX);
			inputPanel.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
			inputPanel.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		}
		add(inputPanel);

		Label outputLabel = new Label();
		outputLabel.getElement().getStyle().setPadding(2, Style.Unit.PX);
		if (casCell!=null && casCell.showOutput()) {
			if (casCell.getLaTeXOutput() != null && !casCell.isError()) {
				SpanElement outputSpan = DOM.createSpan().cast();
				String eqstring = DrawEquationWeb.inputLatexCosmetics(casCell
		                .getLaTeXOutput());
				int el = eqstring.length();
				eqstring = DrawEquationWeb.stripEqnArray(eqstring);
				DrawEquationWeb.drawEquationMathQuill(outputSpan, eqstring, 0
				        , outputLabel.getElement(),false, el == eqstring.length(), true);
				outputSpan.getStyle().setColor(GColor.getColorString(casCell.getAlgebraColor()));
				outputLabel.getElement().appendChild(outputSpan);
			} else {
				if(casCell.isError()){
					outputLabel.getElement().getStyle().setColor("red");
				}
				outputLabel.setText(casCell
				        .getOutput(StringTemplate.defaultTemplate));
			}
		}
		outputPanel = new HorizontalPanel();
		if (casCell!=null) {
			Label commentLabel = new Label();
				commentLabel.setText(casCell.getCommandAndComment());
				commentLabel.getElement().getStyle().setColor("gray");
			outputPanel.add(commentLabel);
		}
		
		outputPanel.add(outputLabel);
		add(outputPanel);

	}
	
	/**
	 * @param editor field for editing
	 */
	public void startEditing(AutoCompleteTextFieldW editor){
		clear();
		add(editor);
		textField = editor;
		textBeforeEdit = inputPanel.getText();
		editor.setText(textBeforeEdit);
		add(outputPanel);
		editor.requestFocus();
	}
	
	/**
	 * Remove editor and show input normally, update the CAS cell input
	 */
	public void stopEditing(){
		if(!textBeforeEdit.equals(textField.getText())){
			casCell.setInput(textField.getText());
			inputPanel.setText(textField.getText());
		}
		clear();
		add(inputPanel);
		add(outputPanel);
	}
	/**
	 * Remove editor and show input normally
	 */
	public void cancelEditing(){
		clear();		
		add(inputPanel);
		add(outputPanel);
	}
	
	/**
	 * @return cas cell represented by this object
	 */
	public GeoCasCell getCASCell(){
		return casCell;
	}

}
