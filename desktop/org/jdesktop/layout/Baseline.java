/*
 * Copyright (C) 2005-2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.layout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;

/**
 * Convenience class that can be used to determine the baseline of a
 * particular component.  The static method <code>getBaseline</code> uses the
 * following algorithm to determine the baseline:
 * <ol>
 * <li>If the component has a <code>getBaseline(JComponent,int,int)</code> 
 *     method, invoke it.
 * <li>If there is a <code>UIManager</code> property of the name
 *     <code>Baseline.instance</code>, forward the call to that Baseline.
 * <li>Otherwise use the built in support.
 * </ol>
 * <p>
 * In addition to determining the baseline, this class also allows for
 * determining how the baseline changes as the size of the component changes.
 * The method getBaselineResizeBehavior can be used for this. This will return
 * one of BRB_OTHER, BRB_CONSTANT_ASCENT, BRB_CONSTANT_DESCENT or
 * BRB_CENTER_OFFSET. The following algorithm is used in determining the
 * baseline resize behavior.
 * <ol>
 * <li>If the Component defines the method
 *     getBaselineResizeBehaviorInt, the return value from that method is used.
 * <li>If running on 1.6, the Component method getBaselineResizeBehavior is
 *     invoked and the return value converted to one of the constants defined
 *     by this class.
 * <li>If the component is one of the known Swing components,the baseline resize
 *     behavior is calculated and returned.
 * <li>Otherwise, BRB_OTHER is returned.
 * </ol>
 * <p>
 * This class is primarily useful for JREs prior to 1.6.  In 1.6 API for this
 * was added directly to Component. When run on 1.6 or newer, this class calls
 * into the appropriate Component methods.
 *
 * @version $Revision: 1.13 $
 */
public class Baseline {
    static final int BRB_NONE = 0;
    /**
     * Baseline resize behavior constant. Indicates as the size of the component
     * changes the baseline remains a fixed distance from the top of the
     * component.
     */
    public static final int BRB_CONSTANT_ASCENT = 1;

    /**
     * Baseline resize behavior constant. Indicates as the size of the component
     * changes the baseline remains a fixed distance from the bottom of the 
     * component.
     */
    public static final int BRB_CONSTANT_DESCENT = 2;

    /**
     * Baseline resize behavior constant. Indicates as the size of the component
     * changes the baseline remains a fixed distance from the center of the
     * component.
     */
    public static final int BRB_CENTER_OFFSET = 3;

    /**
     * Baseline resize behavior constant. Indicates as the size of the component
     * changes the baseline can not be determined using one of the other
     * constants.
     */
    public static final int BRB_OTHER = 4;
    
    //
    // Used by button and label baseline code, cached to avoid excessive
    // garbage.
    //
    private static final Rectangle viewRect = new Rectangle();
    private static final Rectangle textRect = new Rectangle();
    private static final Rectangle iconRect = new Rectangle();

    // 
    // These come from TitleBorder.  NOTE that these are NOT final in
    // TitledBorder
    //
    private static final int EDGE_SPACING = 2;
    private static final int TEXT_SPACING = 2;


    private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

    // Prototype label for calculating baseline of tables.
    private static JLabel TABLE_LABEL;

    // Prototype label for calculating baseline of lists.
    private static JLabel LIST_LABEL;

    // Prototype label for calculating baseline of trees.
    private static JLabel TREE_LABEL;

    // Corresponds to com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel
    private static Class CLASSIC_WINDOWS;
    // Whether or not we've tried to load WindowsClassicLookAndFeel.
    private static boolean checkedForClassic;
    
    // Corresponds to com.sun.java.swing.plaf.windows.WindowsLookAndFeel
    private static Class WINDOWS_CLASS;
    // Whether we've tried to load WindowsLookAndFeel
    private static boolean checkedForWindows;
    
    // Whether or not we are running in a sandbox. This is used to determine
    // how we should decide if we're using ocean.
    private static boolean inSandbox;
    // If in the sandbox, this is set after we've determine if using ocean.
    private static boolean checkedForOcean;
    // Whether or not using ocean. This is only used if inSandbox.
    private static boolean usingOcean;

    // Map<Class,Method> 
    private static final Map BASELINE_MAP = Collections.
            synchronizedMap(new HashMap(1));

    // Map<Class,Method> Method is getBaselineResizeBehaviorAsInt
    private static final Map BRB_I_MAP = Collections.
            synchronizedMap(new HashMap(1));
    
    private static final Method COMPONENT_BASELINE_METHOD;
    private static final Method COMPONENT_BRB_METHOD;
    private static final Object ENUM_BRB_CENTER_OFFSET;
    private static final Object ENUM_BRB_CONSTANT_ASCENT;
    private static final Object ENUM_BRB_CONSTANT_DESCENT;
    private static final Object ENUM_BRB_OTHER;

    // Temporary JList: used to determine baseline resize behavior for
    // comboboxs.
    private static JList brbList;
    // Temporary ListCellRenderer: used to determine baseline resize behavior
    // for comboboxs.
    private static ListCellRenderer brbListCellRenderer;
    
