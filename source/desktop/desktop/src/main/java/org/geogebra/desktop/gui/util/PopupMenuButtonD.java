package org.geogebra.desktop.gui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * JButton with popup component. A mouse click on the left side of the button
 * fires a normal action event. A mouse click on the right side triggers a popup
 * with either a selection table, a slider or both. Events generated by the
 * popup are passed up to the button invoker as action events.
 * 
 * 
 * @author G. Sturr
 * 
 */
@SuppressWarnings("javadoc")
public class PopupMenuButtonD extends JButton implements ChangeListener {

	private static final long serialVersionUID = 1L;

	private static final int CLICK_DOWN_ARROW_WIDTH = 20;

	private final SelectionTable mode;
	private Object[] data;
	private final AppD app;
	private final PopupMenuButtonD thisButton;

	private JPopupMenu myPopup;

	private JSlider mySlider;
	private SelectionTableD myTable;
	private Dimension iconSize;

	private final boolean hasTable;

	// flag to determine if the popup should persist after a mouse click
	private boolean keepVisible = true;

	private boolean isDownwardPopup = true;
	private boolean isFixedIcon = false;

	private boolean isIniting = true;
	protected boolean popupIsVisible;

	/**
	 * @param fgColor color
	 */
	public void setFgColor(GColor fgColor) {
		if (myTable != null) {
			myTable.repaint();
		}
		updateGUI();
	}

	public SelectionTableD getMyTable() {
		return myTable;
	}

	public void setIconSize(Dimension iconSize) {
		this.iconSize = iconSize;
	}

	public void setDownwardPopup(boolean isDownwardPopup) {
		this.isDownwardPopup = isDownwardPopup;
	}

	private boolean isStandardButton = false;

	public void setStandardButton(boolean isStandardButton) {
		this.isStandardButton = isStandardButton;
	}

	/*
	 * #*********************************** /** Button constructors
	 */

	/**
	 * @param app application
	 */
	public PopupMenuButtonD(AppD app) {
		this(app, null, -1, -1, null, SelectionTable.UNKNOWN,
				false, false);
	}

	/**
	 * @param app application
	 * @param data data
	 * @param rows rows
	 * @param columns columns
	 * @param iconSize icon size
	 * @param mode selection mode
	 */
	public PopupMenuButtonD(AppD app, Object[] data, int rows, int columns,
			Dimension iconSize, SelectionTable mode) {
		this(app, data, rows, columns, iconSize, mode, true, false);
	}

