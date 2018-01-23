package com.github.optalgo;

import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the singleton class and it is use to enter information about the ASPprogramm.
 * 
 * @author Cedric Perez Donfack
 */

public class UtilityOptAlgo {
	/** reference to the logging facility */
	private static Logger LOG = LoggerFactory.getLogger(UtilityOptAlgo.class);

	/***
	 * This method computes the min of the max of weight differences 
	 * between all pairs of trucks. Its follows the sorted balance algorithm 
	 * @param nTrucks the number of available trucks 
	 * @param weights the 
	 * @return
	 */
	public static int sortedBalanceOptAlgo(int nTruck, Integer ...weights) {
		// If there aren't boxes.
		if(weights.length<=0) return 0;
		// If there are more trucks than boxes.
		if(weights.length<nTruck) {
			IntStream streamWeights=Stream.of(weights).mapToInt(x->x);
			return streamWeights.max().getAsInt();
		}
		// If there are more boxes than trucks. This is a NP-complete problem.
		List<Integer> boxes=new ArrayList<>();
		/*
		 * Make a linked copy of boxes  
		 */
		for(int i=0;i<weights.length;i++) { 
			boxes.add(weights[i]);
		}
		/*
		 * make a normal sort of the boxes
		 */
		Collections.sort(boxes,(a,b)->b-a);

		/*
		 * creates the List of trucks in the descend order 
		 */
		LinkedList<Truck> trucks=new LinkedList<>();
		for(int i=1;i<=nTruck;i++) {
			trucks.add(new Truck());
		}

		boxes.forEach(weight->{
			Truck truck=trucks.poll();
			truck.addBox(weight);
			int index=insertionTri(truck,trucks);
			if(index<0) {
				trucks.add(truck);
			}else {
				trucks.add(index,truck);
			}
		});

		LOG.info("Tuck's contents");
		LOG.info(trucks.toString());
		int minMax=trucks.getLast().getWeight()-trucks.getFirst().getWeight();
		LOG.info("The min value is "+minMax);
		return minMax;
	}

