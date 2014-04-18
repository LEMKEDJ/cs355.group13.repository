package service;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneratorUtilities implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double minimumSupportLevel;
	private double minimumConfidenceLevel;
	private String filepath;
	
	public GeneratorUtilities(){
		
		//minimumSupportLevel = 0;
		//minimumConfidenceLevel =0;
	}
	
	public GeneratorUtilities(double minimumSupportLevel, double minimumConfidenceLevel, String filepath){
		this.minimumSupportLevel=minimumSupportLevel;
		this.minimumConfidenceLevel = minimumConfidenceLevel;
		this.filepath = filepath;
		
	}

	
	public GeneratorUtilities(GeneratorUtilities genUtils) {
		// TODO Auto-generated constructor stub
		//GeneratorUtilities copy = new GeneratorUtilities();
		//copy.setMinimumConfidenceLevel(genUtils.getMinimumConfidenceLevel());
		//copy.setMinimumSupportLevel(genUtils.getMinimumSupportLevel());
		setMinimumConfidenceLevel(genUtils.getMinimumConfidenceLevel());
		setMinimumSupportLevel(genUtils.getMinimumSupportLevel());
		setFilepath(genUtils.getFilepath());
	}

	

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public double getMinimumSupportLevel() {
		return minimumSupportLevel;
	}



	public void setMinimumSupportLevel(double minimumSupportLevel) {
		this.minimumSupportLevel = minimumSupportLevel;
	}



	public double getMinimumConfidenceLevel() {
		return minimumConfidenceLevel;
	}

	/*METHOD NOTES
	* 
	* Use this method to validate a Minimum Support Level or Minimum Confidence Level
	* */


	public void setMinimumConfidenceLevel(double minimumConfidenceLevel) {
		this.minimumConfidenceLevel = minimumConfidenceLevel;
	}
	
	/*METHOD NOTES
	 * 
	 * Use this method to validate a Transaction Set with contents
	 * */
	
    public boolean validateTranSet(TransactionSet transactionSet){
    	if(transactionSet.getTransactionSet().size()>0){
    		return true;
    	}else{
    		return false;
    	}
    }

    /*METHOD NOTES
	 * 
	 * Use this method to validate a RuleSet with contents
	 * */
    public boolean validateRuleSet(RuleSet ruleSet){
    	if(ruleSet.getRuleSet().size()>0){
    		System.out.println("Valid RuleSet");
    		return true;
    	}else{
    		System.out.println("Invalid RuleSet");
    		return false;
    	}
    }

	public boolean validateMinLevel(double level) {
		// TODO Auto-generated method stub
		
		if(level >= 0.0 && level <= 1.0){
			return true;
		}else{
		//System.out.println("Invalid minimum support or confidence level");
			return false;
		}
		
	}
	
	/*METHOD NOTES
	 * 
	 * Use this method to find the the powerset of subsets given an ItemSet
	 * */

	public  ArrayList<ItemSet> findSubsets(ItemSet candidates, ArrayList<ItemSet> ps)
	{
		//System.out.println("findSubsets starting...");
		Timer timer = new Timer();
		timer.startTimer();
		ArrayList<ItemSet> powerSet = ps;
		
		if(!powerSet.contains(candidates) )	{
			powerSet.add(candidates);	
		}
		
		
		for(int i = 0; i<candidates.getItems().size(); i++){
			
			ArrayList<Item> subset = new ArrayList<Item>(candidates.getItems());
			subset.remove(i);
			ItemSet itemSubset = new ItemSet(subset);			
			findSubsets(itemSubset, powerSet);
		}
		timer.stopTimer();
		//System.out.println("FindSubsets elapsed time in msec.: " + timer.getTotal() );
		return powerSet;
	}
	
	
	
	/*METHOD NOTES
	 * 
	 * Use this method to generate the association rules given the text files original transaction set, the A Priori generated algorithm
	 * and the minimum confidence level
	 * */
	
	

	public RuleSet generateRuleSet(TransactionSet originalTranSet, TransactionSet aprioriSet,	double minimumConfidenceLevel) {
		
		
		
		
		ArrayList<Rule> allRules = new ArrayList<Rule>();
			for(Transaction transaction: aprioriSet.getTransactionSet()){
				ArrayList<ItemSet> itemList = new ArrayList<ItemSet>();
				
				itemList = findSubsets(transaction.getItemSet(), itemList);//get all subsets
				
				
				
				for(ItemSet subset : itemList){
					
					//System.out.println(subset + "-->"+transaction.getItemSet());
					//System.out.print(originalTranSet.findSupportLevel(transaction.getItemSet()));
					//System.out.print("/" + originalTranSet.findSupportLevel(subset));
					double confidence = (originalTranSet.findSupportLevel(transaction.getItemSet()))/(originalTranSet.findSupportLevel(subset));
					//System.out.println("="+confidence);
					
					
					if(confidence >= minimumConfidenceLevel){
						Rule newRule = new Rule();
						newRule.setAntecedent(subset);
						
						ArrayList<Item> items = new ArrayList<Item>(transaction.getItemSet().getItems());
						ItemSet consequent = new ItemSet(items);
						
						for(int i =0; i<subset.getItems().size(); i++)
						{
							
							consequent.getItems().remove(subset.getItems().get(i));
						}
						
						
						//round to 4 decimal places
						confidence = Math.round(confidence*10000)/10000.0;
						newRule.setConsequent(consequent);
						
						
						
						newRule.setActualConfidenceLevel(confidence);
						newRule.setSupportLevel(originalTranSet.findSupportLevel(transaction.getItemSet()));
						if(newRule.getAntecedent().getItems().size() > 0 && newRule.getConsequent().getItems().size() >0){
							
							allRules.add(newRule);
						}
					}
				
			}
			}
		
			java.util.Date dt = new java.util.Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = sdf.format(dt);
			RuleSet generated = new RuleSet(allRules);
			generated.setDate(currentTime);
		return generated;
	}
	

	/* METHOD NOTES: 
	 * 
	 * Use this method to take the transaction set and the mininumSupportLevel to produce the 1st filtered transaction set
	 * 
	 * */

	public TransactionSet doApriori(TransactionSet tranSet,	double minimumSupportLevel) {
		
		ItemSet uniqueItems = tranSet.getUniqueItems();
		
		 System.out.println("UNIQUE:" +uniqueItems);

		TransactionSet large = new TransactionSet(); // resultant large ItemSets
		TransactionSet iterations = new TransactionSet(); // large ItemSet in
															// each iteration
		TransactionSet candidates = new TransactionSet(); // candidate ItemSet
															// in each iteration

		// Part 1: Generate all candidate single-item sets
		// first iteration (1-item ItemSets)
		for (int i = 0; i < uniqueItems.getItems().size(); i++) {
			Item candidate = uniqueItems.getItems().get(i);
			ItemSet itemSet = new ItemSet();
			itemSet.add(candidate);
			candidates.add(new Transaction(itemSet));

		}
		//System.out.println("candidates: " + candidates);
		
		
		// next iterations
		int k = 2;
		while (candidates.getTransactionSet().size() != 0) {
			//System.out.println("CANDIDATES");
			//System.out.println(candidates);
			// set iterations from candidates (pruning)
			iterations.getTransactionSet().clear();
			// look at each transaction from the candidates
			for (Transaction transaction : candidates.getTransactionSet()) {
				double supportLevel = tranSet.findSupportLevel(transaction.getItemSet());
				//System.out.println("SL: " + supportLevel);
				//System.out.println("support level: " + supportLevel/tranSet.getTransactionSet().size() + " MSL: " + minimumSupportLevel);
				transaction.getItemSet().setSupportLevel(supportLevel/tranSet.getTransactionSet().size());

				if (transaction.getItemSet().getSupportLevel() >= minimumSupportLevel) {
					iterations.add(transaction);
					
					if (transaction.getItemSet().getItems().size() > 1) {
						large.add(transaction);

					}

				}
			}

			// set candidates for next iteration (find supersets of iterations)
			candidates.getTransactionSet().clear();
			//System.out.println("making new candidates k-item: " + k);
			candidates.setTransactionSet(findSubsetsApriori(iterations.getUniqueItems(), k));// get k-item subsets
			//System.out.println("done making candidates");
			k += 1;

		}
		// System.out.println("LARGE:" +large);
		return large;

	}
	

	/*METHOD NOTES
	 * 
	 * this method is used to generate the subsets for the A Priori algorithm
	 */

	private static ArrayList<Transaction> findSubsetsApriori(ItemSet itemSet, int k) {
		//System.out.println("findSubsetsApriori starting... " + itemSet.getItems().size());
		Timer timer = new Timer();
		timer.startTimer();
		ArrayList<Transaction> allSubsets = new ArrayList<Transaction>();
		int subsetCount = (int) Math.pow(2, itemSet.getItems().size());
		// System.out.println("SubsetCount: " + subsetCount);
		for (int i = 0; i < subsetCount; i++) {
			ItemSet subset = new ItemSet();
			for (int bitIndex = 0; bitIndex < itemSet.getItems().size(); bitIndex++) {
				//System.out.println(bitIndex);
				if (getBit(i, bitIndex) == 1) {
					//System.out.println("adding itemset");
					subset.add(itemSet.getItems().get(bitIndex));

				}

			}

			if (subset.getItems().size() == k - 1) {
				allSubsets.add(new Transaction(subset));
				//System.out.println("Added new transaction subset");
			}
		}
		timer.stopTimer();
		//System.out.println("FindSubsetsApriori elapsed time in msec.: " + timer.getTotal() );
		return allSubsets;
	}

	/* METHOD NOTES: 
	 * 
	 * Used to find the item indices for subsets
	 * */

	private static int getBit(int value, int position) {
		//System.out.println("getBit called");
		int bit = value & (int) Math.pow(2, position);
		if (bit > 0) {
			//System.out.println("getBit done");
			return 1;
		} else {
			//System.out.println("getBit done");
			return 0;
		}

	}

	/* METHOD NOTES: 
	 * 
	 * Used to read a transactions set from a file
	 * This is will need to have validation methods later
	 * */

