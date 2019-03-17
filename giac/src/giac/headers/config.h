// This file has been taylored after autogenerating it by the autoconf machinery.
// Which is, actually, no longer used.

/* Additional settings or overrides should be put
 * into build.gradle. This file should be considered as
 * as a "common base" for all platforms.
 */

/* Set if debugging is enabled */
#define DEBUG_SUPPORT

/* Name of package */
#define PACKAGE "giac"

/* Define to the address where bug reports for this package should be sent. */
#define PACKAGE_BUGREPORT ""

/* Define to the full name of this package. */
#define PACKAGE_NAME "giac"

/* Define to the full name and version of this package. */
#ifndef PACKAGE_STRING
#define PACKAGE_STRING "giac 1.2.3"
#endif

/* Define to the one symbol short name of this package. */
#define PACKAGE_TARNAME "giac"

/* Define to the version of this package. */
#ifndef PACKAGE_VERSION
#define PACKAGE_VERSION "1.2.3"
#endif

/* Version number of package */
#ifndef VERSION
#define VERSION "1.2.3"
#endif

#define GIAC_NO_OPTIMIZATIONS
#define HAVE_NO_HOME_DIRECTORY

#define HAVE_SYSCONF

#define HAVE_LIBMPFR
#define HAVE_MPFR_H 1

/* The size of `int' and `long long', as computed by sizeof. */
#define SIZEOF_INT 4
#define SIZEOF_LONG_LONG 8