    static {
        Method componentBaselineMethod = null;
        Method componentBRBMethod = null;
        Method componentBRBIMethod = null;
        Object brbCenterOffset = null;
        Object brbConstantAscent = null;
        Object brbConstantDescent = null;
        Object brbOther = null;
        try {
            componentBaselineMethod = Component.class.getMethod(
                "getBaseline", new Class[] { int.class, int.class});
            componentBRBMethod = Component.class.getMethod(
                "getBaselineResizeBehavior", new Class[] { });
            Class brbClass = Class.forName("java.awt.Component$BaselineResizeBehavior");
            brbCenterOffset = getFieldValue(brbClass, "CENTER_OFFSET");
            brbConstantAscent = getFieldValue(brbClass, "CONSTANT_ASCENT");
            brbConstantDescent = getFieldValue(brbClass, "CONSTANT_DESCENT");
            brbOther = getFieldValue(brbClass, "OTHER");
        } catch (NoSuchMethodException nsme) {
        } catch (ClassNotFoundException cnfe) {
        } catch (NoSuchFieldException nsfe) {
        } catch (IllegalAccessException iae) {
        }
        if (componentBaselineMethod == null ||
                componentBRBMethod == null ||
                brbCenterOffset == null ||
                brbConstantDescent == null ||
                brbConstantAscent == null ||
                brbOther == null) {
            componentBaselineMethod = componentBRBMethod = null;
            brbCenterOffset = brbConstantAscent = brbConstantDescent =
                    brbOther = null;
        }
        COMPONENT_BASELINE_METHOD = componentBaselineMethod;
        COMPONENT_BRB_METHOD = componentBRBMethod;
        ENUM_BRB_CENTER_OFFSET = brbCenterOffset;
        ENUM_BRB_CONSTANT_ASCENT = brbConstantAscent;
        ENUM_BRB_CONSTANT_DESCENT = brbConstantDescent;
        ENUM_BRB_OTHER = brbOther;
    }
    
    private static Object getFieldValue(Class type, String name) throws IllegalAccessException, NoSuchFieldException {
        return type.getField(name).get(null);
    }

    static int getBaselineResizeBehavior(Component c) {
        if (c instanceof JComponent) {
            return getBaselineResizeBehavior((JComponent)c);
        }
        return BRB_OTHER;
    }
    
    /**
     * Returns a constant indicating how the baseline varies with the size
     * of the component.
     *
     * @param c the JComponent to get the baseline resize behavior for
     * @return one of BRB_CONSTANT_ASCENT, BRB_CONSTANT_DESCENT,
     *         BRB_CENTER_OFFSET or BRB_OTHER
     */
    public static int getBaselineResizeBehavior(JComponent c) {
        Method brbIMethod = getBRBIMethod(c);
        if (brbIMethod != null) {
            return invokeBRBIMethod(brbIMethod, c);
        }
        if (COMPONENT_BRB_METHOD != null) {
            return getBaselineResizeBehaviorUsingMustang(c);
        }
        String uid = c.getUIClassID();
        if (uid == "ButtonUI" || uid == "CheckBoxUI" ||
                uid == "RadioButtonUI" || uid == "ToggleButtonUI") {
            return getButtonBaselineResizeBehavior((AbstractButton)c);
        }
        else if (uid == "ComboBoxUI") {
            return getComboBoxBaselineResizeBehavior((JComboBox)c);
        }
        else if (uid == "TextAreaUI") {
            return getTextAreaBaselineResizeBehavior((JTextArea)c);
        }
        else if (uid == "TextFieldUI" ||
                uid == "FormattedTextFieldUI" ||
                uid == "PasswordFieldUI") {
            return getSingleLineTextBaselineResizeBehavior((JTextField)c);
        }
        else if (uid == "LabelUI") {
            return getLabelBaselineResizeBehavior((JLabel)c);
        }
        else if (uid == "ListUI") {
            return getListBaselineResizeBehavior((JList)c);
        }
        else if (uid == "PanelUI") {
            return getPanelBaselineResizeBehavior((JPanel)c);
        }
        else if (uid == "ProgressBarUI") {
            return getProgressBarBaselineResizeBehavior((JProgressBar)c);
        }
        else if (uid == "SliderUI") {
            return getSliderBaselineResizeBehavior((JSlider)c);
        }
        else if (uid == "SpinnerUI") {
            return getSpinnerBaselineResizeBehavior((JSpinner)c);
        }
        else if (uid == "ScrollPaneUI") {
            return getScrollPaneBaselineBaselineResizeBehavior((JScrollPane)c);
        }
        else if (uid == "TabbedPaneUI") {
            return getTabbedPaneBaselineResizeBehavior((JTabbedPane)c);
        }
        else if (uid == "TableUI") {
            return getTableBaselineResizeBehavior((JTable)c);
        }
        else if (uid == "TreeUI") {
            return getTreeBaselineResizeBehavior((JTree)c);
        }
        return BRB_OTHER;
    }
    
    private static int getBaselineResizeBehaviorUsingMustang(JComponent c) {
        try {
            Object result = COMPONENT_BRB_METHOD.invoke(c, null);
            if (result == ENUM_BRB_CENTER_OFFSET) {
                return BRB_CENTER_OFFSET;
            } else if (result == ENUM_BRB_CONSTANT_ASCENT) {
                return BRB_CONSTANT_ASCENT;
            } else if (result == ENUM_BRB_CONSTANT_DESCENT) {
                return BRB_CONSTANT_DESCENT;
            }
        } catch (IllegalAccessException iae) {
            assert false;
        } catch (IllegalArgumentException iae2) {
            assert false;
        } catch (InvocationTargetException ite) {
            assert false;
        }
        return BRB_OTHER;
    }

    private static Method getBRBIMethod(Component component) {
        Class klass = component.getClass();
        while (klass != null) {
            if (BRB_I_MAP.containsKey(klass)) {
                Method method = (Method)BRB_I_MAP.get(klass);
                return method;
            }
            klass = klass.getSuperclass();
        }
        klass = component.getClass();
        Method[] methods = klass.getMethods();
        for (int i = methods.length - 1; i >= 0; i--) {
            Method method = methods[i];
            if ("getBaselineResizeBehaviorInt".equals(method.getName())) {
                Class[] params = method.getParameterTypes();
                if (params.length == 0) {
                    BRB_I_MAP.put(klass, method);
                    return method;
                }
            }
        }
        BRB_I_MAP.put(klass, null);
        return null;
    }

