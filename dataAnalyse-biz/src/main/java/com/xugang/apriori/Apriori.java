package com.xugang.apriori;

 
import java.util.Iterator;
import java.util.List ;
import java.util.Map ;
import java.util.ArrayList ;
import java.util.HashMap; 
import java.util.Map.Entry;




public class Apriori {

	static private boolean endTag = false ;
	static private List<List<String>> cItemset ;
	static private List<List<String>> ckItemset ;
	static private Map<List<String>, Integer> lItemset ;
	static private Map<List<String>,Integer>  lkItemset ;
	
	static List<List<String>> record = new ArrayList<List<String>> () ;
	
	final static double MIN_SUPPORT =2 ;
	final static double MIN_CONF = 0.8 ;
	
	static Map<List<String>, Double > confItemset = new HashMap<List<String>,Double >() ;
	
	
	public static  List<List<String>> getDataSet ()
	{
		return FileReader.getDatabase();
	}
	
	
	public static List<List<String>> getFirstCandidate ()
	{
		
		List<List<String>> cItemset = new ArrayList<List<String>> () ;
		List<String> tempLine = new ArrayList<String>() ;
		
		for( int i = 0 ; i < record.size() ; i++ )
		{
			
			for (int j = 0 ; j < record.get(i).size(); j++)
			{
				if(tempLine.contains(record.get(i).get(j))) ;
				else
				{
					tempLine.add(record.get(i).get(j)) ;
					 
				}
			}
			
		}
		
		for ( int i = 0 ; i < tempLine.size() ;i++)
		{
			List<String> str = new ArrayList<String>() ;
			str.add(tempLine.get(i));
			
			cItemset.add(str) ;
			 
		}
		
		return cItemset ;
	}
	
	static Map<List<String>,Integer> getSupportedItemset( List<List<String>> cItemset )
	{
		Map<List<String>,Integer> supportedItemset = new HashMap<List<String>,Integer> () ;
		
		boolean end = true ;
		
		for( int i = 0 ; i < cItemset.size(); i++ )
		{
			int count = countFrequent ( cItemset.get(i)) ;
			
			if( count >= MIN_SUPPORT )
			{
			supportedItemset.put(cItemset.get(i), count) ;
			end = false ;
		
			}
		}
		
		endTag = end ;
		
		
		//System.out.println("value of the endTag here !!!"+endTag);
		
		return supportedItemset  ;
	}
	
