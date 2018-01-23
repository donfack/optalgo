package com.github.optalgo;

/**
 * This class represents a box with its informations
 * @author Cedric Perez Donfack
 *
 */
public class Box {
	
	

	/**
	 * The weight of the box.
	 */
	private int weight;
	
	
	public Box(int weight) {
		
		setWeight(weight);
	}
	
	public int getWeight() {
		return weight;
	}

	private void setWeight(int weight) {
		if(weight>=0) 
			this.weight=weight;
		else
			throw new IllegalArgumentException("The weight must greater than zero");
	}
}
