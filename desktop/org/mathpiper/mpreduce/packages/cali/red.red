module red;

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

                    #################
                    ##             ##
                    ## NORMAL FORM ##
                    ## ALGORITHMS  ##
                    ##             ##
                    #################

This module contains normal form algorithms for base elements. All
reductions executed on the dpoly part, are repeated on the rep part,
hence tracing them up for further use. We do pseudoreduction, but
organized following up the multipliers in a different way than in the
version 2.1 :

For total reduction we hide terms prefixing the current lead term on the
negative slots of the rep part. This allows not to follow up the
multipliers, since head terms are multiplied automatically.

If You nevertheless need the multipliers, You can prepare the base
elements with "red_prepare" to keep track of them using the 0-slot of
the rep-part :

        f --> (f,e_0) -NF-> (f',z*e_0) --> (f' . z)

Extract the multiplier back with "red_extract". This allows a unified
treating of the multipliers for both noetherian and non noetherian
term orders.

For     NF : [f,r] |--> [f',r']         using B={[f_i,r_i]}
with representation parts r, r_i we get

                f' = z*f + \sum a_i*f_i
                r' = z*r + \sum a_i*r_i

The output trace intensity can be managed with cali_trace() that has
the following meaning :

       cali_trace() >=  0      no trace
                       10      '.' for each substitution
                       70      trace interreduce!*
                       80      trace redpol
                       90      show substituents

The reduction strategy is first matching in the simplifier (base)
list. It can be changed overloading red_better, the relation
according to what base lists are sorted. Standard is minimal ecart,
breaking ties with minimal length (since such a strategy is good for
both the classical and the local case).

There are two (head) reduction functions, the usual one and one, that
allows reduction only by reducers with bounded ecart, i.e. where the
ecart of the reducer is leq the ecart of the poly to be reduced. This
allows a unified handling of noetherian and non-noetherian term orders.

Switches :

          red_total :   t      compute total normal forms
                        nil    reduce only until lt is standard
          bcsimp    :   t      apply bas_simp


END COMMENT;

% Standard is :

!*red_total:=t;
!*bcsimp:=t;

symbolic procedure red_better(a,b);
% Base list sort criterion. Simplifier lists are sorted such that the
% best substituent comes first. Due to reduction with bounded ecart we
% need no more lowest ecarts first.
  bas_dplen a < bas_dplen b;

% ---- Preparing data for collecting multipliers  ---

symbolic procedure red_prepare model;
% Prepare the zero rep-part to follow up multipliers
% in the pseudoreductions.
%         if !*binomial then model else
  bas_make1(bas_nr model,bas_dpoly model,
                dp_sum(bas_rep model,dp_from_ei 0));

symbolic procedure red_extract model;
% Returns (model . dpoly), extracting the multiplier part from the
% zero rep-part.
%    if !*binomial then (model . dp_fi 1) else
  (bas_make1(bas_nr model, bas_dpoly model,
                dp_diff(bas_rep model,z)) . z
     where z=dp_comp(0,bas_rep model));

% -------- Substitution operations ----------------

symbolic procedure red_subst(model,basel);
%  model and basel = base elements
%  Returns a base element, such that
%       pol_new := z * pol_old - z1 * mo * f_a
%       rep_new := z * rep_old - z1 * mo * rep_a
% with appropriate base coeff. z and z1 and monomial mo.

%  if !*binomial then red!=subst2(model,basel) else
  red!=subst1(model,basel);

symbolic procedure red!=subst1(model,basel);
    begin scalar polold,polnew,repold,repnew,gcd,mo,fa,z,z1;
        polold:=bas_dpoly model; z1:=dp_lc polold;
        repold:=bas_rep model;
        fa:=bas_dpoly basel; z:= dp_lc fa;
        if !*bcsimp then      % modify z and z1
          if (gcd:=bc_inv z) then
            << z1:=bc_prod(z1,gcd); z:=bc_fi 1 >>
          else
            << gcd:=bc_gcd(z,z1);
               z:=car bc_divmod(z,gcd);
               z1:=car bc_divmod(z1,gcd)
            >>;
        mo:=mo_diff(dp_lmon polold,dp_lmon fa);
        polnew:=dp_diff(dp_times_bc(z,polold),
                                dp_times_bcmo(z1,mo,fa));
        repnew:=dp_diff(dp_times_bc(z,repold),
                                dp_times_bcmo(z1,mo,bas_rep basel));
        if cali_trace() > 79 then
                << prin2 "---> "; dp_print polnew >>
        else if cali_trace() > 0 then prin2 ".";
        if cali_trace() > 89 then
                << prin2 " uses "; dp_print fa >>;
        return bas_make1(bas_nr model,polnew,repnew);
    end;

