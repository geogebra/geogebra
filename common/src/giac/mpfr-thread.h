/* MPFR internal header related to thread-local variables.

Copyright 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Free Software Foundation, Inc.
Contributed by the AriC and Caramel projects, INRIA.

This file is part of the GNU MPFR Library.

The GNU MPFR Library is free software; you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation; either version 3 of the License, or (at your
option) any later version.

The GNU MPFR Library is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the GNU MPFR Library; see the file COPYING.LESSER.  If not, see
http://www.gnu.org/licenses/ or write to the Free Software Foundation, Inc.,
51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA. */

#ifndef __MPFR_THREAD_H__
#define __MPFR_THREAD_H__

/* Note: Let's define MPFR_THREAD_ATTR even after a #error to make the
   error message more visible (e.g. gcc doesn't immediately stop after
   the #error line and outputs many error messages if MPFR_THREAD_ATTR
   is not defined). But some compilers will just output a message and
   may build MPFR "successfully" (without thread support). */
#ifndef MPFR_THREAD_ATTR
# ifdef MPFR_USE_THREAD_SAFE
#  if defined(_MSC_VER)
#   if defined(_WINDLL)
#    error "Can't build MPFR DLL as thread safe."
#    define MPFR_THREAD_ATTR
#   else
#    define MPFR_THREAD_ATTR __declspec( thread )
#   endif
#  else
#   define MPFR_THREAD_ATTR __thread
#  endif
# else
#  define MPFR_THREAD_ATTR
# endif
#endif

#endif