public TransactionSet doAprioriPCY(TransactionSet tranSet,	double minimumSupportLevel) {
		
	
		/*part 1*/
		//set all buckets of hash for 2 items to be 0 ;
	
	//http://user.it.uu.se/~kostis/Teaching/DM-01/Handouts/PCY.pdf
		
		ItemSet uniqueItems = tranSet.getUniqueItems();
		
		TreeMap<ItemSet, Integer> hash = new TreeMap<ItemSet, Integer>();
		hash.put(uniqueItems, 0);
		
		
		 System.out.println("UNIQUE:" +uniqueItems);

		TransactionSet large = new TransactionSet(); // resultant large ItemSets
		TransactionSet iterations = new TransactionSet(); // large ItemSet in
															// each iteration
		TransactionSet candidates = new TransactionSet(); // candidate ItemSet
															// in each iteration

		// Part 1: Generate all candidate single-item sets
		// first iteration (1-item ItemSets)
		for (int i = 0; i < uniqueItems.getItems().size(); i++) {
			Item candidate = uniqueItems.getItems().get(i);
			ItemSet itemSet = new ItemSet();
			itemSet.add(candidate);
			candidates.add(new Transaction(itemSet));

		}
		//System.out.println("candidates: " + candidates);
		
		
		// next iterations
		int k = 2;
		while (candidates.getTransactionSet().size() != 0) {
			//System.out.println("CANDIDATES");
			//System.out.println(candidates);
			// set iterations from candidates (pruning)
			iterations.getTransactionSet().clear();
			// look at each transaction from the candidates
			for (Transaction transaction : candidates.getTransactionSet()) {
				double supportLevel = tranSet.findSupportLevel(transaction.getItemSet());
				//System.out.println("SL: " + supportLevel);
				//System.out.println("support level: " + supportLevel/tranSet.getTransactionSet().size() + " MSL: " + minimumSupportLevel);
				transaction.getItemSet().setSupportLevel(supportLevel/tranSet.getTransactionSet().size());

				if (transaction.getItemSet().getSupportLevel() >= minimumSupportLevel) {
					iterations.add(transaction);
					
					if (transaction.getItemSet().getItems().size() > 1) {
						large.add(transaction);

					}

				}
			}

			// set candidates for next iteration (find supersets of iterations)
			candidates.getTransactionSet().clear();
			//System.out.println("making new candidates k-item: " + k);
			candidates.setTransactionSet(findSubsetsApriori(iterations.getUniqueItems(), k));// get k-item subsets
			//System.out.println("done making candidates");
			k += 1;

		}
		// System.out.println("LARGE:" +large);
		return large;

	}
	
	
	
	
	
	public TransactionSet getTransactionSetFromFile(String fileName) {

		TransactionSet allTransactions = new TransactionSet();
		try {
			ReadFile file = new ReadFile(fileName);
			String[] transactionSetLines = file.openFile();
			
			/*If we need to use a FileReader
			
			FileReader filereader = new FileReader(fileName);
			Scanner fileScanner = new Scanner(filereader);
			
			ArrayList<String> transactionLines = new ArrayList<String>();
			while(fileScanner.hasNextLine()){
				transactionLines.add(fileScanner.nextLine());
				
			}
			
			for(String insideBracket : transactionLines){
				//no leading brace
				validateBraces(insideBracket);
				//no closing brace
			}
			*/
			//System.out.println("transactionSetLines" + transactionSetLines);
			//Get the Date
			
			Pattern pattern = null;
			Matcher matcher = null;
			Vendor vendor = new Vendor(transactionSetLines[0]);
			String startDate = transactionSetLines[1];//for TransactionSet
			String endDate = transactionSetLines[2];//for TransactionSet
			String transactionDate = "2014-04-04 12:00:00";//default date
			
			for (int i = 3; i < transactionSetLines.length; i++) {
				
				//Scanner scanner = new Scanner(transactionSetLines[i]);
				//continue if the length of the line is greater than 0
				if(transactionSetLines[i].length()>0){
				
					
					
					
				
				//check for a only 1 left brace at beginning and look ahead to see there is no other left braces
				
				String bracesRegex =  "\\{{2,}|\\}{2,}|\\{\\s*\\}";//look for multiple left braces, right braces, or no contents
				pattern = Pattern.compile(bracesRegex);
				matcher = pattern.matcher(transactionSetLines[i]);
				if(matcher.find()&& !matcher.group(0).isEmpty()){
					System.out.println("Bad brace format");
					//return an empty transaction set
					return new TransactionSet();
				}else{
					
						String regex =	"(?<=\\{)(.*)(?=\\})";
						pattern = Pattern.compile(regex);
						matcher = pattern.matcher(transactionSetLines[i]);	
						String group = "";
						if(matcher.find()&& !matcher.group(0).isEmpty()){
							//System.out.println(matcher.group(0));
							group = matcher.group(0);
							//get rid of extra whitespace
							group =group.replaceAll(" {1,}", " ");
							
						}else{
							System.out.println("Error: in transaction \""+transactionSetLines[i]+ "\" at transaction " + (i-2) );
							System.out.println("Each transaction requires opening and closing curly braces, as well as containing at least one item.");
						}
						//separate by commas
						String[] candidates = group.split(",");
						// make a new ItemSet to store
						ItemSet itemset = new ItemSet();
						for(int k = 0; k<candidates.length; k++)
						{
							candidates[k] = candidates[k].trim();
							
							//System.out.println("Candidate " + k + ": " + candidates[k]);
							Item nextItem = new Item(candidates[k]);
		
							itemset.add(nextItem);
						}
						
		
						// create a new transaction from the itemSet
						Transaction nextTransaction = new Transaction(itemset);
						nextTransaction.setDate(transactionDate);
		
						// add the finished transaction to the total TransactionSet
						allTransactions.add(nextTransaction);
					}//
				}//end of brace regex
			}//end of > length 0

			allTransactions.add(vendor);
			allTransactions.setStartDate(startDate);
			allTransactions.setEndDate(endDate);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return allTransactions;

	}
	
	
	
	
}
