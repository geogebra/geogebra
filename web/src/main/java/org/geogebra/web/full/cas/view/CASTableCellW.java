package org.geogebra.web.full.cas.view;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.cas.view.InputPanel.InputPanelCanvas;
import org.geogebra.web.full.cas.view.InputPanel.InputPanelLabel;
import org.geogebra.web.html5.main.DrawEquationW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.serializer.TeXSerializer;

/**
 * Graphical representation of CAS cells in Web
 * 
 * @author Zbynek Konecny
 *
 */
public class CASTableCellW extends VerticalPanel {
	private GeoCasCell casCell;
	private final InputPanel inputPanel;
	private FlowPanel outputPanel;
	private String textBeforeEdit;
	private CASEditorW textField;
	private String outputText;
	private Label commentLabel;

	/**
	 * Creates new graphical representation of CAS cell
	 * 
	 * @param casCell
	 *            cas cell value
	 * @param app
	 *            application
	 */
	public CASTableCellW(GeoCasCell casCell, App app) {
		this.casCell = casCell;
		inputPanel = (casCell == null || !casCell.isUseAsText())
				? new InputPanelCanvas(
				app)
				: new InputPanelLabel();
		inputPanel.addStyleName("CAS_inputPanel");
		if (casCell != null) {
			inputPanel
			        .setText(casCell.getInput(StringTemplate.defaultTemplate));
			inputPanel.setLaTeX(getLaTeXInputFromCell());
		}
		add(inputPanel);

		Label outputLabel = null;
		outputText = "";
		Canvas canvas = null;
		if (casCell != null && casCell.showOutput()) {
			if (casCell.getLaTeXOutput() != null && !casCell.isError()) {
				String eqstring = casCell.getLaTeXOutput();

				canvas = DrawEquationW.paintOnCanvasOutput(casCell, eqstring,
						null,
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
			if (StringUtil.empty(casCell.getCommandAndComment())) {
				commentLabel.setVisible(false);
			} else {
				commentLabel.setText(casCell.getCommandAndComment() + " ");
			}
			commentLabel.getElement().getStyle()
					.setFontSize(app.getFontSize(), Unit.PX);
			// commentLabel.getElement().getStyle().setColor("gray");
			outputPanel.add(commentLabel);
		}
		outputPanel.add(canvas == null ? outputLabel : canvas);
		outputPanel.setStyleName("CAS_outputPanel");
		add(outputPanel);
	}

	private String getLaTeXInputFromCell() {
		String ret = casCell.getLaTeXInput(StringTemplate.latexTemplate);
		if (ret == null) {
			try {
				MathFormula mf = new Parser(new MetaModel())
						.parse(casCell.getInput(StringTemplate.defaultTemplate));
				return new TeXSerializer().serialize(mf);
			} catch (ParseException e) {
				Log.debug("Cannot parse cas call input");
			}
		}
		return ret == null ? "" : ret;
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
	 * @param casEditorW
	 *            field for editing
	 * @param newText
	 *            editor content to overwrite current
	 * @param asText
	 *            whether this is plain text mode
	 */
	public void startEditing(CASEditorW casEditorW, String newText,
			boolean asText) {
		clear();
		textField = casEditorW;
		add(textField.toWidget());
		textBeforeEdit = inputPanel.getText();

		textField.setEditAsText(asText);
		textField.setText(newText == null ? textBeforeEdit : newText);
		casEditorW.ensureEditing();
		add(outputPanel);
		if (getCASCell() != null && getCASCell().isError()) {
			showError();
		}
		textField.requestFocus();
	}

	/**
	 * Remove editor and show input normally, update the CAS cell input
	 */
	public void stopEditing() {
		if (textField != null && !textBeforeEdit.equals(textField.getText())) {
			setInput();
			inputPanel.setText(textField.getText());
			inputPanel.setLaTeX(textField.getLaTeX());
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

	/**
	 * Set input of cell from textField
	 */
	public void setInput() {
		if (textField != null) {
			casCell.setInput(textField.getText());
			casCell.setLaTeXInput(textField.getLaTeX());
		}
	}

	/**
	 * @return cas cell represented by this object
	 */
	public GeoCasCell getCASCell() {
		return casCell;
	}

	/**
	 * Update font from geo
	 */
	public void setFont() {
		setFont(casCell.getGeoText().getFontStyle());
	}

	/**
	 * @param fontStyle
	 *            font style
	 */
	public void setFont(int fontStyle) {
		if (inputPanel != null) {
			if ((fontStyle & GFont.BOLD) != 0) {
				inputPanel.addStyleName("bold");
			} else {
				inputPanel.removeStyleName("bold");
			}

			if ((fontStyle & GFont.ITALIC) != 0) {
				inputPanel.addStyleName("italic");
			} else {
				inputPanel.removeStyleName("italic");
			}
		}
	}

	/**
	 * Set input color from geo
	 */
	public void setColor() {
		GColor newColor = casCell.getFontColor();
		inputPanel.getElement().getStyle()
				.setColor(GColor.getColorString(newColor));
	}

	/**
	 * @return output panel
	 */
	public Widget getOutputWidget() {
		return outputPanel;
	}

	/**
	 * @return input in CAS (plain text)
	 */
	public String getInputString() {
		return inputPanel.getText();
	}

	/**
	 * @return output string
	 */
	public String getOutputString() {
		return outputText;
	}

	/**
	 * Insert input to currently edited position and start edit mode
	 * 
	 * @param input
	 *            input string
	 */
	public void insertInput(String input) {
		if (textField == null) {
			return;
		}
		textField.insertInput(input);
		textField.ensureEditing();
	}

	/**
	 * @param ratio
	 *            CSS pixel ratio
	 */
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
										.getFontSize(), Unit.PX);
					this.outputPanel.add(this.commentLabel);
				}
				this.outputPanel.add(DrawEquationW.paintOnCanvasOutput(casCell,
						eqstring, null, casCell.getKernel().getApplication()
								.getFontSize() + 1));
			}
		}
		this.inputPanel.setPixelRatio(ratio);
	}

	/**
	 * Show error from CAS cell
	 */
	public void showError() {
		outputPanel.clear();
		outputPanel.add(renderPlain());
	}

	public String getTextBeforeEdit() {
		return textBeforeEdit;
	}
}
