package geogebra.gui.view.spreadsheet;

import geogebra.kernel.GeoButton;
import geogebra.kernel.GeoElement;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;

public class MyCellEditorButton extends AbstractCellEditor implements TableCellEditor {

	  private JButton delegate = new JButton();
	  private GeoButton editGeo;
	 // Font fontPoint = app.getPlainFont().deriveFont(Font.PLAIN, fontSize);
	  
	  public MyCellEditorButton() {
	    ActionListener actionListener = new ActionListener() {
	      public void actionPerformed(ActionEvent actionEvent) {
	    	  SwingUtilities.invokeLater( new Runnable(){ public void
	            	run() { editGeo.runScripts(null); }});

	      }
	    };
	    delegate.addActionListener(actionListener);
	  }


	  public Object getCellEditorValue() {
		  return editGeo;
	  }


	  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
	      int row, int column) {
		  editGeo = (GeoButton)value;
		  
			// show hide label by setting text
			if (editGeo.isLabelVisible()) {
				// get caption to show r
				String caption = editGeo.getCaption();
				caption = GeoElement.indicesToHTML(caption, true);				
				delegate.setText(caption);
			} else {
				delegate.setText(" ");
			}			
			
			delegate.setOpaque(true);
			delegate.setHorizontalAlignment(SwingConstants.CENTER);
		//	delegate.setFont(view.fontPoint);
			delegate.setForeground(editGeo.getObjectColor());
		  
	    return delegate;
	  }
	}


	
