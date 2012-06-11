/**
 * 
 */
package geogebra.common.kernel.locusequ.arith;

import geogebra.common.kernel.locusequ.EquationPoint;

/**
 * @author sergio
 * A class that should be static imported when you
 * want to make some calculations.
 * 
 * I seriously considered to make all classes but this
 * and EquationExpression package private.
 */
public class EquationArithHelper {

    
    public final static String SUM = "+";
    public final static String DIFF = "-";
    public final static String TIMES = "*";
    public final static String DIV = "/";
    public final static String POW = "^";
    
    /**
     * @param equ Expression to be wrapped.
     * @return An Equation object wrapping EquationExpression.
     */
    public static Equation equation(EquationExpression equ) {
        return new Equation(equ);
    }
    
    /**
     * @param x Expression to be multiplied by itself.
     * @return x * x Expression.
     */
    public static EquationExpression sqr(EquationExpression x) {
        return times(x,x);
    }
    
    /**dbl(EquationExpression x)
     * @param x Expression to be doubled.
     * @return 2 * x Expression
     */
    public static EquationExpression dbl(EquationExpression x) {
        return times(EquationNumericValue.from(2), x);
    }

    /**
     * @param x Original expression.
     * @return Square Root of x.
     */
    public static EquationExpression sqrt(EquationExpression x) {
        return new EquationSqrtOperator(x);
    }
    
    /**
     * Deprecated. Use getInverse in x instead.
     * @param x Original Expression.
     * @return x^-1
     */
    @Deprecated
    public static EquationExpression inverse(EquationExpression x) {
        return x.getInverse();
    }
    
    
    /**
     * @param base Base Expression.
     * @param exp Exp Expression.
     * @return base ^ exp
     */
    public static EquationExpression pow(EquationExpression base, EquationExpression exp) {
        return new EquationExpOperator(base, exp);
    }
    
    /**
     * @param x Original Expression
     * @return |x|
     */
    public static EquationExpression abs(EquationExpression x) {
        return new EquationAbsOperator(x);
    }
    
    /**
     * Makes the middle param to be the middle between extrm1 and extrm2
     * @param extrm1 one of the extremes.
     * @param extrm2 the other extreme.
     * @param middle the expression that will be set as the middle.
     * @return middle - (extrm1+extrm2)/2
     */
    public static EquationExpression middle(EquationExpression extrm1, EquationExpression extrm2, EquationExpression middle) {
        return diff(middle,
                div(sum(extrm1, extrm2), EquationNumericValue.from(2)));
    }
    
    /**
     * Works out the order-3 determinant. Elements are a_{row,column}.
     * @param a1 a_{1,1}
     * @param a2 a_{1,2}
     * @param a3 a_{1,3}
     * @param a4 a_{2,1}
     * @param a5 a_{2,2}
     * @param a6 a_{2,3}
     * @param a7 a_{3,1}
     * @param a8 a_{3,2}
     * @param a9 a_{3,3}
     * @return determinant of previous matrix.
     */
    public static EquationExpression det3(EquationExpression a1, EquationExpression a2, EquationExpression a3, EquationExpression a4, EquationExpression a5, EquationExpression a6, EquationExpression a7, EquationExpression a8, EquationExpression a9) {
        return diff(
                sum(times(a1,a5,a9),
                    times(a3,a4,a8),
                    times(a2,a6,a7)),
                sum(times(a3,a5,a7),
                    times(a1,a6,a8),
                    times(a2,a4,a9)));
    }
    
    public static EquationExpression det4(EquationExpression a1, EquationExpression a2, EquationExpression a3, EquationExpression a4, EquationExpression a5, EquationExpression a6, EquationExpression a7, EquationExpression a8, EquationExpression a9,
            EquationExpression a10, EquationExpression a11, EquationExpression a12, EquationExpression a13, EquationExpression a14, EquationExpression a15, EquationExpression a16) {
        return sum( times(a1, det3(a6 , a7,  a8,
                                   a10, a11, a12,
                                   a14, a15, a16)),
                    times(a2, det3(a5,  a7,  a8,
                                   a9,  a11, a12,
                                   a13, a15, a16)).getOpposite(),
                    times(a3, det3(a5,  a6,  a8,
                                   a9,  a10, a12,
                                   a13, a14, a16)),
                    times(a4, det3(a5,  a6,  a7,
                                   a9,  a10, a11,
                                   a13, a14, a15)).getOpposite());
    }
    
