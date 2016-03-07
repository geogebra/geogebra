package com.himamis.retex.editor.share.controller;

import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathSequence;

public class EditorState {

    private MetaModel metaModel;
    private MathSequence rootComponent;

    private MathSequence currentField;
    private Integer currentOffset;

    public EditorState(MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    public MathSequence getRootComponent() {
        return rootComponent;
    }

    public void setRootComponent(MathSequence rootComponent) {
        this.rootComponent = rootComponent;
    }

    public MathSequence getCurrentField() {
        return currentField;
    }

    public void setCurrentField(MathSequence currentField) {
        this.currentField = currentField;
    }

    public Integer getCurrentOffset() {
        return currentOffset;
    }

    public void setCurrentOffset(Integer currentOffset) {
        this.currentOffset = currentOffset;
    }

    public void incCurrentOffset() {
        currentOffset++;
    }

    public void addCurrentOffset(int size) {
        currentOffset += size;
    }

    public void decCurrentOffset() {
        currentOffset--;
    }

    public void addArgument(MathComponent mathComponent) {
        currentField.addArgument(currentOffset, mathComponent);
        incCurrentOffset();
    }

    public MetaModel getMetaModel() {
        return metaModel;
    }
}
