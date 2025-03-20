/*  Name: Ernesto Padrino Sequera  
    Course: CNT 4714 Fall 2024 
    Assignment title: Project 2 – Synchronized/Cooperating Threads – A  Banking Simulation 
    Due Date: September 22, 2024 
*/

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class ABankAccount implements TheBank
{
    // Lock to control mutually exclusive access to the bank account
    private static Lock accessLock = new ReentrantLock();

    // Variables for the bank account
    private int balance = 0;
    private static int transactionNum = 0;
    private int internalCounter = 0;
    private int internalHolder = 0;
    private int treasuryCounter = 0;
    private int treasuryHolder = 0;

    // Flag alert variables
    private static final int DEPOSIT_ALERT_LEVEL = 450;
    private static final int WITHDRAWAL_ALERT_LEVEL = 90;
    private static final int TRANSFER_ALERT_LEVEL = 350;

    // Formatting currency variable
    private NumberFormat nF = NumberFormat.getCurrencyInstance(Locale.US);

    // Condition variables
    private Condition canWithdraw = accessLock.newCondition();

    // Prints console to file
    static
    {
        try
        {
            PrintStream ps = new PrintStream(new File("ABankingSimulatorOutput.txt"));
            System.setOut(ps);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    // Getter method for lock
    public Lock getLock()
    {
        return accessLock;
    }

    // Method used to make a deposit into the bank account
    public void deposit(int depositAmount, String accountNum, String agentName)
    {
        // Get the lock on the account
        accessLock.lock();
        try
        {
            // Make deposit into account
            balance += depositAmount;

            // Increase transaction number
            transactionNum++;

            System.out.println(agentName + " deposits " + nF.format(depositAmount) + " into: " + accountNum + " \t\t\t\t\t\t\t\t\t\t\t (+) " + accountNum + " balance is: " + nF.format(balance) + "\t\t\t\t\t\t\t\t\t\t\t " + transactionNum);

            // Handle transaction logging for flagged transaction
            flagged_transaction(depositAmount, agentName, "D");

            // Signal all waiting threads that deposit has been made
            canWithdraw.signalAll();

        }
        catch (Exception e)
        {
            System.out.println("Exception throw making a deposit of funds");
        }
        finally
        {
            // Unlock the bank account
            accessLock.unlock();
        }
    }

    
    // Method used to make a withdrawal from the bank account
    public void withdrawal(int withdrawalAmount, String accountNum, String agentName)
    {
        // Lock the bank account
        accessLock.lock();

        try
        {
            if (balance < withdrawalAmount)
            {
                System.out.println("\t\t\t\t\t\t\t\t" + agentName + " attempts to withdraw " + nF.format(withdrawalAmount) + " from " + accountNum + " (*****) WITHDRAWAL BLOCKED - INSUFFICIENT FUNDS!!!! Balance only " + nF.format(balance));
                
                // Blocks agents until a deposit has been made
                canWithdraw.await();
            }
            else if (balance >= withdrawalAmount)
            {
                // Makes withdrawal from account
                balance -= withdrawalAmount;

                // Increase transaction number
                transactionNum++;

                System.out.println("\t\t\t\t\t\t\t\t" + agentName + " withdraws " + nF.format(withdrawalAmount) + " from " + accountNum + " \t\t\t (-) " + accountNum + " balance is: " + nF.format(balance) + "\t\t\t\t\t\t\t\t\t\t\t " + transactionNum);

                // Check for flagged transaction
                flagged_transaction(withdrawalAmount, agentName, "W");
            }
        }
        catch (Exception e)
        {
            System.out.println("An Exception was thrown getting making a withdrawal of funds");
        }
        finally
        {
            // Unlock the bank account
            accessLock.unlock();
        }
    }

    // Method used to make a transfer from one bank account to another bank account
    public void transfer(int transferAmount, String fromAcctNum, String toAcctNum, String agentName, ABankAccount jointAccount)
    {
        // Invocation of this method is coming on the fromAcct - hence all account references are to the toAcct
        // Returns true if both locks are free to grab
        if (accessLock.tryLock() && jointAccount.getLock().tryLock())
        {
            try
            {
                // Acquires inner locks
                accessLock.lock();
                jointAccount.getLock().lock();
                try
                {
                    // Check for sufficient funds to make transfer
                    if (balance >= transferAmount)
                    {
                        // Makes withdrawal from account
                        balance -= transferAmount;

                        // Transfer the amount to the new account
                        jointAccount.balance += transferAmount;

                        // Increase transaction number
                        transactionNum++;

                        System.out.println("\t\t\t\t TRANSFER --> " + agentName + " transferring " + nF.format(transferAmount) + " from " + fromAcctNum + " to " + toAcctNum + " - - " + fromAcctNum + " balance is now " + nF.format(balance) + "\t\t\t\t\t\t\t\t\t\t\t " + transactionNum);
                        System.out.println("\t\t\t\t TRANSFER COMPLETE -->  Account " + toAcctNum + " balance is now " + nF.format(jointAccount.balance) + "\n");

                        // Check for flagged transaction
                        flagged_transaction(transferAmount, agentName, "T");
                    } 
                }
                catch (Exception e)
                {
                    System.out.println("An Exception was thrown attempting the transfer of funds");
                }
                finally 
                {
                    // Always release the inner locks if held 
                    accessLock.unlock();
                    jointAccount.getLock().unlock();
                }
            }
            catch (Exception e)
            {
                System.out.println("An Exception was thrown attempting the transfer of funds");
            }
            finally 
            {
                // Always release the outer locks if held 
                accessLock.unlock();
                jointAccount.getLock().unlock();
            }
        }
    }

    // Method used to make an audit of the bank account 
    public void internalAudit(ABankAccount jointAccount)
    {
        // Print headings
        // Print # transactions since last audit line
        // Run audit numbers and print results
        // Reset # transaction counters
        if (accessLock.tryLock() && jointAccount.getLock().tryLock())
        {
            try
            {
                // Acquires inner locks
                accessLock.lock();
                jointAccount.getLock().lock();
                try
                {
                    internalCounter = transactionNum - internalHolder;
                    internalHolder = transactionNum;
                    System.out.println("\n\n*********************************************************************************************************************************************************************");
                    System.out.println("\n\nIntenal Bank Audit Beginning...\n");
                    System.out.println("\tThe total number of transactions since the last Internal audit is: " + internalCounter + "\n");
                    System.out.println("\tINTERNAL BANK AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-1 TO BE: " + nF.format(balance));
                    System.out.println("\tINTERNAL BANK AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-2 TO BE: " + nF.format(jointAccount.balance) + "\n\n");
                    System.out.println("Internal Bank Audit Complete\n\n");
                    System.out.println("*********************************************************************************************************************************************************************\n\n");
                }
                catch (Exception e)
                {
                    System.out.println("An Exception was thrown getting the balance by an Internal Auditor.");
                }
                finally 
                {
                    // Always release the inner locks if held 
                    accessLock.unlock();
                    jointAccount.getLock().unlock();
                }
            }
            catch (Exception e)
            {
                System.out.println("An Exception was thrown getting the balance by an Internal Auditor.");
            }
            finally 
            {
                // Always release the outer locks if held 
                accessLock.unlock();
                jointAccount.getLock().unlock();
            }
        }
    }

    // Method used to make an audit of the bank account 
    public void treasuryDepartmentAudit(ABankAccount jointAccount)
    {
        if (accessLock.tryLock() && jointAccount.getLock().tryLock())
        {
            try
            {
                // Acquires inner locks
                accessLock.lock();
                jointAccount.getLock().lock();
                try
                {
                    // Print headings
                    // Print # transactions since last audit line
                    // Run audit numbers and print results
                    // Reset # transaction counters
                    treasuryCounter = transactionNum - treasuryHolder;
                    treasuryHolder = transactionNum;
                    System.out.println("\n\n*********************************************************************************************************************************************************************");
                    System.out.println("\n\nUNITED STATES DEPARTMENT OF THE TREASURY - Bank Audit Beginning...\n");
                    System.out.println("\tThe total number of transactions since the last Treasury Department audit is: " + treasuryCounter + "\n");
                    System.out.println("\tTREASURY DEPT AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-1 TO BE: " + nF.format(balance));
                    System.out.println("\tTREASURY DEPT AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-2 TO BE: " + nF.format(jointAccount.balance) + "\n\n");
                    System.out.println("UNITED STATES DEPARTMENT OF THE TREASURY - Bank Audit Terminated...\n\n");
                    System.out.println("*********************************************************************************************************************************************************************\n\n");
                }
                catch (Exception e)
                {
                    System.out.println("An Exception was thrown getting the balance by a Treasury Department Auditor.");
                }
                finally
                {
                    // Always release the inner locks if held 
                    accessLock.unlock();
                    jointAccount.getLock().unlock();
                }   
            }
            catch (Exception e)
            {
                System.out.println("An Exception was thrown getting the balance by a Treasury Department Auditor.");
            }
            finally
            {
                // Always release the outer locks if held 
                accessLock.unlock();
                jointAccount.getLock().unlock();
            }
        }
    }
    

    // Method used to log flagged transactions made against the bank account
    public void flagged_transaction(int transAmount, String agentName, String transt_type)
    {
        // Set class variables
        FileWriter fw = null;
        Date date = new Date();
        String dateTimeStr = "";
        try
        {
            // Writes log transactions to csv file
            fw = new FileWriter("transactions.csv", true);
            dateTimeStr = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG).format(date); // Fix

            // Depending on the type of transaction, it will output a slighly different log
            if (transAmount >= DEPOSIT_ALERT_LEVEL && transt_type.equals("D"))
            {
                System.out.println("\n\n***Flagged Transaction*** " + agentName + " Made A Deposit In Excess Of " + nF.format(DEPOSIT_ALERT_LEVEL) + " USD - See Flagged Transaction Log.\n");
                fw.write(agentName + " issued deposit of " + nF.format(transAmount) + " at: " + dateTimeStr + "  Transaction Number: " + transactionNum + "\n");
            }
            else if (transAmount >= WITHDRAWAL_ALERT_LEVEL && transt_type.equals("W"))
            {
                System.out.println("\n\n***Flagged Transaction*** " + agentName + " Made A Withdrawal In Excess Of " + nF.format(WITHDRAWAL_ALERT_LEVEL) + " USD - See Flagged Transaction Log.\n");
                fw.write(agentName + " issued withdrawal of " + nF.format(transAmount) + " at: " + dateTimeStr + "  Transaction Number: " + transactionNum + "\n");
            }
            else if (transAmount >= TRANSFER_ALERT_LEVEL && transt_type.equals("T"))
            {
                System.out.println("\n\n***Flagged Transaction*** " + agentName + " Made A Transfer In Excess Of " + nF.format(TRANSFER_ALERT_LEVEL) + " USD - See Flagged Transaction Log.\n");
                fw.write(agentName + " issued transfer of " + nF.format(transAmount) + " at: " + dateTimeStr + "  Transaction Number: " + transactionNum + "\n");
            }
            fw.close();
        }
        catch(NumberFormatException numberFormatException)
        {
            numberFormatException.printStackTrace();
        }
        catch(FileNotFoundException fileNotFoundException)
        {
            fileNotFoundException.printStackTrace();
        }
        catch(IOException ioException)
        {
            ioException.printStackTrace();
        }
    }
}
