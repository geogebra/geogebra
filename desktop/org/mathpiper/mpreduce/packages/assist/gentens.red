module gentens;

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


% This module defines the characteristics of 'generic' tensors.
% 'generic' means: any nimbers of indices, no transformation
% properties under coordinate transformations assumed, any space
% assignement allowed.
% TENSOR  calls make_tensor which applies on the list of IDP the
% following properties:
% Flags: tensor, full
% Properties: indvarprt, xindvarprt_tens  for printing indices.
%           : SIMPTENSOR for simplification.
%           : Presently used to construct a correct list of indices.
% All arguments are NOT supposed to be tensor-indices. So
% dependencies may be either IMPLICIT ir EXPLICIT.

lisp remflag(list 'minus,'intfn);

fluid '(ycoord!* ymax!* ymin!* obrkp!*);

global '(dimex!* sgn!*  signat!* spaces!* numindxl!* pair_id_num!*) ;


lisp (pair_id_num!*:= '((!0 . 0) (!1 . 1) (!2 . 2) (!3 . 3) (!4 . 4)
                        (!5 . 5) (!6 . 6) (!7 . 7) (!8 . 8) (!9 . 9)
                        (!10 . 10) (!11 . 11) (!12 . 12) (!13 . 13)));

fluid('(dummy_id!* g_dvnames epsilon!*));

% g_dvnames is a vector.


switch onespace;

!*onespace:=t;  % working inside a unique space is the default.

rlistat('(tensor rem_tensor rem_value_tens));

flag('(make_bloc_diagonal),'opfn);

symbolic procedure make_bloc_diagonal te;
% te is a generic tensor. Forces it to be bloc
% diagonal when several spaces are involved.
<<put(te,'bloc_diagonal,'symb_belong_several_spaces);t>>;

symbolic procedure rem_value_tens u;
% remove values of the components of tensors included in u
 << for each x in u do
     if atom x then remprop(x,'kvalue)
      else
     if listp x then
       begin scalar kval,tens,varl,ind;
         tens:=car x;
         kval:=get(tens,'kvalue);
         remprop(tens,'kvalue);
         varl:= splitlist!:(x,'list);
         ind:=if null varl then cdr x else setdiff(cdr x,varl);
         varl:=if varl then car varl;
         ind:= (lambda y;
                 (mkindxlist for each z in y collect revalind z)) ind;
         kval:=delete(assoc(if varl then tens . varl . ind
                                 else tens . ind,kval),kval);
         put(tens,'kvalue,kval);
      end; t>>;

symbolic procedure rem_tensor1 x;
<<remflag(list x,'tensor); elim_names x;
    remprop(x,'kvalue);
    remprop(x,'klist);
    remprop(x,'simpfn);
    remprop(x,'prifn);
    remprop(x,'fancy!-pprifn);
    remprop(x,'partic_tens);
    remprop(x,'belong_to_space);
    remprop(x,'bloc_diagonal);
    remprop(x,'symtree);
    remflag(list x,'full);
    remflag(list x,'simp0fn);
    remflag(list x,'listargp);
    remflag(list x,'generic);
    remflag(list x, 'symmetric);
    remflag(list x,'antisymmetric);
    (if y then epsilon!*:=delete(y,epsilon!*))where y=assoc(x,epsilon!*);
    >>;

symbolic procedure elim_names u;
% u is the name of a particular tensor
 if get(u,'partic_tens)='simpdelt then remprop('delta,'name)
  else
 if get(u,'partic_tens)='simpdel then remprop('del,'name)
  else
 if get(u,'partic_tens)='simpeta then remprop('eta,'name)
  else
 if get(u,'partic_tens)='simpepsi then remprop('epsilon,'name)
  else
 if get(u,'partic_tens)='metric then remprop('metric,'name);


symbolic procedure tensor u;
% this is the basic constructor for the tensor object.
 begin;
 u:= for each x in u collect reval x; % correction
  for each x in u do
 if get(x,'avalue) or (flagp(x,'reserved) and null flagp(x,'tensor))
     or getrtype x  or (gettype x eq 'procedure)
     or (x memq list('sin,'cos,'tan,'atan,'acos,'asin,'int,'df))
    then rerror(cantens,1,list(x,"may not be defined as tensor"))
   else make_tensor(x,t);
   return t
 end;


symbolic procedure make_tensor(u,v);
 <<if v and flagp(u,'tensor) then
    lpri {"*** Warning: ",
                  u,"redefined as generic tensor "};
 rem_tensor list u;
 flag(list u,'tensor);
 flag(list u,'listargp);
 put(u,'simpfn,'simptensor);
 flag(list u,'simp0fn);
 put(u,'prifn,'indvarprt);
 put(u,'fancy!-pprifn,'xindvarprt_tens);
 flag(list u,'full)>>;

symbolic procedure rem_tensor u;
% To erase tensor properties on the list of identifiers u.
 <<u:=for each x in u collect reval x;
    for each x in u do if flagp(x,'tensor) then
    rem_tensor1 x;
  t>>;

symbolic procedure tensorp u;
% Elementary function to detect tensors.
 not atom u and flagp(car u,'tensor);

symbolic procedure tensorp!: u;
% u is a list of kernel as it comes from the
% function list_of_factors applied to a standard term.
% returns the number of tensor kernel present.
  begin integer nt;
  <<while u do if tensorp car u then nt:=nt+1; u:=cdr u>>;
  return nt
end;

 flag(list('make_tensor_belong_space),'opfn);

symbolic procedure make_tensor_belong_space(te,sp);
% te must be a tensor identifier
% introduces the indicator 'belong_to_space
% sp is a space name
% First, if no space is defined, it is, by default, unique
% and nothing should be done.
  if !*onespace then nil
   else
   if flagp(te,'tensor) then
      if get(te,'partic_tens) eq 'simpepsi then
       <<epsilon!* :=union(list(te . sp),
               delete(assoc(te,epsilon!*),epsilon!*));
          put(te,'belong_to_space,sp)
       >>
         else  put(te,'belong_to_space,sp);


rlistat '(make_tensor_belong_anyspace);

symbolic procedure make_tensor_belong_anyspace u;
% replace the list of tensors u in the ON ONESPACE
% environment.
 <<for each x in u do
     <<remprop(x,'belong_to_space);
       (if y then
           epsilon!*:=delete(y,epsilon!*))where y=assoc(x,epsilon!*)
     >>;
 t>>;

symbolic procedure simptensor u;
% Basic simplification procedure for all tensors.
 begin scalar x,ind,func,varl,bool,lsym;
    varl:= splitlist!:(u,'list); % gives ((list ...)) or nil.
   if null varl then
          (if z then <<varl:=z; bool:=t;>>)where z=extract_vars cdr u;
   ind:=if null varl then cdr u else setdiff(cdr u,varl);
   varl:=if  bool then 'list . varl
            else
           if varl then car varl;
    varl:= reval varl;
    x:= (lambda y;
            mkindxlist for each z in y collect revalind z) ind;
    x:=for each j in x collect reval j; % if substitutions are made.
    x:= (lambda y;
            mkindxlist for each z in y collect revalind z) x;
   x:=car u . x;
  % identify the possible 'dummy indices':
    ind:=split_cov_cont_ids cdr x;
   % Check numeric indices:
    num_ids_range(ind,car u);
    mk_dummy_ids ind;
   % verify if the set of dummy indices is consistent:
    verify_tens_ids ind;
   % if u is chosen bloc-diagonal then check the input
   % and, if symbols belong to different subspaces return 0
    if
      (if x  then apply1(x,ind))where x=get(car u,'bloc_diagonal)
      then return nil ./ 1;
   % If u is a special tensor then apply the relevant simplification
   % function:
    return if func:=get(car x,'partic_tens) then
                   if flagp(car u,'generic)  then
                       if func neq 'simpdelt then apply2(func,x,varl)
                         else apply2(func,x,varl) ./ 1
                    else  apply1(func,x) ./ 1
            else
           if flagp(car x,'symmetric) then
                            mksq(car x .
             if null varl then cont_before_cov ordn cdr x
                 else varl .  cont_before_cov ordn cdr x,1)
            else
           if flagp(car x,'antisymmetric) then
             if repeats
                  (if null affinep u then
                      (lambda y; append(car y,cadr y)
                                            )split_cov_cont_ids cdr x
                     else cdr x)
                            then nil ./ 1
               else
             (if not permp!:(z,cdr x) then
                      negsq mksq(car x . if varl then varl . z
                                          else z,1)
              else mksq(car x . if varl then varl . z
                                 else z,1)
              )where z= cont_before_cov ordn cdr x
            else
           % cases of partial symmetry
           % when the tensor is 0 it is advantageous to detect it
           % BEFORE canonical acts:
           if lsym:=get(car u,'symtree) then
             if symtree_zerop(cdr x,lsym) then nil ./ 1
                  else
                 mksq(if varl then car x . varl . cdr x else x,1)
           else
              mksq(if varl then car x . varl . cdr x else x,1)

 end;

%symbolic procedure current_princ_index_lst(u,v);
 % u is the tensor-kernel, v is its number of indices.
 % it returns a list of the form
 % ((id_tens1 (index1 . 1) (index2 . 2)...))
 % for instance:
 % ((tt (a . 1) ((minus b) . 2) (c . 3) (d . 4)))
 % for the currently handled tensors tt(a,-b,c,d).
 % From it one may extract all informations.
 % subla(v,'tt); ==>
 % ((a . 1) ((minus b) . 2) (c . 3) (d . 4))
 % it is also obtained from the macro 'extract_index_tens'.
% begin integer n;
%       scalar x,id_tens;
%    n:=1;
%    id_tens:=car u;
%    u:=cdr u;
%    while n leq v do
%           <<x:=nconc(list(car u . n),x);u:=cdr u; n:=n+1>>;
%    return (id_tens . reverse x) . nil
%end;

%symbolic procedure get_n_index(n,u);
 % u is the ouput of the smacro extract_index_tens.
 % n is an integer which corresponds to the index position.
 % gives the corresponding index.
 % it is an atom if contravariant.
 % it is a list which begins by 'minus' if it is
 % covariant.
% if n <= length u then car assoc2(n,u);

%symbolic procedure index_list u;
 % u is the ouput of extract_index_tens.
 % gives the list of indices without their positions
 % order in the list corresponds to the order of indices
 % for instance:
 % (a (minus b) c d) for tt(a,-b,c,d)
 % when the tensor is given explicitly in prefix form,
 % it is better to take the cdr of this form.
 %  begin scalar x;
 %   for i:=1:length u do  x:=get_n_index(i,u) . x;
 %   return reversip x
%end;

symbolic procedure split_cov_cont_ids u;
 % output is the composite list ((cov_indices)(cont_indices))
 % INPUT u is the output of 'index_list' or is simply the cdr
 % of the prefix form.
 begin scalar xcov,xcont;
 while u do << (if careq_minus y then xcov:= (raiseind y) . xcov
               else xcont := y . xcont)where y=car u; u:=cdr u>>;
 return list(reversip xcov,reversip xcont)
end;

symbolic procedure verify_tens_ids u;
% u is the output of split_cov_cont_ids
  begin scalar cov,cnt;
   cov:= car u;
   cnt:=cadr u;
   % eliminate the obviously misplaced dummy indices:
   % i.e. when a dummy index is at least TWICE in cov or cont
      if repeats extract_dummy_ids cov  or
         repeats extract_dummy_ids cnt then
   rerror(cantens,2,
            list(list(car u, cadr u),
             "are inconsistent lists of indices"))

   else return  t

 end;

rlistat '(make_variables remove_variables);

symbolic procedure make_variables u;
% u is a list of idp's.
% declare them as variables.
% allow to distinghish them from indices.
 <<for each x in u do flag(list x,'variable);t>>;

symbolic procedure remove_variables u;
% u is a list of idp's.
% declare them as variables.
% allow to distinghish them from indices.
 <<for each x in u do remflag(list x,'variable);t>>;

symbolic procedure extract_vars u;
 if null u then nil
  else
 if flagp(raiseind!: car u,'variable) then car u . extract_vars cdr u
  else extract_vars cdr u;

symbolic procedure select_vars u;
% used for SYMMETRIZE.
% use extract_vars
 begin scalar varl,ind,bool;
    varl:= splitlist!:(u,'list); % gives ((list ...)) or nil.
   if null varl then
          (if z then <<varl:=z; bool:=t;>>)where z=extract_vars cdr u;
   ind:=if null varl then cdr u else setdiff(cdr u,varl);
   varl:=if  bool then 'list . varl
            else
           if varl then car varl;
    return list(ind,varl)
 end;

symbolic procedure symb_belong_several_spaces ind;
% ind is the list  which comes from split_cov_cont_ids
if !*onespace then nil
 else
   begin scalar x,sp;
     x:=clean_numid flattens1 ind;
     while x and
      (null get(car x,'space) or get(car x,'space) eq 'wholespace)
         do x:= cdr x;
     if null x then return nil
      else
        while x and (null get(car x,'space)  or
                      get(car x,'space) eq 'wholespace) do x:=cdr x;
      sp:=get(car x,'space);
     while x and (null get(car x,'space) or
             get(car x,'space) eq 'wholespace or
             get(car x,'space) eq sp) do  x:=cdr x;
    return
     if null x then nil else t
end;

symbolic procedure num_ids_range(ind,tens);
% this procedure checks the validity of numeric indices in various
% cases
if !*onespace then
    if out_of_range(ind,dimex!*,nil) then
       rerror(cantens,3,"numeric indices out of range")
     else nil
 else % onespace is OFF.
      % verify if the tensor belong to a subspace:
if null numindxl!* then
    if out_of_range(ind,get_dim_space get(tens,'belong_to_space),
         get_sign_space get(tens,'belong_to_space))
       then   rerror(cantens,3,"numeric indices out of range")
    else  nil
 else  (if null lst_belong_interval(x,int) then
         rerror(cantens,3,"numeric indices do not belong to (sub)-space")
        )where x=extract_numid flattens1 ind,
                int=subla(numindxl!*,get(tens,'belong_to_space));


symbolic procedure restore_tens_idx(u,v);
 % u is a dummy-compatible list,
 % v is the original list of indices given by
 % index_list extract_intex_tens <tensor> or cdr <prefix form>.
 % result is the new index_list
 % exemple:
 % u=(d (minus b) a a), v=(a (minus b) c (minus c))
 % restore_tesn_idx(u,v); ==> (d (minus b) a (minus (a)))
 if null u then nil
  else
 if null memq(car u,dummy_id!*) then car u . restore_tens_idx(cdr u,cdr v)
  else
 if atom car u and atom car v then car u . restore_tens_idx(cdr u,cdr v)
  else
 lowerind u . restore_tens_idx(cdr u,cdr v);

symbolic procedure clean_numid u;
 % input is a list of indices.
 % output is a list of 'non-numeric' indices.
 % 11 is the biggest allowed integer
  if null u then nil
   else
 if !*id2num car u then clean_numid cdr u
  else car u . clean_numid cdr u;

symbolic procedure extract_num_id u;
% extract all pseudo-numeric indices from u.
 if null u then nil
  else
 if charnump!: car u then car u . extract_num_id cdr u
  else extract_num_id cdr u;

symbolic procedure extract_numid u;
 % input is a list of indices.
 % output is a list of the corresponding 'numeric' indices.
 % 13 is the biggest allowed integer
  if null u then nil
   else
 (if x  then x . extract_numid cdr u
  else extract_numid cdr u)where x=!*id2num car u;

symbolic procedure mkindxlist u;
% CONSTRUCTS THE COVARIANT and CONTRAVARIANT numeric INDICES.
 for each j in u collect
   if fixp j then !*num2id j else
       if pairp j and fixp cadr j then list('minus, !*num2id cadr j)
                                  else j;

symbolic procedure !*num2id u;
 %CONVERTS A NUMERIC INDEX TO AN ID;
 %TAKEN FROM EXCALC.
    if u<12 then intern cdr assoc(u,
              '((0 . !0) (1 . !1) (2 . !2) (3 . !3) (4 . !4)
                (5 . !5) (6 . !6) (7 . !7) (8 . !8) (9 . !9)
                 (10 . !10) (11 . !11) (12 . !12) (13 . !13)))
   else intern compress append(explode '!!,explode u);

symbolic procedure !*id2num u;
 %CONVERTS AN INDEX TO A NUMBER OR nil IS RETURNED.
 begin scalar x ;
   if x:=  assoc(u, pair_id_num!*) then
    return cdr x
end;

symbolic procedure num_indlistp u;
% returns True if the list of indices
% contains ONLY numeric indices.
 numlis for each y in u collect !*id2num y;

symbolic procedure out_of_range(u,dim,sign);
% dim represents the
% actual space dimension of the space.
% acts only when it is an integer.
% dimsub represents  the subspace signature
% u is the list generated by split_cov_cont_ids
 if fixp dim then
  begin scalar lu,sign_space;
   lu:=extract_numid flattens1 u;
   sign_space:=if null sign then signat!* else sign;
   while lu and
        (if sign_space=1 then car lu < dim
          else
         if sign_space =0 then car lu <=dim)
                                do lu:=cdr lu;
    return if lu then t else nil
  end;

symbolic procedure revalind u;
 % Pour que -0 ne devienne pas +0:
    begin scalar x,y,alglist!*;
      x := subfg!*;
      subfg!* := nil;
      u := subst('!0,0,u);
      % The above line is used to avoid the simplification of -0 to 0.
      y := prepsq simp u;
      subfg!* := x;
      return y
   end;

symbolic procedure revalindl u;
for each ind in u collect revalind ind;

symbolic procedure indvarprt u;
% An extension of the corresponding function of EXCALC
    if null !*nat then <<prin2!* car u;
                         prin2!* "(";
                         if cddr u then inprint('!*comma!*,0,cdr u)
                          else maprin cadr u;
                         prin2!* ")" >>
     else begin scalar x,y,y2,args,spaceit; integer l,maxposn!*,oldy;
            l := flatsizec flatindxl u+length cdr u-1;
            if l>(linelength nil-spare!*)-posn!* then terpri!* t;
            %avoid breaking of an indexed variable over a line;
            y := ycoord!*;
            maxposn!*:=0;
            prin2!* car u;
            spaceit := if get(car u,'partic_tens) memq {'simpdelt,'simpdel}
                         then  << x := posn!*; nil>>
                         else t;
            for each j on cdr u do
              <<oldy:=ycoord!*;
                ycoord!* :=  y + if (atom car j) or (careq_tilde car j) then 1 else -1;
                if null(spaceit) and (oldy neq ycoord!*) then
                  << if posn!*>maxposn!* then maxposn!*:=posn!*;
                     posn!*:=x;
                  >>;
                if ycoord!*>ymax!* then ymax!* := ycoord!*;
                if ycoord!*<ymin!* then ymin!* := ycoord!*;
                if (atom car j) or (careq_tilde car j)
                  then maprint (car j,0)
                else if careq_minus car j
                  then maprint (cadar j,0)
                else args := car j;
                if cdr j then prin2!* " ">>;
            if null cdr u then
              <<ycoord!* :=  y + 1;
                if ycoord!*>ymax!* then ymax!* := ycoord!*;
                if ycoord!*<ymin!* then ymin!* := ycoord!*;
                maprint ('!(!),0)
              >>;
            ycoord!* := y;
            if (maxposn!*>0) and (posn!*<maxposn!*) then posn!*:=maxposn!*;
            if args then
                << prin2!* "(";
                   obrkp!* := nil;
                   y2 := orig!*;
                   orig!* := if posn!*<18 then posn!* else orig!*+3;
                   if cdr args then inprint('!*comma!*,0,cdr reval args );
                   obrkp!* := t;
                   orig!* := y2;
                   prin2!* ")";
                >>;
          end;

put('indvarprt,'expt,'inbrackets);

symbolic procedure xindvarprt_tens(l,p);
  % An extension of the function XINDVARPRT of  EXCALC.
  fancy!-level
  ( if not(get('expt,'infix)>p) then
      fancy!-in!-brackets({'xindvarprt_tens,mkquote l,0}, '!(,'!))
    else
      begin scalar w,x,s,args,spaceit;
        spaceit:=t;
        w:=(fancy!-prefix!-operator car l) where fancy_lower_digits = nil;
                 if get(car l,'partic_tens) memq {'simpdelt,'simpdel}
                                  then spaceit:=nil;
        if w eq 'failed then return w;
        l := cdr l;
        if l then
          <<
          while l and (w neq 'failed) do
            << if (atom car l) or (careq_tilde car l) then
                 (if s eq '!^ then
                    x := car l . x
                  else <<
                    if s then
                      <<if spaceit then fancy!-prin2!*("{}",0);
                      w := fancy!-print!-indexlist1(reversip x,s,nil)>>;
                    x := {car l};
                    s := '!^>> )
               else (
                 if careq_minus(car l) then
                   ( if s eq '!_
                     then x := cadar l . x
                     else <<
                       if s then
                         <<if spaceit then fancy!-prin2!*("{}",0);
                           w := fancy!-print!-indexlist1(reversip x,s,nil)>>;
                       x := {cadar l};
                       s := '!_>> )
                 else
                   args:=car l);
              l := cdr l>>;
          if x then
            << if spaceit then fancy!-prin2!*("{}",0);
               w := fancy!-print!-indexlist1(reversip x,s,nil);
               if w eq 'failed then return w >>;
          if args then w:=fancy!-print!-function!-arguments cdr args;
          >>
            else
          <<
             w := fancy!-print!-indexlist1(list('!(,'!)),'!^,nil)
          >>;
       return w;
   end);

endmodule;

end;
