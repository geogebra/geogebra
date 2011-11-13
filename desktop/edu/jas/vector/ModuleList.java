/*
 * $Id: ModuleList.java 2741 2009-07-12 18:13:07Z kredel $
 */

package edu.jas.vector;

import java.util.List;
import java.util.ArrayList;

import java.io.Serializable;

import org.apache.log4j.Logger;

//import edu.jas.structure.RingFactory;
import edu.jas.structure.RingElem;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;


/**
 * List of vectors of polynomials.
 * Mainly for storage and printing / toString and 
 * conversions to other representations.
 * @author Heinz Kredel
 */

public class ModuleList<C extends RingElem<C> > implements Serializable {


    /** The factory for the solvable polynomial ring. 
     */
    public final GenPolynomialRing< C > ring;


    /** The data structure is a List of Lists of polynomials. 
     */
    public final List< List< GenPolynomial<C> > > list;


    /** Number of rows in the data structure. 
     */
    public final int rows; // -1 is undefined


    /** Number of columns in the data structure. 
     */
    public final int cols; // -1 is undefined


    private static final Logger logger = Logger.getLogger(ModuleList.class);


    /**
     * Constructor.
     * @param r polynomial ring factory.
     * @param l list of list of polynomials.
     */
    public ModuleList( GenPolynomialRing< C > r,
                       List< List<GenPolynomial< C >>> l) {
        ring = r;
        list = ModuleList.<C>padCols(r,l); 
        if ( list == null ) {
            rows = -1; 
            cols = -1;
        } else {
            rows = list.size();
            if ( rows > 0 ) {
                cols = list.get(0).size();
            } else {
                cols = -1;
            }
        }
    }


    /**
     * Constructor.
     * @param r solvable polynomial ring factory.
     * @param l list of list of solvable polynomials.
     */
    public ModuleList( GenSolvablePolynomialRing< C > r,
                       List< List<GenSolvablePolynomial< C >>> l) {
        this( r, ModuleList.<C>castToList(l) );
    }


