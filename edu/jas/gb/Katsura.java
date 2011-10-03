/*
 * Created on 03.10.2004
 * $Id: Katsura.java 3058 2010-03-27 11:05:23Z kredel $
  */

package edu.jas.gb;

/**
 * Class to produce a system of equations as defined by Katsura.
 * 
 * @author Heinz Kredel
 *
 */
public class Katsura {

   /**
    * main.
    */
    public static void main(String[] args) {
        if ( args.length == 0 ) {
           System.out.println("usage: Katsura N <order> <var>");
           return;
        }
        int n = Integer.parseInt(args[0]);
        Katsura k = null;
        if ( args.length == 1 ) {               
           k = new Katsura(n);
        }
        if ( args.length == 2 ) {               
           k = new Katsura("u",n, args[1]);
        }
        if ( args.length == 3 ) {               
        k = new Katsura(args[2],n, args[1]);
                        }
        System.out.println("#Katsura equations for N = " + n + ":");
                System.out.println("" + k);
    }

    final int N;
    final String var;
    final String order;


    /**
     * Katsura constructor.
     * @param n problem size.
     */
    public Katsura(int n) {
           this("u", n);
    }


    /**
     * Katsura constructor.
     * @param v name of variables.
     * @param n problem size.
     */
    public Katsura(String v, int n) {
           this(v, n, "G");
    }


    /**
     * Katsura constructor.
     * @param var name of variables.
     * @param n problem size.
     * @param order term order letter for output.
     */
    public Katsura(String var, int n, String order) {
           this.var = var;
           this.N = n;
           this.order = order;
    }


    String sum1() {
           StringBuffer s = new StringBuffer();
           for (int i = -N; i <= N; i++) {
               s.append(variable(i));
               if (i < N) {
                   s.append(" + ");
               }
           }
           s.append(" - 1");
           return s.toString();
    }


    String sumUm(int m) {
           StringBuffer s = new StringBuffer();
           for (int i = -N; i <= N; i++) {
               s.append(variable(i));
               s.append("*");
               s.append(variable(m - i));
               if (i < N) {
                  s.append(" + ");
               }
           }
           s.append(" - " + variable(m));
           return s.toString();
    }


    /**
     * Generate variable list.
     * @param order term order letter.
     * @return polynomial ring description.
     */
    public String varList(String order) {
        return varList("Rat",order);
    }


    /**
     * Generate variable list.
     * @param order term order letter.
     * @param coeff coefficient ring name.
     * @return polynomial ring description.
     */
    public String varList(String coeff, String order) {
           StringBuffer s = new StringBuffer();
           s.append(coeff);
           s.append("(");
           // for (int i = 0; i <= N; i++) {
           for (int i = N; i >=0; i--) {
               s.append(variable(i));
               if (i > 0) {
                  s.append(",");
               }
           }
           s.append(")  ");
           s.append(order);
           return s.toString();
    }


    /**
     * toString.
     * @return Katsura problem as string.
     */
    @Override
     public String toString() {
           StringBuffer s = new StringBuffer();
           s.append(varList(order));
           s.append(System.getProperty("line.separator"));
           s.append(polyList());
           return s.toString();
    }


    /**
     * Generate polynomial list.
     * @return Katsura polynomials as string.
     */
    public String polyList() {
           StringBuffer s = new StringBuffer();
           s.append("("+System.getProperty("line.separator"));
           //for (int m = -N + 1; m <= N - 1; m++) { doubles polynomials
           for (int m = 0; m <= N - 1; m++) {
               s.append( sumUm(m) );
               s.append(","+System.getProperty("line.separator"));
           }
           s.append( sum1() );
           s.append(System.getProperty("line.separator"));
           s.append(")"+System.getProperty("line.separator"));
           return s.toString();
    }


    /**
     * Generate variable string.
     * @return varaible name as string.
     */
    String variable(int i) {
           if (i < 0) {
               return variable(-i);
           }
           if (i > N) {
               return "0";
           }
           return var + i;
    }

}
