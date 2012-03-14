n := 4;

on rational, rat;
off allfac;

array p(n/2+2);

harmonic u,v,w,x,y,z;

weight e=1, b=1, d=1, a=1;

%% Step1: Solve Kepler equation
bige := fourier 0;
for k:=1:n do <<
  wtlevel k;
  bige:=fourier e * hsub(fourier(sin u), u, u, bige, k);
>>;
write "Kepler Eqn solution:", bige$

%% Ensure we do not calculate things of too high an order
wtlevel n;

%% Step 2: Calculate r/a in terms of e and l
dd:=-e*e; hh:=3/2; j:=1; cc := 1;
for i:=1:n/2 do <<
  j:=i*j; hh:=hh-1; cc:=cc+hh*(dd^i)/j
>>;

bb:=hsub(fourier(1-e*cos u), u, u, bige, n);
aa:=fourier 1+hdiff(bige,u); ff:=hint(aa*aa*fourier cc,u);


%% Step 3: a/r and f
uu := hsub(bb,u,v); uu:=hsub(uu,e,b);
vv := hsub(aa,u,v); vv:=hsub(vv,e,b);
ww := hsub(ff,u,v); ww:=hsub(ww,e,b);

%% Step 4: Substitute f and f' into S
yy:=ff-ww; zz:=ff+ww;
xx:=hsub(fourier((1-d*d)*cos(u)),u,u-v+w-x-y+z,yy,n)+
    hsub(fourier(d*d*cos(v)),v,u+v+w+x+y-z,zz,n);

%% Step 5: Calculate R
zz:=bb*vv; yy:=zz*zz*vv;

on fourier;

p(0):= fourier 1; p(1) := xx;
for i := 2:n/2+2 do <<
  wtlevel n+4-2i;
  p(i) := fourier ((2*i-1)/i)*xx*p(i-1) - fourier ((i-1)/i)*p(i-2);
>>;

wtlevel n;
for i:=n/2+2 step -1 until 3 do p(n/2+2):=fourier(a*a)*zz*p(n/2+2)+p(i-1);

yy*p(n/2+2);

showtime;

end;
