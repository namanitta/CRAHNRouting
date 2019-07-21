/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author anamanya
 */
import java.io.*;
public class Dissertation {
      public static void main(String[] args)throws IOException{
          File directory = new File("Dissertation");
             directory.mkdir();
          System.out.println("Simulations started please wait....");
          PrintWriter output = new PrintWriter(new FileWriter("Dissertation/AccessMethods.xls"));
          PrintWriter output2 = new PrintWriter(new FileWriter("Dissertation/GatedNode.xls"));
          PrintWriter output3 = new PrintWriter(new FileWriter("Dissertation/PRPmethods.xls"));
          PrintWriter output4 = new PrintWriter(new FileWriter("Dissertation/QOS.xls"));

          Probability_Branching probAccess = new Probability_Branching(output);
          Shortest_Queue1 sqAccess = new Shortest_Queue1(output);
          Gated_Node_Theoretical theoGN = new Gated_Node_Theoretical(output2);
          Gated_Node_Simulation simGN = new Gated_Node_Simulation(output2);
          GE_GE_PRP_HOL hol = new  GE_GE_PRP_HOL(output3);
          GE_GE_PRP_EOL eol = new  GE_GE_PRP_EOL(output3);
          GEGEC_R_Class_HOL_Theo holtheo = new GEGEC_R_Class_HOL_Theo(output3);
          CRQOS_R qos = new CRQOS_R(output4);
          RelayNodes rnode = new RelayNodes(output4);
          RelayNode_PB  pbrnode = new RelayNode_PB(output4);

          probAccess.mainMethod();
          sqAccess.mainMethod();
          theoGN.mainMethod();
          simGN.mainMethod();
          hol.mainMethod();
          eol.mainMethod();
          holtheo.mainMethod();
          qos.mainMethod();
          rnode.mainMethod();
          pbrnode.mainMethod();

          output.close();
          output2.close();
          output3.close();
          output4.close();



      }

}
