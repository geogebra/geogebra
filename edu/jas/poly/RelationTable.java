/*
 * $Id: RelationTable.java 3122 2010-05-09 10:36:30Z kredel $
 */

package edu.jas.poly;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

import edu.jas.kern.PrettyPrint;


/**
 * RelationTable for solvable polynomials.
 * This class maintains the non-commutative multiplication 
 * relations of solvable polynomial rings.
 * The table entries are initialized with relations of the 
 * form x<sub>j</sub> * x<sub>i</sub> = p<sub>ij</sub>.
 * During multiplication the ralations are updated by relations 
 * of the form x<sub>j</sub><sup>k</sup> * x<sub>i</sub><sup>l</sup> 
 * = p<sub>ijkl</sub>.
 * If no relation for x<sub>j</sub> * x<sub>i</sub> is found in 
 * the table, this multiplication is assumed to be commutative
 * x<sub>i</sub> x<sub>j</sub>.
 * @author Heinz Kredel
 */

public class RelationTable<C extends RingElem<C>> implements Serializable {


    /** The data structure for the relations. 
     */
    public final Map< List<Integer>, List > table;


    /** The factory for the solvable polynomial ring. 
     */
    public final GenSolvablePolynomialRing<C> ring;


    private static final Logger logger = Logger.getLogger(RelationTable.class);

    private final boolean debug = true; //logger.isDebugEnabled();


    /**
     * Constructor for RelationTable requires ring factory.
     * Note: This constructor is called within the constructor 
     * of the ring factory, so methods of this class can only be used
     * after the other constructor has terminated.
     * @param r solvable polynomial ring factory.
     */
    protected RelationTable(GenSolvablePolynomialRing<C> r) {
        table = new HashMap< List<Integer>, List >();
        ring = r;
        if ( ring == null ) {
           throw new IllegalArgumentException("RelationTable no ring");
        }
    }


