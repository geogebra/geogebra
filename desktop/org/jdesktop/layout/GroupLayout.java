/*
 * Copyright (C) 2005-2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingConstants;

/**
 * GroupLayout is a LayoutManager that hierarchically groups components to
 * achieve common, and not so common, layouts.  Grouping is done by instances
 * of the Group class.  GroupLayout supports two types of groups:
 * <table>
 *   <tr><td valign=top>Sequential:<td>A sequential group positions its child
 *           elements sequentially, one after another.
 *   <tr><td valign=top>Parallel:<td>A parallel group positions its child 
 *           elements in the same space on top of each other.  Parallel groups 
 *           can also align the child elements along their baseline.
 * </table>
 * Each Group can contain any number of child groups, Components or gaps.
 * GroupLayout treats each axis independently.  That is, there is a group
 * representing the horizontal axis, and a separate group representing the
 * vertical axis.  The horizontal group is responsible for setting the x
 * and width of its contents, where as the vertical group is responsible for
 * setting the y and height of its contents.
 * <p>
 * The following code builds a simple layout consisting of two labels in
 * one column, followed by two textfields in the next column:
 * <pre>
 *   JComponent panel = ...;
 *   GroupLayout layout = new GroupLayout(panel);
 *   panel.setLayout(layout);
 *   layout.setAutocreateGaps(true);
 *   layout.setAutocreateContainerGaps(true);
 *   GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
 *   hGroup.add(layout.createParallelGroup().add(label1).add(label2)).
 *          add(layout.createParallelGroup().add(tf1).add(tf2));
 *   layout.setHorizontalGroup(hGroup);
 *   GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
 *   vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(label1).add(tf1)).
 *          add(layout.createParallelGroup(GroupLayout.BASELINE).add(label2).add(tf2));
 *   layout.setVerticalGroup(vGroup);
 * </pre>
 * <p>
 * This layout consists of the following:
 * <ul><li>The horizontal axis consists of a sequential group containing two
 *         parallel groups.  The first parallel group consists of the labels,
 *         with the second parallel group consisting of the text fields.
 *     <li>The vertical axis similarly consists of a sequential group
 *         containing two parallel groups.  The parallel groups align their
 *         contents along the baseline.  The first parallel group consists
 *         of the first label and text field, and the second group consists
 *         of the second label and text field.
 * </ul>
 * There are a couple of things to notice in this code:
 * <ul>
 *   <li>You need not explicitly add the components to the container, this
 *       is indirectly done by using one of the <code>add</code> methods.
 *   <li>The various <code>add</code> methods of <code>Groups</code> return
 *       themselves.  This allows for easy chaining of invocations.  For
 *       example, <code>group.add(label1).add(label2);</code> is equivalent to
 *       <code>group.add(label1);group.add(label2);</code>.
 *   <li>There are no public constructors for the Groups, instead
 *       use the create methods of <code>GroupLayout</code>.
 * </ul>
 * GroupLayout offer the ability to automatically insert the appropriate gap
 * between components.  This can be turned on using the
 * <code>setAutocreateGaps()</code> method.  Similarly you can use
 * the <code>setAutocreateContainerGaps()</code> method to insert gaps
 * between the components and the container.
 * 
 * @version $Revision: 1.25 $
 * @author Tomas Pavek
 * @author Jan Stola
 * @author Scott Violet
 */
public class GroupLayout implements LayoutManager2 {
    // Used in size calculations
    private static final int MIN_SIZE = 0;
    private static final int PREF_SIZE = 1;
    private static final int MAX_SIZE = 2;
    // Used by prepare, indicates min, pref or max isn't going to be used.
    private static final int SPECIFIC_SIZE = 3;
    
    private static final int UNSET = Integer.MIN_VALUE;

    /**
     * Possible argument when linking sizes of components.  Specifies the
     * the two component should share the same size along the horizontal
     * axis.
     *
     * @see #linkSize(java.awt.Component[],int)
     */
    public static final int HORIZONTAL = 1;

    /**
     * Possible argument when linking sizes of components.  Specifies the
     * the two component should share the same size along the vertical
     * axis.
     *
     * @see #linkSize(java.awt.Component[],int)
     */
    public static final int VERTICAL = 2;
    
    private static final int NO_ALIGNMENT = 0;
    /**
     * Possible alignment type.  Indicates the elements should be
     * aligned to the origin.  For the horizontal axis with a left to
     * right orientation this means aligned to the left.
     *
     * @see #createParallelGroup(int)
     */
    public static final int LEADING = 1;
    /**
     * Possible alignment type.  Indicates the elements should be
     * aligned to the end.  For the horizontal axis with a left to
     * right orientation this means aligned to the right.
     *
     * @see #createParallelGroup(int)
     */
    public static final int TRAILING = 2;
    /**
     * Possible alignment type.  Indicates the elements should centered in
     * the spaced provided.
     *
     * @see #createParallelGroup(int)
     */
    public static final int CENTER = 4;
    /**
     * Possible alignment type.  Indicates the elements should aligned along
     * their baseline.
     *
     * @see #createParallelGroup(int)
     */
    public static final int BASELINE = 3;
    
    /**
     * Possible value for the add methods that takes a Component.
     * Indicates the size from the component should be used.
     */
    public static final int DEFAULT_SIZE = -1;
    /**
     * Possible value for the add methods that takes a Component.
     * Indicates the preferred size should be used.
     */
    public static final int PREFERRED_SIZE = -2;
    
    // Whether or not we automatically try and create the preferred
    // padding between components.
    private boolean autocreatePadding;
    
    // Whether or not we automatically try and create the preferred
    // padding between containers
    private boolean autocreateContainerPadding;
    
    /**
     * Group responsible for layout along the horizontal axis.  This is NOT
     * the user specified group, use getHorizontalGroup to dig that out.
     */
    private Group horizontalGroup;
    /**
     * Group responsible for layout along the vertical axis.  This is NOT
     * the user specified group, use getVerticalGroup to dig that out.
     */
    private Group verticalGroup;
    
    // Maps from Component to ComponentInfo.  This is used for tracking
    // information specific to a Component.
    private Map componentInfos;
    
    // Container we're doing layout for.
    private Container host;
    
    // Used by areParallelSiblings, cached to avoid excessive garbage.
    private Set tmpParallelSet;
    
    // Indicates Springs have changed in some way since last change.
    private boolean springsChanged;
    
    // Indicates invalidateLayout has been invoked.
    private boolean isValid;

    // Whether or not any preferred padding (or container padding) springs exist
    private boolean hasPreferredPaddingSprings;
    
    /**
     * The LayoutStyle instance to use, if null the sharedInstance is used.
     */
    private LayoutStyle layoutStyle;

    /**
     * If true, components that are not visible are treated as though they
     * aren't there.
     */
    private boolean honorsVisibility;

    
    private static void checkSize(int min, int pref, int max,
            boolean isComponentSpring) {
        checkResizeType(min, isComponentSpring);
        if (!isComponentSpring && pref < 0) {
            throw new IllegalArgumentException("Pref must be >= 0");
        } else if (isComponentSpring) {
            checkResizeType(pref, true);
        }
        checkResizeType(max, isComponentSpring);
        checkLessThan(min, pref);
        checkLessThan(pref, max);
    }

    private static void checkResizeType(int type, boolean isComponentSpring) {
        if (type < 0 && ((isComponentSpring && type != DEFAULT_SIZE &&
                                               type != PREFERRED_SIZE) ||
                          (!isComponentSpring && type != PREFERRED_SIZE))) {
            throw new IllegalArgumentException("Invalid size");
        }
    }
    
    private static void checkLessThan(int min, int max) {
        if (min >= 0 && max >= 0 && min > max) {
            throw new IllegalArgumentException(
                             "Following is not met: min<=pref<=max");
        }
    }
    

    /**
     * Creates a GroupLayout for the specified JComponent.
     *
     * @param host the Container to layout
     * @throws IllegalArgumentException if host is null
     */
    public GroupLayout(Container host) {
        if (host == null) {
            throw new IllegalArgumentException("Container must be non-null");
        }
        honorsVisibility = true;
        this.host = host;
        setHorizontalGroup(createParallelGroup(LEADING, true));
        setVerticalGroup(createParallelGroup(LEADING, true));
        componentInfos = new HashMap();
        tmpParallelSet = new HashSet();
    }

    /**
     * Sets whether component visiblity is considered when sizing and
     * positioning components. A value of <code>true</code> indicates that
     * non-visible components should not be treated as part of the
     * layout. A value of <code>false</code> indicates that components should be
     * positioned and sized regardless of visibility.
     * <p>
     * A value of <code>false</code> is useful when the visibility of components
     * is dynamically adjusted and you don't want surrounding components and
     * the sizing to change.
     * <p>
     * The specified value is used for components that do not have an
     * explicit visibility specified.
     * <p>
     * The default is <code>true</code>.
     *
     * @param honorsVisibility whether component visiblity is considered when
     *                         sizing and positioning components
     * @see #setHonorsVisibility(Component,Boolean)
     */
    public void setHonorsVisibility(boolean honorsVisibility) {
        if (this.honorsVisibility != honorsVisibility) {
            this.honorsVisibility = honorsVisibility;
            springsChanged = true;
            isValid = false;
            invalidateHost();
        }
    }
    
    /**
     * Returns whether component visiblity is considered when sizing and
     * positioning components.
     *
     * @return whether component visiblity is considered when sizing and
     *         positioning components
     */
    public boolean getHonorsVisibility() {
        return honorsVisibility;
    }
    
    /**
     * Sets whether the component's visiblity is considered for
     * sizing and positioning. A value of <code>Boolean.TRUE</code>
     * indicates that if <code>component</code> is not visible it should
     * not be treated as part of the layout. A value of <code>false</code>
     * indicates that <code>component</code> is positioned and sized
     * regardless of it's visibility.  A value of <code>null</code>
     * indicates the value specified by the single argument method <code>
     * setHonorsVisibility</code> should be used.
     * <p>
     * If <code>component</code> is not a child of the <code>Container</code> this
     * <code>GroupLayout</code> is managing, it will be added to the
     * <code>Container</code>.
     *
     * @param component the component
     * @param honorsVisibility whether <code>component</code>'s visiblity should be
     *              considered for sizing and positioning
     * @throws IllegalArgumentException if <code>component</code> is <code>null</code>
     * @see #setHonorsVisibility(boolean)
     */
    public void setHonorsVisibility(Component component,
            Boolean honorsVisibility) {
        if (component == null) {
            throw new IllegalArgumentException("Component must be non-null");
        }
        getComponentInfo(component).setHonorsVisibility(honorsVisibility);
        springsChanged = true;
        isValid = false;
        invalidateHost();
    }

