#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
#include "first.h"
#ifdef HAVE_LIBCOCOA
#ifndef CoCoA_FGLM_H
#define CoCoA_FGLM_H

//   Copyright (c)  2006  Stefan Kaspar

//   This file is part of the source of CoCoALib, the CoCoA Library.

//   CoCoALib is free software; you can redistribute it and/or modify
//   it under the terms of the GNU General Public License (version 3)
//   as published by the Free Software Foundation.  A copy of the full
//   licence may be found in the file COPYING in this directory.

//   CoCoALib is distributed in the hope that it will be useful,
//   but WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//   GNU General Public License for more details.

//   You should have received a copy of the GNU General Public License
//   along with CoCoA; if not, write to the Free Software
//   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

#include "CoCoA/PPMonoid.H"
#include "CoCoA/ring.H"

// #include <vector> // Included by PPMonoid.H
// using std::vector;

namespace CoCoADortmund
{
  /*-----------------------------------------------------------------*/
  /** \include fglm.txt  */
  /*-----------------------------------------------------------------*/

  /*! \brief Convert a Groebner basis into a Groebner basis w.r.t. NewOrdering.
   *
   *  Convert a Groebner basis G into a Groebner basis w.r.t. NewOrdering.<br>
   *  NOTE: Id(G) must be zero dimensional!
   *
   *  \param NewGB Will hold the new Groebner basis w.r.t. NewOrdering. Note:
   *               The elements of NewGB belong to a different polynomial ring
   *               than the elements of OldGB!
   *  \param OldGB Old Groebner basis.
   *  \param NewOrdering A PPOrdering representing the target term ordering.
   */
  void FGLMBasisConversion(std::vector<CoCoA::RingElem>& NewGB, const std::vector<CoCoA::RingElem>& OldGB, const CoCoA::PPOrdering& NewOrdering);


} // end of namespace CoCoADortmund

#endif
#endif // HAVE_LIBCOCOA
