package org.geogebra.web.full.gui.pagecontrolpanel;

import org.geogebra.common.awt.GPoint;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.ContextMenuButtonCard;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.Command;

/**
 * Context Menu of Page Preview Cards
 * 
 * @author Alicia Hofstaetter
 *
 */
public class ContextMenuButtonPreviewCard extends ContextMenuButtonCard {

	private PagePreviewCard card;

	/**
	 * @param app
	 *            application
	 * @param card
	 *            associated preview card
	 */
	public ContextMenuButtonPreviewCard(AppW app, PagePreviewCard card) {
		super(app);
		this.card = card;
	}

	@Override
	protected void initPopup() {
		super.initPopup();
		addDeleteItem();
		addDuplicateItem();
	}

	private void addDeleteItem() {
		addItem(MaterialDesignResources.INSTANCE.delete_black(),
				loc.getMenu("Delete"), new Command() {
					@Override
					public void execute() {
						onDelete();
					}
				});
	}

	private void addDuplicateItem() {
		addItem(MaterialDesignResources.INSTANCE.duplicate_black(),
				loc.getMenu("Duplicate"), new Command() {
					@Override
					public void execute() {
						onDuplicate();
					}
				});
	}

	/**
	 * execute delete action
	 */
	protected void onDelete() {
		hide();
		frame.getPageControlPanel().removePage(card.getPageIndex());
	}

	/**
	 * execute duplicate action
	 */
	protected void onDuplicate() {
		hide();
		frame.getPageControlPanel().duplicatePage(card);
	}

	@Override
	protected void show() {
		super.show();
		wrappedPopup.show(
				new GPoint(getAbsoluteLeft() - 122, getAbsoluteTop() + 36));
	}
}
