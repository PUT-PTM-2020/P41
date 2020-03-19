package worldEditor;

public class TimeFunction {
	public String functionAsString;
	public int dimensions;
	//you'll need regex for this shit man
	
	public int calculate(int [] inputs) {
		int result=0;
		
		if(inputs.length>dimensions) {return result;} //handle lack of inputs
		String fString= functionAsString+"";
		if(fString.length()==0) {return result;}
			
		//substitue all unknowns for value 0-9
		for(int i=0;i<inputs.length;i++) 
		{
			char varValue=((char) ('A'+i));
			
			for(int j=0;j<functionAsString.length();j++)
			{
				fString.replace(varValue, Integer.toString(inputs[i]).charAt(0));
			}	
			
		}
		
		
		return result;
	}
	
}
