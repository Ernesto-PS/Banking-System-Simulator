/*  Name: Ernesto Padrino Sequera  
    Course: CNT 4714 Fall 2024 
    Assignment title: Project 2 – Synchronized/Cooperating Threads – A  Banking Simulation 
    Due Date: September 22, 2024 
*/

import java.util.*;

public class Withdrawal implements Runnable {
    private static final int MAX_WITHDRAWAL = 99;
    private static final int MAXSLEEPTIME = 200;
    private static Random withdrawalAccount = new Random();
    private static Random sleepTime = new Random();
    private ABankAccount jointAccount1;
    private ABankAccount jointAccount2;
    private int withdrawalAmount;
    private int accountNum;
    private String tname;

    public Withdrawal(ABankAccount shared1, ABankAccount shared2, String name) // References to the joint accounts and the thread's name making the deposit
    {
        jointAccount1 = shared1;
        jointAccount2 = shared2;
        tname = name;
    }

    // Add money to the bank account
    public void run()
    {
        while(true)
        {
            try
            {
                // Generates at random one of the two accounts
                accountNum = (Math.random() <= 0.5) ? 1 : 2;

                // Generates a random amount of money from $1 to $99
                withdrawalAmount = withdrawalAccount.nextInt(MAX_WITHDRAWAL) + 1;
                
                if (accountNum == 1)
                {
                    jointAccount1.withdrawal(withdrawalAmount, "JA-1", tname);
                }
                else
                {
                    jointAccount2.withdrawal(withdrawalAmount, "JA-2", tname);
                }

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
