package geogebra.web.cas.view;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.web.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.web.main.DrawEquationWeb;

import java.util.Iterator;

import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CASTableCellW extends VerticalPanel {
	private GeoCasCell casCell;
	private Label inputPanel;
	private HorizontalPanel outputPanel;
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
				DrawEquationWeb.drawEquationMathQuill(outputSpan,
				        DrawEquationWeb.inputLatexCosmetics(casCell
				                .getLaTeXOutput()), outputLabel.getElement());
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
		if (casCell!=null && casCell.getEvalComment()!=null) {
			Label commentLabel = new Label();
				commentLabel.setText(casCell.getEvalComment());
				commentLabel.getElement().getStyle().setColor("gray");
			outputPanel.add(commentLabel);
		}
		
		outputPanel.add(outputLabel);
		add(outputPanel);

	}
	
	public void startEditing(AutoCompleteTextFieldW editor){
		remove(inputPanel);
		remove(outputPanel);
		add(editor);
		editor.setText(inputPanel.getText());
		add(outputPanel);
		editor.requestFocus();
	}
	
	public void stopEditing(){
		Iterator<Widget>it = getChildren().iterator();
		while(it.hasNext()){
			remove(it.next());
		}		
		add(inputPanel);
		add(outputPanel);
	}
	
	
	public GeoCasCell getCASCell(){
		return casCell;
	}

}
