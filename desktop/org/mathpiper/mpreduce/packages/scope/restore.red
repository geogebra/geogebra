module restore;

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


fluid '(prefixlist);

global '(!*vectorc malst optlang!*);

symbolic procedure vectorcode list_of_names;
% ------------------------------------------------------------------- ;
% All names are assigned the flag subscripted
% ------------------------------------------------------------------- ;
<< %!*vectorc:='t;                   % should NOT be set. JB 15/3/94
   flag(list_of_names,'subscripted);
   flag(list_of_names,'vectorvar); >>$

put('vectorcode,'stat,'rlis)$
symbolic operator vectorcode$

symbolic procedure vclear list_of_names;
% ------------------------------------------------------------------- ;
% All names are assigned the flag subscripted.
% ------------------------------------------------------------------- ;
<< remflag(list_of_names,'subscripted);
   remflag(list_of_names,'vectorvar); >>$

put('vclear,'stat,'rlis)$
symbolic operator vclear$

symbolic procedure vectorvarp u;
(!*vectorc and subscriptedvarp(u))
or
flagp(u, 'vectorvar);

%global '(!*vectorc)$ switch vectorc$ !*vectorc:='nil$

symbolic procedure optlang u;
if not member(car u, '(nil c fortran f90 pascal ratfor))
   then if eq(car(u), 'fortran90)
           then optlang!* := 'f90
           else rederr("No such targetlanguage available !!!")
   else optlang!* := car u$

put('optlang,'stat,'rlis);

global '(avarlst)$ malst:=avarlst:='nil$

symbolic procedure algresults;
algresults1 prefixlist;

symbolic procedure algresults1 prefixlist;
%-------------------------------------------------------------------- ;
% The algebraic mode facility aresults is used to produce an alg. mode;
% list, presenting the result of a previous optimize-run. All possibly;
% existing algebraic values, of both lhs and rhs variables in the     ;
% listed eq's are stored with the indicator-name a2value,             ;
% simply to avoid untimely backsubstitutions.                         ;
% The algebraic variables, having an avalue are collectedin the list  ;
% avarlst. This list is mainly produced with the procedure check_info.;
% ------------------------------------------------------------------- ;
begin
  scalar results;
  foreach item in prefixlist do
   << check_info car item;
      check_info cdr item;
      results:=list('equal,car item, reval cdr item).results;
   >>;
 if malst then foreach el in malst do put(car el,'simpfn,'simpiden);
 return append(list('list),reverse results)
end;

symbolic operator algresults$
algebraic operator aresults;
algebraic(let aresults=algresults());

symbolic procedure check_info info;
% ------------------------------------------------------------------- ;
% The list info is searched for algebraic variables having an avalue. ;
% This value is saved as value of the indicator a2value, before the   ;
% avalue itself is removed. The variable name is stored in the list   ;
% avarlst.                                                            ;
% ------------------------------------------------------------------- ;
begin scalar aval;
 if pairp(info)
  then
   if constp(info) % Could be some float...
     then info
     else foreach item in info do check_info item
  else
   if idp(info) and not(memq(info,avarlst)) and
      (aval:=get(info,'avalue))
    then << put(info,'a2value,aval);
            remprop(info,'avalue);
            avarlst:=info.avarlst;
            if member(get(info,'rtype),'(array matrix))
               then <<malst := cons(info, get(info,'rtype)) . malst;
                      remprop(info,'rtype)
                    >>
         >>;
end;
symbolic expr procedure arestore(list_of_names);
% ------------------------------------------------------------------- ;
% All names in the list_of_names get their avalue back.
% Their names are removed from the avarlst.
% ------------------------------------------------------------------- ;
 foreach name in list_of_names do
        << put(name,'avalue,get(name,'a2value));
           remprop(name,'a2value);
           avarlst:=delete(name,avarlst);
           if assoc(name,malst)
              then <<put(name,'rtype,cdr assoc(name,malst));
                     remprop(name,'klist);
                     remprop(name,'simpfn);
                     malst:=delete(assoc(name,malst),malst)
                   >>
        >>;
put('arestore,'stat,'rlis)$
symbolic operator arestore$

symbolic procedure restoreall;
% ------------------------------------------------------------------- ;
% All names in the list avarlst get their avalue back.
% Then avarlst is set to nil again.
% ------------------------------------------------------------------- ;
arestore avarlst;


remprop('restoreall,'stat)$   % So next line parses properly.
symbolic operator restoreall$
put('restoreall,'stat,'endstat)$

symbolic expr procedure ireval ex;
%----------------------------------------------------------------------
% `Symbolic-reval'; all variables known to the system by their avalue,
% are hidden by check_info.
% This prevents expressions like x + 1  and 2x + 1  to evaluate to 1
% when x has the avalue 0.
% After this `reval' is applied to obtain a canonical representation of
% ex.
%----------------------------------------------------------------------
begin
  check_info ex;
  if atom ex
     then return ex
     else return (car ex . foreach el in cdr ex collect reval el);
  end;


symbolic procedure ids_to_restore;
% ---------------------------------------------------------------------
% The present value of the fluid variable avarlst is printed.
% ---------------------------------------------------------------------
append(list('list),avarlst)$

symbolic operator ids_to_restore$
algebraic operator restorables$
algebraic(let restorables=ids_to_restore())$

endmodule;

end;
