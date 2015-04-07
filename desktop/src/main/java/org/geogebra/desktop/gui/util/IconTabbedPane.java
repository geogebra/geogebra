package org.geogebra.desktop.gui.util;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * Tabbed pane with icons and a minimalistic appearance.
 * 
 * This tabbed pane will just use the special IconTabbedPaneUI class for it's
 * UI. It's required to reconstruct the UI object if updateUI() is called (e.g.
 * because the font size changed).
 * 
 * @author Florian Sonner
 */
public class IconTabbedPane extends JTabbedPane {
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * Set the UI of this component to the IconTabbedPaneUI.
	 */
	public IconTabbedPane() {
		setUI(new IconTabbedPaneUI());
	}

	/**
	 * Ignore any non IconTabbedPaneUI objects.
	 */
	@Override
	public void setUI(TabbedPaneUI ui) {
		if (ui instanceof IconTabbedPaneUI) {
			super.setUI(ui);
		}
	}

	/**
	 * Update the UI of this component.
	 * 
	 * This will lead to an update of the fonts of the UI as just the font size
	 * should change.
	 */
	@Override
	public void updateUI() {
		if (ui instanceof IconTabbedPaneUI) {
			((IconTabbedPaneUI) getUI()).updateFont();
		}
	}

	/**
	 * Custom UI for the tabbed pane with icons.
	 * 
	 * @author Florian Sonner
	 */
	class IconTabbedPaneUI extends BasicTabbedPaneUI {
		/**
		 * The background color for tabs which are neither active not hovered.
		 */
		private Color bgColor;

		/**
		 * The background color of active tabs (i.e. the content of this tab is
		 * currently displayed).
		 */
		private Color bgActiveColor;

		/**
		 * The background color of tabs the mouse is over at the moment. Will
		 * not apply to active tabs.
		 */
		private Color bgHoverColor;

		/**
		 * Initialization of default values.
		 */
		@Override
		protected void installDefaults() {
			super.installDefaults();
			tabAreaInsets = new Insets(0, 15, 0, 15);
			contentBorderInsets = new Insets(3, 3, 3, 3);
			tabInsets = new Insets(10, 10, 10, 10);
			selectedTabPadInsets = new Insets(0, 0, 0, 0);

			bgColor = Color.white;
			bgActiveColor = new Color(193, 210, 238);
			bgHoverColor = new Color(224, 232, 246);
		}

		/**
		 * Uninstall our custom defaults.
		 */
		@Override
		protected void uninstallDefaults() {
			super.uninstallDefaults();

			bgColor = null;
			bgActiveColor = null;
			bgHoverColor = null;
		}

		/**
		 * Update the font.
		 */
		public void updateFont() {
			LookAndFeel.installColorsAndFont(tabPane, "TabbedPane.background",
					"TabbedPane.foreground", "TabbedPane.font");
		}

		/**
		 * Paint the tab border.
		 */
		@Override
		protected void paintTabBorder(Graphics g, int tabPlacement,
				int tabIndex, int x, int y, int w, int h, boolean isSelected) {
			g.setColor(SystemColor.controlShadow);
			g.drawLine(x, y, x, y + h - 1);
			g.drawLine(x + w, y, x + w, y + h - 1);
		}

		/**
		 * Paint the background of the tabs.
		 */
		@Override
		protected void paintTabBackground(Graphics g, int tabPlacement,
				int tabIndex, int x, int y, int w, int h, boolean isSelected) {
			g.setColor(isSelected ? bgActiveColor
					: (tabIndex == getRolloverTab() ? bgHoverColor : bgColor));
			g.fillRect(x, y, w, h);
		}

		/**
		 * Repaint the tabbed pane if the mouse is hovering a new tab.
		 */
		@Override
		protected void setRolloverTab(int index) {
			if (getRolloverTab() != index) {
				super.setRolloverTab(index);
				repaint();
			}
		}

		/**
		 * Fill the background with white.
		 */
		@Override
		protected void paintTabArea(Graphics g, int tabPlacement,
				int selectedIndex) {
			g.setColor(Color.white);

			g.fillRect(
					0,
					0,
					tabPane.getBounds().width,
					calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight));

			super.paintTabArea(g, tabPlacement, selectedIndex);
		}

		/**
		 * Use a custom layout for the label (icon centered, text below icon).
		 * 
		 * Copy 'n' paste from the original source of BasicTabbedPaneUI.
		 */
		@Override
		protected void layoutLabel(int tabPlacement, FontMetrics metrics,
				int tabIndex, String title, Icon icon, Rectangle tabRect,
				Rectangle iconRect, Rectangle textRect, boolean isSelected) {
			textRect.x = 0;
			textRect.y = 0;
			textRect.width = 0;
			textRect.height = 0;
			iconRect.x = 0;
			iconRect.y = 0;
			iconRect.width = 0;
			iconRect.height = 0;

			// -- just this has to be changed to change the layout of the tabs
			// --
			SwingUtilities.layoutCompoundLabel(tabPane, metrics, title, icon,
					SwingConstants.CENTER, SwingConstants.CENTER,
					SwingConstants.BOTTOM, SwingConstants.CENTER, tabRect,
					iconRect, textRect, textIconGap);

			int shiftX = getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
			int shiftY = getTabLabelShiftY(tabPlacement, tabIndex, isSelected);

			iconRect.x += shiftX;
			iconRect.y += shiftY;

			textRect.x += shiftX;
			textRect.y += shiftY;
		}

		/**
		 * The tab should always have enough space for a 32x32 icon and the
		 * label.
		 */
		@Override
		protected int calculateTabHeight(int tabPlacement, int tabIndex,
				int fontHeight) {
			if (!isEnabledAt(tabIndex))
				return 0;

			return fontHeight + 45;
		}

		/**
		 * Reduce the tab width by 32 as the icon is not drawn in one line with
		 * the text.
		 */
		@Override
		protected int calculateTabWidth(int tabPlacement, int tabIndex,
				FontMetrics metrics) {
			if (!isEnabledAt(tabIndex))
				return 0;

			return super.calculateTabWidth(tabPlacement, tabIndex, metrics) - 32;
		}

		/**
		 * Do not move the label if we select a tab (always return 0 as shift).
		 */
		@Override
		protected int getTabLabelShiftY(int tabPlacement, int tabIndex,
				boolean isSelected) {
			return 0;
		}

		/**
		 * Paint the top border.
		 */
		@Override
		protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
				int selectedIndex, int x, int y, int w, int h) {
			g.setColor(SystemColor.controlDkShadow);
			g.drawLine(x, y, x + w, y);
			g.setColor(SystemColor.controlLtHighlight);
			g.drawLine(x, y + 1, x + w, y + 1);
		}

		@Override
		protected void paintFocusIndicator(Graphics g, int tabPlacement,
				Rectangle[] rects, int tabIndex, Rectangle iconRect,
				Rectangle textRect, boolean isSelected) {
			/* paint nothing.. */
		}

		@Override
		protected void paintContentBorderRightEdge(Graphics g,
				int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
			/* paint nothing */
		}

		@Override
		protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement,
				int selectedIndex, int x, int y, int w, int h) {
			/* paint nothing */
		}

		@Override
		protected void paintContentBorderBottomEdge(Graphics g,
				int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
			/* paint nothing */
		}
	}
}