symbolic procedure red!=subst2(model,basel);
% Only for binomials without representation parts.
    begin scalar m,b,u,r;
    if cali_trace()>0 then prin2 ".";
    m:=bas_dpoly model; b:=bas_dpoly basel;
    if (length b neq 2) or bas_rep model then
            rederr"switch off binomial";
    u:=mo_qrem(dp_lmon m,dp_lmon b);
    r:=list dp_term(dp_lc m,
                mo_sum(mo_power(dp_lmon cdr b,car u),cdr u));
    return bas_make(bas_nr model,dp_sum(r,cdr m));
    end;

% ---------------- Top reduction ------------------------

symbolic procedure red_TopRedBE(bas,model);
% Takes a base element model and returns it top reduced with bounded
% ecart.
  if (null bas_dpoly model) or (null bas) then model
  else begin
     scalar v,q;
     if cali_trace()>79 then
       << write" reduce "; dp_print bas_dpoly model >>;
     while (q:=bas_dpoly model) and
                (v:=red_divtestBE(bas,dp_lmon q,bas_dpecart model)) do
                        model:=red_subst(model,v);
     return model;
     end;

symbolic procedure red_divtestBE(a,b,e);
% Returns the first f in the base list a, such that lt(f) | b
% and ec(f)<=e, else nil. b is a monomial.
  if null a then nil
  else if (bas_dpecart(car a) <= e) and
        mo_vdivides!?(dp_lmon bas_dpoly car a,b) then car a
  else red_divtestBE(cdr a,b,e);

symbolic procedure red_divtest(a,b);
% Returns the first f in the base list a, such that lt(f) | b else nil.
% b is a monomial.
  if null a then nil
  else if mo_vdivides!?(dp_lmon bas_dpoly car a,b) then car a
  else red_divtest(cdr a,b);

symbolic procedure red_TopRed(bas,model);
% Takes a base element model and returns it top reduced.
% For noetherian term orders this is the classical top reduction; no
% additional simplifiers occur. For local term orders it is Mora's
% reduction by minimal ecart.
  if (null bas_dpoly model) or (null bas) then model
  else begin
     scalar v,q;
        % Make first reduction with bounded ecart.
     model:=red_TopRedBE(bas,model);
        % Now loop into reduction with minimal ecart.
     while (q:=bas_dpoly model) and (v:=red_divtest(bas,dp_lmon q)) do
         << v:=red_subst(model,v);
            if not !*noetherian then bas:=red_update(bas,model);
            model:=red_TopRedBE(bas,v);
         >>;
     return model;
     end;

% Management of the simplifier list. Has a meaning only in the
% non noetherian case.

symbolic procedure red_update(simp,b);
% Update the simplifier list simp with the base element b.
    begin
    if cali_trace()>59 then
      << terpri(); write "[ec:",bas_dpecart b,"] ->";
         dp_print2 bas_dpoly b
      >>
    else if cali_trace()>0 then write"*";
    return merge(list b,
                for each x in simp join
                        if red!=cancelsimp(b,x) then nil else {x},
                function red_better);
    end;

symbolic procedure red!=cancelsimp(a,b);
% Test for updating the simplifier list.
  red_better(a,b) and
        mo_vdivides!?(dp_lmon bas_dpoly a,dp_lmon bas_dpoly b);

% ------------- Total reduction and Tail reduction -----------

Comment

