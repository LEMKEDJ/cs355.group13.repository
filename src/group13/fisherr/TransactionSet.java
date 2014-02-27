package group13.fisherr;

import java.util.ArrayList;

public class TransactionSet {

	public ArrayList<Transaction> transactionSet;

	public TransactionSet(ArrayList<Transaction> transactionSet) {
		this.transactionSet = transactionSet;

	}

	public TransactionSet() {
		// TODO Auto-generated constructor stub
		this.transactionSet = new ArrayList<Transaction>();
	}

	public ArrayList<Transaction> getTransactionSet() {
		return transactionSet;
	}

	public void setTransactionSet(ArrayList<Transaction> transactionSet) {
		this.transactionSet = transactionSet;
	}
	

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		String str = "TransactionSet: \n";
		System.out.println(transactionSet.size());
		for(int i = 0; i < transactionSet.size();i++){
			str+=transactionSet.get(i).toString();
		}
		return str;
	}
	
	public ItemSet getUniqueItems(){
		ItemSet itemSet = new ItemSet();
		//look in the current transaction set and search for unique items in each transaction
		for(int i = 0; i < this.transactionSet.size();i++){
			
			for(int j = 0; j < this.transactionSet.get(i).getItemSet().getItems().size();j++){
				//get the current item
				Item currentItem = this.transactionSet.get(i).getItemSet().getItems().get(j);
				
				//if you find an item that is unique to the list, then add it, else don't
				//override the equalsMethod
				if(!itemSet.containsItem(currentItem)){
					//System.out.println("currentItem: " + currentItem);
					itemSet.add(currentItem);
				}
			}
			
		}
		return itemSet;
	}

	

	public void add(Transaction transaction) {
		// TODO Auto-generated method stub
		this.transactionSet.add(transaction);
		
	}
	
	public int findSupportLevel(ItemSet itemSet){
		int supportLevel = 0;
		//System.out.println("find SupportLevel started");
		//System.out.println("TS:\n" + transactionSet);
		for(int i = 0; i < this.transactionSet.size();i++){
			for(int j = 0; j < itemSet.getItems().size();j++){
				//System.out.println("DOES: "+transactionSet.get(i).getItemSet() + "=="  +itemSet);
				//if the transaction's itemset contains an item found in the itemSet in question
				//increase the 
				if(transactionSet.get(i).getItemSet().getItems().contains(itemSet.getItems().get(j))){
					supportLevel+=1;
				
			}
			
			}
		}
		
		return supportLevel;
	}
	
	
}
