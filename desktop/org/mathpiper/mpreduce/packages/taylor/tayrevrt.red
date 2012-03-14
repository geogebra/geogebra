module TayRevrt;

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


%*****************************************************************
%
%       Functions for reversion of Taylor kernels
%
%*****************************************************************


exports taylorrevert;

imports

% from the REDUCE kernel:
        !*a2k, !*q2a, invsq, lastpair, mvar, neq, nth, numr, over,
        reval, simp!*, typerr,

% from the header module:
        !*TayExp2q, cst!-Taylor!*, make!-cst!-coefficient,
        make!-taylor!*, multtaylorsq, prepTayExp, prune!-coefflist,
        set!-TayTemplate, TayCfPl, TayCfSq, TayCoeffList,
        TayExp!-Quotient, Taylor!:, TayMakeCoeff,
        Taylor!-kernel!-sq!-p, TayTemplate, TayTpElNext, TayTpElOrder,
        TayTpElPoint, TayTpElVars,

% from module Tayintro:
        delete!-nth, Taylor!-error,

% from module Taybasic:
        addtaylor, invtaylor, multtaylor, negtaylor,

% from module TaySimp:
        expttayrat,

% from module Tayutils:
        enter!-sorted, smallest!-increment;


symbolic procedure tayrevert (tay, okrnl, krnl);
  %
  % Reverts Taylor kernel tay that has okrnl as variable
  %  into a Taylor kernel that has krnl as variable.
  %
  % This is the driver procedure; it check whether okrnl
  %  is valid for this operation and calls tayrevert1 to do the work.
  %
  begin scalar tp, cfl, x; integer i;
    cfl := TayCoeffList tay;
    tp := TayTemplate tay;
    x := tp;
    i := 1;
    %
    % Loop through the template and make sure that the kernel
    %  okrnl is present and not part of a homogeneous template.
    %
   loop:
    if okrnl member TayTpElVars car x then <<
      if not null cdr TayTpElVars car x then <<
        Taylor!-error ('tayrevert,
                       {"Kernel", okrnl,
                        "appears in homogenous template", car x});
        return
        >>
       else goto found;
      >>
     else <<
       x := cdr x;
       i := i + 1;
       if not null x then goto loop
       >>;
    Taylor!-error
      ('tayrevert, {"Kernel", okrnl, "not found in template"});
    return;
   found:
    return tayrevert1 (tp, cfl, car x, i, okrnl, krnl)
  end;


symbolic procedure tayrevertreorder (cf, i);
  %
  % reorders coefflist cf so that
  %  a) part i of template is put first
  %  b) the resulting coefflist is again ordered properly
  %
  begin scalar cf1, pl, sq;
    for each pp in cf do <<
      pl := TayCfPl pp;
      sq := TayCfSq pp;
      pl := nth (pl, i) . delete!-nth (pl, i);
      cf1 := enter!-sorted (TayMakeCoeff (pl, sq), cf1)
      >>;
    return cf1
  end;

symbolic procedure tayrevertfirstdegreecoeffs (cf, n);
  %
  % Returns a coefflist that corresponds to the coefficient
  %  of (the first kernel in the template) ** n.
  %
  for each el in cf join
    if car car TayCfPl el = n and not null numr TayCfSq el
     then {TayMakeCoeff (cdr TayCfPl el, TayCfSq el)} else nil;

