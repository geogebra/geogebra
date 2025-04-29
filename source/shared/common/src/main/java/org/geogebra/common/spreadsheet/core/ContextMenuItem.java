package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public final class ContextMenuItem {
	public enum Identifier {
		CUT("Cut"), COPY("Copy"),
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
	private final @CheckForNull Runnable action;
	private @CheckForNull List<ContextMenuItem> subMenuItems;

	/**
	 * @param identifier {@link Identifier}
	 * @param action the menu action.
	 */
	public ContextMenuItem(@Nonnull Identifier identifier, @CheckForNull Runnable action) {
		this.identifier = identifier;
		this.action = action;
		this.subMenuItems = null;
	}

	/**
	 * Only use for divider and other non-functional items!
	 * @param identifier {@link Identifier}
	 */
	public ContextMenuItem(@Nonnull Identifier identifier) {
		this.identifier = identifier;
		this.action = null;
		this.subMenuItems = null;
	}

	/**
	 * @param identifier {@link Identifier}
	 * @param subMenuIdentifiers list of {@link Identifier} for submenu items
	 * @param subMenuActions list of actions for submenu items
	 */
	public ContextMenuItem(@Nonnull Identifier identifier,
			@Nonnull List<Identifier> subMenuIdentifiers,
			@Nonnull List<Runnable> subMenuActions) {
		this.identifier = identifier;
		action = null;
		subMenuItems = new ArrayList<>();
		for (int index = 0; index < subMenuIdentifiers.size(); index++) {
			ContextMenuItem subMenuItem = new ContextMenuItem(subMenuIdentifiers.get(index),
					subMenuActions.get(index));
			subMenuItems.add(subMenuItem);
		}
	}

	public @Nonnull String getLocalizationKey() {
		return identifier.localizationKey;
	}

	/**
	 * Run the item's action.
	 */
	public void performAction() {
		if (action != null) {
			action.run();
		}
	}

	public @Nonnull Identifier getIdentifier() {
		return identifier;
	}

	public @CheckForNull List<ContextMenuItem> getSubMenuItems() {
		return subMenuItems;
	}
}