    private static int invokeBRBIMethod(Method method, Component c) {
        int brb = BRB_OTHER;
        try {
            brb = ((Integer)method.invoke(c, null)).intValue();
        } catch (IllegalAccessException iae) {
        } catch (IllegalArgumentException iae2) {
        } catch (InvocationTargetException ite2) {
        }
        return brb;
    }
    
    private static int getTreeBaselineResizeBehavior(JTree tree) {
        return BRB_CONSTANT_ASCENT;
    }
    
    private static int getSingleLineTextBaselineResizeBehavior(JTextField tf) {
        return BRB_CENTER_OFFSET;
    }
    
    private static int getTextAreaBaselineResizeBehavior(JTextArea ta) {
        return BRB_CONSTANT_ASCENT;
    }
    
    private static int getTableBaselineResizeBehavior(JTable table) {
        return BRB_CONSTANT_ASCENT;
    }
    
    private static int getTabbedPaneBaselineResizeBehavior(JTabbedPane tp) {
        switch(tp.getTabPlacement()) {
        case JTabbedPane.LEFT:
        case JTabbedPane.RIGHT:
        case JTabbedPane.TOP:
            return BRB_CONSTANT_ASCENT;
        case JTabbedPane.BOTTOM:
            return BRB_CONSTANT_DESCENT;
        }
        return BRB_OTHER;
    }
    
    private static int getSpinnerBaselineResizeBehavior(JSpinner spinner) {
        return getBaselineResizeBehavior(spinner.getEditor());
    }
    
    private static int getSliderBaselineResizeBehavior(JSlider slider) {
        return BRB_OTHER;
    }
    
    private static int getScrollPaneBaselineBaselineResizeBehavior(JScrollPane sp) {
        return BRB_CONSTANT_ASCENT;
    }
    
    private static int getProgressBarBaselineResizeBehavior(JProgressBar pb) {
        if (pb.isStringPainted() &&
                pb.getOrientation() == JProgressBar.HORIZONTAL) {
            return BRB_CENTER_OFFSET;
        }
        return BRB_OTHER;
    }
    
    private static int getPanelBaselineResizeBehavior(JPanel panel) {
        Border b = panel.getBorder();
        if (b instanceof TitledBorder) {
            switch(((TitledBorder)b).getTitlePosition()) {
                case TitledBorder.ABOVE_TOP:
                case TitledBorder.TOP:
                case TitledBorder.DEFAULT_POSITION:
                case TitledBorder.BELOW_TOP:
                    return BRB_CONSTANT_ASCENT;
                case TitledBorder.ABOVE_BOTTOM:
                case TitledBorder.BOTTOM:
                case TitledBorder.BELOW_BOTTOM:
                    return BRB_CONSTANT_DESCENT;
            }
        }
        return BRB_OTHER;
    }
    
    private static int getListBaselineResizeBehavior(JList list) {
        return BRB_CONSTANT_ASCENT;
    }
    
    private static int getLabelBaselineResizeBehavior(JLabel label) {
        if (label.getClientProperty("html") != null) {
            return BRB_OTHER;
        }
        switch(label.getVerticalAlignment()) {
        case JLabel.TOP:
            return BRB_CONSTANT_ASCENT;
        case JLabel.BOTTOM:
            return BRB_CONSTANT_DESCENT;
        case JLabel.CENTER:
            return BRB_CENTER_OFFSET;
        }
        return BRB_OTHER;
    }
    
    private static int getButtonBaselineResizeBehavior(AbstractButton button) {
        if (button.getClientProperty("html") != null) {
            return BRB_OTHER;
        }
        switch(button.getVerticalAlignment()) {
        case AbstractButton.TOP:
            return BRB_CONSTANT_ASCENT;
        case AbstractButton.BOTTOM:
            return BRB_CONSTANT_DESCENT;
        case AbstractButton.CENTER:
            return BRB_CENTER_OFFSET;
        }
        return BRB_OTHER;
    }
    
    private static int getComboBoxBaselineResizeBehavior(JComboBox cb) {
        if (cb.isEditable()) {
            return getBaselineResizeBehavior(cb.getEditor().getEditorComponent());
        }
        ListCellRenderer renderer = cb.getRenderer();
        if (renderer == null) {
            if (brbListCellRenderer == null) {
                brbListCellRenderer = new DefaultListCellRenderer();
            }
            renderer = brbListCellRenderer;
        }
        Object value = null;
        Object prototypeValue = cb.getPrototypeDisplayValue();
        if (prototypeValue != null)  {
            value = prototypeValue;
        } else if (cb.getModel().getSize() > 0) {
            value = cb.getModel().getElementAt(0);
        }
        if (value != null) {
            if (brbList == null) {
                brbList = new JList();
            }
            Component component = renderer.
                    getListCellRendererComponent(brbList, value, -1,
                    false, false);
            return getBaselineResizeBehavior(component);
        }
        return BRB_OTHER;
    }

    /**
     * Returns the baseline for the specified component, or -1 if the
     * baseline can not be determined.  The baseline is measured from
     * the top of the component.  This method returns the baseline based
     * on the preferred size.
     *
     * @param component JComponent to calculate baseline for
     * @return baseline for the specified component
     */
    public static int getBaseline(JComponent component) {
        Dimension pref = component.getPreferredSize();
        return getBaseline(component, pref.width, pref.height);
    }
    
