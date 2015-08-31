package wiss.query.execution;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class QueryExecutioner {
	
	private static QueryExecution qexec;
	
	public static ResultSet executeQuery(String sparqlEndpoint, String queryString) {
		Query query = QueryFactory.create(queryString);
		qexec = QueryExecutionFactory.sparqlService(
				sparqlEndpoint, query);
		ResultSet results = null;		
		try {
			results = qexec.execSelect();
		} catch (Exception e) {
			e.printStackTrace();
			qexec.close();
		}
		return results;
	}
	
	public static ResultSet executeQuery(String sparqlEndpoint, DBQuery query){
		return executeQuery(sparqlEndpoint, query.getQuery());
	}
	
	public static void displayResult(ResultSet results){
		for (; results.hasNext();) {
			QuerySolution soln = results.nextSolution();
			System.out.println(soln.get("?name"));
		}
	}
	
	public static void closeQueryExecution(){
		if (qexec!=null) qexec.close();
	}
}
