package org.geogebra.desktop.gui.inputfield;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_UP;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.geogebra.common.main.GeoGebraColorConstants;

/**
 * Prepares and shows a JPopupMenu containing the history list for an
 * AutoCompleteTextField. Adapted from OptionsPopup.
 * 
 * @author G. Sturr
 *
 */
public class HistoryPopupD implements ListSelectionListener {

	private AutoCompleteTextFieldD textField;
	private JPopupMenu popup;
	private JList historyList;
	private boolean isDownPopup = false;

	private KeyListener keyListener;
	private KeyListener[] textFieldKeyListeners;
	private DefaultListModel model;

	public HistoryPopupD(AutoCompleteTextFieldD autoCompleteField) {

		this.textField = autoCompleteField;

		historyList = new JList();
		historyList.setCellRenderer(new HistoryListCellRenderer());
		historyList.setBorder(BorderFactory.createEmptyBorder());
		historyList.addListSelectionListener(this);
		historyList.setFocusable(false);

		popup = new JPopupMenu();
		JScrollPane scroller = new JScrollPane(historyList);
		scroller.setBorder(BorderFactory.createEmptyBorder());
		popup.add(scroller);
		popup.setFocusable(false);

		registerListeners();
	}

	/**
	 * Set the font to display the history commands
	 * 
	 * @param font
	 *            the new font
	 */
	public void setFont(Font font) {
		historyList.setFont(font);
	}

	private class PopupListener implements PopupMenuListener {

		private KeyListener[] listListeners;

		public void popupMenuCanceled(PopupMenuEvent e) {
		}

		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			textField.removeKeyListener(keyListener);
			for (KeyListener listener : textFieldKeyListeners) {
				textField.addKeyListener(listener);
			}
		}

		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			// Remove key listeners and replace with own;
			textFieldKeyListeners = textField.getKeyListeners();
			for (KeyListener listener : textFieldKeyListeners) {
				textField.removeKeyListener(listener);
			}
			textField.addKeyListener(keyListener);

			listListeners = historyList.getKeyListeners();
			for (KeyListener listener : listListeners) {
				historyList.removeKeyListener(listener);
			}
			historyList.addKeyListener(keyListener);
		}

	}

	private void registerListeners() {

		// add mouse motion listener to repaint the list for rollover effect
		historyList.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				historyList.repaint();
			}
		});

		// create key listener (will be added by PopupListener)
		keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				handleSpecialKeys(e);
			}
		};

		// add mouse listener
		historyList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				handleMouseClick(e);
			}
		});

		popup.addPopupMenuListener(new PopupListener());
	}

	public void showPopup() {

		// get the current history list and load it into the JList
		ArrayList<String> list = textField.getHistory();

		if (list.isEmpty())
			return;

		model = new DefaultListModel();

		if (isDownPopup)
			for (int i = 0; i < list.size(); i++)
				model.addElement(list.get(list.size() - i - 1));
		else
			for (int i = 0; i < list.size(); i++)
				model.addElement(list.get(i));

		historyList.setModel(model);

		// set the visual features of the list
		int rowCount = Math.min(list.size(), 10);
		historyList.setVisibleRowCount(rowCount);

		if (isDownPopup) {
			historyList.setSelectedIndex(0);
			historyList.ensureIndexIsVisible(0);
		} else {
			historyList.setSelectedIndex(list.size() - 1);
			historyList.ensureIndexIsVisible(list.size() - 1);
		}

		popup.setPreferredSize(null);

		// set the width of the popup equal to the textfield width
		Dimension d = popup.getPreferredSize();
		d.width = textField.getWidth();
		popup.setPopupSize(d);

		// popup.pack();

		// position the popup above/below the text field
		// with small vertical offset to compensate for the shadow in upwards
		// popups
		if (isDownPopup)
			popup.show(textField, 0, textField.getPreferredSize().height);
		else
			popup.show(textField, 0, -popup.getPreferredSize().height - 4);

	}

	public boolean isDownPopup() {
		return isDownPopup;
	}

	public void setDownPopup(boolean isDownPopup) {
		this.isDownPopup = isDownPopup;
	}

	private boolean isPopupVisible() {
		return popup.isVisible();
	}

	private void hidePopup() {
		if (!isPopupVisible()) {
			return;
		}
		popup.setVisible(false);
	}

	/**
	 * handles selection in the history popup; pastes the selected string into
	 * the input field and hides the popup
	 */
	public void valueChanged(ListSelectionEvent evt) {
		if (!evt.getValueIsAdjusting()) {
			if (evt.getSource() == historyList) {
				textField.setText((String) historyList.getSelectedValue());
				// this.setVisible(false);
			}
		}
	}

	private void undoPopupChange() {
		DefaultListModel model = (DefaultListModel) historyList.getModel();
		textField.setText((String) model.getElementAt(model.size() - 1));
	}

	public void handleMouseClick(MouseEvent e) {
		// selection listener has handled text changes, so just exit after a
		// click
		hidePopup();
	}

	public void handleSpecialKeys(KeyEvent keyEvent) {
		if (!isPopupVisible()) {
			return;
		}
		switch (keyEvent.getKeyCode()) {
		case VK_ESCAPE: // [ESC] cancel the popup and undo any changes
			undoPopupChange();
			hidePopup();
			keyEvent.consume();
			break;

		case VK_ENTER:
			hidePopup();
			keyEvent.consume();
			break;

		case VK_DOWN:
			if (!isDownPopup
					&& historyList.getSelectedIndex() == historyList.getModel()
							.getSize() - 1)
				hidePopup();
			else
				navigateRelative(+1);
			break;

		case VK_UP:
			if (isDownPopup && historyList.getSelectedIndex() == 0)
				hidePopup();
			else
				navigateRelative(-1);
			break;

		default:
			hidePopup();
		}
	}

	private void navigateRelative(int offset) {
		boolean up = offset < 0;
		int end = model.getSize() - 1;
		int index = historyList.getSelectedIndex();

		// Wrap around
		if (-1 == index) {
			index = up ? end : 0;
		} else if (0 == index && up || end == index && !up) {
			index = -1;
		} else {
			index += offset;
			index = max(0, min(end, index));
		}

		if (-1 == index) {
			historyList.clearSelection();
		} else {
			historyList.setSelectedIndex(index);
			historyList.ensureIndexIsVisible(index);
		}
	}

	/**
	 * custom cell renderer for the history list, draws grid lines
	 *
	 */
	private class HistoryListCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		/*
		 * private Color bgColor; private Color listSelectionBackground =
		 * MyTable.SELECTED_BACKGROUND_COLOR; private Color listBackground =
		 * Color.white; private Color rolloverBackground = Color.lightGray;
		 */

		// create grid lines with this border
		private Border gridBorder = BorderFactory
				.createCompoundBorder(
						BorderFactory
								.createMatteBorder(
										0,
										0,
										1,
										0,
										org.geogebra.desktop.awt.GColorD
												.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR)),
						BorderFactory.createEmptyBorder(2, 5, 2, 5));

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);

			setText((String) value);
			setBorder(gridBorder);

			/*
			 * setForeground(Color.black);
			 * 
			 * // paint roll-over row Point point = list.getMousePosition(); int
			 * mouseOver = point==null ? -1 : list.locationToIndex(point); if
			 * (index == mouseOver) bgColor = rolloverBackground; else bgColor =
			 * listBackground; setBackground(bgColor);
			 */

			return this;
		}
	}

}