    private static Method getBaselineMethod(JComponent component) {
        if (COMPONENT_BASELINE_METHOD != null) {
            return COMPONENT_BASELINE_METHOD;
        }
        Class klass = component.getClass();
        while (klass != null) {
            if (BASELINE_MAP.containsKey(klass)) {
                Method method = (Method)BASELINE_MAP.get(klass);
                if (method != null || klass == component.getClass()) {
                    return method;
                }
                break;
            }
            klass = klass.getSuperclass();
        }
        klass = component.getClass();
        Method[] methods = klass.getMethods();
        for (int i = methods.length - 1; i >= 0; i--) {
            Method method = methods[i];
            if ("getBaseline".equals(method.getName())) {
                Class[] params = method.getParameterTypes();
                if (params.length == 2 && params[0] == int.class &&
                        params[1] == int.class) {
                    BASELINE_MAP.put(klass, method);
                    return method;
                }
            }
        }
        BASELINE_MAP.put(klass, null);
        return null;
    }

    private static int invokeBaseline(Method method, JComponent c, int width,
            int height) {
        int baseline = -1;
        try {
            baseline = ((Integer)method.invoke(c,
                    new Object[] { new Integer(width),
                            new Integer(height) })).intValue();
        } catch (IllegalAccessException iae) {
        } catch (IllegalArgumentException iae2) {
        } catch (InvocationTargetException ite2) {
        }
        return baseline;
    }
    
    private static boolean isKnownLookAndFeel() {
        LookAndFeel laf = UIManager.getLookAndFeel();
        String lookAndFeelID = laf.getID();
        return (lookAndFeelID == "GTK" || lookAndFeelID == "Aqua" ||
                isMetal(laf) || isWindows(laf));
    }
    
    /**
     * Returns the baseline for the specified component, or a value less 
     * than 0 if the baseline can not be determined.  The baseline is measured 
     * from the top of the component.
     *
     * @param component JComponent to calculate baseline for
     * @param width Width of the component to determine baseline for.
     * @param height Height of the component to determine baseline for.
     * @return baseline for the specified component
     */
    public static int getBaseline(JComponent component, int width, int height) {
        Method baselineMethod = getBaselineMethod(component);
        if (baselineMethod != null) {
            return invokeBaseline(baselineMethod, component, width, height);
        }
        Object baselineImpl = UIManager.get("Baseline.instance");
        if (baselineImpl != null && (baselineImpl instanceof Baseline)) {
            return ((Baseline)baselineImpl).getComponentBaseline(
                    component, width, height);
        }
        if (!isKnownLookAndFeel()) {
            return -1;
        }
        String uid = component.getUIClassID();
        int baseline = -1;
        if (uid == "ButtonUI" || uid == "CheckBoxUI" ||
                uid == "RadioButtonUI" || uid == "ToggleButtonUI") {
            baseline = getButtonBaseline((AbstractButton)component,
                                         height);
        }
        else if (uid == "ComboBoxUI") {
            return getComboBoxBaseline((JComboBox)component,
                                       height);
        }
        else if (uid == "TextAreaUI") {
            return getTextAreaBaseline((JTextArea)component, height);
        }
        else if (uid == "FormattedTextFieldUI" ||
                 uid == "PasswordFieldUI" ||
                 uid == "TextFieldUI") {
            baseline = getSingleLineTextBaseline((JTextComponent)component,
                                                 height);
        }
        else if (uid == "LabelUI") {
            baseline = getLabelBaseline((JLabel)component, height);
        }
        else if (uid == "ListUI") {
            baseline = getListBaseline((JList)component, height);
        }
        else if (uid == "PanelUI") {
            baseline = getPanelBaseline((JPanel)component, height);
        }
        else if (uid == "ProgressBarUI") {
            baseline = getProgressBarBaseline((JProgressBar)component, height);
        }
        else if (uid == "SliderUI") {
            baseline = getSliderBaseline((JSlider)component, height);
        }
        else if (uid == "SpinnerUI") {
            baseline = getSpinnerBaseline((JSpinner)component, height);
        }
        else if (uid == "ScrollPaneUI") {
            baseline = getScrollPaneBaseline((JScrollPane)component, height);
        }
        else if (uid == "TabbedPaneUI") {
            baseline = getTabbedPaneBaseline((JTabbedPane)component, height);
        }
        else if (uid == "TableUI") {
            baseline = getTableBaseline((JTable)component, height);
        }
        else if (uid == "TreeUI") {
            baseline = getTreeBaseline((JTree)component, height);
        }
        return Math.max(baseline, -1);
    }

    private static Insets rotateInsets(Insets topInsets, int targetPlacement) {
        switch(targetPlacement) {
          case JTabbedPane.LEFT:
              return new Insets(topInsets.left, topInsets.top, 
                                topInsets.right, topInsets.bottom);
          case JTabbedPane.BOTTOM:
              return new Insets(topInsets.bottom, topInsets.left,
                                topInsets.top, topInsets.right);
          case JTabbedPane.RIGHT:
              return new Insets(topInsets.left, topInsets.bottom,
                                topInsets.right, topInsets.top);
          default:
              return new Insets(topInsets.top, topInsets.left,
                                topInsets.bottom, topInsets.right);
        }
    }

    private static int getMaxTabHeight(JTabbedPane tp) {
        int fontHeight = tp.getFontMetrics(tp.getFont()).getHeight();
        int height = fontHeight;
        boolean tallerIcons = false;
        for (int counter = tp.getTabCount() - 1; counter >= 0; counter--) {
            Icon icon = tp.getIconAt(counter);
            if (icon != null) {
                int iconHeight = icon.getIconHeight();
                height = Math.max(height, iconHeight);
                if (iconHeight > fontHeight) {
                    tallerIcons = true;
                }
            }
        }
        Insets tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
        height += 2;
        if (!isMetal() || !tallerIcons) {
            height += tabInsets.top + tabInsets.bottom;
        }
        return height;
    }

