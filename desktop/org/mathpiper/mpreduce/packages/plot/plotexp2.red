module plotexp2; % Compute explicit 2-dim graph y=f(x);

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


symbolic procedure ploteval2x(x,y);
  begin scalar xlo,xhi,ylo,yhi,rx,ry,fp,fcn,fcns,pts;
     if y='implicit then
        rederr "no implicit plot in one dimension";
     rx:=plotrange(x,
       reval(plot_xrange or '(!*interval!* -10 10)));
     xlo:=car rx;
     xhi:=cadr rx;
     fcns:= reverse plotfunctions!*;
     ry:=plotrange(y, reval(plot_yrange or nil));
     if ry then <<ylo:=car ry; yhi:=cadr ry>>;
     while fcns do
     <<fcn := car fcns; fcns := cdr fcns;
       if eqcar(fcn,'points) then fp:=caddr fcn . fp else % WN 25.9.98
       pts:=ploteval2x1(cdr fcn,x,xlo,xhi,ylo,yhi).pts;
     >>;
     plotdriver(plot!-2exp,x,y,pts,fp);
  end;

symbolic procedure ploteval2x1(f,x,xlo,xhi,ylo,yhi);
   begin scalar plotsynerr!*,l,d,d0,u,v,vv,p,mx,mn,ff;
        scalar plotderiv!*;
        integer nx;
     % compute algebraic derivative.
    ff:= errorset({'reval,mkquote {'df,f,x}},nil,nil);
    if not errorp ff and not smemq('df,car ff) then
% Hier irrte Goethe.  % This comment is for Herbert, please keep it
%      plotderiv!*:= rdwrap plotderiv!* . plotderiv!*;
       plotderiv!*:= rdwrap car ff . car ff;
    ff:=rdwrap f;
    p:=float (nx:=plot!-points(x));
    d:=(d0:=(xhi-xlo))/p;
    v:=xlo;
    for i:=0:nx  do
     <<vv:=if i=0 or i=nx  then v
           else v+d*(random(100)-50)*0.001;
       u:= plotevalform(ff,f,{x.vv});
       if plotsynerr!* then typerr(f,"function to plot");
       if eqcar(u,'overflow) then u:=nil;
       if u then
       <<
          if yhi and u>yhi then u:=yhi else
          if ylo and u<ylo then u:=ylo;;
          if null mx or u>mx then mx:=u;
          if null mn or u<mn then mn:=u
       >>;
       l:=(vv.u).l;
       v:=v+d;
     >>;
     if null mx or null mn then rederr "plot, sampling failed";
     variation!* :=
     if yhi and not(yhi=plotmax!*) then (yhi-ylo) else
        (mx-mn); %  ploteval2xvariation l;

    if plot!-refine!*>0 then
        l:=smooth(reversip l,ff,f,x,mx,mn,ylo,yhi,d);
    return {for each x in l collect {car x,cdr x}};
   end;


symbolic procedure ploteval2xvariation l;
  begin scalar u;
   % compute geometric mean value.
    integer m;
    u:=1.0;
    for each p in l do
     <<m:=m+1; p:=cdr p;
       if p and p neq 0.0 then u:=u*abs p;
     >>;
    return expt(u,1/float m);
  end;

symbolic procedure smooth(l,ff,f,x,maxv,minv,ylo,yhi,d);
  begin scalar rat,grain,cutmax,cutmin,z,z0;
   z:=l;
   cutmax :=  yhi or (- 2*minv + 3*maxv);
   cutmin :=  ylo or (3*minv - 2*maxv);
   grain  :=  variation!* *0.05;
   rat := d / if numberp maxv and maxv > minv then (maxv - minv)
               else 1;
    % Delete empty points in front of the point list.
   while z and null cdar z and cdr z and null cdadr z do z:=cdr z;
    % Find left starting point if there is no one yet.
   if z and null cdar z and cdr z then
   <<z0:= findsing(ff,f,x,ylo,yhi,cadr z,car z);
     if z0 then l:=z:=z0.cdr z;
   >>;
   while z and cdr z do
   <<z0:=z; z:=cdr z;
       smooth1(z0,ff,f,x,cutmin,cutmax,grain,rat,0,plot!-refine!*)
   >>;
   return l;
  end;

symbolic procedure smooth1(l,ff,f,x,minv,maxv,g,rat,lev,ml);
    smooth2(l,ff,f,x,minv,maxv,g,rat,lev,ml);


symbolic procedure smooth2(l,ff,f,x,minv,maxv,g,rat,lev,ml);
  if lev >= ml then
      smooth3(l,ff,f,x,minv,maxv,g,rat,lev,ml)
    else
  if null cdar l then t else
 begin scalar p0,p1,p2,p3,x1,x2,x3,y1,y2,y3,d;
       scalar dy12,dy32,dx12,dx32,z,a,w;
%%%%%    fdeclare(x1,x2,x3,y1,y2,y3,rat,d,dx12,dx32,dy12,dy32);
    lev:= add1 lev;
    p1:=car l; p3:=cadr l;
    x1:=car p1; y1:=cdr p1;
    x3:=car p3; y3:=cdr p3;
  nopoint:
    if null y3 then
    <<if null cddr l then return(cdr l:=nil);
      x2:=x3; y2:=y3; cdr l:=cddr l;
      p3:=cadr l; x3:=car p3; y3:=cdr p3;
      if y3 then goto outside else goto nopoint;
    >>;
    % Generate a new point
    x2:=(x1+x3)*0.5;
    if null (y2 := plotevalform(ff,f,{x.x2}))
       or eqcar(y2,'overflow) then goto outside;
    if y2 < minv or y2 > maxv then goto outside;

    dy32 := (y3 - y2) * rat; dx32 := x3 - x2;
    dy12 := (y1 - y2) * rat; dx12 := x1 - x2;

%% extremely careful here !! see : plot(e^(x/(2-x)),x);
%%                                     WN 29. 9.99
    w :=  errorset({'times2,dy32,dy32},nil,nil);
    if ploterrorp w then goto disc else w:=car w;
    d :=  errorset({'times2,dy12,dy12},nil,nil);
    if ploterrorp d then goto disc else d:=car d;
    w := (w + dx32**2);
    d := (d + dx12**2);
    d:= errorset({'times2,w,d},nil,nil);
    if ploterrorp d then goto disc else d:=car d;
    d := sqrt d;
%% original    d :=  sqrt((dy32**2 + dx32**2) * (dy12**2 + dx12**2));

    if zerop d then return t;
    w := (dy32*dy12 + dx32*dx12);
    d:= errorset({'quotient,w,d},nil,nil);
     % d is cos of angle between the vectors p2p1 and p2p3.
     % d near to 1 means that the angle is almost 0 (needle).
    if ploterrorp d then goto disc else d:=car d;
    a:=abs(y3-y1)<g;
    if d > plotprecision!* and a and lev=ml then goto disc;

      % I have a good point, insert it with destructive replacement.
    cdr l := (x2 . y2) . cdr l;
      % When I have an almost 180 degree angle, I compare the
      % derivative at the midpoint with the difference quotient.
      % If these are close to each other, I stop the refinement.
    if -d  > plotprecision!*
       and (null plotderiv!*
             or
           ((w:=plotevalform(car plotderiv!*,cdr plotderiv!*,{x.x2}))
                and abs (w - (y3-y1)/(x3-x1))*rat <0.1))
      then return t;
    smooth2(cdr l,ff,f,x,minv,maxv,g,rat,lev,ml);
    smooth2(l,ff,f,x,minv,maxv,g,rat,lev,ml);
    return t;

    % Function has exceeded the boundaries. I try to locate the screen
    % crossing point.
  outside:
    cdr l := (x2 . nil) . cdr l;
    z := cdr l;    % insert a break
    p2:= (x2 . y2);  % artificial midpoint

    p0:=findsing(ff,f,x, minv, maxv, p1, p2);
    if p0 then
      << cdr l := p0 . z;
         smooth2(l,ff,f,x,minv,maxv,g,rat,lev,ml) >>;
    p0 := findsing(ff,f,x, minv, maxv, p3, p2);
    if p0 then
      << cdr z := p0 . cdr z;
         smooth2(cdr z,ff,f,x,minv,maxv,g,rat,lev,ml) >>;
    return;

  disc:  % look for discontinuities.
    return smooth3(l,ff,f,x,minv,maxv,g,rat,lev,ml);
  end;

symbolic procedure smooth3(l,ff,f,x,minv,maxv,g,rat,lev,ml);
 % detect discontinuities.
 begin scalar p1,p2,p3,x1,x2,x3,y1,y2,y3,d;
       scalar x2hi,y2hi,x2lo,y2lo,dir,hidir,chi,clo;
       scalar lobound,hibound;
       integer n;
    g := rat := lev := ml := nil;
 %%%%%      fdeclare(x1,x2,x3,y1,y2,y3,d,hidir);
    p1:=car l; p3:=cadr l;
    x1:=car p1; y1:=cdr p1;
    x3:=car p3; y3:=cdr p3;
    if abs(y3-y1)<variation!* then return t;

  % Bigstep found.
    hibound:=variation!**10.0; lobound:=-hibound;
    if y1>y3 then
    <<x2hi:=x1; y2hi:=y1; x2lo:= x3; y2lo:=y3; hidir:=-1.0>>
       else
    <<x2hi:=x3; y2hi:=y3; x2lo:= x1; y2lo:=y1; hidir:=1.0>>;
    n:=0; d:= (x3-x1)*0.5; x2:=x1+d;
  % intersection loop. Cut remaining interval into two parts.
  % If there is again a high increase in one direction we assume
  % a pole.
  next_point:
    if null (y2 := plotevalform(ff,f,{x.x2}))
       or eqcar(y2,'overflow) then goto outside;
    if y2 < minv then
      <<x2lo:=x2;y2lo:=minv;dir:=hidir>>
    else if y2 < y2lo then
      <<if y2lo<0.0 and y2<y2lo+y2lo and y2<lobound then clo:=t;
        x2lo:=x2;y2lo:=y2;dir:=hidir;>>
    else if y2 > maxv then
     <<x2hi:=x2;y2hi:=maxv;dir:=-hidir>>
    else if y2 > y2hi then
     <<if y2hi>0.0 and y2>y2hi+y2hi and y2>hibound then chi:=t;
       x2hi:=x2;y2hi:=y2;dir:=-hidir;>> else
      goto no_disc;
    if dir and (n:=n+1)<20 and (not clo or not chi) then
    <<d:=d/2; x2:=x2+d*dir; goto next_point>>;
  no_disc:
    if null dir then return t;
    if clo then y2lo:=minv;
    if chi then y2hi:=maxv;
  outside:
    p1:=(x2hi.y2hi); p3:=(x2lo.y2lo);
    if hidir=1.0 then <<p2:=p3;p3:=p1;p1:=p2>>;
    cdr l := p1 . (car p1.nil) . p3 . cdr l;
    return;
  brk:
    cdr l := (car p1.nil)  . cdr l;
    return;
  end;

symbolic procedure findsing(ff,f,x,minv,maxv,p1,p3);
% P3 is a point with a singularity.
% Try to locate the point where we exceed minv/maxv by subdivision.
begin scalar p1, p2, p3, x1, x2, x3, y1, y2, y3, d, x2n;
    x1:=car p1; y1:=cdr p1;
    x3:=car p3; y3:=cdr p3;
    d := (x3-x1)*0.5; x2n:=x1+d;
    for i:=1:5 do
    << d:=d*0.5; x2:= x2n;
       if null(y2 := plotevalform(ff,f,{x.x2}))
          or eqcar(y2,'overflow)
          or y2 < minv or y2 > maxv
        then x2n := x2n - d
        else << p2 := (x2 . y2); x2n := x2n + d>>
    >>;
   if null p2 then return nil;

    % generate uniform maxima/minima
    x2 := car p2; y2 := cdr p2;
    y2 := if (eqcar(y2,'overflow) and cdr y2<0) or y2<minv
          then minv
     else if eqcar(y2,'overflow) or y2>maxv then maxv else y2;
    return (x2 . y2)
end;

endmodule;   % plotexp2

end;
