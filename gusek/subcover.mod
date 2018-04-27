
######   Program for Collaboration  ######
param n, integer; 
param m, integer ;
param r, integer;
#set A:={i in 1..n,j in 1..m};
set I, dimen 2;
set J, dimen 1;
set K, dimen 1;
set H, dimen 1;

/*param A{I};
table tin IN "CSV" "adjacent.csv": 
I<-[i,j], A;*/

table tin IN "CSV" "range.csv" : H<-[i];

param S{I};
 table tin IN "CSV" "coverageR.csv" :
 I<-[i,j], S; 
 
 param y{J};
 #table tin IN "CSV" "subset2.csv" :
 table tin IN "CSV" "subsetC.csv" :
 J<-[i], y; 

 param z{K};
 table tin IN "CSV" "testC.csv" :
 K<-[k], z; 


var x{i in 1..n}, binary; #assignment

#minimize Cost: (sum{i in H} x[i]);
minimize Cost: (sum{i in r+1..n} x[i]);
#minimize Cost: (sum{i in 1..k} x[i]);

subject to first {j in 1..m}: (sum{i in 1..n} S[i,j]*x[i])>=y[j]; 
           second {i in 1..n} : x[i] <= z[i];

solve;
printf "\n";
printf{i in 1..n} "%d,", x[i];
table result{i in 1..n} OUT "CSV" "set2.csv":
 i~i, x[i]~x;
/* Displaying results */
#display x{i in 1..n};
#display{(i,j) in I}:S[i,j];#: x[i,j]=1 }: x[i,j];*/
#display 'total costs=',sum{j in V}(sum{i in V} (c[i,j]*x[i,j]))
data;
param n:= 500;
param m:= 100;
param r:= 5;
/*param p:=6;
param S:=[1,1] 1 [1,2] 0 [1,3] 1 [2,1] 0 [2,2] 1 [2,3] 0 [3,1] 0 [3,2] 0 [3,3] 1 [4,1] 1 [4,2] 0 [4,3] 0 ;*/
end; 
 