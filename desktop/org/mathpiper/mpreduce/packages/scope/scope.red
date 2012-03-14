module scope;  % Header module for SCOPE package.

% ------------------------------------------------------------------- ;
% Copyright : J.A. van Hulzen, Twente University, Dept. of Computer   ;
%             Science, P.O.Box 217, 7500 AE Enschede, the Netherlands.;
% Authors :   J.A. van Hulzen, B.J.A. Hulshof, W.N. Borst, M.C.       ;
%             van Heerwaarden, J.B. van Veelen.                       ;
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


create!-package('(scope codctl restore minlngth codmat codopt codad1
                  codad2 coddec codpri codgen codhrn codstr coddom),
                % ghorner
                '(contrib scope));

% Smacro definitions for access functions.

% ------------------------------------------------------------------- ;
% Access functions for the incidence matrix                           ;
% ------------------------------------------------------------------- ;

global '(codmat maxvar)$

define lenrow=8,lencol=4;
% ------------------------------------------------------------------- ;
% Length of the rows and the columns                                  ;
% ------------------------------------------------------------------- ;

symbolic smacro procedure row x$
   getv(codmat,maxvar+x)$

symbolic smacro procedure free x$
   getv(row x,0)$

symbolic smacro procedure wght x$
   getv(row x,1)$

symbolic smacro procedure awght x$
   caar(wght x)$

symbolic smacro procedure mwght x$
   cdar(wght x)$

symbolic smacro procedure hwght x$
   cdr(wght x)$

symbolic smacro procedure opval x$
   getv(row x,2)$

symbolic smacro procedure farvar x$
   getv(row x,3)$

symbolic smacro procedure zstrt x$
   getv(row x,4)$

symbolic smacro procedure chrow x$
   getv(row x,5)$

symbolic smacro procedure expcof x$
   getv(row x,6)$

symbolic smacro procedure hir x$
   getv(row x,7)$

symbolic smacro procedure phir x$
   car(hir x)$

symbolic smacro procedure nhir x$
   cdr(hir x)$

% ------------------------------------------------------------------- ;
% Assignments in the incidence matrix                                 ;
% ------------------------------------------------------------------- ;

symbolic smacro procedure fillrow(x,v)$
   putv(codmat,maxvar+x,v)$

symbolic smacro procedure setoccup x$
   putv(row x,0,nil)$

symbolic smacro procedure setfree x$
   putv(row x,0,t)$

symbolic smacro procedure setwght(x,v)$
   putv(row x,1,v)$


symbolic smacro procedure setopval(x,v)$
   putv(row x,2,v)$

symbolic smacro procedure setfarvar(x,v)$
   putv(row x,3,v)$

symbolic smacro procedure setzstrt(x,v)$
   putv(row x,4,v)$

symbolic smacro procedure setchrow(x,v)$
   putv(row x,5,v)$

symbolic smacro procedure setexpcof(x,v)$
   putv(row x,6,v)$

symbolic smacro procedure sethir(x,v)$
   putv(row x,7,v)$

symbolic smacro procedure setphir(x,v)$
   rplaca(hir x,v)$

symbolic smacro procedure setnhir(x,v)$
   rplacd(hir x,v)$

% ------------------------------------------------------------------- ;
% Access functions for Z elements                                     ;
% ------------------------------------------------------------------- ;

symbolic smacro procedure xind z$
   car z$

symbolic smacro procedure yind z$
   car z$

symbolic smacro procedure val z$
   cdr z$

symbolic smacro procedure ival z$
   car val z$

symbolic smacro procedure bval z$
   cdr val z$

% ------------------------------------------------------------------- ;
% Assignment functions for Z elements                                 ;
% ------------------------------------------------------------------- ;

symbolic smacro procedure setival(z,v)$
   rplaca(val z,v)$

symbolic smacro procedure setbval(z,v)$
   rplacd(val z,v)$

symbolic smacro procedure mkzel(n,iv);
   if idp(iv) or constp(iv) then n.(iv.nil) else n.iv$
    % --------------------------------------------------------------- ;
    % Distinguish between atom and non atom for IVAL and BVAL.        ;
    % --------------------------------------------------------------- ;

% ------------------------------------------------------------------- ;
% Access functions for ordening subexpressions                        ;
% ------------------------------------------------------------------- ;

symbolic smacro procedure ordr x$
   getv(row x,8)$

symbolic smacro procedure setordr(x,l)$
   putv(row x,8,l)$

% ------------------------------------------------------------------- ;
% Access functions for Histogram                                      ;
% ------------------------------------------------------------------- ;

global '(codhisto)$

codhisto:=nil;

define histolen=200$

symbolic smacro procedure histo x$
   getv(codhisto,x)$

symbolic smacro procedure sethisto(x,v)$
   putv(codhisto,x,v)$

endmodule;

end$