    /**
     * RelationTable equals.
     * Tests same keySets only, not relations itself.
     * Will be improved in the future.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals(Object p) {
        if ( ! (p instanceof RelationTable) ) {
            System.out.println("no RelationTable");
            return false;
        }
        RelationTable< C > tab = null;
        try {
            tab = (RelationTable< C >)p;
        } catch (ClassCastException ignored) {
        }
        if ( tab == null ) {
           return false;
        }
        if ( ! ring.equals( tab.ring ) ) {
            System.out.println("not same Ring " + ring.toScript() + ", " + tab.ring.toScript());
            return false;
        }
        for ( List<Integer> k: table.keySet() ) { 
            List a = table.get(k);
            List b = tab.table.get(k);
            if ( b == null ) {
                return false;
            }
            // check contents, but only base relations
            if ( ! a.equals(b) ) {
                return false;
            }
        }
        for ( List<Integer> k: tab.table.keySet() ) { 
            List a = table.get(k);
            List b = tab.table.get(k);
            if ( a == null ) {
               return false;
            }
            // check contents, but only base relations
            if ( ! a.equals(b) ) {
               return false;
            }
        }
        return true;
    }


    /** Hash code for this relation table.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
       int h;
       h = ring.hashCode();
       h = 31 * h + table.hashCode();
       return h;
    }


    /** Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        List v;
        StringBuffer s = new StringBuffer("RelationTable[");
        boolean first = true;
        for ( List<Integer> k: table.keySet() ) { 
            if ( first ) {
               first = false;
            } else {
               s.append( ", " );
            }
            s.append( k.toString() );
            v = table.get( k );
            s.append("=");
            s.append( v.toString() );
        }
        s.append("]");
        return s.toString();
    }


    /** Get the String representation.
     * @param vars names for the variables.
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("unchecked")
    public String toString(String[] vars) {
        if ( vars == null ) {
            return toString();
        }
        List v;
        StringBuffer s = new StringBuffer("RelationTable\n(");
        if ( PrettyPrint.isTrue() ) {
            boolean first = true;
            for ( List<Integer> k: table.keySet() ) { 
                if ( first ) {
                    first = false;
                    s.append( "\n" );
                } else {
                    s.append( ",\n" );
                }
                v = table.get( k );
                for (Iterator jt = v.iterator(); jt.hasNext(); ) { 
                    ExpVectorPair ep = (ExpVectorPair)jt.next();
                    s.append("( " + ep.getFirst().toString(vars) + " ), " );
                    s.append("( " + ep.getSecond().toString(vars) + " ), " );
                    GenSolvablePolynomial<C> p 
                        = (GenSolvablePolynomial<C>)jt.next();
                    s.append("( " + p.toString(vars) + " )" );
                    if ( jt.hasNext() ) {
                        s.append(",\n");
                    }
                }
            }
        } else {
            boolean first = true;
            for ( List<Integer> k: table.keySet() ) { 
                if ( first ) {
                    first = false;
                } else {
                    s.append( ",\n" );
                }
                v = table.get( k );
                for (Iterator jt = v.iterator(); jt.hasNext(); ) { 
                    ExpVectorPair ep = (ExpVectorPair)jt.next();
                    s.append("( " + ep.getFirst().toString(vars) + " ), " );
                    s.append("( " + ep.getSecond().toString(vars) + " ), " );
                    GenSolvablePolynomial<C> p 
                        = (GenSolvablePolynomial<C>)jt.next();
                    s.append("( " + p.toString(vars) + " )" );
                    if ( jt.hasNext() ) {
                        s.append(",\n");
                    }
                }
            }
        }
        s.append("\n)\n");
        return s.toString();
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this relation table.
     */
    public String toScript() {
        // Python case
        String[] vars = ring.vars;
        List v;
        StringBuffer s = new StringBuffer("[");
        boolean first = true;
        for ( List<Integer> k: table.keySet() ) { 
            if ( first ) {
                first = false;
                s.append( "" );
            } else {
                s.append( ", " );
            }
            v = table.get( k );
            for (Iterator jt = v.iterator(); jt.hasNext(); ) { 
                ExpVectorPair ep = (ExpVectorPair)jt.next();
                s.append("" + ep.getFirst().toScript(vars) + ", " );
                s.append("" + ep.getSecond().toScript(vars) + ", " );
                GenPolynomial<C> p = (GenPolynomial<C>)jt.next();
                s.append("( " + p.toScript() + " )" );
                if ( jt.hasNext() ) {
                    s.append(", ");
                }
            }
        }
        s.append( "]" );
        return s.toString();
    }


