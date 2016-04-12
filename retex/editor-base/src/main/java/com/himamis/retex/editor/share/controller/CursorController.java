package com.himamis.retex.editor.share.controller;

import java.util.ArrayList;

import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

public class CursorController {

    /** Next character -> key. */
	public boolean nextCharacter(EditorState editorState) {
        int currentOffset = editorState.getCurrentOffset();
        MathSequence currentField = editorState.getCurrentField();
        if (currentOffset < currentField.size() &&
                currentField.getArgument(currentOffset) != null &&
                currentField.getArgument(currentOffset) instanceof MathContainer &&
                ((MathContainer) currentField.getArgument(currentOffset)).hasChildren()) {
            MathComponent component = currentField.getArgument(currentOffset);
            firstField(editorState, (MathContainer) component);
			return true;
        } else if (currentOffset < currentField.size()) {
            editorState.incCurrentOffset();
			return true;
        } else {
			return nextField(editorState);
        }
    }

    /** Previous character <- key. */
    public void prevCharacter(EditorState editorState) {
        int currentOffset = editorState.getCurrentOffset();
        MathSequence currentField = editorState.getCurrentField();
        if (currentOffset - 1 >= 0 &&
                currentField.getArgument(currentOffset - 1) != null &&
                currentField.getArgument(currentOffset - 1) instanceof MathContainer &&
                ((MathContainer) currentField.getArgument(currentOffset - 1)).hasChildren()) {
            MathComponent component = currentField.getArgument(currentOffset - 1);
            lastField(editorState, (MathContainer) component);

        } else if (currentOffset > 0) {
            editorState.decCurrentOffset();
        } else {
            prevField(editorState);
        }
    }

    public void firstField(EditorState editorState) {
        firstField(editorState, editorState.getRootComponent());
    }

    public void firstField(EditorState editorState, MathContainer component) {
        // surface to first symbol
        while (!(component instanceof MathSequence)) {
            int current = component.first();
            component = (MathContainer) component.getArgument(current);
        }
        editorState.setCurrentField((MathSequence) component);
        editorState.setCurrentOffset(0);
    }

    public void lastField(EditorState editorState) {
        lastField(editorState, editorState.getRootComponent());
    }

    public void lastField(EditorState editorState, MathContainer component) {
        // surface to last symbol
        while (!(component instanceof MathSequence)) {
            int current = component.last();
            component = (MathContainer) component.getArgument(current);
        }
        editorState.setCurrentField((MathSequence) component);
        editorState.setCurrentOffset(component.size());
    }

	public boolean nextField(EditorState editorState) {
		return nextField(editorState, editorState.getCurrentField());
    }

	public boolean nextField(EditorState editorState, MathContainer component) {
        // retrieve parent
        MathContainer container = component.getParent();
        int current = component.getParentIndex();

        if (container == null) {
            // this component has no parent
            // previous component doesn't exist
            // no-op
			System.out.println("no container");
			return false;
        } else if (container instanceof MathSequence) {
            editorState.setCurrentField((MathSequence) container);
			editorState.setCurrentOffset(component.getParentIndex() + 1);
			return container.size() > component.getParentIndex();
            // try to find next sibling
        } else if (container.hasNext(current)) {
            current = container.next(current);
            component = (MathContainer) container.getArgument(current);
            firstField(editorState, component);
			System.out.println("has next");
			return true;
            // try to delve down the tree
        } else {
			return nextField(editorState, container);
        }
    }


    /** Find previous field. */
    public void prevField(EditorState editorState) {
        prevField(editorState, editorState.getCurrentField());
    }

    /* Search for previous component */
    private void prevField(EditorState editorState, MathContainer component) {
        // retrieve parent
        MathContainer container = component.getParent();
        int current = component.getParentIndex();

        if (container == null) {
            // this component has no parent
            // previous component doesn't exist
            // no-op

            // try sequence
        } else if (container instanceof MathSequence) {
            editorState.setCurrentField((MathSequence) container);
            editorState.setCurrentOffset(component.getParentIndex());

            // try to find previous sibling
        } else if (container.hasPrev(current)) {
            current = container.prev(current);
            component = (MathContainer) container.getArgument(current);
            lastField(editorState, component);

            // delve down the tree
        } else {
            prevField(editorState, container);
        }
    }

    /** Up field. */
    public boolean upField(EditorState editorState) {
        return upField(editorState, editorState.getCurrentField());
    }

    /** Down field. */
    public boolean downField(EditorState editorState) {
        return downField(editorState, editorState.getCurrentField());
    }

    /** Up field. */
    private boolean upField(EditorState editorState, MathContainer component) {
        if (component instanceof MathSequence) {
            if (component.getParent() instanceof MathFunction) {
                MathFunction function = (MathFunction) component.getParent();
                int upIndex = function.getUpIndex(component.getParentIndex());
                if (upIndex >= 0) {
                    editorState.setCurrentField(function.getArgument(upIndex));
                    editorState.setCurrentOffset(0);
                    return true;
                }
            }
            // matrix goes here
        }
        if (component.getParent() != null) {
            return upField(editorState, component.getParent());
        }
        return false;
    }

    /** Down field. */
    private boolean downField(EditorState editorState, MathContainer component) {
        if (component instanceof MathSequence) {
            if (component.getParent() instanceof MathFunction) {
                MathFunction function = (MathFunction) component.getParent();
                int downIndex = function.getDownIndex(component.getParentIndex());
                if (downIndex >= 0) {
                    editorState.setCurrentField(function.getArgument(downIndex));
                    editorState.setCurrentOffset(0);
                    return true;
                }
            }
            // matrix goes here
        }
        if (component.getParent() != null) {
            return downField(editorState, component.getParent());
        }
        return false;
    }

    /**
     * set position in editor state from tree path
     * @param list tree path
     * @param ct starting container
     * @param editorState
     */
	public void setPath(ArrayList<Integer> list, MathContainer ct,
			EditorState editorState) {
		MathContainer current = ct;
        int i = list.size() - 1;
		while (i >=0) {
            int index = list.get(i);
            System.out.println("index: "+index+" / "+current.size());
			if (index < current.size()) {
                MathComponent child = current.getArgument(index);
                System.out.println("child: "+child);
                if (child instanceof MathCharacter){
                    editorState.setCurrentField((MathSequence) current);
                    editorState.setCurrentOffset(index);
                    System.out.println("-- offset");
                    return;
                }else if (child instanceof MathSequence) {
                    System.out.println("-- MathSequence");
					current = (MathSequence) child;
                    i--;
				} else  {
                    System.out.println("-- MathContainer (MathArray or MathSequence)");
                    i--;
                    current = (MathSequence) ((MathContainer) child).getArgument(list.get(i));
                    i--;
                }
            } else if (index == current.size()){
                editorState.setCurrentField((MathSequence) current);
                editorState.setCurrentOffset(index);
                System.out.println("-- offset");
                return;
			} else {
				return;
			}
		}

	}

    /**
     * set position in editor state from tree path, starting at root component
     * @param list tree path
     * @param editorState
     */
    public void setPath(ArrayList<Integer> list, EditorState editorState) {
        editorState.setCurrentOffset(0);
        setPath(list, editorState.getRootComponent(), editorState);
    }


    public ArrayList<Integer> getPath(EditorState editorState){

        ArrayList<Integer> path = new ArrayList<Integer>();

        path.add(editorState.getCurrentOffset());
        MathContainer field = editorState.getCurrentField();
        MathContainer parent = field.getParent();
        while (parent != null){
            path.add(field.getParentIndex());
            field = parent;
            parent = field.getParent();
        }

        return path;
    }

}