    public static EquationExpression det5(EquationExpression a1, EquationExpression a2, EquationExpression a3, EquationExpression a4, EquationExpression a5, EquationExpression a6, EquationExpression a7, EquationExpression a8, EquationExpression a9,
            EquationExpression a10, EquationExpression a11, EquationExpression a12, EquationExpression a13, EquationExpression a14, EquationExpression a15, EquationExpression a16, EquationExpression a17, EquationExpression a18,
            EquationExpression a19, EquationExpression a20, EquationExpression a21, EquationExpression a22,  EquationExpression a23, EquationExpression a24, EquationExpression a25) {
        return sum(times(a1, det4(a7,  a8,  a9,  a10,
                                  a12, a13, a14, a15,
                                  a17, a18, a19, a20,
                                  a22, a23, a24,  a25)),
                   times(a2, det4(a6,  a8,  a9,  a10,
                                  a11, a13, a14, a15,
                                  a16, a18, a19, a20,
                                  a21, a23, a24, a25)).getOpposite(),
                   times(a3, det4(a6,  a7,  a9,  a10,
                                  a11, a12, a14, a15,
                                  a16, a17, a19, a20,
                                  a21, a22, a24, a25)),
                   times(a4, det4(a6,  a7,  a8,  a10,
                                  a11, a12, a13, a15,
                                  a16, a17, a18, a20,
                                  a21, a22, a23, a25)).getOpposite(),
                   times(a5, det4(a6,  a7,  a8,  a9,
                                  a11, a12, a13, a14,
                                  a16, a17, a18, a19,
                                  a21, a22, a23, a24)));
    }

    /**
     * @param args EquationExpression to be "summed".
     * @return A EquationExpression like "(args[0]+args[1]+...)".
     */
    public static EquationExpression sum(EquationExpression... args) {
        switch(args.length) {
        case 0:
            return EquationNumericValue.from(0);
        case 1:
            return args[0];
        case 2:
            return new EquationSumOperator(args[0], args[1]);
        default:
            EquationExpression[] args2 = new EquationExpression[args.length-1];
            System.arraycopy(args, 1, args2, 0, args.length-1);
            return new EquationSumOperator(args[0], sum(args2));
        }
    }
    
    /**
     * @param args EquationExpression to be "substracted".
     * @return A EquationExpression like "(args[0]-(args[1]+...+args[args.length-1]))".
     */
    public static EquationExpression diff(EquationExpression... args) {
        switch(args.length) {
        case 0:
            return EquationNumericValue.from(0);
        case 1:
            return args[0];
        case 2:
            return new EquationDiffOperator(args[0], args[1]);
        default:
            EquationExpression[] args2 = new EquationExpression[args.length-1];
            System.arraycopy(args, 1, args2, 0, args.length-1);
            return new EquationDiffOperator(args[0], sum(args2)); // You can think about this.
                                                                  // But you cannot change it.
        }
    }
    
    /**
     * @param args EquationExpression to be "multiplied".
     * @return A EquationExpression like "(args[0]*args[1]*...)".
     */
    public static EquationExpression times(EquationExpression...  args) {
        switch(args.length) {
        case 0:
            return EquationNumericValue.from(1);
        case 1:
            return args[0];
        case 2:
            return new EquationProductOperator(args[0], args[1]);
        default:
            EquationExpression[] args2 = new EquationExpression[args.length-1];
            System.arraycopy(args, 1, args2, 0, args.length-1);
            return new EquationProductOperator(args[0], times(args2));
        }
    }
    
    /**
     * @param numerator Numerator.
     * @param denominator Denominator
     * @return A EquationExpression like "numerator/denominator".
     */
    public static EquationExpression div(EquationExpression numerator, EquationExpression denominator) {
        return new EquationDivOperator(numerator, denominator);
    }
    
    /**
     * Calculates the distance between two points.
     * @param a one point
     * @param b other point
     * @return the distance between a and b.
     */
    public static EquationExpression dist(final EquationPoint a, final EquationPoint b) {
        return sqrt(dist2(a,b));
    }
    
    
    /**
     * Returns the distance squared.
     * @param a one point.
     * @param b other points.
     * @return square of distance between a and b.
     */
    public static EquationExpression dist2(final EquationPoint a, final EquationPoint b) {
        EquationExpression x = diff(a.getXExpression(), b.getXExpression());
        EquationExpression y = diff(a.getYExpression(), b.getYExpression());
        return sum(times(x,x), times(y,y));
    }
}
