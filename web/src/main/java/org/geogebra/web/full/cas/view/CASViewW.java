package org.geogebra.web.full.cas.view;

import org.geogebra.common.cas.view.CASInputHandler;
import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.common.cas.view.CASView;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.html5.awt.PrintableW;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseMoveEvent;
import org.gwtproject.event.dom.client.MouseUpEvent;
import org.gwtproject.event.dom.client.TouchEndEvent;
import org.gwtproject.event.dom.client.TouchMoveEvent;
import org.gwtproject.event.dom.client.TouchStartEvent;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * Web implementation of the CAS view
 *
 */
public class CASViewW extends CASView implements PrintableW {

	private CASComponentW component;
	private AppW app;
	private CASTableW consoleTable;
	private CASStylebarW styleBar;
	private CASSubDialogW subDialog;

	/**
	 * @param app
	 *            application
	 */
	public CASViewW(final AppW app) {
		super(app.getKernel());
		component = new CASComponentW();
		this.app = app;

		// CAS input/output cells
		CASTableControllerW ml = new CASTableControllerW(this, app);
		consoleTable = new CASTableW(app, ml, this);
		component.add(consoleTable);

		// input handler
		setCasInputHandler(new CASInputHandler(this));

		component.addDomHandler(ml, MouseDownEvent.getType());
		component.addDomHandler(ml, MouseUpEvent.getType());
		component.addDomHandler(ml, MouseMoveEvent.getType());
		component.addBitlessDomHandler(ml, TouchStartEvent.getType());
		component.addBitlessDomHandler(ml, TouchMoveEvent.getType());
		component.addBitlessDomHandler(ml, TouchEndEvent.getType());

		app.invokeLater(() -> {
			getCAS().initCurrentCAS();
			GuiManagerW gm = (GuiManagerW) app.getGuiManager();
			if (gm != null) {
				gm.reInitHelpPanel(true);
			}
		});
		// prevent default: no blur
		ClickStartHandler.init(component, new ClickStartHandler(true, false) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				CASEditorW editor = (CASEditorW) getConsoleTable().getEditor();
				if (!StringUtil.empty(editor.getText())
						&& getConsoleTable().isEditing()) {
					editor.onEnter(true);
				}
			}

		});
	}

	@Override
	public void repaintView() {
		// no repaint needed in Web
	}

	@Override
	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isShowing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CASTableW getConsoleTable() {
		return consoleTable;
	}

	@Override
	public AppW getApp() {
		return app;
	}

	@Override
	public void showSubstituteDialog(String prefix, String evalText,
	        String postfix, int selRow) {
		if (subDialog != null && subDialog.getDialog().isShowing()) {
			return;
		}
		CASSubDialogW d = new CASSubDialogW(this, prefix, evalText, postfix,
		        selRow);
		d.getDialog().show();
		subDialog = d;
	}

	/**
	 * @return widget
	 */
	public CASComponentW getComponent() {
		return component;
	}

	/**
	 * @return style bar
	 */
	public CASStylebarW getCASStyleBar() {
		if (styleBar == null) {
			styleBar = newCASStyleBar();
		}
		return styleBar;
	}

	private CASStylebarW newCASStyleBar() {
		return new CASStylebarW(this, app);
	}

	@Override
	public boolean suggestRepaint() {
		// not used for this view
		return false;
	}

	@Override
	public void setLabels() {
		super.setLabels();
		if (this.styleBar != null) {
			this.styleBar.setLabels();
		}
	}

	/**
	 * @return CAS input editor
	 */
	public MathKeyboardListener getEditor() {
		return (MathKeyboardListener) consoleTable.getEditor();
	}

	/**
	 * @param force
	 *            make keyboard immediately visible
	 */
	public void maybeOpenKeyboard(final boolean force) {
		if (app.isStartedWithFile()
				|| app.showView(App.VIEW_ALGEBRA)) {
			return;
		}
		final AppW app1 = app;
		app.invokeLater(() -> {
			app1.showKeyboard(getEditor(), force);
			getEditor().setFocus(true);
			getConsoleTable().startEditingRow(
					getConsoleTable().getRowCount() - 1);

		});
		/*
		 * getEditor().ensureEditing(); getEditor().setFocus(true);
		 */
	}

	/**
	 * Update inputs and outputs on zoom
	 * 
	 * @param ratio
	 *            CSS pixel ratio
	 */
	public void setPixelRatio(double ratio) {
		if (this.consoleTable != null) {
			for (int row = 0; row < this.getRowCount(); row++) {
				if (consoleTable.getWidget(row,
						CASTableW.COL_CAS_CELLS_WEB) instanceof CASTableCellW) {
					((CASTableCellW) consoleTable.getWidget(row,
							CASTableW.COL_CAS_CELLS_WEB)).setPixelRatio(ratio);
				}
			}
			if (consoleTable.hasEditor()) {
				consoleTable.getEditor().setPixelRatio(ratio);
			}
		}
	}

	@Override
	public void getPrintable(FlowPanel pPanel, Runnable enablePrintBtn) {
		// nothing to do here
	}

	@Override
	public void attachView() {
		super.attachView();
		maybeOpenKeyboard(false);
	}

	@Override
	public void resetItems(boolean unselectAll) {
		this.getConsoleTable().getEditor().onEnter(false);
	}

	/**
	 * Update the fonts of the editor
	 */
	public void updateFonts() {
		CASTableCellEditor editor = consoleTable.getEditor();
		if (editor instanceof CASLaTeXEditor) {
			((CASLaTeXEditor) editor).updateFontSize();
		}
	}
}