    private static int getTabbedPaneBaseline(JTabbedPane tp, int height) {
        if (tp.getTabCount() > 0) {
            if (isAqua()) {
                return getAquaTabbedPaneBaseline(tp, height);
            }
            Insets insets = tp.getInsets();
            Insets contentBorderInsets = UIManager.getInsets(
                "TabbedPane.contentBorderInsets");
            Insets tabAreaInsets = rotateInsets(UIManager.getInsets(
                                                 "TabbedPane.tabAreaInsets"),
                                                tp.getTabPlacement());
            FontMetrics metrics = tp.getFontMetrics(tp.getFont());
            int maxHeight = getMaxTabHeight(tp);
            iconRect.setBounds(0, 0, 0, 0);
            textRect.setBounds(0, 0, 0, 0);
            viewRect.setBounds(0, 0, Short.MAX_VALUE, maxHeight);
            SwingUtilities.layoutCompoundLabel(tp, metrics, "A", null,
                                               SwingUtilities.CENTER,
                                               SwingUtilities.CENTER,
                                               SwingUtilities.CENTER,
                                               SwingUtilities.TRAILING,
                                               viewRect,
                                               iconRect,
                                               textRect,
                                               0);
            int baseline = textRect.y + metrics.getAscent();
            switch(tp.getTabPlacement()) {
            case JTabbedPane.TOP:
                baseline += insets.top + tabAreaInsets.top;
                if (isWindows()) {
                    if (tp.getTabCount() > 1) {
                        baseline += 1;
                    }
                    else {
                        baseline -= 1;
                    }
                }
                return baseline;
            case JTabbedPane.BOTTOM:
                baseline = tp.getHeight() - insets.bottom -
                    tabAreaInsets.bottom - maxHeight + baseline;
                if (isWindows()) {
                    if (tp.getTabCount() > 1) {
                        baseline += -1;
                    }
                    else {
                        baseline += 1;
                    }
                }
                return baseline;
            case JTabbedPane.LEFT:
            case JTabbedPane.RIGHT:
                if (isAqua()) {
                    // Aqua rotates left/right text, so that there isn't a good
                    // baseline.
                    return -1;
                }
                baseline += insets.top + tabAreaInsets.top;
                if (isWindows()) {
                    baseline += (maxHeight % 2);
                }
                return baseline;
            }
        }
        return -1;
    }

    private static int getAquaTabbedPaneBaseline(JTabbedPane tp, int height) {
        Font font = tp.getFont();
        FontMetrics metrics = tp.getFontMetrics(font);
        int ascent = metrics.getAscent();
        int offset;
        switch(tp.getTabPlacement()) {
            case JTabbedPane.TOP:
                offset = 5;
                if (tp.getFont().getSize() > 12) {
                    offset = 6;
                }
                int yOffset = 20 - metrics.getHeight();
                yOffset /= 2;
                return offset + yOffset + ascent - 1;
            case JTabbedPane.BOTTOM:
                if (tp.getFont().getSize() > 12) {
                    offset = 6;
                } else {
                    offset = 4;
                }
                return height - (20 -
                        ((20 - metrics.getHeight()) / 2 + ascent)) - offset;
            case JTabbedPane.LEFT:
            case JTabbedPane.RIGHT:
                // Aqua rotates left/right text, so that there isn't a good
                // baseline.
                return -1;
        }
        return -1;
    }
    
    private static int getSliderBaseline(JSlider slider, int height) {
        // We don't handle GTK as too much is hidden to be able to calculate it
        if (slider.getPaintLabels() && !isGTK()) {
            boolean isAqua = isAqua();
            FontMetrics metrics = slider.getFontMetrics(slider.getFont());
            Insets insets = slider.getInsets();
            Insets focusInsets = (Insets)UIManager.get("Slider.focusInsets");
	    if (slider.getOrientation() == JSlider.HORIZONTAL) {
                int tickLength = 8;
                int contentHeight = height - insets.top - insets.bottom -
                    focusInsets.top - focusInsets.bottom;
                int thumbHeight = 20;
                if (isMetal()) {
                    tickLength = ((Integer)UIManager.get(
                                      "Slider.majorTickLength")).intValue() + 5;
                    thumbHeight = UIManager.getIcon(
                        "Slider.horizontalThumbIcon" ).getIconHeight();
                }
                else if (isWindows() && isXP()) {
                    // NOTE: this is not correct, this should come from
                    // the skin (in >= 1.5), but short of reflection
                    // hacks we don't have access to the real value.
                    thumbHeight++;
                }
                int centerSpacing = thumbHeight;
                if (isAqua || slider.getPaintTicks()) {
                    // centerSpacing += getTickLength();
                    centerSpacing += tickLength;
                }
                // Assume uniform labels.
                centerSpacing += metrics.getAscent() + metrics.getDescent();
                int trackY = insets.top + focusInsets.top +
                    (contentHeight - centerSpacing - 1) / 2;
                if (isAqua) {
                    if (slider.getPaintTicks()) {
                        int prefHeight = slider.getUI().getPreferredSize(slider).
                                height;
                        int prefDelta = height - prefHeight;
                        if (prefDelta > 0) {
                            trackY -= Math.min(1, prefDelta);
                        }
                    } else {
                        trackY--;
                    }
                }
  
                int trackHeight = thumbHeight;
                int tickY = trackY + trackHeight;
                int tickHeight = tickLength;
                if (!isAqua && !slider.getPaintTicks()) {
                    tickHeight = 0;
                }
                int labelY = tickY + tickHeight;
                return labelY + metrics.getAscent();
            }
            else { // vertical
                boolean inverted = slider.getInverted();
                Integer value = inverted ? getMinSliderValue(slider) :
                                           getMaxSliderValue(slider);
                if (value != null) {
                    int thumbHeight = 11;
                    if (isMetal()) {
                        thumbHeight = UIManager.getIcon(
                            "Slider.verticalThumbIcon").getIconHeight();
                    }
                    int trackBuffer = Math.max(metrics.getHeight() / 2,
                                               thumbHeight / 2);
                    int contentY = focusInsets.top + insets.top;
                    int trackY = contentY + trackBuffer;
                    int trackHeight = height - focusInsets.top -
                        focusInsets.bottom - insets.top - insets.bottom -
                        trackBuffer - trackBuffer;
                    int maxValue = getMaxSliderValue(slider).intValue();
                    int min = slider.getMinimum();
                    int max = slider.getMaximum();
                    double valueRange = (double)max - (double)min;
                    double pixelsPerValue = (double)trackHeight /
                        (double)valueRange;
                    int trackBottom = trackY + (trackHeight - 1);
                    if (isAqua) {
                        trackY -= 3;
                        trackBottom += 6;
                    }
                    int yPosition = trackY;
                    double offset;

                    if (!inverted) {
                        offset = pixelsPerValue *
                                            ((double)max - value.intValue());
                    }
                    else {
                        offset = pixelsPerValue *
                                           ((double)value.intValue() - min);
                    }
                    if (isAqua) {
                        yPosition += Math.floor(offset);
                    } else {
                        yPosition += Math.round(offset);
                    }
                    yPosition = Math.max(trackY, yPosition);
                    yPosition = Math.min(trackBottom, yPosition);
                    if (isAqua) {
                        return yPosition + metrics.getAscent();
                    }
                    return yPosition - metrics.getHeight() / 2 +
                        metrics.getAscent();
                }
            }
        }
        return -1;
    }

