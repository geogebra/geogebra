package org.geogebra.common.contextmenu;

/**
 * Filter for context menu items.
 */
public interface ContextMenuItemFilter {
    /**
     * Evaluates whether the specified context menu item is allowed by this filter.
     *
     * @param contextMenuItem the item to be evaluated
     * @return {@code true} if the values is allowed, {@code false} otherwise
     */
    boolean isAllowed(ContextMenuItem contextMenuItem);
}
