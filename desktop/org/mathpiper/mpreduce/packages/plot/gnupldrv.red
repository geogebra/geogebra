module gnupldrv; % main GNUPLOT driver.

% Author: Herbert Melenk.
% Modifications by: Arthur Norman.

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


fluid '(plotstyle!*);

global '(!*plotusepipe !*trplot !*plotkeep !*plotrefine plotheader!*
         plotcleanup!* plotoptions!*);

switch  plotusepipe;       % use pipes

switch  trplot;            % list Gnuplot commands to REDUCE
                           % output (e.g. screen).

switch  plotkeep;          % if ON, the command and data files are
                           % not erased after calling Gnuplot.

global '(
        !*plotpause        % Gnuplot pause command at the end:
                           % nil: no pause
                           % -1: Gnuplot will ask the user for
                           %     a return key.
                           % number>0: Gnuplot will wait <number>
                           % seconds.


      plotcommand!*          % string: command to start gnuplot


      plotcmds!*             % file for collecting commands

      plotdta!*            % files for collecting data

      plotheader!*           % list of Gnuplot commands (strings)
                           % for initializing GNUPLOT

      plotcleanup!*          % list of system commands (strings)
                           % for cleaning up after gnuplot

);

if null plotcommand!* then rederr
      " no support of GNUPLOT for this installation";

if null getd 'explodec then copyd ('explodec, 'explode2); % for PSL

fluid '(plot!-files!* plotpipe!*);

symbolic procedure gp!-init();
   <<
    plot!-files!* := plotdta!*;
    plotoptions!*:=  nil;
    PlotOpenDisplay();
  >>;

