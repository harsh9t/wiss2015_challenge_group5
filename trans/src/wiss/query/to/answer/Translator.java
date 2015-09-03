package wiss.query.to.answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Translator {
	private static ArrayList<String> yesNoQuestionWords;
	private static List<String> toBe;
	private static List<String> toDo;
	private static Map<String, String> WHQuestionWords;
	private static List<String> articles;

	public Translator(){
		yesNoQuestionWords = new ArrayList<String>(Arrays.asList(
			"do","did", "does",
			"have","has","had",
			"should","shall",
			"can","could",
			"is","are","am","was","were",
			"may", "might",
			"will", "would"));
		
		toBe = new ArrayList<String>(Arrays.asList("is","are","am","was","were"));
		toDo = new ArrayList<String>(Arrays.asList("do","does","did"));
		
		WHQuestionWords = new HashMap<String, String>();
		WHQuestionWords.put("what", "");
		WHQuestionWords.put("which", "");
		WHQuestionWords.put("who", "");
		WHQuestionWords.put("whom", "");
		WHQuestionWords.put("where", "in "); // Preposition may vary
		WHQuestionWords.put("whose", "'s");
		WHQuestionWords.put("when", "at ");
		WHQuestionWords.put("why", "because,");
		WHQuestionWords.put("how", "by ");
		WHQuestionWords.put("how many", "");
		WHQuestionWords.put("how much", "");
		WHQuestionWords.put("how long", "");
		
		articles = new ArrayList<String>(Arrays.asList("a","the","an"));
		
	}
	
	public Map<String, String> extractArticles(String question){
		Map<String, String> articles = new HashMap<String, String>();
		String words[] = question.split(" ");
		for(int i=0; i<words.length; i++){
			if (Translator.articles.contains(words[i].toLowerCase()) && i+1<words.length){
				articles.put(words[i+1].replace("?", ""), words[i]);
			}
		}
		return articles;
	}
	
	public String removeArticles(String question){
		String result = "";
		String words[] = question.split(" ");
		for(int i=0; i<words.length; i++){
			if (!(Translator.articles.contains(words[i].toLowerCase()) && i+1<words.length)){
				result += words[i]+" ";
			}
		}
		return result.trim();
	}
	
	public String restoreArticles(Map<String, String> articles, String sentence){
		String result = "";
		String  words[] = sentence.split(" ");	
		for(int i=0; i<words.length; i++){
			String art = articles.get(words[i].replace(".", ""));
			if(art != null){
				result += " "+art + " " +words[i];
			}else{
				result += " "+ words[i];
			}
		}
		
		return result.trim();
	}
		
	// Generic solution
	public String lastResort(String question, Object answer){
		//return "The answer to the query \""+question+"\" is "+answer;
		return question+" "+answer+".";
	}
	
	
	// ASK - Yes/No Question ----------------------------------------------------
	public String yesNoQuestion(String question, boolean answer){
			String result = "Yes.";
			if(!answer) result = "No.";
			return question+" "+result;
	}
	
	public String advancedYesNoQuestion(String question, boolean answer, List<String> instances){
		Map<String, String> articles = extractArticles(question);
		question = removeArticles(question);
		
		String subject = getSubject(question, instances);
		String interrogativeWord = question.split(" ")[0];
		String theRest = questionToStatement(question.substring(interrogativeWord.length()+subject.length()+2),".");
		String result = null;
		if(answer){
			result = "Yes, "+subject+" "+interrogativeWord.toLowerCase()+" "+theRest;
		}else{
			result = "No, "+subject+" "+interrogativeWord.toLowerCase()+" not "+theRest;	
		}
		return restoreArticles(articles, result);
	}
	
	public String getSubject(String sentence, List<String> instances){
		String subject = instances.get(0);
		for(int i=1; i<instances.size(); i++){
			//System.out.println(sentence.indexOf(instances.get(i)));
			if(sentence.indexOf(subject) > sentence.indexOf(instances.get(i)) && sentence.indexOf(instances.get(i)) >= 0) subject = instances.get(i);
		}
		return subject;
	}
	
	public String getLastSubject(String sentence, List<String> instances){
		String subject = instances.get(0);
		for(int i=1; i<instances.size(); i++){
			//System.out.println(sentence.indexOf(instances.get(i)));
			if(sentence.indexOf(subject) < sentence.indexOf(instances.get(i)) && sentence.indexOf(instances.get(i)) >= 0) subject = instances.get(i);
		}
		return subject;
	}
	
	public String questionToStatement(String question, String endSymbol){
			question = question.replace("?", "");
			return question+endSymbol;
		
	}
	
	// SELECT - WH question -------------------------------------------------------------------------------------------------	
	
	// Object + Verb question (e.g. To whom did John give the medal?) * Tense problem & Means (e.g. How did Obama become a president?)
	public String OVWHQuestion(String question, String answer, List<String> instances){
		String word[] = question.split(" ");
		String subject = getSubject(question, instances);
		String WHWord = getWHQuestionWord(question);
		//System.out.println("WH word: "+WHWord);
		String result = null;
		if(WHQuestionWords.containsKey(word[0].toLowerCase())){
			String theRest = question.substring(question.toLowerCase().indexOf(subject.toLowerCase())+subject.length()).trim();
			String auxVerb = word[1];
			result = subject;
			if(WHWord.contains(" ")){
				auxVerb= word[2];
			}
			result += " "+auxVerb;
			
			if (!theRest.equals("?")) result += " "+theRest;
			result = questionToStatement(result, "");
			result += " "+attachPreposition(WHWord, answer);
		}else{ // Preposition
			/*String preposition = word[0].toLowerCase();
			String auxVerb =  word[2];
			WHWord = word[1];
			String theRest = question.substring(question.toLowerCase().indexOf(subject.toLowerCase())+subject.length()).trim();
			result = subject + " " + auxVerb;
			//result = subject;
			//System.out.println(word[2]);
			if (!theRest.equals("?")) result += " "+theRest;
			result = questionToStatement(result, "");
			result += " "+preposition+" "+attachPreposition(WHWord, answer)*/;
			
			String preposition = word[0].toLowerCase();
			WHWord = word[1];
			String auxVerb =  getWordBefore(question, subject);
			String theRest = question.substring(question.toLowerCase().indexOf(subject.toLowerCase())+subject.length()).trim();
			result = subject + " " + auxVerb;
			//result = subject;
			//System.out.println(word[2]);
			if (!theRest.equals("?")) result += " "+theRest;
			result = questionToStatement(result, "");
			result += " "+preposition+" "+attachPreposition(WHWord, answer);
		}
	
		return questionToStatement(result, ".");
	}
	
	public String attachPreposition(String WHWord, String answer){
		WHWord = WHWord.toLowerCase();
		if(WHWord.equals("whose")){
			// It has to be plural (not just ending with an s)
			// * if(answer.toLowerCase().charAt(answer.length()-1) == 's') return answer+"'";
			
			return answer+WHQuestionWords.get(WHWord);
		}else{
			return WHQuestionWords.get(WHWord)+answer;
		}
	}
	
	// Subject + Verb question (e.g. Who owns the most land in France?)
	public String SVWHQuestion(String question, String answer){
		String theRest = question.split(" ", 2)[1];
		String WHWord = getWHQuestionWord(question);
		return questionToStatement(attachPreposition(WHWord, answer)+" "+theRest,".");
	}
	
	public String getWHQuestionWord(String question){
		String words[] = question.toLowerCase().split(" ");
		String WHWord = words[0]+" "+words[1];
		for(int i=0; i<WHQuestionWords.size(); i++){
			if(WHQuestionWords.containsKey(WHWord)){
				return WHWord;
			}
		}
		return words[0];
	}
	
	// Quantity (e.g. How much money does he have? /  How many children does Joe have? / How long? )
	public String quantityQuestion(String question, String answer, List<String> instances){
		String result = null;
		String subject = getLastSubject(question, instances);
		if(question.toLowerCase().contains("are there")){
			String theRest = question.substring(question.toLowerCase().indexOf(subject.toLowerCase())+subject.length()).trim();
			theRest = theRest.replaceAll("are there ", "");
			result = "There are " + answer + " " + theRest;	
		}else{
			String theRest = question.substring(question.toLowerCase().indexOf(subject.toLowerCase())+subject.length()).trim();
			String verb = getWordBefore(question, subject);
			// Get the object entity
			String WHWord = getWHQuestionWord(question);
			//System.out.println(WHWord);
			String unity = question.substring(WHWord.length(), question.indexOf(verb)).trim();
			result = subject + " " + verb + " " + theRest +" "+ answer;
			if ( !unity.equals("") ) result += " "+unity;
		}
		return questionToStatement(result, ".");
	}
	
	public String getWordBefore(String sentence, String afterWord){
		String containingPart = sentence.substring(0, sentence.toLowerCase().indexOf(afterWord.toLowerCase())).trim();
		String words[] = containingPart.split(" ");
		return words[words.length-1];
	}
	
	public String imperative(String question,String answer){
		return question+"\n Answer: "+answer;
	}
	
	public boolean isImperative(String question){
		return !question.contains("?");
	}
	
	public boolean whQuestion(String question){
		question = question.toLowerCase();
		Iterator it = WHQuestionWords.entrySet().iterator();
		while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        if(question.contains(pair.getKey()+"")) return true;
		        it.remove(); // avoids a ConcurrentModificationException
		}
		
		return false;
	}
	
	public String methodSelector(String question, String answer, List<String> instances){
		if(isImperative(question)){
			return imperative(question, answer);
		}else if(whQuestion(question)){
			return null;
		}else{
			boolean transformedAnswer = true;
			if(answer.toLowerCase().equals("no")) transformedAnswer = false;
			return advancedYesNoQuestion(question, transformedAnswer, instances);
		}
	}
	
	
	
	public static void main(String args[]){
		Translator t = new Translator();
		//System.out.println(t.lastResort("Is Natalie Portman an actress?", "Yes"));
		//System.out.println(t.yesNoQuestion("Is Natalie Portman an actress?", true));
		
		System.out.println(t.advancedYesNoQuestion("Is Natalie Portman an actress?", false, Arrays.asList("Natalie Portman", "actress")));
		System.out.println(t.advancedYesNoQuestion("Are Jame and Sam siblings?", true, Arrays.asList("Jame and Sam", "she")));
		System.out.println(t.advancedYesNoQuestion("Would it be a good idea to help her?", false, Arrays.asList("it", "her")));
		System.out.println(t.advancedYesNoQuestion("Did Natalie Portman go to Harvard?", true, Arrays.asList("Natalie Portman", "actress")));
		System.out.println(t.advancedYesNoQuestion("Have Natalie Portman been awarded?", false, Arrays.asList("Natalie Portman", "actress")));
		System.out.println(t.advancedYesNoQuestion("Was Natalie Portman with her family during Star wars filming?", true, Arrays.asList("Natalie Portman", "actress")));
	
		System.out.println(t.SVWHQuestion("Who owns the most land in France?", "KOD"));
		System.out.println(t.SVWHQuestion("What goes well with Coding?", "Coffee"));
		System.out.println(t.SVWHQuestion("Whose car is the most expensive?", "ThisRichGuy"));
		System.out.println(t.SVWHQuestion("Whose house is the most expensive?", "James"));
		
		System.out.println(t.OVWHQuestion("What did John Kane Steward talk to?", "Key", Arrays.asList("John Kane Steward", "James")));		
		System.out.println(t.OVWHQuestion("To Whom did John Kane give the medal?", "Key", Arrays.asList("John Kane", "James")));
		System.out.println(t.OVWHQuestion("With Whom did John hang out?", "Key", Arrays.asList("John", "James")));

		System.out.println(t.OVWHQuestion("Where is Paris?", "France", Arrays.asList("Paris")));
		System.out.println(t.OVWHQuestion("Where is the highest mountain in the world?", "France", Arrays.asList("highest mountain in the world")));

		System.out.println(t.quantityQuestion("How long does a year last?", "365 days", Arrays.asList("a year")));
		System.out.println(t.quantityQuestion("How much does a Lambo cost?", "1.5 Million", Arrays.asList("a Lambo")));
		System.out.println(t.quantityQuestion("How much is a Lambo?", "1.5 Million", Arrays.asList("a Lambo")));
		System.out.println(t.quantityQuestion("How much money does Bill Gates have?", "52B dollars", Arrays.asList("Bill Gates")));
		System.out.println(t.quantityQuestion("How many children does Joe have?", "111", Arrays.asList("Joe")));
		
		System.out.println(t.quantityQuestion("How many people are there in the world?", "@@ Billion", Arrays.asList("people")));


		//* System.out.println(t.OVWHQuestion("Whose car is this lambo?", "ThisRichGuy", Arrays.asList("Car")));
		//* Article a/an/the
		//* Tense and conjugation
		//* Give me as a question
		
	
		// Examples
		System.out.println("\n\n\n\n\n\n");
		//System.out.println(t.yesNoQuestion("Is Natalie Portman an actress?", true));
		System.out.println(t.advancedYesNoQuestion("Is Natalie Portman an actress?", true, Arrays.asList("Natalie Portman", "actress")));
	
		System.out.println(t.SVWHQuestion("Who is the wife of Barack Obama?", "Michelle Obama"));
		
		System.out.println(t.quantityQuestion("How many children does Bill Clinton have?", "5", Arrays.asList("Bill Clinton")));

		//*
		System.out.println(t.OVWHQuestion("In which country does the Ganges start?", "France", Arrays.asList("the Ganges")));	
		System.out.println(t.SVWHQuestion("Who was J F K's vice president?", "Mr. L"));
		
		System.out.println(t.imperative("Give me all professional skateboarders from Sweden.", "A, B, C"));
		

	}	
}
