package group13.fisherr;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import edu.uwec.cs.wagnerpj.daointerface.StudentPersistenceController;

public class Test {
	
	public static void main(String[] args) {
		
		GeneratorUtilities generator = new GeneratorUtilities();
		
		
		//1. Provide hardcoded text file paths for the file reader
		
				//String fileName1 = "src/transactionSet_01.txt";
				//String fileName2 = "src/transactionSet_02.txt";
				String fileName3 = "src/transactionSet_03.txt";//current location of Dr.Wagners test case from online
				// String fileName = "H://CS/CS 355/GROUP13/GROUP13.FISHERR/src/transactionSet_01.txt";
				
				//2. Read the transaction set from the file
				//transactions2 and transactions4.txt
				TransactionSet textFileTranSet = generator.getTransactionSetFromFile("src/transactions1.txt");
				
				//3. specify the minimumSupportLevel, calculated or hardcoded
				double minimumSupportLevel = 0;
				
				//4. specify the minimumConfidenceLevel
				double minimumConfidenceLevel = 0;
			   
				if(generator.validateMinLevel(minimumSupportLevel) && generator.validateMinLevel(minimumConfidenceLevel)){
					Timer timer = new Timer();
					timer.startTimer();
					TransactionSet apriori = generator.doApriori(textFileTranSet, minimumSupportLevel);
					//System.out.println("APRIORI: \n" + apriori);
					timer.stopTimer();
					System.out.println("elapsed time in msec.: " + timer.getTotal() );
					//5. generate the ruleSet from the apriori 
					RuleSet generatedRuleSet = generator.generateRuleSet(textFileTranSet, apriori, minimumConfidenceLevel);
					
					//6. Write ruleset to the output file
					System.out.println(generatedRuleSet);
					//inserting original TransactionSet and generated RuleSet
					
					
					
					
					DAOController(textFileTranSet,generatedRuleSet);
					
					
					
					
					
					
					PrintWriter writer;
					try {
						writer = new PrintWriter("output.txt");
						writer.println(generatedRuleSet);
						writer.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
				//ERRORS	
				}else{
					
					if(!generator.validateMinLevel(minimumSupportLevel)){
						System.out.println("Error: Minimum Support Level must be between 0.000 and 1.000");

					}
					if(!generator.validateMinLevel(minimumConfidenceLevel)){
						System.out.println("Error: Minimum Confidence Level must be between 0.000 and 1.000");

					}
					//System.out.println("Minimum Support Level and Minimum Confidence Level must be between 0.000 and 1.000");
				}
			}//end of MAIN
			
			
			
			
			
		
		
	
	



public static void DAOController(TransactionSet transactionSet, RuleSet ruleSet){
	
	
	/*DAO MAIN*/
	
	VendorPersistenceController vendorPC = new VendorPersistenceController();		// controller for delegating vendor persistence
	RulePersistenceController rulePC = new RulePersistenceController();		// controller for delegating rule persistence
	RuleSetPersistenceController ruleSetPC = new RuleSetPersistenceController();		// controller for delegating ruleSet persistence
	TransactionPersistenceController tranPC = new TransactionPersistenceController();		// controller for delegating transaction persistence
	TransactionSetPersistenceController tranSetPC = new TransactionSetPersistenceController();		// controller for delegating transactionSet persistence

	String daoString = null;
    InputStreamReader unbuffered = new InputStreamReader( System.in );
    BufferedReader keyboard = new BufferedReader( unbuffered );
	try {
		System.out.println("Use (Mock) DAO or (MySQL) DAO? Mock");
		daoString = keyboard.readLine();
	}
	catch (IOException error) {
		System.err.println("Error reading input");
	}
	//set the daoStrings
	vendorPC.setDAO(daoString);
	rulePC.setDAO(daoString);
	ruleSetPC.setDAO(daoString);
	tranPC.setDAO(daoString);
	tranSetPC.setDAO(daoString);
	
	
	
	System.out.println(transactionSet.getVendorSet());
	//persist Vendor
	for(Vendor vendor: transactionSet.getVendorSet()){
		vendorPC.persistVendor(vendor);
	}
	
	
	
	//persist TransactionSet
	tranSetPC.persistTransactionSet(transactionSet);
	//persist RuleSet
	ruleSetPC.persistRuleSet(ruleSet);
	
	
	//iterate through each transaction in transactionSet and persist
	for(Transaction transaction: transactionSet.getTransactionSet()){
		System.out.println("persisting transaction");
		tranPC.persistTransaction(transaction);
		
	}
	//iterate through each rule in ruleSet and persist
	for(Rule rule: ruleSet.getRuleSet()){
		System.out.println("persisting rule");
		rulePC.persistRule(rule);
	}
	
}
}