/*
 * Copyright (C) 2005-2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.layout;

import java.awt.Container;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

/**
 * LayoutStyle is used to determine how much space to place between components
 * during layout.  LayoutStyle can be obtained for two components, or for
 * a component relative to an edge of a parent container.  The amount of
 * space can vary depending upon whether or not the components are
 * logically grouped together (<code>RELATED</code>).
 * <p>
 * This class is primarily useful for JREs prior to 1.6.  In 1.6 API for this
 * was added to Swing.  When run on a JRE of 1.6 or greater this will call into
 * the appropriate methods in Swing.
 *
 * @version $Revision: 1.11 $
 */
public class LayoutStyle {
    private static final boolean USE_CORE_LAYOUT_STYLE;
    /**
     * Possible argument to getPreferredGap.  Used to indicate the two componets
     * are grouped together.
     */
    public static final int RELATED = 0;
    /**
     * Possible argument to getPreferredGap.  Used to indicate the two componets
     * are not grouped together.
     */
    public static final int UNRELATED = 1;
    
    /**
     * Possible argument to getPreferredGap.  Used to indicate the distance
     * to indent a component is being requested.  To visually indicate
     * a set of related components they will often times be horizontally
     * indented, the <code>INDENT</code> constant for this.
     * For example, to indent a check box relative to a label use this
     * constant to <code>getPreferredGap</code>.
     */
    public static final int INDENT = 3;

    private static LayoutStyle layoutStyle;
    private static LookAndFeel laf;

    static {
        boolean useCoreLayoutStyle = false;
        try {
            Class.forName("javax.swing.LayoutStyle");
            useCoreLayoutStyle = true;
        } catch (ClassNotFoundException cnfe) {
        }
        USE_CORE_LAYOUT_STYLE = useCoreLayoutStyle;
    }
    /**
     * Sets the LayoutStyle instance to use for this look and feel.
     * You generally don't need to invoke this, instead use the getter which
     * will return the LayoutStyle appropriate for the current look and feel.
     * 
     * @param layoutStyle the LayoutStyle to use; a value of null indicates 
     *        the default should be used
     */
    public static void setSharedInstance(LayoutStyle layoutStyle) {
        UIManager.getLookAndFeelDefaults().put("LayoutStyle.instance",
                layoutStyle);
    }
    
    /**
     * Factory methods for obtaining the current <code>LayoutStyle</code>
     * object appropriate for the current look and feel.
     * 
     * @return the current LayoutStyle instance
     */
    public static LayoutStyle getSharedInstance() {
        Object layoutImpl = UIManager.get("LayoutStyle.instance");
        if (layoutImpl != null && (layoutImpl instanceof LayoutStyle)) {
            return (LayoutStyle)layoutImpl;
        }
        LookAndFeel currentLAF = UIManager.getLookAndFeel();
        if (layoutStyle == null || currentLAF != laf) {
            laf = currentLAF;
            String lafID= laf.getID();
            if (USE_CORE_LAYOUT_STYLE) {
                if ("Aqua" == lafID) {
                    try {
                        currentLAF.getClass().getDeclaredMethod("getLayoutStyle", new Class[0]);
                        layoutStyle = new SwingLayoutStyle();
                    } catch (NoSuchMethodException nsfex) {
                        // getLayoutStyle() not overriden => use our own (issue 52)
                        layoutStyle = new AquaLayoutStyle();
                    }
                } else {
                    layoutStyle = new SwingLayoutStyle();
                }
            } else if ("Metal" == lafID) {
                layoutStyle = new MetalLayoutStyle();
            }
            else if ("Windows" == lafID) {
                layoutStyle = new WindowsLayoutStyle();
            }
            else if ("GTK" == lafID) {
                layoutStyle = new GnomeLayoutStyle();
            }
            else if ("Aqua" == lafID) {
                layoutStyle = new AquaLayoutStyle();
            } else {
                layoutStyle = new LayoutStyle();
            }
        }
        return layoutStyle;
    }

