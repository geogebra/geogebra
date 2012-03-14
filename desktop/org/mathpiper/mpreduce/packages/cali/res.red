module res;

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


COMMENT

          ######################
          ###                ###
          ###   RESOLUTIONS  ###
          ###                ###
          ######################

This module contains algorithms on complexes, i.e. chains of modules
(submodules of free modules represented as im f of certain dpmat's).

A chain (in particular a resolution) is a list of dpmat's with the
usual annihilation property of subsequent dpmat's.

This module contains

        - An algorithm to compute a minimal resolution of a dpmat,

        - the same for a local dpmat.

        - the extraction of the (graded) Betti numbers from a
                resolution.

This module is just under development.

END COMMENT;

% ------------- Minimal resolutions --------------

symbolic procedure Resolve!*(m,d);
% Compute a minimal resolution of the dpmat m, i.e. a list of dpmat's
% (s0 s1 s2 ...), where sk is the k-th syzygy module of m, upto the
% d'th part.
  (begin scalar a,u;
  if dpmat_cols m=0 then
    << cali!=degrees:=nil; m:=ideal2mat!* m>>
  else cali!=degrees:=dpmat_coldegs m;
  a:=list(m); u:=syzygies!* m;
  while (not dpmat_zero!? u)and(d>1) do
    << m:=u; u:=syzygies!* m; d:=d-1;
       u:=groeb_minimize(m,u); m:=car u; u:=cdr u; a:=m . a;
    >>;
  return reversip (u.a);
  end) where cali!=degrees:=cali!=degrees;

% ----------------- The Betti numbers -------------

symbolic procedure bettiNumbers!* c;
% Returns the list of Betti numbers of the chain c.
   for each x in c collect dpmat_cols x;

symbolic procedure gradedBettiNumbers!* c;
% Returns the list of degree lists (according to the ecart) of the
% generators of the chain c.
  for each x in c collect
     begin scalar i,d; d:=dpmat_coldegs x;
        return
    if d then sort(for each y in d collect mo_ecart cdr y,'leq)
        else for i:=1:dpmat_cols x collect 0;
     end;

endmodule; % res

end;
