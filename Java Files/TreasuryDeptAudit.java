/*  Name: Ernesto Padrino Sequera  
    Course: CNT 4714 Fall 2024 
    Assignment title: Project 2 – Synchronized/Cooperating Threads – A  Banking Simulation 
    Due Date: September 22, 2024 
*/

import java.util.*;

public class TreasuryDeptAudit implements Runnable 
{
    private static final int MAXSLEEPTIME = 5500;
    private static Random sleepTime = new Random();
    private ABankAccount jointAccount1;
    private ABankAccount jointAccount2;
    //private int accountNum;

    public TreasuryDeptAudit(ABankAccount shared1, ABankAccount shared2) // References to the joint accounts and the thread's name making the deposit
    {
        jointAccount1 = shared1;
        jointAccount2 = shared2;
    }

    // Add money to the bank account
    public void run()
    {
        while(true)
        {
            try
            {
                // Accesses both accounts to conduct audit
                jointAccount1.treasuryDepartmentAudit(jointAccount2);

                // Generates random sleep time
                Thread.sleep(sleepTime.nextInt(MAXSLEEPTIME) + 1);
            }
            catch (InterruptedException exception)
            {
                exception.printStackTrace();
            }
        }
    }
}
