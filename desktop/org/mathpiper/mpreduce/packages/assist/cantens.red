module cantens; % header module tested for REDUCE 3.6 and 3.7.

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


create!-package('(cantens ctintro auxitens gentens spaces
                   partitns checkind opertens contrtns),
                '(contrib cantens));


% This package requires ASSIST and DUMMY.
%
% ************************************************************************
%
%     Authors: H. Caprasse <hubert.caprasse@ulg.ac.be>
%            : F. Fontaine <pascal.fontaine@ulg.ac.be>
%
% Version and Date:  Version 1.11, 15 January 1999.
%
% November 2008 - changed by H Caprasse to use the BSD license as above.
%
% Revision history to versions 1.0 and 1.1:
% 15/12/98 : Flag 'LOOSE' removed on DEPENDS in order to
%          :  allow its redefinition in CSL.
%          : SIMPTENSOR, NUM_EPSI_NON_EUCLID, MATCH_KVALUE and
%          : SIMPMETRIC  modified.
%          : MAKE_PARTIC_TENS no longer protected by the 'reserved'
%          : flag.
%          : Modifications to SYMTREE_ZEROP and DV_SKEL2FACTOR1
%          : to allow proper compilation under CSL.
%% ******************************************************************
%
% an extension of the REDUCE command 'depend':
% patch to extend depend to tensors...

  remflag('(depends),'loose); % because of csl

symbolic procedure depends(u,v);
   if null u or numberp u or numberp v then nil
    else if u=v then u
    else if atom u and u memq frlis!* then t
      %to allow the most general pattern matching to occur;
    else if (lambda x; x and ldepends(cdr x,v)) assoc(u,depl!*)
     then t
    else if not atom u and idp car u and get(car u,'dname) then
        (if depends!-fn then apply2(depends!-fn,u,v) else nil)
           where (depends!-fn = get(car u,'domain!-depends!-fn))
    else if not atom u
      and (ldepends(cdr u,v) or depends(car u,v)) then t
    else if atom v or idp car v and get(car v,'dname) then nil
    % else dependsl(u,cdr v);
    else if flagp(u,'tensor) and pairp v and u=car v then t
    else nil;

% an "importation" from EXCALC:

symbolic procedure permp!:(u,v);
% True if v is an even permutation of u NIl otherwise.
    if null u then t else if car u = car v then permp!:(cdr u,cdr v)
     else not permp!:(cdr u,subst(car v,car u,cdr v));

% global and fluid variables defined.

lisp remflag(list 'minus,'intfn);

global '(dimex!* sgn!*  signat!* spaces!* numindxl!* pair_id_num!*) ;


lisp (pair_id_num!*:= '((!0 . 0) (!1 . 1) (!2 . 2) (!3 . 3) (!4 . 4)
                        (!5 . 5) (!6 . 6) (!7 . 7) (!8 . 8) (!9 . 9)
                        (!10 . 10) (!11 . 11) (!12 . 12) (!13 . 13)));

fluid('(dummy_id!* g_dvnames epsilon!*));

% g_dvnames is a vector.


switch onespace;

!*onespace:=t;  % working inside a unique space is the default.

%  Various smacros

smacro procedure id_cov u;
   % to get the covariant identifier
   % u is the output of get_n_index
   cadr u;

smacro procedure id_cont u;
   % to get the contravariant identifier
   % u is the output of get_n_index
   u;

smacro procedure careq_tilde u;
   eqcar(u,'!~);

smacro procedure careq_minus u;
   eqcar(u,'minus);

smacro procedure lowerind u;
   list('minus,u);

smacro procedure raiseind u;
   cadr u;

smacro procedure id_switch_variance u;
 if eqcar(u,'minus) then cadr u
                    else list ('minus, u);


smacro procedure get!-impfun!-args u;
   % Get dependencies of id u.
   cdr assoc(u,depl!*);

endmodule;
end;
