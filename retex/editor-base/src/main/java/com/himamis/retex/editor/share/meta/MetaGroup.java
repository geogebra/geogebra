package com.himamis.retex.editor.share.meta;

/**
 * Created by Balazs on 7/21/2015.
 */
public interface MetaGroup {

    /**
     * @return the name of the group
     */
    String getName();

    /**
     * @return the group name
     */
    String getGroup();

    /**
     * @param componentName the name of the component
     * @return the respective meta component if exists, null otherwise
     */
    MetaComponent getComponent(String componentName);

}
