var giac = require('bindings')('giac');

console.log(giac.evaluate("expand((x+y)^3)"));
console.log(giac.evaluate("expand((x+y)^4)"));
console.log(giac.evaluate("2^50"));
console.log(giac.evaluate("[1]"));
console.log(giac.evaluate("caseval(\"init geogebra\")"));
console.log(giac.evaluate("[1]"));
console.log(giac.evaluate("evalf(7,15)"));
console.log(giac.evaluate("caseval(\"close geogebra\")"));
console.log(giac.evaluate("normal(sqrt(1+i))"));

/* The expected output is:
 *
 * x^3+y^3+3*x*y^2+3*x^2*y
 * x^4+y^4+4*x*y^3+6*x^2*y^2+4*x^3*y
 * 1125899906842624
 * [1]
 * "geogebra mode on"
 * {1}
 * 7.00000000000000
 * "geogebra mode off"
 * (sqrt(2)*sqrt(sqrt(2)+1)+(1+i)*sqrt(sqrt(2)+1))/(sqrt(2)+2)
 *
 */
