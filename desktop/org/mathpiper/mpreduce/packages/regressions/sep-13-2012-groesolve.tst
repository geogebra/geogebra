off nat;

load groebner;

groebmonfac:=0;

res3:={c16=(c11**2*c6)/(a11*cp11),
b11=(bp11*cp11)/c11,
b6=c11/cp11,
a6=cp11/c11,
ap11=(a11*ap16*cp11)/c11,
apzz=(a11*cp11*cpzz)/(c11*cp6),
 - ap16*cp11*cp6 + c11*c6};

groesolve ws;

sub(bp11=bp10,res3);

groesolve ws;

end;
