module TayConv;

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
%     Functions converting Taylor kernels to prefix forms
%
%*****************************************************************


exports prepTaylor!*!*, prepTaylor!*, prepTaylor!*1,
        Taylor!-gen!-big!-O;

imports

% from the REDUCE kernel:
        eqcar, lastpair, prepsq!*, replus, retimes, reval,

% from the header module:
        prepTayExp, TayCfPl, TayCfSq, TayCoeffList, TayTemplate,
        TayTpElNext, TayTpElPoint, TayTpElVars;


fluid '(convert!-Taylor!*
        TaylorPrintTerms
        Taylor!-truncation!-flag);


symbolic procedure prepTaylor!*1 (coefflist, template, no!-of!-terms);
  replus for each cc in coefflist join
    begin scalar x; integer count;
      if Taylor!-truncation!-flag then return nil;
      x := prepTaylor!*2 (cc, template);
      if null x or null no!-of!-terms then return x;
      no!-of!-terms := no!-of!-terms - 1;
      if no!-of!-terms < 0
        then << Taylor!-truncation!-flag := t;
                return nil >>;
      return x
    end;

symbolic procedure prepTaylor!*2 (coeff, template);
  (lambda (pc);
    if pc = 0 then nil
     else {retimes (
            (if eqcar (pc, 'quotient) and eqcar (cadr pc, 'minus)
               then {'minus, {'quotient, cadr cadr pc, caddr pc}}
              else pc) . preptaycoeff (TayCfPl coeff, template))})
    (prepsq!* TayCfSq coeff);


symbolic procedure checkdifference (var, var0);
  if var0 = 0 then var else {'difference, var, var0};

symbolic procedure checkexp(bas,exp);
  if exp = 0 then 1
   else if exp = 1 then bas
   else {'expt,bas,prepTayExp exp};

symbolic smacro procedure checkpower (var, var0, n);
  if var0 eq 'infinity
    then if n = 0 then 1
          else {'quotient, 1, checkexp (var, n)}
   else checkexp (checkdifference (var, reval var0), n);

symbolic procedure preptaycoeff (cc, template);
  begin scalar result;
    while not null template do begin scalar ccl;
      ccl := car cc;
      for each var in TayTpElVars car template do <<
        result := checkpower (var, TayTpElPoint car template, car ccl)
                    . result;
        ccl := cdr ccl >>;
      cc := cdr cc;
      template := cdr template
    end;
    return result
  end;

put ('taylor!*, 'prepfn2, 'preptaylor!*!*);

symbolic procedure prepTaylor!*!* u;
   if null convert!-taylor!* then u else preptaylor!* u;

symbolic procedure prepTaylor!* u;
   prepTaylor!*1 (TayCoeffList u, TayTemplate u, nil);

symbolic procedure Taylor!-gen!-big!-O tp;
  %
  % Generates a big-O notation for the Taylor template tp
  %
  "O" . for each el in tp collect
          if null cdr TayTpElVars el
            then checkpower(car TayTpElVars el,TayTpElPoint el,
                            TayTpElNext el)
           else begin scalar var0;
             var0 := reval TayTpElPoint el;
             return
               if var0 eq 'infinity
                 then {'quotient,1,
                       checkexp('list . TayTpElVars el,TayTpElNext el)}
                else checkexp(
                 'list .
                   for each krnl in TayTpElVars el collect
                     checkdifference(krnl,var0),
                 TayTpElNext el)
           end;

endmodule;

end;
