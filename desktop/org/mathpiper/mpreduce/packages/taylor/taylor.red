module Taylor;

%****************************************************************
%
%                      THE TAYLOR PACKAGE
%                      ==================
%
%****************************************************************
%
%  Copyright (C) 1989--2010 by Rainer M. Schoepf, all rights reserved.
%
%
%  Error reports please to: <reduce-algebra-developers@lists.sourceforge.net>
%
%
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%
%
%
%
%  This package implements a new data structure:
%
%            >>> the TAYLOR KERNEL <<<
%
%  and the functions necessary to operate on it.
%
%  A TAYLOR KERNEL is a kernel of the following form:
%
%   (Taylor!* TayCoeffList TayTemplate TayOrig TayFlags)
%
%   where:
%    Taylor!*        is a symbol identifying the kernel.
%    TayCoeffList    is a list of TayCoeff's.
%                    A TayCoeff is a pair
%                     (TayPowerList . StandardQuotient).
%                    A TayPowerList is a list of TayDegreeList's,
%                    each of which is a list of integers that
%                    denote the exponents of the Taylor variables.
%    TayTemplate     is a list of lists, each containing the three
%                    elements  TayVars, TayPoint, TayOrder,
%                    TayNextOrder, (being the list of variables, the
%                    expansion point the power of the variable in the
%                    last coefficient computed, and the power of the
%                    next coefficient, respectively)
%                    It is used as a template for the car of the
%                    pairs in a TayCoeffList.
%    TayOrig         is the original expression to be Taylor expanded,
%                    as a standard quotient. It is held mainly for
%                    the use by possible future extensions.
%                    This part is nil if the switch taylorkeeporiginal
%                    was off during expansion.
%    TayFlags        is currently unused and reserved for future
%                    extensions.
%
%
%
%*****************************************************************
%
%       Revision history
%
%*****************************************************************
%
% 21-Aug-2010    2.2c
%   Create taylor's own fkern function, instead of manipulating
%   the klist!* directly
%
% 08-Jun-2001    2.2b
%   Bind !*precise to nil in simptaylor, to avoid problems with SQRT
%    and ABS.
%
% 01-Apr-2000    2.2a
%   Corrected problem in taysimpasin, discovered by John Fitch.
%
% 18-Jun-1997    2.2
%   Added module TayPart, an interface to the PART operator.
%
%   Minor improvement in procedure taylorexpand!-samevar: do not signal
%    an error if we are content with the terms we got (i.e. third
%    argument flg is nil.
%
% 16-Apr-1997    2.1f
%   Slight improvement in tracing output (smacro Taylor!-trace).
%   Avoid infinite recursion when mcd is off (pointed out by
%    Wolfram Koepf).
%
% 03-May-1995    2.1e
%   Corrected differentation of Taylor kernels if expansion point
%    depends on variable. (Integration still gives wrong result.)
%   Printing procedures did not always work for right hand side of
%    rules.
%
% 03-Apr-1995    2.1d
%   Removed special handling for constant Taylor series in taysimptan,
%    taysimpsin and taysimpsinh, as it didn't work when the function in
%    question has a pole and the expansion point.
%
% 09-Jan-1995    2.1c
%   Changed rules in tayfns.red to new ~~ format.
%
% 22-Jul-1994    2.1b
%   Corrected glitch in function common!-increment.
%
% 10-Jun-1994    2.1a
%   Corrected expansion of tan at pole up to order 0: make quottaylor
%    and invtaylor signal restartable error, have taylorexpand truncate
%    its result.
%   Operator expint is now called ei.
%   Changed error message in taysimpasin to signal a branch point, not
%    an essential singularity.
%
% 08-May-1994    2.1
%   Added implicit_taylor and inverse_taylor.
%
%
% 02-May-1994    2.0c
%   Corrected missing Next field in function taylor2e.
%
% 14-Apr-1994    2.0b
%   Corrected function expttayi.
%   Renamed pos!-rat!-kern!-pow to rat!-kern!-pow and changed calling
%    convention, moved to tayutils module.
%   subfg!* changed from global to fluid.
%   Removed a few unused local variables.
%   Added TayExp!-max2.
%   Rewrote expansion code for monomials to produce higher order terms.
%
% 14-Mar-1994    2.0a
%   Corrected generation of simple Taylor kernels when switch mcd is
%    off.
%   Added code to catch looping with increased order, introduced a
%    better error message.
%   Added a few missing parentheses in functions taysimpexpt and
%    tayrevert1.
%
% 03-Mar-1994    2.0
%   Introduced rational exponents for Puiseux series. This required
%    changes to various functions:
%      Taylor exponent manipulation functions
%      expttayrat
%      expttayrat!* (removed)
%      pos!-rat!-kern!-pow
%      smallest!-increment and common!-increment
%      truncate!-Taylor!*
%      tayrevert1
%   Added exceeds!-order!-variant that uses strict greaterp, rather
%    than geq as exceeds!-order does.
%   Added lcmn to compute the least common multiple of two integers.
%   Replaced call to subs2 with binding of !*sub2 by subs2!* in
%    functions below.
%   Removed error type inttaylorwrttayvar, no longer generated.
%   
%
%
% 15-Feb-1994    1.9c
%   Improved taysimpsinh to generate hyperbolic functions in the
%    the coefficients of the result rather than exponentials.
%   Improved handling of zero Taylor kernels if TayOrig is present.
%   Improved taylorexpand!-samevar to handle expansions in more
%    than one variable.
%   Corrected Taylor substitution to handle parallel substitution
%    of several Taylor variables.
%   Updated check for differentiation rules of kernels.
%   Added new error types for Taylor expansion of implicit and
%    inverse functions.
%   Made division by zero in taysubst recoverable error during
%    expansion.
%   Corrected term ordering in Taylorexpand!-diff2.
%   Corrected addition and multiplication of Taylor kernels in
%    taylorexpand!-sf.
%   Added taysimpasec!* to handle singular arguments of asec and
%    friends.
%
% 02-Dec-1993    1.9b
%   Added missing setting of TayOrig part in make!-var!-Taylor!*.
%   Corrected handling of substitution of all Taylor variables in
%    taysubst.
%   Ensure that psi is declared algebraic operator.
%   Corrected substitution of Taylor variable by a power of itself.
%
% 30-Nov-1993    1.9a
%   Corrected cdr of nil in several places, caused by makecoeffs
%    returning an empty list.
%   Improved handling of zero Taylor kernels if TayOrig is present.
%   Added missing setting of TayOrig part in taylorexpand!-diff2.
%
% 29-Nov-1993    1.9
%   Added min2!-order and replace!-next.
%   Improved basic Taylor manipulation functions addtaylor, multtaylor,
%    quottaylor.
%   Added taylorsingularity feature to expansion code.
%
%
% 24-Nov-1993    1.8h
%   Changed calling convention for addtaylor1 to include the template.
%
% 15-Nov-1993    1.8g
%   Rewritten parts of subsubtaylor to better recognize invalid
%    substitutions.
%
% 09-Nov-1993    1.8f
%   Added TayExp!-geq and TayExp!-leq.
%   Changed protocol of truncate!-coefflist and exceeds!-order to use
%    the next part rather than the order part of a template.
%   Cleaned up inv!.tp!..
%
% 08-Nov-1993    1.8e
%   Improved handling of negative first coefficient in taysimplog.
%   Removed binding of !*taylorautocombine in taysimpsq!*.
%
% 03-Nov-1993    1.8d
%   Improved taysimpsq and taysimpf to avoid returning a constant Taylor
%    series as part of an expression.
%   Fixed bug in error handling of expansion code that would produce
%    "This can't happen" error message.
%   Improved error detection for (unknown) operators with logarithmic
%    singularity in derivative.
%   Removed tayinpoly/tayinpoly1 (no longer used).
%   Replaced !*f2q by !*TayExp2q in difftaylorwrttayvar.
%   Changed evaluation and printing procedures so that they can be used
%    in rule patterns.
%   Corrected bug in Taylor kernel integration if the switch
%    taylorkeeporiginal was on.
%   Improved integration of Taylor kernels in combination with
%    logarithmic terms.
%   Rewrote rules integration rules in algebraic form.
%   Added TpNextList smacro.
%   Changed mult!.comp!.tp!. in preparation for non-integer exponents,
%    to handle next parts.
%   Added invert!-powerlist smacro and used in invtaylor1.
%   Renamed taydegree!-lessp to taydegree!< and
%    taydegree!-strict!-less!-or!-equal!-p to taydegree!-strict!<!=.
%   Added smacro prune!-coefflist.
%   Introduced prepTayExp.
%   Changed protocol for add!.comp!.tp!. to return a list of the new
%    template, so that an empty template can be distiguished from
%    failure.
%   Changed procedures for Taylor reversion to use addtaylor and friends
%    instead of addtaylor1, etc.
%   Added an increment argument to all the makecoeff... procedures.
%   Added smallest!-increment and common!-increment procedures.
%   Rewrote taysimpsq to better handle Taylor kernel in denominator.
%
% 23-Sep-1993    1.8c
%   Changed Taylor!-generate!-big!-O to produce a capital O even if
%    !*lower is t.
%
% 03-Sep-1993    1.8b
%   Changed multintocoefflist to bind !*sub2 and first call subs2
%    and then resimp so that power substitutions are carried out
%    properly.
%
% 02-Sep-1993    1.8a
%   Corrected stupid error that caused car/cdr of an atom.
%   Corrected code for expansion by differentiation and for caching
%    of intermediate results to use the template of the computed kernel
%    rather than the template given to the expansion functions.
%   Corrected truncate!-Taylor!* to correctly handle the case that
%    the kernel to be truncated has already fewer terms than expected.
%
% 17-Aug-1993    1.8
%   Improvement in expttayrat if zero is raised to negative power;
%    this might be a case for expansion to higher order.
%   Rewritten code for expansion by differentiation to handle
%    occurrence of a kernel in its own derivative.
%
%
% 12-Aug-1993    1.7e
%   Rewritten part of the expansion by differentiation code to try
%    to differentiate once and expand the result.
%   Slight improvement in trace printing.
%
% 13-Jul-1993    1.7d
%   Corrected taysimpasin and taysimpasin!* to handle branch points
%    correctly.
%   simpTaylor!* can now handle free variables as part of Taylor!*
%    kernel.
%
% 28-Jun-1993    1.7c
%   Corrected error introduced by last change to mult!.comp!.tp!.
%    and inv!.tp!.
%
% 21-Jun-1993    1.7b
%   Changed taylor2f to not collect zero coefficients into coefflist.
%   Changed subs2coefflist to leave out zero coefficients.
%   Set default of Taylorautocombine switch to on.
%   Replaced calls to subs2!* be calls to subs2 with explicit binding
%    of !*sub2.
%
% 17-Jun-1993    1.7a
%   Added get!-min!-degreelist to find minimum degree list of a
%    coefflist; corrected mult!.comp!.tp!. and inv!.comp!.tp!. to
%    use it.
%   Corrected error in quottaylor that could wrongly calculate first   
%    coefficient of result.
%   Shortened lines in tayfns.red to 72 characters.
%   Improved taysimpexp and expttayrat!* to yield an exponential only
%    if there are no analytical terms.
%   Added code to expand inverse trigonometric and hyperbolic functions
%    around their logarithmic singularities.
%   Corrected taysimpsin, improved taysimpsinh.
%
% 15-Jun-1993    1.7
%   Added a lot of utility functions to test for zero, constant,
%    and nonzero constant Taylor series.
%   Rewritten the Taylor simplification functions in modules TaySimp
%    and TayFns to handle non-analytical terms as well.
%   Split expttayrat into two procedures.
%
%
% 14-Jun-1993    1.6
%   Added Taylor!*!-zerop and TayCoeffList!-zerop.
%   Introduced addtaylor!-as!-sq, multtaylor!-as!-sq, and
%    quottaylor!-as!-sq.
%   Changed taymincoeff into an expr procedure, added tayminpowerlist.
%   simptaylor returns now the input if it cannot expand.
%   Added trtaylor switch and some procedures for tracing.
%   Added Taylor!-error!*, currently used only in taysimplog.
%   Added TayExpnd module, removed taylorexpand from tayinterf module.
%   Moved taymincoeff and tayminpowerlist into TayUtils module.
%   Corrected procedures in tayfns.red to not crash for Taylor kernels
%    with empty TayCoefflist.
%
%
% 10-Jun-1993    1.5b
%   Introduced get!-degreelist.
%   Rewritten exceeds!-order to be tail-recursive.
%   Changed protocol for makecoeffs call, introduced makecoeffs0.
%   Introduced !*TayExp2q to convert a power into a standard quotient.
%
% 09-Jun-1993    1.5a
%   Introduced has!-TayVars predicate, replaced calls to smemberlp
%    and smemqlp by it.
%   Split addtaylor, multtaylor and quottaylor into two procedures.
%   Replaced taysimpp by expttayi in difftaylor.
%   Introduced taysimpsq!* (taysimpsq with taylorautoexpand off). 
%
% 08-Jun-1993    1.5
%   Introduction of special operations for exponents in powerlists:
%    TayExp!-<op>. The macro Taylor!: is used to replace normal
%    operations by these.
%    Changed makecoeffpairs and addcoeffs to avoid consing pairs and
%     taking them apart again.
%    Changed makecoeffpairshom to allow steps different from 1.
%
%
% 07-Jun-1993    1.4l
%   Introduced TayNextOrder and made corresponding changes to the
%    sources.
%   Added addto!-all!-TayTpElOrders function.
%   Corrected problem in expttayrat that would result in extra (wrong)
%    terms if there was no constant term in the Taylor series to be
%    raised to a power.
%   Removed some calls to !*n2f.
%   Replaced call to subs2 by subs2!* in taysimptan.
%   Corrected generation of new template in inttaylorwrttayvar
%
% 03-Jun-1993    1.4k
%   Changed expttayi to handle negative integer powers as well,
%    changed taysimpp correspondingly.
%
% 27-May-1993    1.4j
%   Corrected some oversights in taysimpexpt.
%   Added smacro TayTpVars.
%   Added code to taysimpp to handle s.f. as main variable.
%   Added function subtr!-degrees to tayutils.red.
%   Corrected error in var!-is!-nth in tayintro.red.
%   Corrected error in taydegree!-lessp in tayutils.red that caused
%    incorrect ordering of coefficients.
%   Corrected error in quottaylor1.
%
% 06-May-1993    1.4i
%   Changed printing routines for better interface to fancy printing.
%
% 21-Apr-1993    1.4h
%   Corrected cosec --> csc, cosech --> csch in tayfns.red.
%   Corrected taysimpsin to calculate coth with correct overall sign.
%   Improved handling of poles in taysimptan.
%   Rewritten taysimpsin to use exponential rather than tangent.
%   Added slight optimization in taylorsamevar if desired matches
%    actual expansion order.
%
% 01-Apr-1993    1.4g
%   Changed multtaylor1 so that in multiplication of Taylor kernels
%    products would be simplified as well.
%   Changed taysimptan to allow expansion point to be a pole of tan,
%    cot, tanh, or coth.
%   Changed constant!-sq!-p and taysimpexpt to recognize constant
%    expression in first argument to expt.
%   Changed printing so that only non-vanishing terms are counted.
%   Introduced taysimpsinh for simplifcation of expressions involving
%   sinh, coth, sech, csch.
%   Added subs2coefflist and resimpcoefflist smacros.
%   Modified addtaylor1, multtaylor1, quottaylor1 and invtaylor1 to call
%    subs2coefflist.
%   Protected klist property of atom Taylor!* against being filled
%    unnecessarily with intermediate results.
%   Changed expttayrat to print a more meaningful error message when it
%    detects a branch point.
%   Improved taysimpexpt to handle more cases.
%
% 08-Mar-1993    1.4f
%   Changed printing of Taylor kernels to include `(nn terms)' instead
%    of `...'.
%
% 25-Feb-1993    1.4e
%   Made expttayi more efficient by replacing straightforward 
%    multiplication by a scheme that computes powers of 2.
%   Corrected error in taysimpexp.
%
% 24-Feb-1993    1.4d
%   Corrected error in taydiffp.
%   Made especially multtaylor1, but also addtaylor1 and expttayrat
%    more efficient.
%
% 01-Feb-1993    1.4c
%   Corrected error in tayrevert1: constant term was missing.
%   Changed frlis!* from global to fluid.
%   Corrected error in taylor2f that caused certain expressions, like
%    i^2, not to be simplified correctly (discovered by Stan Kameny).
%
% 16-Apr-1992    1.4b
%   Corrected errors in taysimpexpt, mult!.comp!.tp!., and expttayrat.
%   The error corrected in version 1.4a was also present in invtaylor.
%   Corrected error in taylor2e/taylor2f.
%   Improved printing of negative coefficients.
%   Corrected error in subsubtaylor.
%   Corrected error in taysimpexp.
%   Added simp0fn property to Taylor!* for proper handling of
%    substitutions.
%   Added make!-cst!-coefficient smacro.
%   Added partial suppression of printing of coefficients and
%    TAYLORPRINTTERMS variable.
%
% 11-Feb-1992    1.4a
%   Corrected error in quottaylor1: If numerator or denominator had
%    a zero constant term, the result had the wrong number of terms.
%
% 09-Jan-1992    1.4
%   Implemented Taylor reversion.
%
%
% 07-Jan-1992    1.3e
%   Corrected bug in taysimpsin: wrong type of return value.
%
% 27-Nov-1991    1.3d
%   Corrected glitch in quottaylor1: Taylor kernel representing 0
%    as numerator gave an error.
%
% 06-Nov-1991    1.3c
%   Improved support for integration of expressions involving Taylor
%    kernels.
%
% 31-Oct-1991    1.3b
%   Added (limited) support for the integration of Taylor kernels.
%
% 19-Jul-1991    1.3a
%   Introduced taysimpmainvar for main variables that are standard
%    forms, as in factored expressions. Changed taysimpp accordingsly.
%   Introduced new smacros !*tay2f and !*tay2q that make Taylor kernels
%    unique.
%
% 03-Jun-1991    1.3
%   Started version for REDUCE 3.4.
%   Updated diffp according to changes for REDUCE 3.4.
%   Replaced freeof by has!-Taylor!* in taysimpf and taysimpt, and by
%    depends in difftaylor and taylor2e.
%   Changed module names to conform to file names.
%   Moved (nearly) all smacros into header (taylor) module,
%    made cst!-Taylor!* an smacro, moved remaining functions from
%    taymacros into tayutils, deleted taymacros module.
%   Made makecoeffpairs (in module taybasic) from smacro to expr.
%   Changed taylorsamevar to use TayOrig part of Taylor kernel if
%    present.
%   Changed Taylor printing functions since it is now possible to
%    pass information of operator precedence (via pprifn property).
%   Fixed bug in subsubtaylor (found by H. Melenk): did not substitute
%    in expansion point.
%   Changed error handling to call new function rerror instead of
%    rederr.
%   Changed for use of new hooks prepfn2, subfunc, and dfform
%    instead of redefining sqchk, subsublis, and diffp.
%
% 20-Oct-1990    1.2j
%   Added check in subsubtaylor for variable dependency.
%   Fixed stupid bug in taylorsamevar.
%   Corrected taysimpexpt to handle rational exponents with ON RATIONAL.
%   Corrected expttayrat: looks now at first NON-ZERO coefficient to
%    determine whether root can safely be computed.
%   Fixed bug in mult!.comp!.tp..
%   Added error check in invtaylor1 and quottaylor1.
%   Fixed bug in quottaylor1 that produced wrong results for
%    multidimensional Taylor series, introduced taydegree!-min2 and
%    taydegree!-strict!-less!-or!-equal!-p.
%
% 05-Oct-1990    1.2i
%   Replaced variable name nn by nm in taysimpsq to avoid name conflicts
%    with the SPDE package by Fritz Schwartz.
%   Replaced call to apply by apply1 in taysimpkernel.
%   Minor changes in expttayrat, taysimplog, taysimpexp, and taysimptan:
%    inserted explicit calls to invsq to allow negative numbers in
%    denominator.
%   Fixed bugs in difftaylorwrttayvar, inttaylorwrttayvar and
%    subsubtaylor: treatment of a Taylor kernel expanded about infinity
%    would give a wrong result.  Found by John Stewart.
%
% 11-Aug-1990    1.2h
%   Replaced call to get!* by get in diffp since get!* will no longer
%    be available in REDUCE 3.4.
%   Fixed bug in multintocoefflist that was introduced by replacing
%    car by TayCfPl.
%   Moved setting of TayOrig part from taylor1 to taylorexpand.  This
%    avoids Taylor kernels in the TayOrig part of multidimensional
%    Taylor expansions.  It does not fully solve the problem since
%    they can still be generated by applying the Taylor operator to
%    expressions that do not contain fully Taylor-combined Taylor
%    kernels.
%   Reversed list of expansions in call to taylorexpand in simptaylor.
%    Modified taylor1 accordingly.  Previously this could trigger a
%    `This can't happen' error message (due to incorrect ordering of
%    the Taylor variables).
%   Removed procedures delete!-coeff and replace!-coeff since they are
%    no longer used.
%   Moved call to subs2 out of differentiation loop in taylor2f,
%    improves timing significantly; deleted superfluous declared
%    integer variable.
%   Fixed bug in taylorsamevar.
%   Added extra checks and double evaluation of lists in simptaylor.
%   Replaced a number of ./ by !*f2q, introduced some !*n2f conversion
%    functions.
%   Development frozen, version shipped out.
%
% 06-May-1990    1.2g
%   Fixed bug in taylor2e that caused order of kernels in homogenous
%    expansions to be reverted.  Discovered by Karin Gatermann.
%   Removed binomial!-coeff since no longer needed (in expttayrat).
%   Replaced some forgotten car/cdr by TayCfPl/TayCfSq.
%   Reordered import declarations.
%   Replaced many semicolons by dollar signs.  Decreases amount of
%    printing during input of this file.
%   Minor bug fix in taysimpsin.
%   Minor change in taysimpasin and taysimpatan.
%   Split inttaylorwrttayvar into two procedures, added check for
%    logarithmic term in integration procedure inttaylorwrttayvar1.
%   Replaced combination of addsq and negsq by subtrsq in quottaylor1,
%    subsubtaylor and taysimplog.
%   Renamed taygetcoeff to TayGetCoeff (doesn't make any difference
%    on case-insensitive systems).
%   Minor changes in taymultcoeffs, multintocoefflist, resimptaylor,
%    taylor1sq, taylor2f, negtaylor1, quottaylor1, invtaylor1,
%    expttayrat, subsubtaylor, difftaylor, taysimpasin, taysimpatan,
%    taysimplog, taysimpexp, taysimptan, difftaylorwrttayvar,
%    inttaylorwrttayvar1, addtaylor1 (cons -> TayMakeCoeff).
%   Similar change in taysimpp (cons -> .**, i.e. to).
%
% 29-Mar-1990    1.2f
%   Fixed bug in taysimpf (addition of Taylor kernels) that could cause
%    "This can't happen" message.
%   Fixed bug in difftaylorwrttayvar: arguments to make!-cst!-coefflis
%    were interchanged.
%   Fixed similar bug in expttayrat (this procedure was never used!)
%   Added forgotten call to list in taylor2f.
%   Changed representation of big-O notation: print O(x^3,y^3) instead
%    of O(x^3*y^3) if expansion was done up to x^2*y^2.
%   Introduced new version of expttayrat (algorithm as quoted by Knuth)
%    which is faster by about a factor of two.
%   Fixed Taylor!-gen!-big!-O so that expansion point at infinity
%    is treated correctly for homogeneously expanded series.
%
% 27-Feb-1990    1.2e
%   Removed procedures addlogcoeffs, addexpcoeffs and addtancoeffs,
%    inserted code directly into taysimplog, taysimpexp, and
%    taysimptan.
%   taylorvars renamed to TayVars.
%   find!-non!-zero moved into Taylor!:macros module.
%
% 26-Feb-1990    1.2d
%   Added following selector, constructor, and modification smacros:
%    TayCfPl, TayCfSq, TayMakeCoeff, set!-TayCfPl, set!-TayCfSq,
%    TayTpElVars, TayTpElPoint, TayTpElOrder.
%   Some minor changes in several procedures to improve readability.
%
% 19-Jan-1990    1.2c
%   Removed first argument of addtaylor since never used.
%
% 14-Nov-1989    1.2b
%   Added taysimpsin.
%   Split tayinpoly1 off from tayinpoly, modified expttayrat
%    accordingly.
%   Changed global declarations to fluid.  No reason to prevent
%    binding.
%
% 11-Nov-1989    1.2a
%   Minor changes in the procedures changed yesterday (cleanup).
%   Added procedure taysimptan.
%   Replaced taylor1sq by taylorexpand in taysimpf1.
%   taysimpsq partly rewritten (will these bugs never die out?)
%   taysimpf1 renamed to taysimpf, taylor!*!-sf!-p to
%    Taylor!-kernel!-sf!-p.
%   Replaced a few of these by Taylor!-kernel!-sq!-p.
%
% 10-Nov-1989    1.2
%   Introduced taylorexpand to be the heart of simptaylor and to
%    replace simptaylor in taysimpt and multpowerintotaylor.
%   Added new versions of procedures taysimplog and taysimpexp
%    (Knuth's algorithm).
%   Taylor!:basic module moved up (so that some smacros are
%    defined earlier).
%
%
% 09-Nov-1989    1.1b
%   Fixed bug in taylor2e: quottaylor1 got the wrong template so
%    that it would truncate the resulting coeff list.
%   Added call to subs2 after call to diffsq in taylor2f so that
%    expressions containing radicals are simplified already at
%    this point.
%
% 21-Aug-1989    1.1a
%   Fixed bug in taysimpp: it could return a s.q. instead of a s.f.
%   Added a few forgotten import declarations.
%
% 31-Jul-1989    1.1
%   Slight changes in calls to confusion, minor change in taysimpp.
%   Introduced big-O notation, added taylorprintorder switch.
%   taysimpasin and taysimpatan now also calculate the inverse
%    hyperbolic functions.
%   New smacro Taylor!-kernel!-sq!-p replaces combinations of
%    kernp and Taylor!*p.
%
%
% 24-Jul-1989    1.0a
%   Bug fix in constant!-sq!-p: mvar applied to s.q., not s.f.
%   Added safety check in taysimpt.
%
% 27-May-1989    1.0
%   Decided to call this version 1.0, it seems to be sufficiently (?)
%    bug free to do so.
%   Minor bug fix in expttayrat (forgotten variable declaration).
%
%
% 25-May-1989    0.9l
%   Bug fixed in taylor2e
%    (thanx to Rich Winkel from UMC for pointing out this one).
%   Cleaned up the code for truncating when combining Taylor kernels
%    of different order.
%   Introduced taysimpasin for computing the asin, acos, etc.
%    of a Taylor kernel.
%   Changed some internal procedures to call addtaylor1, etc.
%    instead of addtaylor, etc. if both arguments have the same
%    template.
%   Changed representation of the coefflist: expansion with respect
%    to one variable is a special case of homogeneous expansion.
%    This is now reflected in the internal representation.  These
%    changes make the code shorter since all expansions are
%    done the same way (fewer checks necessary).
%
% 23-Mar-1989    0.9k
%   Numerous bug fixes.
%   Changed subsubtaylor to allow error checking in var!-is!-nth.
%   Rewrote taydegree!-lessp to iterate over its arguments rather
%    than call itself recursively.
%   Introduced exceeds!-order instead of taydegree!-lessp
%    (in truncate!-coefflist and multtaylor1).
%   Minor changes in Taylor!*!-sf!-p, taysimpexp, var!-is!-nth,
%    taysimpexpt, and inttaylorwrttayvar.
%   Changed simptaylor!* to apply resimp to all coefficients and
%    to the tayorig part.
%   Changed subsubtaylor to allow substitution of a kernel into a
%    homogeneously expanded expression.
%   Changed difftaylorwrttayvar to allow differentiation of
%    homogeneously expanded expressions.
%   Changed subsubtaylor so that substitution of a kernel is possible
%    (not only a variable).
%   New constructor smacros make!-Taylor!* and TayFlagsCombine replace
%    explicit list building.
%   New procedures: get!-degree and truncate!-coefflist induced
%    changes in addtaylor/multtaylor/quottaylor/invtaylor.
%    This fixes the other problem pointed out by H. Melenk.
%   Split addtaylor/multtaylor the same way as quottaylor/invtaylor.
%   Introduced taylorautocombine switch and interface to simp!*
%    (via mul!* list).
%   Added symbolic; statement in taylor!-intro module; necessary
%    until module/endmodule are fixed to work together with faslout.
%   Changed subsubtaylor to return a conventional prefix form
%    if all Taylor variables are substituted by constants.
%   Changed difftaylorwrttayvar to ensure that the coefflist of
%    the Taylor kernel it returns is not empty.
%   Changed subsubtaylor to avoid 0**n for n<=0 when substituting
%    a Taylor variable (to signal an error instead);  changed
%    taylor!-error accordingly.
%   Added taylortemplate operator, removed smacro
%    taylor!-map!-over!-coefficients.
%   Added code for expansion about infinity.
%   Split quottaylor into two parts: the driver (quottaylor) and
%    the routine doing the work (quottaylor1).  Same for invtaylor.
%   Rewrote the expansion procedures taylor1, taylor2,...
%   Change in taylor2e: added flg parameter, introduced
%    delete!-superfluous!-coeffs.
%   Added set!-tayorig and multintocoefflist.
%   Replaced simp by simp!* for simplication of tayorig part in
%    taysimplog and taysimpexpt.
%   Removed taysimpsq in taylorseriesp: it now returns t iff its
%    argument is a Taylor kernel disguised as a standard quotient.
%   Added taylororiginal operator.
%   Added a number of tests if tayorig of a Taylor kernel is non-nil
%    if !*taylorkeeporiginal is set.
%   Replaced calls to simpdiff in taylor2e and simpexpt by a call
%    to simp.
%   Minor change in taylor!*print!*1.
%   H. Melenk discovered that the code did not behave as documented:
%    addition of Taylor kernels differing only in their order did not
%    work, and Taylor expansion of a Taylor kernel w.r.t. one of its
%    variables would fail.
%    Corrected the latter problem by changing the substitution code
%    to allow a Taylor variable to be replaced by a constant.
%   taylorseriesp is now a boolean operator and therefore only
%    usable in if statements.
%   Replaced calls to subsq1/subf1 by subsq/subf,
%    definitions of subsq1 and taymaxdegree deleted.
%   Minor changes in taylor2hom and taylor2e.
%
% 28-Nov-1988    0.9j
%   Changed printing of `. . . ' to `...'.
%   Changed simptrig to simp in taysimpatan.
%   Changed simptaylor to simplify all its arguments, not only
%    the first one.
%   Added !*verboseload switch to conditionalize printing of
%    loading info.
%   Changed taylor2 to call taylor!-error instead of rederr.
%    Modified taylor!-error accordingly.
%
% 16-Nov-1988    0.9i
%   Fixed a Typo in quottaylor.
%   Inserted module/endmodule declarations.
%   Added errorset in taylor2 to catch zero denominators, etc.,
%    caused by expansion about essential singularities.
%
% 10-Nov-1988    0.9h
%   Fixed bugs that caused taking car of an atom (found by A.C.Hearn).
%    taysimpt used multf instead of multpf.
%    I also discovered a car/cdr of nil in
%    makecoeffpairs1/makecoeffpairshom1.
%    Reason: (nil . nil) == (nil), but what I want is
%    ((nil . nil)) == ((nil)).  Stupid, eh?
%
% 23-Jul-1988    0.9g
%   Added dmode!* to list of fluid variables,
%    removed taylor!-map!-over!-coefficients!-and!-orig.
%
% 26-May-1988    0.9f
%   Minor bug fix in taydegree!-lessp.
%
% 25-May-1988    0.9e
%   Fixed a number of smaller bugs.
%   Finally implemented multiplication and division for
%    homogeneously expanded Taylor series.
%   Today I realized that the procedure diffp in ALG2 had
%    been changed for REDUCE 3.3.
%
% 21-May-1988    0.9d
%   Fixed bug in invtaylor.
%   Rewrote quottaylor to do direct division rather use invtaylor.
%
% 14-May-1988    0.9c
%   Fixed substitution for expansion variable.
%
% 11-May-1988    0.9b
%   Fixed user interface functions taylorseriesp and taylortostandard.
%
% 10-May-1988    0.9a
%   Small changes in subsubtaylor and difftaylor to make the code
%    compilable, plus minor bug fixes.
%
% 08-May-1988    0.9
%    invtaylor changed to allow inversion of multidimensional
%     Taylor kernels (but still not for homogeneous expansion).
%
%
% 06-May-1988    0.8i
%   `conc' changed to `join' (for mnemonic purposes).
%
% 29-Apr-1988    0.8h
%   Minor bug fix in invtaylor (missing quote).
%
% 21-Mar-1988    0.8g
%   Minor change in TayDegreeSum.
%
% 17-Jan-1988    0.8f
%   Started implementation of homogeneous expansion
%    (required change in conversion to prefix form).
%
% 16-Jan-1988    0.8e
%   Minor change in the definition of confusion.
%
% 15-Jan-1988    0.8d
%   Changed to conform to REDUCE 3.3
%    (SWITCH statement, minor changes in parsing).
%
% 03-Jan-1988    0.8c
%   First version that is supposed to return always a correct result
%    (but not all possible cases are handled).
%
%
%*****************************************************************
%
%       Things to do:
%
%*****************************************************************
%
%     a) Finish implementation of homogeneous expansion (hard).
%     b) Find a method to handle singularities (very hard).
%     c) Perhaps I should change the definition of ORDP to order
%         Taylor kernels in some special way?
%     d) A better interface for the PART operator is desirable, e.g.
%         where the whole argument list to PART is passed to some
%         function.
%        It is not possible to interface to COEFF and COEFFN with
%         redefining the functions in the REDUCE kernel.
%     e) Rewrite the expansion code to recursively descend a standard
%         form.  This allows recognition of certain special functions,
%         e.g., roots and logarithms.  (Much work, requires rewriting
%         of a large part of the code.)
%     f) With e) it is easy to implement a DEFTAYLOR operator so that
%         the user may define the Taylor expansion of an unknown
%         function.
%     g) This would also allow the use of Taylor for power series
%         solutions of ODEs.
%     h) Implement a sort of lazy evaluation scheme, similar to the
%         one used in the TPS package written by Alan Barnes and
%         Julian Padget.  This would allow the calculation of more
%         terms of a series when they are needed.
%     i) Replace all non-id kernels that are independent of the Taylor
%         variables by gensyms.  This would reduce the size of the
%         expressions.
%
%

