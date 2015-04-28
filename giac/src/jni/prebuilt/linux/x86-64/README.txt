These binaries are own built versions of gmp-6.0.0a and mpfr-3.1.2. They
have been compiled under Ubuntu 14.10 (64 bit) by using the --with-pic
option: first gmp have to be compiled and then mpfr (by using it).

The --with-pic option is mandatory. Without it, an error message like
"relocation R_X86_64_32 against `.rodata.str1.1' can not be used when
making a shared object; recompile with -fPIC" will be shown (also when
using the shipped version of gmp or mpfr from Ubuntu).

TODO: add automatic compilation of these .a files into the Gradle build.