    /**
     * Returns the amount of space to use between two components.
     * The return value indicates the distance to place
     * <code>component2</code> relative to <code>component1</code>.
     * For example, the following returns the amount of space to place
     * between <code>component2</code> and <code>component1</code>
     * when <code>component2</code> is placed vertically above
     * <code>component1</code>:
     * <pre>
     *   int gap = getPreferredGap(component1, component2,
     *                             LayoutStyle.RELATED,
     *                             SwingConstants.NORTH, parent);
     * </pre>
     * The <code>type</code> parameter indicates the type 
     * of gap being requested.  It can be one of the following values:
     * <table>
     * <tr><td><code>RELATED</code>
     *     <td>If the two components will be contained in
     *         the same parent and are showing similar logically related
     *         items, use <code>RELATED</code>.
     * <tr><td><code>UNRELATED</code>
     *     <td>If the two components will be
     *          contained in the same parent but show logically unrelated items
     *          use <code>UNRELATED</code>.
     * <tr><td><code>INDENT</code>
     *     <td>Used to obtain the preferred distance to indent a component
     *         relative to another.  For example, if you want to horizontally
     *         indent a JCheckBox relative to a JLabel use <code>INDENT</code>.
     *         This is only useful for the horizontal axis.
     * </table>
     * <p>
     * It's important to note that some look and feels may not distinguish
     * between <code>RELATED</code> and <code>UNRELATED</code>.
     * <p>
     * The return value is not intended to take into account the
     * current size and position of <code>component2</code> or
     * <code>component1</code>.  The return value may take into
     * consideration various properties of the components.  For
     * example, the space may vary based on font size, or the preferred
     * size of the component.
     * 
     * @param component1 the <code>JComponent</code>
     *               <code>component2</code> is being placed relative to
     * @param component2 the <code>JComponent</code> being placed
     * @param type how the two components are being placed
     * @param position the position <code>component2</code> is being placed 
     *        relative to <code>component1</code>; one of
     *        <code>SwingConstants.NORTH</code>,
     *        <code>SwingConstants.SOUTH</code>,
     *        <code>SwingConstants.EAST</code> or
     *        <code>SwingConstants.WEST</code>
     * @param parent the parent of <code>component2</code>; this may differ
     *        from the actual parent and may be null
     * @return the amount of space to place between the two components
     * @throws IllegalArgumentException if <code>position</code> is not
     *         one of <code>SwingConstants.NORTH</code>,
     *         <code>SwingConstants.SOUTH</code>,
     *         <code>SwingConstants.EAST</code> or 
     *         <code>SwingConstants.WEST</code>; <code>type</code> not one
     *         of <code>INDENT</code>, <code>RELATED</code>
     *         or <code>UNRELATED</code>; or <code>component1</code> or
     *         <code>component2</code> is null
     */
    public int getPreferredGap(JComponent component1, JComponent component2,
                          int type, int position, Container parent) {
        if (position != SwingConstants.NORTH &&
              position != SwingConstants.SOUTH &&
              position != SwingConstants.WEST &&
              position != SwingConstants.EAST) {
            throw new IllegalArgumentException("Invalid position");
        }
        if (component1 == null || component2== null) {
            throw new IllegalArgumentException("Components must be non-null");
        }
        if (type == RELATED) {
            return 6;
        } else if (type == UNRELATED) {
            return 12;
        } else if (type == INDENT) {
            if (position == SwingConstants.EAST || position == SwingConstants.WEST) {
                int gap = getButtonChildIndent(component1, position);
                if (gap != 0) {
                    return gap;
                }
                return 6;
            }
            return 6;
        }
        throw new IllegalArgumentException("Invalid type");
    }

    /**
     * Returns the amount of space to position a component inside its
     * parent.
     *
     * @param component the <code>Component</code> being positioned
     * @param position the position <code>component</code> is being placed 
     *        relative to its parent; one of
     *        <code>SwingConstants.NORTH</code>,
     *        <code>SwingConstants.SOUTH</code>,
     *        <code>SwingConstants.EAST</code> or
     *        <code>SwingConstants.WEST</code>
     * @param parent the parent of <code>component</code>; this may differ
     *        from the actual parent and may be null
     * @return the amount of space to place between the component and specified
     *         edge
     * @throws IllegalArgumentException if <code>position</code> is not
     *         one of <code>SwingConstants.NORTH</code>,
     *         <code>SwingConstants.SOUTH</code>,
     *         <code>SwingConstants.EAST</code> or 
     *         <code>SwingConstants.WEST</code>;
     *         or <code>component</code> is null
     */
    public int getContainerGap(JComponent component, int position,
            Container parent) {
        if (position != SwingConstants.NORTH &&
              position != SwingConstants.SOUTH &&
              position != SwingConstants.WEST &&
              position != SwingConstants.EAST) {
            throw new IllegalArgumentException("Invalid position");
        }
        if (component == null) {
            throw new IllegalArgumentException("Component must be non-null");
        }
        return 12;
    }

    /**
     * Returns true if <code>component</code> should be treated as a dialog.
     */
    boolean isDialog(JComponent component) {
        // PENDING: tag the content pane to make this easier to check for
        String name = component.getName();
        return (name != null && name.endsWith(".contentPane"));
    }

