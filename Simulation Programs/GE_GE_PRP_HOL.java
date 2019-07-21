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

    public class GE_GE_PRP_HOL
   {

    public static double   sample_size=20; // number of iterations

    public static double WqP[]=new double [80];
    public static double WqC[]=new double [80];
    public static double U[]= new double [80];
    public static double LqP[]=new double [80];
    public static double LqC[]=new double [80];
    public static double PLP_P[]= new double [80];
     public static double PLP_C[]= new double [80];
     public static double sense_time[]= new double [80];

    public static int counter;
    public static double  M_Q_Len_P, M_Packet_Loss_P, M_Delay_P,  M_Utilization,M_Q_Len_C, M_Packet_Loss_C, M_Delay_C, M_sense_time;
    public static double V_Q_Len_P,V_Packet_Loss_P, V_Delay_P, V_Q_Len_C,V_Packet_Loss_C, V_Delay_C, V_Utilization, V_sense_time;
    public static int num_customer_required;
    public static double sim_time,mu,scvP,scvC,scv2,aP,aC,a2;
    public static double max_double;
    public static int next_event_type;
    public static  int num_customer;
    public static  int num_in_q_PU,num_in_q_CU ,k,KK;

    public static int server_status;
    public static  double time_past;
    public static double average_number_in_queue_PU,average_number_in_queue_CU;
   public static double    server_utilization;
    public static  double total_of_delays_PU,total_of_delays_CU;
    public static double average_delay_PU,average_delay_CU;
    public static double area_num_in_q_PU,area_num_in_q_CU;
    public static  double area_server_status;
    public static   double time_last_event;
    public static    int Q_SIZE=1000,index;
    public static int num_delayed_PU,num_delayed_CU,lost_PU,lost_CU, sensing_nodes;
    public static double sensingstarttime,sensingtime,node, average_sensing_time;

   public static double lamda_PU[]=new double[50];// mean arrival rate for corresponding GE
   public static double lamda_CU[]=new double[50];// mean arrival rate for corresponding GE
   public static   double[] time_next_event=new double[3];
   public static    double[] time_arrival_PU=new double[Q_SIZE+1];
   public static    double[] time_arrival_CU=new double[Q_SIZE+1];
   public static double CU_pending_serv_time, response_time_PU,response_time_CU, job_in_server,ST,ST_CU;
   static PrintWriter sim_out;
   static PrintWriter sim_out2;


   GE_GE_PRP_HOL(PrintWriter out)
    {
       sim_out2 = out;
   }

   public static void initialize()

      {
       lamda_CU[KK] = (KK*0.2 + 0.44);
       lamda_PU[KK] =lamda_CU[KK]-0.08;

        mu=4.0;   // Mean service rate for corresponding GE

       scvP= 1.0;
       scvC=1.0;// inter-arrival SCV
       scv2=1.0;// service time SCV

        aP= 2.0/(scvP+1);
        aC=2.0/(scvC+1);
        a2=2.0/(scv2+1);

        num_customer_required=100000;
        sim_time=0.0;
        response_time_PU = 0.0;
        CU_pending_serv_time= 0.0;
        response_time_CU=0.0;
        max_double=2000000;
        num_customer=0;
        lost_PU=lost_CU=0;
        sensing_nodes = 0;
        num_in_q_PU=0;
        num_in_q_CU=0;
        total_of_delays_PU=0.0;
        total_of_delays_CU=0.0;
        server_status=0;//server status is IDLE

      //initialize the statistical counters
         area_num_in_q_PU=0.0;
         area_num_in_q_CU=0.0;
         area_server_status=0.0;
         time_last_event=0.0;
         time_next_event[0]=sim_time+GE(aP,lamda_PU[KK]);
         time_next_event[1]=sim_time+GE(aC,lamda_CU[KK]);
         time_next_event[2]=max_double;
         num_delayed_PU=0;
         num_delayed_CU=0;
         sensingstarttime=0.0;
         sensingtime=0.0;
         node=0.0;
         time_past= 0;
         //M_Utilization=0;

        }

//------------------------------------------the main program.---------------
       public void mainMethod()

      {
       sim_out2.println("\n**********PRP HOL ************* ");
       sim_out2.println("M_Utilization\tM_SU Delay\tM_Q len_SU");
       

    //System.out.println("M_Utilization\tM_Q len_PU\tM_Q len_SU");


         //KK=1;
      while (M_Utilization < 0.9)//for ( KK=0;KK<20;KK++)
      {
       
         counter=0;

   while (counter < sample_size )

   {
         initialize();

         while(num_customer<num_customer_required)

         {
             timing();
           //sim_out.println(sim_time+"\t"+time_next_event[0]+"\t"+time_next_event[1]+"\t"+ST+"\t"+time_next_event[2]+"\t"
                  // +CU_pending_serv_time+"\t"+num_in_q_CU+"\t"+num_in_q_PU);
            update_time_avg_stats();
            if(next_event_type==0||next_event_type==1)
            {arrive();}
            else {depart();} }
         report();
         counter++;

        }

      confidence_calculation();
       KK+=1;

         }
    //sim_out2.close();
    //sim_out.close();
     System.out.println("The output of PRP HOL has been written to the file");
      }

       public static void timing()

       {
         if((time_next_event[0]<=time_next_event[2])&& (time_next_event[0]<=time_next_event[1]))

              next_event_type = 0;

           else if(time_next_event[1] <= time_next_event[2])

                next_event_type = 1;

                else
                     next_event_type = 2;

         time_last_event = sim_time;

       // advance the simulation clock
        sim_time = time_next_event[next_event_type];
      }

      static void premptServer()
        {
          sensingstarttime=sim_time;
          sensing_nodes++;
          num_delayed_CU++;

          CU_pending_serv_time =time_next_event[2]-sim_time;
          time_next_event[1]=sim_time;
          if(num_in_q_CU < Q_SIZE-1)
          {
              num_in_q_CU++;

          }
        else lost_CU++;
          try {
                for(int i = num_in_q_CU; i>1; i--)
                    {
                        time_arrival_CU[i]=time_arrival_CU[i-1];
                     }

                  time_arrival_CU[1] = sim_time; }
          catch (ArrayIndexOutOfBoundsException e) { e.printStackTrace();}
      }
      static void servePremptedCU()
        {
          ST = CU_pending_serv_time;
          sensing_nodes--;
          CU_pending_serv_time= 0;
           

          try {
                for(int i = 1; i<= num_in_q_CU; i++)

                        time_arrival_CU[i]=time_arrival_CU[i+1];

              }
          catch (ArrayIndexOutOfBoundsException e) { e.printStackTrace();}

       num_in_q_CU--;
       sensingtime=sim_time-sensingstarttime;
       node++;

      }
       public static void arrive()

      {

        if(next_event_type == 0)
        {

           time_next_event[0]=sim_time+GE(aP,lamda_PU[KK]);
            num_customer++;

            if(server_status==0)
            {
                server_status=1;
                job_in_server =0;
                ST=GE(a2,mu);
                time_next_event[2]=sim_time+ST;
                response_time_PU+=ST;

                //num_delayed_PU++;
            }
        
            else if(server_status==1&&job_in_server ==1)
            {
                
                premptServer();
                job_in_server=0;
                ST=GE(a2,mu);
                time_next_event[2]=sim_time+ST;
                response_time_PU+=ST;
                
            }
            else if(server_status==1 && num_in_q_PU < Q_SIZE - 1) // If server is BUSY
            {

            try{
             num_in_q_PU++;
             num_delayed_PU++;
               time_arrival_PU[num_in_q_PU]=sim_time;
            }
           catch(ArrayIndexOutOfBoundsException e) { e.printStackTrace();}

         }
         else lost_PU++;
       }

        else if (next_event_type == 1)
       {
           time_next_event[1]=sim_time+GE(aC,lamda_CU[KK]);
           num_customer++;

            if(server_status==0)
            {
                server_status=1;
                job_in_server = 1;
                ST = GE(a2, mu);
                
                    
                time_next_event[2]=sim_time+ST;
                ST_CU=time_next_event[2];
                response_time_CU+=ST;
                num_delayed_CU++;
            }

        else if(num_in_q_CU < (Q_SIZE - 1)) // If server is BUSY
            {

            //try{
                    num_in_q_CU++;
                    num_delayed_CU++;
                    time_arrival_CU[num_in_q_CU]=sim_time;
               //  }
           // catch(ArrayIndexOutOfBoundsException e) { e.printStackTrace();}

            }
         else lost_CU++;

       }

        }

       public static void depart()

      { 
         if(num_in_q_PU==0 && num_in_q_CU==0)

         {
             server_status=0;
             time_next_event[2]=max_double;
         }

         else if(num_in_q_PU>0)
         {
            
            total_of_delays_PU+=sim_time-time_arrival_PU[1];
           // num_customer++;
            num_delayed_PU++;
            ST=GE(a2,mu);
            time_next_event[2]=sim_time+ST;
            response_time_PU+=(total_of_delays_PU+ST);
            job_in_server = 0;

         try {
            for(int i=1;i<= num_in_q_PU;i++)

                time_arrival_PU[i]=time_arrival_PU[i+1];
            }
             catch(ArrayIndexOutOfBoundsException e) { e.printStackTrace();}
             num_in_q_PU--;
            }

       else if(num_in_q_CU >0)
           {

            total_of_delays_CU+=sim_time-time_arrival_CU[1];

            if(sensing_nodes!=0)
                servePremptedCU();
            else {ST=GE(a2,mu);
            //num_customer++;
            }

           
            num_delayed_CU++;
            time_next_event[2]=sim_time+ST;
            ST_CU=time_next_event[2];
            response_time_CU+=(total_of_delays_CU+ST);
            job_in_server = 1;

         try {
            for(int i=1;i<= num_in_q_CU;i++)

                time_arrival_CU[i]=time_arrival_CU[i+1];
            }
             catch(ArrayIndexOutOfBoundsException e) { e.printStackTrace();}
             num_in_q_CU--;
           }
        }

       public static void update_time_avg_stats(){

        //Update area accumulators for time-average statistics

         time_past=sim_time-time_last_event;
         area_num_in_q_PU+=(time_past*num_in_q_PU);
         area_num_in_q_CU+=(time_past*num_in_q_CU);
         area_server_status+=(time_past*server_status);
         average_sensing_time += sensingtime;
       }


       public static void report()

      {

     WqP[counter]=total_of_delays_PU / num_delayed_PU;
     WqC[counter]=total_of_delays_CU / num_delayed_CU;
     U[counter]= area_server_status / sim_time;
     LqP[counter]=area_num_in_q_PU / sim_time;
     LqC[counter]=area_num_in_q_CU / sim_time;
     PLP_P[counter]= (double)lost_PU /num_customer;
     PLP_C[counter]= (double)lost_CU /num_customer;
     sense_time[counter] = average_sensing_time/node;

       }


    public static void confidence_calculation()
        {
       double sum1_P=0, sum2_P=0, sum3_P=0, sum4_P=0, sum5_P=0, sum6_P=0, sum7=0, sum8=0 ,sum9=0, sum10=0;
       double sum1_C=0, sum2_C=0, sum3_C=0, sum4_C=0, sum5_C=0, sum6_C=0;

          for (counter=0;counter<sample_size;counter++)

             {
             sum1_P+= LqP [counter];
             sum1_C+= LqC [counter];

             sum3_P+= PLP_P[counter];
             sum3_C+= PLP_C[counter];

             sum5_P+=WqP[counter];
             sum5_C+=WqC[counter];

             sum7+= U[counter];
             sum9+=sense_time[counter];
                   }

                M_Q_Len_P=sum1_P/sample_size;
                M_Q_Len_C=sum1_C/sample_size;
                M_Packet_Loss_P=sum3_P/sample_size;
                M_Packet_Loss_C=sum3_C/sample_size;
                M_Delay_P=sum5_P/sample_size;
                M_Delay_C=sum5_C/sample_size;
                M_Utilization=sum7/sample_size;
                M_sense_time= sum9/sample_size;


                for (counter =0;counter < sample_size ;counter++)


             {sum2_P+=Math.pow(  LqP [counter] -M_Q_Len_P,2);
             sum4_P+=Math.pow(    PLP_P[counter] -M_Packet_Loss_P ,2);
             sum6_P+= Math.pow( WqP[counter] -M_Delay_P ,2);
             sum8+= Math.pow(  U[counter] -M_Utilization,2);
             sum2_C+=Math.pow(  LqC [counter] -M_Q_Len_C,2);
             sum4_C+=Math.pow(    PLP_C[counter] -M_Packet_Loss_C ,2);
             sum6_C+= Math.pow( WqC[counter] -M_Delay_C ,2);
             sum10+= Math.pow( sense_time[counter] -M_sense_time ,2);


               }

      sum2_P/=(sample_size-1);
      sum4_P/=(sample_size-1);
      sum6_P/=(sample_size-1);
      sum2_C/=(sample_size-1);
      sum4_C/=(sample_size-1);
      sum6_C/=(sample_size-1);
      sum8/=(sample_size-1);
      sum10/=(sample_size-1);

   V_Q_Len_P= Math.sqrt(sum2_P);

   V_Packet_Loss_P  =  Math.sqrt(sum4_P);

   V_Delay_P  =   Math.sqrt(sum6_P);

   V_Q_Len_C= Math.sqrt(sum2_C);

   V_Packet_Loss_C  =  Math.sqrt(sum4_C);

   V_Delay_C  =   Math.sqrt(sum6_C);

   V_Utilization  =  Math.sqrt(sum8);

   V_sense_time  =  Math.sqrt(sum10);

     double U1P,U2P,U3P,U4,U5,L1P,L2P,L3P,L4,L5;
     double U1C,U2C,U3C,L1C,L2C,L3C;

     // 1.96 is correspngin to %95 CI

                       U1P=  M_Q_Len_P +1.96*V_Q_Len_P /Math.sqrt(sample_size-1);
                       L1P=  M_Q_Len_P  -1.96 *V_Q_Len_P  /Math.sqrt(sample_size-1);

                       U2P= M_Packet_Loss_P  +1.96*  V_Packet_Loss_P  /Math.sqrt(sample_size-1);
                       L2P= M_Packet_Loss_P - 1.96 *   V_Packet_Loss_P /Math.sqrt(sample_size-1);

                       U3P=M_Delay_P + 1.96 *   V_Delay_P /Math.sqrt(sample_size-1);
                       L3P=M_Delay_P-1.96 *  V_Delay_P /Math.sqrt(sample_size-1);

                       U1C=  M_Q_Len_C +1.96*V_Q_Len_C /Math.sqrt(sample_size-1);
                       L1C=  M_Q_Len_C  -1.96 *V_Q_Len_C  /Math.sqrt(sample_size-1);

                       U2C= M_Packet_Loss_C  +1.96*  V_Packet_Loss_C  /Math.sqrt(sample_size-1);
                       L2C= M_Packet_Loss_C  - 1.96 *   V_Packet_Loss_C /Math.sqrt(sample_size-1);

                       U3C=M_Delay_C + 1.96 *   V_Delay_C /Math.sqrt(sample_size-1);
                       L3C=M_Delay_C-1.96 *  V_Delay_C /Math.sqrt(sample_size-1);

                       U4= M_Utilization +1.96 * V_Utilization /Math.sqrt(sample_size-1);
                       L4= M_Utilization-1.96 *  V_Utilization  /Math.sqrt(sample_size-1);

                       U5= M_sense_time +1.96 * V_sense_time /Math.sqrt(sample_size-1);
                       L5= M_sense_time-1.96 *  V_sense_time  /Math.sqrt(sample_size-1);

   //System.out.println(lamda_CU[KK]+"\t"+ lamda_PU[KK]+"\t"+M_Utilization+"\t"+M_Q_Len_P+"\t"+M_Q_Len_C+"\t"+(M_Delay_C));//+(1/mu)));
    //System.out.println( M_Utilization+"\t"+M_Q_Len_P+"\t"+M_Q_Len_C+"\t"+M_Delay_C);
   sim_out2.println(M_Utilization+"\t"+M_Delay_C+"\t"+M_Q_Len_C);

   }

   static double GE (double a ,double v)
    {
   double U,X;

     U=Math.random();

     X=-(1.0/(a*v))*Math.log((1-U)/a);
    if (X < 0)
          X = 0;
   
   return X;

   }
}

        
