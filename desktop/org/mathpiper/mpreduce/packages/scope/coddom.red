module coddom;

% ------------------------------------------------------------------- ;
% Copyright : J.A. van Hulzen, Twente University, Dept. of Computer   ;
%             Science, P.O.Box 217, 7500 AE Enschede, the Netherlands.;
% Author    : W.N. Borst.                                             ;
% ------------------------------------------------------------------- ;

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


symbolic$

fluid '(!:prec!:);
fluid '(pline!* posn!* orig!* ycoord!* ymax!* ymin!*);

symbolic procedure zeropp u;
% Returns T if u equals 0, regardless of u being
% an integer or an floating-point number.
   if atom u then zerop u
    else if car u eq '!:rd!: then rd!:zerop u
    else nil$

symbolic procedure constp c;
% Returns T iff c is a number, NIL otherwise
   numberp(c) or (pairp(c) and memq(car c, domainlist!*))$

symbolic procedure integerp i;
% Returns T iff i is an integer, NIL otherwise
   numberp(i) and not floatp(i)$

symbolic procedure floatprop f;
% Returns T iff f is a (domain mode) float, NIL otherwise
   floatp(f) or eqcar(f,'!:rd!:)$

symbolic procedure domprop d;
% Returns T iff d is a domain element, NIL otherwise
   pairp(d) and memq(car d, domainlist!*);

symbolic procedure doublep d;
% Returns T iff d is an arbitrary precision rounded number, else NIL
   eqcar(d,'!:rd!:) and pairp(cdr d);

symbolic procedure nil2zero u;
% Conversion NIL -> 0 needed for domain mode operations
   if null(u) then 0 else u;

symbolic procedure zero2nil u;
% Conversion 0 -> NIL needed for domain mode operations
   if !:zerop(u) then nil else u;

symbolic procedure dm!-plus(u,v);
   nil2zero(!:plus(zero2nil u, zero2nil v));

symbolic procedure dm!-difference(u,v);
   nil2zero(!:difference(zero2nil u, v));

symbolic procedure dm!-minus(u);
   nil2zero(!:minus(u));

symbolic procedure dm!-abs(u);
   if !:minusp(u) then dm!-minus(u) else u;

symbolic procedure dm!-min(u,v);
% Domain mode minimum
   if dm!-gt(u,v) then v else u;

symbolic procedure dm!-max(u,v);
% Domain mode maximum
   if dm!-gt(u,v) then u else v;

symbolic procedure dm!-times(u,v);
   nil2zero(!:times(zero2nil u,zero2nil v));

symbolic procedure dm!-mkfloat(u);
% Use consistent and version independent trafo:
   if integerp u then
      %'!:rd!: . (u + 0.0)
      %i2rd!* u
      apply1(get('!:rd!:,'i2d),u)
   else u;

symbolic procedure dm!-quotient(u,v);
% ---
% Domain mode quotient
% Always performs a floating point division and returns integers
% when possible
% ---
begin scalar noequiv;
   noequiv:=!*noequiv;
   !*noequiv:=nil;            % for integer results in productscheme
   return nil2zero(!:quotient(dm!-mkfloat u,dm!-mkfloat v));
   !*noequiv:=noequiv;
end;

symbolic procedure dm!-expt(u,n);
   nil2zero(!:expt(zero2nil u,n));

symbolic procedure dm!-gt(u,v);
% Domain mode greater than
   !:minusp(dm!-difference(v,u));

symbolic procedure dm!-eq(u,v);
% Domain mode equal to
   !:zerop(dm!-difference(u,v));

symbolic procedure dm!-lt(u,v);
% Domain mode less than
   !:minusp dm!-difference(u,v);

symbolic procedure dm!-print(p);
% ---
% Domain mode PRIN2. This is an adapted version of mathprint.
% It is used for printing floats in the data structures
% (part 1 of CODPRI)
% ---
begin
   terpri!* nil;
   maprint(p,0);
   pline!* := reverse pline!*;
   scprint(pline!*, ymax!*);
   pline!* := nil;
   posn!* := orig!*;
   ycoord!* := ymax!* := ymin!* := 0;
end;

symbolic procedure rd!:zerop!: u;
   if atom cdr u then
      ft!:zerop cdr u
   else
      bfzerop!: round!* u;

%-----------------------------------
% R3.5 seems to have machine-dependent precision algorithms.
% So we comment this out :
%
%symbolic procedure bfzerop!: u;
%% A new bigfloat zerop test which respects the precision setting
%begin scalar x;
%   return
%      << x:=cadr(u) * 10^(cddr(u) + !:prec!:);
%         ((x>-50) and (x<50))
%      >>
%end;

symbolic procedure ft!:zerop u;
begin scalar x;
   return
      << x:=u * 10^!:prec!:;
         (x>-50 and x<50)
      >>
end;

symbolic procedure ftintequiv u;
begin scalar x;
   return
      if ft!:zerop(u-(x := fix u)) then x else nil
end;

symbolic procedure dm!-fixp u;
% u = (m . e), meaning m*10^e.
% Returned : fix(u) if u is interpretable as an integer,
% nil otherwise.
%                                                  JB 14/4/94
begin scalar r,fp;
   r:=reverse explode car u;
   fp:='t;
   if (cdr u) >= 0
      then for i:=1:(cdr u) do r:='!0 . r
      else if (fp:=(length(r) > -(cdr u)))
              then for i:=1:-cdr(u) do <<fp:=fp and eq(car r,'!0);
                                         r:=cdr r>>
              else r:= list '!0;
    return if fp then compress reverse r
                 else nil;
   end;

symbolic procedure bfintequiv u;
% We need to be sure we work with radix 10.
% This is guaranteed by `internal2decimal'.
% We need `dm!-fixp' to avoid entering an endless loop.
%                                                  JB 14/4/94
begin scalar i;
   i:=dm!-fixp internal2decimal(u,!:prec!:);
   return
      if i then i else u
end;

symbolic procedure rdintequiv u;
   if atom cdr u then
      ftintequiv cdr u
   else
      bfintequiv u;

put('!:rd!:,'intequivfn,'rdintequiv);

% complex mode . Is momentarliy superfluous ??
symbolic expr procedure complexp v;
('complex member getdec(car v))
 or
(!*complex and not(freeof(cdr v,'i)));

symbolic procedure myprepsq u;
   if null numr u then 0 else sqform(u,function myprepf);

symbolic procedure myprepf u;
   (if null x then 0 else replus x) where x=myprepf1(u,nil);

symbolic procedure myprepf1(u,v);
   if null u then nil
    else if domainp u then list retimes(u . exchk v)
    else nconc!*(myprepf1(lc u,if mvar u eq 'k!* then v
                                else lpow u . v),
                 myprepf1(red u,v));

symbolic procedure cireval u;
% (plus a (times b i)) -> (!:cr!: !:crn!: !:gi!:)
begin
  scalar ocmplx, res;
  ocmplx:=!*complex;!*complex:='t;
  res :=if freeof(u,'i)
           then u
           else myprepsq cadr aeval ireval u;
  !*complex:=ocmplx;
  return res;
  end$

symbolic procedure remcomplex u;
% (!:cr!: !:crn!: !:gi!:) -> (plus a (times b i))
if atom u
   then u
   else if member(car u,'(!:cr!: !:crn!: !:gi!:))
           then if eqcar(u,'!:gi!:)
                   then list('plus,cadr u,list('times,cddr u,'i))
                   else prepsq cr!:simp u
           else if not(constp u) % Could be other domain-notation.
                                 % JB 18/3/94.
                   then (car u)
                      . foreach el in cdr u collect remcomplex el
                   else u;
endmodule;
end;
