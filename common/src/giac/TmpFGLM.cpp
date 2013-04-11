/* -*- mode:C++ ; compile-command: "g++-3.4 -I.. -I../include -g -c -Wall TmpFGLM.C" -*- */
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
#include "TmpFGLM.H"
#include "TmpLESystemSolver.H"
#include "CoCoA/DenseMatrix.H"
#include "CoCoA/symbol.H"
#include "CoCoA/QBGenerator.H"
#include "CoCoA/RingDistrMPolyInlPP.H"
#include "CoCoA/RingHom.H"
#include "CoCoA/SparsePolyRing.H"

// #include <cstddef>
using std::size_t;
#include <set>
using std::set;
// #include <list> // Included by QBGenerator.H
using std::list;
#include <map>
using std::map;
// #include <memory> // Included by SparsePolyRing.H
using std::auto_ptr;
#include <utility>
using std::make_pair;
// #include <vector>
using std::vector;

namespace CoCoADortmund
{
  using namespace CoCoA;

  // Used for constructing matrices to solve linear equation systems
  // (Kind of sparse matrix representation)
  struct MatrixMapEntry
  {
    MatrixMapEntry(ConstRefRingElem r): rhs(r) {}
    std::vector<std::size_t> VarIndices; // Column numbers in linear equation matrix
    std::vector<RingElem> coeffs; // Matrix components in linear equation matrix
    RingElem rhs; // Right hand side component in linear equation
  };


  // Embed element p of PPM into another PPM with different term ordering
  PPMonoidElem PPIntoOtherPPM(ConstRefPPMonoidElem p, const PPMonoid& OtherPPM)
  {
    vector<long> expv;
    exponents(expv, p);
    return PPMonoidElem(OtherPPM, expv);
  }

  // Update matrix map during main loop
  // !! Weakly exception safe, writable parameters might get rendered useless !!
  void UpdateMatrixMap(map<PPMonoidElem, struct MatrixMapEntry>& MatrixMap, size_t& NumCols, QBGenerator& NewQB, ConstRefRingElem p, ConstRefPPMonoidElem t)
  {
    for (SparsePolyIter m = BeginIter(p); !IsEnded(m); ++m)
    {
      map<PPMonoidElem, struct MatrixMapEntry>::iterator entry = MatrixMap.find(PP(m));
      // ToDo: Avoid this tedious if-else block
      if (entry == MatrixMap.end())
      {
        ring K = CoeffRing(AsSparsePolyRing(owner(p)));
        struct MatrixMapEntry NewEntry(zero(K));
        NewEntry.VarIndices.push_back(NumCols);
        NewEntry.coeffs.push_back(coeff(m));
        MatrixMap.insert(make_pair(PP(m), NewEntry));
      }
      else
      {
        entry->second.VarIndices.push_back(NumCols);
        entry->second.coeffs.push_back(coeff(m));
      }
    }
    ++NumCols;

    // Update quotient basis NewQB
    NewQB.myCornerPPIntoQB(t);
  }

