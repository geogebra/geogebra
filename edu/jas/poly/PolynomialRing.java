/*
 * $Id: PolynomialRing.java 1801 2008-05-19 20:50:39Z kredel $
 */

package edu.jas.poly;


import java.util.List;
import java.util.Random;


import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Polynomial factory interface.
 * Defines polynomial specific factory methods.
 * @author Heinz Kredel
 */

public interface PolynomialRing<C extends RingElem<C>> 
                 extends RingFactory< Polynomial<C> > {


    /**
     * Number of variables.
     * @return the number of variables.
     */
    public int numberOfVariables();


    /** Get the variable names. 
     * @return vars.
     */
    public String[] getVars();


    /**
     * Generate a random polynomial.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     * @return a random polynomial.
     */
    public Polynomial<C> random(int k, int l, int d, float q);


    /**
     * Generate a random polynomial.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     * @param rnd is a source for random bits.
     * @return a random polynomial.
     */
    public Polynomial<C> random(int k, int l, int d, float q, Random rnd);


    /**
     * Generate univariate polynomial in a given variable.
     * @param i the index of the variable.
     * @return X_i as univariate polynomial.
     */
    public Polynomial<C> univariate(int i);


    /**
     * Generate univariate polynomial in a given variable with given exponent.
     * @param i the index of the variable.
     * @param e the exponent of the variable.
     * @return X_i^e as univariate polynomial.
     */
    public Polynomial<C> univariate(int i, long e);


    /**
     * Generate list of univariate polynomials in all variables.
     * @return List(X_1,...,X_n) a list of univariate polynomials.
     */
    public List<? extends Polynomial<C>> univariateList();


    /**
     * Extend variables. Used e.g. in module embedding.
     * Extend number of variables by i.
     * @param i number of variables to extend.
     * @return extended polynomial ring factory.
     */
    public PolynomialRing<C> extend(int i);


    /**
     * Contract variables. Used e.g. in module embedding.
     * Contract number of variables by i.
     * @param i number of variables to remove.
     * @return contracted polynomial ring factory.
     */
    public PolynomialRing<C> contract(int i);


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @return polynomial ring factory with reversed variables.
     */
    public PolynomialRing<C> reverse();


}
