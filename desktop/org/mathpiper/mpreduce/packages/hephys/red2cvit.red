module red2cvit;

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


% COPYRIGHT (C) 1988,1990,INSTITUTE OF NUCLEAR PHYSICS,MOSCOW STATE
%                          UNIV.
% PURPOSE   INTERFACE BETWEEN REDUCE AND CVITANOVICH ALGORITHM.
% AUTHOR    A.KRYUKOV
% VERSION   2.1
% RELEASE   11-MAR-90

  exports isimp1,replace_by_vector,replace_by_vectorp,gamma5p$
  imports calc_spur,isimp2$

  switch cvit$               % CVITANOVICH ALGORITHM SWITCH
  !*cvit := t$               % DEFAULT ON

  %************ ISIMP1 REDEFINITION ************************

  remflag('(isimp1),'lose)$

  symbolic procedure isimp1(u,i,v,w,x)$
     if null u then nil
      else if domainp u
         then if x then multd(u,if !*cvit
                                  then calc_spurx (i,v,w,x)
                                  else spur0 (car x,i,v,w,cdr x)
                             )
               else if v then multd(u,index_simp (1,i,v,w))
               else if w then multfs(emult w,isimp1(u,i,v,nil,nil))
               else u
      else addfs(isimp2(car u,i,v,w,x),isimp1(cdr u,i,v,w,x))$

  flag('(isimp1),'lose)$

  %************* INDEX_SIMP *******************************

  symbolic procedure index_simp (u,i,v,w)$
     if v then index_simp (multf(mksprod(caar v,cdar v),u),
                           update_index (i,car v),cdr v,w)
       else isimp1(u,i,nil,w,nil)$

  symbolic procedure mksprod(x,y)$
     mkdot(if indexp x then replace_by_vector x else x,
           if indexp y then replace_by_vector y else y)$

  symbolic procedure update_index (i,v)$
     % I  - LIST OF UNMATCH INDICES
     % V  - PAIR: (I/V . I/V)
     % VALUE - UPDATE LIST OF INDICES
     delete(cdr v,delete(car v,i))$

  %************ CALC_SPURX - MAIN PROCEDURE ***************

  symbolic procedure calc_spurx (i,v,w,x)$
     % I - LIST OF INDICES
     % V - LIST OF SCALAR PRODUCT:(<I/V> . <I/V>)
     % W - EPS-EXPR
     % X - LIST OF SPURS
     % VALUE - CALCULATED SPUR(S.F.)
     begin scalar u,         % SPUR: (LNAME G5SWITCH I/V I/V ... )
                  x1,        % (UN ... U1)
                  dindices!*,% A-LIST OF DUMMY INDICES: (I . NIL/T)
                  c$          % COEFFICIENT GENERATIED BY GX*GX
           if numberp ndims!* and null evenp ndims!*
             then cviterr list('calc_spur,":",ndims!*,
                     "is not even dimension of G-matrix space")$
           c := 1$            % INITIAL VALUE
           while x
             do << if nospurp caar x
                     then cviterr list "Nospur not yet implemented"$
                   u := cdar x$
                   x := cdr x$
                   if car u
                     then if evenp ndims!*
                            then u := next_gamma5() . reverse cdr u
                            else cviterr
                                   {"G5 invalid for non even dimension"}
                     else u := reverse cdr u$
                   if null u then nil                 % SP()
                     else if null evenp
                        length(if gamma5p car u and cdr u then cdr u
                                else u)
                            then x := c := nil        % ODD - VALUE=0
                     else << u := remove_gx!*gx u$
                             c := multf(car u,c)$
                             u := replace_vector(cdr u,i,v,w)$
                             i := cadr u$
                             v := caddr u$
                             w := cadddr u$
                             if u then x1 := car u . x1
                          >>
                >>$
           x1 := if null c then nil ./ 1         % ZERO
                   else if x1 then multsq(c ./ 1,calc_spur x1)
                   else c ./ 1$
           if denr x1 neq 1 then cviterr list('calc_spurx,":",x1,
                                    "has non unit denominator")$
           clear_windices ()$
           clear_gamma5 ()$
           return isimp1(numr x1,i,v,w,nil)
     end$

  symbolic procedure third_eq_indexp i$
     begin scalar z$
           if null(z := assoc(i,dindices!*))
             then dindices!* := (i . nil) . dindices!*
             else if null cdr z
                    then dindices!* := (i . t) . delete(z,dindices!*)$
           return if z then cdr z else nil
     end$

  symbolic procedure replace_vector(u,i,v,w)$
     % U  - SPUR (INVERSE)
     % I  - LIST OF UNMATCH INDICES
     % V  - A-LIST OF SCALAR PRODUCT
     % W  - EPS-EXPRESION
     % VALUE - LIST(U,UPDATE I,UPDATE V,UPDATE W)
     begin scalar z,y,x,    % WORK VARIABLES
                  u1$        % SPUR WITHOUT VECTOR
           while u
             do << z := car u$
                   u := cdr u$
                   if indexp z
                     then << % REMOVE DUMMY INDICES
                             while (y := bassoc(z,v))
                               do << i := delete(z,i)$
                                     v := delete(y,v)$
