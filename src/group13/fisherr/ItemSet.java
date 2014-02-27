package group13.fisherr;

import java.util.ArrayList;

public class ItemSet {
	
	public ArrayList<Item> items;
	
	
	public double support = 0;
	
	public ItemSet(ArrayList<Item>items){
		this.items = items;
		this.support = 0;
	}
	
	public ItemSet(){
		this.items = new ArrayList<Item>();
	}
	

	public ArrayList<Item> getItems() {
		return items;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

	public double getSupport() {
		return support;
	}
	
	public void setSupport(double supportLevel) {
		this.support = supportLevel;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str = "{";
		for (int i = 0; i < this.items.size();i++){
			str+= ""+items.get(i)+",";
		}
		
		return str +"}-" + support;
	}

	
	public boolean containsItem(Item currentItem) {
		// TODO Auto-generated method stub
		
		if(this.items.contains(currentItem)){
			return true;
		}else{
			return false;
		}
	}

	public void add(Item currentItem) {
		// TODO Auto-generated method stub
		this.items.add(currentItem);
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		ItemSet itemSet = (ItemSet)obj;
		
		//Only proceed if both itemSets are of equal sizes
		if(this.items.size()==itemSet.items.size()){
			
		
				//compare each item in each itemSet to each other
				for(int j = 0; j<itemSet.items.size();j++){
					if(!this.items.get(j).equals(itemSet.items.get(j))){
						System.out.println("UNEQUAL ITEMSETS");
						return false;
						
					}
				}
			
			System.out.println("EQUAL ITEMSETS");
			return true;
		}else{
			System.out.println("UNEQUAL ITEMSETS");
			return false;
		}
		
		
		
	}
	
	
	

}