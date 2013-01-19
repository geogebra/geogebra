package geogebra.web.cas.view;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.web.main.DrawEquationWeb;

import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CASTableCellW extends VerticalPanel {
	public CASTableCellW(GeoCasCell casCell) {
		Label inputPanel = new Label();
		inputPanel.setText(casCell.getInput(StringTemplate.defaultTemplate));
		inputPanel.getElement().getStyle().setPadding(2, Style.Unit.PX);
		inputPanel.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		inputPanel.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		add(inputPanel);

		Label outputPanel = new Label();
		outputPanel.getElement().getStyle().setHeight(100, Style.Unit.PX);
		if (casCell.showOutput()) {
			if (casCell.getLaTeXOutput() != null && !casCell.isError()) {
				SpanElement outputSpan = DOM.createSpan().cast();
				outputSpan.getStyle().setPosition(Style.Position.ABSOLUTE);
				DrawEquationWeb.drawEquationMathQuill(outputSpan,
				        DrawEquationWeb.inputLatexCosmetics(casCell
				                .getLaTeXOutput()), outputPanel.getElement());
				outputPanel.getElement().appendChild(outputSpan);
			} else {
				outputPanel.setText(casCell
				        .getOutput(StringTemplate.defaultTemplate));
			}
		}
		add(outputPanel);

	}

}
