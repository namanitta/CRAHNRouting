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
public class Gated_Node_Simulation {


    public static double   sample_size=10; // number of iterations


    public static double WqA[]=new double [80];
    public static double U[]= new double [80];
    public static double LqA[]=new double [80];
     public static double PLP_A[]= new double [80];

    public static int counter;
    public static double  M_Q_Len, M_Packet_Loss, M_Delay,  M_Utilization;
    public static double V_Q_Len,V_Packet_Loss, V_Delay, V_Utilization;
    public static int num_customer_required;
    public static double sim_time,mu,lamda_in,scv_in, scv_mu, a, a_in;
    public static double lamda_on, lamda_off,scv_on,scv_off,a_on,a_off;
    public static double max_double;
    public static int next_event_type;
    public static  int num_customer;
    public static  int num_in_q,k,KK;

    public static int server_status;
    public static  double time_past;
    public static double average_number_in_queue;
   public static double    server_utilization;
    public static  double total_of_delays;
    public static double average_delay;
    public static double area_num_in_q;
    public static  double area_server_status;
    public static   double time_last_event;
    public static    int Q_SIZE=100,index;
    public static int num_delayed,lost;
    public static double sensingstarttime,sensingtime,node, average_sensing_time;

   public static   double[] time_next_event=new double[3];
   public static    double[] time_arrival_PU=new double[Q_SIZE+1];
   public static double response_time, job_in_server,ST,ST_CU;
   
   static PrintWriter sim_out2;

   public static boolean link_state;

   Gated_Node_Simulation(PrintWriter out)
    {
       sim_out2 = out;
   }

   public static void initialize()

      {

        mu=0.17;  // Mean service rate for corresponding GE
        //lamda_in = 6.0;
        lamda_off = 25;
        lamda_on =10.0/9.0;

       scv_in=1;
       scv_on=1;// inter-arrival SCV
       scv_off=1.0;// service time SCV
       scv_mu=1.0;

        a_in= 2.0/(scv_in+1);
        a_on= 2.0/(scv_on+1);
        a_off= 2.0/(scv_off+1);
        a= 2.0/(scv_mu+1);

        num_customer_required=100000;
        sim_time=0.0;
        response_time=0.0;
        max_double=2000000;
        num_customer=0;
        lost =0;
        num_in_q =0;
        total_of_delays=0.0;
        server_status=0;//server status is IDLE

      //initialize the statistical counters
         area_num_in_q=0.0;
         area_server_status=0.0;
         time_last_event=0.0;
         time_next_event[0]=sim_time+expon(lamda_in );
         time_next_event[1]=sim_time+expon(lamda_off);
         time_next_event[2]=max_double;
         num_delayed=0;
         sensingstarttime=sensingtime=node=0.0;

         link_state = true;

        }

       public static void timing()

       {
         int min =0;

         for(int c =1; c < time_next_event.length;c++ )
           {
                    if(time_next_event[c]< time_next_event[min])
                    min=c;

            }

          next_event_type = min;

         time_last_event = sim_time;

       // advance the simulation clock
        sim_time = time_next_event[next_event_type];
      }

     
       public static void arrive()

      {

            num_customer++;
            time_next_event[0]=sim_time+expon(lamda_in );
           if (link_state == true){

                  if(server_status==0)
                  {

                      server_status=1;
                      ST=expon(mu);
                      time_next_event[2]=sim_time+ST;
                      response_time+=ST;
                      
                  }

           else if(num_in_q < Q_SIZE - 1) // If server is BUSY
            {

            try{
              
                num_in_q++;
               time_arrival_PU[num_in_q]=sim_time;
            }
           catch(ArrayIndexOutOfBoundsException e) { e.printStackTrace();}

         } else
               lost++;
       }//else
           //lost++;
    }

      public static void Gate_state()
       {
           if(link_state ==false)
           {
            link_state = true;
          
           time_next_event[1]=sim_time+expon(lamda_off);
            }

        else
            {

               link_state =false;
                time_next_event[1]=sim_time+expon(lamda_on);

            }

        }

       public static void depart()

      {
         if(num_in_q ==0)
         {
             server_status=0;
             time_next_event[2]=max_double;
          }
         else
         {
           
            //System.out.println("time_arrival_PU[1] = "+time_arrival_PU[1] );
            total_of_delays+=(sim_time-time_arrival_PU[1]);
            //num_customer++;
            num_delayed++;
            ST=expon(mu);
            time_next_event[2]=sim_time+ST;
            response_time+=(total_of_delays+ST);

         try {
            for(int i=1;i<num_in_q;i++)

                time_arrival_PU[i]=time_arrival_PU[i+1];
            }
             catch(ArrayIndexOutOfBoundsException e) { e.printStackTrace();}
             num_in_q--;

            }

        }