	static int countFrequent ( List<String> list)
	{
		
		int count = 0 ;
		
		for ( int i = 1 ; i < record.size() ; i++ )
		{
			boolean curRecordLineNotHave = false ;
			
			for ( int k = 0 ; k < list.size(); k++)
			{
				if(!record.get(i).contains(list.get(k)))
				{
					curRecordLineNotHave = true ;
					break ;
				}
			}
			
			if(curRecordLineNotHave == false )
			{
				count++ ;
			}
		}
		
		return count ;
	}
	
	
	/**
	 * method following is the getNextCandidata usually can be known as 
	 * get Ck from Lk-1 
	 * */
	private static List<List<String>> getNextCandidate ( Map<List<String>,Integer> lItemset )
	{
		List<List<String>> nextItemset = new ArrayList<List<String>>() ;
		
		List<List<String>> preItemset = getLItemset(lItemset ) ;
		
		int count = 0 ;
		
		for ( int i = 0 ; i < preItemset.size() ; i++ )
		{
			List<String> tempLine = new ArrayList<String> () ;
			tempLine.addAll(preItemset.get(i)) ;
			
			
		//	System.out.println( "step 1 : tempLine "+tempLine);
			
			
			for( int j = i+1 ; j < preItemset.size(); j++)
			{
				 if( preItemset.get(i).size() == preItemset.get(j).size())
				 {
			//		 System.out.println("step 2 : preItemset "+i+ " preItemset" + j+ "size match  size= "+ preItemset.get(i).size());
					 
					 if( 1 == differElemNum(preItemset.get(i),preItemset.get(j)))
					 {
						 int index = getDifferIndex ( preItemset.get(i), preItemset.get(j)) ;
						 
			//			 System.out.println("step 3 : differ index of "+preItemset.get(i)+" and "+ preItemset.get(j)+ " is  "+index ) ;
						 
						 tempLine.add(preItemset.get(j).get(index)) ;
						 
				//		 System.out.println("step 4 : after combine :" +tempLine) ;
						 
						 if( isSubSets ( tempLine, preItemset))
						 {
				//			 System.out.println("step 5 : "+tempLine +" is the sub set of preItemset ");
							
							 List<String> aLine = new ArrayList() ;
							 
							 for(int m = 0 ; m < tempLine.size() ;m++)
							 {
								 aLine.add(tempLine.get(m));
							 }
							 
							 if( nextItemSetNotHave( aLine, nextItemset ))
								 nextItemset.add(aLine) ;
							 
						 }
					 }
				 }//outer if 
				 
				 tempLine.remove(tempLine.size()-1 ) ;
			}//for j 
		}
		
		/*System.out.println("in sub method ================");
		for(int i = 0 ;i < nextItemset.size() ; i++ )
		{
			 
				System.out.println(nextItemset.get(i));
			 
			 
		}*/
		
		 return nextItemset ;
		
		
		 
	}
	
	
	private static boolean nextItemSetNotHave( List<String> aLine , List<List<String>> nextItemset )
	{
		boolean notHave = true ;
		
		for( int i = 0 ; i < nextItemset.size(); i++ )
		{
			if(aLine.equals(nextItemset.get(i)))
			{
				notHave = false ;
			}
		}
		
		return notHave ;
	}
	
	
	private static int getDifferIndex ( List<String> list1 , List<String> list2)
	{
		int index = -1 ;
		
		for ( int i = 0 ; i < list1.size() ; i++ )
		{
			for( int j = 0 ; j < list1.size(); j++ )
			{
				if ( !list1.get(i).equals(list2.get(j)))
				{
					index = j ;
				}
			}
			
			if ( index != -1 )
				break ;
		}
		
		return index ;
	}
	
	private static int differElemNum ( List<String> list1, List<String>list2 )
	{
		int count = 0 ;
		
		boolean flag ;
		
		for( int i = 0 ; i < list1.size() ; i++ )
		{
			flag = true ;
			
			for(int j = 0 ; j < list1.size(); j++ )
			{
				if(list1.get(i).equals(list2.get(j)))
				{
					 flag = false ;
					break;
				}
			}
			
			if( flag == true )
			{
				count++ ;
			}
		}
		
		return count ;
	}
	 
	
	
	/**
	 * method following is used to justice whether 
	 * @param tempList all subsets except itself is the subsets of 
	 * @param lItemset
	 * 
	 * @return boolean true : all subsets of tempList are all in
	 * lItemset's set
	 * */
	
	private static boolean isSubSets ( List<String> tempList , List<List<String>> lItemset)
	{
		
		boolean flag = false ;
		
		for ( int i = 0 ; i < tempList.size() ; i++ )
		{
			List<String> testLine = new ArrayList() ;
			
			for (int j = 0 ; j < tempList.size(); j++ )
			{
				if (i!= j )
				{
					testLine.add(tempList.get(j)) ;
				}
			}
			
			
			for ( int k = 0 ; k < lItemset.size() ; k++ )
			{
				if ( testLine.equals(lItemset.get(k)))
				{
					flag = true ;
					break ;
				}
			}
			
			
			if ( flag == false )
			{
				/**
				 * 这种情况对应的是，其中对于tempList 中有一个 ，即存在一个 对应的子集，
				 * 它将会在 Ck 中是 作为一条记录 的 子集存在的， 但是，在它的 Lk-1 也就是 所谓的频繁相机中
				 * 是不存在的，，那么他所在的 Ck 一定也不会是 可以 过滤为 Lk 的频繁相机，
				 * 所以这样的话，他是不满足提议的， 所以 只要是 flag = FALSE，
				 * 则说明 他没有进入到上面的if 判断 中区，也就是lItemset中的每一条记录 都没有和他是相等的，
				 * 所以这种情况下无须继续向下比较了，只需要 返回false 即可。
				 * */
				return false ;
			}
		}
		
		
		return flag ; //return true ;
		
	}
	
	
	