    /**
     * Returns a textual description of this GroupLayout.  The return value
     * is intended for debugging purposes only.
     *
     * @return textual description of this GroupLayout
     **/
    public String toString() {
        if (springsChanged) {
            registerComponents(horizontalGroup, HORIZONTAL);
            registerComponents(verticalGroup, VERTICAL);
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("HORIZONTAL\n");
        dump(buffer, horizontalGroup, "  ", HORIZONTAL);
        buffer.append("\nVERTICAL\n");
        dump(buffer, verticalGroup, "  ", VERTICAL);
        return buffer.toString();
    }
    
    private void dump(StringBuffer buffer, Spring spring, String indent,
            int axis) {
        String origin = "";
        String padding = "";
        if (spring instanceof ComponentSpring) {
            ComponentSpring cSpring = (ComponentSpring)spring;
            origin = Integer.toString(cSpring.getOrigin()) + " ";
            String name = cSpring.getComponent().getName();
            if (name != null) {
                origin = "name=" + name + ", ";
            }
        }
        if (spring instanceof AutopaddingSpring) {
            AutopaddingSpring paddingSpring = (AutopaddingSpring)spring;
            padding = ", userCreated=" + paddingSpring.getUserCreated() +
                    ", matches=" + paddingSpring.getMatchDescription();
        }
        buffer.append(indent + spring.getClass().getName() + " " +
                Integer.toHexString(spring.hashCode()) + " " +
                origin +
                ", size=" + spring.getSize() +
                ", alignment=" + spring.getAlignment() +
                " prefs=[" + spring.getMinimumSize(axis) +
                " " + spring.getPreferredSize(axis) +
                " " + spring.getMaximumSize(axis) + 
                padding + "]\n");
        if (spring instanceof Group) {
            List springs = ((Group)spring).springs;
            indent += "  ";
            for (int counter = 0; counter < springs.size(); counter++) {
                dump(buffer, (Spring)springs.get(counter), indent, axis);
            }
        }
    }
    
    /**
     * Sets whether or not a gap between components 
     * should automatically be created.  For example, if this is true
     * and you add two components to a <code>SequentialGroup</code> a
     * gap between the two will automatically be created.  The default
     * is false.
     *
     * @param autocreatePadding whether or not to automatically created a gap
     *        between components and the container
     */
    public void setAutocreateGaps(boolean autocreatePadding) {
        if (this.autocreatePadding != autocreatePadding) {
            this.autocreatePadding = autocreatePadding;
            invalidateHost();
        }
    }
    
    /**
     * Returns true if gaps between components are automatically be created.
     *
     * @return true if gaps between components should automatically be created
     */
    public boolean getAutocreateGaps() {
        return autocreatePadding;
    }

    /**
     * Sets whether or not gaps between the container and the first/last
     * components should automatically be created. The default
     * is false.
     *
     * @param autocreatePadding whether or not to automatically create
     *        gaps between the container and first/last components.
     */
    public void setAutocreateContainerGaps(boolean autocreatePadding) {
        if (autocreatePadding != autocreateContainerPadding) {
            autocreateContainerPadding = autocreatePadding;
            horizontalGroup = createTopLevelGroup(getHorizontalGroup());
            verticalGroup = createTopLevelGroup(getVerticalGroup());
            invalidateHost();
        }
    }
    
    /**
     * Returns whether or not gaps between the container and the
     * first/last components should automatically be created. The default
     * is false.
     *
     * @return whether or not the gaps between the container and the
     *         first/last components should automatically be created
     */
    public boolean getAutocreateContainerGaps() {
        return autocreateContainerPadding;
    }

    /**
     * Sets the <code>Group</code> that is responsible for
     * layout along the horizontal axis.
     *
     * @param group <code>Group</code> responsible for layout along
     *          the horizontal axis
     * @throws IllegalArgumentException if group is null
     */
    public void setHorizontalGroup(Group group) {
        if (group == null) {
            throw new IllegalArgumentException("Group must be non-null");
        }
        horizontalGroup = createTopLevelGroup(group);
        invalidateHost();
    }
    
    /**
     * Returns the <code>Group</code> that is responsible for
     * layout along the horizontal axis.
     *
     * @return <code>ParallelGroup</code> responsible for layout along
     *          the horizontal axis.
     */
    public Group getHorizontalGroup() {
        int index = 0;
        if (horizontalGroup.springs.size() > 1) {
            index = 1;
        }
        return (Group)horizontalGroup.springs.get(index);
    }
    
    /**
     * Sets the <code>Group</code> that is responsible for
     * layout along the vertical axis.
     *
     * @param group <code>Group</code> responsible for layout along
     *          the vertical axis.
     * @throws IllegalArgumentException if group is null.
     */
    public void setVerticalGroup(Group group) {
        if (group == null) {
            throw new IllegalArgumentException("Group must be non-null");
        }
        verticalGroup = createTopLevelGroup(group);
        invalidateHost();
    }
    
    /**
     * Returns the <code>ParallelGroup</code> that is responsible for
     * layout along the vertical axis.
     *
     * @return <code>ParallelGroup</code> responsible for layout along
     *          the vertical axis.
     */
    public Group getVerticalGroup() {
        int index = 0;
        if (verticalGroup.springs.size() > 1) {
            index = 1;
        }
        return (Group)verticalGroup.springs.get(index);
    }

    /**
     * Wraps the user specified group in a sequential group.  If 
     * container gaps should be generate the necessary springs are
     * added.
     */
    private Group createTopLevelGroup(Group specifiedGroup) {
        SequentialGroup group = createSequentialGroup();
        if (getAutocreateContainerGaps()) {
            group.addSpring(new ContainerAutopaddingSpring());
            group.add(specifiedGroup);
            group.addSpring(new ContainerAutopaddingSpring());
        } else {
            group.add(specifiedGroup);
        }
        return group;
    }

    /**
     * Creates and returns a <code>SequentialGroup</code>.
     *
     * @return a new <code>SequentialGroup</code>
     */
    public SequentialGroup createSequentialGroup() {
        return new SequentialGroup();
    }
    
    /**
     * Creates and returns a <code>ParallelGroup</code> with a
     * <code>LEADING</code> alignment.  This is a cover method for the more
     * general <code>createParallelGroup(int)</code> method.
     *
     * @return a new ParallelGroup
     * @see #createParallelGroup(int)
     */
    public ParallelGroup createParallelGroup() {
        return createParallelGroup(LEADING);
    }
    
    /**
     * Creates and returns an <code>ParallelGroup</code>.  The alignment
     * specifies how children elements should be positioned when the
     * the parallel group is given more space than necessary.  For example,
     * if a ParallelGroup with an alignment of TRAILING is given 100 pixels
     * and a child only needs 50 pixels, the child will be positioned at the
     * position 50.
     *
     * @param alignment alignment for the elements of the Group, one
     *        of <code>LEADING</code>, <code>TRAILING</code>,
     *        <code>CENTER</code> or <code>BASELINE</code>.
     * @throws IllegalArgumentException if alignment is not one of
     *         <code>LEADING</code>, <code>TRAILING</code>,
     *         <code>CENTER</code> or <code>BASELINE</code>
     * @return a new <code>ParallelGroup</code>
     */
    public ParallelGroup createParallelGroup(int alignment) {
        return createParallelGroup(alignment, true);
    }
    
    /**
     * Creates and returns an <code>ParallelGroup</code>.  The alignment
     * specifies how children elements should be positioned when the
     * the parallel group is given more space than necessary.  For example,
     * if a ParallelGroup with an alignment of TRAILING is given 100 pixels
     * and a child only needs 50 pixels, the child will be positioned at the
     * position 50.
     *
     * @param alignment alignment for the elements of the Group, one
     *        of <code>LEADING</code>, <code>TRAILING</code>,
     *        <code>CENTER</code> or <code>BASELINE</code>.
     * @param resizable whether or not the group is resizable.  If the group
     *        is not resizable the min/max size will be the same as the
     *        preferred.
     * @throws IllegalArgumentException if alignment is not one of
     *         <code>LEADING</code>, <code>TRAILING</code>,
     *         <code>CENTER</code> or <code>BASELINE</code>
     * @return a new <code>ParallelGroup</code>
     */
    public ParallelGroup createParallelGroup(int alignment, boolean resizable) {
        if (alignment == BASELINE) {
            return new BaselineGroup(resizable);
        }
        return new ParallelGroup(alignment, resizable);
    }
    
    /**
     * Creates and returns a <code>ParallelGroup</code> that aligns it's
     * elements along the baseline. 
     *
     * @param resizable whether the group is resizable
     * @param anchorBaselineToTop whether the baseline is anchored to
     *        the top or bottom of the group
     * @see #createBaselineGroup
     * @see ParallelGroup
     */
    public ParallelGroup createBaselineGroup(boolean resizable,
            boolean anchorBaselineToTop) {
        return new BaselineGroup(resizable, anchorBaselineToTop);
    }
    
    /**
     * Forces the set of components to have the same size.
     * This can be used multiple times to force
     * any number of components to share the same size.
     * <p>
     * Linked Components are not be resizable.
     *
     * @param components Components to force to have same size.
     * @throws IllegalArgumentException if <code>components</code> is
     *         null, or contains null.
     */
    public void linkSize(Component[] components) {
        linkSize(components, HORIZONTAL | VERTICAL);
    }
    
    /**
     * Forces the set of components to have the same size.
     * This can be used multiple times to force
     * any number of components to share the same size.
     * <p>
     * Linked Components are not be resizable.
     *
     * @param components Components to force to have same size.
     * @param axis Axis to bind size, one of HORIZONTAL, VERTICAL or
     *             HORIZONTAL | VERTICAL
     * @throws IllegalArgumentException if <code>components</code> is
     *         null, or contains null.
     * @throws IllegalArgumentException if <code>axis</code> does not
     *         contain <code>HORIZONTAL</code> or <code>VERTICAL</code>
     */
    public void linkSize(Component[] components, int axis) {
        if (components == null) {
            throw new IllegalArgumentException("Components must be non-null");
        }
        boolean horizontal = ((axis & HORIZONTAL) == HORIZONTAL);
        boolean vertical = ((axis & VERTICAL) == VERTICAL);
        if (!vertical && !horizontal) {
            throw new IllegalArgumentException(
                    "Axis must contain HORIZONTAL or VERTICAL");
        }
        for (int counter = components.length - 1; counter >= 0; counter--) {
            Component c = components[counter];
            if (components[counter] == null) {
                throw new IllegalArgumentException(
                        "Components must be non-null");
            }
            // Force the component to be added
            getComponentInfo(c);
        }
        if (horizontal) {
            linkSize0(components, HORIZONTAL);
        }
        if (vertical) {
            linkSize0(components, VERTICAL);
        }
        invalidateHost();
    }
    
    private void linkSize0(Component[] components, int axis) {
        LinkInfo master = getComponentInfo(
                components[components.length - 1]).getLinkInfo(axis);
        for (int counter = components.length - 2; counter >= 0; counter--) {
            master.add(getComponentInfo(components[counter]));
        }
    }

    /**
     * Removes an existing component replacing it with the specified component.
     *
     * @param existingComponent the Component that should be removed and
     *        replaced with newComponent
     * @param newComponent the Component to put in existingComponents place
     * @throws IllegalArgumentException is either of the Components are null or
     *         if existingComponent is not being managed by this layout manager
     */
    public void replace(Component existingComponent, Component newComponent) {
        if (existingComponent == null || newComponent == null) {
            throw new IllegalArgumentException("Components must be non-null");
        }
        // Make sure all the components have been registered, otherwise we may
        // not update the correct Springs.
        if (springsChanged) {
            registerComponents(horizontalGroup, HORIZONTAL);
            registerComponents(verticalGroup, VERTICAL);
        }
        ComponentInfo info = (ComponentInfo)componentInfos.
                remove(existingComponent);
        if (info == null) {
            throw new IllegalArgumentException("Component must already exist");
        }
        host.remove(existingComponent);
        if (newComponent.getParent() != host) {
            host.add(newComponent);
        }
        info.setComponent(newComponent);
        componentInfos.put(newComponent, info);
        invalidateHost();
    }

    /**
     * Sets the LayoutStyle this GroupLayout is to use. A value of null can
     * be used to indicate the shared instance of LayoutStyle should be used.
     *
     * @param layoutStyle the LayoutStyle to use
     */
    public void setLayoutStyle(LayoutStyle layoutStyle) {
        this.layoutStyle = layoutStyle;
        invalidateHost();
    }
    
    /**
     * Returns the LayoutStyle instance to use
     *
     * @return the LayoutStyle instance to use
     */
    public LayoutStyle getLayoutStyle() {
        return layoutStyle;
    }
    
    private LayoutStyle getLayoutStyle0() {
        LayoutStyle layoutStyle = getLayoutStyle();
        if (layoutStyle == null) {
            layoutStyle = LayoutStyle.getSharedInstance();
        }
        return layoutStyle;
    }
    
    private void invalidateHost() {
        if (host instanceof JComponent) {
            ((JComponent)host).revalidate();
        } else {
            host.invalidate();
        }
        host.repaint();
    }

    //
    // LayoutManager
    //
    /**
     * Notification that a <code>Component</code> has been added to
     * the parent container.  Developers should not invoke this method
     * directly, instead you should use one of the <code>Group</code>
     * methods to add a <code>Component</code>.
     *
     * @param name the string to be associated with the component
     * @param component the <code>Component</code> to be added
     */
    public void addLayoutComponent(String name, Component component) {
    }
    
    /**
     * Notification that a <code>Component</code> has been removed from
     * the parent container.  You should not invoke this method
     * directly, instead invoke <code>remove</code> on the parent
     * <code>Container</code>.
     *
     * @param component the component to be removed
     * @see java.awt.Component#remove
     */
    public void removeLayoutComponent(Component component) {
        ComponentInfo info = (ComponentInfo)componentInfos.remove(component);
        if (info != null) {
            info.dispose();
            springsChanged = true;
            isValid = false;
        }
    }
    
    /**
     * Returns the preferred size for the specified container.
     *
     * @param parent the container to return size for
     * @throws IllegalArgumentException if <code>parent</code> is not
     *         the same <code>Container</code> that this was created with
     * @throws IllegalStateException if any of the components added to
     *         this layout are not in both a horizontal and vertical group
     * @see java.awt.Container#getPreferredSize
     */
    public Dimension preferredLayoutSize(Container parent) {
        checkParent(parent);
        prepare(PREF_SIZE);
        return adjustSize(horizontalGroup.getPreferredSize(HORIZONTAL),
                verticalGroup.getPreferredSize(VERTICAL));
    }
    
    /**
     * Returns the minimum size for the specified container.
     *
     * @param parent the container to return size for
     * @throws IllegalArgumentException if <code>parent</code> is not
     *         the same <code>Container</code> that this was created with
     * @throws IllegalStateException if any of the components added to
     *         this layout are not in both a horizontal and vertical group
     * @see java.awt.Container#getMinimumSize
     */
    public Dimension minimumLayoutSize(Container parent) {
        checkParent(parent);
        prepare(MIN_SIZE);
        return adjustSize(horizontalGroup.getMinimumSize(HORIZONTAL),
                verticalGroup.getMinimumSize(VERTICAL));
    }
    
    /**
     * Lays out the specified container.
     *
     * @param parent the container to be laid out
     * @throws IllegalStateException if any of the components added to
     *         this layout are not in both a horizontal and vertical group
     */
    public void layoutContainer(Container parent) {
        // Step 1: Prepare for layout.
        prepare(SPECIFIC_SIZE);
        Insets insets = parent.getInsets();
        int width = parent.getWidth() - insets.left - insets.right;
        int height = parent.getHeight() - insets.top - insets.bottom;
        boolean ltr = isLeftToRight();
        if (getAutocreateGaps() || getAutocreateContainerGaps() ||
                hasPreferredPaddingSprings) {
            // Step 2: Calculate autopadding springs
            calculateAutopadding(horizontalGroup, HORIZONTAL, SPECIFIC_SIZE, 0,
                    width);
            calculateAutopadding(verticalGroup, VERTICAL, SPECIFIC_SIZE, 0,
                    height);
        }
        // Step 3: set the size of the groups.
        horizontalGroup.setSize(HORIZONTAL, 0, width);
        verticalGroup.setSize(VERTICAL, 0, height);
        // Step 4: apply the size to the components.
        Iterator componentInfo = componentInfos.values().iterator();
        while (componentInfo.hasNext()) {
            ComponentInfo info = (ComponentInfo)componentInfo.next();
            Component c = info.getComponent();
            info.setBounds(insets, width, ltr);
        }
    }
    
    //
    // LayoutManager2
    //
    /**
     * Notification that a <code>Component</code> has been added to
     * the parent container.  You should not invoke this method
     * directly, instead you should use one of the <code>Group</code>
     * methods to add a <code>Component</code>.
     *
     * @param component The component added
     * @param constraints Description of where to place the component.
     */
    public void addLayoutComponent(Component component, Object constraints) {
    }
    
    /**
     * Returns the maximum size for the specified container.
     *
     * @param parent the container to return size for
     * @throws IllegalArgumentException if <code>parent</code> is not
     *         the same <code>Container</code> that this was created with
     * @throws IllegalStateException if any of the components added to
     *         this layout are not in both a horizontal and vertical group
     * @see java.awt.Container#getMaximumSize
     */
    public Dimension maximumLayoutSize(Container parent) {
        checkParent(parent);
        prepare(MAX_SIZE);
        return adjustSize(horizontalGroup.getMaximumSize(HORIZONTAL),
                verticalGroup.getMaximumSize(VERTICAL));
    }
    
    /**
     * Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     *
     * @param parent Container hosting this LayoutManager
     * @throws IllegalArgumentException if <code>parent</code> is not
     *         the same <code>Container</code> that this was created with
     * @return alignment
     */
    public float getLayoutAlignmentX(Container parent) {
        checkParent(parent);
        return .5f;
    }
    
    /**
     * Returns the alignment along the y axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     *
     * @param parent Container hosting this LayoutManager
     * @throws IllegalArgumentException if <code>parent</code> is not
     *         the same <code>Container</code> that this was created with
     * @return alignment
     */
    public float getLayoutAlignmentY(Container parent) {
        checkParent(parent);
        return .5f;
    }
    
    /**
     * Invalidates the layout, indicating that if the layout manager
     * has cached information it should be discarded.
     *
     * @param parent Container hosting this LayoutManager
     * @throws IllegalArgumentException if <code>parent</code> is not
     *         the same <code>Container</code> that this was created with
     */
    public void invalidateLayout(Container parent) {
        checkParent(parent);
        // invalidateLayout is called from Container.invalidate, which
        // does NOT grab the treelock.  All other methods do.  To make sure
        // there aren't any possible threading problems we grab the tree lock
        // here.
        synchronized(parent.getTreeLock()) {
            isValid = false;
        }
    }
    
    private void prepare(int sizeType) {
        boolean visChanged = false;
        // Step 1: If not-valid, clear springs and update visibility.
        if (!isValid) {
            isValid = true;
            horizontalGroup.setSize(HORIZONTAL, UNSET, UNSET);
            verticalGroup.setSize(VERTICAL, UNSET, UNSET);
            for (Iterator cis = componentInfos.values().iterator();
                     cis.hasNext();) {
                ComponentInfo ci = (ComponentInfo)cis.next();
                if (ci.updateVisibility()) {
                    visChanged = true;
                }
                ci.clearCachedSize();
            }
        }
        // Step 2: Make sure components are bound to ComponentInfos
        if (springsChanged) {
            registerComponents(horizontalGroup, HORIZONTAL);
            registerComponents(verticalGroup, VERTICAL);
        }
        // Step 3: Adjust the autopadding. This removes existing
        // autopadding, then recalculates where it should go.
        if (springsChanged || visChanged) {
            checkComponents();
            horizontalGroup.removeAutopadding();
            verticalGroup.removeAutopadding();
            if (getAutocreateGaps()) {
                insertAutopadding(true);
            } else if (hasPreferredPaddingSprings ||
                    getAutocreateContainerGaps()) {
                insertAutopadding(false);
            }
            springsChanged = false;
        }
        // Step 4: (for min/pref/max size calculations only) calculate the
        // autopadding. This invokes for unsetting the calculated values, then
        // recalculating them.
        // If sizeType == SPECIFIC_SIZE, it indicates we're doing layout, this
        // step will be done later on.
        if (sizeType != SPECIFIC_SIZE && (getAutocreateGaps() ||
                getAutocreateContainerGaps() || hasPreferredPaddingSprings)) {
            calculateAutopadding(horizontalGroup, HORIZONTAL, sizeType, 0, 0);
            calculateAutopadding(verticalGroup, VERTICAL, sizeType, 0, 0);
        }
    }

    private void calculateAutopadding(Group group, int axis, int sizeType,
            int origin, int size) {
        group.unsetAutopadding();
        switch(sizeType) {
            case MIN_SIZE:
                size = group.getMinimumSize(axis);
                break;
            case PREF_SIZE:
                size = group.getPreferredSize(axis);
                break;
            case MAX_SIZE:
                size = group.getMaximumSize(axis);
                break;
        }
        group.setSize(axis, origin, size);
        group.calculateAutopadding(axis);
    }
    
    private void checkComponents() {
        Iterator infos = componentInfos.values().iterator();
        while (infos.hasNext()) {
            ComponentInfo info = (ComponentInfo)infos.next();
            if (info.horizontalSpring == null) {
                throw new IllegalStateException(info.component +
                        " is not attached to a horizontal group");
            }
            if (info.verticalSpring == null) {
                throw new IllegalStateException(info.component +
                        " is not attached to a vertical group");
            }
        }
    }
    
    private void registerComponents(Group group, int axis) {
        List springs = group.springs;
        for (int counter = springs.size() - 1; counter >= 0; counter--) {
            Spring spring = (Spring)springs.get(counter);
            if (spring instanceof ComponentSpring) {
                ((ComponentSpring)spring).installIfNecessary(axis);
            } else if (spring instanceof Group) {
                registerComponents((Group)spring, axis);
            }
        }
    }
    
    private Dimension adjustSize(int width, int height) {
        Insets insets = host.getInsets();
        return new Dimension(width + insets.left + insets.right,
                height + insets.top + insets.bottom);
    }
    
    private void checkParent(Container parent) {
        if (parent != host) {
            throw new IllegalArgumentException(
                    "GroupLayout can only be used with one Container at a time");
        }
    }
    
    /**
     * Returns the <code>ComponentInfo</code> for the specified Component.
     */
    private ComponentInfo getComponentInfo(Component component) {
        ComponentInfo info = (ComponentInfo)componentInfos.get(component);
        if (info == null) {
            info = new ComponentInfo(component);
            componentInfos.put(component, info);
            if (component.getParent() != host) {
                host.add(component);
            }
        }
        return info;
    }
    
    /**
     * Adjusts the autopadding springs for the horizontal and vertical
     * groups.  If <code>insert</code> is true this will insert auto padding
     * springs, otherwise this will only adjust the springs that
     * comprise auto preferred padding springs.
     */
    private void insertAutopadding(boolean insert) {
        horizontalGroup.insertAutopadding(HORIZONTAL, new ArrayList(1),
                new ArrayList(1), new ArrayList(1), new ArrayList(1), insert);
        verticalGroup.insertAutopadding(VERTICAL, new ArrayList(1),
                new ArrayList(1), new ArrayList(1), new ArrayList(1), insert);
    }
    
    /**
     * Returns true if the two Components have a common ParallelGroup ancestor
     * along the particular axis.
     */
    private boolean areParallelSiblings(Component source, Component target,
            int axis) {
        ComponentInfo sourceInfo = getComponentInfo(source);
        ComponentInfo targetInfo = getComponentInfo(target);
        Spring sourceSpring;
        Spring targetSpring;
        if (axis == HORIZONTAL) {
            sourceSpring = sourceInfo.horizontalSpring;
            targetSpring = targetInfo.horizontalSpring;
        } else {
            sourceSpring = sourceInfo.verticalSpring;
            targetSpring = targetInfo.verticalSpring;
        }
        Set sourcePath = tmpParallelSet;
        sourcePath.clear();
        Spring spring = sourceSpring.getParent();
        while (spring != null) {
            sourcePath.add(spring);
            spring = spring.getParent();
        }
        spring = targetSpring.getParent();
        while (spring != null) {
            if (sourcePath.contains(spring)) {
                sourcePath.clear();
                while (spring != null) {
                    if (spring instanceof ParallelGroup) {
                        return true;
                    }
                    spring = spring.getParent();
                }
                return false;
            }
            spring = spring.getParent();
        }
        sourcePath.clear();
        return false;
    }
    
    private boolean isLeftToRight() {
        return host.getComponentOrientation().isLeftToRight();
    }
    
    /**
     * Spring consists of a range: min, pref and max a value some where in
     * the middle of that and a location.  Subclasses must override
     * methods to get the min/max/pref and will likely want to override
     * the <code>setSize</code> method.  Spring automatically caches the
     * min/max/pref.  If the min/pref/max has internally changes, or needs
     * to be updated you must invoked clear.
     */
    abstract class Spring {
        private int size;
        private int min;
        private int max;
        private int pref;
        private Spring parent;
        
        private int alignment;
        
        Spring() {
            min = pref = max = UNSET;
        }
        
        /**
         * Calculates and returns the minimum size.
         *
         * @param axis the axis of layout; one of HORIZONTAL or VERTICAL
         * @return the minimum size
         */
        abstract int calculateMinimumSize(int axis);
        
        /**
         * Calculates and returns the preferred size.
         *
         * @param axis the axis of layout; one of HORIZONTAL or VERTICAL
         * @return the preferred size
         */
        abstract int calculatePreferredSize(int axis);
        
        /**
         * Calculates and returns the minimum size.
         *
         * @param axis the axis of layout; one of HORIZONTAL or VERTICAL
         * @return the minimum size
         */
        abstract int calculateMaximumSize(int axis);
        
        /**
         * Sets the parent of this Spring.
         */
        void setParent(Spring parent) {
            this.parent = parent;
        }
        
        /**
         * Returns the parent of this spring.
         */
        Spring getParent() {
            return parent;
        }
        
        // This is here purely as a conveniance for ParallelGroup to avoid
        // having to track alignment separately.
        void setAlignment(int alignment) {
            this.alignment = alignment;
        }
        
        int getAlignment() {
            return alignment;
        }
        
        /**
         * Returns the minimum size.
         */
        final int getMinimumSize(int axis) {
            if (min == UNSET) {
                min = constrain(calculateMinimumSize(axis));
            }
            return min;
        }
        
        /**
         * Returns the preferred size.
         */
        final int getPreferredSize(int axis) {
            if (pref == UNSET) {
                pref = constrain(calculatePreferredSize(axis));
            }
            return pref;
        }
        
        /**
         * Returns the maximum size.
         */
        final int getMaximumSize(int axis) {
            if (max == UNSET) {
                max = constrain(calculateMaximumSize(axis));
            }
            return max;
        }
        
        /**
         * Resets the cached min/max/pref.
         */
        void unset() {
            size = min = pref = max = UNSET;
        }
        
        /**
         * Sets the value and location of the spring.  Subclasses
         * will want to invoke super, then do any additional sizing.
         *
         * @param axis HORIZONTAL or VERTICAL
         * @param origin of this Spring
         * @param size of the Spring.  If size is UNSET, this invokes
         *        clear.
         */
        void setSize(int axis, int origin, int size) {
            this.size = size;
            if (size == UNSET) {
                unset();
            }
        }
        
        /**
         * Returns the current size.
         */
        int getSize() {
            return size;
        }
        
        int constrain(int value) {
            return Math.min(value, Short.MAX_VALUE);
        }

        int getBaseline() {
            return -1;
        }
        
        int getBaselineResizeBehavior() {
            return Baseline.BRB_OTHER;
        }
        
        final boolean isResizable(int axis) {
            int min = getMinimumSize(axis);
            int pref = getPreferredSize(axis);
            return (min != pref || pref != getMaximumSize(axis));
        }
        
        /**
         * Returns true if this Spring will ALWAYS have a zero size. This should 
         * NOT check the current size, rather it's meant to 
         * quickly test if this Spring will always have a zero size.
         */
        abstract boolean willHaveZeroSize(boolean treatAutopaddingAsZeroSized);
    }
    
    
    /**
     * Group provides for commonality between the two types of operations
     * supported by <code>GroupLayout</code>: laying out components one
     * after another (<code>SequentialGroup</code>) or layout on top
     * of each other (<code>ParallelGroup</code>). Use one of
     * <code>createSequentialGroup</code> or
     * <code>createParallelGroup</code> to create one.
     */
    public abstract class Group extends Spring {
        // private int origin;
        // private int size;
        List springs;
        
        Group() {
            springs = new ArrayList();
        }
        
        int indexOf(Spring spring) {
            return springs.indexOf(spring);
        }
        
        /**
         * Adds the Spring to the list of <code>Spring</code>s and returns
         * the receiver.
         */
        Group addSpring(Spring spring) {
            springs.add(spring);
            spring.setParent(this);
            if (!(spring instanceof AutopaddingSpring) ||
                    !((AutopaddingSpring)spring).getUserCreated()) {
                springsChanged = true;
            }
            return this;
        }
        
        //
        // Spring methods
        //
        
        void setSize(int axis, int origin, int size) {
            super.setSize(axis, origin, size);
            if (size == UNSET) {
                for (int counter = springs.size() - 1; counter >= 0;
                     counter--) {
                    getSpring(counter).setSize(axis, origin, size);
                }
            } else {
                setValidSize(axis, origin, size);
            }
        }
        
        /**
         * This is invoked from <code>setSize</code> if passed a value
         * other than UNSET.
         */
        abstract void setValidSize(int axis, int origin, int size);
        
        int calculateMinimumSize(int axis) {
            return calculateSize(axis, MIN_SIZE);
        }
        
        int calculatePreferredSize(int axis) {
            return calculateSize(axis, PREF_SIZE);
        }
        
        int calculateMaximumSize(int axis) {
            return calculateSize(axis, MAX_SIZE);
        }
        
        /**
         * Used to compute how the two values representing two springs
         * will be combined.  For example, a group that layed things out
         * one after the next would return <code>a + b</code>.
         */
        abstract int operator(int a, int b);
        
        /**
         * Calculates the specified size.  This is called from
         * one of the <code>getMinimumSize0</code>,
         * <code>getPreferredSize0</code> or
         * <code>getMaximumSize0</code> methods.  This will invoke
         * to <code>operator</code> to combine the values.
         */
        int calculateSize(int axis, int type) {
            int count = springs.size();
            if (count == 0) {
                return 0;
            }
            if (count == 1) {
                return getSpringSize(getSpring(0), axis, type);
            }
            int size = constrain(operator(getSpringSize(getSpring(0), axis, type),
                    getSpringSize(getSpring(1), axis, type)));
            for (int counter = 2; counter < count; counter++) {
                size = constrain(operator(size, getSpringSize(getSpring(counter),
                        axis, type)));
            }
            return size;
        }
        
        Spring getSpring(int index) {
            return (Spring)springs.get(index);
        }
        
        int getSpringSize(Spring spring, int axis, int type) {
            switch(type) {
                case MIN_SIZE:
                    return spring.getMinimumSize(axis);
                case PREF_SIZE:
                    return spring.getPreferredSize(axis);
                case MAX_SIZE:
                    return spring.getMaximumSize(axis);
            }
            assert false;
            return 0;
        }
        
        // Padding
        /**
         * Adjusts the autopadding springs in this group and its children.
         * If <code>insert</code> is true this will insert auto padding
         * springs, otherwise this will only adjust the springs that
         * comprise auto preferred padding springs.
         *
         * @param axis the axis of the springs; HORIZONTAL or VERTICAL
         * @param leadingPadding List of AutopaddingSprings that occur before
         *                       this Group
         * @param trailingPadding any trailing autopadding springs are added
         *                        to this on exit
         * @param leading List of ComponentSprings that occur before this Group
         * @param trailing any trailing ComponentSpring are added to this
         *                 List
         * @param insert Whether or not to insert AutopaddingSprings or just
         *               adjust any existing AutopaddingSprings.
         */
        abstract void insertAutopadding(int axis, List leadingPadding,
                List trailingPadding, List leading, List trailing,
                boolean insert);
        
        /**
         * Removes any AutopaddingSprings.
         */
        void removeAutopadding() {
            unset();
            for (int counter = springs.size() - 1; counter >= 0; counter--) {
                Spring spring = (Spring)springs.get(counter);
                if (spring instanceof AutopaddingSpring) {
                    if (((AutopaddingSpring)spring).getUserCreated()) {
                        ((AutopaddingSpring)spring).reset();
                    } else {
                        springs.remove(counter);
                    }
                } else if (spring instanceof Group) {
                    ((Group)spring).removeAutopadding();
                }
            }
        }
        
        void unsetAutopadding() {
            // Clear cached pref/min/max.
            unset();
            for (int counter = springs.size() - 1; counter >= 0; counter--) {
                Spring spring = (Spring)springs.get(counter);
                if (spring instanceof AutopaddingSpring) {
                    ((AutopaddingSpring)spring).unset();
                } else if (spring instanceof Group) {
                    ((Group)spring).unsetAutopadding();
                }
            }
        }
        
        void calculateAutopadding(int axis) {
            for (int counter = springs.size() - 1; counter >= 0; counter--) {
                Spring spring = (Spring)springs.get(counter);
                if (spring instanceof AutopaddingSpring) {
                    // Force size to be reset.
                    spring.unset();
                    ((AutopaddingSpring)spring).calculatePadding(axis);
                } else if (spring instanceof Group) {
                    ((Group)spring).calculateAutopadding(axis);
                }
            }
            // Clear cached pref/min/max.
            unset();
        }
        
        boolean willHaveZeroSize(boolean treatAutopaddingAsZeroSized) {
            for (int i = springs.size() -1; i >= 0; i--) {
                Spring spring = (Spring)springs.get(i);
                if (!spring.willHaveZeroSize(treatAutopaddingAsZeroSized)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    
    /**
     * A <code>Group</code> that lays out its elements sequentially, one
     * after another.  This class has no public constructor, use the
     * <code>createSequentialGroup</code> method to create one.
     *
     * @see #createSequentialGroup()
     */
    public class SequentialGroup extends Group {
        private Spring baselineSpring;
        
        SequentialGroup() {
        }
        
        /**
         * Adds the specified <code>Group</code> to this
         * <code>SequentialGroup</code>
         *
         * @param group the Group to add
         * @return this Group
         */
        public SequentialGroup add(Group group) {
            return (SequentialGroup)addSpring(group);
        }
        
        /**
         * Adds a <code>Group</code> to this <code>Group</code>.
         *
         * @param group the <code>Group</code> to add
         * @param useAsBaseline whether the specified <code>Group</code> should
         *        be used to calculate the baseline for this <code>Group</code>
         * @return this <code>Group</code>
         */
        public SequentialGroup add(boolean useAsBaseline, Group group) {
            add(group);
            if (useAsBaseline) {
                baselineSpring = group;
            }
            return this;
        }
        
        /**
         * Adds the specified Component.  If the Component's min/max
         * are different from its pref than the component will be resizable.
         *
         * @param component the Component to add
         * @return this <code>SequentialGroup</code>
         */
        public SequentialGroup add(Component component) {
            return add(component, DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_SIZE);
        }
        
        /**
         * Adds a <code>Component</code> to this <code>Group</code>.
         *
         * @param useAsBaseline whether the specified <code>Component</code> should
         *        be used to calculate the baseline for this <code>Group</code>
         * @param component the <code>Component</code> to add
         * @return this <code>Group</code>
         */
        public SequentialGroup add(boolean useAsBaseline, Component component) {
            add(component);
            if (useAsBaseline) {
                baselineSpring = getSpring(springs.size() - 1);
            }
            return this;
        }

        /**
         * Adds the specified <code>Component</code>.  Min, pref and max
         * can be absolute values, or they can be one of
         * <code>DEFAULT_SIZE</code> or <code>PREFERRED_SIZE</code>.  For
         * example, the following:
         * <pre>
         *   add(component, PREFERRED_SIZE, PREFERRED_SIZE, 1000);
         * </pre>
         * Forces a max of 1000, with the min and preferred equalling that
         * of the preferred size of <code>component</code>.
         *
         * @param component the Component to add
         * @param min the minimum size
         * @param pref the preferred size
         * @param max the maximum size
         * @throws IllegalArgumentException if min, pref or max are
         *         not positive and not one of PREFERRED_SIZE or DEFAULT_SIZE
         * @return this <code>SequentialGroup</code>
         */
        public SequentialGroup add(Component component, int min, int pref,
                int max) {
            return (SequentialGroup)addSpring(new ComponentSpring(
                    component, min, pref, max));
        }
        
        /**
         * Adds a <code>Component</code> to this <code>Group</code>
         * with the specified size.
         *
         * @param useAsBaseline whether the specified <code>Component</code> should
         *        be used to calculate the baseline for this <code>Group</code>
         * @param component the <code>Component</code> to add
         * @param min the minimum size or one of <code>DEFAULT_SIZE</code> or
         *            <code>PREFERRED_SIZE</code>
         * @param pref the preferred size or one of <code>DEFAULT_SIZE</code> or
         *            <code>PREFERRED_SIZE</code>
         * @param max the maximum size or one of <code>DEFAULT_SIZE</code> or
         *            <code>PREFERRED_SIZE</code>
         * @return this <code>Group</code>
         */
        public SequentialGroup add(boolean useAsBaseline,
                Component component, int min, int pref, int max) {
            add(component, min, pref, max);
            if (useAsBaseline) {
                baselineSpring = getSpring(springs.size() - 1);
            }
            return this;
        }

        /**
         * Adds a rigid gap.
         *
         * @param pref the size of the gap
         * @throws IllegalArgumentException if min < 0 or pref < 0 or max < 0
         *         or the following is not meant min <= pref <= max
         * @return this <code>SequentialGroup</code>
         */
        public SequentialGroup add(int pref) {
            return add(pref, pref, pref);
        }
        
        /**
         * Adds a gap with the specified size.
         *
         * @param min the minimum size of the gap, or PREFERRED_SIZE
         * @param pref the preferred size of the gap
         * @param max the maximum size of the gap, or PREFERRED_SIZE
         * @throws IllegalArgumentException if min < 0 or pref < 0 or max < 0
         *         or the following is not meant min <= pref <= max
         * @return this <code>SequentialGroup</code>
         */
        public SequentialGroup add(int min, int pref, int max) {
            return (SequentialGroup)addSpring(new GapSpring(min, pref, max));
        }
        
        /**
         * Adds an element representing the preferred gap between the two
         * components.
         * 
         * @param comp1 the first component
         * @param comp2 the second component
         * @param type the type of gap; one of the constants defined by
         *        LayoutStyle
         * @return this <code>SequentialGroup</code>
         * @throws IllegalArgumentException if <code>type</code> is not a
         *         valid LayoutStyle constant
         * @see LayoutStyle
         */
        public SequentialGroup addPreferredGap(JComponent comp1,
                JComponent comp2,
                int type) {
            return addPreferredGap(comp1, comp2, type, false);
        }
        
        /**
         * Adds an element representing the preferred gap between the two
         * components.
         * 
         * @param comp1 the first component
         * @param comp2 the second component
         * @param type the type of gap; one of the constants defined by
         *        LayoutStyle
         * @param canGrow true if the gap can grow if more
         *                space is available
         * @return this <code>SequentialGroup</code>
         * @throws IllegalArgumentException if <code>type</code> is not a
         *         valid LayoutStyle constant
         * @see LayoutStyle
         */
        public SequentialGroup addPreferredGap(JComponent comp1,
                JComponent comp2,
                int type, boolean canGrow) {
            if (type != LayoutStyle.RELATED &&
                    type != LayoutStyle.UNRELATED &&
                    type != LayoutStyle.INDENT) {
                throw new IllegalArgumentException("Invalid type argument");
            }
            if (comp1 == null || comp2 == null) {
                throw new IllegalArgumentException(
                        "Components must be non-null");
            }
            return (SequentialGroup)addSpring(new PaddingSpring(
                    comp1, comp2, type, canGrow));
        }

        /**
         * Adds an element representing the preferred gap between the
         * nearest components.  That is, during layout the neighboring
         * components are found, and the min, pref and max of this
         * element is set based on the preferred gap between the
         * components.  If no neighboring components are found the
         * min, pref and max are set to 0.
         * 
         * @param type the type of gap; one of the LayoutStyle constants
         * @return this SequentialGroup
         * @throws IllegalArgumentException if type is not one of
         *         <code>LayoutStyle.RELATED</code> or
         *         <code>LayoutStyle.UNRELATED</code>
         * @see LayoutStyle
         */
        public SequentialGroup addPreferredGap(int type) {
            return addPreferredGap(type, DEFAULT_SIZE, DEFAULT_SIZE);
        }
        
        /**
         * Adds an element for the preferred gap between the
         * nearest components.  That is, during layout the neighboring
         * components are found, and the min of this
         * element is set based on the preferred gap between the
         * components.  If no neighboring components are found the
         * min is set to 0.  This method allows you to specify the
         * preferred and maximum size by way of the <code>pref</code>
         * and <code>max</code> arguments.  These can either be a
         * value &gt;= 0, in which case the preferred or max is the max
         * of the argument and the preferred gap, of DEFAULT_VALUE in
         * which case the value is the same as the preferred gap.
         * 
         * @param type the type of gap; one of LayoutStyle.RELATED or
         *        LayoutStyle.UNRELATED
         * @param pref the preferred size; one of DEFAULT_SIZE or a value > 0
         * @param max the maximum size; one of DEFAULT_SIZE, PREFERRED_SIZE
         *        or a value > 0
         * @return this SequentialGroup
         * @throws IllegalArgumentException if type is not one of
         *         <code>LayoutStyle.RELATED</code> or
         *         <code>LayoutStyle.UNRELATED</code> or pref/max is
         *         != DEFAULT_SIZE and < 0, or pref > max
         * @see LayoutStyle
         */
        public SequentialGroup addPreferredGap(int type, int pref,
                                                   int max) {
            if (type != LayoutStyle.RELATED && type != LayoutStyle.UNRELATED) {
                throw new IllegalArgumentException(
                        "Padding type must be one of Padding.RELATED or Padding.UNRELATED");
            }
            if ((pref < 0 && pref != DEFAULT_SIZE && pref != PREFERRED_SIZE) ||
                    (max < 0 && max != DEFAULT_SIZE && max != PREFERRED_SIZE)||
                    (pref >= 0 && max >= 0 && pref > max)) {
                throw new IllegalArgumentException(
                        "Pref and max must be either DEFAULT_SIZE, " +
                        "PREFERRED_SIZE, or >= 0 and pref <= max");
            }
            hasPreferredPaddingSprings = true;
            return (SequentialGroup)addSpring(new AutopaddingSpring(
                                       type, pref, max));
        }
        
        /**
         * Adds an element representing the preferred gap between one edge
         * of the container and the next/previous Component.  This will have
         * no effect if the next/previous element is not a Component and does
         * not touch one edge of the parent container. 
         *
         * @return this <code>SequentialGroup</code>.
         */
        public SequentialGroup addContainerGap() {
            return addContainerGap(DEFAULT_SIZE, DEFAULT_SIZE);
        }
        
        /**
         * Adds an element representing the preferred gap between one edge
         * of the container and the next/previous Component.  This will have
         * no effect if the next/previous element is not a Component and does
         * not touch one edge of the parent container. 
         *
         * @param pref the preferred size; one of DEFAULT_SIZE or a value > 0
         * @param max the maximum size; one of DEFAULT_SIZE, PREFERRED_SIZE
         *        or a value > 0.
         * @throws IllegalArgumentException if pref/max is
         *         != DEFAULT_SIZE and < 0, or pref > max
         * @return this <code>SequentialGroup</code>
         */
        public SequentialGroup addContainerGap(int pref, int max) {
            if ((pref < 0 && pref != DEFAULT_SIZE) ||
                    (max < 0 && max != DEFAULT_SIZE && max != PREFERRED_SIZE) ||
                    (pref >= 0 && max >= 0 && pref > max)) {
                throw new IllegalArgumentException(
                        "Pref and max must be either DEFAULT_VALUE or >= 0 and pref <= max");
            }
            hasPreferredPaddingSprings = true;
            return (SequentialGroup)addSpring(
                    new ContainerAutopaddingSpring(pref, max));
        }
        
        int operator(int a, int b) {
            return constrain(a) + constrain(b);
        }
        
        void setValidSize(int axis, int origin, int size) {
            int pref = getPreferredSize(axis);
            if (size == pref) {
                for (int counter = 0, max = springs.size(); counter < max;
                counter++) {
                    Spring spring = getSpring(counter);
                    int springPref = spring.getPreferredSize(axis);
                    spring.setSize(axis, origin, springPref);
                    origin += springPref;
                }
            } else if (springs.size() == 1) {
                Spring spring = getSpring(0);
                spring.setSize(axis, origin, Math.min(
                        Math.max(size, spring.getMinimumSize(axis)),
                        spring.getMaximumSize(axis)));
            } else if (springs.size() > 1) {
                // Adjust between min/pref
                setValidSizeNotPreferred(axis, origin, size);
            }
        }
        
        private void setValidSizeNotPreferred(int axis, int origin, int size) {
            int delta = size - getPreferredSize(axis);
            assert delta != 0;
            boolean useMin = (delta < 0);
            int springCount = springs.size();
            if (useMin) {
                delta *= -1;
            }
            
            // The following algorithm if used for resizing springs:
            // 1. Calculate the resizability of each spring (pref - min or
            //    max - pref) into a list.
            // 2. Sort the list in ascending order
            // 3. Iterate through each of the resizable Springs, attempting
            //    to give them (pref - size) / resizeCount
            // 4. For any Springs that can not accomodate that much space
            //    add the remainder back to the amount to distribute and
            //    recalculate how must space the remaining springs will get.
            // 5. Set the size of the springs.

            // First pass, sort the resizable springs into resizable
            List resizable = buildResizableList(axis, useMin);
            int resizableCount = resizable.size();
            
            if (resizableCount > 0) {
                // How much we would like to give each Spring.
                int sDelta = delta / resizableCount;
                // Remaining space.
                int slop = delta - sDelta * resizableCount;
                int[] sizes = new int[springCount];
                int sign = useMin ? -1 : 1;
                // Second pass, accumulate the resulting deltas (relative to
                // preferred) into sizes.
                for (int counter = 0; counter < resizableCount; counter++) {
                    SpringDelta springDelta = (SpringDelta)resizable.
                            get(counter);
                    if ((counter + 1) == resizableCount) {
                        sDelta += slop;
                    }
                    springDelta.delta = Math.min(sDelta, springDelta.delta);
                    delta -= springDelta.delta;
                    if (springDelta.delta != sDelta && counter + 1 <
                            resizableCount) {
                        // Spring didn't take all the space, reset how much
                        // each spring will get.
                        sDelta = delta / (resizableCount - counter - 1);
                        slop = delta - sDelta * (resizableCount - counter - 1);
                    }
                    sizes[springDelta.index] = sign * springDelta.delta;
                }
                
                // And finally set the size of each spring
                for (int counter = 0; counter < springCount; counter++) {
                    Spring spring = getSpring(counter);
                    int sSize = spring.getPreferredSize(axis) + sizes[counter];
                    spring.setSize(axis, origin, sSize);
                    origin += sSize;
                }
            } else {
                // Nothing resizable, use the min or max of each of the
                // springs.
                for (int counter = 0; counter < springCount; counter++) {
                    Spring spring = getSpring(counter);
                    int sSize;
                    if (useMin) {
                        sSize = spring.getMinimumSize(axis);
                    } else {
                        sSize = spring.getMaximumSize(axis);
                    }
                    spring.setSize(axis, origin, sSize);
                    origin += sSize;
                }
            }
        }
        
        /**
         * Returns the sorted list of SpringDelta's for the current set of
         * Springs.
         */
        private List buildResizableList(int axis, boolean useMin) {
            // First pass, figure out what is resizable
            int size = springs.size();
            List sorted = new ArrayList(size);
            for (int counter = 0; counter < size; counter++) {
                Spring spring = getSpring(counter);
                int sDelta;
                if (useMin) {
                    sDelta = spring.getPreferredSize(axis) -
                            spring.getMinimumSize(axis);
                } else {
                    sDelta = spring.getMaximumSize(axis) -
                            spring.getPreferredSize(axis);
                }
                if (sDelta > 0) {
                    sorted.add(new SpringDelta(counter, sDelta));
                }
            }
            Collections.sort(sorted);
            return sorted;
        }
        
        private int indexOfNextNonZeroSpring(int index, boolean treatAutopaddingAsZeroSized) {
            while (index < springs.size()) {
                Spring spring = (Spring)springs.get(index);
                if (!((Spring)spring).willHaveZeroSize(treatAutopaddingAsZeroSized)) {
                    return index;
                }
                index++;
            }
            return index;
        }

        void insertAutopadding(int axis, List leadingPadding,
                List trailingPadding, List leading, List trailing,
                boolean insert) {
            List newLeadingPadding = new ArrayList(leadingPadding);
            List newTrailingPadding = new ArrayList(1);
            List newLeading = new ArrayList(leading);
            List newTrailing = null;
            int counter = 0;
            // Warning, this must use springs.size, as it may change during the
            // loop.
            while (counter < springs.size()) {
                Spring spring = getSpring(counter);
                if (spring instanceof AutopaddingSpring) {
                    if (newLeadingPadding.size() == 0) {
                        AutopaddingSpring padding = (AutopaddingSpring)spring;
                        padding.setSources(newLeading);
                        newLeading.clear();
                        int nextCounter = indexOfNextNonZeroSpring(counter + 1, true);
                        if (nextCounter == springs.size()) {
                            // Last spring in the list, add it to trailingPadding.
                            if (!(padding instanceof ContainerAutopaddingSpring)) {
                                trailingPadding.add(padding);
                            }
                        } else {
                            newLeadingPadding.clear();
                            newLeadingPadding.add(padding);
                        }
                        counter = nextCounter;
                    } else {
                        counter = indexOfNextNonZeroSpring(counter + 1, true);
                    }
                } else {
                    // Not a padding spring
                    if (newLeading.size() > 0 && insert) {
                        // There's leading ComponentSprings, create an
                        // autopadding spring.
                        AutopaddingSpring padding = new AutopaddingSpring();
                        // Force the newly created spring to be considered
                        // by NOT incrementing counter
                        springs.add(counter, padding);
                        continue;
                    }
                    if (spring instanceof ComponentSpring) {
                        // Spring is a Component, make it the target of any
                        // leading AutopaddingSpring.
                        ComponentSpring cSpring = (ComponentSpring)spring;
                        if (!cSpring.isVisible()) {
                            counter++;
                            continue;
                        }
                        for (int i = 0; i < newLeadingPadding.size(); i++) {
                            ((AutopaddingSpring)newLeadingPadding.get(i)).
                                    addTarget(cSpring, axis);
                        }
                        newLeading.clear();
                        newLeadingPadding.clear();
                        int nextCounter = indexOfNextNonZeroSpring(counter + 1, false);
                        if (nextCounter == springs.size()) {
                            // Last Spring, add it to trailing
                            trailing.add(cSpring);
                        } else {
                            // Not that last Spring, add it to leading
                            newLeading.add(cSpring);
                        }
                        counter = nextCounter;
                    } else if (spring instanceof Group) {
                        // Forward call to child Group
                        if (newTrailing == null) {
                            newTrailing = new ArrayList(1);
                        } else {
                            newTrailing.clear();
                        }
                        newTrailingPadding.clear();
                        ((Group)spring).insertAutopadding(axis, newLeadingPadding,
                                newTrailingPadding, newLeading, newTrailing,
                                insert);
                        newLeading.clear();
                        newLeadingPadding.clear();
                        int nextCounter = indexOfNextNonZeroSpring(counter + 1, 
                                newTrailing.size() == 0);
                        if (nextCounter == springs.size()) {
                            trailing.addAll(newTrailing);
                            trailingPadding.addAll(newTrailingPadding);
                        } else {
                            newLeading.addAll(newTrailing);
                            newLeadingPadding.addAll(newTrailingPadding);
                        }
                        counter = nextCounter;
                    } else {
                        // Gap
                        newLeadingPadding.clear();
                        newLeading.clear();
                        counter++;
                    }
                }
            }
        }
        
        int getBaseline() {
            if (baselineSpring != null) {
                int baseline = baselineSpring.getBaseline();
                if (baseline >= 0) {
                    int size = 0;
                    for (int i = 0, max = springs.size(); i < max; i++) {
                        Spring spring = getSpring(i);
                        if (spring == baselineSpring) {
                            return size + baseline;
                        } else {
                            size += spring.getPreferredSize(VERTICAL);
                        }
                    }
                }
            }
            return -1;
        }
        
        int getBaselineResizeBehavior() {
            if (isResizable(VERTICAL)) {
                if (!baselineSpring.isResizable(VERTICAL)) {
                    // Spring to use for baseline isn't resizable. In this case
                    // baseline resize behavior can be determined based on how
                    // preceeding springs resize.
                    boolean leadingResizable = false;
                    for (int i = 0, max = springs.size(); i < max; i++) {
                        Spring spring = getSpring(i);
                        if (spring == baselineSpring) {
                            break;
                        } else if (spring.isResizable(VERTICAL)) {
                            leadingResizable = true;
                            break;
                        }
                    }
                    boolean trailingResizable = false;
                    for (int i = springs.size() - 1; i >= 0; i--) {
                        Spring spring = getSpring(i);
                        if (spring == baselineSpring) {
                            break;
                        }
                        if (spring.isResizable(VERTICAL)) {
                            trailingResizable = true;
                            break;
                        }
                    }
                    if (leadingResizable && !trailingResizable) {
                        return Baseline.BRB_CONSTANT_DESCENT;
                    } else if (!leadingResizable && trailingResizable) {
                        return Baseline.BRB_CONSTANT_ASCENT;
                    }
                    // If we get here, both leading and trailing springs are
                    // resizable. Fall through to OTHER.
                } else {
                    int brb = baselineSpring.getBaselineResizeBehavior();
                    if (brb == Baseline.BRB_CONSTANT_ASCENT) {
                        for (int i = 0, max = springs.size(); i < max; i++) {
                            Spring spring = getSpring(i);
                            if (spring == baselineSpring) {
                                return Baseline.BRB_CONSTANT_ASCENT;
                            }
                            if (spring.isResizable(VERTICAL)) {
                                return Baseline.BRB_OTHER;
                            }
                        }
                    } else if (brb == Baseline.BRB_CONSTANT_DESCENT) {
                        for (int i = springs.size() - 1; i >= 0; i--) {
                            Spring spring = getSpring(i);
                            if (spring == baselineSpring) {
                                return Baseline.BRB_CONSTANT_DESCENT;
                            }
                            if (spring.isResizable(VERTICAL)) {
                                return Baseline.BRB_OTHER;
                            }
                        }
                    }
                }
                return Baseline.BRB_OTHER;
            }
            // Not resizable, treat as constant_ascent
            return Baseline.BRB_CONSTANT_ASCENT;
        }
    }
    
    
    /**
     * Used in figuring out how much space to give resizable springs.
     */
    private static final class SpringDelta implements Comparable {
        // Original index.
        public final int index;
        // Delta, one of pref - min or max - pref.
        public int delta;
        
        public SpringDelta(int index, int delta) {
            this.index = index;
            this.delta = delta;
        }
        
        public int compareTo(Object o) {
            return delta - ((SpringDelta)o).delta;
        }
        
        public String toString() {
            return super.toString() + "[index=" + index + ", delta=" +
                    delta + "]";
        }
    }
    
    
    /**
     * A <code>Group</code> that lays out its elements on top of each
     * other.  If a child element is smaller than the provided space it
     * is aligned based on the alignment of the child (if specified) or
     * on the alignment of the ParallelGroup.
     *
     * @see #createParallelGroup()
     */
    public class ParallelGroup extends Group {
        // How children are layed out.
        private final int childAlignment;
        // Whether or not we're resizable.
        private final boolean resizable;
        
        ParallelGroup(int childAlignment, boolean resizable) {
            this.childAlignment = childAlignment;
            this.resizable = resizable;
        }
        
        /**
         * Adds the specified <code>Group</code>.
         *
         * @param group the Group to add
         * @return this Group
         */
        public ParallelGroup add(Group group) {
            return (ParallelGroup)addSpring(group);
        }
        
        /**
         * Adds the specified Component.  If the Component's min/max
         * are different from its pref than the component will be resizable.
         *
         * @param component the Component to add
         * @return this <code>ParallelGroup</code>
         */
        public ParallelGroup add(Component component) {
            return add(component, DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_SIZE);
        }
        
        /**
         * Adds the specified <code>Component</code>.  Min, pref and max
         * can be absolute values, or they can be one of
         * <code>DEFAULT_SIZE</code> or <code>PREFERRED_SIZE</code>.  For
         * example, the following:
         * <pre>
         *   add(component, PREFERRED_SIZE, PREFERRED_SIZE, 1000);
         * </pre>
         * Forces a max of 1000, with the min and preferred equalling that
         * of the preferred size of <code>component</code>.
         *
         * @param component the Component to add
         * @param min the minimum size
         * @param pref the preferred size
         * @param max the maximum size
         * @throws IllegalArgumentException if min, pref or max are
         *         not positive and not one of PREFERRED_SIZE or DEFAULT_SIZE.
         * @return this <code>SequentialGroup</code>
         */
        public ParallelGroup add(Component component, int min, int pref,
                int max) {
            return (ParallelGroup)addSpring(new ComponentSpring(
                    component, min, pref, max));
        }
        
        /**
         * Adds a rigid gap.
         *
         * @param pref the size of the gap
         * @throws IllegalArgumentException if min < 0 or pref < 0 or max < 0
         *         or the following is not meant min <= pref <= max.
         * @return this <code>ParallelGroup</code>
         */
        public ParallelGroup add(int pref) {
            return add(pref, pref, pref);
        }
        
        /**
         * Adds a gap with the specified size.
         *
         * @param min the minimum size of the gap
         * @param pref the preferred size of the gap
         * @param max the maximum size of the gap
         * @throws IllegalArgumentException if min < 0 or pref < 0 or max < 0
         *         or the following is not meant min <= pref <= max.
         * @return this <code>ParallelGroup</code>
         */
        public ParallelGroup add(int min, int pref, int max) {
            return (ParallelGroup)addSpring(new GapSpring(min, pref, max));
        }
        
        /**
         * Adds the specified <code>Group</code> as a child of this group.
         *
         * @param alignment the alignment of the Group.
         * @param group the Group to add
         * @return this <code>ParallelGroup</code>
         * @throws IllegalArgumentException if alignment is not one of
         *         <code>LEADING</code>, <code>TRAILING</code> or
         *         <code>CENTER</code>
         */
        public ParallelGroup add(int alignment, Group group) {
            checkChildAlignment(alignment);
            group.setAlignment(alignment);
            return (ParallelGroup)addSpring(group);
        }
        
        /**
         * Adds the specified Component.  If the Component's min/max
         * are different from its pref than the component will be resizable.
         *
         * @param alignment the alignment for the component
         * @param component the Component to add
         * @return this <code>Group</code>
         * @throws IllegalArgumentException if alignment is not one of
         *         <code>LEADING</code>, <code>TRAILING</code> or
         *         <code>CENTER</code>
         */
        public ParallelGroup add(int alignment, Component component) {
            return add(alignment, component, DEFAULT_SIZE, DEFAULT_SIZE,
                    DEFAULT_SIZE);
        }
        
        /**
         * Adds the specified <code>Component</code>.  Min, pref and max
         * can be absolute values, or they can be one of
         * <code>DEFAULT_SIZE</code> or <code>PREFERRED_SIZE</code>.  For
         * example, the following:
         * <pre>
         *   add(component, PREFERRED_SIZE, PREFERRED_SIZE, 1000);
         * </pre>
         * Forces a max of 1000, with the min and preferred equalling that
         * of the preferred size of <code>component</code>.
         *
         * @param alignment the alignment for the component.
         * @param component the Component to add
         * @param min the minimum size
         * @param pref the preferred size
         * @param max the maximum size
         * @throws IllegalArgumentException if min, pref or max are
         *         not positive and not one of PREFERRED_SIZE or DEFAULT_SIZE.
         * @return this <code>Group</code>
         */
        public ParallelGroup add(int alignment, Component component, int min,
                int pref, int max) {
            checkChildAlignment(alignment);
            ComponentSpring spring = new ComponentSpring(component,
                    min, pref, max);
            spring.setAlignment(alignment);
            return (ParallelGroup)addSpring(spring);
        }
        
        boolean isResizable() {
            return resizable;
        }
        
        int operator(int a, int b) {
            return Math.max(a, b);
        }
        
        int calculateMinimumSize(int axis) {
            if (!isResizable()) {
                return getPreferredSize(axis);
            }
            return super.calculateMinimumSize(axis);
        }
        
        int calculateMaximumSize(int axis) {
            if (!isResizable()) {
                return getPreferredSize(axis);
            }
            return super.calculateMaximumSize(axis);
        }
        
        void setValidSize(int axis, int origin, int size) {
            for (int i = 0, max = springs.size(); i < max; i++) {
                setChildSize(getSpring(i), axis, origin, size);
            }
        }
        
        void setChildSize(Spring spring, int axis, int origin, int size) {
            int alignment = spring.getAlignment();
            int springSize = Math.min(
                    Math.max(spring.getMinimumSize(axis), size),
                    spring.getMaximumSize(axis));
            if (alignment == NO_ALIGNMENT) {
                alignment = childAlignment;
            }
            switch (alignment) {
                case TRAILING:
                    spring.setSize(axis, origin + size - springSize,
                            springSize);
                    break;
                case CENTER:
                    spring.setSize(axis, origin +
                            (size - springSize) / 2,springSize);
                    break;
                default: // LEADING, or BASELINE
                    spring.setSize(axis, origin, springSize);
                    break;
            }
        }
        
        void insertAutopadding(int axis, List leadingPadding,
                List trailingPadding, List leading, List trailing,
                boolean insert) {
            for (int counter = 0, max = springs.size(); counter < max; counter++) {
                Spring spring = getSpring(counter);
                if (spring instanceof ComponentSpring) {
                    if (((ComponentSpring)spring).isVisible()) {
                        for (int i = 0; i < leadingPadding.size(); i++) {
                            ((AutopaddingSpring)leadingPadding.get(i)).addTarget(
                                    (ComponentSpring)spring, axis);
                        }
                        trailing.add(spring);
                    }
                } else if (spring instanceof Group) {
                    ((Group)spring).insertAutopadding(axis, leadingPadding,
                            trailingPadding, leading, trailing, insert);
                } else if (spring instanceof AutopaddingSpring) {
                    ((AutopaddingSpring)spring).setSources(leading);
                    trailingPadding.add(spring);
                }
            }
        }

        private void checkChildAlignment(int alignment) {
            boolean allowsBaseline = (this instanceof BaselineGroup);
            if (!allowsBaseline && alignment == BASELINE) {
                throw new IllegalArgumentException("Alignment must be one of:" +
                        "LEADING, TRAILING or CENTER");
            }
            if (alignment != CENTER && alignment != BASELINE &&
                    alignment != LEADING && alignment != TRAILING) {
                throw new IllegalArgumentException("Alignment must be one of:" +
                        "LEADING, TRAILING or CENTER");
            }
        }
    }
    
    
    /**
     * An extension of <code>ParallelGroup</code> that aligns its
     * constituent <code>Spring</code>s along the baseline.
     */
    private class BaselineGroup extends ParallelGroup {
        // Whether or not all child springs have a baseline
        private boolean allSpringsHaveBaseline;

        // max(spring.getBaseline()) of all springs aligned along the baseline
        // that have a baseline
        private int prefAscent;

        // max(spring.getPreferredSize().height - spring.getBaseline()) of all
        // springs aligned along the baseline that have a baseline
        private int prefDescent;

        // Whether baselineAnchoredToTop was explicitly set
        private boolean baselineAnchorSet;

        // Whether the baseline is anchored to the top or the bottom.
        // If anchored to the top the baseline is always at prefAscent,
        // otherwise the baseline is at (height - prefDescent)
        private boolean baselineAnchoredToTop;
        
        // Whether or not the baseline has been calculated.
        private boolean calcedBaseline;
        
        BaselineGroup(boolean resizable) {
            super(LEADING, resizable);
            prefAscent = prefDescent = -1;
            calcedBaseline = false;
        }
        
        BaselineGroup(boolean resizable, boolean baselineAnchoredToTop) {
            this(resizable);
            this.baselineAnchoredToTop = baselineAnchoredToTop;
            baselineAnchorSet = true;
        }
        
        void unset() {
            super.unset();
            prefAscent = prefDescent = -1;
            calcedBaseline = false;
        }
        
        void setValidSize(int axis, int origin, int size) {
            checkAxis(axis);
            if (prefAscent == -1) {
                super.setValidSize(axis, origin, size);
            } else {
                // do baseline layout
                baselineLayout(origin, size);
            }
        }
        
        int calculateSize(int axis, int type) {
            checkAxis(axis);
            if (!calcedBaseline) {
                calculateBaselineAndResizeBehavior();
            }
            if (type == MIN_SIZE) {
                return calculateMinSize();
            }
            if (type == MAX_SIZE) {
                return calculateMaxSize();
            }
            if (allSpringsHaveBaseline) {
                return prefAscent + prefDescent;
            }
            return Math.max(prefAscent + prefDescent,
                    super.calculateSize(axis, type));
        }
        
        private void calculateBaselineAndResizeBehavior() {
            // calculate baseline
            prefAscent = 0;
            prefDescent = 0;
            int baselineSpringCount = 0;
            int resizeBehavior = Baseline.BRB_NONE;
            for (int counter = springs.size() - 1; counter >= 0; counter--) {
                Spring spring = getSpring(counter);
                if (spring.getAlignment() == NO_ALIGNMENT ||
                        spring.getAlignment() == BASELINE) {
                    int baseline = spring.getBaseline();
                    if (baseline >= 0) {
                        if (spring.isResizable(VERTICAL)) {
                            int brb = spring.
                                    getBaselineResizeBehavior();
                            if (resizeBehavior == Baseline.BRB_NONE) {
                                resizeBehavior = brb;
                            } else if (brb != resizeBehavior) {
                                resizeBehavior = Baseline.BRB_CONSTANT_ASCENT;
                            }
                        }
                        prefAscent = Math.max(prefAscent, baseline);
                        prefDescent = Math.max(prefDescent, spring.
                                getPreferredSize(VERTICAL) - baseline);
                        baselineSpringCount++;
                    }
                }
            }
            if (!baselineAnchorSet) {
                if (resizeBehavior == Baseline.BRB_CONSTANT_DESCENT){
                    this.baselineAnchoredToTop = false;
                } else {
                    this.baselineAnchoredToTop = true;
                }
            }
            allSpringsHaveBaseline = (baselineSpringCount == springs.size());
            calcedBaseline = true;
        }
        
        private int calculateMaxSize() {
            int maxAscent = prefAscent;
            int maxDescent = prefDescent;
            int nonBaselineMax = 0;
            for (int counter = springs.size() - 1; counter >= 0; counter--) {
                Spring spring = getSpring(counter);
                int baseline;
                int springMax = spring.getMaximumSize(VERTICAL);
                if ((spring.getAlignment() == NO_ALIGNMENT ||
                        spring.getAlignment() == BASELINE) &&
                        (baseline = spring.getBaseline()) >= 0) {
                    int springPref = spring.getPreferredSize(VERTICAL);
                    if (springPref != springMax) {
                        switch (spring.getBaselineResizeBehavior()) {
                            case Baseline.BRB_CONSTANT_ASCENT:
                                if (baselineAnchoredToTop) {
                                    maxDescent = Math.max(maxDescent,
                                            springMax - baseline);
                                }
                                break;
                            case Baseline.BRB_CONSTANT_DESCENT:
                                if (!baselineAnchoredToTop) {
                                    maxAscent = Math.max(maxAscent,
                                            springMax - springPref + baseline);
                                }
                                break;
                            default: // CENTER_OFFSET and OTHER, not resizable
                                break;
                        }
                    }
                } else {
                    // Not aligned along the baseline, or no baseline.
                    nonBaselineMax = Math.max(nonBaselineMax, springMax);
                }
            }
            return Math.max(nonBaselineMax, maxAscent + maxDescent);
        }
        
        private int calculateMinSize() {
            int minAscent = 0;
            int minDescent = 0;
            int nonBaselineMin = 0;
            if (baselineAnchoredToTop) {
                minAscent = prefAscent;
            } else {
                minDescent = prefDescent;
            }
            for (int counter = springs.size() - 1; counter >= 0; counter--) {
                Spring spring = getSpring(counter);
                int springMin = spring.getMinimumSize(VERTICAL);
                int baseline;
                if ((spring.getAlignment() == NO_ALIGNMENT ||
                        spring.getAlignment() == BASELINE) &&
                        (baseline = spring.getBaseline()) >= 0) {
                    int springPref = spring.getPreferredSize(VERTICAL);
                    switch (spring.getBaselineResizeBehavior()) {
                        case Baseline.BRB_CONSTANT_ASCENT:
                            if (baselineAnchoredToTop) {
                                minDescent = Math.max(springMin - baseline,
                                        minDescent);
                            } else {
                                minAscent = Math.max(baseline, minAscent);
                            }
                            break;
                        case Baseline.BRB_CONSTANT_DESCENT:
                            if (!baselineAnchoredToTop) {
                                minAscent = Math.max(
                                        baseline - (springPref - springMin),
                                        minAscent);
                            } else {
                                minDescent = Math.max(springPref - baseline,
                                        minDescent);
                            }
                            break;
                        default:
                            // CENTER_OFFSET and OTHER are !resizable, use
                            // the preferred size.
                            minAscent = Math.max(baseline, minAscent);
                            minDescent = Math.max(springPref - baseline,
                                    minDescent);
                            break;
                    }
                } else {
                    // Not aligned along the baseline, or no baseline.
                    nonBaselineMin = Math.max(nonBaselineMin, springMin);
                }
            }
            return Math.max(nonBaselineMin, minAscent + minDescent);
        }

        /**
         * Lays out springs that have a baseline along the baseline.  All
         * others are centered.
         */
        private void baselineLayout(int origin, int size) {
            int ascent;
            int descent;
            if (baselineAnchoredToTop) {
                ascent = prefAscent;
                descent = size - ascent;
            } else {
                ascent = size - prefDescent;
                descent = prefDescent;
            }
            for (int counter = springs.size() - 1; counter >= 0; counter--) {
                Spring spring = getSpring(counter);
                int alignment = spring.getAlignment();
                if (alignment == NO_ALIGNMENT || alignment == BASELINE) {
                    int baseline = spring.getBaseline();
                    if (baseline >= 0) {
                        int springMax = spring.getMaximumSize(VERTICAL);
                        int springPref = spring.getPreferredSize(VERTICAL);
                        int height = springPref;
                        int y;
                        switch(spring.getBaselineResizeBehavior()) {
                            case Baseline.BRB_CONSTANT_ASCENT:
                                y = origin + ascent - baseline;
                                height = Math.min(descent, springMax -
                                        baseline) + baseline;
                                break;
                            case Baseline.BRB_CONSTANT_DESCENT:
                                height = Math.min(ascent, springMax -
                                        springPref + baseline) +
                                        (springPref - baseline);
                                y = origin + ascent +
                                        (springPref - baseline) - height;
                                break;
                            default: // CENTER_OFFSET & OTHER, not resizable
                                y = origin + ascent - baseline;
                                break;
                        }
                        spring.setSize(VERTICAL, y, height);
                    } else {
                        setChildSize(spring, VERTICAL, origin, size);
                    }
                } else {
                    setChildSize(spring, VERTICAL, origin, size);
                }
            }
        }
        
        int getBaseline() {
            if (springs.size() > 1) {
                // Force the baseline to be calculated
                getPreferredSize(VERTICAL);
                return prefAscent;
            } else if (springs.size() == 1) {
                return getSpring(0).getBaseline();
            }
            return -1;
        }
        
        int getBaselineResizeBehavior() {
            if (springs.size() == 1) {
                return getSpring(0).getBaselineResizeBehavior();
            }
            if (baselineAnchoredToTop) {
                return Baseline.BRB_CONSTANT_ASCENT;
            }
            return Baseline.BRB_CONSTANT_DESCENT;
        }
        
        // If the axis is VERTICAL, throws an IllegalStateException
        private void checkAxis(int axis) {
            if (axis == HORIZONTAL) {
                throw new IllegalStateException(
                        "Baseline must be used along vertical axis");
            }
        }
    }
    
    
    /**
     * A Spring representing one axis of a Component.
     * There are three ways to configure this:
     * <ul>
     * <li>Use the pref/min/max from the component
     * <li>Use the pref from the component and fix the min to 0 or max
     *     to a big number.
     * <li>Force the min/max/pref to be a certain value.
     * If the Component's size is to be linked to another components than
     * the min/max/pref all come from the ComponentInfo.
     */
    private final class ComponentSpring extends Spring {
        private Component component;
        private int origin;
        
        // min/pref/max are either a value >= 0 or one of
        // DEFAULT_SIZE or PREFERRED_SIZE
        private final int min;
        private final int pref;
        private final int max;
        
        // Baseline for the component.
        private int baseline = -1;
        
        // Whether or not the size has been requested yet.
        private boolean installed;
        
        private ComponentSpring(Component component, int min, int pref,
                int max) {
            this.component = component;
            if (component == null) {
                throw new IllegalArgumentException(
                        "Component must be non-null");
            }
            checkSize(min, pref, max, true);
            
            this.min = min;
            this.max = max;
            this.pref = pref;
            
            // getComponentInfo makes sure component is a child of the
            // Container GroupLayout is the LayoutManager for.
            getComponentInfo(component);
        }
        
        int calculateMinimumSize(int axis) {
            if (isLinked(axis)) {
                return getLinkSize(axis, MIN_SIZE);
            }
            return calculateNonlinkedMinimumSize(axis);
        }
        
        int calculatePreferredSize(int axis) {
            if (isLinked(axis)) {
                return getLinkSize(axis, PREF_SIZE);
            }
            int min = getMinimumSize(axis);
            int pref = calculateNonlinkedPreferredSize(axis);
            int max = getMaximumSize(axis);
            return Math.min(max, Math.max(min, pref));
        }
        
        int calculateMaximumSize(int axis) {
            if (isLinked(axis)) {
                return getLinkSize(axis, MAX_SIZE);
            }
            return Math.max(getMinimumSize(axis),
                    calculateNonlinkedMaximumSize(axis));
        }
        
        boolean isVisible() {
            return getComponentInfo(getComponent()).isVisible();
        }
        
        int calculateNonlinkedMinimumSize(int axis) {
            if (!isVisible()) {
                return 0;
            }
            if (min >= 0) {
                return min;
            }
            if (min == PREFERRED_SIZE) {
                return calculateNonlinkedPreferredSize(axis);
            }
            assert (min == DEFAULT_SIZE);
            return getSizeAlongAxis(axis, component.getMinimumSize());
        }
        
        int calculateNonlinkedPreferredSize(int axis) {
            if (!isVisible()) {
                return 0;
            }
            if (pref >= 0) {
                return pref;
            }
            assert (pref == DEFAULT_SIZE || pref == PREFERRED_SIZE);
            return getSizeAlongAxis(axis, component.getPreferredSize());
        }
        
        int calculateNonlinkedMaximumSize(int axis) {
            if (!isVisible()) {
                return 0;
            }
            if (max >= 0) {
                return max;
            }
            if (max == PREFERRED_SIZE) {
                return calculateNonlinkedPreferredSize(axis);
            }
            assert (max == DEFAULT_SIZE);
            return getSizeAlongAxis(axis, component.getMaximumSize());
        }
        
        private int getSizeAlongAxis(int axis, Dimension size) {
            return (axis == HORIZONTAL) ? size.width : size.height;
        }
        
        private int getLinkSize(int axis, int type) {
            if (!isVisible()) {
                return 0;
            }
            ComponentInfo ci = getComponentInfo(component);
            return ci.getLinkSize(axis, type);
        }
        
        void setSize(int axis, int origin, int size) {
            super.setSize(axis, origin, size);
            this.origin = origin;
            if (size == UNSET) {
                baseline = -1;
            }
        }
        
        int getOrigin() {
            return origin;
        }
        
        void setComponent(Component component) {
            this.component = component;
        }
        
        Component getComponent() {
            return component;
        }
        
        int getBaseline() {
            if (baseline == -1 && (component instanceof JComponent)) {
                Spring horizontalSpring = getComponentInfo(component).
                        horizontalSpring;
                int width = horizontalSpring.getPreferredSize(HORIZONTAL);
                int height = getPreferredSize(VERTICAL);
                if (width > 0 && height > 0) {
                    baseline = Baseline.getBaseline((JComponent)component,
                            width, getPreferredSize(VERTICAL));
                }
            }
            return baseline;
        }
        
        int getBaselineResizeBehavior() {
            return Baseline.getBaselineResizeBehavior(getComponent());
        }

        private boolean isLinked(int axis) {
            return getComponentInfo(component).isLinked(axis);
        }
        
        void installIfNecessary(int axis) {
            if (!installed) {
                installed = true;
                if (axis == HORIZONTAL) {
                    getComponentInfo(component).horizontalSpring = this;
                } else {
                    getComponentInfo(component).verticalSpring = this;
                }
            }
        }

        boolean willHaveZeroSize(boolean treatAutopaddingAsZeroSized) {
            return !isVisible();
        }
    }

    
    /**
     * Spring representing the preferred distance between two components.
     */
    private final class PaddingSpring extends Spring {
        private final JComponent source;
        private final JComponent target;
        private final int type;
        private final boolean canGrow;
        
        PaddingSpring(JComponent source, JComponent target, int type,
                boolean canGrow) {
            this.source = source;
            this.target = target;
            this.type = type;
            this.canGrow = canGrow;
        }
        
        int calculateMinimumSize(int axis) {
            return getPadding(axis);
        }
        
        int calculatePreferredSize(int axis) {
            return getPadding(axis);
        }
        
        int calculateMaximumSize(int axis) {
            if (canGrow) {
                return Short.MAX_VALUE;
            }
            return getPadding(axis);
        }
        
        private int getPadding(int axis) {
            int position;
            if (axis == HORIZONTAL) {
                position = SwingConstants.EAST;
            } else {
                position = SwingConstants.SOUTH;
            }
            return getLayoutStyle0().getPreferredGap(source,
                    target, type, position, host);
        }

        boolean willHaveZeroSize(boolean treatAutopaddingAsZeroSized) {
            return false;
        }
    }
    
    
    /**
     * Spring represented a certain amount of space.
     */
    private final class GapSpring extends Spring {
        private final int min;
        private final int pref;
        private final int max;
        
        GapSpring(int min, int pref, int max) {
            checkSize(min, pref, max, false);
            this.min = min;
            this.pref = pref;
            this.max = max;
        }

        int calculateMinimumSize(int axis) {
            if (min == PREFERRED_SIZE) {
                return getPreferredSize(axis);
            }
            return min;
        }
        
        int calculatePreferredSize(int axis) {
            return pref;
        }
        
        int calculateMaximumSize(int axis) {
            if (max == PREFERRED_SIZE) {
                return getPreferredSize(axis);
            }
            return max;
        }

        boolean willHaveZeroSize(boolean treatAutopaddingAsZeroSized) {
            return false;
        }
    }
    
    
    /**
     * Spring reprensenting the distance between any number of sources and
     * targets.  The targets and sources are computed during layout.  An
     * instance of this can either be dynamically created when
     * autocreatePadding is true, or explicitly created by the developer.
     */
    private class AutopaddingSpring extends Spring {
        List sources;
        ComponentSpring source;
        private List matches;
        int size;
        int lastSize;
        private final int pref;
        private final int max;
        private int type;
        private boolean userCreated;
        
        private AutopaddingSpring() {
            this.pref = PREFERRED_SIZE;
            this.max = PREFERRED_SIZE;
            this.type = LayoutStyle.RELATED;
        }

        AutopaddingSpring(int pref, int max) {
            this.pref = pref;
            this.max = max;
        }
        
        AutopaddingSpring(int type, int pref, int max) {
            this.type = type;
            this.pref = pref;
            this.max = max;
            this.userCreated = true;
        }
        
        public void setSource(ComponentSpring source) {
            this.source = source;
        }
        
        public void setSources(List sources) {
            this.sources = new ArrayList(sources);
        }
        
        public void setUserCreated(boolean userCreated) {
            this.userCreated = userCreated;
        }
        
        public boolean getUserCreated() {
            return userCreated;
        }
        
        void unset() {
            lastSize = getSize();
            super.unset();
            size = 0;
        }
        
        public void reset() {
            size = 0;
            sources = null;
            source = null;
            matches = null;
        }
        
        public void calculatePadding(int axis) {
            size = UNSET;
            int maxPadding = UNSET;
            if (matches != null) {
                LayoutStyle p = getLayoutStyle0();
                int position;
                if (axis == HORIZONTAL) {
                    if (isLeftToRight()) {
                        position = SwingConstants.EAST;
                    } else {
                        position = SwingConstants.WEST;
                    }
                } else {
                    position = SwingConstants.SOUTH;
                }
                for (int i = matches.size() - 1; i >= 0; i--) {
                    AutopaddingMatch match = (AutopaddingMatch)matches.get(i);
                    maxPadding = Math.max(maxPadding,
                            calculatePadding(p, position, match.source,
                            match.target));
                }
            }
            if (size == UNSET) {
                size = 0;
            }
            if (maxPadding == UNSET) {
                maxPadding = 0;
            }
            if (lastSize != UNSET) {
                size += Math.min(maxPadding, lastSize);
            }
        }
        
        private int calculatePadding(LayoutStyle p, int position,
                ComponentSpring source,
                ComponentSpring target) {
            int delta = target.getOrigin() - (source.getOrigin() +
                    source.getSize());
            if (delta >= 0) {
                int padding;
                if ((source.getComponent() instanceof JComponent) &&
                        (target.getComponent() instanceof JComponent)) {
                    padding = p.getPreferredGap((JComponent)source.getComponent(),
                        (JComponent)target.getComponent(), type, position, host);
                } else {
                    padding = 10;
                }
                if (padding > delta) {
                    size = Math.max(size, padding - delta);
                }
                return padding;
            }
            return 0;
        }
        
        public void addTarget(ComponentSpring spring, int axis) {
            int oAxis = (axis == HORIZONTAL) ? VERTICAL : HORIZONTAL;
            if (source != null) {
                if (areParallelSiblings(source.getComponent(),
                        spring.getComponent(), oAxis)) {
                    addValidTarget(source, spring);
                }
            } else {
                Component component = spring.getComponent();
                for (int counter = sources.size() - 1; counter >= 0; counter--){
                    ComponentSpring source = (ComponentSpring)sources.
                            get(counter);
                    if (areParallelSiblings(source.getComponent(),
                            component, oAxis)) {
                        addValidTarget(source, spring);
                    }
                }
            }
        }
        
        private void addValidTarget(ComponentSpring source,
                ComponentSpring target) {
            if (matches == null) {
                matches = new ArrayList(1);
            }
            matches.add(new AutopaddingMatch(source, target));
        }
        
        int calculateMinimumSize(int axis) {
            return size;
        }
        
        int calculatePreferredSize(int axis) {
            if (pref == PREFERRED_SIZE || pref == DEFAULT_SIZE) {
                return size;
            }
            return Math.max(size, pref);
        }
        
        int calculateMaximumSize(int axis) {
            if (max >= 0) {
                return Math.max(getPreferredSize(axis), max);
            }
            return size;
        }
  
        String getMatchDescription() {
            return (matches == null) ? "" : matches.toString();
        }
        
        public String toString() {
            return super.toString() + getMatchDescription();
        }

        boolean willHaveZeroSize(boolean treatAutopaddingAsZeroSized) {
            return treatAutopaddingAsZeroSized;
        }
    }
    
    
    /**
     * Represents two springs that should have autopadding inserted between
     * them.
     */
    private final static class AutopaddingMatch {
        public final ComponentSpring source;
        public final ComponentSpring target;

        AutopaddingMatch(ComponentSpring source, ComponentSpring target) {
            this.source = source;
            this.target = target;
        }
        
        private String toString(ComponentSpring spring) {
            return spring.getComponent().getName();
        }

        public String toString() {
            return "[" + toString(source) + "-" + toString(target) + "]";
        }
    }
    
    
    /**
     * An extension of AutopaddingSpring used for container level padding.
     */
    private class ContainerAutopaddingSpring extends AutopaddingSpring {
        private List targets;
        
        ContainerAutopaddingSpring() {
            super();
            setUserCreated(true);
        }

        ContainerAutopaddingSpring(int pref, int max) {
            super(pref, max);
            setUserCreated(true);
        }

        public void addTarget(ComponentSpring spring, int axis) {
            if (targets == null) {
                targets = new ArrayList(1);
            }
            targets.add(spring);
        }

        public void calculatePadding(int axis) {
            LayoutStyle p = getLayoutStyle0();
            int maxPadding = 0;
            int position;
            size = 0;
            if (targets != null) {
                // Leading
                if (axis == HORIZONTAL) {
                    if (isLeftToRight()) {
                        position = SwingConstants.WEST;
                    } else {
                        position = SwingConstants.EAST;
                    }
                } else {
                    position = SwingConstants.SOUTH;
                }
                for (int i = targets.size() - 1; i >= 0; i--) {
                    ComponentSpring targetSpring = (ComponentSpring)targets.
                                                                    get(i);
                    int padding = 10;
                    if (targetSpring.getComponent() instanceof JComponent) {
                        padding = p.getContainerGap(
                                (JComponent)targetSpring.getComponent(),
                                position, host);
                        maxPadding = Math.max(padding, maxPadding);
                        padding -= targetSpring.getOrigin();
                    } else {
                        maxPadding = Math.max(padding, maxPadding);
                    }
                    size = Math.max(size, padding);
                }
            }
            else {
                // Trailing
                if (axis == HORIZONTAL) {
                    if (isLeftToRight()) {
                        position = SwingConstants.EAST;
                    } else {
                        position = SwingConstants.WEST;
                    }
                } else {
                    position = SwingConstants.SOUTH;
                }
                if (sources != null) {
                    for (int i = sources.size() - 1; i >= 0; i--) {
                        ComponentSpring sourceSpring = (ComponentSpring)sources.
                                get(i);
                        maxPadding = Math.max(maxPadding,
                                updateSize(p, sourceSpring, position));
                    }
                }
                else if (source != null) {
                    maxPadding = updateSize(p, source, position);
                }
            }
            if (lastSize != UNSET) {
                size += Math.min(maxPadding, lastSize);
            }
        }

        private int updateSize(LayoutStyle p, ComponentSpring sourceSpring,
                int position) {
            int padding = 10;
            if (sourceSpring.getComponent() instanceof JComponent) {
                padding = p.getContainerGap(
                        (JComponent)sourceSpring.getComponent(), position,
                        host);
            }
            int delta = Math.max(0, getParent().getSize() -
                    sourceSpring.getSize() - sourceSpring.getOrigin());
            size = Math.max(size, padding - delta);
            return padding;
        }
        
        String getMatchDescription() {
            if (targets != null) {
                return "leading: " + targets.toString();
            }
            if (sources != null) {
                return "trailing: " + sources.toString();
            }
            return "--";
        }
}
    
    // LinkInfo contains the set of ComponentInfosthat are linked along a
    // particular axis.
    private static final class LinkInfo {
        private final int axis;
        private final List linked;
        private int size;
        
        LinkInfo(int axis) {
            linked = new ArrayList();
            size = UNSET;
            this.axis = axis;
        }
        
        public void add(ComponentInfo child) {
            LinkInfo childMaster = child.getLinkInfo(axis, false);
            if (childMaster == null) {
                linked.add(child);
                child.setLinkInfo(axis, this);
            } else if (childMaster != this) {
                linked.addAll(childMaster.linked);
                for (int i = 0; i < childMaster.linked.size(); i++) {
                    ComponentInfo childInfo = (ComponentInfo)childMaster.linked.get(i);
                    childInfo.setLinkInfo(axis, this);
                }
            }
            clearCachedSize();
        }
        
        public void remove(ComponentInfo info) {
            linked.remove(info);
            info.setLinkInfo(axis, null);
            if (linked.size() == 1) {
                ((ComponentInfo)linked.get(0)).setLinkInfo(axis, null);
            }
            clearCachedSize();
        }
        
        public void clearCachedSize() {
            size = UNSET;
        }
        
        public int getSize(int axis) {
            if (size == UNSET) {
                size = calculateLinkedSize(axis);
            }
            return size;
        }
        
        private int calculateLinkedSize(int axis) {
            int size = 0;
            for (int i = 0; i < linked.size(); i++) {
                ComponentInfo info = (ComponentInfo)linked.get(i);
                ComponentSpring spring;
                if (axis == HORIZONTAL) {
                    spring = info.horizontalSpring;
                } else {
                    assert (axis == VERTICAL);
                    spring = info.verticalSpring;
                }
                size = Math.max(size,
                        spring.calculateNonlinkedPreferredSize(axis));
            }
            return size;
        }
    }
    

    /**
     * Tracks the horizontal/vertical Springs for a Component.
     * This class is also used to handle Springs that have their sizes
     * linked.
     */
    private final class ComponentInfo {
        // Component being layed out
        private Component component;
        
        ComponentSpring horizontalSpring;
        ComponentSpring verticalSpring;
        
        // If the component's size is linked to other components, the
        // horizontalMaster and/or verticalMaster reference the group of
        // linked components.
        private LinkInfo horizontalMaster;
        private LinkInfo verticalMaster;

        private boolean visible;
        private Boolean honorsVisibility;
        
        ComponentInfo(Component component) {
            this.component = component;
            updateVisibility();
        }
        
        public void dispose() {
            // Remove horizontal/vertical springs
            removeSpring(horizontalSpring);
            horizontalSpring = null;
            removeSpring(verticalSpring);
            verticalSpring = null;
            // Clean up links
            if (horizontalMaster != null) {
                horizontalMaster.remove(this);
            }
            if (verticalMaster != null) {
                verticalMaster.remove(this);
            }
        }
        
        void setHonorsVisibility(Boolean honorsVisibility) {
            this.honorsVisibility = honorsVisibility;
        }

        private void removeSpring(Spring spring) {
            if (spring != null) {
                ((Group)spring.getParent()).springs.remove(spring);
            }
        }
        
        public boolean isVisible() {
            return visible;
        }
        
        /**
         * Updates the cached visibility.
         *
         * @return true if the visibility changed
         */
        boolean updateVisibility() {
            boolean honorsVisibility;
            if (this.honorsVisibility == null) {
                honorsVisibility = GroupLayout.this.getHonorsVisibility();
            } else {
                honorsVisibility = this.honorsVisibility.booleanValue();
            }
            boolean newVisible = (honorsVisibility) ?
                component.isVisible() : true;
            if (visible != newVisible) {
                visible = newVisible;
                return true;
            }
            return false;
        }
        
        public void setBounds(Insets insets, int parentWidth, boolean ltr) {
            int x = horizontalSpring.getOrigin();
            int w = horizontalSpring.getSize();
            int y = verticalSpring.getOrigin();
            int h = verticalSpring.getSize();
            
            if (!ltr) {
                x = parentWidth - x - w;
            }
            component.setBounds(x + insets.left, y + insets.top, w, h);
        }
        
        public void setComponent(Component component) {
            this.component = component;
            if (horizontalSpring != null) {
                horizontalSpring.setComponent(component);
            }
            if (verticalSpring != null) {
                verticalSpring.setComponent(component);
            }
        }
        
        public Component getComponent() {
            return component;
        }
        
        /**
         * Returns true if this component has its size linked to
         * other components.
         */
        public boolean isLinked(int axis) {
            if (axis == HORIZONTAL) {
                return horizontalMaster != null;
            }
            assert (axis == VERTICAL);
            return (verticalMaster != null);
        }
        
        private void setLinkInfo(int axis, LinkInfo linkInfo) {
            if (axis == HORIZONTAL) {
                horizontalMaster = linkInfo;
            } else {
                assert (axis == VERTICAL);
                verticalMaster = linkInfo;
            }
        }
        
        public LinkInfo getLinkInfo(int axis) {
            return getLinkInfo(axis, true);
        }
        
        private LinkInfo getLinkInfo(int axis, boolean create) {
            if (axis == HORIZONTAL) {
                if (horizontalMaster == null && create) {
                    // horizontalMaster field is directly set by adding
                    // us to the LinkInfo.
                    new LinkInfo(HORIZONTAL).add(this);
                }
                return horizontalMaster;
            } else {
                assert (axis == VERTICAL);
                if (verticalMaster == null && create) {
                    // verticalMaster field is directly set by adding
                    // us to the LinkInfo.
                    new LinkInfo(VERTICAL).add(this);
                }
                return verticalMaster;
            }
        }

        public void clearCachedSize() {
            if (horizontalMaster != null) {
                horizontalMaster.clearCachedSize();
            }
            if (verticalMaster != null) {
                verticalMaster.clearCachedSize();
            }
        }
        
        int getLinkSize(int axis, int type) {
            if (axis == HORIZONTAL) {
                return horizontalMaster.getSize(axis);
            } else {
                assert (axis == VERTICAL);
                return verticalMaster.getSize(axis);
            }
        }
    }
}
