package org.geogebra.desktop.cas.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;

import org.geogebra.common.cas.view.CASSubDialog;
import org.geogebra.common.cas.view.CASView;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.desktop.gui.inputfield.MathTextField;
import org.geogebra.desktop.gui.layout.LayoutD;
import org.geogebra.desktop.main.AppD;

/**
 * Dialog to substitute expressions in CAS Input.
 * 
 */
public class CASSubDialogD extends CASSubDialog implements ActionListener {

	private AppD app;
	private CASViewD casView;

	private JButton btSub, btEval, btNumeric;
	private JScrollPane scrollPane;
	private JPanel optionPane, btPanel, captionPanel;
	private JTable replaceTable;
	private JDialog dialog;

	private static final int DEFAULT_TABLE_WIDTH = 200;
	private static final int DEFAULT_TABLE_HEIGHT = 150;
	private static final int DEFAULT_TABLE_CELL_HEIGHT = 21;
	private static final double DEFAULT_FONT_SIZE = 12.;

	/**
	 * Substitute dialog for CAS.
	 * 
	 * @param casView
	 *            view
	 * @param prefix
	 *            before selection, not effected by the substitution
	 * @param evalText
	 *            the String which will be substituted
	 * @param postfix
	 *            after selection, not effected by the substitution
	 * @param editRow
	 *            row to edit
	 */
	public CASSubDialogD(CASViewD casView, String prefix, String evalText,
			String postfix, int editRow) {
		super(prefix, evalText, postfix, editRow);

		this.casView = casView;
		this.app = casView.getApp();

		createGUI();
		dialog.pack();
		dialog.setLocationRelativeTo(casView.getCASViewComponent());
	}

