package org.geogebra.web.web.gui.view.algebra;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.inputfield.HasSymbolPopup;
import org.geogebra.web.html5.gui.inputfield.HistoryPopupW;
import org.geogebra.web.html5.gui.util.BasicIcons;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.DrawEquationWeb;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.layout.panels.AlgebraDockPanelW;

import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

/**
 * NewRadioButtonTreeItem for creating new formulas in the algebra view
 * 
 * File created by Arpad Fekete
 */
public class NewRadioButtonTreeItem extends RadioButtonTreeItem implements
		HasSymbolPopup, FocusHandler, BlurHandler {

	// How large this number should be (e.g. place on the screen, or
	// scrollable?) Let's allow practically everything




	protected PushButton xButton = null;
	// SymbolTablePopupW tablePopup;

	HistoryPopupW historyPopup;
	EquationEditor editor;




	public NewRadioButtonTreeItem(Kernel kern) {
		super(kern);
		editor = new EquationEditor(app, this);


		//should depend on number of previoous elements?
		addHistoryPopup(true);

		// code copied from AutoCompleteTextFieldW,
		// with some modifications!
		xButton = new PushButton(new Image(
		        GuiResources.INSTANCE.keyboard_close()));
		String id = DOM.createUniqueId();
		// textField.setShowSymbolElement(this.XButton.getElement());
		xButton.getElement().setId(id + "_SymbolButton");
		xButton.getElement().setAttribute("data-visible", "false");
		// XButton.getElement().setAttribute("style", "display: none");
		// XButton.setText("X");
		xButton.addStyleName("SymbolToggleButton");
		xButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				DrawEquationWeb.stornoFormulaMathQuillGGB(
				        NewRadioButtonTreeItem.this, seMayLatex);
				NewRadioButtonTreeItem.this.setFocus(true);
				event.stopPropagation();
				// event.preventDefault();
			}
		});

		ClickStartHandler.init(xButton, new ClickStartHandler(false, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// nothing to do here; just makes sure that
				// event.stopPropagation is called
			}
		});
		try{
			//TRY-CATCH needed for Win8 app //TODO find better solution
			xButton.setFocus(false);
		}catch(Throwable t){
		}
		// add(textField);// done in super()

		// it seems this would be part of the Tree, not of TreeItem...
		// why? web programming knowledge helps: we should add position:
		// relative! to ".GeoGebraFrame .gwt-Tree .gwt-TreeItem .elem"
		
		add(xButton);// dirty hack of adding it two times!
		
		// this was necessary earlier in conjuction with add(xButton)
		// ihtml.getElement().appendChild(xButton.getElement());
		// but later this.replaceXButtonDOM() should be used instead

		xButton.getElement().setAttribute("data-visible", "true");
		addStyleName("SymbolCanBeShown");

		// When scheduleDeferred does not work...
		// this code makes the cursor show when the page loads...
	}

	public void replaceXButtonDOM() {
		getElement().getParentElement().appendChild(xButton.getElement());
	}

	/**
	 * This is the interface of bringing up a popup of suggestions, from a query
	 * string "sub"... in AutoCompleteTextFieldW, this is supposed to be
	 * triggered automatically by SuggestBox, but in NewRadioButtonTreeItem we
	 * have to call this every time for the actual word in the formula (i.e.
	 * updateCurrentWord(true)), when the formula is refreshed a bit! e.g.
	 * DrawEquationWeb.editEquationMathQuillGGB.onKeyUp or something, so this
	 * will be a method to override!
	 */
	@Override
	public boolean popupSuggestions() {
		return editor.popupSuggestions();
	}

	@Override
	public boolean hideSuggestions() {
		return editor.hideSuggestions();
	}

	/**
	 * In case the suggestion list is showing, shuffle its selected element
	 * up/down, otherwise consider up/down event for the history popup!
	 */
	@Override
	public boolean shuffleSuggestions(boolean down) {
		if (editor.sug.isSuggestionListShowing()) {
			if (down) {
				editor.sug.accessMoveSelectionDown();
			} else {
				editor.sug.accessMoveSelectionUp();
			}
			return false;
		} else if (down) {
			if (historyPopup != null && historyPopup.isDownPopup()) {
				// this would give the focus to the historyPopup,
				// which should catch the key events itself, but maybe it's
				// not everything all right here!
				historyPopup.showPopup();
			} else {
				String text = editor.getNextInput();
				if (text != null) {
					editor.setText(text);
				}
			}
		} else {
			if (historyPopup != null && !historyPopup.isDownPopup()) {
				historyPopup.showPopup();
			} else {
				String text = editor.getPreviousInput();
				if (text != null)
					editor.setText(text);
			}
		}
		return true;
	}

	@Override
	public boolean stopNewFormulaCreation(String newValue0, String latex,
	        AsyncOperation callback) {
		if (editor.sug.isSuggestionListShowing()) {
			editor.sugCallback.onSuggestionSelected(editor.sug
			        .accessCurrentSelection());
			return false;
		}
		return super.stopNewFormulaCreation(newValue0, latex, callback);
	}

	public boolean getAutoComplete() {
		return true;
	}

	/**
	 * Note that this method should set the text of the MathQuillGGB-editing box
	 * in MathQuillGGB text() format, not latex()... that's why we should have a
	 * mapping from text() format formulas to latex() format formulas, and keep
	 * it in the historyMap class, which should be filled the same time when
	 * addToHistory is filled!
	 */
	public void setText(String s) {
		editor.setText(s);
	}

	public List<String> resetCompletions() {
		return editor.resetCompletions();
	}











	public List<String> getCompletions() {
		return editor.getCompletions();
	}

	@Override
	public void setFocus(boolean b) {
		//App.printStacktrace("FOCUS" + b);
		DrawEquationWeb.focusEquationMathQuillGGB(seMayLatex, b);

		// if (b)
		// geogebra.html5.main.DrawEquationWeb.scrollCursorIntoView(this,
		// seMayLatex);
		// put to focus handler

		// just allow onFocus/onBlur handlers for new formula creation mode now,
		// a.k.a. this class, but later we may want to add this feature to
		// RadioButtonTreeItem, or editing mode (for existing formulas)
		if (b) {
			onFocus(null);
		} else {
			onBlur(null);
		}
	}



	public void showPopup(boolean show) {
		if (this.xButton == null) {
			return;
		}
		Element showSymbolElement = this.xButton.getElement();
		// App.debug("AF focused" + show);
		if (showSymbolElement != null
		        && "true"
		                .equals(showSymbolElement.getAttribute("data-visible"))) {
			if (show) {
				showSymbolElement.addClassName("shown");
			} else {
				if (!"true".equals(showSymbolElement
				        .getAttribute("data-persist"))) {
					showSymbolElement.removeClassName("shown");
				}
			}
		}
	}

	public void onFocus(FocusEvent event) {
		((AlgebraViewW) av).setActiveTreeItem(null);
		if (((AlgebraViewW) av).isNodeTableEmpty()) {
			((AlgebraDockPanelW) app.getGuiManager().getLayout()
			        .getDockManager().getPanel(App.VIEW_ALGEBRA))
			        .showStyleBarPanel(false);
		} else {
			// the else branch is important since no onBlur on blur!
			((AlgebraDockPanelW) app.getGuiManager().getLayout()
			        .getDockManager().getPanel(App.VIEW_ALGEBRA))
			        .showStyleBarPanel(true);
		}

		Object source = this;
		if (event != null)
			source = event.getSource();

		// this is a static method, and the same which is needed here too,
		// so why duplicate the same thing in another copy?
		// this will call the showPopup method, by the way
		AutoCompleteTextFieldW.showSymbolButtonIfExists(source, true);

		app.getSelectionManager().clearSelectedGeos();

		// this.focused = true; // hasFocus is not needed, AFAIK
	}

	@SuppressWarnings("unused")
	public void onBlur(BlurEvent event) {
		// This method is practically never called, but if it will be,
		// decision shall be made whether it's Okay to make the XButton
		// invisible or not (TODO)
		if (true)
			return;

		((AlgebraDockPanelW) app.getGuiManager().getLayout().getDockManager()
		        .getPanel(App.VIEW_ALGEBRA)).showStyleBarPanel(true);

		Object source = this;
		if (event != null)
			source = event.getSource();

		// this is a static method, and the same which is needed here too,
		// so why duplicate the same thing in another copy?
		// this will call the showPopup method, by the way
		AutoCompleteTextFieldW.showSymbolButtonIfExists(source, false);

		// this.focused = false; // hasFocus is not needed, AFAIK
	}

	public ArrayList<String> getHistory() {
		return editor.getHistory();
	}

	/**
	 * Add a history popup list and an embedded popup button. See
	 * AlgebraInputBar
	 */
	public void addHistoryPopup(boolean isDownPopup) {

		if (historyPopup == null)
			historyPopup = new HistoryPopupW(this);

		historyPopup.setDownPopup(isDownPopup);

		ClickHandler al = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// AGString cmd = event.;
				// AGif (cmd.equals(1 + BorderButton.cmdSuffix)) {
				// TODO: should up/down orientation be tied to InputBar?
				// show popup
				historyPopup.showPopup();

			}
		};
		setBorderButton(1, BasicIcons.createUpDownTriangleIcon(false, true), al);
		this.setBorderButtonVisible(1, false);
	}

	private void setBorderButtonVisible(int i, boolean b) {
		App.debug("setBorderVisible() implementation needed"); // TODO
		                                                       // Auto-generated
	}

	private void setBorderButton(int i, ImageData createUpDownTriangleIcon,
	        ClickHandler al) {
		App.debug("setBorderButton() implementation needed"); // TODO
		                                                      // Auto-generated
	}





	@Override
	public void addToHistory(String str, String latex) {
		editor.addToHistory(str, latex);
	}

	@Override
	public boolean isSuggesting() {
		return editor.sug.isSuggestionListShowing();
	}

	@Override
	public void requestFocus() {
		// TODO Auto-generated method stub

	}


	@Override
	public void updatePosition(ScrollableSuggestionDisplay sug) {
		sug.setPositionRelativeTo(ihtml);
	}


}