%                                    W := ....
                                     x := if z eq car y then cdr y
                                            else car y$
                                     if indexp x then z := x
                                       else if gamma5p x
                                         then cviterr
                                            list "G5 bad structure"
                                       else replace_by_index (x,z)
                                  >>$
                             u1 := z . u1
                          >>
                     else if gamma5p z then u1 := z . u1
                     else << z := replace_by_index (z,next_windex())$
                             u1 := z . u1
                          >>
                >>$
           return list(reverse u1,i,v,w)
     end$

  symbolic procedure replace_by_index (v,y)$
     begin scalar z$
           if (z := replace_by_vectorp y) eq v
             then cviterr list('replace_by_index,":",y,
                              "is already defined for vector",z)$
           put(y,'replace_by_vector ,v)$
           return y
     end$

  symbolic procedure remove_gx!*gx u$
     begin scalar x,c$
           integer l,l1$
           c := 1$
           l1 := l := length u$
           u := for each z in u                  % MAKE COPY
                  collect << if indexp z then
                               if third_eq_indexp z
                                 then cviterr
                                       list("Three indices have name",z)
                                 else nil
                               else if null hvectorp z then
                                      if cvitdeclp(z,'vector)
                                         then vector1 list z
                                         else cviterr nil
                               else nil$
                             z
                          >>$
           if l < 2 then return u$
           x := u$
           while cdr x do x := cdr x$
           rplacd(x,u)$                          % MAKE CYCLE
           while l1 > 0
             do if car u eq cadr u               % EQUAL ?
                  then << c := multf(if indexp car u then ndims!*
                                       else mkdot(car u,car u)
                                    ,c)$
                          rplaca(u,caddr u)$    % YES - DELETE
                          rplacd(u,cdddr u)$
                          l1 := l := l - 2
                       >>
                  else << u := cdr u$            % NO - CHECK NEXT PAIR
                          l1 := l1 - 1
                       >>$
           x := cdr u$
           rplacd(u,nil)$                        % CUT CYCLE
           return (c . if cdr x and car x eq cadr x then nil else x)
     end$

  %************* ERROR,MESSAGE *****************************

  symbolic procedure cviterr u$
     << clear_windices()$
        clear_gamma5()$
        if u then rederr u else error(0,nil) >>$

  symbolic procedure cvitdeclp(u,v)$
     if null !*msg then nil
       else if terminalp()
              then yesp list("Declare",u,v,"?")
       else << lprim list(u,"Declare",v)$ t >>$

  %*********** WORK INDICES & VECTOR ***********************

  symbolic procedure clear_windices ()$
     while car windices!*
       do begin scalar z$
             z := caar windices!*$
             windices!* := cdar windices!* . z . cdr windices!*$
             remprop(z,'replace_by_vector)$
             indices!* := delete(z,indices!*)$
          end$

  symbolic procedure next_windex()$
     begin scalar i$
           windices!* := if null cdr windices!*
                           then (intern gensym() . car windices!*) .
                                cdr windices!*
                           else (cadr windices!* . car windices!*) .
                                cddr windices!*$
           i := caar windices!*$
           vector1 list i$
           indices!* := i . indices!*$
           return i
     end$

  symbolic procedure next_gamma5()$
     begin scalar v$
           cviterr list "GAMMA5 is not yet implemented. use OFF CVIT";
           gamma5!* := if null cdr gamma5!*
                           then (intern gensym() . car gamma5!*) .
                                cdr gamma5!*
                           else (cadr gamma5!* . car gamma5!*) .
                                cddr gamma5!*$
           v := list caar gamma5!*$
           vector1 v$
           return car v
     end$

  %************ END ****************************************

%prin2t "_Cvitanovich_algorithm_is_ready"$

endmodule;

end;
