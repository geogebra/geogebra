module groeb;

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


COMMENT

              ##############################
              ##                          ##
              ##      GROEBNER PACKAGE    ##
              ##                          ##
              ##############################

This is now a common package, covering both the noetherian and the
local term orders.

The trace intensity can be managed with cali_trace() by the following
rules :

      cali_trace() >=   0      no trace
                        2      show actual step
                       10      show input and output
                       20      show new base elements
                       30      show pairs
                       40      show actual pairlist
                       50      show S-polynomials

Pair lists have the following informal syntax :

      <spairlist>::= list of spairs
      < spair >  ::= (komp  groeb!=weight  lcm  p_i  p_j)
               with lcm = lcm(lt(bas_dpoly p_i),lt(bas_dpoly p_j)).


The pair selection strategy is by first matching in the pair list.
It can be changed overloading groeb!=better, the relation according to
what pair lists are sorted. Standard is the sugar strategy.

cali!=monset :

One can manage a list of variables, that are allowed to be canceled
out, if they appear as common factors in a dpoly. This is possible if
these variables are non zero divisors (e.g. for prime ideals) and
affects "pure" Groebner basis computation only.

END COMMENT;


% ############   The outer Groebner engine   #################

put('cali,'groeb!=rf,'groeb!=rf1); % First initialization.

symbolic operator gbtestversion;
symbolic procedure gbtestversion n; % Choose the corresponding driver
  if member(n,{1,2,3}) then
        put('cali,'groeb!=rf,mkid('groeb!=rf,n));

symbolic procedure groeb!=postprocess pol;
% Postprocessing for irreducible H-Polynomials. The switches got
% appropriate local values in the Groebner engine.
  begin
  if !*bcsimp then pol:=car bas_simpelement pol;
  if not !*noetherian then
        if !*factorunits then pol:=bas_factorunits pol
        else if !*detectunits then pol:=bas_detectunits pol;
  if cali!=monset then pol:=bas_make(bas_nr pol,
                      car dp_mondelete(bas_dpoly pol,cali!=monset));
  return pol
  end;

symbolic procedure groeb_stbasis(bas,comp_mgb,comp_ch,comp_syz);
  groeb!=choose_driver(bas,comp_mgb,comp_ch,comp_syz,
                                function groeb!=generaldriver);

symbolic procedure
        groeb!=choose_driver(bas,comp_mgb,comp_ch,comp_syz,driver);
% Returns { mgb , change , syz } with
%       dpmat mgb = (if comp_mgb=true the minimal)
%               Groebner basis of the dpmat bas.
%       dpmat change defined by   mgb = change * bas
%               if comp_ch = true.
%       dpmat syz = (not interreduced) syzygy matrix of the dpmat bas
%               if comp_syz = true.
% Changes locally !*factorunits, !*detectunits and cali!=monset.

   if dpmat_zero!? bas then
       {bas,dpmat_unit(dpmat_rows bas,nil),
            dpmat_unit(dpmat_rows bas,nil)}
   else (begin scalar u, gb, syz, change, syz1;

        % ------- Syzygies for the zero base elements.
   if comp_syz then
   << u:=setdiff(for i:=1:dpmat_rows bas collect i,
                for each x in
                bas_zerodelete dpmat_list bas collect bas_nr x);
      syz1:=for each x in u collect bas_make(0,dp_from_ei x);
   >>;

        % ------- Initialize the Groebner computation.
   gb:=bas_zerodelete dpmat_list bas;
                % makes a copy (!) of the base list.
   if comp_ch or comp_syz then
   << !*factorunits:=!*detectunits:=cali!=monset:=nil;
      bas_setrelations gb;
   >>;
   if cali_trace() > 5 then
      << terpri(); write" Compute GBasis of"; bas_print gb >>
   else if cali_trace() > 0 then
      << terpri(); write" Computing GBasis  ";terpri() >>;
   u:=apply(driver,{dpmat_rows bas,dpmat_cols bas,gb,comp_syz});
   syz:=second u;
   if comp_mgb then
        << u:=groeb_mingb car u;
           if !*red_total then
                u:=dpmat_make(dpmat_rows u,dpmat_cols u,
                            red_straight dpmat_list u,
                            cali!=degrees,t);
        >>
   else u:=car u;
   cali!=degrees:=dpmat_rowdegrees bas;
   if comp_ch then
       change:=dpmat_make(dpmat_rows u,dpmat_rows bas,
                bas_neworder bas_getrelations dpmat_list u,
                cali!=degrees,nil);
   bas_removerelations dpmat_list u;
   if comp_syz then
   << syz:=nconc(syz,syz1);
      syz:= dpmat_make(length syz,dpmat_rows bas,
                bas_neworder bas_renumber syz,cali!=degrees,nil);
   >>;
   cali!=degrees:=dpmat_coldegs u;
   return {u,change,syz}
   end) where cali!=degrees:=dpmat_coldegs bas,
                !*factorunits:=!*factorunits,
                !*detectunits:=!*detectunits,
                cali!=monset:=cali!=monset;

% #########   The General Groebner driver ###############

Comment

It returns {gb,syz,trace}  with change on the relation part of gb,
where
  INPUT  : r, c, gb = rows, columns, base list
  OUTPUT :
       <dpmat> gb is the Groebner basis
       <base list> syz is the dpmat_list of the syzygy matrix
       <spairlist> trace is the Groebner trace.

There are three different versions of the general driver that branche
according to a reduction function
        rf : {pol,simp} |---> {pol,simp}
found with get('cali,'groeb!=rf):

1. Total reduction with local simplifier lists. For local term orders
        this is (almost) Mora's first version for the tangent cone.

2. Total reduction with global simplifier list. For local term orders
        this is (almost) Mora's SimpStBasis.

3. Total reduction with bounded ecart. This needs no extra simplifier
        list.

end Comment;

symbolic procedure groeb!=generaldriver(r,c,gb,comp_syz);
  begin scalar u, q, syz, p, pl, pol, trace, return_by_unit,
                simp, rf, Ccrit;
    Ccrit:=(not comp_syz) and (c<2); % don't reduce main syzygies
    simp:=sort(listminimize(gb,function red!=cancelsimp),
                function red_better);
    pl:=groeb_makepairlist(gb,Ccrit);
    rf:=get('cali,'groeb!=rf);
    if cali_trace() > 30 then groeb_printpairlist pl;
    if cali_trace() > 5 then
        <<terpri(); write" New base elements :";terpri() >>;

    % -------- working out pair list
    while pl and not return_by_unit do
    << % ------- Choose a pair
       p:=car pl; pl:=cdr pl;

       % ------ compute S-polynomial (which is a base element)
       if cali_trace() > 10 then groeb_printpair(p,pl);
       u:=apply2(rf,groeb_spol p,simp);
       pol:=first u; simp:=second u;
       if cali_trace() > 70 then
       << terpri(); write" Reduced S.-pol. : ";
          dp_print2 bas_dpoly pol
       >>;

       if bas_dpoly pol then
              % --- the S-polynomial doesn't reduce to zero
       << pol:=groeb!=postprocess pol;
          r:=r+1;
          pol:=bas_newnumber(r,pol);

                   % --- update the tracelist
          q:=bas_dpoly pol;
          trace:=list(groeb!=i p,groeb!=j p,r,dp_lmon q) . trace;

          if cali_trace() > 20 then
            << terpri(); write r,". ---> "; dp_print2 q >>;
          if Ccrit and (dp_unit!? q) then return_by_unit:=t;

                   % ----- update
          if not return_by_unit then
          << pl:=groeb_updatePL(pl,gb,pol,Ccrit);
             if cali_trace() > 30 then
                << terpri(); groeb_printpairlist pl >>;
              gb:=pol.gb;
              simp:=red_update(simp,pol);
          >>;
       >>

       else % ------ S-polynomial reduces to zero
       if comp_syz then
             syz:=car bas_simpelement(bas_make(0,bas_rep pol)) . syz
    >>;

    % --------  updating the result
    if cali_trace()>0 then
    << terpri(); write " Simplifier list has length ",length simp >>;
    if return_by_unit then return
        % --- no syzygies are to be computed
        {dpmat_from_dpoly pol,nil,reversip trace};
    gb:=dpmat_make(length gb,c,gb,cali!=degrees,t);
    return {gb,syz,reversip trace}
    end;

% --- The different reduction functions.

symbolic procedure groeb!=rf1(pol,simp); {red_TotalRed(simp,pol),simp};

symbolic procedure groeb!=rf2(pol,simp);
  if (null bas_dpoly pol) or (null simp) then {pol,simp}
  else begin scalar v,q;

        % Make first reduction with bounded ecart.
     pol:=red_TopRedBE(simp,pol);

        % Now loop into reduction with minimal ecart.
     while (q:=bas_dpoly pol) and (v:=red_divtest(simp,dp_lmon q)) do
         << v:=red_subst(pol,v);
                % Updating the simplifier list could make sense even
                % for the noetherian case, since it is a global list.
            simp:=red_update(simp,pol);
            pol:=red_TopRedBE(simp,v);
         >>;

        % Now make tail reduction
     if !*red_total and bas_dpoly pol then pol:=red_TailRed(simp,pol);
     return {pol,simp};
     end;

symbolic procedure groeb!=rf3(pol,simp);
% Total reduction with bounded ecart.
  if (null bas_dpoly pol) or (null simp) then {pol,simp}
  else begin
     pol:=red_TopRedBE(simp,pol);
     if bas_dpoly pol then
        pol:=red_TailRedDriver(simp,pol,function red_TopRedBE);
     return {pol,simp};
     end;

% #########   The Lazy Groebner driver ###############

Comment

The lazy groebner driver implements the lazy strategy for local
standard bases, i.e. stepwise reduction of S-Polynomials according to
a refinement of the (ascending) division order on leading terms.

end Comment;


symbolic procedure groeb_lazystbasis(bas,comp_mgb,comp_ch,comp_syz);
  groeb!=choose_driver(bas,comp_mgb,comp_ch,comp_syz,
                                function groeb!=lazydriver);

symbolic procedure groeb!=lazymocompare(a,b);
% A dpoly with leading monomial a should be processed before dpolys
% with leading monomial b.
  mo_ecart a < mo_ecart b;

symbolic procedure groeb!=queuesort(a,b);
% Sort criterion for the queue.
  groeb!=lazymocompare(dp_lmon bas_dpoly a,dp_lmon bas_dpoly b);

symbolic procedure groeb!=nextspol(pl,queue);
% True <=> take first pl next.
  if null queue then t
  else if null pl then nil
  else groeb!=lazymocompare(nth(car pl,3),dp_lmon bas_dpoly car queue);

symbolic procedure groeb!=lazydriver(r,c,gb,comp_syz);
% The lazy version of the driver.
  begin scalar syz, Ccrit, queue, v, simp, p, pl, pol, return_by_unit;
    simp:=sort(listminimize(gb,function red!=cancelsimp),
                function red_better);
    Ccrit:=(not comp_syz) and (c<2); % don't reduce main syzygies
    pl:=groeb_makepairlist(gb,Ccrit);
    if cali_trace() > 30 then groeb_printpairlist pl;
    if cali_trace() > 5 then
        <<terpri(); write" New base elements :";terpri() >>;

    % -------- working out pair list

    while (pl or queue) and not return_by_unit do
      if groeb!=nextspol(pl,queue) then
      << p:=car pl; pl:=cdr pl;
         if cali_trace() > 10 then groeb_printpair(p,pl);
         pol:=groeb_spol p;
         if bas_dpoly pol then % back into the queue
             if Ccrit and dp_unit!? bas_dpoly pol then
                        return_by_unit:=t
             else queue:=merge(list pol, queue,
                                    function groeb!=queuesort)
         else if comp_syz then % pol reduced to zero.
                syz:=bas_simpelement bas_make(0,bas_rep pol).syz;
      >>
      else
      << pol:=car queue; queue:=cdr queue;
           % Try one top reduction step
         if (v:=red_divtestBE(simp,dp_lmon bas_dpoly pol,
                        bas_dpecart pol)) then ()
                % do nothing with simp !
         else if (v:=red_divtest(simp,dp_lmon bas_dpoly pol)) then
                simp:=red_update(simp,pol);
           % else v:=nil;
         if v then % do one top reduction step
         << pol:=red_subst(pol,v);
            if bas_dpoly pol then % back into the queue
                queue:=merge(list pol, queue,
                                function groeb!=queuesort)
            else if comp_syz then % pol reduced to zero.
                 syz:=bas_simpelement bas_make(0,bas_rep pol).syz;
         >>
         else % no reduction possible
         << % make a tail reduction with bounded ecart and the
            % usual postprocessing :
            pol:=groeb!=postprocess
                if !*red_total then
                red_TailRedDriver(gb,pol,function red_TopRedBE)
                else pol;
            if dp_unit!? bas_dpoly pol then return_by_unit:=t
            else % update the computation
            << r:=r+1; pol:=bas_newnumber(r,pol);
               if cali_trace() > 20 then
               << terpri(); write r,". --> "; dp_print2 bas_dpoly pol>>;
               pl:=groeb_updatePL(pl,gb,pol,Ccrit);
               simp:=red_update(simp,pol);
               gb:=pol.gb;
            >>
         >>
      >>;

     % --------  updating the result

    if cali_trace()>0 then
    << terpri(); write " Simplifier list has length ",length simp >>;
    if return_by_unit then return {dpmat_from_dpoly pol,nil,nil}
    else return
        {dpmat_make(length simp,c,simp,cali!=degrees,t), syz, nil}
    end;

% ################  The Groebner Tools ##############

% ---------- Critical pair criteria -----------------------

symbolic procedure groeb!=critA(p);
% p is a pair list {(i.k):i running} of pairs with equal module
% component number. Choose those pairs among them that are minimal wrt.
% division order on lcm(i.k).
  listminimize(p,function groeb!=testA);

symbolic procedure groeb!=testA(p,q); mo_divides!?(nth(p,3),nth(q,3));

symbolic procedure groeb!=critB(e,p);
% Delete pairs from p, for which testB is false.
  for each x in p join if not groeb!=testB(e,x) then {x};

symbolic procedure groeb!=testB(e,a);
% e=lt(f_k). Test, whether for a=pair (i j)
% komp(a)=komp(e) and Syz(i,j,k)=[ 1 * * ].
    (mo_comp e=car a)
    and mo_divides!?(e,nth(a,3))
    and (not mo_equal!?(mo_lcm(dp_lmon bas_dpoly nth(a,5),e),
                        nth(a,3)))
    and (not mo_equal!?(mo_lcm(dp_lmon bas_dpoly nth(a,4),e),
                        nth(a,3)));

symbolic procedure groeb!=critC(p);
% Delete main syzygies.
  for each x in p join if not groeb!=testC1 x then {x};

symbolic procedure groeb!=testC1 el;
    mo_equal!?(
        mo_sum(dp_lmon bas_dpoly nth(el,5),
               dp_lmon bas_dpoly nth(el,4)),
        nth(el,3));

symbolic procedure groeb_updatePL(p,gb,be,Ccrit);
% Update the pairlist p with the new base element be and the old ones
% in the base list gb. Discard pairs where both base elements have
% number part 0.
    begin scalar p1,k,a,n; n:=(bas_nr be neq 0);
    a:=dp_lmon bas_dpoly be; k:=mo_comp a;
    for each b in gb do
        if (k=mo_comp dp_lmon bas_dpoly b)
                and(n or (bas_nr b neq 0)) then
                        p1:=groeb!=newpair(k,b,be).p1;
    p1:=groeb!=critA(sort(p1,function groeb!=better));
    if Ccrit then p1:=groeb!=critC p1;
    return
        merge(p1,
              groeb!=critB(a,p), function groeb!=better);
    end;

symbolic procedure groeb_makepairlist(gb,Ccrit);
    begin scalar newgb,p;
    while gb do
    << p:=groeb_updatePL(p,newgb,car gb,Ccrit);
       newgb:=car gb .  newgb; gb:=cdr gb
    >>;
    return p;
    end;

% -------------- Pair Management --------------------

symbolic procedure groeb!=i p; bas_nr nth(p,4);

symbolic procedure groeb!=j p; bas_nr nth(p,5);

symbolic procedure groeb!=better(a,b);
% True if the Spair a is better than the Spair b.
    if (cadr a < cadr b) then t
    else if (cadr a = cadr b) then mo_compare(nth(a,3),nth(b,3))<=0
    else nil;

symbolic procedure groeb!=weight(lcm,p1,p2);
    mo_ecart(lcm) + min2(bas_dpecart p1,bas_dpecart p2);

symbolic procedure groeb!=newpair(k,p1,p2);
% Make an spair from base elements with common component number k.
    list(k,groeb!=weight(lcm,p1,p2),lcm, p1,p2)
        where lcm =mo_lcm(dp_lmon bas_dpoly p1,dp_lmon bas_dpoly p2);

symbolic procedure groeb_printpairlist p;
    begin
    for each x in p do
        << write groeb!=i x,".",groeb!=j x; print_lf  " | " >>;
    terpri();
    end;

symbolic procedure groeb_printpair(pp,p);
    begin terpri();
    write"Investigate (",groeb!=i pp,".",groeb!=j pp,")  ",
        "Pair list has length ",length p; terpri()
    end;

% ------------- S-polynomial constructions -----------------

symbolic procedure groeb_spol pp;
% Make an S-polynomial from the spair pp, i.e. return
% a base element with
%   dpoly = ( zi*mi*(red) pi - zj*mj*(red) pj )
%    rep  = (zi*mi*rep_i - zj*mj*rep_j),
%
%       where mi=lcm/lm(pi), mj=lcm/lm(pj)
%       and  zi and zj are appropriate scalars.
%
    begin scalar pi,pj,ri,rj,zi,zj,lcm,mi,mj,a,b;
      a:=nth(pp,4); b:=nth(pp,5); lcm:=nth(pp,3);
      pi:=bas_dpoly a; pj:=bas_dpoly b; ri:=bas_rep a; rj:=bas_rep b;
      mi:=mo_diff(lcm,dp_lmon pi); mj:=mo_diff(lcm,dp_lmon pj);
      zi:=dp_lc pj; zj:=bc_neg dp_lc pi;
      a:=dp_sum(dp_times_bcmo(zi,mi, cdr pi),
                dp_times_bcmo(zj,mj, cdr pj));
      b:=dp_sum(dp_times_bcmo(zi,mi, ri),
                dp_times_bcmo(zj,mj, rj));
      a:=bas_make1(0,a,b);
      if !*bcsimp then a:=car bas_simpelement a;
      if cali_trace() > 70 then
         << terpri(); write" S.-pol : "; dp_print2 bas_dpoly a >>;
      return a;
    end;

symbolic procedure groeb_mingb gb;
% Returns the min. Groebner basis dpmat mgb of the dpmat gb
% discarding base elements with bas_nr<=0.
   begin scalar u;
    u:=for each x in car red_collect dpmat_list gb join
                if bas_nr x>0 then {x};
        % Choosing base elements with minimal leading terms only.
    return dpmat_make(length u,dpmat_cols gb,bas_renumber u,
                    dpmat_coldegs gb,dpmat_gbtag gb);
    end;

% ------- Minimizing a basis using its syszgies ---------

symbolic procedure groeb!=delete(l,bas);
% Delete base elements from the base list bas with number in the
% integer list l.
    begin scalar b;
    while bas do
      << if not memq(bas_nr car bas,l) then b:=car bas . b;
         bas:= cdr bas
      >>;
    return reverse b
    end;

symbolic procedure groeb_minimize(bas,syz);
% Minimize the dpmat pair bas,syz deleting superfluous base elements
% from bas using syzygies from syz containing unit entries.
   (begin scalar drows, dcols, s,s1,i,j,p,q,y;
   cali!=degrees:=dpmat_coldegs syz;
   s1:=dpmat_list syz; j:=0;
   while j < dpmat_rows syz do
     << j:=j+1;
        if (q:=bas_dpoly bas_getelement(j,s1)) then
          << i:=0;
             while leq(i,dpmat_cols syz) and
                 (memq(i,dcols) or not dp_unit!?(p:=dp_comp(i,q)))
                        do i:=i+1;
             if leq(i,dpmat_cols syz) then
               << drows:=j . drows;
                  dcols:=i . dcols;
                  s1:=for each x in s1 collect
                     if memq(bas_nr x,drows) then x
                     else (bas_make(bas_nr x,
                        dp_diff(dp_prod(y,p),dp_prod(q,dp_comp(i,y))))
                           where y:=bas_dpoly x);
               >>
          >>
     >>;

   % --- s1 becomes the new syzygy part, s the new base part.

   s1:=bas_renumber bas_simp groeb!=delete(drows,s1);
   s1:=dpmat_make(length s1,dpmat_cols syz,s1,cali!=degrees,nil);
                        % The new syzygy matrix of the old basis.
   s:=dpmat_renumber
          dpmat_make(dpmat_rows bas,dpmat_cols bas,
                groeb!=delete(dcols,dpmat_list bas),
                dpmat_coldegs bas,nil);
   s1:=dpmat_mult(s1,dpmat_transpose cdr s);
        % The new syzygy matrix of the new basis, but not yet in the
        % right form since cali!=degrees is empty.
   s:=car s;            % The new basis.
   cali!=degrees:=dpmat_rowdegrees s;
   s1:=interreduce!* dpmat_make(dpmat_rows s1,dpmat_cols s1,
                bas_neworder dpmat_list s1,cali!=degrees,nil);
   return s.s1;
   end) where cali!=degrees:=cali!=degrees;

% ------ Computing standard bases via homogenization ----------------

symbolic procedure groeb_homstbasis(m,comp_mgb,comp_ch,comp_syz);
  (begin scalar v,c,u;
  c:=cali!=basering; v:=list make_cali_varname();
  if not(comp_ch or comp_syz) then cali!=monset:=append(v,cali!=monset);
  setring!* ring_sum(c,ring_define(v,nil,'lex,'(1)));
  cali!=degrees:=mo_degneworder dpmat_coldegs m;
  if cali_trace()>0 then print" Homogenize input ";
  u:=(groeb_stbasis(mathomogenize!*(m,car v),
                comp_mgb,comp_ch,comp_syz) where !*noetherian=t);
  if cali_trace()>0 then print" Dehomogenize output ";
  u:=for each x in u collect if x then matdehomogenize!*(x,car v);
  setring!* c; cali!=degrees:=dpmat_coldegs m;
  return {if first u then dpmat_neworder(first u,t),
                if second u then dpmat_neworder(second u,nil),
                if third u then dpmat_neworder(third u,nil)};
  end) where cali!=basering:=cali!=basering,
                cali!=monset:=cali!=monset,
                cali!=degrees:=cali!=degrees;


% Two special versions for standard basis computations, not included
% in full generality into the algebraic interface.

symbolic operator homstbasis;
symbolic procedure homstbasis m;
  if !*mode='algebraic then dpmat_2a homstbasis!* dpmat_from_a m
  else homstbasis!* m;

symbolic procedure homstbasis!* m;
  groeb_mingb car groeb_homstbasis(m,t,nil,nil);

symbolic operator lazystbasis;
symbolic procedure lazystbasis m;
  if !*mode='algebraic then dpmat_2a lazystbasis!* dpmat_from_a m
  else lazystbasis!* m;

symbolic procedure lazystbasis!* m;
  car groeb_lazystbasis(m,t,nil,nil);

endmodule;  % groeb

end;
