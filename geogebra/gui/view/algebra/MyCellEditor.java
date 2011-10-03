/**
 * 
 */
package geogebra.gui.view.algebra;

import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

//this is needed to distinguish between the editing
// of independent and dependent objects
public class MyCellEditor extends DefaultCellEditor {  
    
    private static final long serialVersionUID = 1L;
    private Application app;
    
    public MyCellEditor(final JTextField textField, Application app) {
        super(textField);           
        this.app = app;
    }
    
    /** Implements the <code>TreeCellEditor</code> interface. */
    public Component getTreeCellEditorComponent(JTree tree, Object value,
                        boolean isSelected,
                        boolean expanded,
                        boolean leaf, int row) {
            
        String str = null;      
        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object ob = node.getUserObject();
            if (ob instanceof GeoElement) {
                GeoElement geo = (GeoElement) ob;
                
                if (geo.isChangeable()) {
                    str = geo.toString();
                } else {
                    str = geo.getCommandDescription();
                } 
            }
        }
    
        String stringValue;
        if (str == null) {              
            stringValue = (value == null) ? "" : value.toString();
        } else {
            stringValue = str;
        }           
        delegate.setValue(stringValue);
        
		// make sure we use a font that can display the text
        editorComponent.setFont(app.getFontCanDisplay(str));
        return editorComponent;
    }
}