    private static Integer getMaxSliderValue(JSlider slider) {
        Dictionary dictionary = slider.getLabelTable();
        if (dictionary != null) {
            Enumeration keys = dictionary.keys();
            int max = slider.getMinimum() - 1;
            while (keys.hasMoreElements()) {
                max = Math.max(max, ((Integer)keys.nextElement()).intValue());
            }
            if (max == slider.getMinimum() - 1) {
                return null;
            }
            return new Integer(max);
        }
        return null;
    }

    private static Integer getMinSliderValue(JSlider slider) {
        Dictionary dictionary = slider.getLabelTable();
        if (dictionary != null) {
            Enumeration keys = dictionary.keys();
            int min = slider.getMaximum() + 1;
            while (keys.hasMoreElements()) {
                min = Math.min(min, ((Integer)keys.nextElement()).intValue());
            }
            if (min == slider.getMaximum() + 1) {
                return null;
            }
            return new Integer(min);
        }
        return null;
    }

    private static int getProgressBarBaseline(JProgressBar pb, int height) {
        if (pb.isStringPainted() &&
                pb.getOrientation() == JProgressBar.HORIZONTAL) {
            FontMetrics metrics = pb.getFontMetrics(pb.getFont());
            Insets insets = pb.getInsets();
            int y = insets.top;
            if (isWindows() && isXP()) {
                if (pb.isIndeterminate()) {
                    y = -1;
                    height--;
                }
                else {
                    y = 0;
                    height -= 3;
                }
            }
            else if (isGTK()) {
                return (height - metrics.getAscent() - 
                        metrics.getDescent()) / 2 + metrics.getAscent();
            }
            else if (isAqua()) {
                if (pb.isIndeterminate()) {
                    // Aqua doesn't appear to support text on indeterminate
                    // progress bars.
                    return -1;
                }
                y -= 1;
                height -= (insets.top + insets.bottom);
            }
            else {
                height -= insets.top + insets.bottom;
            }
            return y + (height + metrics.getAscent() -
                        metrics.getLeading() -
                        metrics.getDescent()) / 2;
        }
        return -1;
    }

    private static int getTreeBaseline(JTree tree, int height) {
        int rowHeight = tree.getRowHeight();
        if (TREE_LABEL == null) {
            TREE_LABEL = new JLabel("X");
            TREE_LABEL.setIcon(UIManager.getIcon("Tree.closedIcon"));
        }
        JLabel label = TREE_LABEL;
        label.setFont(tree.getFont());
        if (rowHeight <= 0) {
            rowHeight = label.getPreferredSize().height;
        }
        return getLabelBaseline(label, rowHeight) + tree.getInsets().top;
    }

    private static int getTableBaseline(JTable table, int height) {
        if (TABLE_LABEL == null) {
            TABLE_LABEL = new JLabel("");
            TABLE_LABEL.setBorder(new EmptyBorder(1, 1, 1, 1));
        }
        JLabel label = TABLE_LABEL;
        label.setFont(table.getFont());
        int rowMargin = table.getRowMargin();
        int baseline = getLabelBaseline(label, table.getRowHeight() -
                                        rowMargin);
        return baseline += rowMargin / 2;
    }

    private static int getTextAreaBaseline(JTextArea text, int height) {
        Insets insets = text.getInsets();
        FontMetrics fm = text.getFontMetrics(text.getFont());
        return insets.top + fm.getAscent();
    }
    
    private static int getListBaseline(JList list, int height) {
        int rowHeight = list.getFixedCellHeight();
        if (LIST_LABEL == null) {
            LIST_LABEL = new JLabel("X");
            LIST_LABEL.setBorder(new EmptyBorder(1, 1, 1, 1));
        }
        JLabel label = LIST_LABEL;
        label.setFont(list.getFont());
        // JList actually has much more complex behavior here.
        // If rowHeight != -1 the rowHeight is either the max of all cell
        // heights (layout orientation != VERTICAL), or is variable depending
        // upon the cell.  We assume a default size.
        // We could theoretically query the real renderer, but that would
        // not work for an empty model and the results may vary with 
        // the content.
        if (rowHeight == -1) {
            rowHeight = label.getPreferredSize().height;
        }
        return getLabelBaseline(label, rowHeight) + list.getInsets().top;
    }

