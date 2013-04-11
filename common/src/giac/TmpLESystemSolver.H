#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
#include "first.h"
#ifdef HAVE_LIBCOCOA
#ifndef CoCoA_LESystemSolver_H
#define CoCoA_LESystemSolver_H

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

#include <CoCoA/matrix.H>

namespace CoCoADortmund
{

  /*! \brief Solve the linear system M*x0 = b using Gauss' algorithm.
   *
   *  Computes a solution of M*x0 = b if there exists any using Gauss' algorithm.
   *  The solution will be stored in x0.
   *
   *  \param x0 Will hold the solution of A*x0 = b.
   *  \param M A matrix.
   *  \param b A vector.
   *  \return true, if a solution exists, false else.
   */
  bool LESystemSolver(CoCoA::matrix& x0, const CoCoA::matrix& M, const CoCoA::matrix& b);

} // end of namespace CoCoADortmund

#endif
#endif
