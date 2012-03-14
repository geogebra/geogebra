
ddx(1,50) := ext(3)*(t*u1 + 1)$

ddx(1,51) := ext(3)*u1$

ddx(1,52) := ext(3)*(u*u1 + u3)$

ddt(1,50) := ext(5)*t*u1 + ext(5) - ext(4)*t*u2 + ext(3)*t*u*u1 + ext(3)*t*u3 + 
ext(3)*u$

ddt(1,51) := ext(5)*u1 - ext(4)*u2 + ext(3)*u*u1 + ext(3)*u3$

ddt(1,52) := ext(5)*u*u1 + ext(5)*u3 - ext(4)*u*u2 - ext(4)*u1**2 - ext(4)*u4 + 
ext(3)*u**2*u1 + 2*ext(3)*u*u3 + 3*ext(3)*u1*u2 + ext(3)*u5$
