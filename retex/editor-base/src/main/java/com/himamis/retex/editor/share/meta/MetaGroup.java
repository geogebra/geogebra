package com.himamis.retex.editor.share.meta;

/**
 * Describes a group of meta objects.
 */
public interface MetaGroup {



    /**
     * @param componentName the name of the component
     * @return the respective meta component if exists, null otherwise
     */
    MetaComponent getComponent(String componentName);

}