    private static int getScrollPaneBaseline(JScrollPane sp, int height) {
        Component view = sp.getViewport().getView();
        if (view instanceof JComponent) {
            int baseline = getBaseline((JComponent)view);
            if (baseline > 0) {
                return baseline + sp.getViewport().getY();
            }
        }
        return -1;
    }

    private static int getPanelBaseline(JPanel panel, int height) {
        Border border = panel.getBorder();
        if (border instanceof TitledBorder) {
            TitledBorder titledBorder = (TitledBorder)border;
            if (titledBorder.getTitle() != null &&
                      !"".equals(titledBorder.getTitle())) {
                Font font = titledBorder.getTitleFont();
                if (font == null) {
                    font = panel.getFont();
                    if (font == null) {
                        font = new Font("Dialog", Font.PLAIN, 12);
                    }
                }
                Border border2 = titledBorder.getBorder();
                Insets borderInsets;
                if (border2 != null) {
                    borderInsets = border2.getBorderInsets(panel);
                }
                else {
                    borderInsets = EMPTY_INSETS;
                }
                FontMetrics fm = panel.getFontMetrics(font);
                int fontHeight = fm.getHeight();
                int descent = fm.getDescent();
                int ascent = fm.getAscent();
                int y = EDGE_SPACING;
                int h = height - EDGE_SPACING * 2;
                int diff;
                switch (((TitledBorder)border).getTitlePosition()) {
                case TitledBorder.ABOVE_TOP:
                    diff = ascent + descent + (Math.max(EDGE_SPACING,
                                    TEXT_SPACING*2) - EDGE_SPACING);
                    return y + diff - (descent + TEXT_SPACING);
                case TitledBorder.TOP:
                case TitledBorder.DEFAULT_POSITION:
                    diff = Math.max(0, ((ascent/2) + TEXT_SPACING) -
                                    EDGE_SPACING);
                    return (y + diff - descent) +
                           (borderInsets.top + ascent + descent)/2;
                case TitledBorder.BELOW_TOP:
                    return y + borderInsets.top + ascent + TEXT_SPACING;
                case TitledBorder.ABOVE_BOTTOM:
                    return (y + h) -
                        (borderInsets.bottom + descent + TEXT_SPACING);
                case TitledBorder.BOTTOM:
                    h -= fontHeight / 2;
                    return ((y + h) - descent) +
                           ((ascent + descent) - borderInsets.bottom)/2;
                case TitledBorder.BELOW_BOTTOM:
                    h -= fontHeight;
                    return y + h + ascent + TEXT_SPACING;
                }
            }
        }
        return -1;
    }

