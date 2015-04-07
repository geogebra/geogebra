package org.geogebra.common.kernel.locusequ;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.geogebra.common.kernel.locusequ.arith.Equation;

/**
 * Represents a list of equations. There are some algos
 * represented by more than one equation (intersection
 * between two objects, for example). This class
 * should be used in general instead of {@link Equation} when
 * dealing with restrictions in general.
 * @author sergio
 *
 */
public class EquationList extends ArrayList<Equation>{
	private static final long serialVersionUID = 1L;
	private static EquationList emptyList = new EmptyEquationList();
    private boolean isAlgebraic = true;
    
    /*
     * A subclass that guarantees that objects cannot be added
     * nor removed (and so it will be always empty).
     */
    private static class EmptyEquationList extends EquationList{
    	private static final long serialVersionUID = 1L;

		public EmptyEquationList() { super(0); }
    	@Override
        public Equation set(int index, Equation element) { return null; }
        @Override
        public boolean add(Equation o) { return false; }
        @Override
        public void add(int index, Equation element) { /* it must be empty! */ }
        @Override
        public Equation remove(int index) { return null; }
        @Override
        public boolean remove(Object o) { return false; }
        @Override
        public boolean addAll(Collection<? extends Equation> c) { return false; }
        
        @Override
        public boolean addAll(int index, Collection<? extends Equation> c) { return false; }
    }
    
    /**
     * Creates an empty {@link EquationList}.
     */
    public EquationList() { super(); }
    /**
     * Creates an empty {@link EquationList} with space for <code>i</code> items.
     * @param i size of initial array.
     */
    public EquationList(int i) { super(i); }
    /**
     * Creates an {@link EquationList} with all items in <code>c</code>
     * @param c Collection whose items will be imported.
     */
    public EquationList(Collection<? extends Equation> c) {
    	super(c);
    	this.updateAlgebraic();
    }
    
    /**
     * Returns <code>true</code> if all elements in this collection
     * are algebraic. <code>false</code> otherwise.
     * @return algebraicity of EquationList
     */
    public boolean isAlgebraic() {
        return this.isAlgebraic;
    }
    
    @Override
    public Equation set(int index, Equation element) {
        Equation result = super.set(index, element);
        this.updateAlgebraic();
        return result;
    }
    @Override
    public boolean add(Equation o) {
        boolean add = super.add(o);
        this.updateAlgebraic();
        return add;
    }
    @Override
    public void add(int index, Equation element) {
        super.add(index, element);
        this.updateAlgebraic();
    }
    @Override
    public Equation remove(int index) {
        Equation result = super.remove(index);
        this.updateAlgebraic();
        return result;
    }
    @Override
    public boolean remove(Object o) {
        boolean result = super.remove(o);
        this.updateAlgebraic();
        return result;
    }
    @Override
    public boolean addAll(Collection<? extends Equation> c) {
        boolean result = super.addAll(c);
        this.updateAlgebraic();
        return result;
    }
    @Override
    public boolean addAll(int index, Collection<? extends Equation> c) {
        boolean result = super.addAll(index, c);
        this.updateAlgebraic();
        return result;
    }
    
    /**
     * Updates algebraicity of this list depending on current elements.
     */
    protected void updateAlgebraic() {
        Iterator<Equation> it = this.iterator();
        
        while(it.hasNext()) {
            if(!it.next().isAlgebraic()){ 
                this.isAlgebraic = false;
                return;
            }
        }
        
        this.isAlgebraic = true;
    }
    
    /**
     * Returns an empty {@link EquationList}. This
     * list is a special one. Elements cannot be added
     * nor removed from it, so it is always empty.
     * @return a special empty list.
     */
    public static EquationList getEmptyList() {
        return emptyList;
    }
    
    public void setAlgebraic(boolean algebraic) {
    	this.isAlgebraic = algebraic;
    }
}
