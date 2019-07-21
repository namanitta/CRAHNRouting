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
import java.awt.geom.Point2D;
import java.util.Random;
import java.lang.StringBuffer;

public class CRQOS_R {
    public static int SU = 250;
    public static int PU = 100;
    public static int pairs= 11;
    public static double sense_time;
    public static int frequency_bands = 12;
    public static double[] link_error_rate = {0,0.2};
    public static int[] traffic_reqt ={0,1};
    public static double PU_arrival_rate = 0.5;
    public static double []PU_arrival_time = new double[PU];
    public static double total_time_slot = 50.0;
    public static double av_PU_interrupt_rate = 0.1;
    public static double radio_power_level = 2.0;
    public static double[] bm = {15.0,20.0,25.0};
    public static double trans_radius =2.2;
    public static double power_level =2.0;
    public static int network_types = 3;
    public static int []source_node = new int [pairs];
    public static int []dest_node = new int [pairs];
    public static double PU_service_rate = 2;
    public static double node_to_node = 0.05;
    public static double []end_Delay = new double [pairs];

    public static Point2D[] SU_points = new Point2D [SU];
    public static double[][] fm = new double[frequency_bands][network_types];
    public static double[][] freq_status = new double[frequency_bands][pairs];

    public static Random r = new Random();
    static PrintWriter sim_out2;


    CRQOS_R(PrintWriter out)
    {
       sim_out2 = out;
   }

    static void initialiseParameters()
    {

        for(int i = 0; i<SU; i++)
        {
            SU_points[i] = new Point2D.Double();
           SU_points[i].setLocation((Math.random()*10.0),(Math.random()*10.0));
        }
        for(int i = 0; i<frequency_bands; i++)
        {
            fm[i][1]=r.nextInt(5);
            fm[i][2]=trans_radius;

                 while(fm[i][1]==0)
                    fm[i][1]=r.nextInt(5);


             if (i <4)
             {
                 fm[i][0]= bm[0];
            }

            else if (i>=4 && i<8)
            {
                 fm[i][0]= bm[1];
                
            }

            else
            {
                 fm[i][0]= bm[2];
                 
            }
            for(int n = 0; n<pairs; n++)
                freq_status[i][n]=0;
        }
        for(int n = 0; n<pairs; n++)
        {
            end_Delay[n]=0.0;
            int s=0, d= 0;
            source_node[n]= r.nextInt(SU);
            dest_node[n]= r.nextInt(SU);
            s =source_node[n];
            d = dest_node[n];
           
            while(s== d ||(SU_points[s].distance(SU_points[d])>2*trans_radius))
            {
                dest_node[n]= r.nextInt(SU);
                d = dest_node[n];
            }
        }
        for (int i =0; i<PU; i++)
            PU_arrival_time[i]= expon(PU_arrival_rate );

     }
   // search for a free band whose duration is the smallest.
    static int spectrumSearch(int n)
    {
         int j = 0;
         int[] holes = new int[frequency_bands];
           for(int f =0; f< frequency_bands;f++ )

           {

               if (freq_status[f][n]!=1)
               {
                    holes[j]=f;
                    j++;
               }
           }

         double min1 = fm[holes[0]][1];
         double min2 = fm[holes[0]][2];

         for (int k =1; k<=j; k++)
         {
                    if(min1< fm[holes[k]][1]&&min2< fm[holes[k]][2])
                    {
                     return holes[k];
                    }
                    
                     return holes[0];

            }
           return -1;
    }

    static int route(int n)
    {
        Point2D sN = SU_points[source_node[n]];
         Point2D dN = SU_points[dest_node[n]];
        int inter_node = 0;
        for (int i =0; i<SU; i++)

        {
           // int j =0;
            Point2D a= SU_points[i];
            if(sN.distance(a)<=trans_radius&&dN.distance(a)<=trans_radius)
                inter_node = i;
        }
        return inter_node;
    }


public static double  expon(double  rate )  /* Exponential variate generation function. */

    {
        return -1/rate * Math.log(Math.random());

    }

static double linkParameter(int pair)
{

    double freq_difference=0.0;
    double capacity = 0.0;
    double transmission_delay =0.0;
    double nodal_delay =0.0;
    double end_to_end= 0.0;
    
    if(spectrumSearch(pair)>=0)

    {

    freq_status[spectrumSearch(pair)][pair]=1;
    capacity =(fm[spectrumSearch(pair)][0]*1000000.0)*(Math.log(1.0+(power_level/fm[spectrumSearch(pair)][0]*1000000.0))/Math.log(2.0));
    transmission_delay = (1.0/(1.0-0.8))*((fm[spectrumSearch(pair)][0]*1000000.0)/(capacity));
   
   if(fm[spectrumSearch(pair)][0]== bm[0])

        freq_difference =0.0;

        else if (fm[spectrumSearch(pair)][0] == bm[1])

        freq_difference = bm[1]-bm[0];

            else freq_difference = bm[2]-bm[0];

   nodal_delay = (sense_time/1000.0*frequency_bands*freq_difference*1000000.0)+(((double)SU/(double)frequency_bands)*node_to_node)+(10.0/1000.0);
   end_to_end = ( 8.0*(transmission_delay+nodal_delay)/(1.0-0.1));

   return end_to_end;

    }

    return 0;

}

static double max_value(double [] array)
{
    double max = array[0];
    for (int i = 1; i < array.length; i++)

        {

            if(max < array[i])
                {
                    max = array[i];

                }

         }
    return max;

    }


static double average(double[]array)

    {

    double sum = 0.0;
        for (int i = 0; i < array.length; i++)
        {
            sum +=array[i];

        }
    return (sum/(double)array.length);

    }

    public void mainMethod()

      {
        double time = 0.0;
        sim_out2.println("\n**********QOS analytical results***************************************************************");
         sim_out2.println("SU\taverage_end_delay");
        for(SU = 100;SU<=250; SU+=10)
         {
            sense_time =25.0;
            initialiseParameters();


            for(int i =0; i<pairs; i++)
            {
                end_Delay[i]= linkParameter(i);
            }
            double  average_end_delay= average(end_Delay);
            sim_out2.println(SU +"\t"+average_end_delay);
            
         }

        SU = 110;
        sim_out2.println("\n*************************************************************************");
        sim_out2.println("\nPower level\taverage_end_delay");
        for(power_level = 0.1; power_level<=1.2; power_level+=0.1)
         {
            sense_time =25.0;
            initialiseParameters();
            for(int i =0; i<pairs; i++)
            {
                end_Delay[i]= linkParameter(i);
            }
            double  average_end_delay= average(end_Delay);
            sim_out2.println(power_level +"\t"+average_end_delay);

         }

        System.out.println("The output of QOS analytical method has been written to the file");
     }
}
