/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author anamanya
 */
import java.math.*;
import java.util.*;
import java.lang.management.*;
import java. io.*;

public class Gated_Node_Theoretical {
    static PrintWriter sim_out;
    Gated_Node_Theoretical(PrintWriter out)
    {
       sim_out = out;
   }
    public void mainMethod()
    {
        //PrintWriter sim_out2= new PrintWriter(new FileWriter("gatedoutputtheo.xls"));
    double lamda_in = 0.2;
    double lamda_on = 10.0/9.0;
    double lamda_off = 25.0;
    double mu = 0.17;
    double arr_SCV =1;
    double Ser_SCV = 1;
    double channel_utilisation =0;
    double eff_lamda;
    double eff_arr_SCV;
    double var_on;
    double var_off;
    double Ws;
    double Ls;
    sim_out.println("\n**********Gated Theoretical************* ");
     sim_out.println( "channel_utilisation\t Ws");
     while(channel_utilisation < 0.99){
 //for(lamda_in = 0.5; lamda_in<=6; lamda_in+=0.5){
    lamda_in+=0.2;
    var_on = 1.0/lamda_on;
    var_off = 1.0/lamda_off;
    eff_lamda = lamda_in* lamda_off/(lamda_off+ lamda_on);
    eff_arr_SCV = arr_SCV+((lamda_on*((var_on*Math.pow(lamda_on, 2.0))+(var_off*Math.pow(lamda_off, 2.0)))/Math.pow((lamda_off+lamda_on),2.0))*lamda_in);
    channel_utilisation = eff_lamda*mu;
    Ws = 1.0/(mu*(1-channel_utilisation));
    Ls = eff_lamda*Ws;

   
   // System.out.println( eff_lamda+"\t"+channel_utilisation+"\t"+ Ws);
    sim_out.println( channel_utilisation+"\t"+ Ws);
        }
//sim_out2.close();
System.out.println("The output of the theoretical gated node has been written to the file");
    }
    public static double  expon(double  rate )  /* Exponential variate generation function. */
{
    return -1/rate * Math.log(Math.random());
}

}
