package org.geogebra.desktop.javax.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.geogebra.common.gui.util.RelationMore;
import org.geogebra.common.javax.swing.RelationPane;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Desktop implementation of the Relation Tool information window.
 * 
 * @author Zoltan "of Swing" Kovacs
 * 
 */
public class RelationPaneD implements RelationPane, ActionListener {

	/**
	 * The Relation window.
	 */
	JFrame frame;
	private String[] columnNames;
	private Object[][] data;
	private DefaultTableModel model;
	/**
	 * The contents of the Relation window.
	 */
	JTable table;
	/**
	 * This stores the array of the actions to be fired when click on "More...".
	 */
	RelationMore[] callbacks;
	private boolean areCallbacks = false;
	private int morewidth = 0;

	private final static int ORIG_INFOWIDTH = 300;
	private int INFOWIDTH;
	/**
	 * Current row height computed by the window size (y), by default it uses
	 * ORIG_ROWHEIGHT.
	 */
	double ROWHEIGHT;
	private final static int ORIG_ROWHEIGHT = 30;
	private final static int MARGIN = 10;
	private final static int ROWMARGIN = 1;

	private final static int ORIG_MOREWIDTH = 140;
	private int MOREWIDTH;

	private final static int ORIG_OKHEIGHT = 30;
	private int OKHEIGHT;
	private final static int ORIG_OKWIDTH = 140;
	private int OKWIDTH;

