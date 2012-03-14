
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

comment

  The following routines can handle scalar expressions that are composed out
  of 3-vectors in three different notations:

  - the extended vector form: ABCD... stands for (A , B x (C x (D x ...)))
    where ',' stands for the vector product and 'x' stands for the
    skew symmetric product,
  - the standard vector form involving only AB = (A , B) and
    ABC = (A , B x C),
  - the component form involving only the components a1,a2,a3,b1,...

  In all 3 notations the vector is represented by a single letter only.
  Any extra (non-vectorial) constant or parameter has to start with the
  characters !&.

  ROUTINES:
  ---------
  e2s : extended vector form into standard vector form, also conversion of a
        standard vector form into standard vector form, but where factors
        in calar products and triple products are sorted lexicographically,
        This is necessary in order to work with this package on expressions
        that have been generated outside this package.
  v2c : any vector form (extended or standard) into component form
  c2s : component form into standard vector form
  c2sl: like c2s, only the resulting vector expression is returned partitioned
        as a list which simplifies choosing arbitrary parameters arbcomplex(i)
        such that the vector expression is as short as possible
  s2s : generates and uses vector identities to length reduce a standard
        vector form expression
  s2e : like s2s but also uses identities involving n-products and thus
        transforms standard vector form into extended vector form
  genpro: generation of all scalar and spate products for a given vector list
  genpro_wg: generation of all scalar and spate products with weight lists
             for a given vector weight list
  poisson_c: computation of the poisson bracket of two arbitrary
             expressions, needs anything what struc_cons needs
             in component form, e.g. kap:=-a1**2-a2**2-a3**2
  poisson_v: computes the Poisson bracket for two vector expressions uses
             global variables v_, gbase_ and initially the procedure
             poisson_c (to compute the Poisson bracket between scalar and
             spate products), so poisson_v needs all that poisson_c needs,
             currently restricted to vectors A,B,U,V through gbase_ .
  %  arb2zero: sets all arbcomplex(1)..arbcomplex(!!arbint):=0 and !!arbint:=0
  gfi: generates a vector expression with unknown coefficients, where
       each term of the generated polynomial has a total weight list
       as required by the first argument to gfi. The weight list of each
       vector is given through the second argument to gfi. The third argument
       is a list of vector expressions to the effect that no term of
       the generated polynomial is purely a product of powers of these vector
       exressions. gfi returns a list with the generated polynomial as first
       element followed by a list of undetermined constants.
  vinit: generates all polynomial identities for all scalar and triple
         products from the (currently) 4 vectors in the input list

  GLOBAL VARIABLES:
  -----------------
  algebraic:  all variables in struc_cons, like U1,U2,U3,V1,V2,V3,kap
              v_, gbase_, heads_ % for poisson_v
              !&c1, !&c2, ...    % from c2s
  operator:   poi_               % stores the Poisson brackets
  lisp:       fino_, wgths_      % from file gengen.red
              !&r1, !&r2, ...    % from gfi
              tr_vec

  INITIALIZATION:
  ---------------
  This file contains all initializations (of v_,gbase_,heads_,struc_cons)
  needed for computations with the 2 constant vectors A,B and 2 dynamical
  vectors U,V. If other vectors or constant vectors with conditions,
  like AB=0 should be used  then
  - run:  vinit(list_of_vectors)
$

lisp <<terpri()$
 write"********************************************************************"$
 terpri()$
 write"* Please note the following conventions to use these routines:     *"$
 terpri()$
 write"* - Vectors are denoted by a single character and products         *"$
 terpri()$
 write"*   (A,Bx(Cx(Dx...))) by single identifiers ABCD... . For example, *"$
 terpri()$
 write"*   scalar products (A,B) by AB, triple products (A,BxC) by ABC,   *"$
 terpri()$
 write"*   (A,Bx(CxD)) by ABCD and so on. For scalar and triple products  *"$
 terpri()$
 write"*   only those versions are used for which letters are sorted      *"$
 terpri()$
 write"*   alphabetically, for example, AB, ABU but not BA, AUB or BUA.   *"$
 terpri()$
 write"* - Any non-vectorial variable starts with the two characters !&.  *"$
 terpri()$
 write"* - For further comments see the beginning of this file and the    *"$
 terpri()$
 write"*   files v3tools.tex and v3tools.tst.                             *"$
 terpri()$
 write"********************************************************************"$
 terpri()
>>$

lisp(if getd 'set_bndstk_size then set_bndstk_size(50000))$
load_package  'groebner$
load_package 'crack$
setcrackflags()$
symbolic fluid '(print_more record_hist max_gc_short size_watch max_gc_fac
                 print_ old_history tr_short tr_vec)$
tr_vec:=nil$

%----------------------------------------------------

algebraic operator poi_$

%----------------------------------------------------
symbolic fluid '(wgths_ fino_ nfct_)$

