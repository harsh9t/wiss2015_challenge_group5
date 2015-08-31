package wiss.query.execution;

public class DBQuery {
	private String query;
	
	public DBQuery(String query){
		this.query = query;
	}
	
	public String getQuery(){
		return query;
	}
	
	public void setQuery(String query){
		this.query = query;
	}
	
	// Any methods for query manipulation
}