	private static List<List<String>> getLItemset ( Map<List<String>, Integer> lItemset )
	{
		List<List<String>> itemset = new ArrayList<List<String>> () ;
		
		Iterator<Entry<List<String>, Integer>> iterator = lItemset.entrySet().iterator();
		Entry<List<String>, Integer> entry ;
		
		
		while ( iterator.hasNext() )
		{
			entry = iterator.next();
			List<String> key = entry.getKey() ;
			
			itemset.add(key) ;
			
			 
		}
		return itemset ;
	}
	
	public static void main ( String [] args ) throws Exception 
	{
		record =getDataSet() ;
		
		
	 
		 cItemset = getFirstCandidate() ;
		
		lItemset = getSupportedItemset( cItemset ) ;
		
		 printfLKitemset ( lItemset) ;
		
		
		/*System.out.println("==========cItemset show ====================");
		 for ( int i = 0 ; i < cItemset.size(); i++)
		{
			for ( int j = 0 ; j < cItemset.get(i).size(); j++)
			{
				System.out.print("item : "+ cItemset.get(i).get(j));
			}
			System.out.println() ;
		} 
		 
		 System.out.println("-------lItemset show ---------");
		
		 Iterator<Entry<List<String>, Integer>> iterator = lItemset.entrySet().iterator();
		 
		 Entry<List<String>,Integer> entry ;
		 
		 
		 while ( iterator.hasNext())
		 {
			 entry = iterator.next();
			 List<String> key = entry.getKey();
			 Integer value = entry.getValue();
			 
			 System.out.println("the key :");
			 
			 for( int i = 0 ; i < key.size(); i++)
			 {
				 System.out.println(key.get(i));
			 }
			 
			 System.out.println("the value :"+ value.intValue()) ;
			 
		 }
		  
		 
		 
		 */
		 
		 
		  while ( endTag != true )
		 {
			 ckItemset = getNextCandidate(lItemset ) ;
			 lkItemset = getSupportedItemset ( ckItemset ) ;
			 
			 if(lkItemset.size() != 0 )
				 printfLKitemset ( lkItemset) ;
			 
			 cItemset = ckItemset ;
			 lItemset = lkItemset ;
		 }  
		 
		  System.out.println("finish ") ;
		 
		 /*System.out.println("===============then the lItemset's item show (map without value) =================");
		 List<List<String>> preItemset = getLItemset(lItemset);
		 
		 for ( int i = 0 ; i < preItemset.size() ; i++)
		 {
			 for(int j = 0 ; j < preItemset.get(i).size(); j++)
				 System.out.println(preItemset.get(i).get(j));
		 }
		 
		 
		 System.out.println("=========getNextCandidate : from L(k-1) -> C(k)=================");
		 
		 ckItemset = getNextCandidate( lItemset ) ;
		 
		 
		 for ( int i = 0 ; i < ckItemset.size () ; i++ )
		 {
			 for( int j = 0 ; j < ckItemset.get(i).size(); j++ )
				 System.out.print(ckItemset.get(i).get(j)+"  ");
			 
			 System.out.println();
		 }

		
		 
		 lkItemset = getSupportedItemset( ckItemset ) ;
		 
		 printfLKitemset ( lkItemset) ;
		  */
	}
	
	 private static void printfLKitemset ( Map<List<String> , Integer> lkItemset )
	 {
		 Iterator<Entry<List<String>,Integer>> iterator = lkItemset.entrySet().iterator();
		 
		 Entry<List<String>,Integer> entry ;
		 
		 while ( iterator.hasNext() )
		 {
			 entry = iterator.next() ;
			 
			 List<String> key = entry.getKey() ;
			 Integer value = entry.getValue() ;
			 
			 System.out.println("the key : ");
			 
			 for ( int i = 0 ; i < key.size() ; i++ )
			 {
				 System.out.print(key.get(i));
				 System.out.print("  "); 
			 }
			 
			 System.out.println("the value : "+ value.intValue());
			 
		 }
	 }
	
	
	
}
