module reord; % Functions for reordering standard forms.

% Author: Anthony C. Hearn.

% Copyright (c) 1990 The RAND Corporation.  All rights reserved.

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


fluid '(alglist!* kord!* ncmp!*);

alglist!* := nil . nil;         % This is first module that uses this.

symbolic procedure reordsq u;
   % Reorders a standard quotient so that current kernel order is used.
   reorder numr u ./ reorder denr u;

symbolic procedure reorder u;
   % Reorders a standard form so that current kernel order is used.
   % Note: this version does not reorder any sfs used as kernels.
   if domainp u then u
    else raddf(rmultpf(lpow u,reorder lc u),reorder red u);

symbolic procedure raddf(u,v);
   % Adds reordered forms U and V.
   if null u then v
    else if null v then u
    else if domainp u then addd(u,v)
    else if domainp v then addd(v,u)
    else if peq(lpow u,lpow v)
     then (lpow u .* raddf(lc u,lc v)) .+ raddf(red u,red v)
    else if ordpp(lpow u,lpow v) then lt u . raddf(red u,v)
    else lt v . raddf(u,red v);

symbolic procedure rmultpf(u,v);
  % Multiplies power U by reordered form V.
   if null v then nil
    else if domainp v or reordop(car u,mvar v) then !*t2f(u .* v)
    else (lpow v .* rmultpf(u,lc v)) .+ rmultpf(u,red v);

symbolic procedure reordop(u,v);
   (!*ncmp and noncomp1 u and noncomp1 v) or ordop(u,v);

symbolic procedure kernel!-list u;
  % Converts u to a list of kernels, expanding lists in u.
  for each x in u join
   <<x:=reval x;
     if eqcar(x,'list) then kernel!-list cdr x else {!*a2k x}>>;

symbolic procedure korder u;
   <<kord!* := if u = '(nil) then nil else kernel!-list u;
     rmsubs()>>;

rlistat '(korder);

symbolic procedure setkorder u;
   begin scalar v;
      v := kord!*;
      if u=v then return v;
      kord!* := u;
      alglist!* := nil . nil;        % Since kernel order has changed.
      return v
   end;

symbolic procedure updkorder u;
   % U is a kernel.  Value is previous kernel order.
   % This function is used when it is necessary to give one kernel
   % highest precedence (e.g., when extracting coefficients), but not
   % change the order of the other kernels.
   begin scalar v,w;
      v := kord!*;
      w := u . delete(u,v);
      if v=w then return v;
      kord!* := w;
      alglist!* := nil . nil;        % Since kernel order has changed.
      return v
   end;

endmodule;

end;
