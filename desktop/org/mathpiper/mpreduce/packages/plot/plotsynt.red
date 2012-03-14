module plotsynt; % Support for the syntax of the plot command.

% Author: Herbert Melenk.

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


fluid '(bye!-actions!*);

% Create .. as the infix operator if not yet done.

!*msg := nil;  % prevent message  ".. redefined" during load

newtok '( (!. !.) !*interval!*);

if not(gettype '!*interval!* = 'operator) then
<<
   precedence .., or;
   algebraic operator ..;
   put('!*interval!*,'PRTCH,'! !.!.! );
>>;

mkop 'point;

!*msg := t;

fluid '(plot!-points!* plot!-refine!* plot!-contour!*);

global '(plot_xrange plot_yrange plot_zrange);
share plot_xmesh,plot_ymesh,plot_xrange,plot_yrange,plot_zrange;

fluid '(plotprecision!*);

plotprecision!* := 0.9995;

fluid '(!*show_grid test_plot);

switch show_grid;
switch test_plot; % for test printouts

if null plotmax!* then
<<
   load!-package 'arith;
   if not !!plumax then roundconstants();
   plotmax!* := !!plumax;     % IEEE double precision
>>;

plotmin!*:= 1.0/plotmax!*;


fluid '(plotranges!* plotfunctions!*  plotstyle!* !*plotoverflow
        !*roundbf);