    private static int getSpinnerBaseline(JSpinner spinner, int height) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor)
                                          editor;
            JTextField tf = defaultEditor.getTextField();
            Insets spinnerInsets = spinner.getInsets();
            Insets editorInsets = defaultEditor.getInsets();
            int offset = spinnerInsets.top + editorInsets.top;
            height -= (offset + spinnerInsets.bottom + editorInsets.bottom);
            if (height <= 0) {
                return -1;
            }
            return offset + getSingleLineTextBaseline(tf, height);
        }
        Insets insets = spinner.getInsets();
        FontMetrics fm = spinner.getFontMetrics(spinner.getFont());
        return insets.top + fm.getAscent();
    }

    private static int getLabelBaseline(JLabel label, int height) {
        Icon icon = (label.isEnabled()) ? label.getIcon() :
                           label.getDisabledIcon();
        FontMetrics fm = label.getFontMetrics(label.getFont());

        resetRects(label, height);

        SwingUtilities.layoutCompoundLabel(label, fm,
            "a", icon, label.getVerticalAlignment(),
            label.getHorizontalAlignment(), label.getVerticalTextPosition(),
            label.getHorizontalTextPosition(), viewRect, iconRect, textRect,
            label.getIconTextGap());

        return textRect.y + fm.getAscent();
    }

    private static int getComboBoxBaseline(JComboBox combobox, int height) {
        Insets insets = combobox.getInsets();
        int y = insets.top;
        height -= (insets.top + insets.bottom);
        if (combobox.isEditable()) {
            ComboBoxEditor editor = combobox.getEditor();
            if (editor != null && (editor.getEditorComponent() instanceof
                                   JTextField)) {
                JTextField tf = (JTextField)editor.getEditorComponent();
                return y + getSingleLineTextBaseline(tf, height);
            }
        }
        // Use the renderer to calculate baseline
        if (isMetal()) {
            if (isOceanTheme()) {
                y += 2;
                height -= 4;
            }
        }
        else if (isWindows()) {
            // This doesn't guarantee an XP style will be active,
            // but we don't offer public API to detect if XP is active.
            String osVersion = System.getProperty("os.version");
            if (osVersion != null) {
                Float version = Float.valueOf(osVersion);
                if (version.floatValue() > 4.0) {
                    y += 2;
                    height -= 4;
                }
            }
        }
        ListCellRenderer renderer = combobox.getRenderer();
        if (renderer instanceof JLabel) {
            int baseline = y + getLabelBaseline((JLabel)renderer, height);
            if (isAqua()) {
                return baseline - 1;
            }
            return baseline;
        }
        // Renderer isn't a label, use metrics directly.
        FontMetrics fm = combobox.getFontMetrics(combobox.getFont());
        return y + fm.getAscent();
    }

    /**
     * Returns the baseline for single line text components, like
     * <code>JTextField</code>.
     */
    private static int getSingleLineTextBaseline(JTextComponent textComponent,
                                                 int h) {
        View rootView = textComponent.getUI().getRootView(textComponent);
        if (rootView.getViewCount() > 0) {
            Insets insets = textComponent.getInsets();
            int height = h - insets.top - insets.bottom;
            int y = insets.top;
            View fieldView = rootView.getView(0);
	    int vspan = (int)fieldView.getPreferredSpan(View.Y_AXIS);
	    if (height != vspan) {
		int slop = height - vspan;
		y += slop / 2;
	    }
            FontMetrics fm = textComponent.getFontMetrics(
                                 textComponent.getFont());
            y += fm.getAscent();
            return y;
        }
        return -1;
    }

    /**
     * Returns the baseline for buttons.
     */
    private static int getButtonBaseline(AbstractButton button, int height) {
        FontMetrics fm = button.getFontMetrics(button.getFont());

        resetRects(button, height);

        String text = button.getText();
        if (text != null && text.startsWith("<html>")) {
            return -1;
        }
        // NOTE: that we use "a" here to make sure we get a valid value, if
        // we were to pass in an empty string or null we would not get
        // back the right thing.
        SwingUtilities.layoutCompoundLabel(
            button, fm, "a", button.getIcon(), 
            button.getVerticalAlignment(), button.getHorizontalAlignment(),
            button.getVerticalTextPosition(),
            button.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, 
            text == null ? 0 : button.getIconTextGap());

        if (isAqua()) {
            return textRect.y + fm.getAscent() + 1;
        }
        return textRect.y + fm.getAscent();
    }

    private static void resetRects(JComponent c, int height) {
        Insets insets = c.getInsets();
        viewRect.x = insets.left;
        viewRect.y = insets.top;
        viewRect.width = c.getWidth() - (insets.right + viewRect.x);
        viewRect.height = height - (insets.bottom + viewRect.y);
        textRect.x = textRect.y = textRect.width = textRect.height = 0;
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
    }
    
    private static boolean isOceanTheme() {
        if (!inSandbox) {
            try {
                java.lang.reflect.Field field = MetalLookAndFeel.class.getDeclaredField("currentTheme");
                field.setAccessible(true);
                Object theme = field.get(null);
                return "javax.swing.plaf.metal.OceanTheme".equals(theme.getClass().getName());
            } catch (Exception ex) {
                // We're in a sandbox and can't access the field
                inSandbox = true;
            }
        }
        if (!checkedForOcean) {
            checkedForOcean = true;
            checkForOcean();
        }
        return usingOcean;
    }
    
    private static void checkForOcean() {
        String version = System.getProperty("java.specification.version");
        int firstDot = version.indexOf('.');
        String majorString;
        String minorString;
        if (firstDot != -1) {
            majorString = version.substring(0, firstDot);
            int secondDot = version.indexOf('.', firstDot + 1);
            if (secondDot == -1) {
                minorString = version.substring(firstDot + 1);
            } else {
                minorString = version.substring(firstDot + 1, secondDot);
            }
        } else {
            majorString = version;
            minorString = null;
        }
        try {
            int majorVersion = Integer.parseInt(majorString);
            int minorVersion = (minorString != null) ? Integer.parseInt(minorString) : 0;
            usingOcean = (majorVersion > 1 || minorVersion > 4);
        } catch (NumberFormatException nfe) {
        }
    }

    private static boolean isWindows() {
        return isWindows(UIManager.getLookAndFeel());
    }
    
    private static boolean isWindows(LookAndFeel laf) {
        if (laf.getID() == "Windows") {
            return true;
        }
        if (!checkedForWindows) {
            try {
                WINDOWS_CLASS = Class.forName(
                  "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (ClassNotFoundException e) {
            }
            checkedForWindows = true;
        }
        return (WINDOWS_CLASS != null && WINDOWS_CLASS.isInstance(laf));
    }
    
    private static boolean isMetal() {
        return isMetal(UIManager.getLookAndFeel());
    }

    private static boolean isMetal(LookAndFeel laf) {
        return (laf.getID() == "Metal" || laf instanceof MetalLookAndFeel);
    }

    private static boolean isGTK() {
        return UIManager.getLookAndFeel().getID() == "GTK";
    }

    private static boolean isAqua() {
        return UIManager.getLookAndFeel().getID() == "Aqua";
    }

    private static boolean isXP() {
        if (!checkedForClassic) {
            try {
                CLASSIC_WINDOWS = Class.forName(
                  "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
            } catch (ClassNotFoundException e) {
            }
            checkedForClassic = true;
        }
        if (CLASSIC_WINDOWS != null && CLASSIC_WINDOWS.
                    isInstance(UIManager.getLookAndFeel())) {
            return false;
        }
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Boolean themeActive = (Boolean)toolkit.getDesktopProperty(
                                       "win.xpstyle.themeActive");
        if (themeActive == null) {
            themeActive = Boolean.FALSE;
        }
        return themeActive.booleanValue();
    }

    /**
     * Creates an instance of Baseline.  You typically don't create a
     * Baseline.  The constructor is provided by look and feels that wish
     * to provide baseline support.
     * <p>
     * A custom look and feel that wants to provide <code>Baseline</code>
     * support should put the instance in the defaults returned
     * from <code>getDefaults</code>.  If you want to override the 
     * baseline suport for a look and feel place the instance in the defaults
     * returned from UIManager.getLookAndFeelDefaults().  Tthis will ensure
     * that if the look and feel changes the appropriate baseline can be used.
     */
    protected Baseline() {
    }
    
    /**
     * Returns the baseline for the specified component, or -1 if the
     * baseline can not be determined.  The baseline is measured from
     * the top of the component.
     *
     * @param component JComponent to calculate baseline for
     * @param width Width of the component to determine baseline for.
     * @param height Height of the component to determine baseline for.
     * @return baseline for the specified component
     */
    public int getComponentBaseline(JComponent component, int width,
            int height) {
        return -1;
    }
}
