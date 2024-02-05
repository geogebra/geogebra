package org.geogebra.common.spreadsheet.core;

public class ContextMenuItem {
	public enum Identifer {
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
		private Identifer(String localizationKey) {
			this.localizationKey = localizationKey;
		}
	}

	private final Identifer identifer;

	private final Runnable action;

	/**
	 * @param identifer {@link Identifer}
	 * @param action the menu action.
	 */
	public ContextMenuItem(Identifer identifer, Runnable action) {
		this.identifer = identifer;
		this.action = action;
	}

	/**
	 * @param identifer {@link Identifer}
	 */
	public ContextMenuItem(Identifer identifer) {
		this.identifer = identifer;
		action = null;
	}

	public String getLocalizationKey() {
		return identifer.localizationKey;
	}

	public void performAction() {
		action.run();
	}

	public Identifer getIdentifier() {
		return identifer;
	}

}
