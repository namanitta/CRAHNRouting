/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author anamanya
 */
import java. io.*;
public class GEGEC_R_Class_HOL_Theo {

static  int R=2;
static double mu = 4.0;
static double scv = 1;
     static  double roh[][]= {{0.0, 0.0, 0.0, 0.0,0.0,0.0,0.0,0.0,0.0,0.0},
                               {0.00,0.09,0.14,0.19,0.24,0.29,0.34,0.39,0.44,0.0},
                               {0.00,0.11,0.16,0.21,0.26,0.31,0.36,0.41,0.46,0.0},
                              };
    static  double lamda[][]= new double[R+1][10];
    static  double L[][] = new double [1+R][10];
    static  double alpha[][] = new double [1+R][10];
    static  double beta[][] = new double [1+R][10];
    static  double gama[][] = new double [1+R][10];
    static  double gama_prime [][] = new double [1+R][10];

    static  double SCVs [] = {0, scv,scv};
    static  double SCVa [] = {0, scv, scv};
    static  double sum_lamda_roh_SCV [] = new double [1+R];
    static double M_roh[] = new double[10];
    static PrintWriter sim_out2;

    GEGEC_R_Class_HOL_Theo(PrintWriter out)
    {
       sim_out2 = out;
   }

    static void initialise()
    {
      for(int i = 1; i<=R; i++)
          for(int j=0; j<10; j++)
        {
          L[i][j]=0; alpha[i][j]=0; beta[i][j]=0; gama[i][j]=0; gama_prime[i][j]=0; sum_lamda_roh_SCV [i]=0;
          lamda[i][j]= mu*roh[i][j];
        }
    }

    public void mainMethod()
    {
        sim_out2.println("\n**********PRP HOL Theoretical************* ");
        sim_out2.println("Utilisation\t QueueLenSU ");
       for(int k=1; k<9; k++)
      {
       initialise();

       gama[0][k] =0;

       for(int i=1; i<=R; i++)
        for(int j = 1; j <=i; j++)
         gama[i][k]+=roh[j][k];

       for(int i=1; i<=R; i++)
         gama_prime[i][k] = 1/(2*(1 - gama[i-1][k])*(1 - gama[i][k]));

       for(int i = 2; i <= R; i++)
        for(int j = 1; j <= i-1; j++)
         sum_lamda_roh_SCV[i]+=(lamda[i][k]/lamda[j][k])*Math.pow(roh[j][k], 2)*(SCVs[i] + SCVa[j]);

       for(int i=2; i<=R; i++)
         alpha[i][k] = Math.pow(roh[i][k], 2)*(SCVs[i] + 1) + sum_lamda_roh_SCV[i];
         alpha[1][k] = Math.pow(roh[1][k], 2)*(SCVs[1] + 1);
         beta[R][k] = 0;

       for(int i=1; i<=R-1; i++)
        for(int j=i+1; j<=R; j++)
         beta[i][k]+= lamda[i][k]/lamda[j][k]*Math.pow(roh[j][k], 2)*(SCVs[j] + 1);

       for(int i=1; i<=R; i++){
         L[i][k] = roh[i][k] + (roh[i][k]/(2*(1 - gama[i][k])))*(SCVa[i] - 1) + gama_prime[i][k] * (alpha[i][k] + beta [i][k]);
          }
         M_roh[k]= roh[1][k] + roh[2][k];
             sim_out2.println(M_roh[k]+"\t"+L[2][k]);
      }
         System.out.println("The output of the Theoretical PRP HOL has been written to the file");


  }
}