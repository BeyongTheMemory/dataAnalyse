package com.xugang.apriori;
 
import java.io.BufferedReader ;
import java.io.File ;
import java.io.FileInputStream ;
import java.io.InputStreamReader; 
import java.util.ArrayList ;
import java.util.List ;

public class FileReader 
{
	public static List<List<String>> getDatabase()
	{
		List<List<String>> db = new ArrayList<List<String>>() ;
		
		try
		{
			File file = new File("data.txt") ;
			
			if ( file.isFile() && file.exists())
			{
				InputStreamReader read = new InputStreamReader
							(
							new FileInputStream(file)
									) ;
				
				BufferedReader reader = new BufferedReader( read ) ;
				
				
				String line = null ;
				
				while ( (line = reader.readLine())!= null )
				{
					String [] strToknizer = line.split("	") ;
					
					List<String> tmpLine = new ArrayList<String>() ;
					
					for ( int i = 1 ; i < strToknizer.length ; i++ )
					{
						
						tmpLine.add(strToknizer[i]) ;
						
					}
					db.add(tmpLine) ;
				}
				
				reader.close();
			}
			else
			{
				System.out.println("fail to find target file !");
			}
		}
		catch (Exception e)
		{
			System.out.println("fail in reading file's content ");
			e.printStackTrace();
		}
		
		return db ;
	}

}