symbolic procedure gen(all)$
% The global variable wgths_ specifies the weights of the generated terms
% which are put into the global list fino_.
% 'all' is the list of variables and their weights to be used.
% Before calling gen externally, set the global variable fino_ to nil.
if null all then {cons(nil,for each h in wgths_ collect 0)}
                   % {{{'bu},0,1,1}}    % i.e. if bu is an overall factor
                   % but then reductions are wrong
            else begin
  scalar h,v,vw,oldli,newli,h,vwcp,wgcp,found_less,found_more,b,c,d$

  h:=car all$  all:=cdr all$
  v:=car h; vw:=cdr h$    % the next factor to be multiplied + its weight-list

  oldli:=gen(all)$
  % old has the form {e1,e2,e3,..} where
  % ei = {{v1,v2,v3,..}, weights} represents a monomial
  % vi are factors of the monomial,
  % for all i: weights(i)<=wgths_(i) and not all relations are equalities.

  while oldli do <<
    h:=car oldli; oldli:=cdr oldli$
    newli:=cons(h,newli);
    repeat <<
      vwcp:=vw;
      wgcp:=wgths_$
      found_less:=nil$
      found_more:=nil$
      h:=cons(cons(v,car h),
              for each b in cdr h collect
              <<d:=b+<<c:=car vwcp;vwcp:=cdr vwcp;c>>;
                if d<car wgcp then found_less:=t else
                if d>car wgcp then found_more:=t$
                wgcp:=cdr wgcp$
                d
              >>
             )$
    >> until if found_more then t else
             if found_less=nil then
             <<fino_:=cons(if cdar h then cons('TIMES,car h)
                                     else caar h            ,fino_)$ t>>
             else <<newli:=cons(h,newli); nil>>
  >>$

  return newli
end$
%----------------------------------------------------

algebraic procedure is_reduceable(trm,hs)$
begin
 while hs neq {} and (den(trm/first hs) neq 1) do hs:=rest hs;
 return if hs neq {} then t
                     else nil
end$

%----------------------------------------------------

symbolic procedure l2s(li)$
% The program converts a list {a,b,c,d,..} which represents
% (a,(bx(cx(dx..)))) where ',' stands for the scalar product and x
% stands for the skew product into a polynomial of scalar products and
% spate products.
% The argument li must be a lisp list with at least 2 elements

if length(li)=2 then if ordp(car li,cadr li) then mkid(car  li,cadr li)
                                             else mkid(cadr li,car  li)
                else
if length(li)=3 then if (car  li=cadr  li) or
                        (car  li=caddr li) or
                        (cadr li=caddr li) then 0
                                           else
if ordp(car   li,cadr  li) and
   ordp(cadr  li,caddr li) then mkid(car li,mkid(cadr li,caddr li)) else
if ordp(cadr  li,caddr li) and
   ordp(caddr li,car   li) then mkid(cadr li,mkid(caddr li,car li)) else
if ordp(caddr li,car   li) and
   ordp(car   li,cadr  li) then mkid(caddr li,mkid(car li,cadr li)) else
if ordp(car   li,caddr li) and
   ordp(caddr li,cadr  li) then{'MINUS,mkid(car li,mkid(caddr li,cadr li))}else
if ordp(cadr  li,car   li) and
   ordp(car   li,caddr li) then{'MINUS,mkid(cadr li,mkid(car li,caddr li))}else
if ordp(caddr li,cadr  li) and
   ordp(cadr  li,car   li) then{'MINUS,mkid(caddr li,mkid(cadr li,car li))}else
write"error in ord!"
                else
{'DIFFERENCE,{'TIMES,if ordp(car  li,caddr li) then
                       mkid(car   li,caddr li) else
                       mkid(caddr li,car   li),l2s(cons(cadr li,cdddr li))},
             {'TIMES,if ordp(cadr li,caddr li) then
                       mkid(cadr  li,caddr li) else
                       mkid(caddr li,cadr  li),l2s(cons(car  li,cdddr li))} }$
%----------------------------------------------------

symbolic procedure permu_repi(li)$
% generates a list of permutations of the elements of li without multiple
% permutations but where li can have multiple elements
begin scalar p,perm,red_perm$
 perm:=
 if 1=length li then list(li)
                else for each x in li join
                     for each y in permu_repi(delete(x,li)) collect cons(x,y)$

 if perm and length car perm = 3 then <<
  for each p in perm do
  if cadr p neq caddr p then red_perm:=cons(p,red_perm)$
  perm:=red_perm;
  red_perm:=nil$
 >>$

 % deleting multiple lists
 for each p in perm do
 if not member(p,red_perm) then red_perm:=cons(p,red_perm)$

 return red_perm
end$
%----------------------------------------------------

symbolic procedure genid(vli,wgli)$
% This procedure generates a list of all n-vector products for given
% vli : the list of vector variables and their weights
%       eg {{A,1,0},{B,1,0},{U,0,1},{V,1,1}}
% wgli: the list of total weights of each term of the polynomial
%       eg {2,3}
begin scalar h,hm,p,perm,allperm,idli,idlicp$

 fino_:=nil$
 wgths_:=wgli$
 gen(vli);

 % now generation of all permutations
 for each h in fino_ do <<
  % Each h is of the form {TIMES a a b b b u v v v ...}
  write"Generating specific permutations of products of the vectors ",
       compress cdr h,"."$
  terpri()$
  allperm:=permu_repi(cdr h)$
  write length allperm," permutations generated."$terpri()$

  % deleting multiple lists with identical first two elements
  for each p in allperm do
  if car p neq cadr p then perm:=cons(p,perm)$
  allperm:=nil$
  write"Applying another filter, ",length perm," are left."$terpri()$

  idli:=nil$
  for each p in perm do <<

   h:=reval l2s p$
   if not zerop h then <<
    hm:=reval {'MINUS,h}$
    idlicp:=idli$
    while idlicp and
          caar idlicp neq h and
          caar idlicp neq hm do idlicp:=cdr idlicp$

    if null idlicp then idli:=cons((h . intern compress p),idli)
   >>$
  >>$
  write "At a first look ",length idli," seem to be non-equivalent."$terpri()$
% for each h in idli do mathprint {'EQUAL,cdr h,car h}$
 >>$
 fino_:=nil;
 return for each h in idli collect {'DIFFERENCE,car h, cdr h}
end$
%----------------------------------------------------

symbolic fluid '(algebraic_reduce_functions)$
lisp (algebraic_reduce_functions:=
'(PLUS MINUS DIFFERENCE TIMES QUOTIENT EXPT ARBCOMPLEX LIST))$

symbolic procedure expro(a)$
% procedure to extract all identifiers
% the parameter a has to be in prefix form

% WINFRIED: also, ich nehme immer die Funktion  groebnervars
% oder auch gvarlis wenn man sowieso das groebnerpackage
% geladen hat. Eventuell groebnervars reval ...

if null a then nil else
if atom a then
  if numberp a or
     member(a,algebraic_reduce_functions) then nil
                                          else {a}
          else union(expro(car a),expro(cdr a))$
%----------------------------------------------------

symbolic procedure extract_prod(a)$
% extracts all identifiers from 'a' which do not start with &,!&,!!
begin scalar ep,epl,h,ret$
 ep:=expro reval a$
 for each h in ep do <<
  epl:=explode h$
  if car epl neq '&  and
     car epl neq '!& and
     car epl neq '!! then ret:=cons(h,ret)
 >>$
 return ret
end$
%----------------------------------------------------

symbolic operator e2s$
symbolic procedure e2s(a)$
% converts extended vector form to standard vector form
% also useful to convert e.g. BA to AB if a vector expression has been
% generated outside of this program
begin scalar p,pl,px$
 pl:=extract_prod a$
 for each p in pl do <<
  px:=l2s explode p$
  if p neq px then a:=subst(px,p,a)
 >>$
 return a
end$
%----------------------------------------------------

symbolic procedure vc(v)$
% generates a lisp list of the 3 components for a given vector
{mkid(v,1),mkid(v,2),mkid(v,3)}$
%----------------------------------------------------

symbolic procedure dot(F,G)$
% returns the scalar product of two vectors represented
% by lisp lists of components
reval {'PLUS,{'TIMES,car   F,car   G},
             {'TIMES,cadr  F,cadr  G},
             {'TIMES,caddr F,caddr G} }$
%----------------------------------------------------

symbolic procedure cross(F,G)$
% returns the skew product of two vectors represented
% by lisp lists of components
{{'DIFFERENCE,{'TIMES,cadr  F,caddr G},{'TIMES,caddr F,cadr  G}},
 {'DIFFERENCE,{'TIMES,caddr F,car   G},{'TIMES,car   F,caddr G}},
 {'DIFFERENCE,{'TIMES,car   F,cadr  G},{'TIMES,cadr  F,car   G}} }$
%----------------------------------------------------

symbolic procedure spat(F,G,H)$
% returns the spate (triple) product of three vectors
dot(F,cross(G,H))$
%----------------------------------------------------

symbolic operator v2c$
% converts any vector form into component form
symbolic procedure v2c(a)$
begin scalar p,pl,px$
 if tr_vec then <<write"v2c start"$terpri()>>$
 a:=e2s a$
 pl:=extract_prod a$
 for each p in pl do <<
  px:=explode p$
  a:=subst(if length px = 2 then dot (vc car px,vc cadr px)
                            else spat(vc car px,vc cadr px,vc caddr px),p,a)
 >>$
  if tr_vec then <<write"v2c end"$terpri()>>$
 return reval a
end$
%----------------------------------------------------

symbolic procedure addvec(v,vl)$
% vl is an assoc list ((a . na) (b . nb) ...) of vectors a,b,... and the
% number of their occurences na, nb, ...
% An occurence of the vector v is added.
begin scalar ve$
 ve:=assoc(v,vl)$
 return
 if null ve then cons((v . 1),vl)
            else cons((v . add1 cdr ve),delete(ve,vl))
end$
%----------------------------------------------------

symbolic procedure letters_of(a)$
% explodes an identifier and returns list of letters
% if 'a' is a vector identifier and nil if 'a' is a parameter
% (ie if 'a' starts with & or !& or !! )
begin scalar l,h,ea;
 ea:=explode a$
 if car ea neq '& and
    car ea neq '!& and
    car ea neq '!! then
 for each h in ea do
 if freeof('(!0 !1 !2 !3 !4 !5 !6 !7 !8 !9),h) then l:=cons(h,l)$
 return l
end$
%----------------------------------------------------

symbolic procedure get_vlist_of_term(at)$
% returns a sorted association list of vectors and their degree
% appearing in the single term 'at'
begin scalar f1,v,vl,vlc,h$
 if pairp at and car at = 'QUOTIENT then at:=cadr at;
 if pairp at and car at = 'MINUS then <<
  at:=cadr at;
  if pairp at and car at = 'QUOTIENT then at:=cadr at
 >>$
 if pairp at and ((car at = 'TIMES) or
                  (car at = 'LIST ) or
                  (car at = 'EQUAL)    ) then at:=cdr at
                                         else at:={at}$
 while at do <<
  f1:=car at;  at:=cdr at;
  if not ((numberp f1               ) or
          ((pairp f1) and
           (car f1 = 'QUOTIENT) and
           (numberp cadr f1) and
           (numberp caddr f1)       )    ) then
  if atom f1 then for each v in letters_of f1 do vl:=addvec(v,vl) else
  if car f1 neq 'ARBCOMPLEX then
  if car f1 neq 'EXPT then write"****** car not EXPT ******" else
  for each v in letters_of cadr f1 do
  for h:=1:(caddr f1) do vl:=addvec(v,vl)
 >>;

 % at first sorting vl to generate later always the same vector
 % products, like ab instead of ba
 while vl do <<
  v:=car vl;
  for each u in vl do if ordp(car v,car u) then v:=u;
  vlc:=cons(v,vlc);
  vl:=delete(v,vl);
 >>$
 return vlc
end$
%----------------------------------------------------

symbolic procedure filter_hom(a,at)$
% From the polynomial vector expression 'a' in component form return
% 1) that part for which each term has the same number of occurences of the
%    same vector components as in the term 'at'
% 2) the list of vectors and their number of occurences ((a.2) (b.1) ...)
begin scalar vl,v,h,gs;
 if tr_vec then <<write"filter_hom start"$terpri()>>$

 vl:=get_vlist_of_term(at)$
 gs:=gensym()$
 for each v in vl do <<
  % replace each component_of_car_v by gs**(component_of_car_v)
  for each h in vc car v do a:=subst({'TIMES,h,gs},h,a)$
  a:=reval a$
  a:=coeffn(a,gs,cdr v)
 >>$

 if tr_vec then <<write"filter_hom end"$terpri()>>$
 return cons(a,vl)
end$
%----------------------------------------------------

symbolic procedure t1(a)$
% 'a' is an expression in prefix form
% t1 returns the first term of its numerator
if null a then 0 else
if atom a then a else
if (car a='QUOTIENT) then reval {'QUOTIENT,t1 cadr a,caddr a} else
if (car a='PLUS) or (car a='DIFFERENCE) then cadr a
                                        else a$
%----------------------------------------------------

symbolic operator genpro_wg$
% converting algebraic input list to symbolic and result back to algebraic
symbolic procedure genpro_wg(vl)$
cons('LIST,for each g in genpro_wg_l(for each h in cdr vl collect cdr h)
           collect cons('LIST,g))$
%----------------------------------------------------

symbolic procedure addvectowg(wg,v)$
% adds vector v of weights to the vector wg
for j:=1:(length wg) collect reval {'PLUS,nth(wg,j),nth(v,j)}$
%----------------------------------------------------

symbolic procedure genpro_wg_l(vl)$
% input: symbolic list of variables with weights, e.g.
%        vl={{A,1,0,0},{B,0,1,0},{U,0,0,1},{V,1,0,1}}$
% output: list of scalar/spate products + weights {{AA,2,0,0},..}
if null vl then {'LIST} else
begin scalar n,j,k,l,p2,p3,wg;

 wg:=for j:=1:((length cdar vl)) collect 0$
 n:=length vl;
 % then generation of the scalar products
 p2:=for j:=1:n join
     for k:=j:n collect
     cons(mkid(car nth(vl,j),car nth(vl,k)),
          addvectowg(addvectowg(wg,cdr nth(vl,j)),cdr nth(vl,k)))$

 % then generation of the spate products
 p3:=for j:=  1  :(n-2) join
     for k:=(j+1):(n-1) join
     for l:=(k+1):  n   collect
     cons(mkid(car nth(vl,j),mkid(car nth(vl,k),car nth(vl,l))),
          addvectowg(addvectowg(addvectowg(wg,cdr nth(vl,j)),
                                cdr nth(vl,k)),cdr nth(vl,l)))$

 return append(p2,p3)
end$
%----------------------------------------------------

symbolic procedure std_wg(vl)$
% lisp input : {a,b,u,v}
% lisp output: {{a,1,0,0,0},{b,0,1,0,0},{u,0,0,1,0},{v,0,0,0,1}}
for h:=1:(length vl) collect cons(nth(vl,h),
for k:=1:(length vl) collect if h=k then 1 else 0)$
%----------------------------------------------------

symbolic operator genpro$  % moved up
symbolic procedure genpro(vl)$
% input: algebraic list of vectors, e.g. {a,b,u,v}
% output: algebraic list of scalar and spate products
cons('LIST,for each h in genpro_wg_l(std_wg cdr vl) collect car h)$
%----------------------------------------------------

symbolic procedure hc2s(ahc)$
% generates a vector expression (if possible) for the homogeneaous
% component expression car ahc
% ahc={hom_expression_in_component_form,{('u.2),('v.1),..}}
% where 2 is the degree of u,...
begin scalar v,vl,nf,f,fl,zro,h,sol,ansatz$ %,oldorder$
 if tr_vec then <<write"hc2s start"$terpri()>>$

 % generation of all vector products with correct weight
 fino_:=nil$
 wgths_:=for each v in cdr ahc collect cdr v$
 if tr_vec then << write"gen start"$terpri()>>$
 gen genpro_wg_l std_wg for each v in cdr ahc collect car v$
 if tr_vec then <<write"gen end"$terpri()>>$

 % generating a vector ansatz with unknown coefficients
 nf:=1;
 ansatz:=for each v in fino_ collect <<
  f:=mkid('!&c,nf);
  nf:=add1 nf$
  fl:=cons(f,fl);
  {'TIMES,f,v}
 >>$
 fino_:=nil;
 if tr_vec then <<
  write"The vector ansatz has ",length fl," unknown coefficients."$terpri()
 >>$
 if null cdr ansatz then ansatz:=reval car ansatz
                    else ansatz:=reval cons('PLUS,ansatz)$

 % generate a list vl of all components of all vectors
 vl:=for each v in cdr ahc join vc car v$

% oldorder:=setkorder vl;

 zro:={'LIST,reval {'DIFFERENCE,car ahc,v2c ansatz}}$

 % splitting zro:

 for each v in vl do <<
  if tr_vec then <<write"splitting wrt ",v$terpri()>>$
  sol:=cdr algebraic(for each h in lisp(zro) join coeff(lisp h,lisp v));
  % deleting zeros
  zro:=nil$
  while sol do <<
   if car sol neq 0 then zro:=cons(car sol,zro);
   sol:=cdr sol$
  >>$
  if tr_vec then <<write"zro has ",length zro," conditions."$terpri()>>$
  zro:=cons('LIST,zro)
 >>$
 if tr_vec then <<
  write (length zro) - 1," conditions for ",length fl," unknowns."$terpri()
 >>$

 % solution of the condition:
 !!arbint:=0$
 if tr_vec then <<write"solveeval start"$terpri()>>$
 sol:=solveeval list(zro,cons('LIST,fl));
 if tr_vec then <<write"solveeval end"$terpri()>>$

 return
 if null cdr sol then nil
                 else algebraic <<

  ansatz:=sub(first lisp sol,lisp ansatz)$

  zro:=0$
  sol:=for h:=1:lisp !!arbint collect <<
   v:=coeffn(ansatz,arbcomplex(h),1)$
   zro:=zro+arbcomplex(h)*v$
   num v
  >>$
  ansatz:=ansatz-zro$

  % Call of CRACK to shorten the identities
  if t then <<
   off batch_mode$
   lisp <<
    print_more:=nil;
    record_hist:=t;
    max_gc_short:=15; % 25;
    size_watch:=t;
    max_gc_fac:=4;
    print_:=nil$
    old_history:='(l 11 !; q 0)$
   >>$
   sol:=crack(sol,{},lisp cons('LIST,extract_prod(sol)),{});
  >>$

  lisp(if tr_vec then <<write"hc2s end"$terpri()>>)$
  cons(ansatz,first first sol)
 >>
end$
%----------------------------------------------------

algebraic procedure c2s(a)$
begin scalar n$
 return <<
  n:=0;
  for each h in c2sl(a) sum
  first h + for each g in rest h sum <<
   n:=n+1;
   (lisp mkid('!&c,reval algebraic n))*g
  >>
 >>
end$
%----------------------------------------------------

symbolic operator c2sl$
symbolic procedure c2sl(a)$
% converts a possibly inhom. vector expression 'a' into standard vector form
begin scalar av,ahv,ahc,at1,tr_vec$
 if tr_vec then <<write"c2sl start"$terpri()>>$
 tr_vec:=nil$
 a:=reval a$
 av:={}$ ahv:=1$
 while (a neq 0) and ahv do <<

  % pick the first term
  at1:=t1 a$
  if tr_vec then <<write length a," terms to vectorize."$terpri()>>$

  % extract all terms of type at1
  ahc:=filter_hom(a,at1)$ % includes assoc list of vectors + degree

  ahv:=hc2s ahc$
  if ahv then <<av:=cons(ahv,av); a:=reval {'DIFFERENCE,a,car ahc}>>
         else <<write"All the terms with the same homogeneity as the"$
                terpri()$
                write"following terms can not be vectorized: "$
                mathprint at1>>
 >>$

 return
 if null ahv then nil
             else <<
  if tr_vec then <<
   terpri()$
   write"The input expression in component form has been partitioned. Each of"$
   terpri()$
   write"the partitions Pi has been converted into vector form and comes with"$
   terpri()$
   write"identities Iij of the same homogeneity type to be used to shorten Pi."$
   terpri()$
   write"Everything is returned in the form {{P1,I11,I12,..},{P2,I21,..},..}."$
   terpri()
  >>$
  if tr_vec then <<write"c2sl stop"$terpri()>>$
  reval cons('LIST,av)
 >>$

end$
%----------------------------------------------------

symbolic procedure add_hom_term(v,at,pl)$
% pl is an assoc list ((vl1 . terms1) (vl2 . terms2) ...) where
% vli are assoc lists of vectors and their appearance and terms1 the sum of all
% terms with these vectors
% In this procedure an occurence of the pair (v . at) is added.
begin scalar plc$
 while pl and (caar pl neq v) do <<plc:=cons(car pl,plc);pl:=cdr pl>>$
 if null pl then plc:=cons((v . at),plc)
            else <<
  plc:=cons((v . {'PLUS,cdar pl,at}),plc);
  pl:=cdr pl;
  while pl do <<plc:=cons(car pl,plc);pl:=cdr pl>>
 >>$
 return plc
end$
%----------------------------------------------------

symbolic procedure shortvex(a,d)$
% generates and uses vector identities to length reduce standard vector
% form expressions
% if d<>nil then it also uses identities involving n-products and thus
% transforms the standard vector form expression 'a' into extended vector form
begin scalar at1,pl,sh,n,wg,j,k,vli,wli,vlc,idty,gs$

 % partition 'a' into parts where within each all terms have the same
 % number of the same vectors
 a:=reval a$
 while a neq 0 do <<

  % pick the first term
  at1:=t1 a$

  % add at1 to the right partition
  pl:=add_hom_term(get_vlist_of_term(at1),at1,pl)$

  a:=reval {'DIFFERENCE,a,at1}
 >>$

 % optimize the formulation in terms of n-products for each single partition
 while pl do <<
  vlc:=caar pl; sh:=cdar pl; pl:=cdr pl;
  if d then <<
   n:=length vlc;
   k:=0;
   vli:=nil; wli:=nil;
   while vlc do <<
    k:=add1 k;
    wg:=append(for j:=1:(k-1) collect 0,cons(1,for j:=(k+1):n collect 0))$
    vli:=cons(cons(caar vlc,wg),vli);
    wli:=cons(cdar vlc,wli);
    vlc:=cdr vlc
   >>$
  >>$

  algebraic write"=========================================================="$
  algebraic write"One partition of input: ",lisp sh$

  % sh contains a specific combination of vectors encoded in vli and wli.
  % For this combination of vectors all identities are generated next:
  algebraic (% write"a) identities: ",
  idty:=rest first c2sl v2c lisp t1 sh);

  % now identities are generated that contain each one n-vector product
  % expressed in terms of scalar and triple products:
  gs:=gensym()$
  idty:=cons('LIST,
             cons(reval {'DIFFERENCE,sh,gs},
                  if d then append(cdr idty,genid(reverse vli,reverse wli))
                       else        cdr idty
            ))$

%write"idty="$prettyprint idty$
  algebraic off batch_mode$
  print_more:=nil;
  record_hist:=t;
  max_gc_short:=15; % 25;
  size_watch:=t;
  max_gc_fac:=4;
  print_:=nil$
%print_:=100000$
  tr_short:=t$
  old_history:='(67 e_1 nil q 0)$
%old_history:=nil$
  idty:=algebraic(crack(idty,{},lisp cons('LIST,extract_prod(idty)),{}));
  if idty={'LIST} then <<write"ERROR 1 in CRACK!"$terpri()>>
                  else <<
   idty:=cadr cadr idty; % the remaining equations from the first solution
   while idty and freeof(car idty,gs) do idty:=cdr idty;
   if null idty then <<write"ERROR 2 in CRACK!"$terpri()>>
                else <<
    idty:=solveeval list(car idty,{'LIST,gs});
    if null idty or (idty={'LIST}) then <<write"ERROR 3 in SOLVE!"$terpri()>>
                                   else <<
     idty:=caddr cadr idty$
     if 0=reval reval {'DIFFERENCE,sh,idty} then
     write"Partition is unchanged."         else <<
      write"shortened expression:";
      mathprint idty    % rhs of first solution
     >>
    >>
   >>
  >>$

  a:=algebraic(a+idty)$

  % to save memory (for now):
  idty:=nil

 >>$
 return a
end$
%----------------------------------------------------

symbolic operator s2s$
symbolic procedure s2s(a)$
shortvex(a,nil)$
%----------------------------------------------------

symbolic operator s2e$
symbolic procedure s2e(a)$
shortvex(a,t)$
%----------------------------------------------------

% not anymore used:
%symbolic operator arb2zero$
%symbolic procedure arb2zero(a)$
%begin scalar h$
% for h:=1:!!arbint do a:=algebraic(sub(arbcomplex(h)=0,a))$
% !!arbint:=0$
% return a
%end$
%----------------------------------------------------

algebraic procedure poisson_c(F,G,poi_struc_mat)$
% computes Poisson bracket for arbitrary expressions F,G
% using the Poisson structure matrix poi_struc_mat
for each h in poi_struc_mat sum (df(F,first h)*df(G,second h)-
                                 df(G,first h)*df(F,second h) )*(third h)$
%----------------------------------------------------

algebraic procedure poisson_v(F,G,poi_struc_mat)$
% computes the numerator of the Poisson bracket for vector expressions F,G
% using the Poisson structure matrix poi_struc_mat.
% It uses the global variables v_ (algebraic list of vectors) and gbase_ (an
% algebraic Groebner basis of the identities between vectors v_).
% If the poisson bracket for the dynamical variables (the operator poi_) is
% not yet computed then they are initially computed in component form and for
% that this procedure needs all relations between identifiers in the structure
% matrix (like in the default settings kap) assigned in component form,
% e.g. kap:=-a1**2-a2**2-a3**2$
begin scalar h,r,s,alle$
 alle:=reverse genpro(v_)$
 torder(alle,lex)$
 lisp(!!arbint:=0)$
 h:=for each r in alle sum for each s in alle sum
    if r=s then <<poi_(r,s):=0;0>>
           else <<if (arglength poi_(r,s) = 2) and
                     (part(poi_(r,s),0) = poi_) then
                  poi_(r,s):=for each h in c2sl poisson_c(v2c r,v2c s,
                                                          poi_struc_mat)
                             sum first h$
                  poi_(r,s)*df(F,r)*df(G,s)>>$

 r:=preduce(num h,gbase_);
 return
 r
 % The following was commented out as computing the quotient r/s
 % can take too much memory, much more than needed for solving r=0 later
 %
 % if den h neq 1 then <<
 %  s:=preduce(den h,gbase_);
 %  if s=0 then write"Error: Poisson bracket has zero denominator!"
 %         else r/s
 % >>             else r
end$

% Typical use:
%
% hh:={poisson_v(F,G,
%                {{U1,U2, U3}, {U2,U3, U1}, {U3,U1, U2},
%                 {U1,V2, V3}, {U2,V3, V1}, {U3,V1, V2},
%                 {U1,V3,-V2}, {U2,V1,-V3}, {U3,V2,-V1},
%                 {V1,V2, kap*U3}, {V2,V3, kap*U1}, {V3,V1, kap*U2}})};
% for each g in genpro {a,b,u,v} do
% hh:=for each h in hh join coeff(h,g)$
%----------------------------------------------------

symbolic procedure symaddweights(vwghts,exl)$
% vweights is an algebraic list like {{a,1,0,0},{b,0,1,0},{u,0,0,1},{v,1,0,1}}
% exl is a list of homogeneous standard vector expressions
% The procedure returns a lisp list of lisp lists like vweights,
% only with the exl-expressions instead of vectors as first elements of
% the sub-lists.

begin scalar h,g,k,wg,p,v,n$
 return
 for each h in cdr exl collect <<
  h:=reval h;
  g:=t1 h$
  k:=get_vlist_of_term g$ % k is an assoc list of all vectors with
                          % their multiplicity
  wg:=for each p in cddadr vwghts collect 0$
  while k do <<
   p:=car k; k:=cdr k;
   v:=cdr vwghts;
   while v and cadar v neq car p do v:=cdr v;
   v:=cddar v;     % v is the weight list of car p
   for n:=1:cdr p do wg:=addvectowg(wg,v)
  >>$

  cons(h,wg)
 >>
end$
%----------------------------------------------------

symbolic operator addweights$
symbolic procedure addweights(vwghts,exl)$
% The same as symaddweights, only returning an algebraic list of alg. lists
cons('LIST,for each h in symaddweights(vwghts,exl) collect cons('LIST,h))$
%----------------------------------------------------

lisp(nfct_:=1)$  % defined in crack

symbolic operator gFI$
symbolic procedure gFI(wgcp,alle,fdep,heads)$
begin scalar k,g,h,p,rtn,dropped1,dropped2,f,fl,gt1$
  % - If one wants a specific factor then this factor has to be multiplied
  %   afterwards and gfi has to be given appropriate lowered weights
  % - dropped1 is the number of terms dropped as they can be replaced
  %   using products of powers of Casimirs, Hamiltonians and known
  %   first integrals.
  % - dropped2 is the number of terms dropped due to general relationships
  %   of 3-component vectors.

  % At first we generate all possible terms with proper multi-weight
  wgths_:=cdr wgcp$
  fino_:=nil$
  gen(for each h in cdr reval algebraic genpro_wg alle collect cdr h);

  % Then we drop all terms that are reducible due to identities
  dropped2:=0;
  while fino_ do <<
   h:=reval car fino_; fino_:=cdr fino_;
   if algebraic is_reduceable(lisp h,heads) then dropped2:=add1 dropped2
                                            else rtn:=cons(h,rtn)
  >>$

  % Now we drop all terms that could be killed through expressions
  % functionally dependent on expressions in fdep

  % 1. finding and adding the weight lists producing a lisp list of lisp lists
  fdep:=symaddweights(alle,fdep)$

  dropped1:=0;
  fino_:=nil$
  gen fdep;
  for each h in fino_ do << % i.e. for each functionally dependent expression
   h:=reval h$
   h:=if not pairp h then list h else
      if (car h='PLUS) or (car h='DIFFERENCE) then cdr h else list h;
   gt1:=nil$

   while h do <<
    g:=car h$ h:=cdr h$ % g is one term of the funct. dep. expression
    % drop minus sign
    if pairp g and car g='MINUS then g:=cadr g;
    % drop numerical denominator
    if pairp g and car g='QUOTIENT and numberp caddr g then g:=cadr g;
    % drop numerical factor
    if pairp g and car g='TIMES then <<
     p:=nil;
     for each k in cdr g do % i.e. for each factor
     if numberp k or
        (pairp k and
         car k = 'QUOTIENT and
         numberp cadr k and
         numberp caddr k) then p:=cons(k,p);
     if p then <<
      if cdr p then p:=cons('TIMES,p)
               else p:=car p;
      g:=reval {'QUOTIENT,g,p}
     >>
    >>$
    if null member(g,rtn) then <<gt1:=nil; % hcp is not contained in ansatz
                                 h:=nil>>  % to stop looking at further terms
                          else if null gt1 then gt1:=g
   >>$ % all terms of h have been looked at
   if gt1 then << % funct. dep. expression is still in the ansatz,
                  % --> drop gt1 from ansatz
    dropped1:=add1 dropped1$
    rtn:=delete(gt1,rtn)
   >>$

  >>$

  % The remaining terms get an undetermined coefficient and are added up
  fino_:=rtn$  rtn:=nil$
  while fino_ do <<
   h:=reval car fino_; fino_:=cdr fino_;
   f:=mkid('!&r,nfct_)$
   nfct_:=add1 nfct_$
   fl:=cons(f,fl)$
   rtn:=cons({'TIMES,f,h},rtn)
  >>$

  if tr_vec then
  write"dropped: ",dropped1,"+",dropped2," kept: ",length rtn$
  return reval
         cons('LIST,if null rtn then nil else
                    cons(if cdr rtn then cons('PLUS,rtn)
                                    else car rtn,
                         fl))
end$
%----------------------------------------------------

algebraic procedure gpi(wgl,vl,gbase)$
% subroutine to generate polynomial identities between vectors
% as used by vinit().
begin scalar hh,vcl,id,fl,g,h$
 hh:=lisp(cons('LIST,for each h in std_wg(cdr vl) collect
                     cons('LIST,h)));
 hh:=gfi(wgl,hh,{},{});
 if hh neq {} then <<
  ID:=first hh$
  FL:=rest hh$
  hh:={v2c ID}$
  vcl:=for each h in vl join lisp cons('LIST,vc reval h);
  for each g in vcl do
  hh:=for each h in hh join coeff(h,g)$
  lisp(!!arbint:=0)$
  hh:=solve(hh,fl)$
  ID:=sub(first hh,ID);
  ID:=for h:=1:lisp(!!arbint) collect num coeffn(ID,arbcomplex(h),1);

  for each h in ID do
  if not fixp h then
  if gbase={} then gbase:={h}
              else <<
   h:=preduce(h,gbase);
   if h neq 0 then gbase:=groebner(cons(h,gbase))
  >>
 >>;
 return gbase
end$
%----------------------------------------------------

algebraic procedure vinit(alle)$
% generates all polynomial identities gbase_ for all scalar and triple
% products from the 4 vectors in the list alle
begin scalar i,j,k,l,natbak$
 v_:=alle$
 torder(reverse genpro v_,lex)$
 gbase_:={};
 on gltbasis$
 for i:=0:3 do  % here for exactly 4 vectors
 for j:=0:3 do
 for k:=0:3 do
 for l:=0:3 do
 if ((i+j+k+l)> 0) and
    ((i+j+k+l)<11) then gbase_:=gpi({i,j,k,l},v_,gbase_)$
 heads_:=gltp$
 on nat
end$
%----------------------------------------------------

symbolic procedure wg_li(a,vlw)$
% checks whether expression 'a' is homogeneous wrt to vlw which
% is a list of variables with weights, e.g.
%        vlw=((A 1 0 0) (B 0 1 0) (U 0 0 1) (V 1 0 1))$
% and in this case return the weightlist of 'a'
% It assumes the expression is a polynomial in the vectors, so any
% denominator is disregarded.
begin scalar wg,at1,h,wl,n,vlwcp,errorcd,m$
 a:=reval a$
 if pairp a and car a = 'QUOTIENT then a:=cadr a$
 a:=if pairp a and car a = 'PLUS then cdr a
                                 else list a$

 repeat <<
  at1:=car a$  a:=cdr a$
  h:=get_vlist_of_term(at1)$  % e.g.   ((a . 1) (b . 1) (u . 2))

  % to get total weightlist wl of the term el1 add for each vector its
  % weightlist times its multiplicity
  wl:=for n:=1:(length cdar vlw) collect 0;
  while h do <<
   vlwcp:=vlw$
   while vlwcp and ((caar vlwcp) neq (caar h)) do vlwcp:=cdr vlwcp;
   if null vlwcp then <<errorcd:=1;
                        write"Unspecified vector ",caar h," found!"$
                        terpri()>>
                 else for m:=1:(cdar h) do wl:=addvectowg(wl,cdar vlwcp)$
                      % adds weightlist of caar h to the vector wl
   h:=cdr h
  >>$

  if null wg then wg:=wl else
  if wg neq wl then <<errorcd:=2;write"Expression is inhomogeneous!"$terpri()>>
 >> until null a or errorcd;
 return
 if errorcd then nil
            else wg
end$
%----------------------------------------------------

symbolic operator fnc_dep$
symbolic procedure fnc_dep(a,li,vlw)$
% investigates whether 'a' is functionally
% independent of the elements of the list li or not.
% vlw is a list of variables with weights, e.g.
%        vlw={{A,1,0,0},{B,0,1,0},{U,0,0,1},{V,1,0,1}}$
% It uses the global variables v_ (algebraic list of vectors) and gbase_ (an
% algebraic Groebner basis of the identities between vectors v_).
begin scalar el,h,subli1,subli2,g,para,f,cnd,fl,ansatz,pl,n$

 vlw:=for each el in cdr vlw collect cdr el;
 % check homogeneity of 'a' and each element el of li and
 % prepare a weight list for 'a' and el
 wgths_:=wg_li(a,vlw)$

 h:=for each g in wgths_ sum g;
 if zerop h then return nil;

 n:=0;
 for each el in cdr li do <<
  h:=gensym()$
  subli1:=cons({'EQUAL,h,el},subli1);
  n:=add1 n$
  subli2:=cons({'EQUAL,h,mkid('p_,n)},subli2);
  g:=wg_li(el,vlw)$
  para:=cons(cons(h,g),para)
 >>$
 subli1:=cons('LIST,subli1)$
 subli2:=cons('LIST,subli2)$

 fino_:=nil;gen para;

 if null fino_ then return nil$
 % write"wgths_=",wgths_$terpri()$
 % write"subli1=",subli1$  terpri()$
 % write"subli2=",subli2$  terpri()$
 % write"para=",para$    terpri()$

 while fino_ do <<
  h:=reval car fino_; fino_:=cdr fino_;
  f:=mkid('!&r,nfct_)$
  nfct_:=add1 nfct_$
  fl:=cons(f,fl)$
  ansatz:=cons({'TIMES,f,h},ansatz)
 >>$
 fl:=cons('LIST,fl)$
 ansatz:=cons('PLUS,ansatz);
 cnd:=reval {'DIFFERENCE,a,algebraic(sub(subli1,lisp ansatz))};

 return
 algebraic <<

  pl:=reverse genpro(v_)$
  torder(pl,lex)$
  cnd:={preduce(cnd,gbase_)}$
  for each g in pl do
  cnd:=for each h in cnd join coeff(h,g)$

  h:=solve(cnd,fl)$

  if h neq {} then <<
   lisp <<
    write "The expression in question is functionally dependent on"$
    terpri()$
    write "the list of expressions {p_1,p_2,...} in the following way:"$
   >>$
   write sub(subli2,sub(first h,ansatz));
   t
  >>          else nil
 >>

end$
%----------------------------------------------------

algebraic  <<
v_:={a,b,u,v}$

torder(reverse genpro v_,lex)$

gbase_ :={ - bb*uu*vv + bb*uv**2 + bu**2*vv - 2*bu*bv*uv + buv**2 + bv**2*uu,
 - ab*uu*vv + ab*uv**2 + au*bu*vv - au*bv*uv + auv*buv - av*bu*uv + av*bv*uu,
 - ab*bu*vv + ab*bv*uv + abv*buv + au*bb*vv - au*bv**2 - av*bb*uv + av*bu*bv,
 - ab*bu*uv + ab*bv*uu + abu*buv + au*bb*uv - au*bu*bv - av*bb*uu + av*bu**2,
 - abu*vv + abv*uv - auv*bv + av*buv,
 - abu*uv + abv*uu + au*buv - auv*bu,
ab*buv - abu*bv + abv*bu - auv*bb,
aa*buv - ab*auv - abu*av + abv*au,
 - aa*uu*vv + aa*uv**2 + au**2*vv - 2*au*av*uv + auv**2 + av**2*uu,
 - aa*bu*vv + aa*bv*uv + ab*au*vv - ab*av*uv + abv*auv - au*av*bv + av**2*bu,
 - aa*bu*uv + aa*bv*uu + ab*au*uv - ab*av*uu + abu*auv - au**2*bv + au*av*bu,
abu*au*vv - abu*av*uv - abv*au*uv + abv*av*uu + au*auv*bv - auv*av*bu,
ab*abu*vv - ab*abv*uv + ab*auv*bv - abu*av*bv + abv*av*bu - auv*av*bb,
aa*abu*vv - aa*abv*uv + aa*auv*bv - ab*auv*av - abu*av**2 + abv*au*av,
ab*abu*uv - ab*abv*uu + ab*auv*bu - abu*au*bv + abv*au*bu - au*auv*bb,
aa*abu*uv - aa*abv*uu + aa*auv*bu - ab*au*auv - abu*au*av + abv*au**2,
aa*abu*bv - aa*abv*bu + aa*auv*bb - ab**2*auv - ab*abu*av + ab*abv*au,
 - aa*bb*vv + aa*bv**2 + ab**2*vv - 2*ab*av*bv + abv**2 + av**2*bb,
 - aa*bb*uv + aa*bu*bv + ab**2*uv - ab*au*bv - ab*av*bu + abu*abv + au*av*bb,
 - ab*abu*bu*vv + ab*abu*bv*uv + ab*abv*bu*uv - ab*abv*bv*uu + abu*au*bb*vv -
abu*au*bv**2 - abu*av*bb*uv + abu*av*bu*bv - abv*au*bb*uv + abv*au*bu*bv +
abv*av*bb*uu - abv*av*bu**2,
 - aa*abu*bu*vv + aa*abu*bv*uv + aa*abv*bu*uv - aa*abv*bv*uu + ab*abu*au*vv -
ab*abu*av*uv - ab*abv*au*uv + ab*abv*av*uu - abu*au*av*bv + abu*av**2*bu +
abv*au**2*bv - abv*au*av*bu,
 - aa*abu*bb*vv + aa*abu*bv**2 + aa*abv*bb*uv - aa*abv*bu*bv + ab**2*abu*vv -
ab**2*abv*uv - 2*ab*abu*av*bv + ab*abv*au*bv + ab*abv*av*bu + abu*av**2*bb -
abv*au*av*bb,
 - aa*abu*bb*uv + aa*abu*bu*bv + aa*abv*bb*uu - aa*abv*bu**2 + ab**2*abu*uv -
ab**2*abv*uu - ab*abu*au*bv - ab*abu*av*bu + 2*ab*abv*au*bu + abu*au*av*bb -
abv*au**2*bb,
 - aa*bb*uu + aa*bu**2 + ab**2*uu - 2*ab*au*bu + abu**2 + au**2*bb,
aa*bb*uu*vv - aa*bb*uv**2 - aa*bu**2*vv + 2*aa*bu*bv*uv - aa*bv**2*uu -
ab**2*uu*vv + ab**2*uv**2 + 2*ab*au*bu*vv - 2*ab*au*bv*uv - 2*ab*av*bu*uv +
2*ab*av*bv*uu - au**2*bb*vv + au**2*bv**2 + 2*au*av*bb*uv - 2*au*av*bu*bv -
av**2*bb*uu + av**2*bu**2}$

heads_ :=
{buv**2,auv*buv,abv*buv,abu*buv,av*buv,au*buv,ab*buv,aa*buv,auv**2,abv*auv,
abu*auv,au*auv*bv,ab*auv*bv,aa*auv*bv,ab*auv*bu,aa*auv*bu,aa*auv*bb,abv**2,abu
*abv,ab*abv*bu*uv,aa*abv*bu*uv,aa*abv*bb*uv,aa*abv*bb*uu,abu**2,aa*bb*uu*vv}$

>>;

% kap:=!&eta**2$
% kap:=-a1**2-a2**2-a3**2$
%
% so(4),so(3,1),e(3)
% STRUC_CONS:={{U1,U2, U3}, {U2,U3, U1}, {U3,U1, U2},
%              {U1,V2, V3}, {U2,V3, V1}, {U3,V1, V2},
%              {U1,V3,-V2}, {U2,V1,-V3}, {U3,V2,-V1},
%              {V1,V2, kap*U3}, {V2,V3, kap*U1}, {V3,V1, kap*U2}}$
%
% so(3):
% STRUC_CONS:={{U1,U2, U3}, {U2,U3, U1}, {U3,U1, U2}}$
%
% e(3):
% STRUC_CONS:={{U1,U2, U3}, {U2,U3, U1}, {U3,U1, U2},
%              {U1,V2, V3}, {U2,V3, V1}, {U3,V1, V2},
%              {U1,V3,-V2}, {U2,V1,-V3}, {U3,V2,-V1} }$
%
% so(4):
% STRUC_CONS:={{U1,U2, U3}, {U2,U3, U1}, {U3,U1, U2},
%              {U1,V2, V3}, {U2,V3, V1}, {U3,V1, V2},
%              {U1,V3,-V2}, {U2,V1,-V3}, {U3,V2,-V1},
%              {V1,V2, U3}, {V2,V3, U1}, {V3,V1, U2} }$
%
% so(3,1):
% STRUC_CONS:={{U1,U2, U3}, {U2,U3, U1}, {U3,U1, U2},
%              {U1,V2, V3}, {U2,V3, V1}, {U3,V1, V2},
%              {U1,V3,-V2}, {U2,V1,-V3}, {U3,V2,-V1},
%              {V1,V2,-U3}, {V2,V3,-U1}, {V3,V1,-U2} }$
%
% so(3)^2:
% STRUC_CONS:={{U1,U2, U3}, {U2,U3, U1}, {U3,U1, U2},
%              {V1,V2, V3}, {V2,V3, V1}, {V3,V1, V2} }$

end$
