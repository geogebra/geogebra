/* -*- mode:C++ ; compile-command: "g++-3.4 -I.. -I../include -g -c -Wall TmpLESystemSolver.C" -*- */
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

#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
#include "first.h"
#ifdef USE_GMP_REPLACEMENTS
#undef HAVE_LIBCOCOA
#endif
#ifdef HAVE_LIBCOCOA
#include "TmpLESystemSolver.H"
#include "CoCoA/DenseMatrix.H"
#include "CoCoA/matrix.H"
#include "CoCoA/ring.H"
#include "CoCoA/error.H"

// #include <vector> // Included by DenseMatrix.H
using std::vector;
#include <utility>
using std::pair;
using std::make_pair;
// #include <cstddef> // Included by DenseMatrix.H
using std::size_t;

namespace CoCoADortmund
{
  using namespace CoCoA;

  // Create a copy of a matrix
  void CopyMatrix(matrix& MTarget, const matrix& MSource)
  {
    const size_t NumRowsMSource = NumRows(MSource);
    const size_t NumColsMSource = NumCols(MSource);

    for (size_t row = 0; row < NumRowsMSource; ++row)
      for (size_t col = 0; col < NumColsMSource; ++col)
        SetEntry(MTarget, row, col, MSource(row, col));
  }

  // Solve the linear system M*x = b by using Gauss' algorithm
  bool LESystemSolver(matrix& x0, const matrix& M, const matrix& b)
  {
    const size_t NumRowsM = NumRows(M);
    const size_t NumColsM = NumCols(M);
    const size_t NumRowsb = NumRows(b);
    const size_t NumColsb = NumCols(b);
	
    // Dimension check
    if (NumRowsM != NumRowsb)
      CoCoA_ERROR(ERR::BadMatrixSize, "mySolve: M and b must have same number of rows.");
    if (NumColsM != NumRows(x0))
      CoCoA_ERROR(ERR::BadMatrixSize, "mySolve: M and x0 must have same number of columns.");
    if (NumCols(x0) != 1)
      CoCoA_ERROR(ERR::BadMatrixSize, "mySolve: NumCols(x0) > 1.");
    if (NumColsb != 1)
      CoCoA_ERROR(ERR::BadMatrixSize, "mySolve: NumCols(b) > 1.");
	
    // Field check; should we also check if BaseRing(M) = BaseRing(b) = BaseRing(x0)?
    ring K(BaseRing(M));
    if (!IsField(K))
      CoCoA_ERROR(ERR::NotField, "mySolve: Gauss' algorithm over non-fields not yet implemented.");
		
    // Create working copies of M and b
    matrix MCopy(NewDenseMat(K, NumRowsM, NumColsM));
    CopyMatrix(MCopy, M);
    matrix bCopy(NewDenseMat(K, NumRowsb, NumColsb));
    CopyMatrix(bCopy, b);

    // For solution computation
    vector< pair<size_t, size_t> > positions;

    // Apply Gauss' algorithm	
    RingElem c(K);
    size_t row = 0;
    for (size_t col = 0; col < NumColsM && row < NumRowsM; ++col)
    {
      // Check if current column contains an element != 0
      if (IsZero(MCopy(row, col)))
      {
        size_t i = row+1;
        for ( ; i < NumRowsM; ++i)
        {
          if (!IsZero(MCopy(i, col)))
          {
            // Switch MCopy and bCopy rows
            MCopy->mySwapRows(i, row);
            bCopy->mySwapRows(i, row);
            break;
          }
        }
        if (i ==  NumRowsM)
          continue;
      }
	
      // For solution computation
      positions.push_back(make_pair(row, col));

      // Found an element != 0 in current column; apply elemination
      c = MCopy(row, col);

      for (size_t i = row+1; i < NumRowsM; ++i)
      {
        // Transform MCopy and bCopy
        bCopy->myAddRowMul(i, row, -MCopy(i, col)/c);
        MCopy->myAddRowMul(i, row, -MCopy(i, col)/c);
      }

      ++row;
    }

    // row = rank(MCopy); check if a solution for the equation system exists
    for (size_t i = row; i < NumRowsb; ++i)
    {
      if (!IsZero(bCopy(i, 0)))
        return false;
    }
	
    // Compute components (x_1, ..., x_n) of vector x0 backwards from x_n to x_1,
    // possibly skipping some components
    matrix x0Tmp = NewDenseMat(K, NumRows(x0), 1);
    while (!positions.empty())
    {
      const size_t i = positions.back().first, j = positions.back().second;

      RingElem x(bCopy(i, 0));
      for (size_t k = j + 1; k < NumColsM; ++k)
      {
        x -= MCopy(i, k) * x0Tmp(k, 0);
      }
      SetEntry(x0Tmp, j, 0, x/MCopy(i, j));

      positions.pop_back();
    }
    CopyMatrix(x0, x0Tmp);
	
    return true;
  }

} // end of namespace CoCoA
#endif
