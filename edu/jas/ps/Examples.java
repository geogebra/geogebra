/*
 * $Id: Examples.java 2639 2009-05-24 17:50:00Z kredel $
 */

package edu.jas.ps;


import edu.jas.arith.BigComplex;
import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.structure.BinaryFunctor;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.Selector;
import edu.jas.structure.UnaryFunctor;


/**
 * Examples for univariate power series implementations.
 * @author Heinz Kredel
 */

public class Examples {


    public static void main(String[] args) {
        example2();
        example4();
        example6();
        example8();
        example9();
        example10();
        example11();
        example1();
        example3();
        example5();
        example7();
        //example12();
        example13();
    }


    static UnivPowerSeries<BigInteger> integersFrom(final int start) {
        UnivPowerSeriesRing<BigInteger> pfac = new UnivPowerSeriesRing<BigInteger>(new BigInteger());
        return new UnivPowerSeries<BigInteger>(pfac, new Coefficients<BigInteger>() {


            @Override
            public BigInteger generate(int i) {
                return new BigInteger(i);
            }
        });
    }


    //----------------------


    static class Sum<C extends RingElem<C>> implements BinaryFunctor<C, C, C> {


        public C eval(C c1, C c2) {
            return c1.sum(c2);
        }
    }


    static class Odds<C extends RingElem<C>> implements Selector<C> {


        C two;


        RingFactory<C> fac;


        public Odds(RingFactory<C> fac) {
            this.fac = fac;
            two = fac.fromInteger(2);
            //System.out.println("two = " + two);
        }


        public boolean select(C c) {
            //System.out.print("c = " + c);
            if (c.remainder(two).isONE()) {
                //System.out.println(" odd");
                return true;
            } else {
                //System.out.println(" even");
                return false;
            }
        }
    }


    //----------------------


    public static void example1() {
        UnivPowerSeries<BigInteger> integers = integersFrom(0);

        BigInteger e = new BigInteger(1);
        BigInteger v = integers.evaluate(e);
        System.out.println("integers(" + e + ") = " + v);
        e = new BigInteger(0);
        v = integers.evaluate(e);
        System.out.println("integers(" + e + ") = " + v);
        e = new BigInteger(2);
        v = integers.evaluate(e);
        System.out.println("integers(" + e + ") = " + v);
    }


    public static void example2() {
        UnivPowerSeries<BigInteger> integers = integersFrom(0);

        System.out.print("integer coefficients = ");
        UnivPowerSeries<BigInteger> s = integers;
        for (int i = 0; i < 20; i++) {
            BigInteger c = s.leadingCoefficient();
            System.out.print(c.toString() + ", ");
            s = s.reductum();
        }
        System.out.println("...");
    }


    public static void example3() {
        RingFactory<BigInteger> fac = new BigInteger(1);
        UnivPowerSeriesRing<BigInteger> ups = new UnivPowerSeriesRing<BigInteger>(fac);
        System.out.println("ups = " + ups);
        System.out.println("ups.isCommutative() = " + ups.isCommutative());
        System.out.println("ups.isAssociative() = " + ups.isAssociative());
        System.out.println("ups.isField()       = " + ups.isField());

        System.out.println("ups.getZERO()       = " + ups.getZERO());
        System.out.println("ups.getONE()        = " + ups.getONE());

        UnivPowerSeries<BigInteger> rnd = ups.random();
        System.out.println("rnd = " + rnd);
        System.out.println("rnd = " + rnd);
        System.out.println("rnd.isUnit()       = " + rnd.isUnit());
    }


    public static void example4() {
        UnivPowerSeries<BigInteger> integers = integersFrom(0);
        System.out.println("integers = " + integers);
    }


    public static void example6() {
        UnivPowerSeriesRing<BigInteger> pfac = new UnivPowerSeriesRing<BigInteger>(new BigInteger());
        UnivPowerSeries<BigInteger> integers;
        integers = pfac.fixPoint(new PowerSeriesMap<BigInteger>() {


            public UnivPowerSeries<BigInteger> map(UnivPowerSeries<BigInteger> ps) {
                return ps.map(new UnaryFunctor<BigInteger, BigInteger>() {


                    public BigInteger eval(BigInteger s) {
                        return s.sum(new BigInteger(1));
                    }
                }).prepend(new BigInteger(0));
            }
        });
        System.out.println("integers1 = " + integers);
        System.out.println("integers2 = " + integers);
    }