	/**
	 * 
	 * @param truck 
	 * @param list
	 * @return
	 */
	private static int insertionTri(Truck truck, List<Truck> list) {
		for(int index=0; index<list.size();index++) {
			if(truck.getWeight()<=list.get(index).getWeight()) {
				return index;
			}
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	public static int dcpBalanceOptAlgo(int nTruck, Integer ...weights) {
		// If there aren't boxes.
		if(weights.length<=0) return 0;
		// If there are more trucks than boxes.
		if(weights.length<nTruck) {
			IntStream streamWeights=Stream.of(weights).mapToInt(x->x);
			return streamWeights.max().getAsInt();
		}
		// If there are more boxes than trucks. This is a NP-complete problem.
		List<Integer> boxesW=new LinkedList<>();
		/*
		 * Make a linked copy of boxes  
		 */
		for(int i=0;i<weights.length;i++) { 
			boxesW.add(weights[i]);
		}
		/*
		 * make a normal sort of the boxes
		 */
		Collections.sort(boxesW,(a,b)->b-a);
		@SuppressWarnings("unchecked")
		Deque<Integer> boxes=(Deque<Integer>)boxesW;

		/*
		 * creates the List of trucks in the descend order 
		 */
		Deque<Truck> trucks=new LinkedList<>();
		for(int i=1;i<=nTruck;i++) {
			trucks.add(new Truck());
		}
		/*
		 * The median of the weight of each truck
		 */
		int medianWeight=medianOfTruckWeight(nTruck, boxes);
		/*
		 * list of truck, which list of box is satisfied
		 */
		Deque<Truck> trucksOpt=new LinkedList<>();
		/*
		 * remove all weights, which are greater than the median.  
		 */
		Integer hightBoxWeight=null;
		while((hightBoxWeight=boxes.poll())!=null) {
			if(hightBoxWeight>=medianWeight) {
				Truck truck=trucks.poll();
				truck.pushBox(hightBoxWeight);
				trucksOpt.add(truck);
				nTruck--;
			}else {
				boxes.push(hightBoxWeight);
				break;
			}
		}
		//		/*
		//		 * The median of the weight of the of each truck
		//		 */
		medianWeight=medianOfTruckWeight(nTruck, boxes);


		fullTruck(medianWeight, trucks, boxes);
		trucksOpt.addAll(trucks);
		Collections.sort((List<Truck>)trucksOpt,(a,b)->a.getWeight()-b.getWeight());
		int minMax=trucksOpt.getLast().getWeight()-trucksOpt.getFirst().getWeight();
		LOG.info("Tuck's contents");
		LOG.info(trucksOpt.toString());
		LOG.info("The min value is "+minMax);

		return minMax;
	}

	/**
	 * 
	 * @param median
	 * @param truck
	 * @param weights
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static int fullTruck(int median, Deque<Truck> trucks, Collection<Integer> weightsCol) {

		Deque<Integer> weights=(Deque<Integer>)weightsCol;
		int optDiff=median;
		Truck truckElt=null;
		List<Truck> trucksList=(List<Truck>)trucks;
		Deque<Integer> optTruck=new ArrayDeque<>();
		int lastIndex=trucksList.size()-1;
		for(int index=0;index<lastIndex;index++) {
			Integer baseElt=weights.poll();	
			truckElt=trucksList.get(index);
			addTruck(baseElt,truckElt,median);
			searchBox(median, truckElt, new ArrayDeque<Integer>(weights), optTruck, optDiff);

			if(optTruck.size()>0) {
				truckElt.clearBox();
				truckElt.addAllBox(optTruck);
			}
			truckElt.getAllBox().forEach(weights::remove);
			median=medianOfTruckWeight(lastIndex-index, weights);
		}
		trucksList.get(lastIndex).addAllBox(weights);

		return 0;
	}

	/**
	 * 
	 * @param median
	 * @param truck
	 * @param weights
	 * @param optTruck
	 * @param optDiff
	 * @return
	 */
	public static int searchBox(int median, Truck truck, Deque<Integer> weights, Deque<Integer> optTruck, int optDiff) {

		int diff=median-truck.getWeight();
		Integer otherElt=null;
		//		int baseElt=weights.poll();
		//		truck.pushBox(baseElt);
		Deque<Integer> backUpTruckBox=new ArrayDeque<>(truck.getAllBox());

		while( (otherElt=weights.poll())!=null ) {

			if(addTruck(otherElt,truck,median)) {

				diff=median-truck.getWeight();
				if(diff==0) {
					optTruck.clear();
					optTruck.addAll(truck.getAllBox());
					return 0;
				}

				if(weights.size()>0) {
					Deque<Integer> weightsCopy=new LinkedList<>(weights);
					diff=searchBox(median, truck, weightsCopy,optTruck, optDiff);
				}
				if(diff==0) return 0;

				if(diff<=optDiff) {
					if(diff<optDiff) {
						optDiff=diff;
						optTruck.clear();
						optTruck.addAll(truck.getAllBox());
					}else if(optTruck.size()>truck.getAllBox().size()) {
						optTruck.clear();
						optTruck.addAll(truck.getAllBox());
					}
				}

				truck.clearBox();
				truck.addAllBox(backUpTruckBox);
			}
		}



		return diff;
	}



	/**
	 * add a box to a truck if the median isn't reached.
	 * @param weight weight of the box
	 * @param truck a the truck
	 * @param median median of each truck's weight.
	 * @return true a box is added and false otherwise.
	 */
	public static boolean addTruck(int weight, Truck truck, int median) {

		int diff=median-(truck.getWeight()+weight);
		if(diff>=0) {
			truck.pushBox(weight);
			return true;
		}

		//		truck.pollBox();
		return false;
	}

	/**
	 *  
	 * @param nTruck list of unused truck
	 * @param boxes list of unused weight 
	 * @return median for each truck's median.
	 */
	public static int medianOfTruckWeight(int nTruck,Collection<Integer> boxes) {

		int sum=boxes.parallelStream().mapToInt(x->x).sum();

		int quotien=sum/nTruck;

		int meanInt=sum%nTruck==0?quotien:quotien+1 ;

		return meanInt ;
	}


	public static void main(String ...args) {
		//		sortedBalanceOptAlgo(3,new int[] {1,2,3,4});
		//		sortedBalanceOptAlgo(3,new int[] {2,3,5});
		//		sortedBalanceOptAlgo(3,new int[] {2, 5, 5, 8, 10, 12, 18, 19, 20});
		Truck.initId();
		sortedBalanceOptAlgo(3,new Integer[] {9, 8, 10, 11,12, 18, 19, 20});
		
//		sortedBalanceOptAlgo(3,new int[] {2, 5, 9, 8, 10, 11,12, 18, 19, 20});
//		Truck.initId();	
		//				sortedBalanceOptAlgo(3,new int[] {2, 3, 4, 11, 12, 13, 18, 19, 20, 22, 23, 23, 23, 23, 23, 32, 32, 34, 34, 41, 41, 43, 53, 56, 56, 56, 67});

		LOG.info( "############################################## The dcp algorithmen ##############################" );

		//		dcpBalanceOptAlgo(3,new int[] {1,2,3,4});
		//		Truck.initId();
		//		dcpBalanceOptAlgo(3,new int[] {2,3,5});
//		Truck.initId();
		//		dcpBalanceOptAlgo(3,new int[] {2, 5, 5, 8, 10, 12, 18, 19, 20});
		//		Truck.initId();
//		dcpBalanceOptAlgo(3,new int[] {2, 5, 9, 8, 10, 11,12, 18, 19, 20});
		Truck.initId();		
		dcpBalanceOptAlgo(3,new Integer[] {9, 8, 10, 11,12, 18, 19, 20}); //[19,18]=37; [20,9,8]=37; [10,11,12]=33
		LOG.debug(Paths.get("./").toAbsolutePath().toString());
//		Truck.initId();
//		dcpBalanceOptAlgo(3,new int[] {2, 3, 4, 11, 12, 13, 18, 19, 20, 22, 23, 23, 23, 23, 23, 32, 32, 34, 34, 41, 41, 43, 53, 56, 56, 56, 67});

	}
}