symbolic procedure tayrevert1(tp,cf,el,i,okrnl,krnl);
  %
  % This is the procedure that does the real work.
  %  tp is the old template,
  %  cf the old coefflist,
  %  el the element of the template that contains the "old" kernel,
  %  i its position in the template,
  %  okrnl the old kernel,
  %  krnl the new kernel.
  %
  Taylor!:
  begin scalar first,incr,newtp,newcf,newpoint,newel,u,u!-k,v,w,x,x1,n,
               expo,upper;
    %
    % First step: reorder the coefflist as if the okrnl appears
    %              at the beginning of the template and construct a
    %              new template by first deleting this element from it.
    %
    newcf := prune!-coefflist tayrevertreorder (cf, i);
    newtp := delete!-nth (tp, i);
    %
    % Check that the lowest degree of okrnl is -1, 0, or +1.
    %  For -1, we have a first order pole.
    %  For 0, reversion is possible only if the coefficient
    %   of okrnl is a constant w.r.t. the other Taylor variables.
    %  For +1, we use the algorithm quoted by Knuth,
    %   in: The Art of Computer Programming, vol2. p. 508.
    %
    n := car car TayCfPl car newcf;
    if n < 0
      then tayrevert1pole(tp,cf,el,i,okrnl,krnl,newcf,newtp);
    if n = 0
      then if not null newtp
             then begin scalar xx;
               xx := tayrevertfirstdegreecoeffs(newcf,0);
               if length xx > 1
                 then Taylor!-error
                       ('tayrevert,
                        "Term with power 0 is a Taylor series");
               xx := car xx;
               for each el in TayCfPl xx do
                 for each el2 in el do
                   if el2 neq 0
                     then Taylor!-error
                           ('tayrevert,
                            "Term with power 0 is a Taylor series");
               newpoint := !*q2a TayCfSq xx;
              end
            else <<newpoint := !*q2a TayCfSq car newcf;
                   newcf := prune!-coefflist cdr newcf;
                   n := car car TayCfPl car newcf>>
     else newpoint := 0;
    tp := {{krnl},newpoint,TayTpElOrder el,TayTpElNext el} . newtp;
    first := TayExp!-quotient(1,n);
    incr := car smallest!-increment newcf;
    expo := first * incr;
    if not(expo=1)
      then (<<newcf := TayCoeffList newtay;
              tp := TayTemplate newtay;
              newtp := cdr tp;
              tp := {TayTpElVars car tp,
                     reval {'expt,TayTpElPoint car tp,prepTayExp expo},
                     TayTpElOrder car tp * expo,
                     TayTpElNext car tp * expo}
                    . newtp>>
            where newtay := expttayrat(Make!-Taylor!*(newcf,tp,nil,nil),
                                       !*TayExp2q expo));
    upper := TayExp!-quotient(TayTpElNext car tp,incr) - 1;
    x := tayrevertfirstdegreecoeffs(newcf,incr);
    x1 := x := invtaylor make!-Taylor!*(x,newtp,nil,nil);
    w := for each pp in TayCoeffList x1 collect
           TayMakeCoeff({expo} . TayCfPl pp,TayCfSq pp);

    v := mkvect upper;

    for j := 2 : upper do
      putv(v,j,
           multtaylor(
               x,
               make!-Taylor!*(tayrevertfirstdegreecoeffs(newcf,j*incr),
                              newtp,nil,nil)));

    u := mkvect upper;

    putv(u,0,cst!-Taylor!*(1 ./ 1,newtp));
    for j := 2 : upper do <<
      for k := 1 : j - 2 do begin
        u!-k := getv(u,k);
        for l := k - 1 step -1 until 0 do
          u!-k := addtaylor
                   (u!-k,
                    negtaylor
                      multtaylor(getv(u,l),
                                 getv(v,k - l + 1)));
        putv(u,k,u!-k);
        end;
      u!-k := multtaylorsq(getv(v,j),!*TayExp2q j);

      for k := 1 : j - 2 do
        u!-k := addtaylor
                 (multtaylorsq(multtaylor(getv(u,k),
                                          getv(v,j - k)),
                               !*TayExp2q (j - k)),
                  u!-k);

      u!-k := negtaylor u!-k;
      putv(u,j - 1,u!-k);
%
      x1 := multtaylor(x1,x);            % x1 is now x ** j
      for each pp in TayCoeffList
             multtaylor(multtaylorsq
                           (getv(u,j - 1),
                            invsq !*TayExp2q j),x1) do
        w := enter!-sorted (TayMakeCoeff({j * expo}
                         . TayCfPl pp,TayCfSq pp),
                            w);
      >>;
%
      newtp := (car tp) . newtp;
      w := enter!-sorted(
             make!-cst!-coefficient(simp!* TayTpElPoint el,newtp),
             w);

    w := Make!-taylor!*(w,newtp,nil,nil);
    return if incr = 1 then w
            else expttayrat(w,invsq !*TayExp2q incr)
  end;

comment The mechanism for a first order pole is very simple:
        This corresponds to a first order zero at infinity,
        so we invert the original kernel and revert the result;

symbolic procedure tayrevert1pole (tp, cf, el, i, okrnl, krnl,
                                   newcf, newtp);
  begin scalar x, y, z;
    cf := TayCoeffList invtaylor make!-Taylor!*(cf,tp,nil,nil);
    x := tayrevert1 (tp, cf, el, i, okrnl, krnl);
    y := TayTemplate x;
    if TayTpElPoint car y neq 0
      then Taylor!-error ('not!-implemented,
                          "(Taylor series reversion)")
     else <<
       set!-TayTemplate (x, {{krnl}, 'infinity, TayTpElOrder car y}
                              . cdr y);
       return x >>
  end;

comment The driver routine;

symbolic procedure TaylorRevert (u, okrnl, nkrnl);
  (if not Taylor!-kernel!-sq!-p sq
     then typerr (u, "Taylor kernel")
    else tayrevert (mvar numr sq, !*a2k okrnl, !*a2k nkrnl))
     where sq := simp!* u$

flag ('(TaylorRevert), 'opfn);

endmodule;

end;
