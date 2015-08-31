package wiss.query.execution;

import org.apache.jena.query.ResultSet;

public class Starter {

    public static void main( String[] args )
    {
    	// Turn off log4j logging system
    	org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
    	
    	String queryString = "PREFIX foaf:  <http://xmlns.com/foaf/0.1/> "
    		+ "SELECT ?name "
    	    + "WHERE { "
    		+    "?person foaf:name ?name . "
    		+ "} limit 10";
    	
    	DBQuery query = new DBQuery(queryString);
    	ResultSet results = QueryExecutioner.executeQuery("http://dbpedia.org/sparql", query);
    	QueryExecutioner.displayResult(results);
    	QueryExecutioner.closeQueryExecution();
    
    }

}
