var giac = require('bindings')('giac');

console.log(giac.evaluate("expand((x+y)^3)"));
console.log(giac.evaluate("expand((x+y)^4)"));
console.log(giac.evaluate("2^50"));
console.log(giac.evaluate("[1]"));
console.log(giac.evaluate("caseval(\"init geogebra\")"));
console.log(giac.evaluate("[1]"));
console.log(giac.evaluate("evalf(7,15)"));

/* The expected output is:
 *
 * x^3+y^3+3*x*y^2+3*x^2*y
 * x^4+y^4+4*x*y^3+6*x^2*y^2+4*x^3*y
 * 1125899906842624
 * [1]
 * "geogebra mode on"
 * {1}
 * 7.00000000000000
 *
 */
