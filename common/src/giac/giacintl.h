#ifndef _GIACINTL_H
#define _GIACINTL_H

#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#include "first.h"

#if (defined(__APPLE__) && !defined(INT128)) || defined(__FreeBSD__)
#include <libintl.h>
#endif

#ifdef HAVE_GETTEXT
#include <libintl.h>
#else

#ifndef _LIBINTL_H
#define _LIBINTL_H      1
#define __LIBINTL_H_DEFINED__ // Pour NetBSD 
#ifdef GIAC_HAS_STO_38
const char * gettext(const char * s); // in aspen.cc
#else
inline const char * gettext(const char * s) { return s; };
#endif
#endif // _LIBINTL_H

#endif // HAVE_GETTEXT
#endif // _GIACINTL_H
