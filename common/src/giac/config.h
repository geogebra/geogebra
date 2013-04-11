/* config.h.  Generated from config.h.in by configure.  */
/* config.h.in.  Generated from configure.in by autoheader.  */

/* Set if debugging is enabled */
#define DEBUG_SUPPORT 

/* Name of package */
#define PACKAGE "giac"

/* Define to the address where bug reports for this package should be sent. */
#define PACKAGE_BUGREPORT ""

/* Define to the full name of this package. */
#define PACKAGE_NAME "giac"

/* Define to the full name and version of this package. */
#define PACKAGE_STRING "giac 1.1"

/* Define to the one symbol short name of this package. */
#define PACKAGE_TARNAME "giac"

/* Define to the version of this package. */
#define PACKAGE_VERSION "1.1"

/* The size of `int', as computed by sizeof. */
#define SIZEOF_INT 4

/* The size of `long', as computed by sizeof. */
#ifdef __x86_64__
#define SIZEOF_LONG 8
#define GIAC_GENERIC_CONSTANTS
#define SMARTPTR64
#else
#define SIZEOF_LONG 4
#endif

/* The size of `long long', as computed by sizeof. */
#define SIZEOF_LONG_LONG 8



/* Version number of package */
#define VERSION "1.1"


#define EMCC
#define GIAC_NO_OPTIMIZATIONS
#define HAVE_NO_HOME_DIRECTORY

#define HAVE_SYSCONF
#define HAVE_LIBMPFR
#define HAVE_MPFR_H 1