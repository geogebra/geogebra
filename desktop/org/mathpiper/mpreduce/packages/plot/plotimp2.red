module plotimp2; % Implicit plot: compute the varity {x,y|f(x,y)=c}.

% Author: Herbert Melenk, ZIB Berlin.

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


% data structure:
%
%    point = {x,y,f(x,y),t1,t2,t3,...}
%               where ti are the numbers of the triangles which
%               have this point as a node.
%               The point list is unique - adjacent triangles share
%               the list for equal nodes. The node numbers are
%               updated in place.
%
%    triangle = {t#,p1,p2,p3}
%       triangles are stored in triangle vector by number
%

fluid '(imp2!-triacount!* imp2!-trias!* !*imp2!-trace);

fluid '(!*show_grid !*test_plot plot!-contour* plot!-refine!*);

imp2!-triacount!*:=0;

symbolic procedure ploteval2xyimpl(rx,ry,f,x,y);
  begin scalar ll,l,form,g;
   for each c in plot!-contour!* do
   << form := plot!-form!-prep({'difference,cdr f,c},x,y);
      l:=imp2!-plot(car rx,cadr rx, car ry,cadr ry,
                   plot!-points(nil),form);
      ll:=append(ll,l);
    >>;
    if !*show_grid and null cdr plot!-contour!*
         then g:= imp2!-show!-meshes();
    plotdriver(plot!-2imp,x,y,ll,g,car rx,cadr rx,car ry,cadr ry);
  end;

symbolic procedure imp2!-init();
  << imp2!-finit();
     if null imp2!-trias!* then imp2!-trias!* :=mkxvect()>>;

symbolic procedure imp2!-finit();
  <<if imp2!-trias!* then
      for i:=0:imp2!-triacount!* do xputv(imp2!-trias!*,i,nil);
    imp2!-triacount!*:=0;
  >>;

symbolic procedure mk!-point(x0,y0,fcn);
         (if caddr point then point else
            rederr("Implicit function cannot be plotted")
         ) where point = {x0,y0,apply2(fcn,x0,y0)};

!#if (member 'csl lispsystem!*)
    symbolic procedure deletip1 (u,v);
       % Auxiliary function for DeletIP.
       pairp cdr v and
          (if u=cadr v then rplacd(v,cddr v) else deletip1(u,cdr v));

    symbolic procedure deletip (u,v);
       % Destructive DELETE.
       if not pairp v then v
        else if u=car v then cdr v
        else <<deletip1(u,v); v>>;
!#endif

symbolic procedure imp2!-delete!-pt!-reference(i,p);
    cdr cddr p := deletip(i,cdddr p);

symbolic procedure mk!-tria(i,p1,p2,p3);
  % make a triangle from 3 points. If the number is given,
  % reuse it. Otherwise generate a fresh number.
  begin scalar p; integer i;
    i := i or (imp2!-triacount!* := imp2!-triacount!* #+1);
    p:={i,p1,p2,p3,imp2!-tria!-zerop!*(p1,p2,p3)};
    xputv(imp2!-trias!*,i,p);
    aconc(p1,i); aconc(p2,i); aconc(p3,i);
      if !*imp2!-trace then print!-tria("new triangle ",p);
    return p;
  end;

symbolic procedure print!-tria(tx,w);
  <<prin2 tx; prin2 car w; w:=cdr w;
    prin2l {{car car w,cadr car w,{caddr car w}},
            {car cadr w,cadr cadr w,{caddr cadr w}},
            {car caddr w,cadr caddr w,{caddr caddr w}}};
      terpri();
  >>;

symbolic procedure imp2!-tria!-zerop!*(p1,p2,p3);
 % Here I test whether the triangle contains a zero line.
  begin scalar f1,f2,f3;
    f1 := caddr p1;
    f2 := caddr p2;
    f3 := caddr p3;
    return f1*f2 <= 0.0 or f1*f3 <= 0.0;
  end;

symbolic procedure imp2!-tria!-zerop(w);
 % Fast access to stored zerop property.
     cadddr cdr w;

symbolic procedure imp2!-neighbours p;
 % Compute the direct neighbours of p - the traingles which have
 % two nodes respectively one complete edge in common with p.
  begin scalar l,p1,p2,p3; integer n;
   if fixp p then p:=xgetv(imp2!-trias!*,p);
   n:= car p; p:=cdr p;
   p1:=delete(n,cdddr car p);
   p2:=delete(n,cdddr cadr p);
   p3:=delete(n,cdddr caddr p);
   l:={find!-one!-common(p1,p2),
       find!-one!-common(p2,p3),
       find!-one!-common(p3,p1)};
   while nil memq l do l:=deletip(nil,l);
   return for each w in l collect xgetv(imp2!-trias!*,w);
  end;

symbolic procedure imp2!-edge!-length(p1,p2);
  begin scalar dx,dy;
    fdeclare('imp2!-edge!-length,dx,dy);
    dx:=thefloat car p1 - thefloat car p2;
    dy:=thefloat cadr p1 - thefloat cadr p2;
    return sqrt(dx*dx + dy*dy)
  end;

symbolic procedure imp2!-tria!-surface w;
 begin scalar x1,x2,x3,y1,y2,y3,p1,p2,p3;
   fdeclare('imp2!-tria!-surface,x1,x2,x3,y1,y2,y3);
   w:=cdr w;
   x1:=car (p1:=car w); y1:=cadr p1;
   x2:=car (p2:=cadr w); y2:=cadr p2;
   x3:=car (p3:=caddr w); y3:=cadr p3;
   return abs ((0.5*(x1*(y2-y3) + x2*(y3-y1) + x3*(y1-y2))));
 end;

symbolic procedure imp2!-tria!-length w;
 begin scalar p1,p2,p3;
   w:=cdr w;
   p1:=car w; p2:=cadr w; p3:=caddr w;
   return
    (0.3*(imp2!-edge!-length(p1,p2)
            + imp2!-edge!-length(p2,p3)
              + imp2!-edge!-length(p3,p1)));
 end;

symbolic procedure imp2!-tria!-midpoint(w);
   <<w:= cdr w;
     {(thefloat car car w
        + thefloat car cadr w
           + thefloat car caddr w)/3.0,
      (thefloat cadr car w
        + thefloat cadr cadr w
           + thefloat cadr caddr w)/3.0}
   >>;

symbolic procedure imp2!-tria!-goodpoint(w,fn);
 % Here I assume that there is a zero in the triangle. Compute
 % a point inside the triangle which is as close as possible
 % to the desired line, but without local recomputation of
 % function values.
 begin scalar p1,p2,p3,f1,f2,f3,s1,s2,s3;
   w:=cdr w;
   p1:=car w; p2:=cadr w; p3:=caddr w;
   if (f1:=caddr p1)=0.0 then return {car p1,cadr p1} else
   if (f2:=caddr p2)=0.0 then return {car p2,cadr p2} else
   if (f3:=caddr p3)=0.0 then return {car p3,cadr p3};
   s1:=f1<0.0; s2:=f2<0.0; s3:=f3<0.0;
   return if s1=s2 then
       imp2!-tria!-goodpoint1(p1,f1,p3,f3,p2,f2,fn)
   else if s1=s3 then
       imp2!-tria!-goodpoint1(p1,f1,p2,f2,p3,f3,fn)
   else
       imp2!-tria!-goodpoint1(p2,f2,p1,f1,p3,f3,fn)
 end;

%symbolic procedure imp2!-tria!-goodpoint1(p1,f1,p2,f2,p3,f3,fn);
% % Now I know that f2 has the opposite sign to f1 and f3.
% % I take the linearly interpolated zero of f on p1-p2 and p2-p3
% % return their arithmetic mean value which lies inside the
% % triangle.
%  begin scalar x1,x2,y1,y2,s;
%    fdeclare (x1,x2,y1,y2,s,f1,f2,f3);
%    s:=f1-f2;
%    x1:=(f1*thefloat car p2  - f2*thefloat car p1)/s;
%    y1:=(f1*thefloat cadr p2 - f2*thefloat cadr p1)/s;
%    s:=f3-f2;
%    x2:=(f3*thefloat car p2  - f2*thefloat car p3)/s;
%    y2:=(f3*thefloat cadr p2 - f2*thefloat cadr p3)/s;
%    return {(x1+x2)*0.5, (y1+y2)*0.5};
%  end;

symbolic procedure imp2!-tria!-goodpoint1(p1,f1,p2,f2,p3,f3,fn);
 % Now I know that f2 has the opposite sign to f1 and f3.
 % F1 and f3 are non-zero.
 % I use the midpoint of the p1-p3 edge and look for an
 % approximation of a zero on the connection of the midpoint
 % and p2 using regula falsi.
  begin scalar x1,x2,y1,y2,x3,y3,s;
    fdeclare (x1,x2,x3,y1,y2,y3,s,f1,f2,f3);
    f1:=(f1+f3)*0.5;
    x1:=(thefloat car p1  + thefloat car p3)*0.5;
    y1:=(thefloat cadr p1  + thefloat cadr p3)*0.5;
    x2:=car p2; y2:=cadr p2;
    s:=f2-f1;
    x3:=(x1*f2 - x2*f1)/s;
    y3:=(y1*f2 - y2*f1)/s;
    f3:=apply2(fn,x3,y3);
    if f2*f3>=0 then
    <<s:=f1-f3; x3:=(x3*f1-x1*f3)/s; y3:=(y3*f1-y1*f3)/s>>
      else
    <<s:=f2-f3; x3:=(x3*f2-x2*f3)/s; y3:=(y3*f2-y2*f3)/s>>;
 done:
    return{x3,y3};
  end;

symbolic procedure imp2!-tri!-refine!-one!-tria (w,fn);
 % Refine one triangle by inserting a new point in the mid
 % of the longest edge. Also, refine the triangle adjacent
 % to that edge with the same point.
  begin scalar p1,p2,p3,pn,xn,yn,new,nb,y; integer i;
    scalar x1,x2,y1,y2,d1,d2,d3,s;
    fdeclare (x1,x2,y1,y2,s,d1,d2,d3);
   if fixp w then w :=xgetv(imp2!-trias!*,w); % record.
     if !*imp2!-trace then print!-tria("refine ",w);
   i:=car w; w :=cdr w;

     % delete reference to this triangle.
   imp2!-delete!-pt!-reference(i,car w);
   imp2!-delete!-pt!-reference(i,cadr w);
   imp2!-delete!-pt!-reference(i,caddr w);

     % find longest edge
   d1:=imp2!-edge!-length(car w,cadr w);
   d2:=imp2!-edge!-length(cadr w,caddr w);
   d3:=imp2!-edge!-length(car w,caddr w);
   if d1>=d2 and d1>=d3 then
     <<p1:=car w; p2:=cadr w; p3:=caddr w>>
   else if d2>=d1 and d2>=d3 then
     <<p1:=cadr w; p2:=caddr w; p3:=car w>>
   else
     <<p1:=car w; p2:=caddr w, p3:=cadr w>>;
    % identify the neighbour triangle.
   nb := find!-one!-common(cdddr p1,cdddr p2);
    % compute new point almost at the mid.
   s:=0.45+0.01*random(10);
   x1:=car p1; y1:=cadr p1;
   x2:=car p2; y2:=cadr p2;
   xn:=x1*s+x2*(1.0-s);
   yn:=y1*s+y2*(1.0-s);
   pn:=mk!-point(xn,yn,fn);
construct:
    % construct new triangles
   new:=mk!-tria(i,p1,pn,p3).new;
   new:=mk!-tria(nil,p2,pn,p3).new;
    % update the neighbour, if there is one
   if null nb then return new;
   w:=nb; nb:=nil;
   if fixp w then w :=xgetv(imp2!-trias!*,w); % record.
   i:=car w; w:=cdr w;
   imp2!-delete!-pt!-reference(i,car w);
   imp2!-delete!-pt!-reference(i,cadr w);
   imp2!-delete!-pt!-reference(i,caddr w);

    % find the far point
   p3 := if not((y:=car w) eq p1 or y eq p2) then y else
         if not((y:=cadr w) eq p1 or y eq p2) then y else
         caddr w;
   goto construct;
  end;

%symbolic procedure imp2!-tri!-refine!-one!-tria!-center (w,fn);
% % Refine one triangle by inserting a new point in the center.
%  begin scalar p1,p2,p3,pn,xn,yn,new,nb,y; integer i;
%    scalar x1,x2,x3,y1,y2,y3;
%   fdeclare (x1,x2,x3,y1,y2,y3);
%   if fixp w then w :=xgetv(imp2!-trias!*,w); % record.
%     if !*imp2!-trace then print!-tria("refine ",w);
%   i:=car w; w :=cdr w;
%
%     % delete reference to this triangle.
%   imp2!-delete!-pt!-reference(i,car w);
%   imp2!-delete!-pt!-reference(i,cadr w);
%   imp2!-delete!-pt!-reference(i,caddr w);
%
%    % compute center.
%   p1:=car w; p2:=cadr w; p3:=caddr w;
%   x1:=car p1; y1:=cadr p1;
%   x2:=car p2; y2:=cadr p2;
%   x3:=car p3; y3:=cadr p3;
%   xn:=(x1+x2+x3)*0.33333;
%   yn:=(y1+y2+y3)*0.33333;
%   pn:=mk!-point(xn,yn,fn);
%construct:
%    % construct new triangles
%   new:=mk!-tria(i,p1,p2,pn).new;
%   new:=mk!-tria(nil,p2,p3,pn).new;
%   new:=mk!-tria(nil,p1,p3,pn).new;
%   return new;
%  end;

symbolic procedure find!-one!-common(u,v);
  % fast equivalent to "car intersection(u,v)".
  if null u then nil else
  if memq(car u,v) then car u else
     find!-one!-common(cdr u,v);

%%%%%% Main program for implicit plot.

symbolic procedure imp2!-plot(xlo,xhi,ylo,yhi,pts,fcn);
  begin scalar p1,p2,p3,p4,sf,z;
   imp2!-init();
    % setup initial triangularization.
   p1:=mk!-point(xlo,ylo,fcn);
   p2:=mk!-point(xhi,ylo,fcn);
   p3:=mk!-point(xhi,yhi,fcn);
   p4:=mk!-point(xlo,yhi,fcn);
   mk!-tria(nil,p1,p2,p3);
   mk!-tria(nil,p1,p3,p4);
   sf:=((xhi-xlo)+(yhi-ylo))*1.5/float pts;
   z:=imp2!-plot!-refine(sf,fcn);
     if !*imp2!-trace then
       for each w in z do print!-tria("zero triangle:",w);
if !*test_plot then print "collect";
   z:=imp2!-plot!-collect(z);
if !*test_plot then print "draw";
   z:=imp2!-plot!-draw(z,fcn);
   if not !*show_grid then imp2!-finit();
   return z;
 end;

symbolic procedure imp2!-plot!-refine(sflimit,fcn);
  begin scalar z,zn,w,s,limit2,again;
    integer i,j;
    limit2 := 0.15*sflimit;

  % In the first stage I refine all triangles until they
  % are fine enough for a coarse overview.
    again := t;

if !*test_plot then print "phase1";
phase1:
    z:=nil; again:=nil;
    for i:=imp2!-triacount!* step -1 until 1 do
    << w := xgetv(imp2!-trias!*,i);
       if imp2!-tria!-length w > sflimit then
           <<again:=t; imp2!-tri!-refine!-one!-tria (w,fcn)>>
        else if not again and imp2!-tria!-zerop w
           then z:=car w.z;
    >>;
    j:=j #+ 1;
    if j+j #< plot!-refine!* or again then goto phase1;

 % Next I refine further only the triangles which contain a zero.
 % I must store in z only the numbers of triangles because these
 % may be modified as side effects by copying.

if !*test_plot then print "phase2";
phase2:
    for each w in z do
      if (s:=imp2!-tria!-length (w:=xgetv(imp2!-trias!*,w))) >limit2
         then <<for each q in imp2!-tri!-refine!-one!-tria (w,fcn) do
                if imp2!-tria!-zerop q and not memq(car q,zn)
                  then zn:=car q.zn>>;
    z:=zn; zn:=nil;
    if z then goto phase2;

 % In the third phase I refine those critical triangles futher:
 % non-zeros with two zero neighbours. These may be turning points
 % or bifurcations.

if !*test_plot then print "phase3";
phase3:
    for i:=imp2!-triacount!* step -1 until 1 do
      imp2!-refine!-phase3(i,i,plot!-refine!*/2,fcn,limit2*0.3);

 % Return the final list of zeros in ascending order.
    z:=for i:=1:imp2!-triacount!* join
      if imp2!-tria!-zerop(w:=xgetv(imp2!-trias!*,i)) then {w};
   return z;
  end;

symbolic procedure imp2!-refine!-phase3(i,low,lev,fn,lth);
  begin scalar w; integer c;
    if lev=0 then return nil;
    w:=if numberp i then xgetv(imp2!-trias!*,i) else i;
    if car w<low or imp2!-tria!-length w < lth then return nil;
    lev:=lev-1;
    for each q in imp2!-neighbours w do
      if imp2!-tria!-zerop q then c:=c+1;
    if imp2!-tria!-zerop w
       then (if eqn(c,2) then return nil)
       else (if c #< 2 then return nil);
    for each q in imp2!-tri!-refine!-one!-tria (w,fn) do
        imp2!-refine!-phase3(q,low,lev,fn,lth);
  end;

symbolic procedure imp2!-plot!-collect(z);
   begin scalar lines,l,q,p,s;

    for each w1 in z do
       for each w2 in imp2!-neighbours w1 do
          if car w2 > car w1 and imp2!-tria!-zerop w2 then
            q:=(w1.w2) . q;

 lineloop:
     if null q then return lines;
     l:={caar q, (p:=cdar q)}; q:= cdr q;
      % first I extend the back end.
     while q and p do
     <<
       if(s:= atsoc(p,q)) then l:=nconc(l,{p:=cdr s}) else
       if(s:=rassoc(p,q)) then l:=nconc(l,{p:=car s});
       if s then q:=delete(s,q) else p:=nil;
     >>;

      % next I extend the front end.
     p:=car l;
     while q and p do
     <<
       if(s:=rassoc(p,q)) then l:=(p:=car s).l else
       if(s:= atsoc(p,q)) then l:=(p:=cdr s).l;
       if s then q:=delete(s,q) else p:=nil;
     >>;

     lines := l.lines;
     goto lineloop;
   end;

symbolic procedure imp2!-plot!-draw(l,fn);
  begin scalar r,s,q;
  q:=for each w in l collect
   <<r:=nil;
     for each q in w join
       <<s:=imp2!-tria!-goodpoint(q,fn);
         if r neq s then {r:=s}>>
   >>;
  return q;
  end;


symbolic procedure imp2!-show!-meshes();
 % generate plot of meshes;
  begin scalar d,l,w,s,p1,p2; integer i;
    i:=1;
 loop:
    w:=xgetv(imp2!-trias!*,i);
    if null w then
    <<imp2!-finit(); return l>>;
    w:=cdr w;
    d:= {{car car w, cadr car w},
         {car cadr w,cadr cadr w},
         {car caddr w,cadr caddr w},
         {car car w, cadr car w}} ;
    while d and cdr d do
    <<p1:=car d; p2:=cadr d; d:=cdr d;
      if car p1 > car p2 then <<w:=p2;p2:=p1;p1:=w>>;
      s:={p1,p2};
      if not member(s,l) then l:=s.l
    >>;
    i:=i+1;
    goto loop;
   end;

endmodule; % plotimp2

end;