put('plot,'psopfn,'ploteval);

symbolic procedure ploteval u;
  begin scalar m,!*exp;
    if null plotdriver!* then
      rederr "no active device driver for PLOT";
    m:=plotrounded(nil);
    plot!-points!* := {20};
    plot!-refine!* := 8;
    !*plotoverflow := nil;
    plotranges!* := plotfunctions!* := nil;
    plotstyle!* := 'lines;
    bye!-actions!* := union('((plotreset)),bye!-actions!*);
    plotdriver(init);
    for each option in u do ploteval1 plot!-reval option;
    errorset('(ploteval2),t,nil);
    plotrounded(m);
  end;

symbolic procedure plot!-reval u;
 % Protected call reval: simplify u, but don't call any
 % algebraic procedure.
   begin scalar w;
     w:={nil};
     u:=plot!-reval1(u,w);
     return car w and u or reval u;
   end;

symbolic procedure plot!-reval1(u,w);
  if idp u then reval u else
  if atom u or eqcar(u,'!:dn!:) or get(car u,'dname) then u else   %WN
  if eq (car u,'!*sq) then plot!-reval1(reval u,w) else
     <<if flagp(car u,'opfn) and
         memq(car u,'(first second rest rhs lhs)) then
     <<      u := reval u;         % lex  Robin Tucker  % WN
        plot!-reval1(u,w)>> else
   << if flagp(car u,'opfn) then car w:=t;
    car u . for each q in cdr u collect plot!-reval1(q,w) >> >>;

symbolic procedure ploteval1 option;
   begin scalar x,do;
     do := get(plotdriver!*,'do);
     if pairp option and (x:=get(car option,do))
             then apply(x,list option) else
     if pairp option and (x:=get(car option,'plot!-do))
             then apply(x,list option) else
     if eqcar(option,'equal) and (x:=get(cadr option,do))
             then apply(x,list caddr option) else
     if eqcar(option,'equal) and (x:=get(cadr option,'plot!-do))
             then apply(x,list caddr option)
       else ploteval0 option;
   end;

symbolic procedure ploteval0 option;
  begin scalar l,r,opt,w;
    opt:=get(plotdriver!*,'option);
    if flagp(option,opt) then
      <<plotoptions!*:=option . plotoptions!*; return>>;
    if eqcar(option,'list) then
      <<option := cdr option;
        if option and eqcar(car option,'list) then
          return (plotfunctions!*:=
             ('points.plotpoints option).plotfunctions!*);
        for each o in option do ploteval0 o; return;
      >>;
    if eqcar(option,'equal) and flagp(cadr option,opt) then
      <<plotoptions!*:=(cadr option.caddr option). plotoptions!*;
       return>>;
    if not eqcar(option,'equal) then
      <<plotfunctions!*:= (nil.option) . plotfunctions!*; return>>;

      % Handle equations.
    l:=plot!-reval cadr option;
    r:=plot!-reval caddr option;
    if plot!-checkcontour(l,r) then return
      plotfunctions!*:=('implicit.l) . plotfunctions!* else %WN 7.3.96
        if not idp l then typerr(option,"illegal option in PLOT");

    if l memq '(size terminal view) then
      <<plotoptions!*:=(l.r).plotoptions!*; return>>;

       % iteration over a range?
    if eqcar(r,'times) and eqcar(caddr r,'!*interval!*)
        and evalnumberp(w:=cadr r) and evalgreaterp(w,0) and
        not evalgreaterp(w,1)
     then <<plot!-points!*:=append(plot!-points!*,
              {l.reval{'floor,{'quotient,1,w}}});
            r:=caddr r>>;

    if eqcar(r,'quotient) and eqcar(cadr r,'!*interval!*)
       and fixp caddr r and caddr r > 0
     then <<plot!-points!*:=append(plot!-points!*,{l.caddr r});
            r:=cadr r>>;

       % range?
    if eqcar(r,'!*interval!*) then
     <<r:='!*interval!* . revalnuminterval(r,t);
       plotranges!* := (l . r) . plotranges!*>>
      else
       plotfunctions!* := (l . r) . plotfunctions!*;
  end;

symbolic procedure ploteval2 ();
   % all options are collected now;
  begin scalar dvar,ivars,para,impl;
   for each u in plotfunctions!* do
     <<impl:=impl or car u eq 'implicit;
       para:=eqcar(cdr u,'point);
       if impl and dvar and dvar neq car u then
          rederr "mixture of implicit and regular plot not supported";
       dvar:=car u or dvar;
       ivars := plotindepvars(cdr u,ivars)>>;
      % classify
   if null dvar then
   <<dvar:='(x y z);
     for each x in ivars do dvar:=delete(x,dvar);
     if dvar then dvar:=if 'y memq dvar then 'y else car dvar;
   >>;
   if para and length ivars=1 then plotevalpara1(car ivars) else
   if para and length ivars=2 then plotevalpara2(car ivars,cadr ivars)
    else if length ivars=1 then ploteval2x(car ivars,dvar) else
   if length ivars=2 then ploteval3xy(car ivars,cadr ivars,dvar) else
  % WN was besseres!!  if length ivars=3 and impl then
            ploteval3impl('x,'y,'z); %car ivars,cadr ivars,caddr ivars);
  comment  else typerr('list . for each p in plotfunctions!* collect
                         if null car p then cdr p else
                         {'equal,car p,cdr p},
                " plot option or function");
  plotdriver(show);
  end;

symbolic procedure plot!-checkcontour(l,r);
  % true if the job is a contour expression.
   if length plotindepvars(l,nil)=2
      or length plotindepvars(l,nil)=3 then % WN 7.3.96
     if r=0 then <<plot!-contour!*:={0};t>>
       else eqcar(r,'list) and
     <<plot!-contour!*:= for each x in cdr r collect
        <<x:=plot!-reval x; l:=l and adomainp x; x>>;
       l>>;

symbolic procedure plotrange(x,d);
   begin scalar y;
     y:=assoc(x,plotranges!*);
     y:=if y then cdr y else d;
     if y=0 or null y then % return nil;
     y:={'!*INTERVAL!*, - plotmax!*, plotmax!*};
     if not eqcar(y,'!*INTERVAL!*) then
        typerr(y,"plot range");
     return {plotevalform0(rdwrap cadr y,nil) ,
             plotevalform0(rdwrap caddr y,nil)};
   end;

symbolic procedure plot!-points(x);
  (if w then cdr w else car plot!-points!*)
    where w=assoc(x,cdr plot!-points!*);

symbolic procedure plotseteq(u,v);
    null u and null v or car u member v
       and plotseteq(cdr u,delete(car u,v));

symbolic procedure plotindepvars(u,v);
    if idp u then
       if member(u,v) or member(u,'(e pi))
                      or u eq 'i and !*complex then v
               else  u . v
    else if eqcar(u,'file) then cddr u
    else if pairp u then
      if eqcar(u,'!:dn!:) or get(car u,'dname) then v else
% WN    if get(car u,'dname) then v else
      if member(car u,'(plus minus difference times quotient expt)) or
         get(car u,'!:RD!:) or get(car u,'simpfn)
            or eqcar(getd(car u),'expr)
       then <<for each x in cdr u do v:=plotindepvars(x,v); v>>
     else typerr(u,"expression in function to plot")
    else v;

remprop('plotshow,'stat);

symbolic procedure plotshow();
   plotdriver(show);

put('plotshow,'stat,'endstat);

remprop('plotreset,'stat);

symbolic procedure plotreset();
   plotdriver(reset);

put('plotreset,'stat,'endstat);

put('points,'plot!-do,
    function(lambda(x);car plot!-points!*:=ieval x));

put('refine,'plot!-do,
    function(lambda(x);plot!-refine!*:=ieval x));

endmodule; % plotsynt.

end;