    /**
     * Constructor.
     * @param r polynomial ring factory.
     * @param l list of vectors of polynomials.
     */
    public ModuleList( GenVectorModul<GenPolynomial<C>> r,
                       List< GenVector<GenPolynomial< C >>> l) {
        this( (GenPolynomialRing<C>)r.coFac, ModuleList.<C>vecToList(l) );
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals(Object m) {
        if ( ! (m instanceof ModuleList) ) {
            //System.out.println("ModuleList");
            return false;
        }
        ModuleList<C> ml = null;
        try {
            ml = (ModuleList<C>)m;
        } catch (ClassCastException ignored) {
        }
        if ( ml == null ) { 
            return false;
        }
        if ( ! ring.equals( ml.ring ) ) {
            //System.out.println("Ring");
            return false;
        }
        if ( list == ml.list ) {
            return true;
        }
        if ( list == null && ml.list != null ) {
            //System.out.println("List, null");
            return false;
        }
        if ( list != null && ml.list == null ) {
            //System.out.println("List, null");
            return false;
        }
        if ( list.size() != ml.list.size() ) {
            //System.out.println("List, size");
            return false;
        }
        // compare sorted lists
        List otl = OrderedModuleList.sort( ring, list );
        List oml = OrderedModuleList.sort( ring, ml.list );
        if ( ! otl.equals(oml) ) {
            return false;
        }
        return true;
    }


    /** Hash code for this module list.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
       int h;
       h = ring.hashCode();
       h = 37 * h + ( list == null ? 0 : list.hashCode() );
       return h;
    }


    /**
     * String representation of the module list.
     * @see java.lang.Object#toString()
     */
    @Override
    //@SuppressWarnings("unchecked") 
    public String toString() {
        StringBuffer erg = new StringBuffer();
        String[] vars = null;
        if ( ring != null ) {
           erg.append( ring.toString() );
           vars = ring.getVars();
        }
        if ( list == null ) {
            erg.append(")");
            return erg.toString();
        }
        boolean first = true;
        erg.append("(\n");
        for ( List< GenPolynomial<C> > row: list ) {
            if ( first ) {
               first = false;
            } else {
               erg.append( ",\n" );
            }
            boolean ifirst = true;
            erg.append(" ( ");
            String os;
            for ( GenPolynomial<C> oa: row ) {
                if ( oa == null ) {
                   os = "0";
                } else if ( vars != null ) {
                   os = oa.toString(vars);
                } else {
                   os = oa.toString();
                }
                if ( ifirst ) {
                   ifirst = false;
                } else {
                   erg.append( ", " );
                   if ( os.length() > 100 ) {
                      erg.append("\n");
                   }
                }
                erg.append( os );
            }
            erg.append(" )");
        }
        erg.append("\n)");
        return erg.toString();
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this ModuleList.
     */
    public String toScript() {
        // Python case
        StringBuffer erg = new StringBuffer();
        if ( ring instanceof GenSolvablePolynomialRing ) {
            erg.append("SolvableSubModule(");
        } else {
            erg.append("SubModule(");
        }
        String[] vars = null;
        if ( ring != null ) {
           erg.append( ring.toScript() );
           vars = ring.getVars();
        } else {
            // should not happen
        }
        if ( list == null ) {
            erg.append(")");
            return erg.toString();
        }
        erg.append(",list=[");
        boolean first = true;
        for ( List< GenPolynomial<C> > row: list ) {
            if ( first ) {
               first = false;
            } else {
               erg.append( "," );
            }
            boolean ifirst = true;
            erg.append(" ( ");
            String os;
            for ( GenPolynomial<C> oa: row ) {
                if ( oa == null ) {
                   os = "0";
                } else {
                   os = oa.toScript();
                }
                if ( ifirst ) {
                   ifirst = false;
                } else {
                   erg.append( ", " );
                }
                erg.append( os );
            }
            erg.append(" )");
        }
        erg.append(" ])");
        return erg.toString();
    }


    /**
     * Pad columns and remove zero rows.
     * Make all rows have the same number of columns.
     * @param ring polynomial ring factory.
     * @param l list of list of polynomials.
     * @return list of list of polynomials with same number of colums.
     */
    public static <C extends RingElem<C> >
        List< List<GenPolynomial< C >>> 
                      padCols(GenPolynomialRing< C > ring,
                              List< List<GenPolynomial< C >>> l) {
        if ( l == null ) {
           return l;
        }
        int mcols = 0;
        int rs = 0;
        for ( List< GenPolynomial<C> > row: l ) {
            if ( row != null ) {
               rs++;
               if ( row.size() > mcols ) {
                  mcols = row.size();
               }
            }
        }
        List< List<GenPolynomial<C>> > norm 
            = new ArrayList< List<GenPolynomial<C>> >( rs );
        for ( List< GenPolynomial<C> > row: l ) {
            if ( row != null ) {
                List<GenPolynomial<C>> rn 
                    = new ArrayList<GenPolynomial<C>>( row );
                while ( rn.size() < mcols ) {
                    rn.add( ring.getZERO() );
                }
                norm.add( rn );
            }
        }
        return norm;
    }



    /**
     * Get PolynomialList.
     * Embed module in a polynomial ring. 
     * @see edu.jas.poly.PolynomialList
     * @return polynomial list corresponding to this.
     */
    public PolynomialList<C> getPolynomialList() {
        GenPolynomialRing< C > pfac = ring.extend(cols);
        logger.debug("extended ring = " + pfac);
        //System.out.println("extended ring = " + pfac);

        List<GenPolynomial<C>> pols = null;
        if ( list == null ) { // rows < 0
           return new PolynomialList<C>(pfac,pols);
        }
        pols = new ArrayList<GenPolynomial<C>>( rows );
        if ( rows == 0 ) { // nothing to do
           return new PolynomialList<C>(pfac,pols);
        }

        GenPolynomial<C> zero = pfac.getZERO();
        GenPolynomial<C> d = null;
        for ( List<GenPolynomial<C>> r: list ) {
            GenPolynomial<C> ext = zero;
            //int m = cols-1;
            int m = 0;
            for ( GenPolynomial<C> c: r ) {
                d = c.extend( pfac, m, 1l );
                ext = ext.sum(d); 
                m++;
            }
            pols.add( ext );
        }
        return new PolynomialList<C>(pfac, pols);
    }


    /**
     * Get list as List of GenSolvablePolynomials.
     * Required because no List casts allowed. Equivalent to 
     * cast (List&lt;List&lt;GenSolvablePolynomial&lt;C&gt;&gt;&gt;) list.
     * @return list of solvable polynomial lists from this.
     */
    public List< List< GenSolvablePolynomial<C> > > castToSolvableList() {
        List< List<GenSolvablePolynomial<C>> > slist = null;
        if ( list == null ) {
            return slist;
        }
        slist = new ArrayList< List<GenSolvablePolynomial<C>> >( list.size() ); 
        for ( List< GenPolynomial<C>> row: list ) {
            List< GenSolvablePolynomial<C> > srow 
                = new ArrayList< GenSolvablePolynomial<C> >( row.size() ); 
            for ( GenPolynomial<C> p: row ) {
                if ( ! (p instanceof GenSolvablePolynomial) ) {
                    throw new RuntimeException("no solvable polynomial "+p);
                }
                GenSolvablePolynomial<C> s
                    = (GenSolvablePolynomial<C>) p;
                srow.add( s );
            }
            slist.add( srow );
        }
        return slist;
    }


    /**
     * Get a solvable polynomials list as List of GenPolynomials.
     * Required because no List casts allowed. Equivalent to 
     * cast (List&lt;List&lt;GenPolynomial&lt;C&gt;&gt;&gt;) list.
     * @param slist list of solvable polynomial lists.
     * @return list of polynomial lists from slist.
     */
    public static <C extends RingElem<C> >
           List< List< GenPolynomial<C> > > 
           castToList( List< List<GenSolvablePolynomial<C>> > slist ) {
        List< List<GenPolynomial<C>> > list = null;
        if ( slist == null ) {
            return list;
        }
        list = new ArrayList< List<GenPolynomial<C>> >( slist.size() ); 
        for ( List< GenSolvablePolynomial<C>> srow: slist ) {
            List< GenPolynomial<C> > row 
                = new ArrayList< GenPolynomial<C> >( srow.size() ); 
            for ( GenSolvablePolynomial<C> s: srow ) {
                row.add( s );
            }
            list.add( row );
        }
        return list;
    }


    /**
     * Get a list of vectors as List of list of GenPolynomials.
     * @param vlist list of vectors of polynomials.
     * @return list of polynomial lists from vlist.
     */
    public static <C extends RingElem<C> >
           List< List< GenPolynomial<C> > > 
           vecToList( List< GenVector<GenPolynomial<C>> > vlist ) {
        List< List<GenPolynomial<C>> > list = null;
        if ( vlist == null ) {
            return list;
        }
        list = new ArrayList< List<GenPolynomial<C>> >( vlist.size() ); 
        for ( GenVector< GenPolynomial<C>> srow: vlist ) {
            List< GenPolynomial<C> > row = srow.val; 
            list.add( row );
        }
        return list;
    }

}
