/*
 * Copyright (C) 2005-2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.layout;

import java.awt.Container;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JComponent;

/**
 *
 * @author sky
 */
class SwingLayoutStyle extends LayoutStyle {
    private static final Method SWING_GET_LAYOUT_STYLE_METHOD;
    
    private static final Method SWING_GET_PREFERRED_GAP_METHOD;
    private static final Method SWING_GET_CONTAINER_GAP_METHOD;
    
    private static final Object RELATED_TYPE;
    private static final Object UNRELATED_TYPE;
    private static final Object INDENT_TYPE;
    
    static {
        Method getLayoutStyle = null;
        Method getPreferredGap = null;
        Method getContainerGap = null;
        Object relatedType = null;
        Object unrelatedType = null;
        Object indentType = null;
        try {
            Class swingLayoutStyleClass;
            Class swingComponentPlacementClass;
            
            swingLayoutStyleClass = Class.forName("javax.swing.LayoutStyle");
            swingComponentPlacementClass = Class.forName(
                    "javax.swing.LayoutStyle$ComponentPlacement");
            swingLayoutStyleClass = Class.forName("javax.swing.LayoutStyle");
            getLayoutStyle =
                    swingLayoutStyleClass.getMethod("getInstance", null);
            getPreferredGap =
                    swingLayoutStyleClass.getMethod("getPreferredGap",
                    new Class[] { JComponent.class, JComponent.class,
                    swingComponentPlacementClass,
                    int.class, Container.class } );
            getContainerGap = swingLayoutStyleClass.getMethod("getContainerGap",
                    new Class[] { JComponent.class, int.class,
                    Container.class } );
            relatedType = swingComponentPlacementClass.getField("RELATED").get(null);
            unrelatedType = swingComponentPlacementClass.getField("UNRELATED").get(null);
            indentType = swingComponentPlacementClass.getField("INDENT").get(null);
        } catch (ClassNotFoundException cnfe) {
        } catch (NoSuchMethodException nsme) {
        } catch (NoSuchFieldException nsfe) {
        } catch (IllegalAccessException iae) {
        }
        SWING_GET_LAYOUT_STYLE_METHOD = getLayoutStyle;
        SWING_GET_PREFERRED_GAP_METHOD = getPreferredGap;
        SWING_GET_CONTAINER_GAP_METHOD = getContainerGap;
        RELATED_TYPE = relatedType;
        UNRELATED_TYPE = unrelatedType;
        INDENT_TYPE = indentType;
    }
    
    private static final Object layoutStyleTypeToComponentPlacement(int type) {
        if (type == LayoutStyle.RELATED) {
            return RELATED_TYPE;
        } else if (type == LayoutStyle.UNRELATED) {
            return UNRELATED_TYPE;
        } else {
            assert (type == LayoutStyle.INDENT);
            return INDENT_TYPE;
        }
    }
    
    private static final Object getSwingLayoutStyle() {
        try {
            return SWING_GET_LAYOUT_STYLE_METHOD.invoke(null, null);
        } catch (IllegalAccessException iae) {
        } catch (InvocationTargetException ite) {
        }
        return null;
    }
    
    public int getPreferredGap(JComponent component1, JComponent component2,
            int type, int position, Container parent) {
        super.getPreferredGap(component1, component2, type, position, parent);
        Object componentPlacement = layoutStyleTypeToComponentPlacement(type);
        Object layoutStyle = getSwingLayoutStyle();
        try {
            return ((Integer)SWING_GET_PREFERRED_GAP_METHOD.invoke(layoutStyle,
                    new Object[] { component1, component2, componentPlacement,
                    new Integer(position), parent })).intValue();
        } catch (IllegalAccessException iae) {
        } catch (InvocationTargetException ite) {
        }
        return 0;
    }
    
    public int getContainerGap(JComponent component, int position,
            Container parent) {
        super.getContainerGap(component, position, parent);
        Object layoutStyle = getSwingLayoutStyle();
        try {
            return ((Integer)SWING_GET_CONTAINER_GAP_METHOD.invoke(layoutStyle,
                    new Object[] { component, new Integer(position),
                    parent })).intValue();
        } catch (IllegalAccessException iae) {
        } catch (InvocationTargetException ite) {
        }
        return 0;
    }
}
