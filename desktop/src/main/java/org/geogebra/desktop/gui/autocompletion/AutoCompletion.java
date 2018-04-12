package org.geogebra.desktop.gui.autocompletion;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.FileFilter;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import org.geogebra.common.util.debug.Log;

/**
 * This class provides static methods for conveniently installing auto
 * completion for {@link JTextField} and {@link JFileChooser} components.
 * 
 * @author Julian Lettner
 */
public class AutoCompletion {

	// --- Static section ---

	private final static int POPUP_ROW_COUNT_FOR_FILE_CHOOSER = 8;

	private final static FileChooserCompletionListCellRenderer FC_CELL_RENDERER = new FileChooserCompletionListCellRenderer();

	private final static boolean caseInsensitivePaths = initCaseInsenitvePaths();

	private static boolean initCaseInsenitvePaths() {
		try {
			return System.getProperty("os.name").toLowerCase()
					.contains("windows");
		} catch (SecurityException ex) {
			Log.debug("Could not determine underlying os: " + ex);
			return false;
		}
	}

	/**
	 * Convenience method for adding auto completion to a {@link JFileChooser}.
	 * Path name completion will be case(in)sensitive depending on the operating
	 * system.
	 * 
	 * @param fileChooser
	 *            file chooser
	 */
	public static void install(JFileChooser fileChooser) {
		install(fileChooser, caseInsensitivePaths);
	}

	/**
	 * Convenience method for adding auto completion to a {@link JFileChooser}.
	 * 
	 * @param fileChooser
	 *            The file chooser
	 * @param caseInsensitiveCompletion
	 *            <code>true</code> if the casing of path names should be
	 *            ignored for completion
	 */
	public static void install(final JFileChooser fileChooser,
			final boolean caseInsensitiveCompletion) {
		// Extract internal text field
		JTextField textField = getInternalTextField(fileChooser);
		if (null == textField) {
			Log.debug(
					"Could not find an instance of JTextField inside the file chooser: "
							+ fileChooser);
			return;
		}

		CompletionProvider<File> fileChooserCompletionProvider = new CompletionProvider<File>() {
			@Override
			public List<File> getCompletionOptions(String prefix) {
				// Create adapter: javax.swing.filechooser.FileFilter -->
				// java.io.FileFilter
				final javax.swing.filechooser.FileFilter fileChooserFileFilter = fileChooser
						.getFileFilter();
				FileFilter fileFilter = new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return fileChooserFileFilter.accept(pathname);
					}
				};
				// All visible items in the file chooser are possible options
				File[] options = fileChooser.getCurrentDirectory()
						.listFiles(fileFilter);
				// We cannot cache the above steps because the user could change
				// the directory or file filter
				if (options == null) {
					return null;
				}
				CompletionProvider<File> completionProvider = new SortedArrayCompletionProvider<File>(
						options, caseInsensitiveCompletion) {
					@Override
					public String toString(File option) {
						return fileToString(option);
					}
				};

				return completionProvider.getCompletionOptions(prefix);
			}

			@Override
			public String toString(File option) {
				return fileToString(option);
			}
		};

		install(textField, fileChooserCompletionProvider, FC_CELL_RENDERER,
				POPUP_ROW_COUNT_FOR_FILE_CHOOSER);
	}

	/**
	 * @param file
	 *            file
	 * @return filename
	 */
	static String fileToString(File file) {
		return file.getName();
	}

	// TODO-investigate: There should be a better way to get hold of the text
	// field inside a JFileChooser
	// This method assumes that there is exactly one internal JTextField
	private static JTextField getInternalTextField(Container parent) {
		if (parent instanceof JTextField) {
			return (JTextField) parent;
		}

		// Decompose component tree
		for (Component child : parent.getComponents()) {
			if (child instanceof Container) {
				JTextField textField = getInternalTextField((Container) child);
				if (null != textField) {
					return textField; // Return first JTextField found
				}
			}
		}

		// JTextField not found in this subtree
		return null;
	}

	/**
	 * Adds auto completion support to a {@link JTextField}. If dynamic or user
	 * defined completion behavior is needed use
	 * {@link #install(JTextField, CompletionProvider, int)} and specify a
	 * custom {@link CompletionProvider}.
	 * 
	 * @param textField
	 *            The text field
	 * @param completionOptions
	 *            The completion options, will be searched linearly for
	 *            completion matches
	 * @param caseInsensitiveCompletion
	 *            <code>true</code> for case insensitive completion
	 * @param maxPopupRowCount
	 *            The maximum number of rows (height) of the completion popup,
	 *            that is the number of options the user can see without
	 *            scrolling
	 */
	public static void install(JTextField textField, String[] completionOptions,
			boolean caseInsensitiveCompletion, int maxPopupRowCount) {
		// Array will be changed (sorted) - create defensive copy
		String[] optionsCopy = new String[completionOptions.length];
		System.arraycopy(completionOptions, 0, optionsCopy, 0,
				completionOptions.length);
		// Wrap array in provider and install
		CompletionProvider<String> arrayProvider = new SortedArrayCompletionProvider<String>(
				optionsCopy, caseInsensitiveCompletion) {
			@Override
			public String toString(String option) {
				return option;
			}
		};
		install(textField, arrayProvider, maxPopupRowCount);
	}

	/**
	 * Adds auto completion support to a {@link JTextField}. If all you need is
	 * completion for a fixed set of options you may use
	 * {@link #install(JTextField, String[], boolean, int)} instead.
	 * 
	 * @param textField
	 *            The text field
	 * @param completionProvider
	 *            A custom completion provider (for simple strings)
	 * @param maxPopupRowCount
	 *            The maximum number of rows (height) of the completion popup,
	 *            that is the number of options the user can see without
	 *            scrolling
	 */
	public static void install(JTextField textField,
			CompletionProvider<String> completionProvider,
			int maxPopupRowCount) {
		install(textField, completionProvider, new DefaultListCellRenderer(),
				maxPopupRowCount);
	}

	/**
	 * Adds auto completion support to a {@link JTextField}. If all you need is
	 * completion for a fixed set of options you may use
	 * {@link #install(JTextField, String[], boolean, int)} instead. <br>
	 * This method offers the most flexibility. Completion options returned by
	 * the completion provider can be arbitrary objects which in turn are
	 * visualized by the supplied {@link ListCellRenderer}.
	 * 
	 * @param <T>
	 *            The objects returned by the completion provider are of this
	 *            type. The list cell renderer can safely cast the
	 *            <code>value</code> parameter of its method
	 *            {@link ListCellRenderer#getListCellRendererComponent} to this
	 *            type.
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
	 *            The maximum number of rows (height) of the completion popup,
	 *            that is the number of options the user can see without
	 *            scrolling
	 * @return the popup
	 */
	@SuppressWarnings("rawtypes")
	public static <T> Object install(JTextField textField,
			CompletionProvider<T> completionProvider,
			ListCellRenderer listCellRenderer, int maxPopupRowCount) {
		return new OptionsPopup<>(textField, completionProvider,
				listCellRenderer,
				maxPopupRowCount);
	}

}
