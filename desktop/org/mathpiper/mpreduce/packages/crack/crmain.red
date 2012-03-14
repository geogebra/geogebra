%**********************************************************************
module crackstar$
%**********************************************************************
%  Main program
%  Authors: Andreas Brand 1995-97,
%           Thomas Wolf since 1996

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


symbolic operator crackshell$
symbolic procedure crackshell$
begin scalar s,ps;
 ps:=promptstring!*$
 promptstring!*:=""$
 terpri()$
 if null old_history then <<
  write"Please give the name of the file in double quotes"$terpri()$
  write"(no ;) from which the session is to be restored: "$
  s:=termread()$
  old_history:={'rb,s};
 >>$
 !*batch_mode:=nil;
 algebraic(crack({},{},{},{}));
 promptstring!*:=ps
end$

symbolic operator crack$
symbolic procedure crack(el,il,fl,vl)$
begin scalar l,l1,l2,n,m,pdes$

 if l:=check_globals() then <<
   write"The global variable ",l," has an incorrect value, please check!"$
   rederr " "
 >>$
 if print_ and logoprint_ then <<terpri()$
  write "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
        "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"$ terpri()$
  write "This is CRACK - a solver for overdetermined partial ",
                  "differential equations"     $ terpri()$
 >>$
 if not !*batch_mode then <<
   if not print_ then <<terpri()$print_:=8>>$
   write"Enter `h' for help."$ terpri()$
 >>$
 %rulelist_:=if pairp userrules_ then
 %            if pairp crackrules_
 %             then list('LIST,userrules_,crackrules_)
 %             else list('LIST,userrules_)
 %           else
 %           if pairp crackrules_ then
 %            list('LIST,crackrules_)
 %                                else nil$
 backup_reduce_flags();   % backup of REDUCE flags

 % initializations of global CRACK variables
 to_do_list:=nil$
 fnew_:=nil$
 vl_:=nil$
 stop_:=nil$
 % dec_hist_list:=nil$
 level_:=nil$
 stepcounter_:=0$
 batchcount_:=-1$
 recycle_eqns:=nil . nil$
 recycle_fcts:=nil$
 recycle_ids:=nil$
 n:=time()$
 m:=gctime()$
 if pairp el and (car el='LIST) then el:=cdr el else el:=list el$
 if pairp fl and (car fl='LIST) then fl:=cdr fl else fl:=list fl$
 if pairp vl and (car vl='LIST) then vl:=cdr vl else vl:=list vl$
 if pairp il and (car il='LIST) then il:=cdr il else il:=list il$
 ineq_:=nil;
 ftem_:=fl;  % for addineq and for mkeqlist
 for each p in il do addineq(nil,p);
 il:=nil$
 vl_:=union(reverse argset fl,vl)$  vl:=nil;
 orderings_:=make_orderings(fl, vl_)$     % Orderings support!
 history_:=nil;
 sol_list:=nil;
 % necessary initializations in case structural equations are to solve:
 if struc_eqn then ini_struc()$

 % Orderings Note: orderings_prop_list_all() inserts all the valid
 % orderings into each of the initial equations, i.e. all equations
 % are in all orderings

 % each equation gets a property list
 pdes:=mkeqlist(el,fl,vl_,allflags_,t,orderings_prop_list_all(),nil)$
 l:=pdes;
 while l and get(car l,'linear_) do l:=cdr l;
 if l then lin_problem:=nil else lin_problem:=t;

 el:=nil$ % to free memory
 size_hist:=if size_watch then {get_statistic(pdes,fl)}
                          else nil$

 % the computation:
 l:=crackmain(pdes,fl)$
 if l=list(nil) then l:=nil$
 l:=union(l,nil)$
 if !*time or time_ then
 <<terpri()$write "CRACK needed :  ",time()-n," ms    GC time : ",
            gctime()-m," ms">>$

 l:=for each a in l collect
      <<l1:=nil$
        l2:=caddr a$
        for each b in cadr a do
         if (pairp b) and (car b = 'EQUAL) then l1:=cons(b,l1)
                                           else l2:=cons(b,l2)$
        list(car a,l1,l2,cadddr a)>>$

 if nil and adjust_fnc and null stop_ then <<
  m:=nil;
  for each a in fl do <<n:=assoc(a,depl!*); if n then m:=cons(n,m)>>$
  l:=for each a in l collect if l1:=dropredund(a,fl,vl_) then cdr l1
                                                         else a$
  for each a in fl do
  if freeof(l,a) then m:=delete(assoc(a,m),m);
  depl!*:=union(m,depl!*)
 >>$

 if null collect_sol then save_sol_list()$

 % statements to free space to make later crack-calls more natural
 nequ_:=1$
 recycle_eqns:=nil . nil$
 recycle_fcts:=nil$
 recycle_ids:=nil$

 recover_reduce_flags()$   % giving the REDUCE flags their backup value

 if print_ and logoprint_ then <<
  terpri()$
  write "This is the end of the CRACK run"$
  terpri()$
  write  "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
         "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"$
  terpri()$
 >>$
 return if l then
        cons('LIST,for each a in l collect
                   list('LIST,cons('LIST,car a),
                              cons('LIST,cadr a),
                              cons('LIST,caddr a),
                              cons('LIST,cadddr a)))
             else list('LIST)
end$

symbolic procedure crackmain(pdes,forg)$
% Main program
% > to be called only from crack() or sub_crack_call()
% > it returns
%   - nil if no solution
%   - {nil} if successful but no solutions are collected (collect_sol=nil)
%   - {sol1,sol2,...}  list of solutions
%     each solution has the form
%        {for each a in pdes collect get(a,'val),
%         forg,setdiff(ftem_,forg),ineq_         }
%
% > The result that is returned is contained completely in the
%   returned value (list) only apart from the variable dependencies
%   of the free functions which is contained in depl!*.
% > apply-calls made within must return either
%   nil or {pdes,forg} or {{sol1,sol2,...}}
% > In the case of more than one solution of an apply call, all of them
%   must be computed because crackmain terminates after such an apply
%   call that returns a list with a single element which then always is
%   treated as a list of solutions.
% > Currently ftem_, ineq_, vl_ are essential (but hidden) input parameters
%   (as well as the properties of the pdes and forg)
% > crackmain() sets the global variable contradiction_.

begin scalar result,l,pl,unsolvable, % dec_hist_list_copy,
             s,ps,batch_one_step,expert_mode_copy,fnc_to_adjust,
                   fnc_adjusted,loopcount,level_length,newli,processes,
             full_proc_list_length$
  level_length:=length level_;
  full_proc_list_length:=length full_proc_list_$
  if level_ then
  history_:=cons(bldmsg("%w%w","*** Start of level ",level_string(nil)),
                 cons('cm,history_));
  if tr_main and print_ then
    <<terpri()$write "start of the main procedure">>$
  % depl_copy_:=depl!*$
  % dec_hist_list_copy:=dec_hist_list$
  fnc_to_adjust:=adjust_fnc;
  contradiction_:=nil$
  ftem_:=fctlist(ftem_,pdes,forg)$     % global list of free functions
again:
  repeat <<
    pl:=proc_list_$                    % global list of procedures
    stop_:=nil$
    ftem_:=fctlist(ftem_,pdes,forg)$
    vl_:=var_list(pdes,forg,vl_)$

    if !*batch_mode or
       to_do_list or
       batch_one_step or
       ((batchcount_>=stepcounter_) and
        ((time_limit=nil) or <<l:=time(); limit_time>=l>>)) then

    % automatic part: -----------------------
    <<if !*batch_mode then
      if print_more then
         print_pde_fct_ineq(pdes,ineq_,
                            append(forg,setdiff(ftem_,forg)),vl_)
      $%else print_statistic(pdes,append(forg,setdiff(ftem_,forg)))$
      if size_watch then
      size_hist:=cons(get_statistic(pdes,append(forg,
                                                setdiff(ftem_,forg))),
                      size_hist);
      stepcounter_:=add1 stepcounter_$
      clean_prop_list(pdes)$
      %if evenp(stepcounter_) and
      %   evenp(stepcounter_/2) then
      err_catch_readin()$
      if to_do_list then batchcount_:=add1 batchcount_;
      if print_ then
       <<terpri()$write "Step ",stepcounter_,": "$
         if print_more then terpri()>>$
      batch_one_step:=nil$
      expert_mode_copy:=expert_mode$
      if (null to_do_list) or
         (caar to_do_list neq 'split_into_cases) then expert_mode:=nil$
      while pl do <<
        if print_ and print_more then
        if pairp(l:=get(car pl,'description)) then <<
          for each a in l do if a then write a$
          write " : "
        >>                                    else
        write "trying ",car pl," : "$
        l:=apply(car pl,list list(pdes,forg,vl_,pdes))$
        if (length l = 1) and (null car l) then contradiction_:=t;
        if l and not contradiction_ then <<
          if length l = 1      % before the test was: if cases_
          then result:= car l  % car l is a list of crackmain results
                               % resulting from investigating subcases
          else <<pdes:=car l$ forg:=cadr l>>$  % no case-splitting
          pl:=nil$
        >>                          else
        if contradiction_ then pl:=nil
                          else <<
          pl:=cdr pl$
          if print_ and print_more then
          <<write " --- "$terpri()>>$
          if not pl then unsolvable:=t
        >>
      >>;
      expert_mode:=expert_mode_copy
    >>
    else % interactive part: -----------------------
    <<if print_ and time_limit and (limit_time<l) then <<
        write"The time limit for automatic execution has been reached."$
        terpri()
      >>$
      rds nil$wrs nil$
      ps:=promptstring!*$
      promptstring!*:="next: "$
      terpri()$s:=termread()$
      % expert_mode:=expert_mode_copy$
      if (s='h) or (s='help) or (s='?) or (s=nil) then printmainmenu()
      else if s='hd then print_hd()
      else if s='hp then print_hp()
      else if s='hf then print_hf()
      else if s='hc then print_hc()
      else if (s='hi) and (getd 'show_id) then print_hi()
      else if s='hb then print_hb()
      % to inspect data -----------------------
      else if s='e then
        if expert_mode then print_pdes(selectpdes(pdes,1))
                       else print_pdes(pdes)
      else if s='eo then <<
        ps:=print_;print_:=1;
        for each s in pdes do <<terpri()$
          write s," : "$
          typeeq(s)$
          plot_non0_coeff_ld(s)
        >>$
        print_:=ps
      >>
      else if s='pi then print_ineq(ineq_)
      else if s='f then
        <<print_fcts(append(forg,setdiff(ftem_,forg)),vl_)$
        terpri()>>
      else if s='v then
        <<print_fcts2(pdes,append(forg,setdiff(ftem_,forg)))$
        terpri()>>
      else if s='s then <<
        print_level(nil)$
        print_statistic(pdes,append(forg,setdiff(ftem_,forg)))
      >>
      else if s='fc then <<
        reclaim()$terpri()$            % do garbage collection
        write if not unboundp 'gcfree!* then gcfree!*
                                        else known!-free!-space(),
              " free cells"$
        terpri()$write countids()," identifiers in use"$;
        terpri()
      >>
      else if s='pe then <<
        promptstring!*:=""$
        terpri()$
        write "Which expression do you want to print?"$
        terpri()$
        write "You can use names of equations, e.g. coeffn(e_12,df(f,x,y),2); "$
        terpri()$
        write "Terminate the expression with ; "$
        terpri()$
        l:=termxread()$
        for each s in pdes do l:=subst(get(s,'val),s,l)$
        l:=reval l;
        for each s in forg do
        if (pairp s) and (car s='EQUAL) then l:=subst(caddr s,cadr s,l)$
        terpri()$
        mathprint(reval l)
      >>
      else if s='ph then <<
        terpri()$
        prettyprint reverse history_
      >>
      else if s='pv then <<
        write "Type in a variable from which you want to know its value: ";
        promptstring!*:=""$
        s:=termread()$
        if not atom s then write"This is not a variable name." else
        if null boundp s then write s," has no value"
                         else <<write s," = "$print eval s>>
      >>
      else if s='pd then plot_dependencies(pdes)
      else if s='ss then err_catch_subsys(pdes)
      else if s='w then write_in_file(pdes,forg)
      % to proceed -----------------------
      else if s='a then batch_one_step:=t
      else if s='g then <<
        promptstring!*:="number of steps: "$
        s:=termread()$
        promptstring!*:="next: "$
        if fixp(s) then batchcount_:=sub1 stepcounter_+s
        else <<write "wrong input!!!"$terpri()>> >>
      else if s='t then <<
        expert_mode:=not expert_mode$
        if expert_mode then
        write"The user will choose equations from now on."
                       else
        write"The program will choose equations from now on.";
        expert_mode_copy:=expert_mode
      >>
      else if s='p1 then printproclist()
      else if s='p2 then printfullproclist()
      else if s='# then <<
         write"Type in a number instead of `#' to ",
              "execute a specific module."$
        terpri()
      >>
      else if (s='l) or numberp s then
      <<if s='l then <<
          repeat_mode:=t;
          ps:=promptstring!*$
          promptstring!*:=""$
          write "Select a method by its number that is to be executed ",
                "repeatedly:"$terpri()$
          s:=termread()$terpri()$
          write "To repeat this method as often as possible, enter `;' "$
          terpri()$
          write "To repeat this method as often as possible, ",
                "but at most a number of n times, enter n :"$
          terpri()$
          repeat_mode:=termread()$
          promptstring!*:=ps$
          if not numberp repeat_mode then repeat_mode:=t
        >>;
        if (s<=0) or (s>full_proc_list_length) then
        if print_ then <<
          write"The number must be in 1 .. ",full_proc_list_length," ."$
          terpri()
        >>        else                         else
        <<
          loopcount:=0;
          if size_watch then
          size_hist:=cons(get_statistic(pdes,append(forg,
                                                    setdiff(ftem_,forg))),
                          size_hist);
          stepcounter_:=add1 stepcounter_$
          clean_prop_list(pdes)$
          if print_ then
          <<terpri()$terpri()$write "Step ",stepcounter_,":"$
            terpri()>>$
          repeat <<
            if to_do_list then loopcount:=sub1 loopcount$
            l:=apply(if to_do_list then 'to_do
                                   else nth(full_proc_list_,s),
                     list list(pdes,forg,vl_,pdes))$
            if (length l = 1) and (null car l) then contradiction_:=t;
            if l and not contradiction_ then <<
              loopcount:=add1 loopcount$
              if length l = 1     % before the test was: if cases_
              then result:=car l  % car l is a list of crackmain results
                                  % resulting from investigating subcases
              else <<pdes:=car l$ forg:=cadr l>>$  % no case-splitting
              terpri()$
              if repeat_mode=1 then repeat_mode:=nil
                               else if repeat_mode then <<
                if numberp repeat_mode then repeat_mode:=sub1(repeat_mode);
                if size_watch then
                size_hist:=cons(get_statistic(pdes,append(forg,
                                                          setdiff(ftem_,
                                                                  forg))),
                                size_hist);
                stepcounter_:=add1 stepcounter_$
                clean_prop_list(pdes)$
                if print_ then
                <<terpri()$terpri()$write "Step ",stepcounter_,":"$
                  terpri()>>$
              >>
            >>
            else if (not contradiction_) and (loopcount=0) then
              <<write "no success"$terpri()>>
          >>
          until (not repeat_mode) or (not l) or contradiction_ or
                  (time_limit and <<ps:=time();limit_time < ps >>);
        >>;
        repeat_mode:=nil
      >>
      else if s='sb then backup_to_file(pdes,forg,t)
      else if s='rb then <<
        l:=restore_backup_from_file(pdes,forg,t)$
        pdes:=car l;forg:=cadr l;
        if null pvm_able then  % assumed not to be started from PVM
        batchcount_:=sub1 stepcounter_
      >>
      else if (s='ep) then <<
       pvm_activate()$
       terpri()$
       if pvm_able then write"Use of PVM is enabled."
                   else write"PVM is not active on this computer."$
      >>
      else if (s='dp) then pvm_able:=nil
      else if (s='pp) and pvm_active() then
        processes:=add_process(processes,pdes,forg)
      else if (s='kp) and pvm_active() then processes:=drop_process(processes)
      else if s='x then !*batch_mode:=t
      else if s='q then stop_:=t
      % to change flags & parameters -----------------------
      else if s='pl then <<
        promptstring!*:="Print length : "$
        s:=termread()$
        if not s or fixp(s) then print_:=s
                            else <<
          terpri()$write "Print length must be NIL or an integer!!!"$
          terpri()
        >>
      >>
      else if s='pm then <<
        print_more:=not print_more;
        if print_more then write"More details will be printed."
                      else write"Fewer details will be printed.";
        terpri()
      >>
      else if s='pa then <<
        print_all:=not print_all;
        if print_all then write"All equation properties will be printed."
                     else write"No equation properties will be printed.";
        terpri()
      >>
      else if s='cp then changeproclist()
      else if s='og then <<
        lex_fc:=not lex_fc$
        if lex_fc then
        write"Lex. ordering of functions has now highest priority."
                  else
        write"Lex. ordering of functions is not of highest priority anymore."$
        terpri()$
        pdes := change_derivs_ordering(pdes,ftem_,vl_)$
      >>
      else if s='od then <<
        lex_df:=not lex_df$
        if lex_df then
        write"From now on lexicographic ordering of derivatives."
                  else
        write"From now on total-degree ordering of derivatives.";
        terpri()$
        pdes := change_derivs_ordering(pdes,ftem_,vl_);
      >>
      else if s='oi then <<
        terpri()$
        write "Current variable ordering is : "$
        s:=vl_;
        while s do <<write car s$ s:=cdr s$ if s then write",">>$
        write";"$terpri()$
        promptstring!*:="New variable ordering : "$
        newli := termlistread()$
        if newli then <<
          if (not not_included(vl_,newli)) and
             (not not_included(newli,vl_)) then <<
            vl_ := newli$
            for each s in pdes do
            put(s,'vars,sort_according_to(get(s,'vars),vl_));
            pdes := change_derivs_ordering(pdes,ftem_,vl_)$
            if tr_orderings then <<
              terpri()$
              write "New variable list: ", vl_$
            >>
          >>$
        >>$
      >>
      else if s='or then <<
        terpri()$
        write "The current variable ordering is going to be reversed. "$
        vl_ := reverse vl_$
        for each s in pdes do
        put(s,'vars,sort_according_to(get(s,'vars),vl_));
        pdes := change_derivs_ordering(pdes,ftem_,vl_)$
        if tr_orderings then <<
          terpri()$
          write "New variable list: ", vl_$
        >>
      >>
      else if s='om then <<
        terpri()$
        write "The current variable ordering is going to be mixed. "$
        s:=vl_; vl_:=nil;
        while s do <<
          l:=nth(s,add1 random length s)$
          s:=delete(l,s);
          vl_:=cons(l,vl_);
        >>;
        for each s in pdes do
        put(s,'vars,sort_according_to(get(s,'vars),vl_));
        pdes := change_derivs_ordering(pdes,ftem_,vl_)$
        if tr_orderings then <<
          terpri()$
          write "New variable list: ", vl_$
        >>
      >>
      else if s='of then <<
        terpri()$
        write "Current function ordering is : "$
        s:=ftem_;
        while s do <<write car s$ s:=cdr s$ if s then write",">>$
        write";"$terpri()$
        promptstring!*:="New function ordering : "$
        newli := termlistread()$
        if newli then <<
         if (not not_included(ftem_,newli)) and
            (not not_included(newli,ftem_)) then
         change_fcts_ordering(newli,pdes,vl_)
        >>
      >>
      else if s='op then <<
        terpri()$
        write "Current orderings are :"$
        terpri()$
        write "Functions : ", ftem_$
        terpri()$
        write "Variables : ", vl_$
      >>
      else if s='ne then <<
        promptstring!*:="Equation name : "$
        s:=termread()$
        if s and idp s then eqname_:=s
                       else
        <<terpri()$write "Equation name must be an identifier!!"$terpri()>>
      >>
      else if s='nf then <<
        promptstring!*:="Function name : "$
        s:=termread()$
        if s and idp s then fname_:=s
                       else
        <<terpri()$write "Function name must be an identifier!!"$terpri()>>
      >>
      else if s='ni then <<
        promptstring!*:="Identity name : "$
        s:=termread()$
        if s and idp s then idname_:=s
                       else
        <<terpri()$write "Identity name must be an identifier!!"$terpri()>>
      >>
      else if s='na then <<!*nat:=not !*nat;
        if !*nat then write"NAT is now on."
                 else write"NAT is now off.">>
      else if s='as then <<
        write "Type in an assignment in the form  ",
              "{variable_name,value}; ";terpri()$
        promptstring!*:="The expression: "$
        s:=termxread()$
        if (pairp s) and (car s='LIST) and (idp cadr s)
        then set(cadr s, reval caddr s)
      >>
      else if s='kp then
      if keep_parti then <<
        keep_parti:=nil;
        for each l in pdes do put(l,'partitioned,nil)
      >>            else keep_parti:=t
      else if s='fi then <<
        freeint_:=not freeint_;
        if freeint_ then write"Integration only if result free ",
                              "of explicit integral from now on."
                    else write"Integration result may involve ",
                              "explicit integral from now on.";
      >>
      else if s='fa then <<
        freeabs_:=not freeabs_;
        if freeabs_ then
        write"Integration only if result free of abs() from now on."
                    else
        write"Integration result may involve abs() from now on.";
      >>
      else if s='cs then <<
        confirm_subst:=not confirm_subst;
        if confirm_subst then
        write"The user will confirm substitutions from now on."
                         else
        write"No user confirmation of substitutions from now on.";
      >>
      else if s='fs then <<
        force_sep:=not force_sep;
        if force_sep then write"Separation will be inforced from now on."
                     else write"Separation will not be inforced from now on.";
      >>
      else if s='ll then <<
        write "What is the new line length? ";
        promptstring!* :=""$
        repeat l:=termread() until fixp l;
        promptstring!*:="next: "$
        linelength l
      >>
      else if s='re then <<
        do_recycle_eqn:=not do_recycle_eqn$
        if do_recycle_eqn then
        write"Equation names will be re-used once the equation is dropped."
                          else
        write"Equation names will not be re-used once the equation is dropped."
      >>
      else if s='rf then <<
        do_recycle_fnc:=not do_recycle_fnc$
        if do_recycle_fnc then
        write"Function names will be re-used once the function",
             " is substituted."
                          else
        write"Function names will not be re-used once the function",
             " is substituted."
      >>
      else if s='st then <<
        batchcount_:=sub1 stepcounter_$
        if time_limit then <<
          l:=limit_time - time()$
          if l<0 then write"The time-limit has expired."
                 else <<
            l:=algebraic(round(l/60000))$
            write"The current CPU time limit for automatic ",
                 "execution to stop is: "$
            s:=algebraic(floor(l/60));
            if s>0 then <<terpri()$write s," hours and ">>$
            write algebraic(l-60*s)," minutes. "$
                 >>
        >>            else write"There is no time-limit set currently."$
        terpri()$
        ps:=promptstring!*$
        promptstring!*:=""$
        if yesp "Do you want to impose a CPU time-limit? " then <<
          time_limit:=t$
          write"How many hours? "$    s:=termread()$
          write"How many minutes? "$  l:=termread()$
          if not numberp s then s:=0$
          if not numberp l then l:=0$
          limit_time:=algebraic (round (s*3600000+l*60000+lisp time()))$
        >>   else time_limit:=nil$
      >>
      else if s='cm then <<
        % do nothing, the input is added as a comment to history_
        ps:=promptstring!*$
        promptstring!*:=""$
        write"Please type your comment in "" "" for the history_ list: "$
        terpri()$
        l:=termread()$
        terpri()$
      >>
      else if s='lr then <<
        ps:=promptstring!*$
        promptstring!*:=""$
        write"Please type in the new LET-rule in the form like"$terpri()$
        write"sqrt(e)**(-~x*log(~y)/~z) => y**(-x/z/2)   : "$
        terpri()$
        l:=termxread()$
        userrules_:=cons('LIST,cons(l,cdr userrules_))$
        algebraic (write "The new list of user defined rules: ",
                         lisp userrules_)$
        terpri()$
        write"Warning: Changes of equations based on LET-rules"$terpri()$
        write"are not recorded in the history of equations."$terpri()$
      >>
      else if s='cr then <<
        ps:=promptstring!*$
        promptstring!*:=""$
        write"These are all the user defined rules: "$      terpri()$
        algebraic (write lisp userrules_);
        write"Give the number of the rule to be dropped: "$ terpri()$
        l:=termread()$
        if l > sub1 length userrules_ then <<
         write"This number is too big."$terpri()
        >>                            else <<
         s:=nil;userrules_:=cdr userrules_;
         while l>1 do <<
          l:=sub1 l;s:=cons(car userrules_,s);userrules_:=cdr userrules_
         >>;
         algebraic(clearrules lisp {'LIST,car userrules_});
         userrules_:=cons('LIST,append(reverse s,cdr userrules_));
         algebraic (write lisp userrules_);
         terpri()$
        >>
      >>
      % to change data of equations -----------------------
      else if s='r then <<pdes:=replacepde(pdes,ftem_,vl_);
                          ftem_:=cadr pdes; pdes:=car pdes>>
      else if s='n then newinequ(pdes)
      else if s='d then pdes:=deletepde(pdes)
      else if s='c then change_pde_flag(pdes)
      else if s='pt then <<l:=General_Trafo({pdes,forg})$
                           if l then <<pdes:=car l$ forg:=cadr l>> >>
      % to work with identities -----------------------
      else if s='i  and getd 'show_id then show_id()
      else if s='id and getd 'show_id then
           if l:=del_red_id(pdes) then pdes:=l else
      else if s='iw and getd 'show_id then write_id_to_file(pdes)
      else if s='ir and getd 'show_id then remove_idl()
      else if s='ia and getd 'show_id then replace_idty()
      else if s='ih and getd 'show_id then start_history(pdes)
      else if s='ip and getd 'show_id then stop_history(pdes)
      else if s='ii and getd 'show_id then
           if l:=integrate_idty(nil,pdes,%forg,
                                ftem_,vl_) then pdes:=l else
           <<write " no success"$terpri()>>
      else if s='ic then check_history(pdes)
      else if s='iy then
           for each l in pdes do mathprint {'EQUAL,l,get(l,'histry_)}
      % to trace and debug -----------------------
      else if s='tm then <<tr_main:=not tr_main;
        if tr_main then write"tr_main is now on."
                   else write"tr_main is now off.">>
      else if s='tg then <<tr_gensep:=not tr_gensep;
        if tr_gensep then write"tr_gensep is now on."
                     else write"tr_gensep is now off.">>
      else if s='ti then <<tr_genint:=not tr_genint;
        if tr_genint then write"tr_genint is now on."
                     else write"tr_genint is now off.">>
      else if s='td then <<tr_decouple:=not tr_decouple;
        if tr_decouple then write"tr_decouple is now on."
                       else write"tr_decouple is now off.">>
      else if s='tl then <<tr_redlength:=not tr_redlength;
        if tr_redlength then write"tr_redlength is now on."
                        else write"tr_redlength is now off.">>
      else if s='ts then <<tr_short:=not tr_short;
        if tr_short then write"tr_short is now on."
                    else write"tr_short is now off.">>
      else if s='to then <<tr_orderings:=not tr_orderings;
        if tr_orderings then write"tr_orderings is now on."
                        else write"tr_orderings is now off.">>
      else if s='tr then <<
        if 'psl memq lispsystem!* then load_package debug$
        ps:=promptstring!*$
        promptstring!*:=""$
        write"Please type the name of the procedure to trace: "$
        l:=termread()$
        terpri()$
        evtr list l
      >>
      else if s='ut then <<
        ps:=promptstring!*$
        promptstring!*:=""$
        write"Please type the name of the procedure to trace: "$
        l:=termread()$
        terpri()$
        evuntr list l
      >>
      else if s='br then <<
        terpri()$write"This is Standard Lisp. Return to Reduce by Ctrl D."$
        terpri()$
        standardlisp()
      >>
      else if s ='pc then <<
       promptstring!* := "The function name: "$
       s:=termread();
       promptstring!* := "The argument list in the form {arg1,...};  : "$
       l:=termxread();
       if (pairp l) and (car l = ' list) and idp s then
          prin2t list ("Result: ", apply(s,cdr l))
      >>
      else if s='in then <<
        ps:=promptstring!*$
        promptstring!*:=""$
        write"Please give the name of the file to be read in"$terpri()$
        write"double quotes (no ;) : "$
        l:=termread()$
        terpri()$
        in l$
      >>
      % otherwise -------------------------------------
      else <<write "illegal input: '",s,"'"$terpri()>>$
      promptstring!*:=ps$
      if ifl!* then rds cadr ifl!*$
      if ofl!* then wrs cdr ofl!*$
     >>;
     if (not pdes) and fnc_to_adjust then
     if fnc_adjusted then <<adjust_fnc:=t; % back to original value
                            fnc_to_adjust:=nil>> else
     if contradiction_ or result then fnc_to_adjust:=nil else
     <<to_do_list:=cons(list('del_redundant_fc,list nil),
                        to_do_list);
       adjust_fnc:=nil;  % in order not to run in a loop
       fnc_adjusted:=t
     >>
    >>
  until contradiction_ or result or stop_ or unsolvable
        or (not pdes and not fnc_to_adjust)$

  ineq_:=drop_triv_ineq(ineq_);
  if not (contradiction_ or result) then <<
    if (print_ or null collect_sol) and not stop_ then <<terpri()$
      terpri()$ write">>>>>>>>> Solution"$
      if level_ then write" of level ",level_string(nil)$
      write" : "$
    >>$
    ftem_:=fctlist(ftem_,pdes,forg)$
    forg:=forg_int(forg,ftem_)$
    if null collect_sol then <<s:=print_;print_:=100>>$
    print_pde_fct_ineq(pdes,ineq_,append(forg,setdiff(ftem_,forg)),vl_)$
    if null collect_sol then print_:=s$
    if not stop_ then <<
      % The following is a procedure the user can define to do
      % specific operations with each solution, e.g. substitution of
      % original equations, substitution into formulae,...
      % This became necessary when for non-linear problems non-solutions
      % were introduced.
      algebraic
      (s:=crack_out(lisp cons('LIST,for each a in pdes collect get(a,'val)),
                   lisp cons('LIST,setdiff(forg,ftem_)),
                   lisp cons('LIST,ftem_),
                   lisp cons('LIST,ineq_) ));
      % If s is not null then s is expected to be an algebraic list of
      % expressions that should be zero but are not and therefore make
      % a new start necessary. This is only relevant for non-linear
      % problems.
      if s and (cdr s) and null lin_problem then <<
        for each l in pdes do
        put(l,'val,simplifypde(get(l,'val),ftem_,t,l))$
        pl:=pdes;
        for each l in cdr s do
        pdes:=eqinsert(mkeq(l,ftem_,vl_,allflags_,t,list(0),nil,pdes),pdes)$
        if setdiff(pdes,pl) then <<
          if print_ then <<
            write"Not all conditions are solved."$terpri()$
            write" --> RESTART with extra conditions ",setdiff(pdes,pl)$
            terpri()>>$
          unsolvable:=nil$
          goto again
        >>
      >>
    >>$
    if session_ and null collect_sol then
    save_solution(for each a in pdes collect get(a,'val),
                  setdiff(forg,ftem_),ftem_,ineq_,nil);   % nil:file_name unsp.
    result:=if collect_sol then
            list list(for each a in pdes collect get(a,'val),
                      forg,setdiff(ftem_,forg),ineq_)
                           else list(nil)$
  >>$
  % dec_hist_list:=dec_hist_list_copy$
  if tr_main and print_ then
     <<terpri()$write "end of the main procedure"$terpri()>>$
  l:=(length level_)+1-level_length;
  for s:=1:l do if level_ then level_:=cdr level_$
  if level_ then
  history_:=cons(bldmsg("%w%w","*** Back to level ",level_string(nil)),
                 cons('cm,history_));

  % delete property lists
  for l:=1:(sub1 nequ_) do drop_pde(mkid(eqname_,l),pdes,nil)$
  for each l in forg do
  if pairp l then setprop(cadr l,nil)
             else setprop(     l,nil)$
  return result$
end$

algebraic procedure crack_out(eqns,assigns,freef,ineq)$
% eqns    .. list of remaining unsolved equations
% assigns .. list of computed assignments of the form `function = expression'
% freef   .. list of list of functiones either free or in eqns
% ineq    .. list of inequalities
begin
end$

symbolic procedure priproli(proclist)$
begin integer i$
      scalar l,cpy$
 for each a in proclist do <<
  cpy:=full_proc_list_;
  i:=1;
  while a neq car cpy do <<i:=add1 i;cpy:=cdr cpy>>$
  if null cpy then i:=0;
  terpri()$
  if i<10 then write " "$
  write i$
  write " : "$
  if pairp(l:=get(a,'description)) then
     (for each s in l do if s then write s)
  else write a>>$
 terpri()$
end$

symbolic procedure priprolinr(proclist,fullproclist)$
begin integer i,j$
 scalar cfpl$
 j:=0;
 for each a in proclist do <<
  j:=j+1;
  i:=1;
  cfpl:=fullproclist;
  while cfpl and (a neq car cfpl) do <<i:=add1 i$cfpl:=cdr cfpl>>$
  if cfpl then <<if (j>1) then write ","$
     if j>21 then <<j:=1;terpri()>>$
     write i>>$
  >>$
 write";"$terpri()$
end$

symbolic procedure changeproclist()$
begin scalar l,p,ps,err;
   terpri()$
   write "Please type in a list of the numbers 1 .. ",
          length full_proc_list_,", like 1,2,5,4,..,15; which"$
   terpri()$
   write"will be the new priority list of procedures done by CRACK."$
   terpri()$
   write"Numbers stand for the following actions:"$terpri()$
   priproli(full_proc_list_)$
   terpri()$write"The list so far was: "$
   priprolinr(proc_list_,full_proc_list_)$
   ps:=promptstring!*$
   promptstring!*:="The new list: "$
   l:=termlistread()$
   promptstring!*:=ps$
   if null l then err:=t
             else <<
     while l do <<
       if (not fixp car l) or
          (car l > length full_proc_list_)
       then
          <<terpri()$write "Error: ",car l,
                           " is not one of the possible numbers.";
          l:=nil$
          err:=t>>
       else <<
         p:=union(list nth(full_proc_list_,car l),p);
         l:=cdr l
       >>
     >>;
   >>;
   if not err then
     <<proc_list_:=reverse p;
     %terpri()$write"The new order of procedures:"$ priproli(proc_list_)
     >>
   else
     <<terpri();write "The procedure list is still unchanged."$terpri()>>
end$

symbolic procedure printproclist()$
begin
 terpri()$
 write "Procedures used currently for automatic execution:"$
 priproli(proc_list_)
end$

symbolic procedure printfullproclist()$
begin
 terpri()$
 write "The complete list of available procedures:"$
 priproli(full_proc_list_)
end$

symbolic procedure printmainmenu()$
<<terpri()$
  write "hd : Help to inspect data"$terpri()$
  write "hp : Help to proceed"$terpri()$
  write "hf : Help to change flags & parameters"$terpri()$
  write "hc : Help to change data of equations"$terpri()$
  if getd 'show_id then
  write "hi : Help to work with identities"$terpri()$
  write "hb : Help to trace and debug"$terpri()$
>>$

symbolic procedure print_hd()$
<<terpri()$
  write "e  : Print equations"$                             terpri()$
  write "eo : Print overview of functions in equations"$    terpri()$
  write "pi : Print inequalities"$                          terpri()$
  write "f  : Print functions and variables"$               terpri()$
  write "v  : Print all derivatives of all functions"$      terpri()$
  write "s  : Print statistics"$                            terpri()$
  write "fc : Print no of free cells"$                      terpri()$
  write "pe : Print an algebraic expression"$               terpri()$
  write "ph : Print history of interactive input"$          terpri()$
  write "pv : Print value of any lisp variable"$            terpri()$
  write "pd : Plot the occurence of functions in equations"$terpri()$
  write "ss : Find and print sub-systems"$                  terpri()$
  write "w  : Write equations into a file"$                 terpri()$
>>$

symbolic procedure print_hp()$
<<terpri()$
  write "a  : Do one step automatically"$                   terpri()$
  write "g  : Go on for a number of steps automatically"$   terpri()$
  write "t  : Toggle equation selection to : "$
              if expert_mode then write "AUTOMATIC"
                             else write "USER"$             terpri()$
  write "p1 : Print a list of all modules in batch mode"$   terpri()$
  write "p2 : Print a complete list of all modules"$        terpri()$
  write "#  : Execute the module with the number `#' once"$ terpri()$
  write "l  : Execute a specific module repeatedly"$        terpri()$
  write "sb : Save complete backup to file"$                terpri()$
  write "rb : Read backup from file"$                       terpri()$
  write "ep : Enable parallelism"$                          terpri()$
  write "dp : Disable parallelism"$                         terpri()$
  write "pp : Start an identical parallel process"$         terpri()$
  write "kp : Kill a parallel process"$                     terpri()$
  write "x  : Exit interactive mode for good"$              terpri()$
  write "q  : Quit current level or crack if in level 0"$   terpri()$
>>$

symbolic procedure print_hf()$
<<terpri()$
  write "pl : Maximal length of an expression to be printed (",
              print_,")"$                                   terpri()$
  write "pm : ",if print_more then "Do not p" else "P",
              "rint more information about the pdes"$       terpri()$
  write "pa : ",if print_all then "Do not p" else "P",
              "rint all information about the pdes"$        terpri()$
  write "cp : Change the priorities of procedures"$         terpri()$
  write "og : Toggle ordering to ",
              if lex_fc then "derivatives > functions"
                        else "functions > derivatives"$     terpri()$
  write "od : Toggle ordering of derivatives to ",
              if lex_df then "total-degree"
                        else "lexicographic"$               terpri()$
  write "oi : Interactive change of ordering on variables"$ terpri()$
  write "or : Reverse ordering on variables"$               terpri()$
  write "om : Mix randomly ordering on variables"$          terpri()$
  write "of : Interactive change of ordering on functions"$ terpri()$
  write "op : Print current ordering"$  terpri()$
  write "ne : Root of the name of new generated equations (",
              eqname_,")"$                                  terpri()$
  write "nf : Root of the name of new functions and constants (",
              fname_,")"$                                   terpri()$
  write "ni : Root of the name of new identities (",
              idname_,")"$                                  terpri()$
  write "na : Change output to "$
              if !*nat then write "OFF NAT"
                       else write "ON NAT"$                 terpri()$
  write "as : Input of an assignment"$                      terpri()$
  write "kp : ",if keep_parti then "Do not keep"
                              else "Keep",
              " a partitioned copy of each equation"$       terpri()$
  write "fi : ",if freeint_ then "Allow unresolved integrals"
              else "Forbid unresolved integrals"$           terpri()$
  write "fa : ",if freeabs_ then "Allow solutions of ODEs with ABS()"
              else "Forbid solutions of ODEs with ABS()"$   terpri()$
  write "cs : ",if confirm_subst then
              "No confirmation of intended substitutions/factorizations"
                                 else
              "Confirmation of intended substitutions/factorizations"$
                                                            terpri()$
  write "fs : ",if force_sep
                then "Do not enforce direct separation"
                else "Enforce direct separation"$           terpri()$
  write "ll : change of the line length"$                   terpri()$
  write "re : ",if do_recycle_eqn then "Do not re-cycle equation names."
                                  else "Do re-cycle equation names."$
                                                            terpri()$
  write "rf : ",if do_recycle_fnc then "Do not re-cycle function names."
                                  else "Do re-cycle function names."$
                                                            terpri()$
  write "st : Setting a CPU time limit for un-interrupted run"$
                                                            terpri()$
  write "cm : Adding a comment to the history_ list"$       terpri()$
  write "lr : Adding a LET-rule"$                           terpri()$
  write "cr : Clearing a LET-rule"$                         terpri()$
>>$

symbolic procedure print_hc()$
<<terpri()$
  write "r  : Replace or add one equation"$                 terpri()$
  write "n  : Add one inequality"$                          terpri()$
  write "d  : Delete equations"$                            terpri()$
  write "c  : Change a flag or property of one pde"$        terpri()$
  write "pt : Perform a transformation of functions and variables"$
                                                            terpri()$
>>$

symbolic procedure print_hi()$
if getd 'show_id then
<<terpri()$
  write "i  : Print identities between equations"$          terpri()$
  write "id : Delete redundand equations"$                  terpri()$
  write "iw : Write identities to a file"$                  terpri()$
  write "ir : Remove list of identities"$                   terpri()$
  write "ia : Add or replace an identity"$                  terpri()$
  write "ih : Start recording histories and identities"$    terpri()$
  write "ip : Stop recording histories and identities"$     terpri()$
  write "ii : Integrate an identity"$                       terpri()$
  write "ic : Check the consistency of identity data"$      terpri()$
  write "iy : Print the history of equations"$              terpri()$
>>$

symbolic procedure print_hb()$
<<terpri()$
  write "tm : ",if tr_main then "Do not t" else "T",
              "race main procedure"$                        terpri()$
  write "tg : ",if tr_gensep then "Do not t" else "T",
              "race generalized separation"$                terpri()$
  write "ti : ",if tr_genint then "Do not t" else "T",
              "race generalized integration"$               terpri()$
  write "td : ",if tr_decouple then "Do not t" else "T",
              "race decoupling process"$                    terpri()$
  write "tl : ",if tr_redlength then "Do not t" else "T",
              "race decoupling length reduction"$           terpri()$
  write "ts : ",if tr_short then "Do not t" else "T",
              "race algebraic length reduction"$            terpri()$
  write "to : ",if tr_orderings then "Do not t" else "T",
              "race orderings process"$                     terpri()$
  write "tr : Trace an arbitrary procedure"$                terpri()$
  write "ut : Untrace a procedure"$                         terpri()$
  write "br : Break"$                                       terpri()$
  write "pc : Do a function call"$                          terpri()$
  write "in : Reading in a REDUCE file"$                    terpri()$
>>$

symbolic procedure to_do(arglist)$
if to_do_list then
  begin scalar p,l$
    p:=car to_do_list;
    to_do_list:=cdr to_do_list;
    if tr_main and print_ and print_more then
      if pairp(l:=get(car p,'description)) then
         <<for each a in l do if a then write a$
         write " : ">>
      else write "trying ",car p," : "$
%    l:=apply(car p,list(list(car arglist,cadr arglist,
%                             caddr arglist,cadddr cdr p)))$
    l:=apply(car p,list(cons(car arglist,cons(cadr arglist,
                             cons(caddr arglist, cdr p)))))$
    if not l then l:=arglist$
    return l$
  end$

symbolic procedure subst_derivative(arglist)$
% Substitution of a derivative of a function by an new function
% in all pdes and in forg
begin scalar f,l,q,g,h,pdes,forg$
  pdes:=car arglist$
  forg:=cadr arglist$
  l:=check_subst_df(pdes,forg)$
  for each d in l do
    <<f:=newfct(fname_,fctargs cadr d,nfct_)$
    nfct_:=add1 nfct_$
    ftem_:=fctinsert(f,delete(cadr d,ftem_))$
    if print_ then
      <<terpri()$write "replacing "$
        fctprint1 d$
        write " by "$fctprint list f$terpri()>>$
    for each s in pdes do dfsubst_update(f,d,s)$
    % integrating f in order to substitute for cadr d
    % in ineq_
    h:=cddr d;
    g:=f;
    while h do <<
      for r:=1:(if (length h =1) or
                   ((length h > 1) and (not fixp cadr h))
                then 1
                else (cadr h)
               )   do
      g:=list('PLUS,gensym(),list('INT,g,car h));
      h:=cdr h;
      if h and (fixp car h) then h:=cdr h
    >>;
    % now the substitution in ineq_
    ineq_:=for each s in ineq_ collect reval subst(g,cadr d,s);
    if member(cadr d,forg) then
       <<q:=mkeq(list('PLUS,d,list('MINUS,f)),
                 list(f,cadr d),fctargs f,allflags_,nil,list(0),nil,pdes)$
       remflag1(q,'to_eval)$
       put(q,'not_to_eval,cons(f,get(q,'not_to_eval)))$
       pdes:=eqinsert(q,pdes)>>$
    forg:=dfsubst_forg(f,g,cadr d,forg)$
    >>$
  return if l then list(pdes,forg)
              else nil
end$

symbolic procedure undo_subst_derivative(arglist)$
% undo Substitution of a derivative of a function by an new function
% in all pdes and in forg
begin scalar success$
 for each p in car arglist do
     if get(p,'not_to_eval) then
        <<remprop(p,'not_to_eval)$
        flag(list p,'to_eval)$
        success:=t>>$
 return if success then arglist
                   else nil
end$

%symbolic procedure make_subst(pdes,forg,vl,l1,length_limit,pdelimit,
%                              less_vars,no_df,no_cases,lin_subst,
%                              min_growth,cost_limit,keep_eqn,sub_fc)$

symbolic procedure subst_level_0(arglist)$
% Substitution of a function by an expression of at most length subst_0
% depending on less variables than the function,
% not allowing case distinctions,
% ftem-dep. coefficient allowed
 make_subst(if length arglist > 4 then nth(arglist,5)
                                  else car arglist,
            cadr arglist,caddr arglist,cadddr arglist,
            subst_0,target_limit_0,t,nil,t,nil,nil,nil,nil,
            if length arglist > 5 then nth(arglist,6)
                                  else nil
           )$

symbolic procedure subst_level_03(arglist)$
% Substitution of a function by an expression of at most length subst_0
% depending on less variables than the function,
% not allowing case distinctions,
% ftem-dep. coefficient allowed
 make_subst(if length arglist > 4 then nth(arglist,5)
                                  else car arglist,
            cadr arglist,caddr arglist,cadddr arglist,
            subst_0,target_limit_0,nil,t,t,nil,nil,nil,nil,
            if length arglist > 5 then nth(arglist,6)
                                  else nil
           )$

symbolic procedure subst_level_04(arglist)$
% Substitution of a function by an expression of at most length subst_1
% depending on less variables than the function,
% not allowing case distinctions,
% ftem-dep. coefficient allowed
 make_subst(if length arglist > 4 then nth(arglist,5)
                                  else car arglist,
            cadr arglist,caddr arglist,cadddr arglist,
            subst_1,target_limit_1,nil,t,t,nil,nil,nil,nil,
            if length arglist > 5 then nth(arglist,6)
                                  else nil
           )$

symbolic procedure subst_level_05(arglist)$
% Substitution of a function by an expression of at most length subst_4
% depending on less variables than the function,
% not allowing case distinctions,
% ftem-dep. coefficient allowed
 make_subst(if length arglist > 4 then nth(arglist,5)
                                  else car arglist,
            cadr arglist,caddr arglist,cadddr arglist,
            subst_4,target_limit_0,nil,t,t,nil,nil,nil,nil,
            if length arglist > 5 then nth(arglist,6)
                                  else nil
           )$

symbolic procedure subst_level_1(arglist)$
% Substitution of a function by an expression of at most length subst_1
% depending on less variables than the function,
% allowing case distinctions,
% ftem-dep. coefficient allowed
 make_subst(if length arglist > 4 then nth(arglist,5)
                                  else car arglist,
            cadr arglist,caddr arglist,cadddr arglist,
            subst_1,target_limit_1,t,nil,nil,nil,nil,nil,nil,
            if length arglist > 5 then nth(arglist,6)
                                  else nil
           )$

symbolic procedure subst_level_2(arglist)$
% Substitution of a function by an expression of at most length subst_2
% depending on less variables than the function,
% allowing case distinctions,
% ftem-dep. coefficient allowed
 make_subst(if length arglist > 4 then nth(arglist,5)
                                  else car arglist,
            cadr arglist,caddr arglist,cadddr arglist,
            subst_2,target_limit_0,t,nil,t,nil,nil,nil,nil,
            if length arglist > 5 then nth(arglist,6)
                                  else nil
           )$

symbolic procedure subst_level_3(arglist)$
% Substitution of a function by an expression of at most length subst_1
% depending on all variables,
% allowing case distinctions,
% ftem-dep. coefficient allowed
 make_subst(if length arglist > 4 then nth(arglist,5)
                                  else car arglist,
            cadr arglist,caddr arglist,cadddr arglist,
            subst_3,target_limit_3,nil,nil,nil,nil,nil,nil,nil,
            if length arglist > 5 then nth(arglist,6)
                                  else nil
           )$

symbolic procedure subst_level_33(arglist)$
% Substitution of a function by an expression of at most length subst_2
% depending on all variables,
% not giving case distinctions,
% no ftem-dep. coefficient
 make_subst(if length arglist > 4 then nth(arglist,5)
                                  else car arglist,
            cadr arglist,caddr arglist,cadddr arglist,
            subst_4,target_limit_4,nil,nil,t,t,nil,nil,nil,
            if length arglist > 5 then nth(arglist,6)
                                  else nil
           )$

symbolic procedure subst_level_35(arglist)$
% Substitution of a function by an expression of at most length subst_2
% depending on all variables,
% not giving case distinctions,
% ftem-dep. coefficient allowed
 make_subst(if length arglist > 4 then nth(arglist,5)
                                  else car arglist,
            cadr arglist,caddr arglist,cadddr arglist,
            subst_4,target_limit_4,nil,nil,t,nil,nil,nil,nil,
            if length arglist > 5 then nth(arglist,6)
                                  else nil
           )$

symbolic procedure subst_level_4(arglist)$
% Substitution of a function by an expression of at most length subst_2
% depending on all variables,
% allowing case distinctions,
% ftem-dep. coefficient allowed
 make_subst(if length arglist > 4 then nth(arglist,5)
                                  else car arglist,
            cadr arglist,caddr arglist,cadddr arglist,
            subst_4,target_limit_4,nil,nil,nil,nil,nil,nil,nil,
            if length arglist > 5 then nth(arglist,6)
                                  else nil
           )$

symbolic procedure subst_level_45(arglist)$
% Substitution of a function by an expression
% such that the growth of all equations is minimal
% with some penalty for non-linearity increasing substitutions
% no substitutions introducing case distinctions
% no growth of total length of all equations
% good for algebraic problems
 make_subst(if length arglist > 4 then nth(arglist,5)
                                  else car arglist,
            cadr arglist,caddr arglist,cadddr arglist,
            nil,nil,nil,nil,t,nil,t,cost_limit5,nil,
            if length arglist > 5 then nth(arglist,6)
                                  else nil
           )$

symbolic procedure subst_level_5(arglist)$
% Substitution of a function by an expression
% such that the growth of all equations is minimal
% with some penalty for non-linearity increasing substitutions
% and substitutions introducing case distinctions
% good for algebraic problems
 make_subst(if length arglist > 4 then nth(arglist,5)
                                  else car arglist,
            cadr arglist,caddr arglist,cadddr arglist,
            subst_4,target_limit_4,nil,nil,nil,nil,t,nil,nil,
            if length arglist > 5 then nth(arglist,6)
                                  else nil
           )$

symbolic procedure factorize_any(arglist)$
% Factorization of a pde and investigation of the resulting subcases
begin scalar l$
 if expert_mode then l:=selectpdes(car arglist,1)
                else l:=cadddr arglist$
 return split_and_crack(get_fact_pde(l,nil),
                                    car arglist,cadr arglist)$
end$

symbolic procedure factorize_to_substitute(arglist)$
% Factorization of a pde and investigation of the resulting subcases
begin scalar l$
 if expert_mode then l:=selectpdes(car arglist,1)
                else l:=cadddr arglist$
 return split_and_crack(get_fact_pde(l,t),
                                    car arglist,cadr arglist)$
end$

symbolic procedure separation(arglist)$
% Direct separation of a pde
if vl_ then % otherwise not possible --> save time
begin scalar p,l,l1,pdes,forg$
 pdes:=car arglist$
 forg:=cadr arglist$
 if expert_mode then l1:=selectpdes(pdes,1)
                else l1:=cadddr arglist$
 if (p:=get_separ_pde(l1)) then
    <<l:=separate(p,pdes)$
      if l and
         ((length l>1) or
          ((length l = 1) and (car l neq p))) then
         <<pdes:=drop_pde(p,pdes,nil)$
           while l do
              <<pdes:=eqinsert(car l,pdes)$
                l:=cdr l>>$
           l:=list(pdes,forg)>> >>$
 return l$
end$

symbolic procedure alg_solve_system(arglist)$
begin scalar pdes,l1,l2,l3,l4,l5,l6,fl,vl,zd,pdes2$
 pdes:=car arglist$
 %l1:=selectpdes(pdes,nil)$
 l1:=select_from_list(pdes,nil)$
 if null l1 then return nil;
 for each l2 in l1 do vl:=union(get(l2,'vars),vl);
 for each l2 in l1 do fl:=union(get(l2,'fcts),fl);
 l1:=for each l2 in l1 collect get(l2,'val)$
 write"Please give a list of constants, functions or derivatives"$
 terpri()$
 write"of functions to be solved algebraically, like f,g,df(g,x,2);"$
 terpri()$
 l2:=termlistread()$
 if l2 then <<
  l3:=cdr solveeval list(cons('LIST,l1),cons('LIST,l2));

  if null l3 then <<
   write"There is no solution."$
   terpri()
  >> else
  if length l3 > 1 then <<
   write"can currently not handle more than 1 solution"$
   terpri()
  >> else <<
   l3:=for each l4 in l3 collect <<        % for each solution l4
    l4:=for each l5 in cdr l4 collect <<
     zd:=union(zero_den(reval l5,fl,vl),zd)$
     l6:=reval {'PLUS,cadr l5,{'MINUS,caddr l5}}$
     if pairp l6 and (car l6 = 'QUOTIENT) then cadr l6
                                          else l6
    >>       % l4 is now a list of expressions to vanish
   >>;
   if length l3 = 1 then << %######### 1 solution - a restriction for now
    l4:=car l3;             % the solution
    pdes2:=pdes;
    for each l5 in l4 do <<
     l5:=mkeq(if zd then cons('TIMES,append(zd,list l5))
                    else l5,
              fl,vl,allflags_,nil,list(0),nil,pdes)$
     pdes:=eqinsert(l5,pdes)$
    >>;
    if print_ then <<
     pdes2:=setdiff(pdes,pdes2);
     write"New equations: ",pdes2$terpri()$
    >>$
    return {pdes,cadr arglist}
   >>
  >>
 >>
end$

symbolic procedure alg_solve_single(arglist)$
% Solving an equation that is algebraic for a function
% or a derivative of a function,
% So far no flag is installed to remember a corresponding
% investigation because the check is quick and done very
% rarely with lowest priority.
begin scalar l,l1,pdes,forg$
 pdes:=car arglist$
 forg:=cadr arglist$
 if expert_mode then l1:=selectpdes(pdes,1)
                else l1:=cadddr arglist$
 if (l:=algsolvederiv(l1)) then
    <<pdes:=drop_pde(car l,pdes,nil)$
      pdes:=eqinsert(cdr l,pdes)$
      to_do_list:=cons(list('factorize_any,%pdes,forg,caddr arglist,
                            list cdr l),
                       to_do_list);
      l:=list(pdes,forg);
    >>$
 return l
end$

symbolic procedure alg_for_deriv(p)$
% find the first function with only one sort of derivative
% which in addition occurs non-linear
begin scalar dl,d,f$
 dl:=get(p,'derivs);
 while dl and null d do <<  % for each function
  d:=car dl$     % d is the leading power of the leading deriv. of f
  f:=caar d;     % the next function f
  if fctlength f < get(p,'nvars) then <<d:=nil;dl:=nil>>
                                 else <<
   dl:=cdr dl;
   if cdr d = 1 then d:=nil; % must not be linear in lead. deriv.
   while dl and (f = caaar dl) do <<
    if d and (car d neq caar dl) then d:=nil;
    dl:=cdr dl
   >>
  >>
 >>;
 return d
end$

symbolic procedure algsolvederiv(l)$
begin scalar d,p,abs_was_not_active$
 while l and null (d:=alg_for_deriv(car l)) do l:=cdr l;
 if d then <<
  p:=cdr d$
  algebraic <<
   abs_was_not_active:=if !%x neq abs !%x then t else nil$
   if abs_was_not_active then let abs_
  >>$
  d:=solveeval list(get(car l,'val),
                    if 1=length car d then caar d
                                      else cons('DF,car d));
  algebraic <<
   if abs_was_not_active then clearrules abs_
  >>$

%  d:=solveeval list(cons('LIST,get(car l,'val)),
%                    {'LIST,if 1=length car d then caar d
%                                             else cons('DF,car d)});
  if d and (car d='LIST) and (length d = p+1) then
  p:=for each el in cdr d collect if car el='EQUAL   then
     reval {'NUM,
     reval {'PLUS,cadr el,{'MINUS,caddr el}}} else d:=nil
                                              else d:=nil;
  if d then <<
   d:=cons('TIMES,p);
   p:=car l;
   d:=mkeq(d,get(p,'fcts),get(p,'vars),allflags_,nil,get(p,'orderings),nil,nil)$
   % last argument is nil as no new inequalities are to be expected.
   if print_ then write p," factorized to ",d
  >>
 >>;
 return if d then p . d
             else nil
end$

symbolic procedure quick_integration(arglist)$
% Integration of a short first order de with at most two terms
begin scalar l,l1,pdes,forg$
 pdes:=car arglist$
 forg:=cadr arglist$
 if expert_mode then
 <<l1:=selectpdes(pdes,1);flag(l1,'to_int);flag(l1,'to_fullint)>>
                else l1:=cadddr arglist$
 if (l:=quick_integrate_one_pde(l1)) then
    <<pdes:=delete(car l,pdes)$
      for each s in l do pdes:=eqinsert(s,pdes)$
      for each s in l do
        to_do_list:=cons(list('subst_level_35,%pdes,forg,caddr arglist,
                              list s),
                         to_do_list)$
      l:=list(pdes,forg)>>$
 return l$
end$

symbolic procedure full_integration(arglist)$
% Integration of a pde
% only if then a function can be substituted
begin scalar l,l1,pdes,forg$
 pdes:=car arglist$
 forg:=cadr arglist$
 if expert_mode then
 <<l1:=selectpdes(pdes,1);flag(l1,'to_int);flag(l1,'to_fullint)>>
                else l1:=cadddr arglist$
 if (l:=integrate_one_pde(l1,genint_,t)) then
    <<pdes:=delete(car l,pdes)$
      for each s in l do pdes:=eqinsert(s,pdes)$
      for each s in l do
        to_do_list:=cons(list('subst_level_35,%pdes,forg,caddr arglist,
                              list s),
                         to_do_list)$
      l:=list(pdes,forg)>>$
 return l$
end$

symbolic procedure integration(arglist)$
% Integration of a pde
begin scalar l,l1,pdes,forg$
 pdes:=car arglist$
 forg:=cadr arglist$
 if expert_mode then
 <<l1:=selectpdes(pdes,1);flag(l1,'to_int);flag(l1,'to_fullint)>>
                else l1:=cadddr arglist$
 if (l:=integrate_one_pde(l1,genint_,nil)) then
    <<pdes:=delete(car l,pdes)$
      for each s in l do pdes:=eqinsert(s,pdes)$
      for each s in cdr l do
        to_do_list:=cons(list('subst_level_35,%pdes,forg,caddr arglist,
                              list s),
                         to_do_list)$
      l:=list(pdes,forg)>>$
 return l$
end$

symbolic procedure multintfac(arglist)$
% Seaching of an integrating factor for a set of pde's
begin scalar pdes,forg,l,stem,ftem,vl,vl1$
 pdes:=car arglist$
 if null pdes or (length pdes=1) then return nil$
 forg:=cadr arglist$
 for each p in pdes do
   if not (get(p,'starde) or get(p,'nonrational)) then
     <<stem:=cons(get(p,'val),stem)$
       ftem:=union(get(p,'fcts),ftem)$
       vl:=union(get(p,'vars),vl)
     >>$
 vl1:=vl$
 fnew_:=nil$
 while vl1 do
  if (l:=findintfac(stem,ftem,vl,car vl1,nil,nil,nil,nil)) then
    <<ftem:=smemberl(ftem,car l)$
      vl:=union(smemberl(vl,car l),argset ftem)$
      l:=addintco(car l, ftem, nil, vl, car vl1)$
      for each f in fnew_ do
        ftem_:=fctinsert(f,ftem_)$
      ftem:=union(fnew_,ftem)$
      fnew_:=nil$
      pdes:=eqinsert(mkeq(l,smemberl(ftem_,ftem),vl,
                          allflags_,t,list(0),nil,pdes),
                     pdes)$
      vl1:=nil$
      l:=list(pdes,forg)>>
  else vl1:=cdr vl1$
 return l$
end$

symbolic procedure diff_length_reduction(arglist)$
% Do one length reduction step
begin scalar l$
 l:=dec_and_red_len_one_step(car arglist,ftem_,%cadr arglist,
                             caddr arglist,0)$
 % 0 for ordering
 if l then l:=list(l,cadr arglist)$
 return l$
end$

symbolic procedure high_prio_decoupling(arglist)$
% Do one decoupling step
begin scalar l$
 l:=dec_one_step(car arglist,ftem_,%cadr arglist,
                 caddr arglist,t,0)$
 % 0 for ordering
 if l then l:=list(l,cadr arglist)$
 return l$
end$

symbolic procedure decoupling(arglist)$
% Do one decoupling step
begin scalar l$
 l:=dec_one_step(car arglist,ftem_,%cadr arglist,
                 caddr arglist,nil,0)$
 % 0 for ordering
 if l then l:=list(l,cadr arglist)$
 return l$
end$

symbolic procedure clean_dec(p,pdes,flg)$
begin scalar propty,el,nl,newpropty$
 propty:=get(p,flg)$
 for each el in propty do <<
  nl:=intersection(cdr el,pdes);
  if nl then newpropty:=cons(cons(car el,nl),newpropty)
 >>$
 put(p,flg,reverse newpropty)
end$

symbolic procedure clean_prop_list(pdes)$
if null car recycle_eqns          and
        cdr recycle_eqns          and
   (length cdr recycle_eqns > 50) then
<<for each p in pdes do <<
    clean_dec(p,pdes,'dec_with)$
    clean_dec(p,pdes,'dec_with_rl)$
  % clean_rl(p,pdes) :
    put(p,'rl_with,intersection(pdes,get(p,'rl_with)))$
  >>$
  % recycle_eqns is a pair of 2 lists:
  % (ready to use eqn. names) . (free eqn. names which still
  %                              may occur in prob_list)
  recycle_eqns:=append(car recycle_eqns,reverse cdr recycle_eqns) . nil;
  nil
>>$

symbolic procedure clean_up(pdes)$
begin scalar newpdes;
  while pdes do <<
    if flagp(car pdes,'to_drop) then
    drop_pde(car pdes,nil,nil)  else
    newpdes:=cons(car pdes,newpdes);
    pdes:=cdr pdes
  >>;
  return reverse newpdes
end$

symbolic procedure add_differentiated_pdes(arglist)$
% all pdes in which the leading derivative of a function of all
% vars occurs nonlinear will be differentited w.r.t all vars and
% the resulting pdes are added to the list of pdes
begin scalar pdes,l,l1,q$
 pdes:=car arglist$
 if expert_mode then l1:=selectpdes(pdes,1)
                else l1:=cadddr arglist$
 for each p in l1 do
  if flagp(p,'to_diff) then
% --------------- it should be differentiated only once
    <<for each f in get(p,'allvarfcts) do
       if (cdr ld_deriv(p,f)>1) then
         <<if print_ then
             <<terpri()$
             write "differentiating ",p," w.r.t. "$
             listprint fctargs f$
             write " we get the new equations : ">>$
         for each v in fctargs f do <<
          q:=mkeq(list('DF,get(p,'val),v),get(p,'fcts),get(p,'vars),
                  delete('to_fullint,delete('to_int,delete('to_diff,allflags_))),
                  t,list(0),nil,pdes)$
           prevent_simp(v,p,q)$
           if print_ then write q," "$
           pdes:=eqinsert(q,pdes)>>$
         remflag1(p,'to_diff)$
         l:=cons(pdes,cdr arglist)>>
    >>$
 return l$
end$

symbolic procedure add_diff_ise(arglist)$
% a star-pde is differentiated and then added
begin scalar pdes,l,l1,q,vli$
  pdes:=car arglist$
  if expert_mode then l1:=selectpdes(pdes,1)
                 else l1:=cadddr arglist$
  for each p in l1 do
  if flagp(p,'to_diff)
     and (null l)
     and get(p,'starde)
  then <<
    vli:=if expert_mode then select_from_list(get(p,'vars),nil)
                        else get(p,'vars);
    if print_ then
    <<terpri()$
      write "differentiating ",p," w.r.t. "$
      listprint vli$
      write " we get the new equations : "
    >>$
    for each v in vli do
    <<q:=mkeq(list('DF,get(p,'val),v),get(p,'fcts),get(p,'vars),
              delete('to_fullint,delete('to_int,allflags_)),
              t,get(p,'orderings),nil,pdes)$
      if null get(q,'starde) then <<
        flag(list q,'to_fullint)$
        flag(list q,'to_int)$
      >>$
      prevent_simp(v,p,q)$
      %check whether q really includes 'fcts and 'vars: should be ok
      if print_ then write q," "$
      pdes:=eqinsert(q,pdes)$
    >>$
    remflag1(p,'to_diff)$
    l:=cons(pdes,cdr arglist)$
  >>$
  return l$
end$

% ACN can not see ANY place where GB_REDUCE gets set, and so is perhaps
% confused by the test that is made on it here...

fluid '(GB_REDUCE);

symbolic procedure alg_groebner(arglist)$
begin scalar pdes,forg,sol,n,result,l1$
  pdes:=car arglist$
  sol:=
  if GB_REDUCE = 'GB then
  algebraic call_gb(lisp(cons('LIST,ftem_)),
                     lisp(cons('LIST,for each p in pdes collect(get(p,'val)))),
                    lisp 'revgradlex)
  else
  algebraic(groebnerf(lisp(cons('LIST,for each p in pdes collect(get(p,'val)))),
                      lisp(cons('LIST,ftem_)),
                      lisp(cons('LIST,ineq_)) ));
  if print_ then <<
    terpri()$
    write"An algebraic Groebner basis computation yields "$
  >>$
  return
  if sol={'LIST,{'LIST,1}} then <<
    if print_ then write"a contradiction."$
    contradiction_:=t$
    nil
  >>                       else <<
    while pdes do pdes:=drop_pde(car pdes,pdes,nil)$
    sol:=cdr sol;
    if null cdr sol then << % only one solution
      sol:=cdar sol;        % a lisp list of necessarily vanishing expressions
      if print_ then <<
        terpri()$
        write"a single new system of conditions."$
        terpri()$
        write"All previous equations are dropped."$
        terpri()$
        write"The new equations are:"$
      >>$
      pdes:=mkeqlist(sol,ftem_,vl_,allflags_,t,%orderings_prop_list_all()
                     list(0),nil)$
      listprint(pdes)$
      if contradiction_ then nil
                        else {pdes,cadr arglist}
    >>              else << % more than one solution
      if print_ then <<
        terpri()$
        write length sol," cases. All previous equations are dropped."$
      >>$
      n:=0$
      forg:=cadr arglist$
      backup_to_file(pdes,forg,nil)$  % with all pdes deleted
      while sol do <<
        n:=n+1$
        level_:=cons(n,level_)$
        if print_ then <<
          print_level(t)$
          terpri()$write "CRACK is now called with a case resulting "$
          terpri()$write "from a Groebner Basis computation : "
        >>;
        % further necessary step to call crackmain():
        recycle_fcts:=nil$  % such that functions generated in the sub-call
                            % will not clash with existing functions
        pdes:=mkeqlist(cdar sol,ftem_,vl_,allflags_,t,
                       %orderings_prop_list_all()
                       list(0),nil)$
        sol:=cdr sol;
        l1:=crackmain(pdes,forg)$
        if l1 and not contradiction_ then result:=union(l1,result);
        contradiction_:=nil$
        if sol then <<
          l1:=restore_backup_from_file(pdes,forg,nil)$
          pdes:=car l1;  forg:=cadr l1;
        >>
      >>;
      delete_backup()$
      list result
    >>
  >>
end$

symbolic procedure split_and_crack(p,pdes,forg)$
%  for each factor of p CRACKMAIN is called
if p then
begin scalar l,l1,q,contrad,result,n,h,d,newpdes,newineq$
             %,sol,f,newfdep$,bak,s
  n:=0$
  l:=cdr get(p,'val)$                 %  list of factors of p
  contrad:=t$
  if print_ then <<
    terpri()$
    write "factorizing ",p$
    write " we get the alternative equations : "$
    deprint(l)>>$
  backup_to_file(pdes,forg,nil)$
  while l
  do <<
    if (null confirm_subst) or (length l = 1) then <<d:=car l;l:=cdr l>>
                                              else <<
      if n>0 then <<
        write"We have the remaining alternative equations : "$
        deprint(l)$
      >>$
      write"Which equation is to be used next? (number, Enter) "$
      repeat <<
        h:=termread()$
        if not fixp h then <<write"This is not a number."$terpri()>>
      >> until fixp h;
      d:=nth(l,h);
      l:=delete(d,l);
      if member(d,ineq_) then <<
        write"It shows that this factor is in the inequality list"$
        terpri()$
        write"of non-zero expressions."$
        terpri()
      >>
    >>;
    if not member(d,ineq_) then <<

      n:=n+1$
      level_:=cons(n,level_)$
      q:=mkeq(d,get(p,'fcts),get(p,'vars),allflags_,nil,
              get(p,'orderings),nil,pdes)$
      if print_ then <<
        print_level(t)$
        terpri()$
        write "CRACK is now called with the new equation ",q," : "$
        deprint(list d)>>$
      % further necessary step to call crackmain():
      recycle_fcts:=nil$  % such that functions generated in the sub-call
                          % will not clash with existing functions
      newpdes:=eqinsert(q,drop_pde(p,pdes,nil))$
      if freeof(newpdes,q) then <<
        write "It turns out that the next factor is a consequence ",
              "of another equation."$ terpri()$
        write "Therefore the investigation of any factors after ",
              "this one is droped."$ terpri()$
        l:=nil
      >>                   else
      to_do_list:=cons(list('subst_level_35,%newpdes,forg,vl_,list q),
                            list q,newpdes),
                       to_do_list)$
      l1:=if pvm_try() and (null collect_sol)
      then remote_crackmain(newpdes,forg) % i.e. l1:=nil
      else crackmain(newpdes,forg)$
%      newfdep:=nil$
%      for each sol in l1 do
%      if sol then <<
%        for each f in caddr sol do
%        if h:=assoc(f,depl!*) then newfdep:=cons(h,newfdep);
%      >>;
%      % newfdep are additional dependencies of the new functions in l1
%      pdes:=car restore_pdes(bak)$ % to restore all global variables and pdes
%      depl!*:=append(depl!*,newfdep);

      if l then << % there are further factors=0 to be investigated
        h:=restore_and_merge(l1,pdes,forg)$
        pdes:= car h;
        forg:=cadr h; % was not assigned above as it has not changed probably
        newineq:=union(list d,newineq);                   % new for %1
        for each h in reverse newineq do <<               % new for %1
          if contradictioncheck(h,pdes) then l:=nil;  % new for %1
          % --> drops factors h in all pdes without asking!!
          % if contradictioncheck then h can not be non-zero
          % but that would be so for all remaining cases --> stop
          if not member(h,ineq_) then addineq(pdes,h)     % new for %1
        >>                                                % new for %1
      >>;

      if not contradiction_ then contrad:=nil$
      if l1 and not contradiction_ then result:=union(l1,result);
      contradiction_:=nil$                           % <--- neu
    >>
  >>$
  delete_backup()$
  contradiction_:=contrad$
  if contradiction_ then result:=nil$
  if print_ then <<
    terpri()$
    write"This completes the investigation of all cases of a factorization."$
    terpri()$
  >>$
  return list result
% by returning `list result' and not just `result', what is returned
% is a list with only a single element. This is indicating that the
% content of what is returned from this procedure is a list of
% crackmain returns and not (pdes,forg) which is returned from
% other modules and which is a list of more than one element.
end$

symbolic procedure split_into_cases(arglist)$
% programmed or interactive introduction of two cases whether a
% given expression is zero or not
begin scalar h,hh,s,pdes,forg,contrad,n,q,l1,
             result,ps,intact$%,newfdep,bak,sol,f,depl
  pdes:=car arglist$
  forg:=cadr arglist$
  if cdddr arglist then h:=cadddr arglist$
  if h=pdes then << % interactive call
    intact:=t$
    terpri()$
    write "Type in the expression for which its vanishing and"$
    terpri()$
    write "non-vanishing should be considered."$
    terpri()$
%  write "Terminate with $ or ; : "$
    write "You can use names of pds, e.g.: "$terpri()$
    write "coeffn(e_12,df(f,x,2),1);    or   df(e_12,df(f,x,2));"$
    terpri()$
    ps:=promptstring!*$
    promptstring!*:=""$
    h:=termxread()$
  >>$
  for each hh in pdes do h:=subst(get(hh,'val),hh,h)$
  h:=reval h;

  if not may_vanish(h) then return <<
    write"According to the known inequalities, ",
         "this expression can not vanish!"$
    terpri()$
    write" --> Back to main menu."$terpri()$
    promptstring!*:=ps$
    nil
  >>$
  if intact then <<
    write"If you first want to consider this expression to vanish and"$ terpri()$
    write"afterwards it to be non-zero then input t"$ terpri()$
    write"                        otherwise input nil : "$
    s:=termread()$
    promptstring!*:=ps$
  >>        else s:=t$

  contrad:=t$
  n:=0$
  %-------------------
  backup_to_file(pdes,forg,nil)$ % moved before again:, should be ok
again:
%  bak:=backup_pdes(pdes,forg)$

  n:=add1 n$
  level_:=cons(n,level_)$
  print_level(t)$
  terpri()$

  if s then <<
    q:=mkeq(h,ftem_,vl_,allflags_,t,list(0),nil,pdes)$
    if print_ then <<
      write "CRACK is now called with the assumption 0 = ",q," : "$
      deprint(list h)$
    >>
  >>   else <<
    if print_ then <<
      write "CRACK is now called with assuming  "$terpri()$
      mathprint h$
      write" to be nonzero. "$
    >>$
    addineq(pdes,h)$
  >>$
  % necessary steps to call crackmain():
  recycle_fcts:=nil$  % such that functions generated in the sub-call
                      % will not clash with existing functions

  % This test comes only now as it drops factors s from all pdes
  if (s=nil) and contradictioncheck(h,car arglist) then <<
    if print_ then <<
      write"According to the system of pdes, this expression must be zero!"$
      terpri()$
      write" --> Back to main menu."$
    >>$
    contradiction_:=nil$
    promptstring!*:=ps$
    l1:=nil$
%    newfdep:=nil$
  >>                                               else <<
    l1:=if pvm_try() and (null collect_sol)
    then remote_crackmain(if null s then pdes
                                    else eqinsert(q,pdes),forg) % ie. l1:=nil
    else crackmain(if null s then pdes else eqinsert(q,pdes),forg)$
%    newfdep:=nil$
%    for each sol in l1 do
%    if sol then <<
%      for each f in caddr sol do
%      if depl:=assoc(f,depl!*) then newfdep:=cons(depl,newfdep);
%    >>;
%    % newfdep are additional dependencies of the new functions in l1
  >>;
%  pdes:=car restore_pdes(bak)$   % to restore all global variables and pdes
%  depl!*:=append(depl!*,newfdep);

  hh:=restore_and_merge(l1,pdes,forg)$
  pdes:= car hh;
  forg:=cadr hh;

  if not contradiction_ then contrad:=nil$
  if l1 and not contradiction_ then result:=union(l1,result);
  contradiction_:=nil$

  if n=1 then <<s:=not s; goto again >>;

  delete_backup()$
  contradiction_:=contrad$
  if contradiction_ then result:=nil$
  if print_ then <<
    terpri()$
    write"This completes the investigation of all cases of a case-distinction."$
    terpri()$
  >>$
  return list result
% by returning `list result' and not just `result', what is returned
% is a list with only a single element. This is indicating that the
% content of what is returned from this procedure is a list of
% crackmain returns and not (pdes,forg) which is returned from
% other modules and which is a list of more than one element.
end$

symbolic procedure stop_batch(arglist)$
begin
 if !*batch_mode then <<
  write"Drop this point from the proc_list_ with 'o, 'cp or quit with 'q."$
  terpri()$
  !*batch_mode:=nil$
 >>$
 batchcount_:=stepcounter_ - 2$
 return {car arglist,cadr arglist}  % only to have arglist involved
end$

symbolic procedure user_defined(arglist)$
begin
 arglist:=nil;  % only to use arglist
end$

symbolic procedure sub_problem(arglist)$
begin scalar ps,s,h,fl,newpdes,sol,pdes,bak,newfdep,f,sub_afterwards$

 if !*batch_mode then return nil;
 terpri()$
 ps:=promptstring!*$
 promptstring!*:=""$
 write"This module so far works only for linear problems."$terpri()$
 write"Do you want to continue (Y/N)? "$
 repeat s:=termread() until (s='y) or (s='n)$
 if s='n then <<
  promptstring!*:=ps$
  return nil
 >>$
 terpri()$

 % Choice
 write"Do you want to specify a set of equation to be solved --> Enter 1"$
 terpri()$
 write"or a set of functions (and then all equations containing"$
 terpri()$
 write"only these functions are selected)                    --> Enter 2: "$
 repeat h:=termread() until h=1 or h=2$

 if h=1 then <<  %------ Input of a subset of equations
  write"Specify a subset of equations to be solved in the form:  "$
  listprint(car arglist)$
  write";"$ terpri()$
  s:=termlistread()$
  if s=nil then newpdes:=nil else
  if not_included(s,car arglist) then <<
   write"Equations ",setdiff(s,car arglist)," are not valid."$
   terpri()$
   newpdes:=nil
  >>                             else <<
   for each h in s do fl:=union(fl,get(h,'fcts));
   newpdes:=s
  >>
 >>     else <<  %------ Input of a subset of functions
  write"Specify a subset of functions to be solved in the form:  "$
  listprint(ftem_)$
  write";"$ terpri()$
  s:=termlistread()$
  if s=nil then newpdes:=nil else
  if not_included(s,ftem_) then <<
   write"Fnctions ",setdiff(s,ftem_)," are not valid."$
   terpri()$
   newpdes:=nil
  >>                             else <<
   fl:=s;
   % Determining a subset of equations containing only these functions
   for each s in car arglist do
   if null setdiff(get(s,'fcts),fl) then newpdes:=cons(s,newpdes)$
   if null newpdes then <<
    write"There is no subset of equations containing only these functions."$
    terpri()
   >>
  >>
 >>;
 if null newpdes then return nil;

 write "Do you want an automatic substitution "$terpri()$
 write "of computed functions afterwards (Y/N)? "$
 repeat s:=termread() until (s='y) or (s='n)$
 if s='y then sub_afterwards:=t
         else sub_afterwards:=nil;

 promptstring!*:=ps$
 write"CRACK is now called with the following subset of equations"$
 terpri()$
 write newpdes$ terpri()$

 bak:=backup_pdes(car arglist,cadr arglist)$
 sol:=crackmain(newpdes,fl)$
 % One could add an dropredund call here
 newfdep:=nil$
 for each s in sol do
 if s then <<
  for each f in caddr s do
  if h:=assoc(f,depl!*) then newfdep:=cons(h,newfdep);
 >>;
 % newfdep are additional dependencies of the new functions in l1
 pdes:=car restore_pdes(bak)$   % to restore all global variables and pdes
 depl!*:=append(depl!*,newfdep);
 ftem_:=union(ftem_,caddar sol)$

 % Test for contradiction or more than one solution
 % to be investigated further
%%%%%%
%%%%%% ACN found mkeq with 7 not 8 args here and has corrected that by
%%%%%% passing pdes as the last arg - but he is not certain that that will
%%%%%% be what is required...
 for each s in caar  sol do
   pdes:=eqinsert(mkeq(s,ftem_,vl_,allflags_,t,list(0),nil,pdes),
                  pdes)$
 for each s in cadar sol do
 if pairp s and (car s='EQUAL) then <<
  h:=mkeq({'DIFFERENCE,caddr s,cadr s},ftem_,vl_,allflags_,t,list(0),nil,pdes);
  pdes:=eqinsert(h,pdes)$
  if sub_afterwards then
  to_do_list:=cons(list('subst_level_35,%pdes,cadr arglist,caddr arglist,
                        list h),
                   to_do_list)
 >>$
 ftem_:=union(ftem_,caddar sol)$

 return {pdes,cadr arglist}
end$


endmodule$

end$
