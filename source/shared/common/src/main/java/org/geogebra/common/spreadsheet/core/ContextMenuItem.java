package org.geogebra.common.spreadsheet.core;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Context menu items (for spreadsheet).
 */
public class ContextMenuItem {
	/**
	 * Possible context menu items.
	 */
	public enum Identifier {
		CUT("Cut"),
		COPY("Copy"),
		PASTE("Paste"),
		DELETE("Delete"),
		CALCULATE("Calculate"),
		SUM("Sum.Tool"),
		MEAN("Mean.Tool"),
		CREATE_CHART("ContextMenu.CreateChart"),
		LINE_CHART("ContextMenu.LineChart"),
		BAR_CHART("ContextMenu.BarChart"),
		HISTOGRAM("Histogram"),
		PIE_CHART("ContextMenu.PieChart"),
		INSERT_ROW_ABOVE("ContextMenu.insertRowAbove"),
		INSERT_ROW_BELOW("ContextMenu.insertRowBelow"),
		DELETE_ROW("ContextMenu.deleteRow"),
		INSERT_COLUMN_LEFT("ContextMenu.insertColumnLeft"),
		INSERT_COLUMN_RIGHT("ContextMenu.insertColumnRight"),
		DELETE_COLUMN("ContextMenu.deleteColumn"),
		DIVIDER("");

		public final @Nonnull String localizationKey;

		Identifier(@Nonnull String localizationKey) {
			this.localizationKey = localizationKey;
		}
	}

	private final @Nonnull Identifier identifier;

	/**
	 * Creates a context menu item.
	 * @param identifier identifier to specify the context menu item and its displayed text
	 */
	protected ContextMenuItem(@Nonnull Identifier identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the identifier that specifies the context menu item
	 */
	public @Nonnull Identifier getIdentifier() {
		return identifier;
	}

	/**
	 * @return localization key for the displayed text, derived from the identifier
	 */
	public @Nonnull String getLocalizationKey() {
		return identifier.localizationKey;
	}

	/**
	 * Actionable context menu item that can be executed when selected.
	 */
	public static final class ActionableItem extends ContextMenuItem {
		private final Runnable action;

		/**
		 * Creates an actionable context menu item.
		 * @param identifier identifier to specify the context menu item and its displayed text
		 * @param action operation to execute when this item is selected
		 */
		public ActionableItem(@Nonnull Identifier identifier, @Nonnull Runnable action) {
			super(identifier);
			this.action = action;
		}

		/**
		 * Executes the configured operation.
		 */
		public void performAction() {
			this.action.run();
		}
	}

	/**
	 * Nested context menu item with child context menu items.
	 */
	public static final class SubMenuItem extends ContextMenuItem {
		private final List<ContextMenuItem> items;

		/**
		 * Creates a submenu item.
		 * @param identifier identifier to specify the context menu item and its displayed text
		 * @param items child context menu items to be nested in this item
		 */
		public SubMenuItem(@Nonnull Identifier identifier, @Nonnull List<ContextMenuItem> items) {
			super(identifier);
			this.items = items;
		}

		/**
		 * @return the list of child items
		 */
		public @Nonnull List<ContextMenuItem> getItems() {
			return this.items;
		}
	}

	/**
	 * Divider context menu item with no displayed text or behaviour.
	 */
	public static final class Divider extends ContextMenuItem {
		/**
		 * Creates a divider context menu item.
		 */
		public Divider() {
			super(Identifier.DIVIDER);
		}
	}
}
