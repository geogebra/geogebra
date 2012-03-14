comment factorizer test file;

array a(20);

factorize(x**2-1);   % To make sure factorizer is loaded.

algebraic procedure test(prob,nfac);
  begin integer m; scalar p,q,r;
    scalar basetime;
    p := for i:=1:nfac product a(i);
    Write "Problem number ",prob;
    symbolic (basetime := time());
    r := factorize p;
    symbolic (basetime := time() - basetime);
    q := for each j in r product first j^second j;
    m := for each j in r sum second j;
    if m=nfac and p=q then return ok;
    write "This example failed:";
    write r
  end;

% Wang test case 1;
 
a(1) := x*y+z+10$
a(2) := x*z+y+30$
a(3) := x+y*z+20$
test(1,3);
 
% Wang test case 2;

a(1) := x**3*z+x**3*y+z-11$
a(2) := x**2*z**2+x**2*y**2+y+90$
test(2,2);

 
% Wang test case 3;

a(1) := x**3*y**2+x*z**4+x+z$
a(2) := x**3+x*y*z+y**2+y*z**3$
test(3,2);

 
% Wang test case 4;

a(1) := x**2*z+y**4*z**2+5$
a(2) := x*y**3+z**2$
a(3) := -x**3*y+z**2+3$
a(4) := x**3*y**4+z**2$
test(4,4);

 
% Wang test case 5;
 
a(1) := 3*u**2*x**3*y**4*z+x*z**2+y**2*z**2+19*y**2$
a(2) := u**2*y**4*z**2+x**2*z+5$
a(3) := u**2+x**3*y**4+z**2$
test(5,3);

 
% Wang test case 6;
 
a(1) := w**4*x**5*y**6-w**4*z**3+w**2*x**3*y+x*y**2*z**2$
a(2) := w**4*z**6-w**3*x**3*y-w**2*x**2*y**2*z**2+x**5*z
           -x**4*y**2+y**2*z**3$
a(3) := -x**5*z**3+x**2*y**3+y*z$
test(6,3);

 
% Wang test case 7;

a(1) := x+y+z-2$
a(2) := x+y+z-2$
a(3) := x+y+z-3$
a(4) := x+y+z-3$
a(5) := x+y+z-3$
test(7,5);

 
% Wang test case 8;

a(1) := -z**31-w**12*z**20+y**18-y**14+x**2*y**2+x**21+w**2$
a(2) := -15*y**2*z**16+29*w**4*x**12*z**3+21*x**3*z**2+3*w**15*y**20$

% Commented out, since it can take a long time.

% TEST(8,2);
 
 
 
% Wang test case 9;
 
a(1) := 18*u**2*w**3*x*z**2+10*u**2*w*x*y**3+15*u*z**2+6*w**2*y**3*z**2$
a(2) := x$
a(3) := 25*u**2*w**3*y*z**4+32*u**2*w**4*y**4*z**3-
        48*u**2*x**2*y**3*z**3-2*u**2*w*x**2*y**2+44*u*w*x*y**4*z**4-
        8*u*w*x**3*z**4+4*w**2*x+11*w**2*x**3*y+12*y**3*z**2$
a(4) := z$
a(5) := z$
a(6) := u$
a(7) := u$
a(8) := u$
a(9) := u$
test(9,9);


 
% Wang test case 10;

a(1) := 31*u**2*x*z+35*w**2*y**2+40*w*x**2+6*x*y$
a(2) := 42*u**2*w**2*y**2+47*u**2*w**2*z+22*u**2*w**2+9*u**2*w*x**2+21
        *u**2*w*x*y*z+37*u**2*y**2*z+u**2*w**2*x*y**2*z**2+8*u**2*w**2
        *z**2+24*u**2*w*x*y**2*z**2+24*u**2*x**2*y*z**2+12*u**2*x*y**2
        *z**2+13*u*w**2*x**2*y**2+27*u*w**2*x**2*y+39*u*w*x*z+43*u*
        x**2*y+44*u*w**2* z**2+37*w**2*x*y+29*w**2*y**2+31*w**2*y*z**2
        +12*w*x**2*y*z+43*w*x*y*z**2+22*x*y**2+23*x*y*z+24*x*y+41*y**2
        *z$
