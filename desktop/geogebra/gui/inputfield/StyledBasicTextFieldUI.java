package geogebra.gui.inputfield;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;

import com.apple.laf.AquaTextFieldUI;



/**
 * @author G.Sturr
 *
 */
public class StyledBasicTextFieldUI extends BasicTextFieldUI {
    
    @Override
    public EditorKit getEditorKit(JTextComponent tc) {
        if (tc.getClientProperty("editorKit") instanceof EditorKit) {
            return (EditorKit) tc.getClientProperty("editorKit");
        }
        return super.getEditorKit(tc);
    }

    public static ComponentUI createUI(JComponent c) {
        return new StyledBasicTextFieldUI();
    }
}