    public static void example8() {
        final BigInteger z = new BigInteger(0);
        final BigInteger one = new BigInteger(1);
        UnivPowerSeriesRing<BigInteger> pfac = new UnivPowerSeriesRing<BigInteger>(z);
        UnivPowerSeries<BigInteger> fibs;
        fibs = pfac.fixPoint(new PowerSeriesMap<BigInteger>() {


            public UnivPowerSeries<BigInteger> map(UnivPowerSeries<BigInteger> ps) {
                return ps.zip(new Sum<BigInteger>(), ps.prepend(one)).prepend(z);
            }
        });
        System.out.println("fibs1 = " + fibs.toString(/*20*/));
        System.out.println("fibs2 = " + fibs.toString(/*20*/));
    }


    public static void example9() {
        UnivPowerSeries<BigInteger> integers = integersFrom(0);
        System.out.println("      integers = " + integers);
        UnivPowerSeries<BigInteger> doubleintegers = integers.sum(integers);
        System.out.println("doubleintegers = " + doubleintegers);
        UnivPowerSeries<BigInteger> nulls = integers.subtract(integers);
        System.out.println("null  integers = " + nulls);
        doubleintegers = integers.multiply(new BigInteger(2));
        System.out.println("doubleintegers = " + doubleintegers);
        nulls = integers.multiply(new BigInteger(0));
        System.out.println("null  integers = " + nulls);
        UnivPowerSeries<BigInteger> odds = integers.select(new Odds<BigInteger>(new BigInteger()));
        System.out.println("odd   integers = " + odds);
    }


    public static void example10() {
        final BigInteger fac = new BigInteger();
        UnivPowerSeriesRing<BigInteger> pfac = new UnivPowerSeriesRing<BigInteger>(fac);
        UnivPowerSeries<BigInteger> integers = integersFrom(0);
        System.out.println("      integers = " + integers);
        UnivPowerSeries<BigInteger> ONE = new UnivPowerSeries<BigInteger>(pfac,
                new Coefficients<BigInteger>() {


                    @Override
                    public BigInteger generate(int i) {
                        if (i == 0) {
                            return fac.getONE();
                        } else {
                            return fac.getZERO();
                        }
                    }
                }//, null
        );
        System.out.println("ONE  = " + ONE);
        UnivPowerSeries<BigInteger> ZERO = new UnivPowerSeries<BigInteger>(pfac,
                new Coefficients<BigInteger>() {


                    @Override
                    public BigInteger generate(int i) {
                        return fac.getZERO();
                    }
                }//, null
        );
        System.out.println("ZERO = " + ZERO);
        UnivPowerSeries<BigInteger> ints = integers.multiply(ONE);
        System.out.println("integers       = " + ints);
        UnivPowerSeries<BigInteger> nulls = integers.multiply(ZERO);
        System.out.println("null  integers = " + nulls);
        UnivPowerSeries<BigInteger> nints = integers.negate();
        System.out.println("-integers      = " + nints);
        UnivPowerSeries<BigInteger> one = ONE.multiply(ONE);
        System.out.println("integers one   = " + one);
        UnivPowerSeries<BigInteger> ints2 = integers.multiply(integers);
        System.out.println("integers 2     = " + ints2);
        UnivPowerSeries<BigInteger> inv1 = ONE.inverse();
        System.out.println("integers inv1  = " + inv1);
        UnivPowerSeries<BigInteger> int1 = integers.reductum();
        System.out.println("integers int1  = " + int1);
        UnivPowerSeries<BigInteger> intinv = int1.inverse();
        System.out.println("integers intinv = " + intinv);
        UnivPowerSeries<BigInteger> one1 = int1.multiply(intinv);
        System.out.println("integers one1  = " + one1);
        UnivPowerSeries<BigInteger> ii = intinv.inverse();
        System.out.println("integers ii    = " + ii);
        UnivPowerSeries<BigInteger> rem = integers.subtract(integers.divide(int1).multiply(int1));
        System.out.println("integers rem   = " + rem);
    }


