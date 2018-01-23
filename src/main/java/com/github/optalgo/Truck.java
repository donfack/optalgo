package com.github.optalgo;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 
 * @author Cedric Perez Donfack
 *
 */
public class Truck{
	/**
	 * number of instances objects of this class
	 */
	private static int nbTruck;
	/**
	 * the identity of the truck
	 */
	private int id=0;
	{
		nbTruck++;
		id=nbTruck;
	}
	
	/**
	 * True if it is complete and false otherwise.
	 */
	private boolean full;
	
	
	
	public static void initId() {
		nbTruck=0;
	}
	
	/**
	 * The weight of a truck
	 */
	private int weight;
	
	/**
	 * List of boxes
	 */
	private Deque<Integer> boxes=new ArrayDeque<>(); 

	/**
	 * 
	 * @return a weight of a truck
	 */
	public int getWeight() {
		return weight;
	}
	
	/**
	 * clear the weight to a truck
	 * @param boxW the weight of a box 
	 */
	public void clearBox() {
		boxes.clear();
		weight=0;
	}
	
	/**
	 * copy the weight to a truck
	 * @param boxW the weight of a box 
	 */
	public Deque<Integer> getAllBox() {
		Deque<Integer> copyBoxes=new ArrayDeque<>();
		copyBoxes.addAll(boxes);
		return copyBoxes;
	}
	
	/**
	 * add the weight to a truck
	 * @param boxW the weight of a box 
	 */
	public void addBox(Integer boxW) {
		boxes.add(boxW);
		weight+=boxW;
	}
	
	/**
	 * add all the weight to a truck
	 * @param queueBox queue of boxes 
	 */
	public void addAllBox(Deque<Integer> queueBox) {
		for(Integer elt:queueBox) {
			addBox(elt);
		}
	}
	
	/**
	 * push the weight to a truck
	 * @param boxW the weight of a box 
	 */
	public void pushBox(Integer boxW) {
		boxes.push(boxW);
		weight+=boxW;
	}
	
	/**
	 * remove the weight to a truck
	 * @param boxW the weight of a box 
	 */
	public void remove(Integer boxW) {
		boxes.remove(boxW);
		weight-=boxW;
	}
	
	/**
	 * poll the weight to a truck
	 * @param boxW the weight of a box
	 * @return the weight 
	 */
	public int pollBox() {
		
		int obj=boxes.poll()!=0?boxes.poll():0;
		weight-=obj;
		return obj;
	}
	
	/**
	 * 
	 * @return the fully status
	 */
	public boolean isFull() {
		return full;
	}
	
	/**
	 * @param full the value about the fully status
	 */
	public void setFull(boolean full) {
		this.full=full;
	}
	
	public String toString() {
		return String.format("Truck%d: %s= %d.", id,boxes.toString(),weight);
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Truck)) return false;
		Truck obj=(Truck)other;
		return this.id==obj.id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
}
