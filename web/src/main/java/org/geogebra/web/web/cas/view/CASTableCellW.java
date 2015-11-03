package org.geogebra.web.web.cas.view;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.web.cas.view.InputPanel.InputPanelCanvas;
import org.geogebra.web.web.cas.view.InputPanel.InputPanelLabel;
import org.geogebra.web.web.gui.view.algebra.EquationEditorListener;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Graphical representation of CAS cells in Web
 * 
 * @author Zbynek Konecny
 *
 */
public class CASTableCellW extends VerticalPanel {
	private GeoCasCell casCell;
	private InputPanel inputPanel;
	private FlowPanel outputPanel;
	private String textBeforeEdit;
	private AutoCompleteW textField;
	private String outputText;
	private Label commentLabel;

	/**
	 * Creates new graphical representation of CAS cell
	 * 
	 * @param casCell
	 *            cas cell value
	 */
	public CASTableCellW(GeoCasCell casCell, App app) {
		this.casCell = casCell;
		inputPanel = app.has(Feature.CAS_EDITOR)
				&& (casCell == null || !casCell.isUseAsText()) ? new InputPanelCanvas(
				app)
				: new InputPanelLabel();
		inputPanel.addStyleName("CAS_inputPanel");
		if (casCell != null) {
			inputPanel
			        .setText(casCell.getInput(StringTemplate.defaultTemplate));
			inputPanel.setLaTeX(casCell.getLaTeXInput());

		}
		add(inputPanel);

		Label outputLabel = null;
		outputText = "";
		Canvas c = null;
		if (casCell != null && casCell.showOutput()) {
			if (casCell.getLaTeXOutput() != null && !casCell.isError()) {
				String eqstring = casCell.getLaTeXOutput();

				c = DrawEquationW.paintOnCanvas(casCell, eqstring, null,
						casCell.getKernel().getApplication().getFontSize() + 1);

			} else {
				outputLabel = renderPlain();
			}
			// #5119
			outputText = casCell.getOutput(StringTemplate.numericDefault);
		} else {
			outputLabel = new Label();
		}
		outputPanel = new FlowPanel();
		if (casCell != null) {
			commentLabel = new Label();
			commentLabel.addStyleName("CAS_commentLabel");
			commentLabel.setText(casCell.getCommandAndComment());
			commentLabel.getElement().getStyle()
					.setFontSize(app.getFontSizeWeb(), Unit.PX);
			// commentLabel.getElement().getStyle().setColor("gray");
			outputPanel.add(commentLabel);
		}
		outputPanel.add(c == null ? outputLabel : c);
		outputPanel.setStyleName("CAS_outputPanel");
		add(outputPanel);

	}

	private Label renderPlain() {
		Label outputLabel = new Label();
		if (casCell.isError()) {
			outputLabel.getElement().getStyle().setColor("red");
		}
		// #5119
		outputLabel.setText(casCell.getOutput(StringTemplate.numericDefault));
		return outputLabel;
	}

	/**
	 * @param editor
	 *            field for editing
	 */
	public void startEditing(AutoCompleteW editor, String newText) {
		clear();
		add(editor.toWidget());
		textField = editor;
		textBeforeEdit = inputPanel.getText();
		editor.setText(newText == null ? textBeforeEdit : newText);
		add(outputPanel);
		if(getCASCell() != null && getCASCell().isError()){
			outputPanel.clear();
			outputPanel.add(renderPlain());
		}
		editor.requestFocus();
	}

	/**
	 * Remove editor and show input normally, update the CAS cell input
	 */
	public void stopEditing() {
		if (!textBeforeEdit.equals(textField.getText())) {
			setInput();
			inputPanel.setText(textField.getText());
			if (textField instanceof EquationEditorListener) {
				inputPanel.setLaTeX(((EquationEditorListener) textField)
						.getLaTeX());
			}
		}
		clear();
		add(inputPanel);
		add(outputPanel);
	}

	/**
	 * Remove editor and show input normally
	 */
	public void cancelEditing() {
		clear();
		add(inputPanel);
		add(outputPanel);
	}

	public void setInput() {
		casCell.setInput(textField.getText());
		if (textField instanceof EquationEditorListener) {
			casCell.setLaTeXInput(((EquationEditorListener) textField)
					.getLaTeX());
		}
	}

	/**
	 * @return cas cell represented by this object
	 */
	public GeoCasCell getCASCell() {
		return casCell;
	}

	public void setFont() {
		setFont(casCell.getGeoText().getFontStyle());
	}

	public void setFont(int fontStyle) {
		if (inputPanel != null) {
			if ((fontStyle & GFont.BOLD) != 0) {
				inputPanel.addStyleName("bold");
			} else
				inputPanel.removeStyleName("bold");
		}

		if ((fontStyle & GFont.ITALIC) != 0) {
			inputPanel.addStyleName("italic");
		} else
			inputPanel.removeStyleName("italic");

	}

	public void setColor() {
		GColor newColor = casCell.getFontColor();
		inputPanel.getElement().getStyle()
		        .setColor(GColor.getColorString(newColor));
	}

	public Widget getOutputWidget() {
		return outputPanel;
	}

	public String getInputString() {
		return inputPanel.getText();
	}

	public String getOutputString() {
		return outputText;
	}

	public void insertInput(String input) {
		if (textField == null) {
			return;
		}
		textField.insertString(input);
	}

	public void setPixelRatio(double ratio) {
		if (casCell != null && casCell.showOutput()) {
			if (casCell.getLaTeXOutput() != null && !casCell.isError()) {
				String eqstring = casCell.getLaTeXOutput();

				this.outputPanel.clear();
				if (this.commentLabel != null) {
					this.commentLabel
						.getElement()
						.getStyle()
						.setFontSize(
								casCell.getKernel().getApplication()
										.getFontSizeWeb(), Unit.PX);
					this.outputPanel.add(this.commentLabel);
				}
				this.outputPanel.add(DrawEquationW.paintOnCanvas(casCell,
						eqstring, null, casCell.getKernel().getApplication()
								.getFontSizeWeb() + 1));
			}

		}
		if (inputPanel != null) {
			this.inputPanel.setPixelRatio(ratio);
		}
	}

}