put('gnuplot,'init,'gp!-init);

symbolic procedure plot!-filename();
   <<plot!-files!* := cdr plot!-files!*; u>>
      where u=if null plot!-files!* then
          rederr "ran out of scratch files" else car plot!-files!*;

symbolic procedure gp!-reset();
   if !*plotusepipe and plotpipe!* then
    <<  plotprin2 "exit"; plotterpri();
        close plotpipe!*; plotpipe!*:=nil;>>;

put('gnuplot,'reset,'gp!-reset);

symbolic procedure PlotOpenDisplay();
   begin
    if null plotpipe!* then
    if not !*plotusepipe then plotpipe!* := open(plotcmds!*,'output)
        else <<plotpipe!* :=pipe!-open(plotcommand!*,'output)>>;
    if null plotheader!* then nil else
    if atom plotheader!* then <<plotprin2 plotheader!*; plotterpri()>>
     else if eqcar(plotheader!*,'list) then
      for each x in cdr plotheader!* do <<plotprin2 x; plotterpri()>>
     else typerr(plotheader!*,"gnuplot header");
   end;

symbolic procedure gp!-show();
   if !*plotusepipe and plotpipe!* then
     << channelflush  plotpipe!*; >>
    else
   <<if !*plotpause then plotprin2lt{"pause ",!*plotpause};
     close  plotpipe!*;
     plotpipe!* := nil;
     if plotcommand!* then
       <<plot!-exec plotcommand!*;
         if not !*plotkeep then
            for each u in plotcleanup!* do system u;
       >>;
    >>;

put('gnuplot,'show,'gp!-show);

symbolic procedure plot!-exec u; system u;

symbolic procedure plotprin2 u;
   <<prin2 u; wrs v;
     if !*trplot then prin2 u>> where v=wrs plotpipe!*,!*lower=t;

symbolic procedure plotterpri();
   <<terpri(); wrs v;
     if !*trplot then terpri() >> where v=wrs plotpipe!*;

symbolic procedure plotprin2lt l;
   <<for each x in l do plotprin2 x; plotterpri()>>;

fluid '(plotprinitms!*);

symbolic procedure plotprinexpr u;
   begin scalar plotprinitms!*,!*lower,v;
     !*lower:=t;
     v := wrs plotpipe!*;
     plotprinitms!* := 0;
     if eqcar(u,'file) then
        <<prin2 '!"; prin2 cadr u;prin2 '!"; prin2 " ">>
     else
        errorset(list('plotprinexpr1,mkquote u,nil),nil,nil);
     wrs v;
   end;

symbolic procedure plotprinexpr1(u,oldop);
   begin scalar op;
     if plotprinitms!* > 5 then
        <<prin2 "\"; terpri(); plotprinitms!*:=0>>;
     if atom u then
        <<prin2 if u='e then 2.718281 else
                if u='pi then 3.14159 else u;
          plotprinitms!* := plotprinitms!*+1>>
          else
     if eqcar(u,'!:rd!:) then
         plotprinexpr1 (if atom cdr u then cdr u else
                           bf2flr u,nil)
          else
     if (op:=car u) memq '(plus times difference quotient expt) then
           plotprinexpr2(cdr u,get(car u,'PRTCH),
               oldop and not (op memq(oldop memq
                      '(difference plus times quotient expt)))
               ,op)
          else
     if op='MINUS then
          <<prin2 "(-";
            plotprinexpr1(cadr u,t);
            prin2 ")">>
          else
     if get(car u,'!:RD!:) then
         <<prin2 car u; plotprinexpr2(cdr u,'!, ,t,nil)>>
          else
        typerr(u," expression for printing")
   end;

symbolic procedure plotprinexpr2(u,sep,br,op);
   <<if br then prin2 " (";
     while u do
     <<plotprinexpr1(car u,op);
       u := cdr u;
       if u then prin2 sep>>;
     if br then prin2 ") "
   >>;

symbolic procedure gnuploteval u;
 % Support of explicit calls to GNUPLOT in algebraic mode.
  begin scalar m,evallhseqp!*;
    evallhseqp!* := t;
    m:=plotrounded(nil);
    PlotOpenDisplay();
    for each v in u do
    <<plotprinexpr reval v; plotprin2 " ">>;
    plotterpri();
    plotrounded(m);
  end;

put('gnuplot,'psopfn,'gnuploteval);

% Declare options which are supported by GNUPLOT:

flag ('(

          % keyword options
    contour nocontour logscale nologscale surface nosurface

          % equation type options
    hidden3d xlabel ylabel zlabel title size terminal view output

),'gp!-option);

put('gnuplot,'option,'gp!-option);

symbolic procedure plotpoints u;
  begin scalar f,fn,of,dim,w;
     fn := plot!-filename();
     f := open(fn,'output);
     of := wrs f;
     w:={'plotpoints0,mkquote(nil.u)};
     dim:=errorset(w,t,nil);
     wrs of;
     close f;
     if ploterrorp dim then
        rederr "failure during plotting point set";
     return if car dim=2 then {'file,fn,'x} else {'file,fn,'x,'y};
  end;

symbolic procedure plotpoints0 u;
  begin scalar z,bool;
    integer n;
   for each x in cdr u do
    if not bool and eqcar(x,'list) then n:=plotpoints0 x
      else
     <<bool:=t; n:=n#+1;
       z:=rdwrap reval x;
       if not numberp z then <<wrs nil; typerr(x,"number")>>;
       prin2 z; prin2 " ";
     >>;
   terpri();
   return n;
  end;

symbolic procedure plotpoints1 u;
  begin scalar f,fn,of,y;
     fn :=  plot!-filename();
     f := open(fn,'output);
     of := wrs f;
     for each x in u do
     <<for each y in x do gp!-plotprinpoint y;
       terpri();
     >>;
     wrs of;
     close f;
     return fn;
  end;

symbolic procedure gp!-plotgrids g;
  begin scalar f,fn,of,y;
     fn :=  plot!-filename();
     f := open(fn,'output);
     of := wrs f;
     for each u in g do
     <<for each x in u do
         <<
         for each y in x do gp!-plotprinpoint y;
         terpri();
         >>;
     >>;
     wrs of;
     close f;
     return fn;
  end;


symbolic procedure gp!-plotquads u;
   % each quad is a list of 4 points
   % p1,p2,p3,p4. Gnuplot needs a regular grid -
   %    therefore we print them as p1,p2 / p4,p3
  begin scalar f,fn,of;
     fn :=  plot!-filename();
     f := open(fn,'output);
     of := wrs f;
     for each q in u do
     <<gp!-plotprinpoint car q;
       gp!-plotprinpoint cadr q;
       terpri();
       gp!-plotprinpoint cadddr q;
       gp!-plotprinpoint caddr q;
       terpri(); terpri();
     >>;
     wrs of;
     close f;
     return fn;
  end;

symbolic procedure gp!-plotprinpoint y;
    << if null y or nil member y then t else
           for each z in y do <<plotprin2number z; prin2 " ">>;
         terpri()
    >>;

symbolic procedure plotprin2number u;
  prin2 if floatp u and abs u < plotmin!* then "0.0" else u;


flag ('(xlabel ylabel zlabel output title),'plotstring);

symbolic procedure gp!-plotoptions();
  <<if not('polar memq plotoptions!*) then
      plotoptions!* := 'nopolar . plotoptions!*;
    if not('contour memq plotoptions!*) then
      plotoptions!* := 'nocontour . plotoptions!*;
    if not('title  memq plotoptions!*) then
      plotoptions!* := '(title . "REDUCE Plot") . plotoptions!*;
  for each x in plotoptions!* do
    begin
      scalar a, b;
      a := x;
      if not idp a then a := car a;
      b := explodec a;
      if eqcar(b, 'n) and eqcar(cdr b, 'o) then <<
         a := compress cddr b;
         b := "unset " >>
      else b := "set ";
      plotprin2 b;
      if idp x then plotprin2 a
      else
      <<plotprin2 a;
        plotprin2 " ";
        if flagp(car x,'plotstring) then plotprin2 '!";
        plotprin2 cdr x;
        if flagp(car x,'plotstring) then plotprin2 '!">>;
      plotterpri()
    end;
  >>;

symbolic procedure plotstyle1();
   if plotstyle!* then
    <<plotprin2 " \";
     plotterpri();
     plotprin2 " with ";
     plotprin2 plotstyle!*;
     plotprin2 " ";
   >>;

symbolic procedure plotstyle option;
  if option memq '(lines points linespoints impulses dots errorbars
                        boxes boxerrorbars boxxyerrorbars candlesticks
                        financebars fsteps histeps steps vector
                        xerrorbars xyerrorbars yerrorbars)
     then plotstyle!* := option
  else typerr(option, "plot style option");

put('style,'gp!-do,'plotstyle);


% Drivers for different picture types.

symbolic procedure gp!-2exp(x,y,pts,fp);
  % x:   name of x coordinate,
  % y:   name of y coordinate,
  % pts: list of computed point sets,
  % fp:  list of user supplied point sets.
  begin scalar cm,cm1;
     plotoptions!* := 'noparametric .  plotoptions!*;
     plotprin2lt{"set size 1,1"};
     plotprin2lt{"set xlabel ",'!",x,'!"};
     plotprin2lt{"set ylabel ",'!",y,'!"};
     gp!-plotoptions();
     plotprin2lt{"unset key"};
     if pts or fp then plotprin2 "plot ";

     for each f in reversip pts do
     << if cm then <<plotprin2 ",\"; plotterpri()>>;
        plotprin2 "'"; plotprin2 plotpoints1 f; plotprin2 "'";
        plotstyle1(); cm:=t;
     >>;
     if fp then
     << if cm then <<plotprin2 ",\"; plotterpri()>>;

        if atom fp then <<
            plotprin2 "'"; plotprin2 fp; plotprin2 "'";
            if cm then plotprin2 " with points" else plotstyle1();
        >> else
        foreach ff in fp do % WN 25.9.98 (Allowing for colourful lines)
        <<   if cm1 then <<plotprin2 ",\"; plotterpri()>>;
             plotprin2 "'"; plotprin2 ff; plotprin2 "'";
             if cm then plotprin2 " with points" else plotstyle1();
             cm1 := t;
     >>; >>;
     plotterpri();
  end;

put('gnuplot,'plot!-2exp,'gp!-2exp);

symbolic procedure badpointp u;
      null u or nil memq u;

symbolic procedure gp!-3exp(x,y,z,f);
 % x:   name of x coordinate,
 % y:   name of y coordinate,
 % z:   name of z coordinate,
 % f:   orthogonal list of point lists.
  begin scalar h;     % bad.
   %  h:=member('hidden3d,plotoptions!*);
    % if h then f:=for each l in f collect
      % for each p in l collect {caddr p};
     if z = 'points then z := 'z else f:=gp!-plotgrids f;
     plotprin2lt{"unset hidden3d"};
     if not h then plotoptions!* := 'parametric .
           delete('noparametric,plotoptions!*)
        else
            plotoptions!* := 'noparametric .
           delete('parametric,plotoptions!*);
     plotprin2lt{"set view 60,30,1,1"};
     plotprin2lt{"set size 1,1"};
     if h then plotprin2lt{"set format xy ",'!",'!"};
     plotprin2lt{"set xlabel ",'!",x,'!"};
     plotprin2lt{"set ylabel ",'!",y,'!"};
     plotprin2lt{"set zlabel ",'!",z,'!"};
     gp!-plotoptions();
     plotprin2lt{"unset key"};
     plotprin2 "splot ";
     plotprin2 "'"; plotprin2 f; plotprin2 "'";
     plotprin2 " with lines ";
     plotterpri();
     plotprin2lt{"unset hidden3d"};
     plotprin2lt{"set format xy"};
  end;

put('gnuplot,'plot!-3exp!-reg,'gp!-3exp);

symbolic procedure gp!-reg2quads f;
 % convert a regular grid structure to a sequence of quadrangles.
 begin scalar l,l1,l2,p1,p2,p3,p4;
   while f and cdr f do
   <<l1:=car f; l2:= cadr f; f:= cdr f;
     while l1 and cdr l1 do
     <<p1 := car l1; l1:= cdr l1; p2:= car l1;
       p4 := car l2; l2:= cdr l2; p3:= car l2;
       if not badpointp p1 and not badpointp p2
        and not badpointp p3 and not badpointp p4 then
          l:={p1,p2,p3,p4} . l
   >> >>;
   return l;
  end;

symbolic procedure gp!-3quads(x,y,z,f);
 % x:   name of x coordinate,
 % y:   name of y coordinate,
 % z:   name of z coordinate,
 % f:   list of quadranges in 3 dim space
  begin scalar h;
     h:=member('hidden3d,plotoptions!*);
     f:=gp!-plotquads f;
     plotprin2lt{"unset hidden3d"};
     plotoptions!* := 'parametric .
           delete('noparametric,plotoptions!*);
     plotprin2lt{"set view 60,30,1,1"};
     plotprin2lt{"set size 1,1"};
     if h then plotprin2lt{"set format xy ",'!",'!"};
     plotprin2lt{"set xlabel ",'!",x,'!"};
     plotprin2lt{"set ylabel ",'!",y,'!"};
     plotprin2lt{"set zlabel ",'!",z,'!"};
     gp!-plotoptions();
     plotprin2lt{"unset key"};
     plotprin2 "splot ";
     plotprin2 "'"; plotprin2 f; plotprin2 "'";
     plotprin2 " with lines ";
     plotterpri();
     plotprin2lt{"unset hidden3d"};
     plotprin2lt{"set format xy"};
  end;

put('gnuplot,'plot!-3quads,'gp!-3quads);

symbolic procedure gp!-2imp(x,y,l,g,xmin,xmax,ymin,ymax);
 % x,y:   names of coordinates,
 % l:     point lists for funtion,
 % g:     nil or point lists for grid,
 % xmin..ymax: minimum and maximum coordinate values.
  begin scalar f,q;
    q:={{xmin,ymin},nil,{xmin,ymax},nil,
        {xmax,ymin},nil,{xmax,ymax}};
    plotoptions!* := 'noparametric .  plotoptions!*;
    f:=plotpoints1 (q.l);
    plotprin2lt{"set size 1,1"};
    plotprin2lt{"set xlabel ",'!",x,'!"};
    plotprin2lt{"set ylabel ",'!",y,'!"};
    gp!-plotoptions();
    plotprin2lt{"unset key"};
    plotprin2 "plot "; plotprin2  "'"; plotprin2 f; plotprin2 "'";
    plotprin2 " with lines";
    if g then
    <<plotprin2 ", '"; plotprin2 plotpoints1 g;
      plotprin2 "' with lines";
    >>;
    plotterpri();
  end;

put('gnuplot,'plot!-2imp,'gp!-2imp);

endmodule;

end;
