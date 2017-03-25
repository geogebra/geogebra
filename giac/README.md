# Giac, a free computer algebra system #

This document describes the Node port of
[Giac](http://www-fourier.ujf-grenoble.fr/~parisse/giac.html). It offers
direct use of Giac in JavaScript programs including
[Electron](https://electron.atom.io/) applications.

## Example usage ##

```javascript
var giac = require('bindings')('giac');
console.log(giac.evaluate("expand((x+y)^3);"));
```

## Supported platforms ##

Currently only Linux and Mac OS X have been tested. The Windows platform is
theoretically supported but yet untested.

## Prerequisites ##

Install the [GMP](https://gmplib.org) development library.

### Linux ###

On a Debian based system it can be performed by running `sudo apt install
libgmp-dev`.

### Mac OS X ###

Install [MacPorts](https://www.macports.org/install.php). Then `sudo port
install gmp` will install GMP as well.

## Compilation ##

Enter `npm install` in the current folder. The compilation will take
several minutes.

## Testing ##

Enter `npm test` in the current folder.

## Authors ##

Giac was mostly written and is continuously developed by Bernard Parisse
<bernard.parisse@univ-grenoble-alpes.fr>. The Node port was implemented
by Zoltán Kovács <zoltan@geogebra.org>.

## Acknowledgments ##

The Node port of Giac is dedicated to Gábor Ancsin, one of the most
prominent JavaScript heroes of the [GeoGebra](http://www.geogebra.org) [Team](http://www.geogebra.org/team).
