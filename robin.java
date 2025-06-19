//ITCS325 - Project - CPU Priority Scheduling with Round Robin
//Fatima Khalil Khalil - 202107442
//Sarah Jalal Ali - 202109931
//Zahra Salman Ahmed - 202104265
//Zahra Sayed Mohamed - 202104523
//Bayan Habib Ahmed - 202102697

import java.util.ArrayList;
import java.util.Scanner;

public class robin
{
   public static void main(String[] args) 
   {
      Scanner kbd = new Scanner(System.in);
      int q, maxArrival=0;
      int [][] priQ = new int[100][10];
      System.out.print("Enter Quantium q: ");
      q=kbd.nextInt();
      System.out.println("\nEnter process ID, Arrival time, and burst time (in ms) for each process to be run on the CPU");
      System.out.println("Note: Process ID, Burst Time, and Priority must be a Positive Integer");
      System.out.println("To Terminate, enter (0  0  0  0)");
      System.out.println("\n\n Process ID \t Arrival Time \t Burst Time \t Priority");
      int pID, aTime,bTime, priority,minArrivalIndex=0, c=0, tot=0;
   
      do
      {
         pID=kbd.nextInt();
         aTime=kbd.nextInt();
         bTime=kbd.nextInt();
         priority=kbd.nextInt();
         
         if (pID>=0 && aTime>=0 && bTime>0 && priority>0)
         {
            priQ[c][0]=pID;
            priQ[c][1]=aTime; //arrival time
            priQ[c][2]=bTime; //burst time
            priQ[c][3]=priority; //priority number
            priQ[c][4]=bTime; //remaining execution time
            priQ[c][5]=-1; //start time
            priQ[c][6]=0; //finish time
            priQ[c][7]=0; //TurnAround
            priQ[c][8]=0; //Response Time
            priQ[c][9]=0; //Waiting time
         
            tot+=bTime; //total of execution time
            
            if(aTime>maxArrival)
               maxArrival=aTime;
            if(aTime<priQ[minArrivalIndex][1] || c==0)
               minArrivalIndex= c;
            
            c++;
         } 
         else if (pID==0 && aTime==0 && bTime==0 && priority==0) 
            System.out.println("\t      ----------  Terminated  ----------");
         else
            System.out.println("Invalid Input, Please Enter Again the last process values");
      
      } while (pID!=0 || aTime!=0 || bTime!=0 || priority!=0);
   
   
      ArrayList<Integer> robin = new ArrayList<Integer>(); //used to store the RoundRobin queue
      ArrayList<Integer> gantChart = new ArrayList<Integer>(); //used for print later
      int index = minArrivalIndex; //to track each iteration index
      boolean rrMode = false; //used incase of RoundRobin
      int qDo = q; //used to execute Quantium time for RoundRobin case
   
      /* ##############    Loop 1: Until Maximun Arrival Time      ################## */
      for (int i=0; i<tot; i++)
      {
         /********Search for best Process to select (highest Priority) with arrival time constraint********/
         for(int j=0; j<c; j++)
         {
            if(priQ[j][1] <= i && priQ[j][4] !=0)
            {
               if(priQ[index][4]==0)
               {
                  rrMode = false; 
                  index=j;
               }    
               
               if(priQ[j][3]<priQ[index][3])
               {
                  rrMode = false; 
                  index = j;
               }
            
               if (priQ[j][3]==priQ[index][3] && j!=index) //roundRobin 
               {
                  rrMode = true; 
                  
                  if (robin.size()==0) 
                  {
                     if(priQ[index][1] < priQ[j][1])
                     {
                        robin.add(index);
                        robin.add(j);
                     }
                     else if(priQ[index][1] > priQ[j][1])
                     {
                        robin.add(j);
                        robin.add(index);
                     }
                     else
                     {
                        robin.add(index);
                        robin.add(j);
                     }
                  }
                  else
                  {
                     boolean in1 = false, in2=false;
                     
                     for (int j2 = 0; j2 < robin.size(); j2++) 
                     {
                        if (robin.get(j2) == index) 
                           in1=true;
                        if (robin.get(j2) == j) 
                           in2=true;                           
                     }
                  
                     //found one with higher priority and they are not in the list
                     if(!in1 || !in2)
                     {
                        //robin.clear();
                        for(int j3 = 0; j3 < robin.size(); j3++)
                        {
                           if (!in1)
                           {   
                              if(priQ[robin.get(j3)][3] > priQ[index][3])
                              {
                                 robin.add(j3, index);
                                 in1=true;  
                              }
                           }
                           
                           if (!in2 && in1==true)
                           {   
                              if(priQ[robin.get(j3)][3] > priQ[j][3])
                              {
                                 robin.add(j3, j);
                                 in2=true;  
                              }
                              else if(priQ[robin.get(j3)][3] == priQ[j][3])
                                 continue;
                           }
                        } //end for
                     } //end if
                  
                     //reached end of list and didn't add it (meaning all same priority)
                     if (!in2) {
                        robin.add(j);
                     }
                     
                  } //end else
               } //end robin case  
            } //end if arrived earlier
         } //end for j loop
         
         if (rrMode) 
         {
            index = robin.get(0);
            qDo = q;
            
            //if process didn't run before. record the start time
            if(priQ[robin.get(0)][5] == -1)
               priQ[robin.get(0)][5]=i;
            
            //execute Quantium time for first process in RR queue
            while (qDo!=0 && priQ[robin.get(0)][4]!=0) 
            {
               priQ[robin.get(0)][4]--;
               gantChart.add(robin.get(0));
               i++;
               qDo--;
            }
            i--;
            
            //if process has no remaining time, record end time
            if(priQ[robin.get(0)][4] == 0)
            {
               priQ[robin.get(0)][6] = i+1;  //record finish time
               qDo = q; //reset quantum time
               robin.remove(0); //remove completed process
            }   
            else
            {
               int complete = robin.get(0); //completed index
               robin.remove(0);
               boolean enter = false;
               //add the completed in the end of list of same priority
               for(int j3 = 0; j3 < robin.size(); j3++)
               {
                  if(priQ[robin.get(j3)][3] > priQ[complete][3])
                  {
                     enter = true;
                     robin.add(j3, complete);
                     break;
                  }
                  else if(priQ[robin.get(j3)][3] == priQ[complete][3])
                     continue;
                  //if equal then we add it at end 
               }
            
               //if we reached end of list and didn't add it, then add it at end of list
               if(!enter) 
                  robin.add(complete);
            }
            
            rrMode=false; //to check in case new process arrive at that time
         
         }  //end rr mode          
         else
         {
            rrMode=false;
            
            priQ[index][4]--; 
            gantChart.add(index);
            
            if(priQ[index][5] == -1)
               priQ[index][5]=i;
            
            if(priQ[index][4] == 0)
               priQ[index][6] = i+1; 
                        
         } 
         
         //If 1 process left in roundRobin Queue, Switch to Noraml case
         if(robin.size() == 1)
         {
            robin.clear();
            qDo=q;
         } 
         
         //one of priority group is only left
         if(robin.size()>1 && (priQ[robin.get(0)][3] != priQ[robin.get(1)][3]))
         {
            robin.remove(0);
         }
      
      } //end for i loop
      
      
   
      /* ##############    Print Gannt Chart      ################## */
      System.out.println("\nThe Gant Chart for the above input: ");
      System.out.print("0 - ");
      for (int i = 0; i < gantChart.size() - 1; i++) 
      {
         if (gantChart.get(i) != gantChart.get(i + 1)) 
         {
            System.out.print("P" + priQ[gantChart.get(i)][0] + " - " + (i + 1) + " - ");
         }
      }
   
      //print last process
      System.out.print("P" + priQ[gantChart.get(gantChart.size() - 1)][0] + "  -  " + gantChart.size());
      
      
      /* ##############    Print Each Process turnaround, response, and waiting time      ################## */
      System.out.println("\n\nEach Process Turnaround Time, Response Time, Waiting Time:\nPID\t Turnaround Time\t Response Time\t\t Waiting Time");
      int totalTurnAround=0;
      int totalResponseTime=0;
      int totalWaiting=0;
      for(int i=0; i<c; i++)
      {
         //priQ[i][?], 1, Arrival,  2 burstTime, 5 Start, 6 Finish
         //7 turnAround, 8 ResponseTime, 9 Waiting
         System.out.print(" P" + priQ[i][0] + "\t\t\t\t"); 
      
         //turnAround = finish time - arrival time
         int turnAround = priQ[i][6] - priQ[i][1];
         priQ[i][7] = turnAround;
         System.out.print(priQ[i][7] + "\t\t\t\t\t"); 
         totalTurnAround += turnAround;
         
         //response time = start time - arrival time
         int response = priQ[i][5] - priQ[i][1];
         priQ[i][8] = response;
         System.out.print(priQ[i][8] + "\t\t\t\t\t\t"); 
         totalResponseTime += response;
         
         //waiting time = finsish time - arrival time - burst
         int wait = priQ[i][6] - priQ[i][5] - priQ[i][2];
         priQ[i][9] = wait;
         System.out.println(priQ[i][9]); 
         totalWaiting += wait;
      
      }
      
      /* ##############    Calculate & print Average turnaround, response, and waiting time      ################## */
      double numOfProcess = c*1.0;
      double turnAvg = totalTurnAround / numOfProcess;
      double responseAvg = totalResponseTime / numOfProcess;
      double waitAvg = totalWaiting / numOfProcess;
   
      System.out.printf("\nAverage Turnaround Time = %.3f (ms)%n", turnAvg);
      System.out.printf("Average Response Time = %.3f (ms)%n", responseAvg);
      System.out.printf("Average Waiting Time = %.3f (ms)%n", waitAvg);
      
   }
}