test(10,2);

 
 
% Wang test case 11;

a(1) := -36*u**2*w**3*x*y*z**3-31*u**2*w**3*y**2+20*u**2*w**2*x**2*y**2
        *z**2-36*u**2*w*x*y**3*z+46*u**2*w*x+9*u**2*y**2-36*u*w**2*y**3
        +9*u*w*y**3-5*u*w*x**2*y**3+48*u*w*x**3*y**2*z+23*u*w*x**3*y**2
        -43*u*x**3*y**3*z**3-46*u*x**3*y**2+29*w**3*x*y**3*z**2-
        14*w**3*x**3*y**3*z**2-45*x**3-8*x*y**2$
a(2) := 13*u**3*w**2*x*y*z**3-4*u*x*y**2-w**3*z**3-47*x*y$
a(3) := x$
a(4) := y$
test(11,4);

 
 
 
% Wang test case 12; 
a(1) := x+y+z-3$
a(2) := x+y+z-3$
a(3) := x+y+z-3$
test(12,3);



 
% Wang test case 13;

a(1) := 2*w*z+45*x**3-9*y**3-y**2+3*z**3$
a(2) := w**2*z**3-w**2+47*x*y$
test(13,2);


 
 
% Wang test case 14;

a(1) := 18*x**4*y**5+41*x**4*y**2-37*x**4+26*x**3*y**4+38*x**2*y**4-29*
        x**2*y**3-22*y**5$
a(2) := 33*x**5*y**6-22*x**4+35*x**3*y+11*y**2$
test(14,2);

 
 
 
% Wang test case 15;

a(1) := 12*w**2*x*y*z**3-w**2*z**3+w**2-29*x-3*x*y**2$
a(2) := 14*w**2*y**2+2*w*z+18*x**3*y-8*x*y**2-y**2+3*z**3$
a(3) := z$
a(4) := z$
a(5) := y$
a(6) := y$
a(7) := y$
a(8) := x$
a(9) := x$
a(10) := x$
a(11) := x$
a(12) := x$
a(13) := x$
test(15,13);


% Test 16 - the 40th degree polynomial that comes from
% SIGSAM problem number 7;

a(1) := 8192*y**10+20480*y**9+58368*y**8-161792*y**7+198656*y**6+
        199680*y**5-414848*y**4-4160*y**3+171816*y**2-48556*y+469$
a(2) := 8192*y**10+12288*y**9+66560*y**8-22528*y**7-138240*y**6+
        572928*y**5-90496*y**4-356032*y**3+113032*y**2+23420*y-8179$
a(3) := 4096*y**10+8192*y**9+1600*y**8-20608*y**7+20032*y**6+87360*y**5-
        105904*y**4+18544*y**3+11888*y**2-3416*y+1$
a(4) := 4096*y**10+8192*y**9-3008*y**8-30848*y**7+21056*y**6+146496*
        y**5-221360*y**4+1232*y**3+144464*y**2-78488*y+11993$
test(16,4);

% Test 17 - taken from Erich Kaltofen's thesis. This polynomial
% splits mod all possible primes p;

a(1) := x**25-25*x**20-3500*x**15-57500*x**10+21875*x**5-3125$
test(17,1);

% Test 18 - another 'hard-to-factorize' univariate;

a(1) := x**18+9*x**17+45*x**16+126*x**15+189*x**14+27*x**13-
        540*x**12-1215*x**11+1377*x**10+15444*x**9+46899*x**8+
        90153*x**7+133893*x**6+125388*x**5+29160*x**4-
        32076*x**3+26244*x**2-8748*x+2916$
test(18,1);

% Test 19 - another example chosen to lead to false splits mod p;

a(1) := x**16+4*x**12-16*x**11+80*x**9+2*x**8+160*x**7+
        128*x**6-160*x**5+28*x**4-48*x**3+128*x**2-16*x+1$
a(2) := x**16+4*x**12+16*x**11-80*x**9+2*x**8-160*x**7+
        128*x**6+160*x**5+28*x**4+48*x**3+128*x**2+16*x+1$
test(19,2);

 
% End of all tests;
 
 
end;
