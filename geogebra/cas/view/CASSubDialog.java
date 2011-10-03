package geogebra.cas.view;

import geogebra.gui.inputfield.MathTextField;
import geogebra.kernel.GeoCasCell;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;

/**
 * Dialog to substitute expressions in CAS Input.
 * 
 */

public class CASSubDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton btSub, btEval, btNumeric;
	private JScrollPane scrollPane;
	private JPanel optionPane, btPanel, captionPanel;
	private JTable replaceTable;
	private Vector<Vector<String>> data;
	
	private CASView casView;
	private Application app;
	private int editRow;
	private String prefix, evalText, postfix;
	
	private static final int DEFAULT_TABLE_CELL_HEIGHT = 21;
	private static final double DEFAULT_FONT_SIZE=12.;
	private static final int DEFAULT_TABLE_WIDTH=200;
	private static final int DEFAULT_TABLE_HEIGHT=150;

	/**
	 * Substitute dialog for CAS.
	 * @param casView
	 * @param prefix before selection, not effected by the substitution
	 * @param evalText the String which will be substituted
	 * @param postfix after selection, not effected by the substitution
	 * @param editRow
	 */
	public CASSubDialog(CASView casView, String prefix, String evalText, String postfix, int editRow) {
		super(casView.getApp().getFrame());
		setModal(false);
		
		this.casView = casView;
		this.app = casView.getApp();
		this.prefix = prefix;
		this.evalText = evalText;
		this.postfix = postfix;
		
		this.editRow = editRow;
	
		createGUI();
		pack();
		setLocationRelativeTo(casView);
	}

	/**
	 * 
	 */
	protected void createGUI() {
		setTitle(app.getPlain("Substitute") + " - " + app.getCommand("Row") + " " + (editRow+1));
		setResizable(true);
		
		GeoCasCell cell=casView.getConsoleTable().getGeoCasCell(editRow);
		
		HashSet<GeoElement> vars=cell.getInputVE().getVariables();
		Vector<String> row;
		if (vars!=null){
			data=new Vector<Vector<String>>(vars.size()+1);
			Iterator<GeoElement> iter=vars.iterator();
			while(iter.hasNext()){
				row=new Vector<String>(2);
				GeoElement var=iter.next();
				String nextVar=var.getLabel();
				int i=0;
				for (i=0;i<data.size();i++){
					if (data.get(i).firstElement().compareTo(nextVar)>=0){
						break;
					}
				}
				if (i==data.size()||!data.get(i).firstElement().equals(nextVar)){
					row.add(nextVar);
					row.add("");
					data.insertElementAt(row, i);
				}
			}
		}else{
			data=new Vector<Vector<String>>(1);
		}
		row=new Vector<String>(2);
		row.add("");
		row.add("");
		data.add(row);
		
		Vector<String> header=new Vector<String>();
		header.add(app.getPlain("OldExpression"));
		header.add(app.getPlain("NewExpression"));
		replaceTable=new JTable(data,header);
		replaceTable.setDefaultEditor(Object.class,new MathTextCellEditor());
		replaceTable.getTableHeader().setReorderingAllowed(false);
		double fontFactor=Math.max(1,app.getGUIFontSize()/DEFAULT_FONT_SIZE);
		replaceTable.setRowHeight((int) (DEFAULT_TABLE_CELL_HEIGHT*fontFactor));
		
		replaceTable.setPreferredScrollableViewportSize(
				new Dimension((int)(DEFAULT_TABLE_WIDTH*fontFactor),(int)(DEFAULT_TABLE_HEIGHT*fontFactor)));
		scrollPane=new JScrollPane(replaceTable);

		captionPanel = new JPanel(new BorderLayout(5, 0));
		
		captionPanel.add(scrollPane, BorderLayout.CENTER);
		
		
		replaceTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		replaceTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				addRow(false);
			}
		});
		
		replaceTable.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar()!=KeyEvent.CHAR_UNDEFINED&&e.getKeyChar()!='\t')
					addRow(true);
			}
		});
		

		// buttons
		btEval = new JButton("=");
		btEval.setToolTipText(app.getCommandTooltip("Evaluate"));
		btEval.setActionCommand("Evaluate");
		btEval.addActionListener(this);
		
		btNumeric = new JButton("\u2248");
		btNumeric.setToolTipText(app.getCommandTooltip("Numeric"));
		btNumeric.setActionCommand("Numeric");
		btNumeric.addActionListener(this);
		
		btSub = new JButton(app.getPlain("\u2713"));
		btSub.setToolTipText(app.getCommandTooltip("Substitute"));
		btSub.setActionCommand("Substitute");
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
		setContentPane(optionPane);
		
	}
	
	/**
	 * tests if there should be an empty row appended
	 * @param inserting is set: the selected cell will be filled but is not yet
	 */
	public void addRow(boolean inserting){
		int row=replaceTable.getSelectedRow();
		int col=replaceTable.getSelectedColumn();
		if (row+1==replaceTable.getRowCount()&&col>=0){
			boolean[] colSet=new boolean[2];
			colSet[0]=!data.lastElement().firstElement().equals("");
			colSet[1]=!data.lastElement().lastElement().equals("");
			colSet[col]=colSet[col]||inserting;
			if (colSet[0]&&colSet[1]){
				TableCellEditor editor=replaceTable.getCellEditor();
				if (editor!=null){
					row=replaceTable.getEditingRow();
					col=replaceTable.getEditingColumn();
					data.get(row).set(col, editor.getCellEditorValue().toString());
				}
				data.add(new Vector<String>(Arrays.asList(new String[]{"",""})));
				replaceTable.revalidate();
				CASSubDialog.this.pack();
				Rectangle r=replaceTable.getCellRect(replaceTable.getRowCount()-1, col, false);
				scrollPane.getViewport().scrollRectToVisible(r);
				if (editor!=null){
					replaceTable.editCellAt(row, col);
				}
			}
		}
	}

	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();
		replaceTable.clearSelection();
		if (replaceTable.isEditing())
			replaceTable.getCellEditor().stopCellEditing();
		if (src instanceof JComponent){
			((JComponent) src).requestFocusInWindow();
		}
		if (src == btEval) {
			if (apply(btEval.getActionCommand()))
				setVisible(false);
		} else if (src == btSub) {
			if (apply(btSub.getActionCommand()))
				setVisible(false);
		} else if (src == btNumeric) {
			if (apply(btNumeric.getActionCommand()))
				setVisible(false);
		}
	}
	
	public void setVisible(boolean flag) {
		casView.setSubstituteDialog(flag ? this : null);
		super.setVisible(flag);
		if (flag){
			//focus top right cell
			replaceTable.setRowSelectionInterval(0, 0);
			replaceTable.setColumnSelectionInterval(1, 1);
		}
	}

	
	/**
	 * if editing insert inStr at current caret position
	 * @param inStr 
	 */
	public void insertText(String inStr) {
		if (inStr == null) return;
		TableCellEditor editor=replaceTable.getCellEditor();
		if (editor!=null&&editor instanceof MathTextCellEditor){
			((MathTextCellEditor)editor).insertString(inStr);
		}
	}

	private boolean apply(String actionCommand) {
		
		CASTable table = casView.getConsoleTable();
		
		//create substitution list
		StringBuilder substList=new StringBuilder("{");
		StringBuilder substComment=new StringBuilder();
		for (int i=0;i<data.size();i++){
			String	fromExpr = data.get(i).get(0).trim();
			String toExpr = data.get(i).get(1).trim();
			if (!fromExpr.equals("") && !toExpr.equals("")){
				if (substList.length()>1){
					substList.append(',');
					substComment.append(',');
				}
				fromExpr=casView.resolveCASrowReferences(fromExpr, editRow);
				toExpr=casView.resolveCASrowReferences(toExpr, editRow);
				substList.append(fromExpr);
				substList.append('=');
				substList.append(toExpr);
				substComment.append(fromExpr);
				substComment.append('=');
				substComment.append(toExpr);
			}
		}
		substList.append('}');
		
		// make sure pure substitute is not evaluated 
		boolean keepInput = true;
		
		// substitute command
		String subCmd = "Substitute[" + evalText + "," + substList + "]";
		if (actionCommand.equals("Substitute")) {
			subCmd = "Substitute[" + evalText + "," + substList + "]"; 
			keepInput = false;
		}
		else if (actionCommand.equals("Numeric")) {
			subCmd = "Numeric[" + subCmd + "]";
			keepInput = false;
		}

		try {
			GeoCasCell currCell = table.getGeoCasCell(editRow);
			currCell.setProcessingInformation(prefix, subCmd, postfix);
			currCell.setEvalCommand("Substitute");
			currCell.setEvalComment(substComment.toString());
			
			// make sure pure substitute is not evaluated 
			currCell.setKeepInputUsed(keepInput);
			
			casView.processRowThenEdit(editRow, true);
			//table.startEditingRow(editRow + 1);
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private class MathTextCellEditor extends AbstractCellEditor implements TableCellEditor{

		private static final long serialVersionUID = 1L;
		boolean editing;
		MathTextField delegate;
		
		public MathTextCellEditor() {
			super();
			delegate=new MathTextField(app);
			editing=false;
			changeEvent=new ChangeEvent(delegate);
			delegate.addKeyListener(new KeyAdapter() {	
				public void keyTyped(KeyEvent e) {
					addRow(true);
				}
			});
		}

		public Object getCellEditorValue() {
			return delegate.getText();
		}


		public boolean stopCellEditing() {
			if (editing)
				fireEditingStopped();
			editing=false;
			return true;
		}

		public void cancelCellEditing() {
			if (editing)
				fireEditingCanceled();
			editing=false;
		}


		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			delegate.setText(value.toString());
			delegate.setFont(app.getPlainFont());
			editing=true;
			return delegate;
		}
		
		public void insertString(String text){
			delegate.insertString(text);
		}
		
		
		
	}

	
}