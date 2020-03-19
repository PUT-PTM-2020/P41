package worldEditor;

public class editWorld {
	public static Visualizer [] v = {Visualizer.visualize(Visualizer.types.SWAP_VISUALIZER),Visualizer.visualize(Visualizer.types.FANTASMAGORIA_VISUALIZER)};
	
	public static void visualizeRaindbow(){
		v[0].isOn=true;
	}
	public static void deVisualizeRaindbow(){ 
		v[0].isOn=false;
	}
	public static void switchVisualizeRaindbow(){
		v[0].isOn=!v[0].isOn;
	}
	
	
	public static void visualizeLamps(){
		
	}
	
	
}
