package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.DrawEquationW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.web.MathFieldW;

public class LatexTreeItem extends RadioTreeItem {

	public LatexTreeItem(GeoElement geo0) {
		super(geo0);
	}

	protected boolean startEditing(boolean substituteNumbers) {
		String text = getTextForEditing(substituteNumbers,
				StringTemplate.latexTemplate);
		if (text == null) {
			return false;
		}
		if (errorLabel != null) {
			errorLabel.setText("");
		}

		Widget old = latex ? (canvas != null ? canvas : latexItem)
					: getPlainTextItem();

			renderLatex(text, old, true);



		DrawEquationW.editEquationMathQuillGGB(this, latexItem, false);

		app.getGuiManager().setOnScreenKeyboardTextField(this);
		CancelEventTimer.keyboardSetVisible();
		ClickStartHandler.init(main, new ClickStartHandler(false, false) {
			@Override
			public void onClickStart(int x, int y,
					final PointerEventType type) {
				app.getGuiManager()
						.setOnScreenKeyboardTextField(LatexTreeItem.this);
				// prevent that keyboard is closed on clicks (changing
				// cursor position)
				CancelEventTimer.keyboardSetVisible();
			}
		});

		return true;
	}

	private void renderLatex(String text0, Element old, boolean forceMQ) {
		if (!forceMQ) {
			canvas = DrawEquationW.paintOnCanvas(geo, text0, canvas,
					getFontSize());

			if (canvas != null && ihtml.getElement().isOrHasChild(old)) {
				ihtml.getElement().replaceChild(canvas.getCanvasElement(), old);
			}

		} 
		else {
			// // // Log.debug(REFX + "renderLatex 2");
			if (latexItem == null) {
				latexItem = new FlowPanel();
			}
			latexItem.clear();
			latexItem.addStyleName("avTextItem");
		//TODO	updateColor(latexItem);

			ihtml.clear();


			String text = text0;
			if (text0 == null) {
				text = "";
			}
			text = DrawEquationW.inputLatexCosmetics(text);

			String latexString = "";
			latexString = "\\mathrm {" + text + "}";


			ihtml.add(latexItem);
			if (canvas == null) {
				DrawEquationW.drawEquationAlgebraView(latexItem, latexString,
					isInputTreeItem());
			} else {
				MathFieldW mf = new MathFieldW(ihtml.getElement(),
						canvas.getContext2d());

				app.getAppletFrame().showKeyBoard(true, null, false);
			}


		}
	}

}