       public static void update_time_avg_stats(){

        //Update area accumulators for time-average statistics

         time_past=sim_time-time_last_event;
         area_num_in_q+=time_past*num_in_q;
         area_server_status+=time_past*server_status;
         average_sensing_time += sensingtime;
       }


       public static void report()

      {
//System.out.println("server status = "+total_of_delays );
     WqA[counter]=total_of_delays / num_delayed;
     U[counter]= area_server_status / sim_time;
     LqA[counter]=area_num_in_q / sim_time;
     PLP_A[counter]= (double)lost /num_customer;
     average_sensing_time = average_sensing_time/node;

       }
//------------------------------------------the main program.---------------
       public  void mainMethod()

      {
        //sim_out2= new PrintWriter(new FileWriter("gatedoutputsim.xls"));
       sim_out2.println("\n*********Gated Node Simulation Values*********");
       sim_out2.println("M_Utilization\tM_Delay");

    //System.out.println("M_Delay\tM_Utilization");


      //for(lamda_in = 0.5; lamda_in<= 6; lamda_in+=0.5)//for ( KK=0;KK<4;KK++)
    while(M_Utilization < 0.99)
      {
    lamda_in += 0.2;
         counter=0;

   while (counter < sample_size )

   {
         initialize();

         while(num_customer<num_customer_required)

         { timing();
            update_time_avg_stats();
           // System.out.println("i " + num_customer);
            if(next_event_type==0)

                arrive();

            else if(next_event_type==2)
                depart();

            else
                Gate_state();
         }
         report();
         counter++;

        }

      confidence_calculation();

         }
    System.out.println("The output of the simulated gated node has been written to the file");
    //sim_out2.close();
      }


    public static void confidence_calculation()
        {
       double sum1=0, sum2=0, sum3=0, sum4=0, sum5=0, sum6=0, sum7=0, sum8=0;
       double sum1_C=0, sum2_C=0, sum3_C=0, sum4_C=0, sum5_C=0, sum6_C=0;

          for (counter=0;counter<sample_size;counter++)

             {
             sum1+= LqA [counter];

             sum3+= PLP_A[counter];

             sum5+=WqA[counter];

             sum7+= U[counter];
                   }

                M_Q_Len=sum1/sample_size;
                M_Packet_Loss=sum3/sample_size;
                M_Delay=sum5/sample_size;
                M_Utilization=sum7/sample_size;


                for (counter =0;counter < sample_size ;counter++)


             {
                    sum2+=Math.pow(  LqA [counter] -M_Q_Len,2);
             sum4+=Math.pow(    PLP_A[counter] -M_Packet_Loss ,2);
             sum6+= Math.pow( WqA[counter] -M_Delay ,2);
             sum8+= Math.pow(  U[counter] -M_Utilization,2);


               }

      sum2/=(sample_size-1);
      sum4/=(sample_size-1);
      sum6/=(sample_size-1);
      sum8/=(sample_size-1);

   V_Q_Len= Math.sqrt(sum2);

   V_Packet_Loss  =  Math.sqrt(sum4);

   V_Delay  =   Math.sqrt(sum6);

   V_Utilization  =  Math.sqrt(sum8);

     double U1,U2,U3,U4,L1,L2,L3,L4;
     double U1C,U2C,U3C,L1C,L2C,L3C;

     // 1.96 is correspngin to %95 CI

                       U1=  M_Q_Len +1.96*V_Q_Len /Math.sqrt(sample_size-1);
                       L1=  M_Q_Len  -1.96 *V_Q_Len  /Math.sqrt(sample_size-1);

                       U2= M_Packet_Loss  +1.96*  V_Packet_Loss  /Math.sqrt(sample_size-1);
                       L2= M_Packet_Loss - 1.96 *   V_Packet_Loss/Math.sqrt(sample_size-1);

                       U3=M_Delay + 1.96 *   V_Delay /Math.sqrt(sample_size-1);
                       L3=M_Delay-1.96 *  V_Delay /Math.sqrt(sample_size-1);

                       U4= M_Utilization +1.96 * V_Utilization /Math.sqrt(sample_size-1);
                       L4= M_Utilization-1.96 *  V_Utilization  /Math.sqrt(sample_size-1);

  // System.out.println(M_Delay+"\t"+M_Utilization);
   sim_out2.println(M_Utilization+ "\t"+M_Delay);

   }

   static double GE (double a ,double v)
    {
   double U,X;

   U=Math.random();

   X=-1/(a*v)*Math.log((1-U)/a);
if (X < 0)
          X=0;

   return X;

   }
   public static double  expon(double  rate )  /* Exponential variate generation function. */
{
    return -1/rate * Math.log(Math.random());
}
}

