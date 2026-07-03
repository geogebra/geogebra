/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.view.algebra;

import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.editor.share.controller.MatrixResizeController;
import org.geogebra.editor.share.controller.MatrixResizeController.State;
import org.geogebra.editor.web.MathFieldW;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

/**
 * Provides a matrix/vector resizing popup together with a button that opens it.
 */
public class MatrixResizePopup implements MatrixResizeController.StateListener {
	private static final int POPUP_HEIGHT = 74;
	private static final int POPUP_HALF_WIDTH = 224 / 2;
	private static final int INDICATOR_HALF_WIDTH = 28 / 2;
	private final AppW app;
	private final MathFieldW mathField;
	private final MatrixResizeController controller;
	private final Runnable onStateChanged;
	private GPopupPanel popupPanel;
	private StandardButton openResizeButton;
	private StandardButton removeRow;
	private StandardButton addRow;
	private StandardButton removeCol;
	private StandardButton addCol;
	private Label rowCount;
	private Label colCount;

	MatrixResizePopup(
			@Nonnull MatrixResizeController matrixResizeController,
			@Nonnull MathFieldW mathField,
			@Nonnull AppW app,
			@Nonnull Runnable onStateChanged) {
		this.controller = matrixResizeController;
		this.app = app;
		this.mathField = mathField;
		this.onStateChanged = onStateChanged;
	}

	@Override
	public void stateChanged(@Nonnull State state) {
		MatrixResizeController.PopupState popupState = state.popupState();
		if (popupState == null) {
			hide();
		} else {
			show(popupState);
			onStateChanged.run();
		}
	}

	void hide() {
		if (popupPanel != null) {
			popupPanel.hide();
		}
		if (openResizeButton != null) {
			openResizeButton.removeFromParent();
		}
		popupPanel = null;
		openResizeButton = null;
	}

	private void show(MatrixResizeController.PopupState popupState) {
		if (popupPanel == null) {
			buildUI();
		}
		removeCol.setEnabled(popupState.controlState().isRemoveColumnEnabled());
		addCol.setEnabled(popupState.controlState().isAddColumnEnabled());
		removeRow.setEnabled(popupState.controlState().isRemoveRowEnabled());
		addRow.setEnabled(popupState.controlState().isAddRowEnabled());
		rowCount.setText(popupState.controlState().rows());
		colCount.setText(popupState.controlState().columns());
		double center = (popupState.anchor().getMinX() + popupState.anchor().getMaxX()) / 2;
		Widget mathInput = mathField.asWidget();
		double indicatorLeft = Math.min(center, mathInput.getParent().getOffsetWidth()
				- INDICATOR_HALF_WIDTH)
				+ mathInput.getAbsoluteLeft() - app.getAbsLeft() - INDICATOR_HALF_WIDTH;
		double indicatorTop = popupState.indicatorOffset() + mathInput.getAbsoluteTop()
				- app.getAbsTop();
		double popupCenter = Math.max(Math.min(indicatorLeft + 14,
				app.getWidth() - POPUP_HALF_WIDTH), POPUP_HALF_WIDTH);
		double popupTop = indicatorTop + 12;
		if (popupTop + POPUP_HEIGHT > app.getHeight()) {
			popupTop = Math.max(0, mathInput.getAbsoluteTop() - app.getAbsTop() - POPUP_HEIGHT);
		}
		popupPanel.setPopupPosition((int) popupCenter, (int) popupTop);
		openResizeButton.getElement().getStyle().setLeft(indicatorLeft, Unit.PX);
		openResizeButton.getElement().getStyle().setTop(indicatorTop, Unit.PX);
	}

	private void buildUI() {
		SVGResource moreIcon = KeyboardResources.INSTANCE.keyboard_more();
		openResizeButton = new StandardButton(moreIcon, 16);
		app.getAppletFrame().add(openResizeButton);
		openResizeButton.addStyleName("openResizeControlButton");
		popupPanel = new GPopupPanel(true, false, app.getAppletFrame(), app);
		popupPanel.addStyleName("resizePopup");
		popupPanel.addCloseHandler(
				evt -> setOpeningButtonActive(false, moreIcon));
		ClickStartHandler.init(openResizeButton, new ClickStartHandler(true, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				setOpeningButtonActive(!popupPanel.isShowing(), moreIcon);
				togglePopup();
			}
		});
		// the button toggles the popup -> avoid autohide
		popupPanel.addAutoHidePartner(openResizeButton.getElement());
		ClickStartHandler.initDefaults(popupPanel, true, true);
		FlowPanel panelContent = new FlowPanel();
		panelContent.addStyleName("resizePopupContent");
		popupPanel.add(panelContent);
		FlowPanel rowControls = buildRowControls();
		FlowPanel columnControls = buildColumnControls();
		panelContent.add(rowControls);
		NoDragImage separator = new NoDragImage(GuiResourcesSimple.INSTANCE.close()
				.withFill(GeoGebraColorConstants.NEUTRAL_700.toString()), 16);
		separator.setStyleName("resizeControlsSeparator");
		panelContent.add(separator);
		panelContent.add(columnControls);
		rowControls.addStyleName("resizeControlGroup");
		columnControls.addStyleName("resizeControlGroup");
	}

	private void togglePopup() {
		if (!popupPanel.isShowing()) {
			popupPanel.show();
		} else {
			popupPanel.hide();
		}
	}

	private void setOpeningButtonActive(boolean showing, SVGResource moreIcon) {
		GColor color = showing ? GeoGebraColorConstants.PURPLE_700
				: GeoGebraColorConstants.NEUTRAL_800;
		if (openResizeButton != null) {
			openResizeButton.setIcon(moreIcon.withFill(color.toString()));
			openResizeButton.setStyleName("active", popupPanel.isShowing());
		}
	}

	private FlowPanel buildRowControls() {
		FlowPanel rowControls = new FlowPanel();
		removeRow = newStandardButton(MaterialDesignResources.INSTANCE.minus_black(),
				controller::removeRow);
		addRow = newStandardButton(MaterialDesignResources.INSTANCE.add_black(),
				controller::addRow);
		Label rowTitle = new Label(app.getLocalization().getMenu("Rows"));
		rowTitle.setStyleName("groupTitle");
		rowControls.add(rowTitle);
		rowControls.add(removeRow);
		rowCount = new Label();
		rowControls.add(rowCount);
		rowControls.add(addRow);
		return rowControls;
	}

	private FlowPanel buildColumnControls() {
		FlowPanel columnControls = new FlowPanel();
		removeCol = newStandardButton(MaterialDesignResources.INSTANCE.minus_black(),
				controller::removeColumn);
		addCol = newStandardButton(MaterialDesignResources.INSTANCE.add_black(),
				controller::addColumn);
		Label columnTitle = new Label(app.getLocalization().getMenu("Columns"));
		columnTitle.setStyleName("groupTitle");
		columnControls.add(columnTitle);
		columnControls.add(removeCol);
		colCount = new Label();
		columnControls.add(colCount);
		columnControls.add(addCol);
		return columnControls;
	}

	private StandardButton newStandardButton(SVGResource s, Runnable onClick) {
		StandardButton button = new StandardButton(s, 16);
		ClickStartHandler.init(button, new ClickStartHandler(true, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				onClick.run();
			}
		});
		button.addStyleName("resizeButton");
		return button;
	}
}