	/**
	 * @param app application
	 * @param data data
	 * @param rows rows
	 * @param columns columns
	 * @param iconSize icon size
	 * @param mode selection mode
	 * @param hasTable whether table is visible
	 * @param hasSlider whether slider is visible
	 */
	public PopupMenuButtonD(final AppD app, Object[] data, int rows, int columns,
			Dimension iconSize, SelectionTable mode, final boolean hasTable,
			boolean hasSlider) {
		super();
		this.app = app;
		this.hasTable = hasTable;
		this.mode = mode;
		this.iconSize = iconSize;
		this.thisButton = this;

		this.setFocusable(false);

		// create the popup
		myPopup = new JPopupMenu();
		myPopup.setFocusable(false);
		myPopup.setBackground(Color.WHITE);
		myPopup.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.GRAY),
				BorderFactory.createEmptyBorder(3, 3, 3, 3)));

		// add a mouse listener to our button that triggers the popup
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				popupIsVisible = isPopupShowing();
			}

			@Override
			public void mousePressed(MouseEvent e) {

				onMousePressed(e.getX());
			}
		});

		// place text to the left of drop down icon
		this.setHorizontalTextPosition(SwingConstants.LEFT);
		this.setHorizontalAlignment(SwingConstants.LEFT);

		// create selection table
		if (hasTable) {
			this.data = data;

			myTable = new SelectionTableD(app, data, rows, columns, iconSize,
					mode);
			setSelectedIndex(0);

			// add a mouse listener to handle table selection
			myTable.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					handlePopupActionEvent();
				}
			});

			myTable.setBackground(myPopup.getBackground());
			myPopup.add(myTable);
		}

		// create slider
		if (hasSlider) {
			getMySlider();
		}

		isIniting = false;

		if (mode == SelectionTable.MODE_TEXT && iconSize.width == -1) {
			iconSize.width = myTable.getColumnWidth() - 4;
			iconSize.height = myTable.getRowHeight() - 4;
		}

	}

	protected boolean isPopupShowing() {
		return myPopup.isShowing();
	}

	protected void onMousePressed(int x) {
		if (!thisButton.isEnabled()) {
			return;
		}
		if (popupIsVisible && !myPopup.isVisible()) {
			popupIsVisible = false;
			return;
		}

		if (!prepareToShowPopup()) {
			return;
		}
		Point locButton = getLocation();
		final int clicDownArrowWidth = (int) Math
				.round(CLICK_DOWN_ARROW_WIDTH * (app.getScaledIconSize() / 16.0));
		// trigger popup
		// default: trigger only when the mouse is over the right side
		// of the button
		// if isStandardButton: pressing anywhere triggers the popup
		if (isStandardButton || x >= getWidth() - clicDownArrowWidth
				&& x <= getWidth()) {
			if (hasTable) {
				myTable.updateFonts();
			}
			if (isDownwardPopup) {
				// popup appears below the button
				myPopup.show(getParent(), locButton.x, locButton.y
						+ getHeight());
			} else {
				// popup appears above the button
				myPopup.show(getParent(),
						locButton.x - myPopup.getPreferredSize().width
								+ thisButton.getWidth(),
						locButton.y - myPopup.getPreferredSize().height - 2);
			}
		}

		popupIsVisible = myPopup.isShowing();

	}

	/**
	 * Prepares the popup before it is shown. Override this if the popup needs
	 * special handling before opening.
	 * 
	 * @return true if not overridden
	 */
	public boolean prepareToShowPopup() {
		return true;
	}

	/**
	 * Add popup menu item
	 * @param component item to add
	 */
	public void addPopupMenuItem(JComponent component) {
		myPopup.add(component);
	}

	/**
	 * Remove all items.
	 */
	public void removeAllMenuItems() {
		myPopup.removeAll();
	}

	public void setPopupMenu(JPopupMenu menu) {
		myPopup = menu;
	}

	/**
	 * Override processMouseEvents to prevent firing a mouseReleased event and
	 * the resulting ActionPerformed event when the mouse is clicked in the
	 * dropdown triangle region. Clicking in this part of the button should just
	 * trigger the popup. ActionPerformed events will be fired by the popup
	 * following user selection.
	 */
	@Override
	protected void processMouseEvent(MouseEvent e) {
		if (e.getID() == MouseEvent.MOUSE_RELEASED) {
			// mouse is over the popup triangle side of the button
			if (isStandardButton
					|| e.getX() >= getWidth() - app.getScaledIconSize()
							&& e.getX() <= getWidth()) {
				return;
			}
		}

		super.processMouseEvent(e);
	}

	/**
	 * @param geos
	 *            geo elements
	 */
	public void update(List<GeoElement> geos) {
		// override in subclasses
	}

	// =============================================
	// GUI
	// =============================================

	private void updateGUI() {
		if (isIniting) {
			return;
		}

		setIcon(getButtonIcon());

		if (hasTable) {
			myTable.repaint();
		}

		repaint();
	}

	/**
	 * Create our JSlider
	 */
	private void initSlider() {
		mySlider = new JSlider(0, 100);
		mySlider.setMajorTickSpacing(25);
		mySlider.setMinorTickSpacing(5);
		mySlider.setPaintTicks(false);
		mySlider.setPaintLabels(false);

		mySlider.addChangeListener(this);
		SliderUtil.addValueChangeListener(mySlider, val -> app.storeUndoInfo());

		// set slider dimensions
		Dimension d = mySlider.getPreferredSize();
		if (hasTable) {
			d.width = myTable.getPreferredSize().width;
		} else {
			d.width = 110;
		}
		mySlider.setPreferredSize(d);

		mySlider.setBackground(myPopup.getBackground());

		myPopup.add(mySlider);
	}

	// ==============================================
	// Handlers and Listeners
	// ==============================================

	/**
	 * Pass a popup action event up to the button invoker. If the first button
	 * click triggered our popup (the click was in the triangle region), then we
	 * must pass action events from the popup to the invoker
	 */
	public void handlePopupActionEvent() {
		this.fireActionPerformed(new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED, getActionCommand()));
		updateGUI();
		if (!keepVisible) {
			myPopup.setVisible(false);
		}
	}

	/**
	 * Change listener for slider. Fires an action event up to the button
	 * invoker.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		if (mySlider != null) {
			setSliderValue(mySlider.getValue());
		}
		this.fireActionPerformed(new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED, getActionCommand()));
		updateGUI();
	}

	// ==============================================
	// Getters/Setters
	// ==============================================

	public int getSelectedIndex() {
		return myTable.getSelectedIndex();
	}

	public Object getSelectedValue() {
		return myTable.getSelectedValue();
	}

	/**
	 * @param selectedIndex0 selected index; null to deselect
	 */
	public void setSelectedIndex(Integer selectedIndex0) {
		int selectedIndex;
		if (selectedIndex0 == null) {
			selectedIndex = -1;
		} else {
			selectedIndex = selectedIndex0.intValue();
		}

		myTable.setSelectedIndex(selectedIndex);
		updateGUI();
	}

	public int getSliderValue() {
		return mySlider.getValue();
	}

	/**
	 * @param value slider value
	 */
	public void setSliderValue(int value) {

		mySlider.removeChangeListener(this);
		mySlider.setValue(value);
		mySlider.addChangeListener(this);

		if (hasTable) {
			myTable.setSliderValue(value);
		}
		updateGUI();
	}

	/**
	 * @return slider
	 */
	public JSlider getMySlider() {
		if (mySlider == null) {
			initSlider();
		}
		return mySlider;
	}

	public void setKeepVisible(boolean keepVisible) {
		this.keepVisible = keepVisible;
	}

	/**
	 * sets the tooTip strings for the menu selection table; the toolTipArray
	 * should have a 1-1 correspondence with the data array
	 * 
	 * @param toolTipArray tooltips
	 */
	public void setToolTipArray(String[] toolTipArray) {
		myTable.setToolTipArray(toolTipArray);
	}

	// ==============================================
	// Icon Handling
	// ==============================================

	/**
	 * @return button icon
	 */
	public ImageIcon getButtonIcon() {
		ImageIcon icon = (ImageIcon) this.getIcon();
		if (isFixedIcon) {
			return icon;
		}

		// draw the icon for the current table selection
		if (hasTable) {
			switch (mode) {
			case MODE_TEXT:
				// Strings are converted to icons. We don't use setText so that
				// the button size can be controlled
				// regardless of the layout manager.
				String content = getSelectedIndex() >= 0 ? (String) data[getSelectedIndex()] : " ";
				icon = GeoGebraIconD.createStringIcon(
						content, app.getPlainFont(),
						false, false, true, iconSize, Color.BLACK, null);

				break;

			case MODE_ICON:
			case MODE_LATEX:
				icon = (ImageIcon) myTable.getSelectedValue();
				break;

			default:
				icon = myTable.getDataIcon(data[getSelectedIndex()]);
			}
		}
		return icon;
	}

	/**
	 * Append a downward triangle image to the right hand side of an input icon.
	 */
	@Override
	public void setIcon(Icon icon0) {
		if (isFixedIcon) {
			super.setIcon(icon0);
			return;
		}
		Icon icon = icon0;
		if (iconSize == null) {
			if (icon != null) {
				iconSize = new Dimension(icon.getIconWidth(),
						icon.getIconHeight());
			} else {
				iconSize = new Dimension(1, 1);
			}
		}

		if (icon == null) {
			// icon = GeoGebraIcon.createEmptyIcon(1, iconSize.height);
		} else {
			icon = GeoGebraIconD.ensureIconSize((ImageIcon) icon, iconSize);
		}

		// add a down_triangle image to the left of the icon
		if (icon != null) {
			super.setIcon(GeoGebraIconD.joinIcons((ImageIcon) icon,
					app.getScaledIcon(GuiResourcesD.TRIANGLE_DOWN)));
		} else {
			super.setIcon(app.getScaledIcon(GuiResourcesD.TRIANGLE_DOWN));
		}
	}

	/**
	 * @param icon fixed icon (overrides selection)
	 */
	public void setFixedIcon(Icon icon) {
		isFixedIcon = true;
		setIcon(icon);
	}

	/**
	 * Set selected index.
	 * @param selectedIndex selected index
	 */
	public void setIndex(int selectedIndex) {
		myTable.setSelectedIndex(selectedIndex);
	}
}