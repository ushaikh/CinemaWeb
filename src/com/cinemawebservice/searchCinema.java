package com.cinemawebservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/searchCinema")
public class searchCinema {


  // This method is called if HTML is request
  @Path("{query}")
  @GET
  @Produces(MediaType.TEXT_HTML)
  public String search(@PathParam("query") String query) 
  {  
	System.out.println("Query : "+query);
	String outPutJSON = SQLiteJDBC.searchCinema(query);
	if(outPutJSON==null)
		return "Null";
    return outPutJSON;
  }
  
//This method is called if HTML is request

 @GET
 @Produces(MediaType.TEXT_HTML)
 public String processJSON() 
 {  
   SQLiteJDBC.parseJSON();
   return "{\"id\":\"success\", \"content\":\"uzma\"}";
 }
}