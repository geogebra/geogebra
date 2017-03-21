var addon = require('bindings')('giac');

console.log(addon.giac("expand((x+y)^3);"));
