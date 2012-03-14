module grassman;

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


% fichier de manipulation des variables de Grassmann.
% RATIONAL functions involving Grasman variables inside the
% denominator NOT ALLOWED.
%
symbolic procedure putgrass u;
% Allows to define Grassmann  variables.
for each i in u do
  if not idp i then typerr(i,"grassman variable")
  else <<flag(list i,'noncom); put(i,'simpfn,'simpiden);
                     flag(list i, 'grassman);>> ;

rlistat '(putgrass remgrass);

symbolic procedure remgrass u;
% Erase the Grassman properties of the identifiers in  u.
for each i in u do
   if flagp(i,'grassman) then
    <<remflag(list i,'grassman);remflag(list i ,'noncom);
                                            clearop i;>>;

symbolic procedure grassp u;
not atom u and flagp(car u, 'grassman);

flag('(grassp),'boolean);

symbolic procedure grassparityini u;
% Not used anymore
if  grassp u then 1 else 0;

symbolic procedure grassparity u;
if atom u then 0 else
if flagp(car u,'grassman) then 1 else
if car u eq 'plus then "parity undefined" else
if car u eq 'minus then grassparity cadr u else
if car u eq 'times
  then remainder(for each i in cdr u sum grassparity i,2) else
if car u eq 'expt then if oddp caddr u then grassparity cadr u  else 0
else
if car u eq 'quotient then grassparity cadr u
else 0;

flag('(grassparity ghostfactor),'opfn);

symbolic procedure ghostfactor(u,v);
% (-1)^(grassparity u * grassparity v)
if reval list('times, grassparity u, grassparity v) = 0 then 1 else -1;

% *****************
% For the time being we let the explicit manipulation of
% Grassman variables as matching rules :
% here is an example of use of the previous functions :
% to try them erase the %
% putgrass eta,prond;

% grasskernel:=
% {(~x)*(~y) => y*x when grassp x and  not grassp y,
% prond(~x)*eta(~y) => -eta(y)*prond(x),
% eta(~x)*eta(~y) => -eta y*eta x when nordp(x,y),
% prond(~x)*prond(~y) => -prond y*prond x when nordp(x,y),
% (~x)*(~x) => 0 when grassp x};

% let grasskernel;
% ***********************

endmodule;

end;