    public static void example11() {
        final BigInteger fac = new BigInteger();
        UnivPowerSeries<BigInteger> integers = integersFrom(0);
        System.out.println("      integers = " + integers);
        UnivPowerSeries<BigInteger> int2 = integers.multiply(new BigInteger(2));
        System.out.println("    2*integers = " + int2);
        System.out.println("integers < 2*integers  = " + integers.compareTo(int2));
        System.out.println("2*integers > integers  = " + int2.compareTo(integers));
        System.out.println("2*integers == integers = " + int2.equals(integers));
        System.out.println("integers == integers   = " + integers.equals(integers));
        System.out.println("integers.hashCode()    = " + integers.hashCode());
    }


    public static void example5() {
        final BigInteger fac = new BigInteger();
        UnivPowerSeriesRing<BigInteger> pfac = new UnivPowerSeriesRing<BigInteger>(fac);
        UnivPowerSeries<BigInteger> integers = integersFrom(0);
        System.out.println("      integers = " + integers);
        UnivPowerSeries<BigInteger> ints2 = integers.multiply(integers);
        System.out.println("integers 2     = " + ints2);

        UnivPowerSeries<BigInteger> q1 = ints2.divide(integers);
        System.out.println("q1             = " + q1);
        UnivPowerSeries<BigInteger> q2 = integers.divide(ints2);
        System.out.println("q2             = " + q2);

        UnivPowerSeries<BigInteger> r1 = ints2.remainder(integers);
        System.out.println("r1             = " + r1);
        UnivPowerSeries<BigInteger> r2 = integers.remainder(ints2);
        System.out.println("r2             = " + r2);

        UnivPowerSeries<BigInteger> qr1 = q1.multiply(integers).sum(r1);
        System.out.println("qr1            = " + qr1);
        UnivPowerSeries<BigInteger> qr2 = q2.multiply(ints2).sum(r2);
        System.out.println("qr2            = " + qr2);

        System.out.println("sign(qr1-ints2) = " + qr1.compareTo(ints2));
        System.out.println("sign(qr2-integers) = " + qr2.compareTo(integers));

        UnivPowerSeries<BigInteger> g = ints2.gcd(integers);
        System.out.println("g               = " + g);
    }


    public static void example7() {
        final BigRational fac = new BigRational();
        final UnivPowerSeriesRing<BigRational> pfac = new UnivPowerSeriesRing<BigRational>(fac, 11, "y");
        UnivPowerSeries<BigRational> exp = pfac.fixPoint(new PowerSeriesMap<BigRational>() {


            public UnivPowerSeries<BigRational> map(UnivPowerSeries<BigRational> e) {
                return e.integrate(fac.getONE());
            }
        });
        System.out.println("exp = " + exp);
        UnivPowerSeries<BigRational> tan = pfac.fixPoint(new PowerSeriesMap<BigRational>() {


            public UnivPowerSeries<BigRational> map(UnivPowerSeries<BigRational> t) {
                return t.multiply(t).sum(pfac.getONE()).integrate(fac.getZERO());
            }
        });
        System.out.println("tan = " + tan);
        UnivPowerSeries<BigRational> sin = new UnivPowerSeries<BigRational>(pfac,
                new Coefficients<BigRational>() {


                    @Override
                    public BigRational generate(int i) {
                        BigRational c;
                        if (i == 0) {
                            c = fac.getZERO();
                        } else if (i == 1) {
                            c = fac.getONE();
                        } else {
                            c = get(i - 2).negate();
                            c = c.divide(fac.fromInteger(i)).divide(fac.fromInteger(i - 1));
                        }
                        return c;
                    }
                });
        System.out.println("sin = " + sin);
        UnivPowerSeries<BigRational> sin1 = pfac.fixPoint(new PowerSeriesMap<BigRational>() {


            public UnivPowerSeries<BigRational> map(UnivPowerSeries<BigRational> e) {
                return e.negate().integrate(fac.getONE()).integrate(fac.getZERO());
            }
        });
        System.out.println("sin1 = " + sin1);

        UnivPowerSeries<BigRational> cos = new UnivPowerSeries<BigRational>(pfac,
                new Coefficients<BigRational>() {


                    @Override
                    public BigRational generate(int i) {
                        BigRational c;
                        if (i == 0) {
                            c = fac.getONE();
                        } else if (i == 1) {
                            c = fac.getZERO();
                        } else {
                            c = get(i - 2).negate();
                            c = c.divide(fac.fromInteger(i)).divide(fac.fromInteger(i - 1));
                        }
                        return c;
                    }
                });
        System.out.println("cos = " + cos);
        UnivPowerSeries<BigRational> cos1 = pfac.fixPoint(new PowerSeriesMap<BigRational>() {


            public UnivPowerSeries<BigRational> map(UnivPowerSeries<BigRational> e) {
                return e.negate().integrate(fac.getZERO()).integrate(fac.getONE());
            }
        });
        System.out.println("cos1 = " + cos1);

        UnivPowerSeries<BigRational> cos2 = pfac.solveODE( sin1.negate(), fac.getONE() );
        System.out.println("cos2 = " + cos2);

        UnivPowerSeries<BigRational> sin2 = pfac.solveODE( cos1, fac.getZERO() );
        System.out.println("sin2 = " + sin2);

        UnivPowerSeries<BigRational> sinh = new UnivPowerSeries<BigRational>(pfac,
                new Coefficients<BigRational>() {


                    @Override
                    public BigRational generate(int i) {
                        BigRational c;
                        if (i == 0) {
                            c = fac.getZERO();
                        } else if (i == 1) {
                            c = fac.getONE();
                        } else {
                            c = get(i - 2);
                            c = c.divide(fac.fromInteger(i)).divide(fac.fromInteger(i - 1));
                        }
                        return c;
                    }
                });
        System.out.println("sinh = " + sinh);
        UnivPowerSeries<BigRational> cosh = new UnivPowerSeries<BigRational>(pfac,
                new Coefficients<BigRational>() {


                    @Override
                    public BigRational generate(int i) {
                        BigRational c;
                        if (i == 0) {
                            c = fac.getONE();
                        } else if (i == 1) {
                            c = fac.getZERO();
                        } else {
                            c = get(i - 2);
                            c = c.divide(fac.fromInteger(i)).divide(fac.fromInteger(i - 1));
                        }
                        return c;
                    }
                }//, null
        );
        System.out.println("cosh = " + cosh);

        UnivPowerSeries<BigRational> sinhcosh = sinh.sum(cosh);
        System.out.println("sinh+cosh = " + sinhcosh);
        System.out.println("sinh+cosh == exp: " + sinhcosh.equals(exp));
    }


