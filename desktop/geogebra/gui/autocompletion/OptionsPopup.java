package geogebra.gui.autocompletion;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_PAGE_DOWN;
import static java.awt.event.KeyEvent.VK_PAGE_UP;
import static java.awt.event.KeyEvent.VK_UP;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 * Provides infrastructure for realizing a simple auto completion mechanism for
 * {@link JTextField} components. A user should prefer one of the static
 * <code>install</code> methods in the {@link AutoCompletion} class over
 * instantiating this class directly.
 * 
 * @param <T>
 *            The type of the displayed completion options
 * 
 * @author Julian Lettner
 */
public class OptionsPopup<T> {
	private final JTextField textField;
	private final CompletionProvider<T> completionProvider;
	private final int maxPopupRowCount;

	private final JPopupMenu popup;
	private final DelegatingListModel listModel;
	private final JList list;

	private DocumentListener documentListener;
	private String userInput;
	private int popupRowCount;

	/**
	 * Initializes components and registers event listeners.
	 * 
	 * @param textField
	 *            The text field
	 * @param completionProvider
	 *            A completion provider (The returned values will be the input
	 *            for the supplied {@link ListCellRenderer})
	 * @param listCellRenderer
	 *            A list cell renderer which visualizes the options returned by
	 *            the provided {@link CompletionProvider}
	 * @param maxPopupRowCount
	 *            The maximal number of rows for the options popup
	 */
	public OptionsPopup(JTextField textField,
			CompletionProvider<T> completionProvider,
			ListCellRenderer listCellRenderer, int maxPopupRowCount) {
		this.textField = textField;
		this.completionProvider = completionProvider;
		this.maxPopupRowCount = maxPopupRowCount;

		// Initialize components
		listModel = new DelegatingListModel();
		list = new JList(listModel);
		list.setCellRenderer(listCellRenderer);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFocusable(false);
		popup = new JPopupMenu();
		popup.add(new JScrollPane(list));
		popup.setBorder(BorderFactory.createEmptyBorder());
		popup.setFocusable(false);

		registerListeners();
	}

	private void registerListeners() {
		// Suggest completions on text changes, store reference to listener
		// object
		documentListener = new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				showCompletion();
			}

			public void insertUpdate(DocumentEvent e) {
				showCompletion();
			}

			public void changedUpdate(DocumentEvent e) {
				showCompletion();
			}
		};
		textField.getDocument().addDocumentListener(documentListener);
		// Handle special keys (e.g. navigation)
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				handleSpecialKeys(e);
			}
		});
		// Hide popup when text field loses focus
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				hideOptionsPopup();
			}
		});
		// Allow the user click on a option for completion
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				handleMouseClick(e);
			}
		});
	}

	private void showCompletion() {
		userInput = textField.getText();
		if (null == userInput || 0 == userInput.length()) {
			hideOptionsPopup();
			return;
		}

		List<?> options = completionProvider.getCompletionOptions(userInput);
		if (null != options && 0 != options.size()) {
			listModel.setDataList(options);
			showOptionsPopup();
		} else {
			hideOptionsPopup();
		}
	}

	private void showOptionsPopup() {
		// Adjust size of the popup if necessary
		int newPopupRowCount = Math.min(listModel.getSize(), maxPopupRowCount);
		adjustPopupSize(newPopupRowCount);

		// Show popup just beneath the text field
		if (!isOptionsPopupVisible()) {
			popup.show(textField, 0, textField.getHeight());
		}
	}

	// Adjusts the size of the popup (tricky)
	private void adjustPopupSize(int newPopupRowCount) {
		if (popupRowCount == newPopupRowCount) {
			return;
		}
		popupRowCount = newPopupRowCount;

		// Set visible row count in list
		list.setVisibleRowCount(popupRowCount);
		// Let the UI calculate the preferred size
		popup.setPreferredSize(null);
		// Get the preferred size from the UI
		Dimension size = popup.getPreferredSize();
		// Overwrite width
		size.width = textField.getWidth();
		// Set the preferred size
		popup.setPreferredSize(size);
		popup.pack();
	}

	private boolean isOptionsPopupVisible() {
		return popup.isVisible();
	}

	private void hideOptionsPopup() {
		if (isOptionsPopupVisible()) {
			popup.setVisible(false);
			list.clearSelection();
		}
	}

	private void updateText() {
		T option = (T) list.getSelectedValue();
		String text = option == null ? userInput : completionProvider
				.toString(option);
		Document d = textField.getDocument();
		d.removeDocumentListener(documentListener);
		textField.setText(text);
		d.addDocumentListener(documentListener);
	}

	private void handleSpecialKeys(KeyEvent keyEvent) {
		if (!isOptionsPopupVisible()) {
			return;
		}

		switch (keyEvent.getKeyCode()) {
		case VK_ESCAPE: // [ESC]
			hideOptionsPopup();
			keyEvent.consume();
			break;
		case VK_ENTER: // [ENTER]
			hideOptionsPopup();
			textField.selectAll();
			break;
		case VK_DOWN: // [DOWN]
			navigateRelative(+1);
			break;
		case VK_UP: // [UP]
			navigateRelative(-1);
			break;
		case VK_PAGE_DOWN: // [PAGE_DOWN]
			navigateRelative(+maxPopupRowCount - 1);
			break;
		case VK_PAGE_UP: // [PAGE_UP]
			navigateRelative(-maxPopupRowCount + 1);
			break;
		}
	}

	private void navigateRelative(int offset) {
		boolean up = offset < 0;
		int end = listModel.getSize() - 1;
		int index = list.getSelectedIndex();

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
			list.clearSelection();
		} else {
			list.setSelectedIndex(index);
			list.ensureIndexIsVisible(index);
		}
		updateText();
	}

	private void handleMouseClick(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			updateText();
			hideOptionsPopup();
			textField.selectAll();
		}
	}

}