	@Override
	public void showDialog(String title, final RelationRow[] relations,
			App app) {

		frame = new JFrame(title);

		JPanel panel = new JPanel(new BorderLayout(MARGIN, MARGIN));

		ROWHEIGHT = ((double) ORIG_ROWHEIGHT) * app.getFontSize() / 12;
		INFOWIDTH = (ORIG_INFOWIDTH * app.getFontSize() / 12);
		MOREWIDTH = (ORIG_MOREWIDTH * app.getFontSize() / 12);
		OKHEIGHT = (ORIG_OKHEIGHT * app.getFontSize() / 12);
		OKWIDTH = (ORIG_OKWIDTH * app.getFontSize() / 12);

		final int rels = relations.length;

		for (int i = 0; i < rels; ++i) {
			if (relations[i].getCallback() != null) {
				areCallbacks = true;
				morewidth = MOREWIDTH;
			}
		}
		if (areCallbacks) {
			columnNames = new String[] { "String", "" };
			data = new Object[rels][2];
		} else {
			columnNames = new String[] { "String" };
			data = new Object[rels][1];
		}

		callbacks = new RelationMore[rels];
		int height = 0;

		for (int i = 0; i < rels; ++i) {
			data[i][0] = relations[i].getInfo();
			callbacks[i] = relations[i].getCallback();
			if (areCallbacks) {
				if (relations[i].getCallback() != null) {
					data[i][1] = app.getLocalization().getMenu("More")
							+ Unicode.ELLIPSIS;
				} else {
					data[i][1] = "";
				}
			}
		}

		model = new DefaultTableModel(data, columnNames) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return (column == 1) && (callbacks[row] != null);
			}
		};

		table = new JTable(model);
		table.setTableHeader(null);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (areCallbacks) {
			table.getColumnModel().getColumn(1)
					.setCellRenderer(new ClientsTableButtonRenderer());
			table.getColumnModel().getColumn(1).setCellEditor(
					new ClientsTableRenderer(this, new JCheckBox()));
		}
		table.getColumnModel().getColumn(0)
				.setCellRenderer(new ClientsTableTextRenderer());
		table.setBackground(UIManager.getColor("Label.background"));

		for (int i = 0; i < rels; ++i) {
			int thisHeight = (int) (ROWHEIGHT
					* (countLines(relations[i].getInfo())));
			table.setRowHeight(i, thisHeight - 2 * (ROWMARGIN + 1)); // button
																		// border
			height += thisHeight;
		}

		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setDragEnabled(false);
		table.setSize(INFOWIDTH + morewidth, height);
		table.setRowMargin(ROWMARGIN);
		panel.add(table);

		// Adding OK button:
		JPanel buttonrow = new JPanel(new FlowLayout());
		JButton ok = new JButton(app.getLocalization().getMenu("OK"));
		buttonrow.add(ok);
		ok.setSize(OKWIDTH, OKHEIGHT);
		panel.add(buttonrow, BorderLayout.SOUTH);
		ok.addActionListener(this);

		panel.setSize(INFOWIDTH + morewidth + 2 * MARGIN,
				height + 3 * MARGIN + OKHEIGHT);
		panel.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN,
				MARGIN));

		panel.setBackground(UIManager.getColor("Label.background"));
		frame.add(panel);
		frame.setSize(INFOWIDTH + morewidth + 2 * MARGIN,
				height + 3 * MARGIN + OKHEIGHT);

		table.getColumnModel().getColumn(0).setPreferredWidth(INFOWIDTH);
		if (areCallbacks) {
			table.getColumnModel().getColumn(1).setPreferredWidth(MOREWIDTH);
		}

		frame.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent evt) {
				// Extremely ugly way to learn if this event comes from resizing
				// or refresh.
				// TODO: Find a better way.
				String event = Thread.currentThread().getStackTrace()[3]
						.getMethodName();
				if ("processEvent".equals(event)) {
					return;
				}
				// Log.debug(event);

				int ysize = frame.getContentPane().getHeight() - 3 * MARGIN
						- OKHEIGHT;
				int r = relations.length;
				int currentHeight = 0;
				for (int i = 0; i < r; ++i) {
					int thisHeight = (ORIG_ROWHEIGHT
							* (countLines(table.getValueAt(i, 0).toString())));
					currentHeight += thisHeight;
				}
				ROWHEIGHT = ((double) ysize) / currentHeight * ORIG_ROWHEIGHT;
				// Log.debug("resized to rh " + ROWHEIGHT);
				for (int i = 0; i < r; ++i) {
					int newHeight = (int) (ROWHEIGHT
							* (countLines(table.getValueAt(i, 0).toString())));
					table.setRowHeight(i, newHeight - 2 * (ROWMARGIN + 1));
				}
			}

			@Override
			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub
			}
		});

		frame.pack();
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private static int countLines(String html) {
		int ret = 1;
		String[] words = { "<br>", "<li", "<ul>" };
		for (String word : words) {
			int index = html.indexOf(word);
			if (index != -1) {
				ret++;
			}
			while (index >= 0) {
				index = html.indexOf(word, index + word.length() - 1);
				if (index != -1) {
					ret++;
				}
			}
		}
		// Log.debug("# " + ret + html);
		return ret;
	}

	@Override
	public synchronized void updateRow(int row, RelationRow relation) {
		table.setValueAt(relation.getInfo(), row, 0);
		callbacks[row] = relation.getCallback();
		table.setRowHeight(row, (int) (ROWHEIGHT * (countLines(relation.getInfo()))
				- 2 * (ROWMARGIN + 1)));
		int height = 0;

		areCallbacks = false;
		for (int i = 0; i < callbacks.length; ++i) {
			height += table.getRowHeight(i);
			if (callbacks[i] != null) {
				areCallbacks = true;
			}
		}

		if (!areCallbacks) {
			morewidth = 0;
			if (table.getColumnModel().getColumnCount() > 1) {
				table.removeColumn(table.getColumnModel().getColumn(1));
			}
		}

		table.setSize(INFOWIDTH + morewidth, height);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		frame.setSize(INFOWIDTH + morewidth + 2 * MARGIN,
				height + 3 * MARGIN + OKHEIGHT);
		frame.pack();
		frame.paint(frame.getGraphics());
	}

	/**
	 * This code is mostly copied from http://stackoverflow.com/a/10348919
	 * shared by "Bitmap". Button column settings.
	 */
	private class ClientsTableButtonRenderer extends JButton
			implements TableCellRenderer {

		private static final long serialVersionUID = 5188521324132632032L;

		public ClientsTableButtonRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			setForeground(Color.black);
			setBackground(UIManager.getColor("Label.background"));
			setOpaque(true);
			setText((value == null) ? "" : value.toString());
			if (callbacks[row] == null) {
				return null;
			}
			return this;
		}
	}

	/* Text column settings. */
	private static class ClientsTableTextRenderer extends JLabel
			implements TableCellRenderer {

		private static final long serialVersionUID = 5188521324132632032L;

		public ClientsTableTextRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			setForeground(Color.black);
			setBackground(UIManager.getColor("Label.background"));
			setHorizontalAlignment(CENTER);
			setOpaque(true);
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	private class ClientsTableRenderer extends DefaultCellEditor {
		private static final long serialVersionUID = -4426618730428867967L;
		private JButton button;
		private String label;
		private boolean clicked;
		private int row, col;
		private RelationPane pane;

		public ClientsTableRenderer(RelationPane p, JCheckBox checkBox) {
			super(checkBox);
			pane = p;
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable t, Object value,
				boolean isSelected, int r, int column) {
			this.row = r;
			this.col = column;

			button.setForeground(Color.black);
			button.setBackground(UIManager.getColor("Button.background"));

			label = (value == null) ? "" : value.toString();
			button.setText(label);
			clicked = true;
			return button;
		}

		@Override
		public Object getCellEditorValue() {
			if (clicked) {
				callbacks[row].action(pane, this.row);
			}
			if ((col == 1) && callbacks[row] == null) {
				label = "";
			}
			clicked = false;
			return label;
		}

		@Override
		public boolean stopCellEditing() {
			clicked = false;
			return super.stopCellEditing();
		}

		@Override
		protected void fireEditingStopped() {
			/*
			 * FIXME: In some cases this throws an exception. No idea how to fix
			 * it.
			 */
			try {
				super.fireEditingStopped();
			} catch (Exception e) {
				Log.error("Swing error in RelationPaneD");
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		frame.dispose();
	}
}