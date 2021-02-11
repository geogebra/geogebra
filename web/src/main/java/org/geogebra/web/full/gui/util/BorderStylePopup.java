package org.geogebra.web.full.gui.util;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.properties.BorderType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.user.client.ui.FlowPanel;

public class BorderStylePopup extends PopupMenuButtonW {

	private BorderLineThicknessPopup borderThicknessPopup;
	private FlowPanel borderThicknessBtn;

	/**
	 * constructor
	 * @param app see {@link AppW}
	 * @param borderStyles - image src of border styles
	 */
	public BorderStylePopup(AppW app, ImageOrText[] borderStyles) {
		super(app, borderStyles, 1, 4,
				SelectionTable.MODE_ICON);
		getMyPopup().removeStyleName("matPopupPanel");
		getMyPopup().addStyleName("mowPopup");
		getMyPopup().addStyleName("borderStylePopup");
		extendGUI();
		getMyPopup().addCloseHandler(event -> borderThicknessPopup.hide());
	}

	private void extendGUI() {
		FlowPanel divider = new FlowPanel();
		divider.addStyleName("divider");
		((ButtonPopupMenu) getMyPopup()).getPanel().add(divider);

		borderThicknessBtn = new FlowPanel();
		borderThicknessBtn.addStyleName("borderThicknessBtn");
		NoDragImage penImg = new NoDragImage(
				MaterialDesignResources.INSTANCE.edit_black(), 24);
		penImg.addStyleName("penImg");

		borderThicknessBtn.add(penImg);
		NoDragImage arrowImg = new NoDragImage(
				MaterialDesignResources.INSTANCE.arrow_drop_down(), 20);
		arrowImg.addStyleName("arrowImg");
		borderThicknessBtn.add(arrowImg);
		ClickStartHandler.init(borderThicknessBtn, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				Dom.toggleClass(borderThicknessBtn, "active",
						!borderThicknessPopup.isShowing());
				if (borderThicknessPopup.isShowing()) {
					borderThicknessPopup.hide();
				} else {
					borderThicknessPopup.showRelativeTo(borderThicknessBtn);
				}
			}
		});

		borderThicknessPopup =
				new BorderLineThicknessPopup(app, this);
		((ButtonPopupMenu) getMyPopup()).getPanel().add(borderThicknessBtn);
	}

	public BorderLineThicknessPopup getBorderThicknessPopup() {
		return borderThicknessPopup;
	}

	public int getBorderThickness() {
		return borderThicknessPopup.getBorderThickness();
	}

	public void setBorderThickness(int thickness) {
		borderThicknessPopup.setBorderThickness(thickness);
	}

	public BorderType getBorderType() {
		return BorderType.values()[getSelectedIndex()];
 	}

	/**
	 * get button index of certain border type
	 * @param type border type
	 * @return index of button based on border type
	 */
 	public Integer getBorderTypeIndex(BorderType type) {
		return type == BorderType.MIXED ? -1 : type.ordinal();
	}

	public FlowPanel getBorderThicknessBtn() {
		return borderThicknessBtn;
	}
}