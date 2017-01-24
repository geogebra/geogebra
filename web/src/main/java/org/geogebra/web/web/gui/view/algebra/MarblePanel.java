package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.web.css.GuiResources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ToggleButton;

public class MarblePanel extends FlowPanel {
	private Marble marble;
	private boolean selected = false;
	ToggleButton btnHelpToggle;
	RadioTreeItem item;

	public MarblePanel(RadioTreeItem item) {
		this.item = item;
		marble = new Marble(item);
		marble.setStyleName("marble");
		marble.setEnabled(shouldShowMarble());
		marble.setChecked(item.geo.isEuclidianVisible());

		addStyleName("marblePanel");
		add(marble);
		update();
	}

	public void setHighlighted(boolean selected) {
		this.selected = selected;
	}

	public void update() {
		marble.setEnabled(shouldShowMarble());

		marble.setChecked(item.geo.isEuclidianVisible());

		setHighlighted(selected);
	}

	private boolean shouldShowMarble() {
		return item.geo.isEuclidianShowable()
				&& (!item.getApplication().isExam()
						|| item.getApplication().enableGraphing());
	}

	public boolean isHit(int x, int y) {
		return x > getAbsoluteLeft()
				&& x < getAbsoluteLeft() + getOffsetWidth()
				&& y < getAbsoluteTop() + getOffsetHeight();
	}

	public void updateIcons(boolean warning) {
		if (btnHelpToggle == null) {
			btnHelpToggle = new ToggleButton();
			btnHelpToggle.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					item.showCurrentError();

				}
			});
		}
		// if (!warning) {
		// clearErrorLabel();
		// }
		if (warning) {
			remove(marble);
			add(btnHelpToggle);
			addStyleName("error");
		} else {
			add(marble);
			marble.setEnabled(shouldShowMarble());
			remove(btnHelpToggle);
			removeStyleName("error");
		}
		btnHelpToggle.getUpFace().setImage(new NoDragImage(
				(warning ? GuiResourcesSimple.INSTANCE.icon_dialog_warning()
						: GuiResources.INSTANCE.icon_help()).getSafeUri()
								.asString(),
				24));
		// new
		// Image(AppResources.INSTANCE.inputhelp_left_20x20().getSafeUri().asString()),
		btnHelpToggle.getDownFace().setImage(new NoDragImage(
				(warning ? GuiResourcesSimple.INSTANCE.icon_dialog_warning()
						: GuiResources.INSTANCE.icon_help()).getSafeUri()
								.asString(),
				24));

	}
}