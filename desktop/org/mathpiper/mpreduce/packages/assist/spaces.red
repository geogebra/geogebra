module spaces; % definition and general properties
                     % of spaces.

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


lisp remflag(list 'minus,'intfn);

global '(dimex!* sgn!*  signat!* spaces!* numindxl!* pair_id_num!*) ;


lisp (pair_id_num!*:= '((!0 . 0) (!1 . 1) (!2 . 2) (!3 . 3) (!4 . 4)
                        (!5 . 5) (!6 . 6) (!7 . 7) (!8 . 8) (!9 . 9)
                        (!10 . 10) (!11 . 11) (!12 . 12) (!13 . 13)));

fluid('(dummy_id!* g_dvnames epsilon!*));

% g_dvnames is a vector.


switch onespace;

!*onespace:=t;  % working inside a unique space is the default.

fluid('(indxl_tens!* dummy_id!* g_dvnames)); % g_dvnames is a vector.

% dimex!* = global space dimension. Standard form.
% sgn!* = Choice of "global sign". Equals 1 or -1.
%         1 for high energy physicists, -1 for astrophysicists.
% !*onespace = when OFF allows to introduce a space
%              which is the direct product of two  or more spaces.
% numindxl!* := nil initially. Contains all indexranges: ((sp min max) ..)

dimex!*:= !*k2f 'dim;

sgn!* := 1; % Global sign: determine the convention (+---) ou (-+++)
                  % High energy physicists convention is chosen by default.

signat!* :=0; % number of time-like coordinates.

fluid '(alglist!*);

smacro procedure get_prop_space u;
% To get properties of a given space (subspace).
 subla(spaces!*,u);

symbolic procedure charnump!: x;
 if x memq
  list('!0,'!1,'!2,'!3,'!4,'!5,'!6,'!7,'!8,'!9,'!10,'!11,'!12,'!13)
    then t ;


symbolic procedure get_dim_space u;
 if null u then nil
    else
 (if not atom x then car x)where x=subla(spaces!*,u);

symbolic procedure get_sign_space u;
% To get the signature of a given space (subspace).
% result is nil if space is 'affine'
  if null u then nil else
  (if atom cadr x and null cddr x then
       if cadr x eq 'euclidian then 0
         else nil
    else caddr x)where x=subla(spaces!*,u);

symbolic procedure affinep u;
% u is a tensor kernel
% returns T if the the tensor belongs to an affine space.
(if x then null get_sign_space x)where x=get(car u,'belong_to_space);

symbolic procedure get_indexrange_space u;
% To get the signature of a given space (subspace).
   if null spaces!* then nil
    else
   (if x then
       if not atom x and cddr x and cdddr x then cadddr x
        else
       if cddr x and not atom caddr x then caddr x)
                           where x=if spaces!* then subla(spaces!*,u);


symbolic procedure onespace u;
% Defined specifically for the user. tells if
% one or several spaces are active.
% By default, a UNIQUE space is supposed.
 if  u eq '? then
       if !*onespace then symb_to_alg 'YES else symb_to_alg 'NO
       else nil;


symbolic procedure wholespace_dim u;
% if u is ? gives the space-dimension. else sets the space-dim.
 begin
  if  u eq '? then  return
           prepsq!* !*f2q dimex!*
    else
  if null get('wholespace,'spacedef) then
   <<dimex!* :=  !*q2f simp u ;
      return prepsq!* !*f2q dimex!*>>;
 end;

symbolic procedure global_sign u;
% if u is ? gives the global sign else sets it.
 begin
   if  u eq '? then  return sgn!*
    else return
   sgn!* := u
 end;

symbolic procedure signature u;
% if u is ? gives the number of time-like coordinates else sets it.
 if  u eq '? then signat!*
  else
 if !*onespace and fixp u then signat!*:=u
  else "non-active in OFF ONESPACE";


flag({'onespace,'show_spaces,'wholespace_dim ,
                    'global_sign ,'signature},'opfn);

% The notion of indexrange for numeric indices is now implemented:

% taken from INEQ

newtok '( (!. !.) !*interval!*);

% first, introduction of interval through the command a .. b

if null get('!*interval!*,'simpfn) then
<<precedence .., or;
  algebraic operator ..;
 put('!*interval!*,'prtch,'! !.!.! );
>>;

symbolic procedure mkinterval(u,v);
% u et v sont des entiers
% utility function not yet used for the algebraic mode
 symb_to_alg list('!*interval!*,u,v);

symbolic procedure lst_belong_interval(lst,int);
if null lst then t
  else
if idx_belong_interval(car lst,int) then lst_belong_interval(cdr lst,int)
 else nil;

symbolic procedure idx_belong_interval(idx,int);
% t if numeric index  'idx' belongs to the interval 'int'.
 if null int or atom int then t
  else idx geq car int and idx leq cadr int;

symbolic procedure numids2_belong_same_space(i1,i2,tens);
% basic function to determine if two numeric indices
% belong or not to the same space. Boolean.
% tens is the name of the tensor
  (if x and y then
       begin scalar ind,sp;
          if null numindxl!* then return t;
          ind:=if (sp:=get(tens,'belong_to_space)) then
                   list subla(numindxl!*,sp)
                else  for each x in numindxl!* collect cdr x;
       loop:  if null ind then return nil
               else
              if idx_belong_interval(x,car ind)
                and idx_belong_interval(y,car ind)
                                            then  return t
               else ind:=cdr ind;
               go to loop;
        end)where x=!*id2num i1,y=!*id2num i2;

symbolic procedure num_ids_belong_same_space(u,tens);
% u is a list of numeric indices
% tens is the name of a tensor
<< if oddp length u then u:= car u . u;
  while u and numids2_belong_same_space(car u,cadr u,tens)
  do u:=cddr u; if null u then t else nil>>;

symbolic procedure symb_ids_belong_same_space(u,v);
% u is a list of indices.
% nil is the current starting value for v but may be the
% name of one space. In that case, it verifies that all indices
% in u belong to the v space.
 if null u  or v = 'wholespace then t
  else
 if null get(car u,'space) or get(car u,'space) = v
       then symb_ids_belong_same_space(cdr u,v)
  else
 if null v then symb_ids_belong_same_space(cdr u,get(car u,'space))
  else
 if get(car u,'space) neq v then nil;

symbolic procedure symb_ids_belong_same_space!:(u,v);
% This is a variant of the previous procedure.
% needed for DEL-like tensors when working in OFF onespace
% u is a list of indices.
% nil is the current starting value for v but may be the
% name of one space. In that case, it verifies that all indices
% in u belong to the v space.
 if null u  then t
% v = 'wholespace then t NOT VALID in general since some indices
% may have a restricted range while BELONGING to a
% WELL DEFINED space. Should most probably replace it.
  else
 if null get(car u,'space) or get(car u,'space) = v
       then symb_ids_belong_same_space!:(cdr u,v)
  else
 if null v then symb_ids_belong_same_space!:(cdr u,get(car u,'space))
  else
 if get(car u,'space) neq v then nil;

symbolic procedure ind_same_space_tens(u,tens);
% u are the indices of tens.
% verify that they belong to the same space
% !!! if some indices belong to no space or to the
% wholespace it does not take them into account.
 begin scalar lst,lstnum;
 lst := clean_numid u;
 lstnum:=extract_num_id u;
 return
 if num_ids_belong_same_space(lstnum,tens) and
     symb_ids_belong_same_space(lst,get(tens,'belong_to_space))
   then t
  else nil;
 end;

rlistat ('(define_spaces rem_spaces));

symbolic procedure define_spaces u;
% Define subspaces by the commands:
% define_spaces s={ds,affine}
% or
% define_spaces s={ds,euclidean}
% or
% define_spaces s={ds,signature=<number>,indexrange=a .. b}
  if !*onespace then nil
   else
  if not fixp sgn!* then rederr "set the global sign please" else
   begin scalar sp;rmsubs();
     for each j in u do
       if not eqexpr j then errpri2(j,'hold)
        else
       if get(sp:=cadr j,'spacedef) or
                    flagp(sp,'reserved) or getrtype sp or gettype sp
                 then
                lpri{"*** Warning:",sp,
          " cannot be (or is already) defined as space identifier"}

        else <<(put(sp,'spacedef,
                   if eqexpr caddr y then sp . cadr y . whole_space(sp,y)
                   else sp . whole_euclid_space(sp,y)))where y=caddr j;
     spaces!*:=if null assoc(sp,spaces!*) then
                          union(list get(sp,'spacedef),spaces!*);
     numindxl!* := if space_index_range sp then
                    union( list (sp . space_index_range sp),numindxl!*);>>;
     return t
   end;


symbolic procedure whole_euclid_space(sp,u);
% u is the y of define_spaces
% {ds,euclidean,indexrange=a .. b}
 (if sp eq 'wholespace then
  <<dimex!*:=!*k2f car w; signat!*:=0; w>> else w)where w=cdr u;


symbolic procedure whole_space(sp, u);
% u is y of define_spaces
% {ds,signature=<number>,indexrange=a .. b}
 (if sp eq 'wholespace then
   <<dimex!*:=!*k2f car w; signat!*:=caddr cadr w;
      if cddr w then cadadr w . cadr cdadr w . list caddr w
        else cdadr w
   >>
   else
  if cddr w then cadadr w . cadr cdadr w . list caddr w
   else cdadr w )where w=cdr u;

%symbolic procedure whole_space(sp, u);
% In case of emergency, I keep it!
% u is y of define_spaces
% {ds,signature=<number>,indexrange=a .. b}
% (if sp eq 'wholespace then
%  <<dimex!*:=!*k2f car w; signat!*:=caddr cadr w;cdadr w>>
%   else
%   if cddr w then cadadr w . cadr cdadr w . list caddr w
%   else cdadr w )where w=cdr u;


symbolic procedure space_index_range u;
% u is the name of a given space
% result is
 begin scalar x;
  x:=get_indexrange_space u;
 return
  if null x then nil
   else bubblesort1( caddr cadr x . caddr x . nil)
 end;

symbolic procedure rem_spaces u;
 <<for each j in u do
    <<remprop(j,'spacedef);
         spaces!*:=delete(assoc(j, spaces!*),spaces!*);
         numindxl!*:=delete(assoc(j,numindxl!*),numindxl!*);
      remflag(list j,'reserved);
      if j eq 'wholespace then
             <<dimex!*:=!*k2f 'dim; signat!*:=0;>>
     >>;
 t>>;

symbolic procedure mkequal u;
% u is an element of spaces!*
{'equal,'signature,cadr u};

symbolic procedure insert_sign_equal u;
% u is an element of spaces!*
 begin scalar l;
   loop: if null u then return reverse l ;
        if car u neq 'signature then <<l:=car u . l; u:=cdr u>>
          else <<l:=mkequal u . l; u:=cddr u>>;
        go to loop;
  end;

symbolic procedure show_spaces();
% Gives the properties of already defined spaces
 begin scalar x;
    x:=for each i in spaces!* collect insert_sign_equal i;
    x:=for each y in x collect 'list .
             for each z in y collect if pairp z then z else mk!*sq !*k2q z;
  return 'list .  reverse x
 end;


 flag(list 'mk_ids_belong_space,'opfn);

symbolic procedure mk_ids_belong_space(u,v);
% u is a list of identifiers which are indices
% v is the name of an already defined (sub)space
% Make  all indices belong to v.
% Works ONLY when the swith onespace is OFF.
 if !*onespace then nil
  else
 if idp u then  <<put(u,'space,v),t>>
  else <<for each x in u do put(x,'space,v),t>>;

rlistat('(mk_ids_belong_anyspace));

symbolic procedure mk_ids_belong_anyspace u;
% makes all x in u belong to the global space.
<<for each x in u do remprop(x,'space); t>>;

symbolic procedure space_of_idx u;
% try to detect the space to which an index belongs to.
 begin scalar sp;
   return
   if sp:=get(u,'space) then sp
     else
   if assoc('wholespace,spaces!*) then 'wholespace
    else if length spaces!* = 1 then
           if yesp list("Does ",u," belong to ",caar spaces!*,"?")
                 then put(u,'space,caar spaces!*)
            else rerror(cantensor,4,list("Space of index ",u," unknown"))
          else
 % it is not clear that this error message should be maintained:
         msgpri(nil,nil,u, "MUST belong to a (sub)space",t);
end;

symbolic procedure space_dim_of_idx u;
% u is the name of an index
% result is the dimension of the space to which it belongs
% or an error message.
  if null !*onespace then
   begin scalar sp;
     sp:=get(u,'space);
     if null sp then return mvar dimex!*
     else  return get_dim_space sp
   end;

symbolic procedure extract_dummy_ids u;
% extracts the dummy indices from a given list
 if null u then nil
 else if car u memq dummy_id!* then
           car u . extract_dummy_ids cdr u
      else extract_dummy_ids cdr u;

rlistat('(rem_dummy_indices));

symbolic procedure rem_dummy_indices u ;
% remove property 'dummy' of all indices in u.
% redefines g_dvnames.
  <<for each x in u do
        <<dummy_id!* := delete(x,dummy_id!*);
           remprop(x,'space);
           remflag(list x,'dummy); remflag(list x,'reserved)>>;
    dummy_nam dummy_id!*; t>>;


symbolic procedure dummy_indices;
 symb_to_alg dummy_id!*;

 flag(list('dummy_indices),'opfn);

symbolic procedure mk_dummy_ids u;
 % u is the output of split_cov_cont_ids
 % constructs the 'dummy_id!*' and the g_dvnames globals
 % variable.
  begin scalar y;
    y:=clean_numid intersection(car u,cadr u);
    flag(y,'dummy);
    flag(y,'reserved);
    dummy_id!*:= union(y,dummy_id!*);
%    dummy_nam(dummy_id!*)
  end;

symbolic procedure mk_lst_for_dummy u;
% u is the output of index_list
% It eliminates the  minus sign
 for each x in u collect
  if atom x then x
  else
  if cadr x memq dummy_id!* then cadr x
  else x;

symbolic procedure multiplicity_elt(ob,l);
% ob is an arbitrary index, l is a list of indices
% returns the multiplicity of ob in l.
 begin integer n;
  while l:=memq(ob,l) do <<l:=cdr l;n:=n+1>>;
  return n
 end;

symbolic procedure mult_leq_onep u;
% u is a list of indices
if null u then t else
if multiplicity_elt(car u,u) leq 1 then
  mult_leq_onep(cdr u);


symbolic procedure eqn_indices(u,v);
% verify if two indices are fixed (pseudo-numbers) and equal.
(x and y and eqn(x,y))where x=!*id2num u, y=!*id2num v;

endmodule;

end;


