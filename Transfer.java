import java.util.*;

public class Transfer implements Runnable {
    private static final int MAX_TRANSFER = 400;
    private static final int MAXSLEEPTIME = 2500;
    private static Random transferAccount = new Random();
    private static Random sleepTime = new Random();
    private ABankAccount jointAccount1;
    private ABankAccount jointAccount2;
    private int transferAmount;
    private int accountNum;
    private String tname;

    public Transfer(ABankAccount shared1, ABankAccount shared2, String name) // References to the joint accounts and the thread's name making the deposit
    {
        jointAccount1 = shared1;
        jointAccount2 = shared2;
        tname = name;
    }

    // Transfer money from an account to an account
    public void run()
    {
        while(true)
        {
            try
            {
                // Generates at random one of the two accounts
                accountNum = (Math.random() <= 0.5) ? 1 : 2;

                // Generates a random amount of money from $1 to $400
                transferAmount = transferAccount.nextInt(MAX_TRANSFER) + 1;
                
                if (accountNum == 1)
                {
                    jointAccount1.transfer(transferAmount, "JA-1", "JA-2", tname, jointAccount2);
                }
                else
                {
                    jointAccount2.transfer(transferAmount, "JA-2", "JA-1", tname, jointAccount1);
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