	/**
	 * 
	 */
	protected void createGUI() {
		// do not dock the substitution dialog to the main frame: ticket 1832
		dialog = new JDialog(
				(JFrame) ((LayoutD) app.getGuiManager().getLayout())
						.getDockManager().getPanel(App.VIEW_CAS).getFrame());
		dialog.setModal(false);
		Localization loc = getApp().getLocalization();
		dialog.setTitle(loc.getMenu("Substitute") + " - "
				+ loc.getCommand("Row") + " " + (editRow + 1));
		dialog.setResizable(true);

		GeoCasCell cell = casView.getConsoleTable().getGeoCasCell(editRow);

		initData(cell);

		Vector<String> header = new Vector<>();
		header.add(loc.getMenu("OldExpression"));
		header.add(loc.getMenu("NewExpression"));
		replaceTable = new JTable(data, header);
		replaceTable.setDefaultEditor(Object.class, new MathTextCellEditor());
		replaceTable.getTableHeader().setReorderingAllowed(false);
		double fontFactor = Math.max(1,
				getApp().getGUIFontSize() / DEFAULT_FONT_SIZE);
		replaceTable
				.setRowHeight((int) (DEFAULT_TABLE_CELL_HEIGHT * fontFactor));

		replaceTable.setPreferredScrollableViewportSize(
				new Dimension((int) (DEFAULT_TABLE_WIDTH * fontFactor),
						(int) (DEFAULT_TABLE_HEIGHT * fontFactor)));
		scrollPane = new JScrollPane(replaceTable);

		captionPanel = new JPanel(new BorderLayout(5, 0));

		captionPanel.add(scrollPane, BorderLayout.CENTER);

		replaceTable.getSelectionModel()
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		replaceTable.getSelectionModel()
				.addListSelectionListener(new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						addRow(false);
					}
				});

		replaceTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED
						&& e.getKeyChar() != '\t') {
					addRow(true);
				}
			}
		});

		// buttons
		btEval = new JButton(EVAL_SYM);
		btEval.setToolTipText(loc.getMenuTooltip("Evaluate"));
		btEval.setActionCommand(ACTION_EVALUATE);
		btEval.addActionListener(this);

		btNumeric = new JButton(NUM_SYM);
		btNumeric.setToolTipText(loc.getMenuTooltip("Numeric"));
		btNumeric.setActionCommand(ACTION_NUMERIC);
		btNumeric.addActionListener(this);

		btSub = new JButton(loc.getMenu(SUB_SYM));
		btSub.setToolTipText(loc.getMenuTooltip("Substitute"));
		btSub.setActionCommand(ACTION_SUBSTITUTE);
		btSub.addActionListener(this);

		btPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		btPanel.add(btEval);
		btPanel.add(btNumeric);
		btPanel.add(btSub);

		// Create the JOptionPane.
		optionPane = new JPanel(new BorderLayout(5, 5));

		// create object list
		optionPane.add(captionPanel, BorderLayout.CENTER);

		optionPane.add(btPanel, BorderLayout.SOUTH);
		optionPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Make this dialog display it.
		dialog.setContentPane(optionPane);

	}

	/**
	 * tests if there should be an empty row appended
	 * 
	 * @param inserting
	 *            is set: the selected cell will be filled but is not yet
	 */
	public void addRow(boolean inserting) {
		int row = replaceTable.getSelectedRow();
		int col = replaceTable.getSelectedColumn();
		if (row + 1 == replaceTable.getRowCount() && col >= 0) {
			boolean[] colSet = new boolean[2];
			colSet[0] = !data.lastElement().firstElement().equals("");
			colSet[1] = !data.lastElement().lastElement().equals("");
			colSet[col] = colSet[col] || inserting;
			if (colSet[0] && colSet[1]) {
				TableCellEditor editor = replaceTable.getCellEditor();
				if (editor != null) {
					row = replaceTable.getEditingRow();
					col = replaceTable.getEditingColumn();
					data.get(row).set(col,
							editor.getCellEditorValue().toString());
				}
				data.add(new Vector<>(
						Arrays.asList(new String[] { "", "" })));
				replaceTable.revalidate();
				dialog.pack();
				Rectangle r = replaceTable.getCellRect(
						replaceTable.getRowCount() - 1, col, false);
				scrollPane.getViewport().scrollRectToVisible(r);
				if (editor != null) {
					replaceTable.editCellAt(row, col);
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();
		replaceTable.clearSelection();
		if (replaceTable.isEditing()) {
			replaceTable.getCellEditor().stopCellEditing();
		}
		if (src instanceof JComponent) {
			((JComponent) src).requestFocusInWindow();
		}
		if (src == btEval) {
			if (apply(btEval.getActionCommand())) {
				setVisible(false);
			}
		} else if (src == btSub) {
			if (apply(btSub.getActionCommand())) {
				setVisible(false);
			}
		} else if (src == btNumeric) {
			if (apply(btNumeric.getActionCommand())) {
				setVisible(false);
			}
		}
	}

	/**
	 * @param flag
	 *            true to set dialog to visible
	 */
	public void setVisible(boolean flag) {
		casView.setSubstituteDialog(flag ? this : null);
		dialog.setVisible(flag);
		if (flag) {
			// focus top right cell
			replaceTable.setRowSelectionInterval(0, 0);
			replaceTable.setColumnSelectionInterval(1, 1);
		}
	}

	/**
	 * if editing insert inStr at current caret position
	 * 
	 * @param inStr
	 *            string to insert
	 */
	public void insertText(String inStr) {
		if (inStr == null) {
			return;
		}
		TableCellEditor editor = replaceTable.getCellEditor();
		if (editor != null && editor instanceof MathTextCellEditor) {
			((MathTextCellEditor) editor).insertString(inStr);
		}
	}

	/**
	 * @return the app
	 */
	public AppD getApp() {
		return app;
	}

	private class MathTextCellEditor extends AbstractCellEditor
			implements TableCellEditor {

		private static final long serialVersionUID = 1L;
		boolean editing;
		MathTextField delegate;

		public MathTextCellEditor() {
			super();
			delegate = new MathTextField(getApp());
			editing = false;
			changeEvent = new ChangeEvent(delegate);
			delegate.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					addRow(true);
				}
			});
		}

		@Override
		public Object getCellEditorValue() {
			return delegate.getText();
		}

		@Override
		public boolean stopCellEditing() {
			if (editing) {
				fireEditingStopped();
			}
			editing = false;
			return true;
		}

		@Override
		public void cancelCellEditing() {
			if (editing) {
				fireEditingCanceled();
			}
			editing = false;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column) {
			delegate.setText(value.toString());
			delegate.setFont(getApp().getPlainFont());
			editing = true;
			return delegate;
		}

		public void insertString(String text) {
			delegate.insertString(text);
		}
	}

	/**
	 * @param flag
	 *            true to set dialog always on top
	 */
	public void setAlwaysOnTop(boolean flag) {
		dialog.setAlwaysOnTop(flag);
	}

	/**
	 * @return true if dialog is showing
	 */
	public boolean isShowing() {
		return dialog.isShowing();
	}

	@Override
	protected CASView getCASView() {
		return casView;
	}

	/**
	 * @return dialog
	 */
	public JDialog getDialog() {
		return dialog;
	}

}