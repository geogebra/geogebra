package org.geogebra.web.full.gui.util;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.full.javax.swing.LineThicknessCheckMarkItem;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * popup for border line thickness
 */
public class BorderLineThicknessPopup extends GPopupPanel {
	public static final int BORDER_THIN = 1;
	public static final int BORDER_THICK = 3;
	private LineThicknessCheckMarkItem thin;
	private LineThicknessCheckMarkItem thick;
	private BorderStylePopup parentPopup;
	private PopupMenuHandler popupHandler;

	/**
	 * constructor
	 * @param app see {@link AppW}
	 */
	public BorderLineThicknessPopup(AppW app, BorderStylePopup parentPopup) {
		super(app.getPanel(), app);
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
	public void addPopupHandler(PopupMenuHandler popupMenuHandler) {
		this.popupHandler = popupMenuHandler;
	}

	/**
	 * Pass a popup action event up to the button invoker. If the first button
	 * click triggered our popup (the click was in the triangle region), then we
	 * must pass action events from the popup to the invoker
	 */
	public void handlePopupActionEvent() {
		if (popupHandler != null) {
			popupHandler.fireActionPerformed(parentPopup);
		}

		hide();
	}

	private void addClickHandler(LineThicknessCheckMarkItem selectedItem) {
		ClickStartHandler.init(selectedItem,
				new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				selectThickness(selectedItem.equals(thin),
						selectedItem.equals(thick));
				handlePopupActionEvent();
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