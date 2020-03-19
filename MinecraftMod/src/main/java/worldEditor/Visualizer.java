package worldEditor;

public class  Visualizer {
	//ABSTRACT CLASS FOR ALL VISUALIZERS
	//declare types
	public static enum types{SWAP_VISUALIZER,FANTASMAGORIA_VISUALIZER}
	public  types type = null;
	//declare clock
	public  int dTime=0;
	//helper variables
	public   int ID=0;
	public  boolean isOn=false;
	public TimeFunction f;
	
	
	public static Visualizer visualize(Visualizer.types type) {
		if(type.equals(Visualizer.types.SWAP_VISUALIZER)){return new SwapVisualizer();}
		if(type.equals(Visualizer.types.FANTASMAGORIA_VISUALIZER)){return new SwapVisualizer();}
		
		//if the type doesn't match
		return null;
	}
	
	public  int getdTime() {
		return dTime;
	}

	public  void setdTime(int dTime) {
		this.dTime = dTime;
	}

	public int getID() {
		return ID;
	}

	public  void setID(int iD) {
		this.ID = iD;
	}

	public  boolean isOn() {
		return this.isOn;
	}

	public  void setOn(boolean isOn) {
		this.isOn = isOn;
	}

	public  void setType(types type) {
		this.type = type;
	}

	public  Visualizer.types getType(){return this.type;}
	
}
