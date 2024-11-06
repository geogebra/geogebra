package org.geogebra.web.full.gui.util;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.full.euclidian.EuclidianStyleBarW;
import org.geogebra.web.full.javax.swing.LineThicknessCheckMarkItem;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * popup for border thickness
 */
public class BorderLineThicknessPopup extends GPopupPanel {
	public static final int BORDER_THIN = 1;
	public static final int BORDER_THICK = 3;
	private LineThicknessCheckMarkItem thin;
	private LineThicknessCheckMarkItem thick;
	private final BorderStylePopup parentPopup;
	private EuclidianStyleBarW popupHandler;

	/**
	 * constructor
	 * @param app see {@link AppW}
	 * @param parentPopup border style popup
	 */
	public BorderLineThicknessPopup(AppW app, BorderStylePopup parentPopup) {
		super(app.getAppletFrame(), app);
		setAutoHideEnabled(false);
		this.parentPopup = parentPopup;
		addStyleName("mowPopup");
		addStyleName("lineThicknessPopup");
		initGUI();
		addClickHandler(thin);
		addClickHandler(thick);
	}

	private void initGUI() {
		FlowPanel popupContent = new FlowPanel();

		thin = new LineThicknessCheckMarkItem("thin", 1);
		popupContent.add(thin);
		thin.setSelected(true);

		thick = new LineThicknessCheckMarkItem("thick", 3);
		popupContent.add(thick);
		thick.setSelected(false);

		add(popupContent);
	}

	/**
	 * @param popupMenuHandler
	 *            {@link PopupMenuHandler}
	 */
	public void addPopupHandler(EuclidianStyleBarW popupMenuHandler) {
		this.popupHandler = popupMenuHandler;
	}

	private void addClickHandler(LineThicknessCheckMarkItem selectedItem) {
		ClickStartHandler.init(selectedItem,
				new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				selectThickness(selectedItem.equals(thin),
						selectedItem.equals(thick));
				if (popupHandler != null) {
					popupHandler.processSelectionWithUndoAction(popupHandler::handleBorderStyle);
				}

				hide();
			}
		});
	}

	@Override
	public void hide() {
		super.hide();
		parentPopup.getBorderThicknessBtn().removeStyleName("active");
	}

	public int getBorderThickness() {
		return thick.isSelected() ? BORDER_THICK : BORDER_THIN;
	}

	/**
	 * set border thickness
	 * @param thickness border thickness
	 */
	public void setBorderThickness(int thickness) {
		selectThickness(thickness == BORDER_THIN,
				thickness == BORDER_THICK);

	}

	private void selectThickness(boolean isThin, boolean isThick) {
		thin.setSelected(isThin);
		thick.setSelected(isThick);
	}
}