create!-package('(Taylor TayIntro TayUtils TayIntrf TayExpnd TayBasic
                  TaySimp      TaySubst  TayDiff  TayConv     TayPrint
                  TayFront TayFns TayRevrt TayImpl TayPart),
                '(contrib taylor));


%*****************************************************************
%
%       Non-local variables used in this package
%
%*****************************************************************

fluid  '(Taylor!:version        % version number
         Taylor!:date!*         % release date
         TaylorPrintTerms       % Number of terms to be printed
         !*tayexpanding!*
         !*tayinternal!*
         !*tayrestart!*
         !*taylorkeeporiginal   % \
         !*taylorautoexpand     %  \
         !*taylorautocombine    %   >  see below
         !*taylorprintorder     %  /
         !*trtaylor             % /
         convert!-Taylor!*
         !*sub2
         !*verboseload);

share TaylorPrintTerms;

comment This package has six switches:
        `TAYLORKEEPORIGINAL' causes the expression for which the
        expansion is performed to be kept.
        `TAYLORAUTOEXPAND' makes Taylor expressions ``contagious''
        in the sense that all other terms are automatically
        Taylor expanded and combined.
        `TAYLORAUTOCOMBINE' causes taysimpsq to be applied to all
        expressions containing Taylor kernels.  This is equivalent
        to applying `TAYLORCOMBINE' to all those expressions.
        If `TAYLORPRINTORDER' is set to ON Taylor kernels are
        printed in big-O notation instead of just printing three dots.
        `TRTAYLOR', if on, prints some information about the expansion
        process.
        `VERBOSELOAD' is a variable used by Portable Standard Lisp
        and causes a loading info to be printed;

switch taylorautocombine,
       taylorautoexpand,
       taylorkeeporiginal,
       taylorprintorder,
       trtaylor,
       verboseload;

convert!-Taylor!* := nil;      % flag indicating that Taylor kernels
                               % should be converted to prefix forms
TaylorPrintTerms := 5;         % Only this nubmer of non-zero terms 
                               % will normally be printed.
!*taylorkeeporiginal := nil;   % used to indicate if the original
                               % expressions (before the expansion)
                               % are to be kept.
!*taylorautoexpand := nil;     % set if non-taylor expressions are to
                               % be expanded automatically on
                               % combination.
!*taylorautocombine := t;      % set if taysimpsq should be added to
                               % the MUL!* list.
!*taylorprintorder := t;       % set if Taylor kernels should be printed
                               % with big-O notation, now on by default.
%!*verboseload := nil;         % set if loading info should be printed
!*tayexpanding!* := nil;       % set by taylorexpand to indicate that
                               % expansion is in progress.
!*tayinternal!* := nil;        % set while doing internal computations,
                               % to indicate that some normal REDUCE 
                               % operations should not be done (like
                               % making kernels unique and storing them.
!*tayrestart!* := nil;         % set by Taylor!-error!* if expansion is
                               % in progress to indicate that the error
                               % might disappear if the order is
                               % increased.
Taylor!:version := "2.2b";      % version number of the package
Taylor!:date!* := "08-Jun-2001"; % release date

if !*verboseload then
  << terpri ();
     prin2 "TAYLOR PACKAGE, version ";
     prin2 Taylor!:version;
     prin2 ", as of ";
     prin2 Taylor!:date!*;
     prin2t " for REDUCE 3.7 being loaded...";
     terpri () >> ;



exports !*tay2f, !*tay2q, !*TayExp2q, copy!-list, cst!-Taylor!*,
        get!-degree, get!-degreelist, has!-Taylor!*, has!-TayVars,
        make!-cst!-coefficient, make!-cst!-coefflis,
        make!-cst!-powerlist, make!-Taylor!*, multintocoefflist,
        nzerolist, prepTayExp, resimpcoefflist, resimptaylor,
        set!-TayCfPl, set!-TayCfSq, set!-TayCoeffList, set!-TayFlags,
        set!-TayOrig, set!-TayTemplate, subs2coefflist, TayCfPl,
        TayCfSq, TayCoeffList, TayDegreeSum, TayExp!-difference,
        TayExp!-greaterp, TayExp!-lessp, TayExp!-max2, TayExp!-min2,
        TayExp!-minus, TayExp!-minusp, TayExp!-plus, TayExp!-plus2,
        TayExp!-times, TayExp!-times2, TayFlags, TayFlagsCombine,
        TayGetCoeff, Taylor!*p, Taylor!-kernel!-sf!-p,
        Taylor!-kernel!-sq!-p, Taylor!:, TayMakeCoeff, taymincoeff,
        taymultcoeffs, TayOrig, TayTemplate, TayTpElNext,
        TayTpElOrder, TayTpElPoint, TayTpElVars, TayVars,
        TpDegreeList;

imports

% from REDUCE kernel: 
        !*f2q, !*i2rn, !*p2f, !*p2q, !:minusp, confusion, domainp,
        eqcar, kernp, lastpair, lc, ldeg, lpriw, mathprint, mk!*sq,
        mksp, multsq, mvar, nlist, numr, over, prin2t, red, resimp,
        rndifference!:, rnminus!:, rnminusp!:, rnplus!:, rnprep!:,
        rnquotient!:, rntimes!:, simprn, smember, subs2, subs2!*,

% from module Tayintro:
        smemberlp,

% from module Tayutils:
        add!-degrees;

%*****************************************************************
%
%      General utility smacros
%
%*****************************************************************


symbolic smacro procedure nzerolist n;
  %
  % generates a list of n zeros
  %
  nlist (0, n);

symbolic smacro procedure copy!-list l;
  %
  % produces a copy of list l.
  %
  append (l, nil);



%*****************************************************************
%
%      Selector and constructor smacros for Taylor kernels
%
%*****************************************************************


symbolic smacro procedure make!-Taylor!* (cflis, tp, orig, flgs);
  %
  % Builds a new Taylor kernel structure out of its parts.
  %
  {'Taylor!*, cflis, tp, orig, flgs};

symbolic smacro procedure TayMakeCoeff (u, v);
  %
  % Builds a coefficient from degreelist and s.q.
  %
  u . v;


comment Selector smacros for the parts of a Taylor kernel;

symbolic smacro procedure TayCoeffList u; cadr u;

symbolic smacro procedure TayTemplate u; caddr u;

symbolic smacro procedure TayOrig u; cadddr u;

symbolic smacro procedure TayFlags u; car cddddr u;

symbolic smacro procedure TayCfPl u; car u;

symbolic smacro procedure TayCfSq u; cdr u;

symbolic smacro procedure TayTpVars tp;
  for each x in tp join copy!-list car x;

symbolic smacro procedure TayVars u;
  TayTpVars TayTemplate u;

symbolic smacro procedure TayGetCoeff (degrlis, coefflis);
  (if null cc then nil ./ 1 else TayCfSq cc)
    where cc := assoc (degrlis, coefflis);

symbolic smacro procedure TayTpElVars u; car u;

symbolic smacro procedure TayTpElPoint u; cadr u;

symbolic smacro procedure TayTpElOrder u; caddr u;

symbolic smacro procedure TayTpElNext u; cadddr u;

symbolic smacro procedure TpDegreeList tp;
  for each x in tp collect TayTpElOrder x;

symbolic smacro procedure TpNextList tp;
  for each x in tp collect TayTpElNext x;

%symbolic smacro procedure TayDegreeList u;
%  TpDegreeList TayTemplate u;

symbolic smacro procedure TayDegreeSum u;
  for each x in TayTemplate u sum TayTpElOrder x;


comment Modification smacros;

symbolic smacro procedure set!-TayCoeffList (u, v);
  %
  % Sets TayCoeffList part of Taylor kernel u to v
  %
  rplaca (cdr u, v);

symbolic smacro procedure set!-TayTemplate (u, v);
  %
  % Sets TayTemplate part of Taylor kernel u to v
  %
  rplaca (cddr u, v);

symbolic smacro procedure set!-TayOrig (u, v);
  %
  % Sets TayOrig part of Taylor kernel u to v
  %
  rplaca (cdddr u, v);

symbolic smacro procedure set!-TayFlags (u, v);
  %
  % Sets TayFlags part of Taylor kernel u to v
  %
  rplaca (cddddr u, v);

symbolic smacro procedure set!-TayCfPl (u, v);
  rplaca (u, v);

symbolic smacro procedure set!-TayCfSq (u, v);
  rplacd (u, v);


comment Smacro that implement arithmetic operations on
        exponents in powerlist;

symbolic smacro procedure exponent!-check!-int rn;
   if cddr rn=1 then cadr rn else rn;

symbolic procedure !*TayExp2q u;
   if atom u then !*f2q (if zerop u then nil else u)
    else cdr u;

symbolic procedure !*q2TayExp u;
   (if null x then confusion '!*q2TayExp
     else exponent!-check!-int car x)
      where x := simprn {mk!*sq u};

symbolic procedure prepTayExp u;
   if atom u then u else rnprep!: u;

symbolic macro procedure TayExp!-plus x;
   if null cdr x then 0
    else if null cddr x then cadr x
    else expand(cdr x,'TayExp!-plus2);   

symbolic procedure TayExp!-plus2(e1,e2);
   if atom e1 and atom e2 then e1+e2
    else exponent!-check!-int(
           if atom e1 then rnplus!:(!*i2rn e1,e2)
            else if atom e2 then rnplus!:(e1,!*i2rn e2)
            else rnplus!:(e1,e2));

symbolic procedure TayExp!-difference(e1,e2);
   if atom e1 and atom e2 then e1-e2
    else exponent!-check!-int(
           if atom e1 then rndifference!:(!*i2rn e1,e2)
            else if atom e2 then rndifference!:(e1,!*i2rn e2)
            else rndifference!:(e1,e2));

symbolic procedure TayExp!-minus e;
   if atom e then -e else rnminus!: e;

symbolic macro procedure TayExp!-times x;
   if null cdr x then 1
    else if null cddr x then cadr x
    else expand(cdr x,'TayExp!-times2);   

symbolic procedure TayExp!-times2(e1,e2);
   if atom e1 and atom e2 then e1*e2
    else exponent!-check!-int(
           if atom e1 then rntimes!:(!*i2rn e1,e2)
            else if atom e2 then rntimes!:(e1,!*i2rn e2)
            else rntimes!:(e1,e2));

symbolic procedure TayExp!-quotient(u,v);
   exponent!-check!-int
     rnquotient!:(if atom u then !*i2rn u else u,
                  if atom v then !*i2rn v else v);

symbolic procedure TayExp!-minusp e;
   if atom e then minusp e
    else rnminusp!: e;

symbolic procedure TayExp!-greaterp(a,b);
   TayExp!-lessp(b,a);

symbolic macro procedure TayExp!-geq x;
   {'not,'TayExp!-lessp . cdr x};

symbolic procedure TayExp!-lessp(e1,e2);
   if atom e1 and atom e2 then e1<e2
    else !:minusp TayExp!-difference(e1,e2);

symbolic macro procedure TayExp!-leq x;
   {'not,'TayExp!-greaterp . cdr x};

symbolic procedure TayExp!-max2(e1,e2);
   if TayExp!-lessp(e1,e2) then e2 else e1;

symbolic procedure TayExp!-min2(e1,e2);
   if TayExp!-lessp(e1,e2) then e1 else e2;

symbolic macro procedure Taylor!: u;
   sublis('((plus . TayExp!-plus)
            (plus2 . TayExp!-plus2)
            (difference . TayExp!-difference)
            (minus . TayExp!-minus)
            (times . TayExp!-times)
            (times2 . TayExp!-times2)
            (minusp . TayExp!-minusp)
            (greaterp . TayExp!-greaterp)
            (geq . TayExp!-geq)
            (lessp . TayExp!-lessp)
            (leq . TayExp!-leq)
            (max2 . TayExp!-max2)
            (min2 . TayExp!-min2)),
          cadr u);
           


comment Smacros and procedures that are commonly used ;

symbolic smacro procedure TayFlagsCombine (u, v);
  %
  % Not much for now
  %
  nil;

symbolic smacro procedure get!-degree dg;
  %
  % Procedure to handle degree parts of Taylor kernels.
  %
  Taylor!: for each n in dg sum n;

symbolic smacro procedure get!-degreelist dgl;
   for each dg in dgl collect get!-degree dg;

symbolic smacro procedure invert!-powerlist pl;
   Taylor!:
     for each nl in pl collect
       for each p in nl collect -p;

symbolic smacro procedure taymultcoeffs (c1, c2);
  %
  % (TayCoeff, TayCoeff) -> TayCoeff
  %
  % multiplies the two coefficients c1,c2.
  % both are of the form (TayPowerList . s.q.)
  % so generate an appropriate degreelist by adding the degrees.
  %
  TayMakeCoeff (add!-degrees (TayCfPl c1, TayCfPl c2),
                multsq (TayCfSq c1, TayCfSq c2));

symbolic smacro procedure prune!-coefflist(cflist);
   <<while not null cflis and null numr TayCfSq car cflis
       do cflis := cdr cflis;
     cflis>> where cflis := cflist;

symbolic smacro procedure multintocoefflist(coefflis,sq);
  %
  % (TayCoeffList, s.q.) -> TayCoeffList
  %
  % Multiplies each coefficient in coefflis by the s.q. sq.
  %
  for each p in coefflis collect
    TayMakeCoeff(TayCfPl p,resimp subs2!* multsq(TayCfSq p,sq));

symbolic smacro procedure subs2coefflist clist;
  for each pp in clist join
    ((if not null numr sq then {TayMakeCoeff(TayCfPl pp,sq)})
     where sq := subs2!* TayCfSq pp);

symbolic smacro procedure resimpcoefflist clist;
  for each cc in clist collect
    TayMakeCoeff(TayCfPl cc,subs2 resimp TayCfSq cc);

symbolic smacro procedure resimptaylor u;
  %
  % (TaylorKernel) -> TaylorKernel
  %
  % u is a Taylor kernel, value is the Taylor kernel
  % with coefficients and TayOrig part resimplified
  %
  make!-Taylor!* (
         resimpcoefflist TayCoeffList u,
         TayTemplate u,
         if !*taylorkeeporiginal and TayOrig u
           then resimp TayOrig u else nil,
         TayFlags u);

symbolic smacro procedure make!-cst!-powerlist tp;
  %
  % (TayTemplate) -> TayPowerList
  %
  % Generates a powerlist for the constant coefficient
  % according to template tp
  %
  for each el in tp collect nzerolist length TayTpElVars el;

symbolic smacro procedure make!-cst!-coefficient (cst, tp);
  %
  % (s.q., TayTemplate) -> TayCoefficient
  %
  % Generates the constant coefficient cst
  % according to Taylor template tp
  %
  TayMakeCoeff (make!-cst!-powerlist tp, cst);

symbolic smacro procedure make!-cst!-coefflis (cst, tp);
  %
  % (s.q., TayTemplate) -> TayCoeffList
  %
  % Generates a TayCoeffList with only the constant coefficient cst
  % according to Taylor template tp
  %
  {make!-cst!-coefficient (cst, tp)};

symbolic smacro procedure cst!-Taylor!* (cst, tp);
  %
  % (s.q., TayTemplate) -> TaylorKernel
  %
  % generates a Taylor kernel with template tp for the constant cst.
  %
  make!-Taylor!* (
     make!-cst!-coefflis (cst, tp), tp, cst, nil);


comment Predicates;

symbolic smacro procedure has!-Taylor!* u;
  %
  % (Any) -> Boolean
  %
  % checks if an expression u contains a Taylor kernel
  %
  smember ('Taylor!*, u);

symbolic smacro procedure Taylor!*p u;
  %
  % (Kernel) -> Boolean
  %
  % checks if kernel u is a Taylor kernel
  %
  eqcar (u, 'Taylor!*);

symbolic smacro procedure Taylor!-kernel!-sf!-p u;
  %
  % (s.f.) -> Boolean
  %
  % checks if s.f. u is a Taylor kernel
  %
  not domainp u and null red u and lc u = 1
     and ldeg u = 1 and Taylor!*p mvar u;

symbolic smacro procedure Taylor!-kernel!-sq!-p u;
  %
  % u is a standard quotient,
  % returns t if it is simply a Taylor kernel
  %
  kernp u and Taylor!*p mvar numr u;

symbolic smacro procedure has!-TayVars(tay,ex);
   %
   % Checks whether ex contains any of the Taylor variables
   %  of Taylor kernel tay.
   %
   smemberlp(TayVars tay,ex);

symbolic procedure Taylor!*!-zerop tay;
   TayCoeffList!-zerop TayCoefflist tay;

symbolic procedure TayCoeffList!-zerop tcl;
   null tcl
     or null numr TayCfSq car tcl and TayCoeffList!-zerop cdr tcl;


comment smacros for the generation of unique Taylor kernels;

symbolic smacro procedure !*tay2f u;
  !*p2f mksp (u, 1);

symbolic smacro procedure !*tay2q u;
  !*p2q mksp (u, 1);


comment some procedures for tracing;

symbolic smacro procedure Taylor!-trace u;
   if !*trtaylor then lpri("Taylor: " . if u and atom u then list u else u);

symbolic smacro procedure Taylor!-trace!-mprint u;
   if !*trtaylor then mathprint u;

endmodule;

end;
