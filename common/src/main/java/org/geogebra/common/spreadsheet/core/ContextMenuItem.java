package org.geogebra.common.spreadsheet.core;

public class ContextMenuItem {
	public enum Identifier {
		CUT("Cut"), COPY("Copy"),
		PASTE("Paste"),
		DELETE("Delete"),
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

	public String getLocalizationKey() {
		return identifier.localizationKey;
	}

	public void performAction() {
		action.run();
	}

	public Identifier getIdentifier() {
		return identifier;
	}

}
