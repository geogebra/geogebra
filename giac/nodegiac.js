var giac = require('bindings')('giac');

console.log(giac.evaluate("expand((x+y)^3);"));
console.log(giac.evaluate("expand((x+y)^4);"));
console.log(giac.evaluate("2^50;"));
console.log(giac.evaluate("[1]"));
console.log(giac.evaluate("caseval(\"init geogebra\");"));
console.log(giac.evaluate("[1]"));
