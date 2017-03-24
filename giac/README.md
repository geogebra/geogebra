# Giac, a free computer algebra system #

This document describes the Node port of Giac. It offers direct use of
Giac in JavaScript programs including Electron applications.

## Example usage ##

```javascript
var giac = require('bindings')('giac');
console.log(giac.evaluate("expand((x+y)^3);"));
```

## Supported platforms ##

Currently only Linux is supported. Other system will be supported soon.

## Prerequisites ##

Install the GMP development library. On a Debian based system it can be
performed by running `apt install libgmp-dev`.

## Compilation ##

Enter `npm install` in the current folder. The compilation will take
several minutes.

## Testing ##

Enter `node ./` in the current folder.

## Authors ##

Giac was mostly written and is continuously developed by Bernard Parisse
<bernard.parisse@univ-grenoble-alpes.fr>. The Node port was implemented
by Zoltán Kovács <zoltan@geogebra.org>.
