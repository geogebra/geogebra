var addon = require('bindings')('giac');

console.log(addon.giac("expand((x+y)^3);"));
console.log(addon.giac("expand((x+y)^4);"));
console.log(addon.giac("2^50;"));
console.log(addon.giac("[1]"));
console.log(addon.giac("caseval(\"init geogebra\");"));
console.log(addon.giac("[1]"));