    /**
     * Update or initialize RelationTable with new relation.
     * relation is e * f = p.
     * @param e first term.
     * @param f second term.
     * @param p solvable product polynomial.
     */
    @SuppressWarnings("unchecked")
    public synchronized void 
           update(ExpVector e, ExpVector f, GenSolvablePolynomial<C> p) {
        if ( debug ) {
            //System.out.println("new relation = " + e + " .*. " + f + " = " + p);
            logger.info("new relation = " + e + " .*. " + f + " = " + p);
        }
        if ( p == null || e == null || f == null ) {
           throw new IllegalArgumentException("RelationTable update e|f|p == null");
        }
        if ( debug ) {
           if ( e.totalDeg() == 1 && f.totalDeg() == 1 ) {
              int[] de = e.dependencyOnVariables();
              int[] df = f.dependencyOnVariables();
              logger.debug("update e ? f " + de[0] + " " + df[0]);
              //int t = ring.tord.getDescendComparator().compare(e,f);
              //System.out.println("update compare(e,f) = " + t);
              if ( de[0] == df[0] ) { // error 
                 throw new IllegalArgumentException("RelationTable update e==f");
              }
              if ( de[0] > df[0] ) { // invalid update 
                 logger.error("warning: update e > f " + e + " " + f + " changed");
                 ExpVector tmp = e;
                 e = f;
                 f = tmp;
                 Map.Entry<ExpVector,C> m = p.leadingMonomial();
                 GenPolynomial<C> r = p.subtract( m.getValue(), m.getKey() );
                 r = r.negate();
                 p = (GenSolvablePolynomial<C>)r.sum( m.getValue(), m.getKey() );
              }
           }
        }
        ExpVector ef = e.sum(f);
        ExpVector lp = p.leadingExpVector();
        if ( ! ef.equals(lp) ) { // check for suitable term order
           logger.error("relation term order = " + ring.tord);
           throw new IllegalArgumentException("RelationTable update e*f != lt(p)");
        }
        List<Integer> key = makeKey(e,f);
        ExpVectorPair evp = new ExpVectorPair( e, f );
        if ( key.size() != 2 ) {
           System.out.println("key = " + key + ", evp = " + evp);
        }
        List part = table.get( key );
        if ( part == null ) { // initialization only
           part = new LinkedList();
           part.add( evp );
           part.add( p );
           table.put( key, part );
           return;
        }
        Object o;
        int index = -1;
        for ( ListIterator it = part.listIterator(); it.hasNext(); ) {
            ExpVectorPair look = (ExpVectorPair)it.next();
            o = it.next(); // skip poly
            if ( look.isMultiple( evp ) ) {
                index = it.nextIndex(); 
                // last index of or first index of: break
            }
        }
        if ( index < 0 ) {
           index = 0;
        }
        part.add( index, evp );
        part.add( index+1, p );
        // table.put( key, part ); // required??
    }


    /**
     * Update or initialize RelationTable with new relation.
     * relation is e * f = p.
     * @param E first term polynomial.
     * @param F second term polynomial.
     * @param p solvable product polynomial.
     */
    public void update(GenPolynomial<C> E, GenPolynomial<C> F, GenSolvablePolynomial<C> p) {
        if ( E.isZERO() || F.isZERO() ) {
            throw new RuntimeException("polynomials may not be zero: " + E + ", " + F);
        }
        C ce = E.leadingBaseCoefficient();
        C cf = F.leadingBaseCoefficient();
        if ( ! ce.isONE() || ! cf.isONE() ) {
            throw new RuntimeException("lbcf of polynomials must be one: " + ce + ", " + cf);
        }
        ExpVector e = E.leadingExpVector();
        ExpVector f = F.leadingExpVector();
        update(e,f,p);
    }


    /**
     * Update or initialize RelationTable with new relation.
     * relation is e * f = p.
     * @param E first term polynomial.
     * @param F second term polynomial.
     * @param p product polynomial.
     */
    public void update(GenPolynomial<C> E, GenPolynomial<C> F, GenPolynomial<C> p) {
        if ( p.isZERO() ) {
            throw new RuntimeException("polynomial may not be zero: " + p);
        }
        if ( p.isONE() ) {
            throw new RuntimeException("product of polynomials may not be one: " + p);
        }
        GenSolvablePolynomial<C> sp = new GenSolvablePolynomial<C>(ring,p.val);
        update(E,F,sp);
    }


    /**
     * Lookup RelationTable for existing relation.
     * Find p with e * f = p.
     * If no relation for e * f is contained in the table then
     * return the symmetric product p = 1 e f. 
     * @param e first term.
     * @param f second term.
     * @return t table relation container, 
     *         contains e' and f' with e f = e' lt(p) f'. 
     */
    @SuppressWarnings("unchecked")
    public TableRelation<C> lookup(ExpVector e, ExpVector f) {
        List<Integer> key = makeKey(e,f);
        List part = table.get( key );
        if ( part == null ) { // symmetric product
            ExpVector ef =  e.sum( f );
            GenSolvablePolynomial<C> p = ring.getONE().multiply( ef );
            return new TableRelation<C>(null,null,p);
        }
        ExpVectorPair evp = new ExpVectorPair( e, f );
        ExpVector ep = null;
        ExpVector fp = null;
        ExpVectorPair look = null;
        GenSolvablePolynomial<C> p = null;
        for ( Iterator it = part.iterator(); it.hasNext(); ) {
            look = (ExpVectorPair)it.next();
            p = (GenSolvablePolynomial<C>)it.next();
            if ( evp.isMultiple( look ) ) {
                ep = e.subtract( look.getFirst() );
                fp = f.subtract( look.getSecond() );
                if ( ep.isZERO() ) {
                    ep = null;
                }
                if ( fp.isZERO() ) {
                    fp = null;
                }
                return new TableRelation<C>(ep,fp,p);
            }
        }
        // unreacheable code!
        return new TableRelation<C>(ep,fp,p);
    }


