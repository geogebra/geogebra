package geogebra.javax.swing;

import geogebra.common.gui.util.RelationMore;
import geogebra.common.javax.swing.RelationPane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * Desktop implementation of the Relation Tool information window.
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 *
 */
public class RelationPaneD implements RelationPane {

	private JFrame frame;
	private String[] columnNames;
	private Object[][] data;
	private DefaultTableModel model;
	private JTable table;
	/**
	 * This stores the array of the actions to be fired when
	 * click on "More...".
	 */
	RelationMore[] callbacks;
	private boolean areCallbacks = false;
	private int morewidth = 0;
	
	private final int INFOWIDTH = 400;
	private final int ROWHEIGHT = 20;
	private final int MARGIN = 0;
	
	private final int MOREWIDTH = 100;
		
	public void showDialog(String title, RelationRow[] relations) {

		frame = new JFrame(title);
		
		int rels = relations.length;
		
		for (int i=0; i<rels; ++i) {
			if (relations[i].callback != null) {
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
		
		for (int i=0; i<rels; ++i) {
			data[i][0] = relations[i].info;
			callbacks[i] = relations[i].callback;
			if (areCallbacks) {
				if (relations[i].callback != null) {
					data[i][1] = "More...";
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
			table.getColumnModel().getColumn(1)
					.setCellEditor(new ClientsTableRenderer(this, new JCheckBox()));
		}

		for (int i=0; i<rels; ++i) {
			int thisHeight = ROWHEIGHT * (countLines(relations[i].info));
			table.setRowHeight(i, thisHeight);
			height += thisHeight;
		}
		
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setDragEnabled(false);
		table.setPreferredSize(new Dimension(INFOWIDTH + morewidth, height));
		frame.add(table);
        frame.setPreferredSize(new Dimension(INFOWIDTH + morewidth + 2 * MARGIN, height + 2 * MARGIN));
        frame.setSize(frame.getPreferredSize());       
        table.getColumnModel().getColumn(0).setPreferredWidth(INFOWIDTH);
        if (areCallbacks) {
        	table.getColumnModel().getColumn(1).setPreferredWidth(MOREWIDTH);
        }
		
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private static int countLines(String html) {
		int ret = 1;
		String[] words = {"<br>", "<li>", "<ul>"};
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
		return ret;
	}
	
	public void updateRow(int row, RelationRow relation) {
		table.setValueAt(relation.info, row, 0);
		callbacks[row] = relation.callback;
		table.setRowHeight(row, ROWHEIGHT * (countLines(relation.info)));

		int height = 0;
		
		areCallbacks = false;
		for (int i=0; i<callbacks.length; ++i) {
			height += table.getRowHeight(i);
			if (callbacks[i] != null) {
				areCallbacks = true;
			}
		}
		
		if (!areCallbacks) {
			morewidth = 0;
			table.removeColumn(table.getColumnModel().getColumn(1));
		}

		table.setPreferredSize(new Dimension(INFOWIDTH+morewidth,height));
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		frame.setPreferredSize(new Dimension(INFOWIDTH+morewidth+2*MARGIN,height+2*MARGIN));
		frame.pack();
		}	

	/**
	 * This code is mostly copied from
	 * http://stackoverflow.com/a/10348919 shared by "Bitmap".
	 */
	private class ClientsTableButtonRenderer extends JButton implements
			TableCellRenderer {
		
		private static final long serialVersionUID = 5188521324132632032L;

		public ClientsTableButtonRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable t,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			setForeground(Color.black);
			setBackground(UIManager.getColor("Button.background"));
			setText((value == null) ? "" : value.toString());
			if (callbacks[row] == null) {
				return null;
			}
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
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable t,
				Object value, boolean isSelected, int r, int column) {
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
			return new String(label);
		}

		@Override
		public boolean stopCellEditing() {
			clicked = false;
			return super.stopCellEditing();
		}

		@Override
		protected void fireEditingStopped() {
			/*
			 *  FIXME: In some cases this throw an exception.
			 *  No idea how to fix it.
			 */
			super.fireEditingStopped();
		}
	}	
}