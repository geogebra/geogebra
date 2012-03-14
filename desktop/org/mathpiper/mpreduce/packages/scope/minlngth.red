module minlngth;

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


%----------------------------------------------------------------------
%  Minimum length provisions.
%  Date   : Feb. 1992
%  Provides user with operator setlength to indicate minimal length of
%  requested c.s.e.'s.
%----------------------------------------------------------------------

symbolic operator setlength,resetlength;
put('resetlength,'stat,'endstat)$

symbolic procedure setlength l;
%---------------------------------------------------------------
% l : integer evaluable expression.
% min!-expr!-length!* is set accordingly.
%---------------------------------------------------------------
if not fixp reval l
   then
    rederr("Please use integer values for minimum length setting!!")
   else min!-expr!-length!* := reval l;

symbolic procedure resetlength;
%---------------------------------------------------------------
% Resets min!-expr!-length!* to nil.
%---------------------------------------------------------------
if min!-expr!-length!*
   then << % write "Old value : ",min!-expr!-length!*;terpri();
           min!-expr!-length!* := nil;
        >> ;

symbolic procedure countsilent prf;
% -------------------------------------------------------------------
% Altered version of `countnop'.
% The number of +/-, unary -, *, integer ^, / and function applica-
% tions is counted in prf, consisting of a pair (lhs.rhs). Array
% references are seen as function applications if the array name is
% not contained in the symbol table.
% The result of the counts operation is the list totcts of the form :
%  ( #(+/-) #(-) #(*) #(^) #(/) #(other) )
%                                                 (# = number of.)
% -------------------------------------------------------------------
begin scalar totcts,res;
 totcts:='(0 0 0 0 0 0);
 totcts:=counts2(cdr prf,totcts,nil);
 res:=0;
 foreach el in totcts do res:=res + el;
 return res
end;

symbolic procedure counts2(expression,locs,root);
% -------------------------------------------------------------------
% Altered version of `counts'.
% The actual counts are recursively done with the function counts by
% modifying the value of the 6 elements of locs.  The elements of locs
% define the present number of the 6 possible categories of operators,
% which we distinguish.
% -------------------------------------------------------------------
begin scalar n!+,n!-,n!*,n!^,n!/,n!f,tlocs,loper,operands;
 if idp(expression) or constp(expression)
  then tlocs:=locs
  else
   << n!+:=car locs; n!-:=cadr locs; n!*:=caddr locs; n!^:=cadddr locs;
      n!/:=car cddddr locs; n!f:= car reverse locs;
      loper:=car expression; operands:=cdr expression;
      if loper memq '(plus difference)
       then n!+:=(length(operands)-1)+n!+
       else
        if loper eq 'minus
         then (if root neq 'plus then n!-:=1+n!-)
         else
          if loper eq 'times
           then n!*:=(length(operands)-1)+n!*
           else
            if loper eq 'expt
             then (if integerp(cadr operands)
                   then n!^:=1+n!^ else n!f:=min!-expr!-length!*)
             else
              if loper eq 'quotient
               then n!/:=1+n!/
               else
                if not(subscriptedvarp(loper))
                 then n!f:=min!-expr!-length!*;
      tlocs:=list(n!+,n!-,n!*,n!^,n!/,n!f);
      if not subscriptedvarp(loper)
         then foreach op in operands do tlocs:=counts2(op,tlocs,loper);
   >>;
 return(tlocs)
end;

symbolic smacro procedure protected(a,pn);
member((if atom a then a else car a), pn);

symbolic procedure make_min_length(prefixlist, protectednames);
% ---------------------------------------------------------------------
% This procedure modifies the prefixlist in a sense that either :
%    - righthandsides contain at least min!-expr!-length!* operations
%      at the first level.
%    - righthandsides define an output variable
%      (lhside member protectednames)
% ---------------------------------------------------------------------
begin
  scalar exp,lhs,rhs,npfl,dellst,ass;
  exp:=!*exp; !*exp:=nil;
  while prefixlist do
        <<ass := car prefixlist; prefixlist:=cdr prefixlist;
          if dellst
             then << lhs:=car ass; rhs:=replacein(cdr ass, dellst);
                     check_info(rhs); ass:=lhs.reval(rhs)
                  >>;
          if not protected(car ass, protectednames)
             and (countsilent(ass) < min!-expr!-length!*)
             then dellst := ass . dellst
             else npfl := ass . npfl;
         >>;
  !*exp:=exp;
  return reverse npfl;
  end;

symbolic procedure scope_switches2(choice);
% ------------------------------------------------------------------- ;
% If choice = t a list of all switches, given in the list switches,   ;
% which are on, is produced.                                          ;
% If choice = nil a complementary action is performed.                ;
% Hence both possible calls produce the union of all switches relevant;
% in the scope context.                                               ;
% ------------------------------------------------------------------- ;
begin scalar switches, twoblanks, eightblanks, prtlist, len, firstpart;
 switches:='(!*acinfo !*again !*double !*evallhseqp !*exp !*fort !*ftch
             !*gentranopt !*inputc !*intern !*nat !*period !*prefix
             !*priall !*primat !*roundbf !*rounded !*sidrel !*vectorc);
 twoblanks:='(!! !  !! !  );
 eightblanks:=append(
               append(
                append(twoblanks,twoblanks),
                twoblanks),
               twoblanks);
 foreach swtch in reverse(switches) do
  if choice=eval(swtch)
   then prtlist:=append(append(cddr explode swtch,twoblanks),prtlist);
 while (len:=length prtlist)>72 do
  << firstpart:=pnth(reverse prtlist, len-71);
     prtlist:=pnth(prtlist,73);
     while car(firstpart) neq car '(!!) do
      << firstpart:=car(prtlist).firstpart;
         prtlist:=cdr prtlist ;
      >>;
     prtlist:=car(firstpart).prtlist;        %
     firstpart:=reverse cdr firstpart;       % remove '!!
     while car(firstpart) member '(!! ! ) do firstpart:=cdr firstpart;
     write compress firstpart; terpri();
     write compress eightblanks;              % correct indentation
  >>;
 if prtlist then
 while car(prtlist) eq car '(!!) or
       car(prtlist) eq car '(! ) do prtlist:=cdr prtlist;
 if prtlist then write compress prtlist; terpri()
end;

symbolic procedure scope_ons;
<< write" ON  :  ";
   scope_switches2 't
>>;


symbolic procedure scope_offs;
<<write " OFF :  ";
  scope_switches2 'nil
>>;

symbolic procedure scope_switches;
begin
  terpri();
  scope_ons();
  scope_offs();
end;

symbolic operator scope_switches$
put('scope_switches,'stat,'endstat)$
symbolic operator scope_ons$
put('scope_ons,'stat,'endstat)$
symbolic operator scope_offs$
put('scope_offs,'stat,'endstat)$

endmodule;

end;