For total reduction one has to organize recursive calls of TopRed on
tails of the current model. Since we do pseudoreduction, we have to
multiply the prefix terms with the multiplier during recursive calls.
We do that, hiding the prefix terms on rep part components with
negative component number. Retrival may be done not recursively, but
in a single step.

end Comment;

symbolic procedure red!=hide p;
% Hide the terms of the dpoly p. This is involutive !
  for each x in p collect (mo_times_ei(-1,mo_neg car x) . cdr x);

symbolic procedure red!=hideLt model;
  bas_make1(bas_nr model,cdr p,
        dp_sum(bas_rep model, red!=hide({car p})))
  where p=bas_dpoly model;

symbolic procedure red!=recover model;
% The dpoly part of model is empty, but the rep part contains
% hidden terms.
   begin scalar u,v;
     for each x in bas_rep model do
        if mo_comp car x < 0 then u:=x.u else v:=x.v;
     return bas_make1(bas_nr model, dp_neworder reversip red!=hide u,
                reversip v);
   end;

symbolic procedure red_TailRedDriver(bas,model,redfctn);
% Takes a base element model and reduces the tail with the
% top reduce "redfctn" recursively.
  if (null bas_dpoly model) or (null cdr bas_dpoly model)
                or (null bas) then model
  else begin
     while bas_dpoly model do
        model:=apply2(redfctn,bas,red!=hideLt(model));
     return red!=recover(model);
     end;

symbolic procedure red_TailRed(bas,model);
% The tail reduction as we understand it at the moment.
  if !*noetherian then
        red_TailRedDriver(bas,model,function red_TopRed)
  else red_TailRedDriver(bas,model,function red_TopRedBE);

symbolic procedure red_TotalRed(bas,model);
% Make a terminating total reduction, i.e. for noetherian term orders
% the classical one and for local term orders tail reduction with
% bounded ecart.
  red_TailRed(bas,red_TopRed(bas,model));

% ---------- Reduction of the straightening parts --------

symbolic procedure red_Straight(bas);
% Autoreduce straightening formulae of the base list bas, classical
% in the noetherian case and with bounded ecart in the local case.
  begin scalar u;
  u:=for each x in bas collect red_TailRed(bas,x);
  if !*bcsimp then u:=bas_simp u;
  return sort(u,function red_better);
  end;

symbolic procedure red_collect bas;
% Returns ( bas1 . bas2 ), where bas2 may be reduced with bas1.
    begin scalar bas1,bas2;
    bas1:=listminimize(bas,function (lambda(x,y);
         mo_vdivides!?(dp_lmon bas_dpoly x,dp_lmon bas_dpoly y)));
    bas2:=setdiff(bas,bas1);
    return bas1 . bas2;
    end;

symbolic procedure red_TopInterreduce m;
% Reduce rows of the base list m with red_TopRed until it has pairwise
% incomparable leading terms
% Compute correct representation parts. Do no tail reduction.

    begin scalar c,w,bas1;
    m:=bas_sort bas_zerodelete m;
    if !*bcsimp then m:=bas_simp m;
    while cdr (c:=red_collect m) do
      << if cali_trace()>69 then
                <<write" interreduce ";terpri();bas_print m>>;
         m:=nil; w:=cdr c; bas1:=car c;
         while w do
           << c:=red_TopRed(bas1,car w);
              if bas_dpoly c then m:=c . m;
              w:=cdr w
            >>;
         if !*bcsimp then m:=bas_simp m;
         m:=merge(bas1,bas_sort m,function red_better);
      >>;
    return m;
    end;

% ----- Interface to the former syntax --------------

symbolic procedure red_redpol(bas,model);
% Returns (reduced model . multiplier)
  begin scalar m;
  m:=red_prepare model;
  return red_extract
    (if !*red_total then red_TotalRed(bas,m) else red_TopRed(bas,m))
  end;

symbolic procedure red_Interreduce m;
% Applies to arbitrary term orders.
   begin
        % Top reduction, producing pairwise incomparable leading terms.
   m:=red_TopInterreduce m;
   if !*red_total then m:=red_Straight m; % Tail reduction :
   return m;
   end;

endmodule;  % red

end;
