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

    public class RelayNode_PB
   {

    public static double   sample_size=20; // number of iterations
    public static int channels = 4, depart_channel,E_arr, I_dep, arr_channel;
    public static int PU_Q_SIZE = 100000;
    public static int CU_Q_SIZE = 100000;
    public static double [][]prob_matrix = {{0.0, 1.0,0.0,0.0},
                                            {0.0,0.0,0.5, 0.5},
                                            {1.0,0.0,0.0,0.0},
                                            {1.0,0.0,0.0,0.0}}; //1.0,0.0,0.0,0.0

    public static double WqP[][]=new double [channels][80];
    public static double WqC[][]=new double [channels][80];
    public static double U[][]= new double [channels][80];
    public static double LqP[][]=new double [channels][80];
    public static double LqC[][]=new double [channels][80];
    public static double PLP_P[][]=new double [channels][80];
    public static double PLP_C[][]=new double [channels][80];
    public static double sense_time[]= new double [80];


    public static double []M_Q_Len_P = new double [channels];
    public static double []M_Packet_Loss_P = new double [channels];
    public static double []M_Delay_P = new double [channels];
    public static double []M_Utilization = new double [channels];
    public static double []M_Q_Len_C = new double [channels];
    public static double []M_Packet_Loss_C = new double [channels];
    public static double []M_Delay_C = new double [channels];
    public static double []M_sense_time = new double [channels];

    public static double []V_Q_Len_P = new double [channels];
    public static double []V_Packet_Loss_P = new double [channels];
    public static double []V_Delay_P = new double [channels];
    public static double []V_Utilization = new double [channels];
    public static double []V_Q_Len_C = new double [channels];
    public static double []V_Packet_Loss_C = new double [channels];
    public static double []V_Delay_C = new double [channels];
    public static double []V_sense_time = new double [channels];
    
   public static   double[] time_next_event=new double[3];
   public static    double[][] time_arrival_PU=new double[channels][PU_Q_SIZE+1];
   public static    double[][] time_arrival_CU=new double[channels][CU_Q_SIZE+1];
   public static double []depart_time = new double [channels];
   public static double []PU_arrival_time = new double [channels];
   public static double []CU_arrival_time = new double [channels];
   public static double []CU_pending_serv_time = new double [channels];
   public static double []area_server_status = new double [channels];
   public static double []area_num_in_q_PU = new double [channels];
   public static double []area_num_in_q_CU = new double [channels];
   public static double []total_of_delays_PU = new double [channels];
   public static double []total_of_delays_CU = new double [channels];
   public static int []num_delayed_PU = new int [channels];
   public static int []num_delayed_CU = new int [channels];
   public static double []response_time_PU = new double [channels];
   public static double []response_time_CU = new double[channels];
   public static int []lost_PU = new int[channels];
    public static int []lost_CU = new int[channels];
   
    public static int []server_status = new int [channels];
    public static int []job_in_server = new int [channels];
    public static int []num_in_q_PU = new int [channels];
    public static int []num_in_q_CU = new int [channels];
    public static int []sensing_nodes = new int [channels];
  

    public static int counter;
    public static int num_customer_required;
    public static double sim_time,mu,mu2,scvP,scvC,scv2,aP,aC,a2;
    public static double max_double;
    public static int next_event_type;
    public static  int num_customer, prev_job;
    public static  int k,KK;
    public static double []ST_CU = new double [channels];

    public static  double time_past,ST;
    public static double average_number_in_queue_PU,average_number_in_queue_CU;
   public static double    server_utilization;
    public static double average_delay_PU,average_delay_CU;
    public static   double time_last_event;
    public static double sensingstarttime,sensingtime,node, average_sensing_time;

   public static double lamda_PU[]=new double[50];// mean arrival rate for corresponding GE
   public static double lamda_CU[]=new double[50];// mean arrival rate for corresponding GE
   static PrintWriter sim_out;
   static PrintWriter sim_out2;
   public static Random r = new Random();

   public static void initialize()

      {
       lamda_CU[KK] = 2.6;//0.2+(KK*0.2);
       lamda_PU[KK] =2.2;//0.2+(KK*0.2);
        mu=5.0;  // Mean service rate for corresponding GE
        mu2 = 2.5;
       scvP= 1.0;
       scvC=1.0;// inter-arrival SCV
       scv2=1.0;// service time SCV

        aP= 2.0/(scvP+1);
        aC=2.0/(scvC+1);
        a2=2.0/(scv2+1);

        num_customer_required=100000;
        sim_time=0.0;
        
        max_double=2000000;
        num_customer=0;
        for(int i =1; i<channels; i++)
        {
            response_time_PU[i] = 0.0;
            CU_pending_serv_time[i]= 0.0;
            response_time_CU[i]=0.0;
            num_in_q_PU[i]=0;
            num_in_q_CU[i]=0;
            depart_time[i] = max_double;;
            ST_CU[i] = 0.0;
            lost_PU[i]=0;
            lost_CU[i]=0;
            sensing_nodes[i] = 0;
            total_of_delays_PU[i]=0.0;
            total_of_delays_CU[i]=0.0;
            server_status[i]=0;//server status is IDLE

      //initialize the statistical counters
         area_num_in_q_PU[i]=0.0;
         area_num_in_q_CU[i]=0.0;
         area_server_status[i]=0.0;
         num_delayed_PU[i]=0;
         num_delayed_CU[i]=0;
          }
        
         time_last_event=0.0;
         time_next_event[0]=sim_time+GE(aP,lamda_PU[KK]);
         time_next_event[1]=sim_time+GE(aC,lamda_CU[KK]);
         depart_channel =departTime();
         time_next_event[2]=depart_time[depart_channel];
        
         sensingstarttime=0.0;
         sensingtime=0.0;
         node=0.0;

        }

RelayNode_PB(PrintWriter out)
        {
    sim_out2 = out;
        }

//------------------------------------------the main program.---------------
       //public static void main(String[] args)throws IOException
public void mainMethod()
      {
        //sim_out2= new PrintWriter(new FileWriter("CRoutputTand.xls"));
    
       sim_out2.println("\n*********RelayNodes_PB****************");
       sim_out2.println("SU\t average_U\tav_queLen\tAv_delay");

   


     KK =0;
     for(CU_Q_SIZE = 100; CU_Q_SIZE<=250; CU_Q_SIZE+=10)// for ( KK=0;KK<12;KK++)
      {
        PU_Q_SIZE= 100;

         counter=0;

   while (counter < sample_size )

   {
         initialize();

         while(num_customer<num_customer_required)

         {
           timing();
            update_time_avg_stats();
            if(next_event_type==0||next_event_type==1)
            {
                E_arr = 0;
               arr_channel= branch_next_channel(C_Prob_Matrix(), E_arr);
               if(arr_channel != 0)
                  arrive(arr_channel);
                
            }
            else {
                E_arr = depart_channel;
                depart(depart_channel);
            }
             if((num_in_q_CU[1]+num_in_q_CU[2]+num_in_q_CU[3]) >CU_Q_SIZE ) break;
         }
         report();
         counter++;

        }
   for (int i =1; i<channels; i++)
   {

      confidence_calculation(i);
    }

 int n = 1;
 double average_utilisation = ((M_Utilization[n]+M_Utilization[n+1]+M_Utilization[n+2])/(n+2));
 double average_PU_q_len =(M_Q_Len_P[n]+M_Q_Len_P[n+1]+M_Q_Len_P[n+2])/(n+2);
 double average_CU_q_len =(M_Q_Len_C[n]+M_Q_Len_C[n+1]+M_Q_Len_C[n+2])/(n+2);
 double average_PU_delay =(M_Delay_P[n]+Math.max(M_Delay_P[n+1], M_Delay_P[n+2]));
 double average_CU_delay =(M_Delay_C[n]+Math.max(M_Delay_C[n+1], M_Delay_C[n+2]));
 double average_PU_packet_loss =(M_Packet_Loss_P[n]+M_Packet_Loss_P[n+1]+M_Packet_Loss_P[n+2])/(n+2);
 double average_CU_packet_loss =(M_Packet_Loss_C[n]+M_Packet_Loss_C[n+1]+M_Packet_Loss_C[n+2])/(n+2);

// System.out.println(lamda_CU[KK]+"\t"+ M_Utilization[n]+"\t"+M_Q_Len_C[n] +"\t"+M_Delay_C[n]+
               //     "\t"+M_Utilization[n+1]+"\t"+M_Q_Len_C[n+1] +"\t"+M_Delay_C[n+1]+
                //    "\t"+M_Utilization[n+2]+"\t"+M_Q_Len_C[n+2] +"\t"+M_Delay_C[n+2]+
                 //   "\t"+average_utilisation+"\t"+average_CU_q_len+"\t"+average_CU_delay);



   sim_out2.println(CU_Q_SIZE+"\t"+ average_utilisation+"\t"+average_CU_q_len+"\t"+average_CU_delay);
         }
    //sim_out2.close();
 System.out.println("RelayNode_PB simulation has end and the output has been written to the file");
      }

       public static void timing()

       {
           depart_channel = departTime();
            time_next_event[2]= depart_time[depart_channel];
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

       public static void arrive(int channel)

      {
      if( E_arr == 0){
        if(next_event_type == 0)
        {

           time_next_event[0]=sim_time+GE(aP,lamda_PU[KK]);
            num_customer++;

            if(server_status[channel]==0)
            {
                server_status[channel]=1;
                job_in_server[channel] =1;
                ST=GE(a2,mu);
                depart_time[channel]=sim_time+ST;
                response_time_PU[channel]+=ST;

                //num_delayed_PU++;
            }

            else if(server_status[channel]==1&&job_in_server[channel] ==2)
            {

                premptServer(channel);
                job_in_server[channel]=1;

            }
            else if(server_status[channel]==1 && job_in_server[channel] ==1 && num_in_q_PU[channel] < PU_Q_SIZE - 1) // If server is BUSY
            {

            try{
             num_in_q_PU[channel]++;
               time_arrival_PU[channel][num_in_q_PU[channel]]=sim_time;
            }
           catch(ArrayIndexOutOfBoundsException e) { e.printStackTrace();}

         }
         else lost_PU[channel]++;
       }

        else if (next_event_type == 1)
       {
           time_next_event[1]=sim_time+GE(aC,lamda_CU[KK]);
           num_customer++;

            if(server_status[channel]==0)
            {
                server_status[channel]=1;
                job_in_server[channel] = 2;
                ST = GE(a2, mu);


                depart_time[channel]=sim_time+ST;
                ST_CU[channel]=depart_time[channel];
                response_time_CU[channel]+=ST;
                num_delayed_CU[channel]++;
            }

        else if(server_status[channel]==1 &&num_in_q_CU[channel] < (CU_Q_SIZE - 1)) // If server is BUSY
            {

            //try{
                    num_in_q_CU[channel]++;
                    time_arrival_CU[channel][num_in_q_CU[channel]]=sim_time;
               //  }
           // catch(ArrayIndexOutOfBoundsException e) { e.printStackTrace();}

            }
         else lost_CU[channel]++;

       }
          }
 else 
     {
          double mu_used;
           if(channel ==1) mu_used = mu;
            else mu_used = mu2;
        if(prev_job == 1)
        {
            if(server_status[channel]==0)
            {
                server_status[channel]=1;
                job_in_server[channel] =1;
                ST=GE(a2,mu_used);
                depart_time[channel]=sim_time+ST;
                response_time_PU[channel]+=ST;

                //num_delayed_PU++;
            }

            else if(server_status[channel]==1&&job_in_server[channel] ==2)
            {

                premptServer(channel);
                job_in_server[channel]=1;

            }
            else if(server_status[channel]==1 && num_in_q_PU[channel] < (PU_Q_SIZE - 1)) // If server is BUSY
            {

            try{
             num_in_q_PU[channel]++;
               time_arrival_PU[channel][num_in_q_PU[channel]]=sim_time;
            }
           catch(ArrayIndexOutOfBoundsException e) { e.printStackTrace();}

         }
         else lost_PU[channel]++;
       }

        else if (prev_job == 2)
       {

            if(server_status[channel]==0)
            {
                server_status[channel]=1;
                job_in_server[channel] = 2;
                ST = GE(a2, mu_used);


                depart_time[channel]=sim_time+ST;
                ST_CU[channel]=depart_time[channel];
                response_time_CU[channel]+=ST;
                num_delayed_CU[channel]++;
            }

        else if(server_status[channel]==1&&num_in_q_CU[channel] < (CU_Q_SIZE - 1)) // If server is BUSY
            {

            //try{
                    num_in_q_CU[channel]++;
                    time_arrival_CU[channel][num_in_q_CU[channel]]=sim_time;
               //  }
           // catch(ArrayIndexOutOfBoundsException e) { e.printStackTrace();}

            }
         else lost_CU[channel]++;

       }
      }
 
 
 }


       public static void depart(int channel)

      {
           double mu_used;
           if(channel ==1) mu_used = mu;
            else mu_used = mu2;
       
           prev_job = job_in_server[channel];

         if(num_in_q_PU[channel]==0 && num_in_q_CU[channel]==0)

         {
             server_status[channel]=0;
             depart_time[channel]=max_double;
             job_in_server[channel] = 0;
         }

         else if(num_in_q_PU[channel]>0)
         {

            total_of_delays_PU[channel]+=sim_time-time_arrival_PU[channel][1];
           // num_customer++;
            num_delayed_PU[channel]++;

            ST=GE(a2,mu_used);
            depart_time[channel]=sim_time+ST;
            response_time_PU[channel]+=(total_of_delays_PU[channel]+ST);
            job_in_server[channel] = 1;

         try {
            for(int i=1;i<= num_in_q_PU[channel];i++)

                time_arrival_PU[channel][i]=time_arrival_PU[channel][i+1];
            }
             catch(ArrayIndexOutOfBoundsException e) { e.printStackTrace();}
             num_in_q_PU[channel]--;
            }

       else if(num_in_q_CU[channel] >0)
           {

            total_of_delays_CU[channel]+=sim_time-time_arrival_CU[channel][1];

            if(sensing_nodes[channel]!=0)
                servePremptedCU(channel);
            else {ST=GE(a2,mu_used);
            //num_customer++;
            }


            num_delayed_CU[channel]++;
            depart_time[channel]=sim_time+ST;
            ST_CU[channel]=depart_time[channel];
            response_time_CU[channel]+=(total_of_delays_CU[channel]+ST);
            job_in_server[channel] = 2;

         try {
            for(int i=1;i<= num_in_q_CU[channel];i++)

                time_arrival_CU[channel][i]=time_arrival_CU[channel][i+1];
            }
             catch(ArrayIndexOutOfBoundsException e) { e.printStackTrace();}
             num_in_q_CU[channel]--;
           }
                     
            arr_channel = branch_next_channel(C_Prob_Matrix(),channel); // stopped here
            if (arr_channel!=0)
            arrive(arr_channel);
       
        }

       public static void update_time_avg_stats(){

        //Update area accumulators for time-average statistics

         time_past=sim_time-time_last_event;
         
         for (int i =1; i<(channels); i++)
          {
         area_num_in_q_PU[i]+=time_past*(double) num_in_q_PU[i];
         area_num_in_q_CU[i]+=time_past* (double) num_in_q_CU[i]; 
         area_server_status[i]+=time_past * server_status[i];
           }
        
         //average_sensing_time += sensingtime;
       }
       
       // method to find the area server status
       
       public static void report()

      {
        for(int i =1; i<channels; i++)
        {
     
            WqP[i][counter]=total_of_delays_PU[i] / num_delayed_PU[i];
     
            WqC[i][counter]=total_of_delays_CU[i] / num_delayed_CU[i];
     
            U[i][counter]= area_server_status[i] / sim_time;
     
            LqP[i][counter]=area_num_in_q_PU [i]/ sim_time;
     
            LqC[i][counter]=area_num_in_q_CU[i] / sim_time;
            PLP_P[i][counter]= (double)lost_PU[i] /num_customer;

            PLP_C[i][counter]= (double)lost_CU[i] /num_customer;
        }
        
     
        //sense_time[counter] = average_sensing_time/node;

      }


    public static void confidence_calculation(int i)
        {
           
            
       double sum1_P=0, sum2_P=0, sum3_P=0, sum4_P=0, sum5_P=0, sum6_P=0, sum7=0, sum8=0 ,sum9=0, sum10=0;
       double sum1_C=0, sum2_C=0, sum3_C=0, sum4_C=0, sum5_C=0, sum6_C=0;

          for (counter=0;counter<sample_size;counter++)
              
             {
                 
             sum1_P+= LqP[i] [counter];
             sum1_C+= LqC [i][counter];

             sum3_P+= PLP_P[i][counter];
             sum3_C+= PLP_C[i][counter];

             sum5_P+=WqP[i][counter];
             sum5_C+=WqC[i][counter];

             sum7+= U[i][counter];
            // sum9+=sense_time[counter];
                   }

                M_Q_Len_P[i]=sum1_P/sample_size;
                M_Q_Len_C[i]=sum1_C/sample_size;
                M_Packet_Loss_P[i]=sum3_P/sample_size;
                M_Packet_Loss_C [i]=sum3_C/sample_size;
                M_Delay_P[i]=sum5_P/sample_size;
                M_Delay_C[i]=sum5_C/sample_size;
                M_Utilization[i]=sum7/sample_size;
               // M_sense_time[i]= sum9/sample_size;


                for (counter =0;counter < sample_size ;counter++)


             {sum2_P+=Math.pow(  LqP [i][counter] -M_Q_Len_P[i],2);
             sum4_P+=Math.pow(    PLP_P[i][counter] -M_Packet_Loss_P[i] ,2);
             sum6_P+= Math.pow( WqP[i][counter] -M_Delay_P[i] ,2);
             sum8+= Math.pow(  U[i][counter] -M_Utilization[i],2);
             sum2_C+=Math.pow(  LqC [i][counter] -M_Q_Len_C[i],2);
             sum4_C+=Math.pow(    PLP_C[i][counter] -M_Packet_Loss_C[i] ,2);
             sum6_C+= Math.pow( WqC[i][counter] -M_Delay_C[i] ,2);
             sum10+= Math.pow( sense_time[counter] -M_sense_time[i] ,2);


               }

      sum2_P/=(sample_size-1);
      sum4_P/=(sample_size-1);
      sum6_P/=(sample_size-1);
      sum2_C/=(sample_size-1);
      sum4_C/=(sample_size-1);
      sum6_C/=(sample_size-1);
      sum8/=(sample_size-1);
      sum10/=(sample_size-1);

   V_Q_Len_P[i]= Math.sqrt(sum2_P);

   V_Packet_Loss_P[i]  =  Math.sqrt(sum4_P);

   V_Delay_P [i] =   Math.sqrt(sum6_P);

   V_Q_Len_C[i]= Math.sqrt(sum2_C);

   V_Packet_Loss_C[i]  =  Math.sqrt(sum4_C);

   V_Delay_C[i]  =   Math.sqrt(sum6_C);

   V_Utilization[i]  =  Math.sqrt(sum8);

   //V_sense_time [i] =  Math.sqrt(sum10);

     double U1P,U2P,U3P,U4,U5,L1P,L2P,L3P,L4,L5;
     double U1C,U2C,U3C,L1C,L2C,L3C;

     // 1.96 is correspngin to %95 CI

                       U1P=  M_Q_Len_P[i] +1.96*V_Q_Len_P[i] /Math.sqrt(sample_size-1);
                       L1P=  M_Q_Len_P[i]  -1.96 *V_Q_Len_P[i]  /Math.sqrt(sample_size-1);

                       U2P= M_Packet_Loss_P [i] +1.96*  V_Packet_Loss_P [i] /Math.sqrt(sample_size-1);
                       L2P= M_Packet_Loss_P[i] - 1.96 *   V_Packet_Loss_P [i]/Math.sqrt(sample_size-1);

                       U3P=M_Delay_P[i] + 1.96 *   V_Delay_P [i]/Math.sqrt(sample_size-1);
                       L3P=M_Delay_P[i]-1.96 *  V_Delay_P[i] /Math.sqrt(sample_size-1);

                       U1C=  M_Q_Len_C[i] +1.96*V_Q_Len_C[i] /Math.sqrt(sample_size-1);
                       L1C=  M_Q_Len_C [i] -1.96 *V_Q_Len_C[i]  /Math.sqrt(sample_size-1);

                       U2C= M_Packet_Loss_C[i]  +1.96*  V_Packet_Loss_C [i] /Math.sqrt(sample_size-1);
                       L2C= M_Packet_Loss_C[i]  - 1.96 *   V_Packet_Loss_C[i] /Math.sqrt(sample_size-1);

                       U3C=M_Delay_C[i] + 1.96 *   V_Delay_C[i] /Math.sqrt(sample_size-1);
                       L3C=M_Delay_C[i]-1.96 *  V_Delay_C[i] /Math.sqrt(sample_size-1);

                       U4= M_Utilization[i] +1.96 * V_Utilization[i] /Math.sqrt(sample_size-1);
                       L4= M_Utilization[i]-1.96 *  V_Utilization [i] /Math.sqrt(sample_size-1);

                      // U5= M_sense_time[i] +1.96 * V_sense_time[i] /Math.sqrt(sample_size-1);
                       //L5= M_sense_time[i]-1.96 *  V_sense_time[i]  /Math.sqrt(sample_size-1);
   
            

   }
       
      static void premptServer(int chan)
        {
          sensingstarttime=sim_time;
          sensing_nodes[chan]++;
          num_delayed_CU[chan]++;
          CU_pending_serv_time[chan] =depart_time[chan]-sim_time;
          if(num_in_q_CU[chan] < CU_Q_SIZE-1)
          {
              num_in_q_CU[chan]++;
          }
        else lost_CU[chan]++;
          try {
                for(int i = num_in_q_CU[chan]; i>1; i--)
                    {
                        time_arrival_CU[chan][i]=time_arrival_CU[chan][i-1];
                     }

                  time_arrival_CU[chan][1] = sim_time; }
          catch (ArrayIndexOutOfBoundsException e) { e.printStackTrace();}
      }
      static void servePremptedCU(int chan)
        {
          ST = CU_pending_serv_time[chan];
          sensing_nodes[chan]--;
          CU_pending_serv_time[chan]= 0;
          sensingtime=sim_time-sensingstarttime;
            node++;

      }
      
      // method for finding the minimum departure time
     static int departTime()
        {
          int min = 1;
           for(int c =1; c < channels;c++ )
           {
                    if(depart_time[c]< depart_time[min])
                    min=c;
               
            }
           return min;
       }

static double [][] C_Prob_Matrix()
        {

    double [][] cumm_prob = new double [channels][channels];
    for(int i = 0; i< channels; i++)
    {

        cumm_prob[i][0]=prob_matrix[i][0];
        for(int j =1; j< channels; j++)
        {
            cumm_prob[i][j]=prob_matrix[i][j]+cumm_prob[i][j-1];

        }
    }
    return cumm_prob;

}
static int branch_next_channel(double [][] matrix, int channel)
        {
         double cumm_prob = 0.0;
           cumm_prob = r.nextDouble();
           int next_channel=0;
           for(int i =1; i<channels; i++)
           {
           if (cumm_prob<= matrix[channel][i])
                    {
                        if (matrix[channel][i]>matrix[channel][i-1])
                            {

                            next_channel = i ;
                            break;
                            }
                     }
            }
           return next_channel;
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