    /**
     * For some look and feels check boxs and radio buttons have an empty
     * border around them.  Look and feel guidelines generally don't include
     * this space.  Use this method to subtract this space from the specified
     * components.
     *
     * @param source First component
     * @param target Second component
     * @param position Position doing layout along.
     * @param offset Ideal offset, not including border/margin
     * @return offset - border/margin around the component.
     */
    int getCBRBPadding(JComponent source, JComponent target, int position,
                       int offset) {
        offset -= getCBRBPadding(source, position);
        if (offset > 0) {
            offset -= getCBRBPadding(target, flipDirection(position));
        }
        if (offset < 0) {
            return 0;
        }
        return offset;
    }

    /**
     * For some look and feels check boxs and radio buttons have an empty
     * border around them.  Look and feel guidelines generally don't include
     * this space.  Use this method to subtract this space from the specified
     * components.
     *
     * @param source Component
     * @param position Position doing layout along.
     * @param offset Ideal offset, not including border/margin
     * @return offset - border/margin around the component.
     */
    int getCBRBPadding(JComponent source, int position, int offset) {
        offset -= getCBRBPadding(source, position);
        return Math.max(offset, 0);
    }

    int flipDirection(int position) {
        switch(position) {
        case SwingConstants.NORTH:
            return SwingConstants.SOUTH;
        case SwingConstants.SOUTH:
            return SwingConstants.NORTH;
        case SwingConstants.EAST:
            return SwingConstants.WEST;
        case SwingConstants.WEST:
            return SwingConstants.EAST;
        }
        assert false;
        return 0;
    }

    private int getCBRBPadding(JComponent c, int position) {
        if (c.getUIClassID() == "CheckBoxUI" ||
              c.getUIClassID() == "RadioButtonUI") {
            Border border = c.getBorder();
            if (border instanceof UIResource) {
                return getInset(c, position);
            }
        }
        return 0;
    }

    private int getInset(JComponent c, int position) {
        Insets insets = c.getInsets();
        switch(position) {
        case SwingConstants.NORTH:
            return insets.top;
        case SwingConstants.SOUTH:
            return insets.bottom;
        case SwingConstants.EAST:
            return insets.right;
        case SwingConstants.WEST:
            return insets.left;
        }
        assert false;
        return 0;
    }

    private boolean isLeftAligned(AbstractButton button, int position) {
        if (position == SwingConstants.WEST) {
            boolean ltr = button.getComponentOrientation().isLeftToRight();
            int hAlign = button.getHorizontalAlignment();
            return ((ltr && (hAlign == SwingConstants.LEFT ||
                             hAlign == SwingConstants.LEADING)) ||
                    (!ltr && (hAlign == SwingConstants.TRAILING)));
        }
        return false;
    }

    private boolean isRightAligned(AbstractButton button, int position) {
        if (position == SwingConstants.EAST) {
            boolean ltr = button.getComponentOrientation().isLeftToRight();
            int hAlign = button.getHorizontalAlignment();
            return ((ltr && (hAlign == SwingConstants.RIGHT ||
                             hAlign == SwingConstants.TRAILING)) ||
                    (!ltr && (hAlign == SwingConstants.LEADING)));
        }
        return false;
    }

    private Icon getIcon(AbstractButton button) {
        Icon icon = button.getIcon();
        if (icon != null) {
            return icon;
        }
        String key = null;
        if (button instanceof JCheckBox) {
            key = "CheckBox.icon";
        } else if (button instanceof JRadioButton) {
            key = "RadioButton.icon";
        }
        if (key != null) {
            Object oIcon = UIManager.get(key);
            if (oIcon instanceof Icon) {
                return (Icon)oIcon;
            }
        }
        return null;
    }

    /**
     * Returns the amount to indent the specified component if it's
     * a JCheckBox or JRadioButton.  If the component is not a JCheckBox or
     * JRadioButton, 0 will be returned.
     */
    int getButtonChildIndent(JComponent c, int position) {
        if ((c instanceof JRadioButton) || (c instanceof JCheckBox)) {
            AbstractButton button = (AbstractButton)c;
            Insets insets = c.getInsets();
            Icon icon = getIcon(button);
            int gap = button.getIconTextGap();
            if (isLeftAligned(button, position)) {
                return insets.left + icon.getIconWidth() + gap;
            } else if (isRightAligned(button, position)) {
                return insets.right + icon.getIconWidth() + gap;
            }
        }
        return 0;
    }
}
