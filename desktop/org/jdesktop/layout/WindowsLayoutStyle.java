/*
 * Copyright (C) 2005-2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.layout;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * An implementation of <code>LayoutStyle</code> for the Windows look and feel.
 * This information comes from:
 * http://msdn.microsoft.com/library/default.asp?url=/library/en-us/dnwue/html/ch14e.asp
 *
 * @version $Revision: 1.8 $
 */
class WindowsLayoutStyle extends LayoutStyle {
    /**
     * Base dialog units along the horizontal axis.
     */
    private int baseUnitX;
    /**
     * Base dialog units along the vertical axis.
     */
    private int baseUnitY;


    public int getPreferredGap(JComponent source, JComponent target,
                          int type, int position, Container parent) {
        // Invoke super to check arguments.
        super.getPreferredGap(source, target, type, position, parent);

        if (type == INDENT) {
            if (position == SwingConstants.EAST || position == SwingConstants.WEST) {
                int gap = getButtonChildIndent(source, position);
                if (gap != 0) {
                    return gap;
                }
                return 10;
            }
            // Treat vertical INDENT as RELATED
            type = RELATED;
        }
        if (type == UNRELATED) {
            // Between unrelated controls: 7
            return getCBRBPadding(source, target, position,
                                  dluToPixels(7, position));
        }
        else { //type == RELATED
            boolean sourceLabel = (source.getUIClassID() == "LabelUI");
            boolean targetLabel = (target.getUIClassID() == "LabelUI");

            if (((sourceLabel && !targetLabel) ||
                 (targetLabel && !sourceLabel)) &&
                (position == SwingConstants.EAST ||
                 position == SwingConstants.WEST)) {
                // Between text labels and their associated controls (for
                // example, text boxes and list boxes): 3
                // NOTE: We're not honoring:
                // 'Text label beside a button 3 down from the top of
                // the button,' but I suspect that is an attempt to
                // enforce a baseline layout which will be handled
                // separately.  In order to enforce this we would need
                // this API to return a more complicated type (Insets,
                // or something else).
                return getCBRBPadding(source, target, position,
                                      dluToPixels(3, position));
            }
            // Between related controls: 4
            return getCBRBPadding(source, target, position,
                                  dluToPixels(4, position));
        }
    }

    public int getContainerGap(JComponent component, int position,
            Container parent) {
        super.getContainerGap(component, position, parent);
        return getCBRBPadding(component, position, dluToPixels(7, position));
    }
    
    private int dluToPixels(int dlu, int direction) {
        if (baseUnitX == 0) {
            calculateBaseUnits();
        }
        if (direction == SwingConstants.EAST ||
                         direction == SwingConstants.WEST) {
            return dlu * baseUnitX / 4;
        }
        assert (direction == SwingConstants.NORTH ||
                direction == SwingConstants.SOUTH);
        return dlu * baseUnitY / 8;
    }

    private void calculateBaseUnits() {
        // This calculation comes from:
        // http://support.microsoft.com/default.aspx?scid=kb;EN-US;125681
        FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(
                                         UIManager.getFont("Button.font"));
        baseUnitX = metrics.stringWidth(
                      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        baseUnitX = (baseUnitX / 26 + 1) / 2;
        // The -1 comes from experimentation.
        baseUnitY = metrics.getAscent() + metrics.getDescent() - 1;
    }
}