    /**
     * Construct a key for (e,f).
     * @param e first term.
     * @param f second term.
     * @return k key for (e,f).
     */
    protected List<Integer> makeKey(ExpVector e, ExpVector f) {
        int[] de = e.dependencyOnVariables();
        int[] df = f.dependencyOnVariables();
        List<Integer> key = new ArrayList<Integer>( de.length + df.length );
        for (int i = 0; i < de.length; i++ ) {
            key.add( new Integer( de[i] ) );
        }
        for (int i = 0; i < df.length; i++ ) {
            key.add( new Integer( df[i] ) );
        }
        return key;
    }


    /**
     * Size of the table.      
     * @return n number of non-commutative relations.
     */
    public int size() {
        int s = 0;
        if ( table == null ) {
            return s;
        }
        for ( Iterator<List> it = table.values().iterator(); it.hasNext(); ) {
            List list = it.next();
            s += list.size()/2;
        }
        return s;
    }


    /**
     * Extend variables. Used e.g. in module embedding.
     * Extend all ExpVectors and polynomials of the given 
     * relation table by i elements and put the relations into 
     * this table, i.e. this should be empty.
     * @param tab a relation table to be extended and inserted into this.
     */
    @SuppressWarnings("unchecked")
    public void extend(RelationTable<C> tab) {  
        if ( tab.table.size() == 0 ) {
            return;
        }
        // assert this.size() == 0
        int i = ring.nvar - tab.ring.nvar;
        int j = 0;
        long k = 0l;
        List val;
        for ( List<Integer> key: tab.table.keySet() ) { 
            val = tab.table.get( key );
            for ( Iterator jt = val.iterator(); jt.hasNext(); ) { 
                ExpVectorPair ep = (ExpVectorPair)jt.next();
                ExpVector e = ep.getFirst();
                ExpVector f = ep.getSecond();
                GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>)jt.next();
                ExpVector ex = e.extend(i,j,k); 
                ExpVector fx = f.extend(i,j,k); 
                GenSolvablePolynomial<C> px 
                   = (GenSolvablePolynomial<C>)p.extend(ring,j,k);
                this.update( ex, fx, px ); 
            }
        }
        return;
    }


    /**
     * Contract variables. Used e.g. in module embedding.
     * Contract all ExpVectors and polynomials of the given 
     * relation table by i elements and put the relations into 
     * this table, i.e. this should be empty.
     * @param tab a relation table to be contracted and inserted into this.
     */
    @SuppressWarnings("unchecked")
    public void contract(RelationTable<C> tab) { 
        if ( tab.table.size() == 0 ) {
            return;
        }
        // assert this.size() == 0
        int i = tab.ring.nvar - ring.nvar;
        List val;
        for ( List<Integer> key: tab.table.keySet() ) { 
            val = tab.table.get( key );
            for (Iterator jt = val.iterator(); jt.hasNext(); ) { 
                ExpVectorPair ep = (ExpVectorPair)jt.next();
                ExpVector e = ep.getFirst();
                ExpVector f = ep.getSecond();
                GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>)jt.next();
                ExpVector ec = e.contract(i,e.length()-i); 
                ExpVector fc = f.contract(i,f.length()-i); 
                if ( ec.isZERO() || fc.isZERO() ) {
                    continue;
                }
                Map<ExpVector,GenPolynomial<C>> mc = p.contract(ring);
                if ( mc.size() != 1 ) {
                    continue;
                }
                GenSolvablePolynomial<C> pc = null;
                for ( GenPolynomial<C> x : mc.values() ) {
                    if ( pc != null ) {
                       // should not happen 
                       logger.info("e = " + e + ", f = " + f + ", ec = " + ec + ", fc = " + fc);
                       logger.info("p = " + p + ", mc = " + mc);
                       throw new RuntimeException("Map.size() != 1: " + mc.size());
                    }
                    pc = (GenSolvablePolynomial<C>)x;
                }
                this.update( ec, fc, pc );
            }
        }
        return;
    }


    /**
     * Reverse variables and relations. Used e.g. in opposite rings.
     * Reverse all ExpVectors and polynomials of the given 
     * relation table and put the modified relations into this table, 
     * i.e. this should be empty.
     * @param tab a relation table to be reverted and inserted into this.
     */
    @SuppressWarnings("unchecked")
    public void reverse(RelationTable<C> tab) {  
        if ( tab.table.size() == 0 ) {
            return;
        }
        // assert this.size() == 0
        if ( table.size() != 0 ) {
           logger.error("reverse table not empty");         
        }
        int k = -1;
        if ( ring.tord.getEvord2() != 0 && ring.partial ) {
           k = ring.tord.getSplit();
        }
        logger.debug("k split = " + k );
        //System.out.println("k split = " + k );
        for ( List<Integer> key: tab.table.keySet() ) { 
            List val = tab.table.get( key );
            for ( Iterator jt = val.iterator(); jt.hasNext(); ) { 
                ExpVectorPair ep = (ExpVectorPair)jt.next();
                ExpVector e = ep.getFirst();
                ExpVector f = ep.getSecond();
                GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>)jt.next();
                //logger.info("e pre reverse = " + e );
                //logger.info("f pre reverse = " + f );
                //logger.info("p pre reverse = " + p );
                ExpVector ex; 
                ExpVector fx; 
                GenSolvablePolynomial<C> px; 
                boolean change = true; // if relevant vars reversed
                if ( k >= 0 ) {
                   ex = e.reverse(k); 
                   fx = f.reverse(k); 
                   int[] ed = ex.dependencyOnVariables(); // = e
                   if ( ed.length == 0 || ed[0] >= k ) { // k >= 0
                      change = false;
                   }
                   int[] fd = fx.dependencyOnVariables(); // = f
                   if ( fd.length == 0 || fd[0] >= k ) { // k >= 0
                      change = false;
                   }
                } else {
                   ex = e.reverse(); 
                   fx = f.reverse(); 
                }
                px = (GenSolvablePolynomial<C>)p.reverse(ring);
                //System.out.println("change = " + change );
                if ( ! change ) {
                   this.update( e, f, px ); // same order
                } else {
                   this.update( fx, ex, px ); // opposite order
                   //this.update( ex, fx, px ); // same order
                }
            }
        }
        return;
    }

}



/**
 * TableRelation container for storage and printing in RelationTable.
 * @author Heinz Kredel
 */

class TableRelation<C extends RingElem<C>> implements Serializable {

    /** First ExpVector of the data structure.
     */
    public final ExpVector e;


    /** Second ExpVector of the data structure.
     */
    public final ExpVector f;


    /** GenSolvablePolynomial of the data structure.
     */
    public final GenSolvablePolynomial<C> p;


    /**
     * Constructor to setup the data structure.
     * @param e first term.
     * @param f second term.
     * @param p product polynomial.
     */
    public TableRelation(ExpVector e, ExpVector f, 
                         GenSolvablePolynomial<C> p) {
        this.e = e;
        this.f = f;
        this.p = p;
    }


    /** Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
     public String toString() {
        StringBuffer s = new StringBuffer("TableRelation[");
        s.append(""+e);
        s.append(" | ");
        s.append(""+f);
        s.append(" = ");
        s.append(""+p);
        s.append("]");
        return s.toString();
    }

}