    public static void example12() {
        final BigComplex fac = new BigComplex();
        final BigComplex I = BigComplex.I;
        final UnivPowerSeriesRing<BigComplex> pfac = new UnivPowerSeriesRing<BigComplex>(fac);
        UnivPowerSeries<BigComplex> exp = pfac.fixPoint(new PowerSeriesMap<BigComplex>() {


            public UnivPowerSeries<BigComplex> map(UnivPowerSeries<BigComplex> e) {
                return e.integrate(I);
            }
        });
        //System.out.println("exp = " + exp);

        UnivPowerSeries<BigComplex> sin = pfac.fixPoint(new PowerSeriesMap<BigComplex>() {


            public UnivPowerSeries<BigComplex> map(UnivPowerSeries<BigComplex> e) {
                return e.negate().integrate(fac.getONE()).integrate(fac.getZERO());
            }
        });
        System.out.println("sin = " + sin);

        UnivPowerSeries<BigComplex> cos = pfac.fixPoint(new PowerSeriesMap<BigComplex>() {


            public UnivPowerSeries<BigComplex> map(UnivPowerSeries<BigComplex> e) {
                return e.negate().integrate(fac.getZERO()).integrate(fac.getONE());
            }
        });
        System.out.println("cos = " + cos);

        UnivPowerSeries<BigComplex> cpis = cos.sum(sin.multiply(I));
        System.out.println("cpis = " + cpis);
    }


    public static void example13() {
        BigRational fac = new BigRational();
        UnivPowerSeriesRing<BigRational> pfac = new UnivPowerSeriesRing<BigRational>(fac, 11, "y");
        UnivPowerSeries<BigRational> exp = pfac.getEXP();
        System.out.println("exp = " + exp);
        System.out.println("exp(1) = " + exp.evaluate(fac.getONE()));
        System.out.println("exp(0) = " + exp.evaluate(fac.getZERO()));

        BigDecimal dfac = new BigDecimal();
        UnivPowerSeriesRing<BigDecimal> dpfac = new UnivPowerSeriesRing<BigDecimal>(dfac, 11, "z");
        UnivPowerSeries<BigDecimal> dexp = dpfac.getEXP();
        System.out.println("exp = " + dexp);
        System.out.println("exp(1) = " + dexp.evaluate(dfac.getONE()));
        System.out.println("exp(0) = " + dexp.evaluate(dfac.getZERO()));
    }

}
