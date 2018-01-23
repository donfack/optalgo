package com.github.optalgo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.optalgo.UtilityOptAlgo.*;


/**
 * This class is a test process, which check the conformity between a answer set
 * programming (asp) and the given mindmap file. This means: Given a set of
 * data, is the output of mindmap the same as the output of the asp.
 * 
 * @author Cedric Perez Donfack
 *
 */
@RunWith(Parameterized.class)
public class SortedBalanceOptAlgoTest {
	/** reference to the logging facility */
	private static Logger LOG = LoggerFactory.getLogger(SortedBalanceOptAlgoTest.class);
//	/**
//	 * We not want more than one instance of this class, therefore it's a
//	 * singleton and to access its methods one has to use this attribute.
//	 */
////	private static SortedBalanceOptAlgoTest instanceTest;
	
	
	/**
	 * the path where the data cases is located
	 */
	private static Path pathFileCase=Paths.get("target/test-classes/com/github/optalgo/datacases.txt");
	
	/**
	 * List of information about the boxes.
	 */
	private static Deque<Deque<Integer>> boxes=new LinkedList<>();
	/**
	 * List of information about the number of trucks
	 */
	private static Deque<Integer> nTrucks=new LinkedList<>();
	
	/**
	 * Number of trucks of the current test
	 */
	private static Integer nTruck=0; 
	
	/**
	 * the current boxes which will be charged.
	 */
	private static Deque<Integer> boxesW=null; 
	
	/**
	 * This is parameter which has always the value true. It will use to check
	 * whether the real Value and the desired value are the same.
	 */
	@Parameter(0)
	public int shallValue=0;
	
	/**
	 * This constructor signature has to be stayed unaltered.
	 */
	public SortedBalanceOptAlgoTest(){}

	/**
	 * This method will be called only one time. Since there is no constructor's
	 * method, it plays the constructor role.
	 * 
	 * @return the suite of tests being tested
	 * @throws IOException 
	 */
	public static List<Integer> init() throws IOException {
		
		List<Integer> shallValue=Files.lines(pathFileCase).filter(predicate->!predicate.startsWith("%"))
				.map(mapper->mapper.replaceAll(" ", ""))
				.map(mapper->{
			String[] datas=mapper.split(";");
			nTrucks.add(Integer.parseInt(datas[0]));
			String weights=datas[1];
			Deque<Integer> dataCasei= Arrays.asList(weights.split(","))
					.stream()
					.map(arg->Integer.parseInt(arg))
					.collect(LinkedList::new, LinkedList::add,LinkedList::addAll);
			boxes.add(dataCasei);
			return Integer.parseInt(datas[2]);
		}).collect(LinkedList::new, LinkedList::add,LinkedList::addAll);
		
		return shallValue;
	}

	/**
	 * This method allows a automatic test of all the possible test cases.
	 * 
	 * @return a collection of boolean whose values always have to be true,
	 *         because the result must always true.
	 */
	@Parameters
	public static Collection<Object> data() {
		List<Object> obj = new ArrayList<>();
		try {
			List<Integer> shallValue=init();
			for (Integer val:shallValue) {
				obj.add(val);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return obj;
	}

	/**
	 * This method will be called each time, before a test case begin.
	 * 
	 */
	@Before
	public void initTestCase() {
		try {
			nTruck=nTrucks.poll();
			boxesW=boxes.poll();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * This is the compute min of max
	 */
	private int isValue=0; 
	/**
	 * Convert a collection of Integer to a array of integer the same values.
	 * @param collection collections of integer
	 * @return array of integer.
	 */
	@SuppressWarnings("unused")
	private Integer[] convertListToArray(Collection<Integer> collection) {
		if(collection==null || collection.size()==0) return new Integer[0];
		
		Integer[] collect=new Integer[collection.size()];
		int index=0;
		for(Integer weight:collection) {
			collect[index]=weight;
			index++;
		}
			
		return collect;
	}
	
	/**
	 * This method not only make the test itself but it also implements how the
	 * result of the test process will be showed to the use. All test case
	 * methods calls it.
	 */
	@Test
	public void testApp() {
		isValue=sortedBalanceOptAlgo(nTruck,convertListToArray(boxesW));
		String messageTest=String.format("SortedBalanceOptAlgo: nTruck= %d; Boxes=%s ::",nTruck,boxesW.toString());
		Assert.assertEquals(messageTest, shallValue,isValue );
	}

	/**
	 * This method will be called each time, after a test case begin.
	 * 
	 */
	@After
	public void clearTestCase() {
		boxesW=null;
		nTruck=0;
	}

	 /**
	 * This method will be called only one time. Since there is no
	 * deconstructor's method, it plays the deconstructor role.
	 *
	 * @return the suite of tests being tested
	 */
	 @AfterClass
	 public static void clear() {
		 nTrucks.clear();
		 nTrucks=null;
		 boxes.clear();
		 boxes=null;
	 }
}