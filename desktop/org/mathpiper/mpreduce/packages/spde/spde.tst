 %Appendix (Testfile).

 %This appendix is a test file. The symmetry groups for various
 %equations or systems of equations are determined. The variable
 %PCLASS has the default value 0 and may be changed by the user
 %before running it. The output may be compared with the results
 %which are given in the references.

 %The Burgers equations

 deq 1:=u(1,1)+u 1*u(1,2)+u(1,2,2)$

  cresys deq 1$ simpsys()$ result()$

 %The Kadomtsev-Petviashvili equation

 deq 1:=3*u(1,3,3)+u(1,2,2,2,2)+6*u(1,2,2)*u 1

       +6*u(1,2)**2+4*u(1,1,2)$

  cresys deq 1$ simpsys()$ result()$

 %The modified Kadomtsev-Petviashvili equation

  deq 1:=u(1,1,2)-u(1,2,2,2,2)-3*u(1,3,3)

       +6*u(1,2)**2*u(1,2,2)+6*u(1,3)*u(1,2,2)$

  cresys deq 1$ simpsys()$ result()$

 %The real- and the imaginary part of the nonlinear Schroedinger
 %equation

 deq 1:= u(1,1)+u(2,2,2)+2*u 1**2*u 2+2*u 2**3$

 deq 2:=-u(2,1)+u(1,2,2)+2*u 1*u 2**2+2*u 1**3$

 %Because this is not a single equation the two assignments

  sder 1:=u(2,2,2)$  sder 2:=u(1,2,2)$

 %are necessary.

  cresys()$ simpsys()$ result()$

 %The symmetries of the system comprising the four equations

  deq 1:=u(1,1)+u 1*u(1,2)+u(1,2,2)$

  deq 2:=u(2,1)+u(2,2,2)$

  deq 3:=u 1*u 2-2*u(2,2)$

  deq 4:=4*u(2,1)+u 2*(u 1**2+2*u(1,2))$

  sder 1:=u(1,2,2)$ sder 2:=u(2,2,2)$ sder 3:=u(2,2)$ sder 4:=u(2,1)$

 %is obtained by calling

  cresys()$ simpsys()$

  df(c 5,x 1):=-df(c 5,x 2,2)$

  df(c 5,x 2,x 1):=-df(c 5,x 2,3)$

  simpsys()$  result()$


 %The symmetries of the subsystem comprising equation 1 and 3 are
 %obtained by

  cresys(deq 1,deq 3)$ simpsys()$ result()$

 %The result for all possible subsystems is discussed in detail in
 %''Symmetries and Involution Systems: Some Experiments in Computer
 %Algebra'', contribution to the Proceedings of the Oberwolfach
 %Meeting on Nonlinear Evolution Equations, Summer 1986, to appear.

end;
