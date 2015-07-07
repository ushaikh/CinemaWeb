	package com.cinemawebservice;

import java.sql.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.Iterator;


public class Backup
{
  //public static void main(String args[])
   public static void parseJSON()
  {
    Connection c = null;
    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:cinemafinal1.db");
      
      
    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
   
    System.out.println("Opened database successfully");
    createTables(c);
    loadJSONData(c);
    viewJSONData(c);
    viewArgumentData(c);
  }
  
  
  public static void createTables(Connection c)
  {

    Statement stmt = null;
    try 
    {
     
      stmt = c.createStatement();
      
      String sql = "CREATE TABLE IF NOT EXISTS JSON_INFO " +
                   "(JSON_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                   " ROOT_LOCATION     TEXT    NOT NULL, " + 
                   " NAME_PATTERN  TEXT     NOT NULL, " + 
                   " METADATA   TEXT); "; 
      stmt.executeUpdate(sql);
      
      sql = "CREATE TABLE IF NOT EXISTS ARGUMENT " +
              "(ARGUMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + "ARGUMENT_NAME TEXT NOT NULL," +
    		  "DEFAULT_VALUE INTEGER,"+
    		  "JSON_ID INTEGER,"+
              "FOREIGN KEY(JSON_ID) REFERENCES JSON_INFO(JSON_ID)) ;"; 
      stmt.executeUpdate(sql);
      
      sql = "CREATE TABLE IF NOT EXISTS ARGUMENT_VALUE " +
              "(ARGUMENT_VAL REAL NOT NULL," +
    		  "ARGUMENT_ID INTEGER,"+
              "FOREIGN KEY(ARGUMENT_ID) REFERENCES ARGUMENT(ARGUMENT_ID))"; 
      stmt.executeUpdate(sql);
      
      stmt.close();

    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
    System.out.println("Table created successfully");
  }
  
  

  
  public static void viewJSONData(Connection c )
  {

	  Statement stmt = null;
	    try {


	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM JSON_INFO;" );
	      while ( rs.next() ) {
	         int id = rs.getInt("json_id");
	         String  name = rs.getString("name_pattern");
	         String  filename = rs.getString("root_location");
		       
	         String  metadata = rs.getString("metadata");

	         System.out.println( "JSON_ID = " + id );
	         System.out.println( "NAME_PATTERN = " + name );
	         System.out.println( "METADATA = " + metadata );
	         System.out.println( "FILENAME = " + filename );

	         System.out.println();
	      }
	      rs.close();
	      stmt.close();

	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Select Operation done successfully");
  }
  
  public static void viewArgumentData(Connection c )
  {

	  Statement stmt = null;
	    try {


	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM ARGUMENT;" );
	      while ( rs.next() ) 
	      {
	         int id = rs.getInt("json_id");
	         int argument_id = rs.getInt("argument_id");
	         String  name = rs.getString("argument_name");
	         int defaut_value = rs.getInt("default_value");
	
	         System.out.println( "JSON_ID = " + id );
	         System.out.println( "ARGUMENTID = " + argument_id );
	         System.out.println( "ARGUMENT NAME = " + name);
	         System.out.println( "DEFAULT = " + defaut_value );

	         System.out.println();
	      }
	      rs.close();
	      stmt.close();

	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Select Operation done successfully");
  }
  
  
  public static ArrayList<String> getHierarchy(Connection c )
  {

	  Statement stmt = null;
	  ArrayList<String> hierarchy = new ArrayList<String>();
	  try {

		  stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM JSON_INFO where JSON_ID = 1;" );
	      while ( rs.next() ) 
	      {
	         String  name = rs.getString("name_pattern"); 
	         parseNamePattern(name, hierarchy);
	      }
	      rs.close();
	      stmt.close();

	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	  return hierarchy;

  }
  
  
  public static void loadJSONData(Connection c)
  {
	  	InputStream input = null;
 
		Properties prop = new Properties();

		try 
		{
	 
			input = new FileInputStream("/Users/ushaikh/Documents/uzmawork/CinemaWeb/appProperties.properties");
			// load a properties file
			prop.load(input);
			// get the property value and print it out
			String fileName = prop.getProperty("cinemaRootDirectory");
			JSONParser parser = new JSONParser();
		   try 
		   {
		         	Object obj = parser.parse(new FileReader(fileName+"/info.json"));

					Statement stmt = c.createStatement();
		            JSONObject jsonObject = (JSONObject) obj; 
		            JSONObject arguments = (JSONObject) jsonObject.get("arguments");
		            String namePattern = (String) jsonObject.get("name_pattern");
		            JSONObject metadata = (JSONObject) jsonObject.get("metadata");

		            
		            String sql = "INSERT INTO JSON_INFO  VALUES (1, \""+fileName+"\",\"" +namePattern+"\",\""+metadata.get("type").toString()+"\");"; 
		            System.out.println(sql);
		            stmt.executeUpdate(sql);
		            
		            for(Iterator iterator = arguments.keySet().iterator(); iterator.hasNext();) {
		                String key = (String) iterator.next();
		                JSONObject argumentValue = (JSONObject)arguments.get(key);
		                System.out.println(key+" values   " +argumentValue.toString());
		                
		                sql = "INSERT INTO ARGUMENT VALUES ((SELECT MAX(argument_id) FROM argument) + 1"+",\"" +argumentValue.get("label") +"\","+argumentValue.get("default")+", (SELECT MIN(json_id) FROM JSON_INFO));"; 
		                System.out.println(sql);
		                stmt.executeUpdate(sql);
		                
		                JSONArray argValueList = (JSONArray) argumentValue.get("values");
		                for(int i=0; i< argValueList.size(); i++)
		                {
		                	Object val = argValueList.get(i);
		                	sql = "INSERT INTO ARGUMENT_VALUE VALUES ("+val.toString()+",(SELECT MAX(argument_id) FROM argument));"; 
				            System.out.println(sql);
				            stmt.executeUpdate(sql);
		                }
		                
		            }
		            stmt.close();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			
		} 
		catch (IOException ex) 
		{
			ex.printStackTrace();
		} 
		finally 
		{
			if (input != null) 
			{
				try 
				{
					input.close();
					
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		
  }
  
  public static void parseNamePattern(String str, ArrayList<String> hierarchy)
	{
		String[] strs = str.split("[^a-zA-Z']+");

		
		for(int i = 1; i< strs.length - 1; i++)
		{
			hierarchy.add(strs[i]);
		}
	}
  
  
 /* public static void searchCinema(String query)
  {
	  
	  ArrayList<String> hierarchy = new ArrayList<String>();
	  StringBuffer outputString=new StringBuffer("{");
	  Connection c = null;
	  try 
	  {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:cinemafinal1.db");
	     
	      hierarchy = getHierarchy(c);
	      
	    } 
	    catch ( Exception e ) 
	   {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	   }

	  String[] strs = query.split("and");
  
	 
	  Iterator<String> hierarchyIterator = hierarchy.iterator();
	  while (hierarchyIterator.hasNext()) {
			boolean found = false;
			String temp = hierarchyIterator.next();
			for(int i = 0; i< strs.length ; i++)
			{
				if((strs[i]).contains(temp))
				{
					found = true;
					
					String queryCondition= strs[i].replaceAll("[^0-9><=]", "");
					String val=fetchQueryValues(c, temp, queryCondition);
				//	outputString.append(temp+"\":");
					outputString.append(val+",");
				}
				
			}
			if(!found)
			{
				String val=fetchArgumentValues(c, temp);
			//	outputString.append(temp+"\":");
				if(hierarchyIterator.hasNext())
				outputString.append(val+",");
				else
					outputString.append(val);
			}
		}
		outputString.append("}");
		System.out.println("outputString:"+outputString);
	  

  }*/
  
  
  
  public static String searchCinema(String query)
  {
	  
	  ArrayList<String> hierarchy = new ArrayList<String>();
	  List<List<String>> jsonList = new ArrayList<List<String>>();
	  Connection c = null;
	  try 
	  {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:cinemafinal1.db");
	     
	      hierarchy = getHierarchy(c);
	      
	    } 
	    catch ( Exception e ) 
	   {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	   }

	  String[] strs = query.split("and");
  
	 
	  Iterator<String> hierarchyIterator = hierarchy.iterator();
	  while (hierarchyIterator.hasNext()) {
			boolean found = false;
			String temp = hierarchyIterator.next();
			for(int i = 0; i< strs.length ; i++)
			{
				if((strs[i]).contains(temp))
				{
					found = true;
					String queryCondition= strs[i].replaceAll("[^0-9><=]", "");
					jsonList.add(fetchQueryValues(c, temp, queryCondition));

				}
				
			}
			if(!found)
			{
				jsonList.add(fetchArgumentValues(c, temp));
			}
		}
		return prepareOutputJSON(jsonList);
  }
  
  public static void main(String args[])
  {
	 searchCinema("time>15 and phi>90");
	 
  }
  
  /*public static String fetchArgumentValues(Connection c, String argumentString )
  {

	  Statement stmt = null;
	  StringBuffer valString = new StringBuffer("\""+argumentString+"\":[") ;
	  int argument_id=0;
	  try 
	    {
		      stmt = c.createStatement();
		      ResultSet rs = stmt.executeQuery( "SELECT ARGUMENT_ID FROM ARGUMENT WHERE ARGUMENT_NAME = '"+ argumentString+"' and JSON_ID=1");
		      while ( rs.next() ) 
		      {
		         argument_id = rs.getInt("argument_id");
		         System.out.println( "ARGUMENTID = " + argument_id );
		      }
		      stmt = c.createStatement();
		      rs = stmt.executeQuery( "SELECT ARGUMENT_VAL FROM ARGUMENT_VALUE WHERE ARGUMENT_ID ="+ argument_id);
		      while ( rs.next() ) 
		      {
		         int arg_value = rs.getInt("ARGUMENT_VAL");
		         
		        	 valString=valString.append(arg_value+",");
		         
		      }
		      valString.deleteCharAt(valString.length()-1);
		      valString.append("]");
		      System.out.println( "valString = " + valString );
		      rs.close();
		      stmt.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    return valString.toString();
  }*/
  
  
  /*public static String fetchQueryValues(Connection c, String argumentString, String queryString )
  {

	  Statement stmt = null;
	  StringBuffer valString = new StringBuffer("\""+argumentString+"\":[") ;
	  int argument_id=0;
	  try 
	    {
		      stmt = c.createStatement();
		      ResultSet rs = stmt.executeQuery( "SELECT ARGUMENT_ID FROM ARGUMENT WHERE ARGUMENT_NAME = '"+ argumentString+"' and JSON_ID=1");
		      while ( rs.next() ) 
		      {
		         argument_id = rs.getInt("argument_id");
		         System.out.println( "ARGUMENTID = " + argument_id );
		      }
		      stmt = c.createStatement();
		      rs = stmt.executeQuery( "SELECT ARGUMENT_VAL FROM ARGUMENT_VALUE WHERE ARGUMENT_ID ="+ argument_id+" AND ARGUMENT_VAL "+queryString);
		      while ( rs.next() ) 
		      {
		         int arg_value = rs.getInt("ARGUMENT_VAL");
		      //   valString=valString.append("\""+argumentString+"\":"+arg_value+",");		         
		         valString=valString.append(arg_value+",");		
		         
		      }
		      valString.deleteCharAt(valString.length()-1);
		      valString.append("]");
		      System.out.println( "valString = " + valString );
		      rs.close();
		      stmt.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    return valString.toString();
  }*/
  
  
  
  
  public static ArrayList<String> fetchQueryValues(Connection c, String argumentString, String queryString )
  {

	  Statement stmt = null;
	  ArrayList<String> queryValues=new ArrayList<String>();
	  int argument_id=0;
	  int arg_value = 0;
	  try 
	    {
		      stmt = c.createStatement();
		      ResultSet rs = stmt.executeQuery( "SELECT ARGUMENT_ID FROM ARGUMENT WHERE ARGUMENT_NAME = '"+ argumentString+"' and JSON_ID=1");
		      while ( rs.next() ) 
		      {
		         argument_id = rs.getInt("argument_id");
		      }
		      stmt = c.createStatement();
		      rs = stmt.executeQuery( "SELECT ARGUMENT_VAL FROM ARGUMENT_VALUE WHERE ARGUMENT_ID ="+ argument_id+" AND ARGUMENT_VAL "+queryString);
		      while ( rs.next() ) 
		      {
		        arg_value = rs.getInt("ARGUMENT_VAL");
		               
		         queryValues.add("\""+argumentString+"\":\""+arg_value+""
			         		+ "\",");
			         
		      }
		   
		      rs.close();
		      stmt.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	  	return queryValues;
  }
  
  
  public static ArrayList<String> fetchArgumentValues(Connection c, String argumentString )
  {

	  Statement stmt = null;
	  ArrayList<String> argumentValues = new ArrayList<String>();
	  int argument_id=0;
	  try 
	    {
		      stmt = c.createStatement();
		      ResultSet rs = stmt.executeQuery( "SELECT ARGUMENT_ID FROM ARGUMENT WHERE ARGUMENT_NAME = '"+ argumentString+"' and JSON_ID=1");
		      while ( rs.next() ) 
		      {
		         argument_id = rs.getInt("argument_id");
		      }
		      stmt = c.createStatement();
		      rs = stmt.executeQuery( "SELECT ARGUMENT_VAL FROM ARGUMENT_VALUE WHERE ARGUMENT_ID ="+ argument_id);
		      while ( rs.next() ) 
		      {
		         int arg_value = rs.getInt("ARGUMENT_VAL");
		         argumentValues.add("\""+argumentString+"\":\""+arg_value+""
		         		+ "\",");
		         
		      }
		      
		      rs.close();
		      stmt.close();
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    return argumentValues;
  }
  
  
  public static String prepareOutputJSON(List<List<String>> jsonList)
  {
	  StringBuffer outputJSON = new StringBuffer("[");
	  System.out.println("JSONLIST size : "+jsonList.size());
	  
	  System.out.println("Generating output JSON....\n");
	  
	  Iterator<List<String>> jsonIterator = jsonList.iterator();
	  while(jsonIterator.hasNext())
	  {
		 ArrayList<String> argument = (ArrayList<String>)jsonIterator.next();
		 
		 Iterator argumentIterator = argument.iterator();
		 while(argumentIterator.hasNext())
		 {
			 String val = (String)argumentIterator.next();

		 }
	  }

	  outputJSON=outputJSON.append(generate(jsonList,"", new StringBuffer("")));

	  outputJSON.deleteCharAt(outputJSON.length()-1);
	  outputJSON.append("]");
	  System.out.println(outputJSON);
	  return outputJSON.toString();
  }
  

  
  public static void dumpList(int index, List l) {
	    int n = l.size();
	    while(index < n) {
	    	System.out.println( l.get(index));

	        index++;   
	        
	    }
	}
  
  
  public static String generateString(List<List<String>> outerList, StringBuffer outPut) {
      List<String> list = outerList.get(0);

      for(String str : list) {
          List<List<String>> newOuter = new ArrayList<List<String>>(outerList);
          newOuter.remove(list);

          if(outerList.size() > 1) {
        	  outPut=outPut.append(str);
        	  generateString(newOuter,outPut);
           } else {
             outPut=outPut.append(str);
           }
      }
      return outPut.toString();
  }
  
  public static String generate(List<List<String>> outerList, String outPut, StringBuffer outputJSON) {
      List<String> list = outerList.get(0);

      for(String str : list) {
          List<List<String>> newOuter = new ArrayList<List<String>>(outerList);
          newOuter.remove(list);

          if(outerList.size() > 1) {
              generate(newOuter, outPut+str,outputJSON);
           } else {
        	 outputJSON.append("{"+outPut+str);
        	 outputJSON.deleteCharAt(outputJSON.length()-1);
        	 outputJSON.append("},") ;
           //  System.out.println(outPut+str);
           }
      }
      return outputJSON.toString();
  }
}


