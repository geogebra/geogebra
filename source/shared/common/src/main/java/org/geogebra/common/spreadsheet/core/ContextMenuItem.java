package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.List;

public class ContextMenuItem {
	public enum Identifier {
		CUT("Cut"), COPY("Copy"),
		PASTE("Paste"),
		DELETE("Delete"),
		CALCULATE("Calculate"),
		SUM("Sum.Tool"),
		MEAN("Mean.Tool"),
		INSERT_ROW_ABOVE("ContextMenu.insertRowAbove"),
		INSERT_ROW_BELOW("ContextMenu.insertRowBelow"),
		DELETE_ROW("ContextMenu.deleteRow"),
		INSERT_COLUMN_LEFT("ContextMenu.insertColumnLeft"),
		INSERT_COLUMN_RIGHT("ContextMenu.insertColumnRight"),
		DELETE_COLUMN("ContextMenu.deleteColumn"),
		DIVIDER("");

		public final String localizationKey;

		Identifier(String localizationKey) {
			this.localizationKey = localizationKey;
		}
	}

	private final Identifier identifier;

	private final Runnable action;

	private List<ContextMenuItem> subMenuItems = new ArrayList<>();

	/**
	 * @param identifier {@link Identifier}
	 * @param action the menu action.
	 */
	public ContextMenuItem(Identifier identifier, Runnable action) {
		this.identifier = identifier;
		this.action = action;
	}

	/**
	 * @param identifier {@link Identifier}
	 */
	public ContextMenuItem(Identifier identifier) {
		this.identifier = identifier;
		action = null;
	}

	/**
	 * @param identifier {@link Identifier}
	 * @param subMenuIdentifiers list of {@link Identifier} for submenu items
	 * @param subMenuActions list of menu action for submebu items
	 */
	public ContextMenuItem(Identifier identifier, List<Identifier> subMenuIdentifiers,
						   List<Runnable> subMenuActions) {
		this.identifier = identifier;
		action = null;
		for (int index = 0; index < subMenuIdentifiers.size(); index++) {
			ContextMenuItem subMenuItem = new ContextMenuItem(subMenuIdentifiers.get(index),
					subMenuActions.get(index));
			subMenuItems.add(subMenuItem);
		}
	}

	public String getLocalizationKey() {
		return identifier.localizationKey;
	}

	public void performAction() {
		action.run();
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public List<ContextMenuItem> getSubMenuItems() {
		return subMenuItems;
	}
}
