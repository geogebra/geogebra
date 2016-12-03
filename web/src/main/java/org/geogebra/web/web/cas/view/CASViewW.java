package org.geogebra.web.web.cas.view;

import org.geogebra.common.cas.view.CASInputHandler;
import org.geogebra.common.cas.view.CASView;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.awt.PrintableW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.GuiManagerW;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

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
		component = new CASComponentW();
		kernel = app.getKernel();
		this.app = app;

		getCAS();

		// CAS input/output cells
		CASTableControllerW ml = new CASTableControllerW(this, app);
		consoleTable = new CASTableW(app, ml, this);
		component.add(consoleTable);
		// SelectionHandler.disableTextSelectInternal(component.getElement(),
		// true);
		// input handler
		casInputHandler = new CASInputHandler(this);

		component.addDomHandler(ml, MouseDownEvent.getType());
		component.addDomHandler(ml, MouseUpEvent.getType());
		component.addDomHandler(ml, MouseMoveEvent.getType());
		component.addDomHandler(ml, TouchStartEvent.getType());
		component.addDomHandler(ml, TouchMoveEvent.getType());
		component.addDomHandler(ml, TouchEndEvent.getType());

		app.getGuiManager().invokeLater(new Runnable() {

			public void run() {
				getCAS().initCurrentCAS();
				GuiManagerW gm = (GuiManagerW) app.getGuiManager();
				if (gm != null) {
					gm.reInitHelpPanel(true);
				}
			}
		});

	}

	@Override
	public void clearView() {
		super.clearView();
		maybeOpenKeyboard(false);
	}

	public void repaintView() {
		// TODO Auto-generated method stub

	}

	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

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
		if (subDialog != null && subDialog.getDialog().isShowing())
			return;
		CASSubDialogW d = new CASSubDialogW(this, prefix, evalText, postfix,
		        selRow);
		d.getDialog().center();
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
		app.getGuiManager().invokeLater(new Runnable() {

			public void run() {
				app1.showKeyboard(getEditor(), force);
				getEditor().setFocus(true, true);
				getConsoleTable().startEditingRow(
						getConsoleTable().getRowCount() - 1);

			}
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
				if (consoleTable.getWidget(row, CASTableW.COL_CAS_CELLS_WEB) instanceof CASTableCellW) {
					((CASTableCellW) consoleTable.getWidget(row,
							CASTableW.COL_CAS_CELLS_WEB)).setPixelRatio(ratio);
				}
			}
		}

	}
	
	@Override
	protected void showTooltip(int mode) {
		if (getApp().showToolBarHelp()) {
			ToolTipManagerW.sharedInstance().showBottomInfoToolTip(
					app.getToolTooltipHTML(mode),
					app.getGuiManager().getTooltipURL(mode),
					ToolTipLinkType.Help, app,
					app.getAppletFrame().isKeyboardShowing());
		}
	}

	public void getPrintable(FlowPanel pPanel, Button btPrint) {
		// Widget[] printableList = {};

		// printableList[0] = new Label("CAS View");
	}

}
