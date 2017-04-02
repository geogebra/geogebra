# Giac, a free computer algebra system #

This document describes the Node port of
[Giac](http://www-fourier.ujf-grenoble.fr/~parisse/giac.html). It offers
direct use of Giac in JavaScript programs including
[Electron](https://electron.atom.io/) applications.

## Example usage ##

```javascript
var giac = require('bindings')('giac');
console.log(giac.evaluate("expand((x+y)^3)"));
```

## Supported platforms ##

Currently only Linux and Mac OS X have been tested and work properly. The
Windows platform is planned to be supported soon (it cannot be built at
the moment).

## Prerequisites ##

Since Giac is C++ library, you need to have a compiler toolchain on your
system installed. In addition the [GMP](https://gmplib.org) and
[MPFR](http://www.mpfr.org/) development libraries need to be present.

The compilation will be performed by
[node-gyp](https://github.com/nodejs/node-gyp). In order to install it on
your system you may have to accomplish some additional steps. In a doubt,
please consult the **node-gyp** documentation.

### Linux ###

You need a recent **gcc**. Former versions (like 4.8) may fail.

Python 2.7 is required. On some systems you may have to create a symbolic link to
the python2.7 executable (preferably as **/usr/local/bin/python**).

On a Debian based system GMP and MPFR can be installed by running `sudo apt install
libgmp-dev libmpfr-dev`.

Ubuntu 16.04 and 16.10 should work. Currently the *amd64*, *i386* and
*armhf* processors have been tested with success.

### Mac OS X ###

Install [MacPorts](https://www.macports.org/install.php) first. Then `sudo port
install gmp mpfr` will install GMP and MPFR as well.

Mac OS X 10.11.6 with Xcode 8.2.1, GMP 6.1.0 and MPFR 3.1.3 is known to
work correctly.

## Compilation ##

Enter `npm install` in the current folder. Alternatively, `npm install
giac` will do the compilation by using the last stable package from the
npmjs repository. Use `npm install giac@latest` to get the latest
unstable version.

The compilation may take several minutes. To speed up the compilation
process consider entering for example
```
JOBS=4 npm install giac@latest
```
which will use 4 cores in parallel. The best value for the number of
parallel jobs is usually the number of cores you have, but
[this may depend on your system as
well](http://stackoverflow.com/questions/2499070/gnu-make-should-the-number-of-jobs-equal-the-number-of-cpu-cores-in-a-system).

## Testing ##

Enter `npm test` in the current folder. To play with Giac, modify the
file **nodegiac.js** and run `npm test` again.

## Roadmap ##
* Windows implementation
* Electron example

## Authors ##

Giac was mostly written and is continuously developed by Bernard Parisse
<bernard.parisse@univ-grenoble-alpes.fr>. The Node port was implemented
by Zoltán Kovács <zoltan@geogebra.org>.

## Acknowledgments ##

The Node port of Giac is dedicated to Gábor Ancsin, one of the most
prominent JavaScript heroes of the [GeoGebra](http://www.geogebra.org)
[Team](http://www.geogebra.org/team).
