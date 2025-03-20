/*  Name: Ernesto Padrino Sequera  
    Course: CNT 4714 Fall 2024 
    Assignment title: Project 2 – Synchronized/Cooperating Threads – A  Banking Simulation 
    Due Date: September 22, 2024 
*/

// Bank interface specifies abstract methods to define the allowed behaviors on a bank account.
public interface TheBank 
{   
    // Deposit arguments: deposit amount, account number, thread name making the deposit
    public abstract void deposit(int depositAmount, String accountNum, String agentName);

    // Withdrawal argument: withdrawal amount, account number, thread name making the withdrawal
    public abstract void withdrawal(int withdrawalAmount, String accountNum, String agentName);

    // Transfer arguments: transfer amount, from account, to account, thread name making the transfer, and the account to which the money is being transferred to
    public abstract void transfer(int transferAmount, String fromAcctNum, String toAcctNum, String agentName, ABankAccount jointAcct);

    // Internal banking audit - examines balance only
    public abstract void internalAudit(ABankAccount jointAccount);

    // External banking audit - Treasury Dept. - examines balance only
    public abstract void treasuryDepartmentAudit(ABankAccount jointAccount);

    // Flagged transactions are logged independently into a log file
    // Flagged transactions arguments: transaction amount, thread name making the transaction, type of thread making the transaction
    // Use "D" for depositor thread type, "W" for withdrawal thread type, and "T" for transfer thread type 
    public abstract void flagged_transaction(int transAmount, String agentName, String transt_type);
}
