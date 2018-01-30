package com.himamis.retex.editor.share.meta;

import java.util.Collection;

public interface MetaGroupCollection extends MetaGroup {

    /**
     * @return the components in this collection
     */
    Collection<MetaComponent> getComponents();

    /**
     * Prefer using {@link MetaGroup#getComponent(Tag)} over this method.
     *
     * @see #getComponent(Tag)
     * @param name the name of the component
     * @return the component with name, otherwise null
     */
    MetaComponent getComponent(String name);
}
