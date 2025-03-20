/*  Name: Ernesto Padrino Sequera  
    Course: CNT 4714 Fall 2024 
    Assignment title: Project 2 – Synchronized/Cooperating Threads – A  Banking Simulation 
    Due Date: September 22, 2024 
*/

import java.util.concurrent.*;

public class ABankingSimulator
{
    // Fall 2024 - 10 withdrawal threads, 5 depositor threads, 2 transfer threads, 1 internal auditor thread, and 1 treasury department auditor thread
    public static final int MAX_AGENTS = 19;

    public static void main(String[] args)
    {
        // Thread pool - size 19
        ExecutorService application = Executors.newFixedThreadPool(MAX_AGENTS); // Executor object

        // Define the joint accounts
        ABankAccount jointAccount1 = new ABankAccount();
        ABankAccount jointAccount2 = new ABankAccount();

        try
        {
            // Print headings for the simulation run
            System.out.println("* * *   SIMULATION BEGINS...");
            System.out.println();
            System.out.println("Deposit Agents\t\t\t\t\t\t\tWithdrawal Agents\t\t\t\t\t\t\t\t\tBalances\t\t\t\t\t\t\t\t\t\t\tTransaction Number");
            System.out.println("--------------\t\t\t\t\t\t\t-----------------\t\t\t\t\t\t\t------------------------\t\t\t\t\t\t\t\t--------------------------");

            // Create and execute a depositor agent
            for (int i = 0; i < 5; i++)
            {
                application.execute(new Depositor(jointAccount1, jointAccount2, "Agent DT" + i));
            }

            // Create and execute a withdrawal agent
            for (int i = 0; i < 10; i++)
            {
                application.execute(new Withdrawal(jointAccount1, jointAccount2, "Agent WT" + i));
            }

            // Create and execute a transfer agent
            for (int i = 0; i < 2; i++)
            {
                application.execute(new Transfer(jointAccount1, jointAccount2, "Agent TR" + i));
            }

            application.execute(new InternalAudit(jointAccount1, jointAccount2));   // Create and execute an internal audit agent
            application.execute(new TreasuryDeptAudit(jointAccount1, jointAccount2));   // Create and execute a treasury dept audit agent
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

        //application.shutdown();
    }


}