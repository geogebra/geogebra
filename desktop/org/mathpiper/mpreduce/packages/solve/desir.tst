off echo;
write
 "Premier exemple: utilisation interactive sur une equation simple";
write "desir();                       %appel de DESIR";
desir();
3;                             %ordre de l'equation
1;x;x;x**6;                    %coefficients
non;                           %correction ?
non;                           %transformation ?
4;                             %nombre de termes a calculer
non;                           %transformation ?
write "solvalide(first ws,1,4);";
solvalide(first ws,1,4);

write "Le meme exemple en utilisation directe";
write "lcoeff:={1,x,x,x**6};";
lcoeff:={1,x,x,x**6};
write "param:={};";
param:={};
write "on trdesir;             %obtention facultative d'une trace";
on trdesir;
showtime;
write "sol:=delire(x,4,1,lcoeff,param);";
sol:=delire(x,4,1,lcoeff,param);
write "showtime;                      %temps d'execution de 'delire'";
showtime;
on div;j:=0$
for each elt in sol do
    <<j:=j+1;write j,"ieme solution :", sorsol(elt);>>;
write "solvalide({lcoeff,sol},2,4)$";
solvalide({lcoeff,sol},2,4)$
write "solvalide({lcoeff,sol},3,4)$";
solvalide({lcoeff,sol},3,4)$
off div;
write "off trdesir;";
off trdesir;

write "Deuxieme exemple : utilisation interactive, parametres et";
write "transformations";
write "desir();";
desir();
2;                             %ordre de l'equation
x**2-nu**2;x;x**2;             %coefficients
non;                           %correction ?
1;                             %nombre de parametres
nu;                            %nom du parametre
non;                           %transformation ?
2;                             %nombre de termes a calculer
oui;                           %transformation ?
2;                             %changement de variable
1/v;                           %x=1/v
non;                           %transformation ?
2;                             %nombre de termes a calculer
non$                           %transformation ?
sol:=ws$
write "sol1:=first sol$        %solutions au voisinage de 0";
sol1:=first sol$               %solutions au voisinage de 0
write "sol2:=second sol$       %solutions au voisinage de l'infini";
sol2:=second sol$              %solutions au voisinage de l'infini
write "solvalide(sol1,1,2)$";
solvalide(sol1,1,2)$           %presence de solutions conditionnelles
write "solvalide(sol2,1,2)$";
solvalide(sol2,1,2)$    %la verification de la validite des solutions
                        %au voisinage de l'infini est possible malgre
                        %le parametre (pas de condition).
write
 "Remarque : la verification de la validite des solutions est possible";
write "malgre la presence d'un parametre (pas de condition).";
write "standsol(sol1);                %=sol1...sans interet!";
standsol(sol1);                %=sol1...
write "standsol(sol2);      %solutions retournees sous forme standard.";
standsol(sol2);
write "Pour revoir les solutions au voisinage de 0 :";
j:=0$
write "for each elt in second sol1 do";
write "          <<j:=j+1;write j,'ieme solution';sorsol(elt);>>;";
for each elt in second sol1 do
          <<j:=j+1;write j,"ieme solution";sorsol(elt);>>;
write
"Evaluation des solns au voisinage de 0 pour une valeur particuliere";
write "du parametre :";
write "sorparam(sol1,{nu});%evaluation des solutions au voisinage de 0";
write "                 %pour une valeur particuliere du parametre";
write "                 %nu = 1";
write "                 %ecriture et retour des solutions sous forme";
write "                 %standard";
sorparam(sol1,{nu});    %evaluation des solutions au voisinage de 0
                        %pour une valeur particuliere du parametre
1;                      %valeur de nu
                        %ecriture et retour des solutions sous forme
                        %standard
write "solparam(sol1,{nu},{1});";
solparam(sol1,{nu},{1}); %meme fonction avec retour des solutions sous
                         %forme generalisee, ce qui permet d'enchainer
write "Meme fonction avec retour des solutions sous forme generalisee,";
write "ce qui permet d'enchainer :";
write "solvalide(ws,1,2)$";
solvalide(ws,1,2)$

write
 "L'exemple suivant a ete cree specialement pour tester l'algorithme";
write "et utiliser un grand nombre de procedures :";
lcoeff:={x+1,2*x**2*(x+1),x**4,(5*x**7)/2,x**10};
param:={};
showtime;
write "sol:=delire(x,4,1,lcoeff,param);";
sol:=delire(x,4,1,lcoeff,param)$
showtime;
on div;j:=0$
for each elt in sol do
    <<j:=j+1;write j,"ieme solution :", sorsol(elt);>>;
solvalide({lcoeff,sol},1,4)$
solvalide({lcoeff,sol},3,4)$
off div;

end;

