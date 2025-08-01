package org.geogebra.common.kernel.geos;

import static org.geogebra.test.TestStringUtil.unicode;
import static org.hamcrest.Matchers.oneOf;

import org.hamcrest.Matcher;
import org.junit.Test;

public class CommandFilterCASTest extends BaseSymbolicTest {

    @Test
    public void cmdInvert() {
        t("Invert({{1, 2}, {3, 4}})", "{{-2, 1}, {3 / 2, -1 / 2}}");
        t("Invert({{a, b}, {c, d}})", "{{d / (a * d - b * c), (-b) / (a * d - b * c)},"
                + " {(-c) / (a * d - b * c), a / (a * d - b * c)}}");
        Matcher<String> expected = oneOf("-sin⁻¹(x) + 2 * k_{1} * π + π",
                "2 * k_{1} * π - sin⁻¹(x) + π");
        t("Invert(sin(x))", expected);
        t("Invert(PartialFractions((x + 1) / (x + 2)))", "(-2 * x + 1) / (x - 1)");
        t("Invert(CompleteSquare(x^2 + 2 x + 1))", "sqrt(x) - 1");
    }

    @Test
    public void cmdNSolutions() {
        t("NSolutions(x^6 - 2x + 1 = 0)", "{0.508660391642, 1}");
        t("NSolutions(a^4 + 34a^3 = 34, a)", "{-34.00086498588, 0.9904738885574}");
        t("NSolutions(cos(x) = x, x = 0)", "{0.7390851332152}");
        t("NSolutions(a^4 + 34a^3 = 34, a = 3)", "{0.9904738885574}");
        t("NSolutions({pi / x = cos(x - 2y), 2 y - pi = sin(x)}, {x = 3, y = 1.5})",
                "{1.570796326795, 3.14159265359}");
    }

    @Test
    public void cmdSequence() {
        t("Sequence((2, k), k, 1, 5)", "{(2, 1), (2, 2), (2, 3), (2, 4), (2, 5)}");
        t("Sequence(x^k, k, 1, 10)",
                "{x, x^(2), x^(3), x^(4), x^(5), x^(6), x^(7), x^(8), x^(9), x^(10)}");
        t("Sequence((2, k), k, 1, 3, 0.5)", "{(2, 1), (2, 3 / 2), (2, 2), (2, 5 / 2), (2, 3)}");
        t("Sequence(x^k, k, 1, 10, 2)", "{x, x^(3), x^(5), x^(7), x^(9)}");
        t("Sequence(4)", "{1, 2, 3, 4}");
        t("2^Sequence(4)", "{2, 4, 8, 16}");
        t("Sequence(7,13)", "{7, 8, 9, 10, 11, 12, 13}");
        t("Sequence(18,14)", "{18, 17, 16, 15, 14}");
        t("Sequence(-5, 5)", "{-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5}");
    }

    @Test
    public void cmdReducedRowEchelonForm() {
        t("ReducedRowEchelonForm({{1, 6, 4}, {2, 8, 9}, {4, 5, 6}})",
                "{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}");
        t("ReducedRowEchelonForm({{2, 10, 11, 4}, {2, -5, -6, 12}, {2, 5, 3, 2}})",
                "{{1, 0, 0, 5}, {0, 1, 0, -14 / 5}, {0, 0, 1, 2}}");
        t("ReducedRowEchelonForm({{1, 6, 4}, {2, 8, 9}, {4, 5, 6}})",
                "{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}");
        t("ReducedRowEchelonForm({{2, 10, 11, 4}, {2, -5, -6, 12}, {2, 5, 3, 2}})",
                "{{1, 0, 0, 5}, {0, 1, 0, -14 / 5}, {0, 0, 1, 2}}");
    }

    @Test
    public void cmdSubstitute() {
        t("Substitute((3 m - 3)^2 - (m + 3)^2, m, a)",
                "-(a + 3)^(2) + (3 * a - 3)^(2)");
        t("Substitute(2x + 3y - z, {x = a, y = 2, z = d})",
                "2 * a - d + 6");
        t("Substitute(Derivative(\u212f^(-x)/(x^2+1),x,2),{x=-1})", "0");
    }

    @Test
    public void cmdSUM() {
        t("Sum(n^2, n, 1, 3)",
                "14");
        t("Sum(r^k, k, 0, n)",
                "r^(n + 1) / (r - 1) - 1 / (r - 1)");
        t("Sum((1/3)^n, n, 0, Infinity)",
                "3 / 2");
    }

    @Test
    public void cmdTranspose() {
        t("Transpose({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})",
                "{{1, 4, 7}, {2, 5, 8}, {3, 6, 9}}");
        t("Transpose({{a, b}, {c, d}})",
                "{{a, c}, {b, d}}");
    }

    @Test
    public void cmdRemoveUndefined() {
        t("RemoveUndefined(Sequence((-1)^j, j, -3, -1, 0.5))",
                "{-1, -ί, 1, ί, -1}");
    }

    @Test
    public void cmdTangent() {
        t("Tangent((5, 4), 4x^2 - 5y^2 = 20)",
                "{y = x - 1}");
        t("Tangent((1, 0), x^2)",
                "y = 2 * x - 1");
        t("Tangent(1, x^2)",
                "y = 2 * x - 1");
        t("Tangent(x^2 + y^2 = 4, (x - 6)^2 + y^2 = 4)",
                "(y = -x^(4) + 2 * x^(3) - 2 * x^(2) * y^(2) + 2 * x * y^(2) "
                        + "- 12 * x - y^(4) + y^(2) + 36, y = 4)");
        t("Tangent((1,1), x^2+y^2=1)",
                "{x = 1, y = 1}");
    }

    @Test
    public void cmdFit() {
        t("Fit[ {(0,1),(1,2),(2,5)}, {x^2,x,1} ]",
                unicode("x^(2) + 1"));

        t("Fit[ {(0,1,1),(1,1,2),(2,1,5),(0,2,4),(1,2,5),(2,2,8)}, {x^2,x,1,x^2*y,x*y,y} ]",
                unicode("-2 + x^(2) + 3 * y"));

    }

    @Test
    public void cmdDistance() {
        t("Distance[(0,0),(1,1)]", "sqrt(2)");
        t("Distance[(0,0,0),(1,1,1)]", "sqrt(3)");
        t("Distance[y=x, (0,1)]", "sqrt(2) / 2");
        t("Distance[(0,0),x^2+1]", "1");
        t("Distance[(0,0,0), x+y+z=2]", "2 * sqrt(3) / 3");
        t("Distance[(0,0,0), 2x+y=2]", "?");
        t("Distance[(0,0),1]", "?");
    }

    @Test
    public void cmdPoint() {
        t("Point[(3, 2), Vector[(11, 15)]]", "(14, 17)");
        t("Point[(3, 2, 4), Vector[(11, 15, -2)]]", "(14, 17, 2)");
        t("Point[(3, 2), (11, 15)]", "?");
        t("Point[(3, 2, 4), (11, 15, -2)]", "?");
        t("Point[(3, 2), (11, 15, -2)]", "?");
        t("Point[(3, 2, 4), (11, 15)]", "?");
    }
}