  // FGLM implementation
  // exception safe
  void FGLMBasisConversion(vector<RingElem>& NewGB, const vector<RingElem>& OldGB, const PPOrdering& NewOrdering)
  {
    if (OldGB.empty())
      CoCoA_ERROR(ERR::nonstandard, "FGLMBasisConversion: empty Groebner Basis vector");

    // Check if generated ideal is zero-dimensional
    const ideal I(AsSparsePolyRing(owner(OldGB.front())), OldGB);
    if (!IsZeroDim(I))
      CoCoA_ERROR(ERR::nonstandard, "FGLMBasisConversion: ideal must be 0-dimensional");

    // Initialization of objects needed for computation
    const SparsePolyRing Kx = AsSparsePolyRing(owner(OldGB.front()));
    const ring K = CoeffRing(Kx);
    const PPMonoid PPMon = PPM(Kx);
    const RingElem FieldOne(one(K));

    // Adjust K[x_1, ..., x_n] and PPM(K[x_1, ..., x_n]) to correct term ordering
    const PPMonoid PPMAdjusted = NewPPMonoid(symbols(Kx), NewOrdering);
    const SparsePolyRing KxAdjusted = NewPolyRing(CoeffRing(Kx), PPMAdjusted);

//    // Identity mapping Kx -> KxAdjusted
//    // (Not yet used)
//    RingHom KxToKxAdjusted = PolyRingHom(Kx, KxAdjusted, CoeffEmbeddingHom(KxAdjusted), indets(KxAdjusted));

    // These will hold the (temporary) basis elements
    vector<RingElem> NewGBTmp; // Holds elements in K[x_1, ..., x_n] w.r.t. new term ordering
    QBGenerator NewQB(PPMAdjusted); // Holds the new QB in PPM with new term ordering
    map<PPMonoidElem, struct MatrixMapEntry> MatrixMap; // Used to create the matrices for the linear dependency check
    size_t NumCols = 1;

    // FGLM algorithm start
    PPMonoidElem t(PPMAdjusted);
    t = one(PPMAdjusted);
    NewQB.myCornerPPIntoQB(t);
    struct MatrixMapEntry entry(zero(K));
    entry.VarIndices.push_back(0);
    entry.coeffs.push_back(FieldOne);
    MatrixMap.insert(make_pair(PPIntoOtherPPM(t, PPMon), entry)); // Want to keep the keys (PPs) in PPM with old term ordering

    while (!NewQB.myCorners().empty())
    {	
      t = NewQB.myCorners().front(); // t is element of PPMAdjusted
      RingElem h(Kx);
      h = NR(monomial(Kx, FieldOne, PPIntoOtherPPM(t, PPMon)), OldGB);
      vector< map<PPMonoidElem, struct MatrixMapEntry>::iterator > ResetPos;
      // Prepare creation of linear equation system and check if a solution can
      // exist (simple check if equations like "0 = c" would occur with c not
      // equal to 0)
      bool RemainderMightBeIndependent = true;
      for (SparsePolyIter m = BeginIter(h); !IsEnded(m); ++m)
      {
        map<PPMonoidElem, struct MatrixMapEntry>::iterator entry = MatrixMap.find(PP(m));
        // If h contains a term that is not already in the matrix map
        // then h is linearly independent of the previously computed remainders
        if (entry == MatrixMap.end())
        {
          RemainderMightBeIndependent = false; // Only reset-operations after matrix creation code below need to be carried out
          UpdateMatrixMap(MatrixMap, NumCols, NewQB, h, t);
          break;
        }
        else
        {
          // Right hand side component equals coeff(m)
          entry->second.rhs = -coeff(m);
          ResetPos.push_back(entry);
        }
      }

      // If it is not yet clear if the current remainder is linearly dependent on
      // the previously computed remainders we have to create a linear equation
      // system and try to solve it
      if (RemainderMightBeIndependent)
      {
        // Create a system of linear equations from matrix map
        matrix M = NewDenseMat(K, MatrixMap.size(), NumCols),
               b = NewDenseMat(K, MatrixMap.size(), 1);
        size_t j = 0; // Current row

        for (map<PPMonoidElem, struct MatrixMapEntry>::iterator entry = MatrixMap.begin(); entry != MatrixMap.end(); ++entry, ++j)
        {
          vector<size_t>& VarIndices = entry->second.VarIndices;
          vector<RingElem>& coeffs = entry->second.coeffs;

          // Set matrix components
          for (size_t k = 0; k < VarIndices.size(); ++k)
            SetEntry(M, j, VarIndices[k], coeffs[k]);

          // Set right hand side component
          SetEntry(b, j, 0, entry->second.rhs);
        }

        // Check if M*x = b has a solution
        matrix x = NewDenseMat(K, NumCols, 1);
        if (LESystemSolver(x, M, b))
        {
          // Compute new Groebner Basis polynomial
          RingElem g(KxAdjusted); // g in K[x_1, ..., x_n] with correct term ordering
          g = monomial(KxAdjusted, FieldOne, t);
          const vector<PPMonoidElem>& QB = NewQB.myQB();
          for (size_t i = 0; i < NumCols; ++i)
            g += monomial(KxAdjusted, x(i, 0), QB[i]);
          NewGBTmp.push_back(g);

          // Update quotient basis NewQB
          NewQB.myCornerPPIntoAvoidSet(t);
        }
        else
          UpdateMatrixMap(MatrixMap, NumCols, NewQB, h, t);
      }

      // Reset right hand side components in matrix map
      for (size_t i = 0; i < ResetPos.size(); ++i)
        ResetPos[i]->second.rhs = zero(K);
    }

    // Swap computed Groebner Basis
    swap(NewGB, NewGBTmp);
  }

} // End of namespace CoCoA

#endif
