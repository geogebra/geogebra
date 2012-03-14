module fac;  % Support "factor" as an operator.

% Author: Anthony C. Hearn.

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


global '(!*micro!-version);

symbolic procedure factor u;
   if !*micro!-version then factor0 u else factor1(u,t,'factors!*);

symbolic procedure factor0 u;
   begin scalar oldexp,v,w;
      if cdr u or kernp (v := simp!* car u)
        then <<lprim "Please use FAC instead";
               return factor1(u,t,'factors!*)>>;
      oldexp := !*exp;
      !*exp := t;
      if null oldexp then v := resimp v;
      w := !*fcfm2f fctrf numr v ./ !*fcfm2f fctrf denr v;
      if null oldexp then !*exp := oldexp;
%      if w = u or w = v then return u
%       else if null oldexp then return mk!*sq w
%       else return list('!*sq,w,nil)
      return mk!*sq w
    end;

flag('(factor),'intfn);

symbolic procedure !*fcfm2f u;
   % converts factored form u to standard form.
   multf(car u,!*fcfm2f1 cdr u);

symbolic procedure !*fcfm2f1 u;
   if null u then 1 else multpf(mksp(caar u,cdar u),!*fcfm2f1 cdr u);

symbolic procedure expandd u; reval u where !*exp = t;

flag('(expandd),'opfn);

flag('(expandd),'noval);

endmodule;

end;
