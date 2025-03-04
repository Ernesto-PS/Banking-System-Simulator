# Banking-System-Simulator

**Overview**

The simulation models bank account transactions where multiple synchronized agents operate concurrently on two accounts. Transactions must be properly synchronized using ReentrantLocks to avoid lost updates and race conditions. The system implements the producer-consumer problem where withdrawal agents block when there are insufficient funds.

**Key Components**

**Agent-Based Transactions**

Depositor Agents (5): Randomly deposit $1-$600 into accounts (never block).  
Withdrawal Agents (10): Randomly withdraw $1-$99 but block when funds are insufficient.  
Transfer Agents (2): Atomically transfer money between accounts (do not block but retry).  
Internal Audit Agent (1): Periodically verifies balances across all accounts.  
Treasury Audit Agent (1): Verifies all bank-owned account balances periodically.  

**Concurrency Management**

Uses FixedThreadPool() and an Executor object for efficient thread management.  
Implements Reentrant Locks for synchronization (no synchronized keyword or custom locks).  
Ensures fair access to accounts, preventing transaction monopolization.  

**Transaction Logging & Compliance**

Logs all flagged transactions (transactions.csv), including deposits over $450 and withdrawals over $90 (simulating anti-money laundering laws).  
Assigns each transaction a unique transaction number and maintains an audit trail.  

**Randomized Event Execution**

Agents operate in infinite loops with random sleep intervals to simulate real-world transaction timing.  
Different agents have varying frequencies (withdrawals occur most frequently, audits least frequently).  

**How to Run**

**1. Compile all the files**

Use the following format to compile each java file: javac (Name_of_file).java

**2. Run the ABankSimulator file**

Use the "java ABankingSimulator" command to run the program

**3. Observe Output Files**

Look at ABankingSimulatorOutput.txt file to see the real-time transactions.  
Look at transactions.txt for any flagges transactions.  
Look at the documentation provided for an example
