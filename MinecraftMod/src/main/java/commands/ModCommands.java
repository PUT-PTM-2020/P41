package commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.serial.serialmod.Serial;
import com.serial.serialmod.SerialInterface;

import InventoryControl.CraftingGUI;
import binaryCommunication.BinaryByte;
import binaryCommunication.BitArray;
import binaryCommunication.Package;
import binaryCommunication.Package.OrderType;
import binaryCommunication.Package.PackageType;
import interpretation.SerialMessageInterpreter;
import jssc.SerialPortException;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class ModCommands {
		public static LiteralCommandNode<CommandSource> allComands;
	    public static void registerAll(CommandDispatcher<CommandSource> dispatcher) {
	    	//the commands are registered in the dispatcher server class
	    	allComands=
	    			//all commands must begin with /serial <command>
	    	dispatcher.register(Commands.literal("serial") 
	    			//the instructions are added in separate functions
	            .then(registerConnect()) //for example "/serial connect COM12"
	            .then(registerPortList())//prints all ports for example "/serial ports"
	            .then(registerSend())  //sends a message to the connected port e.g. "/serial ABBA"
	            .then(registerSendB()) //sends a message as binary
	            .then(registerEcho())
	            .then(registerEQ())
	            .then(registerDisconnect()) //disconnects from the currently connected port "/serial disconnect"
	            .then(registerIsConnected()) //shows whether any ports are connected 
	            .then(registerHelp()) //prints help
	            .executes(ctx -> {
	            	printHelp(ctx); 
	                return 1;
	            })
	        );
	    	//register alias
	    	dispatcher.register(Commands.literal("s").redirect(allComands));
	    }	
	    
	    //Equipment test
	    public static ArgumentBuilder<CommandSource, ?> registerEQ() {
	    	 return Commands.literal("eq") 
			            .executes(ctx -> {
			            	try 
			            	{
			           
			            		StringTextComponent baseText= new StringTextComponent("");
	                 			baseText.appendSibling(new StringTextComponent("\u00A72"+"Launching crafting EQ"));
	                 			ctx.getSource().sendFeedback(baseText,false);
	                 			
	                 			CraftingGUI gui= new CraftingGUI(true);
	                 			Minecraft.getInstance().displayGuiScreen(gui);
		
			            	}
			            	catch(Exception e) 
	                 		{
	                 			ctx.getSource().sendFeedback(new StringTextComponent("\u00A7c"+"LAUNCHING FAILED"),false);
	                 			ctx.getSource().sendFeedback(new StringTextComponent("\u00A74Error: "+e.toString()),false);
	                 		}	
			                return 1;
			            });    
	    }

	    //CONNECT
	    static ArgumentBuilder<CommandSource, ?> registerConnect()
	    {
	    	   
	        return Commands.literal("connect") 
	        		//WITH ONE ARGUMENT
		            .then(Commands.argument("portName", StringArgumentType.string())
			                .executes(ctx -> { 
			                	String selectedPort= StringArgumentType.getString(ctx, "portName");
			                	if (!selectedPort.contains("COM")){selectedPort="COM"+selectedPort;}
			                	try 
		                 		{
		                 			Serial.serialInterface.connect(selectedPort);
		                 			StringTextComponent baseText= new StringTextComponent("");
		                 			baseText.appendSibling(new StringTextComponent("\u00A72"+"Sucessfully connected to "));
		                 			baseText.appendSibling(new StringTextComponent("\u00A7a"+selectedPort));
		                 			ctx.getSource().sendFeedback(baseText,false);
		                 		}
			                	catch(SerialPortException e) 
		                 		{
		                 			ctx.getSource().sendFeedback(new StringTextComponent("\u00A7c"+"Connection Failed"),false);
		                 			ctx.getSource().sendFeedback(new StringTextComponent("\u00A74Error: "+e.getExceptionType()),false);
		                 		}
			                    return 1;
			                    }))
		            //WITH NO ARGUMENTS
		            .executes(ctx -> {
		            	//help
		            	ctx.getSource().sendFeedback(new StringTextComponent("PRINT_CONNECT_HELP"),false);
		                return 1;
		            });
	    }
	    

	    //DISSCONECT
	    public static ArgumentBuilder<CommandSource, ?> registerDisconnect() {
	    	 return Commands.literal("disconnect") 
			            .executes(ctx -> {
			            	try 
			            	{
			            		String discconectingFrom= Serial.serialInterface.getConnectedPort().getPortName();
			            		Serial.serialInterface.disconnect();
			            		StringTextComponent baseText= new StringTextComponent("");
	                 			baseText.appendSibling(new StringTextComponent("\u00A72"+"Sucessfully disconnected from  "));
	                 			baseText.appendSibling(new StringTextComponent("\u00A7a"+discconectingFrom));
	                 			ctx.getSource().sendFeedback(baseText,false);
			            	}
			            	catch(SerialPortException e) 
	                 		{
	                 			ctx.getSource().sendFeedback(new StringTextComponent("\u00A7c"+"Cannot disconnect"),false);
	                 			ctx.getSource().sendFeedback(new StringTextComponent("\u00A74Error: "+e.getExceptionType()),false);
	                 		}	
			                return 1;
			            });    
	    }
	    
	    
	    //EXAMPLE COMMAND /SERIAL PORTS
	    public static ArgumentBuilder<CommandSource, ?> registerPortList() {
	    	 return Commands.literal("portList") //add port commend used as in /serial ports
			            .executes(ctx -> { //lambda expression with execution instructions
			            	//taking ports from static class in SerialInterface and sending ACK to player
			         		String[] ports= SerialInterface.searchForPorts();	
		                    ctx.getSource().sendFeedback(new StringTextComponent("\u00A73"+"Available ports:"), false);
		                    //printing the port list
		                    for(int i=0;i<ports.length;i++) 
		                    {
		                      ctx.getSource().sendFeedback(new StringTextComponent(" -"+ports[i].toString()), false);
		                    }
			                return 1;
			            });    
	    }
	    
	    public static ArgumentBuilder<CommandSource, ?> registerEcho() {
	        return Commands.literal("echo") 
		            .then(echoASCII())
		            .then(echoB())
		            .then(echoP())
		            .executes(ctx -> {
		            	//help
		            	ctx.getSource().sendFeedback(new StringTextComponent("PRINT_SEND_HELP"),false);
		                return 1;
		            });
	    	
		}  	 
	    
	    
	    public static ArgumentBuilder<CommandSource, ?> echoASCII() {
	        return Commands.literal("ASCII")
	        		 .then(Commands.argument("toBeSent", StringArgumentType.string())
				                .executes(ctx -> { 			                	
				                	String toBeSent = StringArgumentType.getString(ctx, "toBeSent");
				                	try
				                	{
				                		SerialInterface.repeat(toBeSent);
				                		StringTextComponent baseText= new StringTextComponent("");
			                 			baseText.appendSibling(new StringTextComponent("\u00A72"+"Echo data: "));
			                 			baseText.appendSibling(new StringTextComponent("\u00A7e"+toBeSent));
			                 			
			                 			baseText.appendSibling(new StringTextComponent("\n\n\u00A7f"+"Char  Binary     ASCII "));
			                 			byte[]  by = toBeSent.getBytes();
										for(int j=0;j<by.length;j++) {
											baseText.appendSibling(new StringTextComponent("\n \u00A73"+toBeSent.charAt(j)));
											baseText.appendSibling(new StringTextComponent("     \u00A7f"+BinaryByte.getBinary(by[j])));
											baseText.appendSibling(new StringTextComponent("   \u00A7e("+by[j]+")"));
										}
			                 			ctx.getSource().sendFeedback(baseText,false);
				                	}
				                	catch (SerialPortException e) 
				                	{ 
				                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A7c"+"Failed to send the message"),false);
				                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A74Error: "+e.getExceptionType()),false);
				                	}
				                	return 1;
				                    }))
		            .executes(ctx -> {
		            	//help
		            	ctx.getSource().sendFeedback(new StringTextComponent("PRINT_SEND_HELP"),false);
		                return 1;
		            });	
		} 
	    
       	
	    
	    //SENDB
	    public static ArgumentBuilder<CommandSource, ?> registerSendB() {
	        return Commands.literal("sendB")
		            	.then(Commands.argument("toBeSent", StringArgumentType.string())
			                .executes(ctx -> { 			                	
			                	String toBeSent = StringArgumentType.getString(ctx, "toBeSent");
			                	try
			                	{
			                		BinaryByte b[] = BinaryByte.getBinaryByteArray(toBeSent, "N");
			                		Serial.serialInterface.sendMessage(BinaryByte.getByteArray(b));
			                		
			                		
			                		StringTextComponent baseText= new StringTextComponent("");
		                 			baseText.appendSibling(new StringTextComponent("\u00A72"+"Sent data: "));
		                 		
									for(int j=0;j<b.length;j++) {
										baseText.appendSibling(new StringTextComponent("\n \u00A7f["+Integer.toString(j)+"] "));
										baseText.appendSibling(new StringTextComponent(" \u00A73"+b[j]));
									}
		                 			ctx.getSource().sendFeedback(baseText,false);
			                	}
			                	catch (SerialPortException e) 
			                	{ 
			                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A7c"+"Failed to send the message"),false);
			                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A74Error: "+e.getExceptionType()),false);
			                	}
			                	return 1;
			                    }))
		            .executes(ctx -> {
		            	//help
		            	ctx.getSource().sendFeedback(new StringTextComponent("PRINT_SEND_HELP"),false);
		                return 1;
		            });	
		} 
	    
	    
	    public static ArgumentBuilder<CommandSource, ?> echoB() {
	        return Commands.literal("Binary")
		            	.then(Commands.argument("toBeSent", StringArgumentType.string())
			                .executes(ctx -> { 			                	
			                	String toBeSent = StringArgumentType.getString(ctx, "toBeSent");
			                	try
			                	{
			                		BinaryByte b[] = BinaryByte.getBinaryByteArray(toBeSent, "N");
			                		SerialInterface.repeat(BinaryByte.getByteArray(b));
			                		
			                		StringTextComponent baseText= new StringTextComponent("");
		                 			baseText.appendSibling(new StringTextComponent("\u00A72"+"Echoing: "));
		                 		
									for(int j=0;j<b.length;j++) {
										baseText.appendSibling(new StringTextComponent("\n \u00A7f["+Integer.toString(j)+"]"));
										baseText.appendSibling(new StringTextComponent(" \u00A73"+b[j]));
									}
		                 			ctx.getSource().sendFeedback(baseText,false);
			                	}
			                	catch (SerialPortException e) 
			                	{ 
			                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A7c"+"Failed to send the message"),false);
			                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A74Error: "+e.getExceptionType()),false);
			                	}
			                	return 1;
			                    }))
		            .executes(ctx -> {
		            	//help
		            	ctx.getSource().sendFeedback(new StringTextComponent("PRINT_SEND_HELP"),false);
		                return 1;
		            });	
		} 
	    
	    public static ArgumentBuilder<CommandSource, ?> echoP() {
	        return Commands.literal("Package")
		            	.then(Commands.literal("ASCII")
		            			.then(Commands.argument("toBeSent", StringArgumentType.string())
		    			                .executes(ctx -> { 			                	
		    			                	String toBeSent = StringArgumentType.getString(ctx, "toBeSent");
		    			                	try
		    			                	{
		    			                		byte[]  by = toBeSent.getBytes();
		    			                		BitArray[] data= new BitArray[by.length];
		    			                		for(int i=0;i<by.length;i++) {
		    			                			data[i]= new BitArray(by[i]);
		    			                		}
		    			                		Serial.serialInterface.echoPackage(new binaryCommunication.Package(PackageType.ASCII,OrderType.NOTDETERMINED,data));
		    			                		
		    			                		
		    			                		StringTextComponent baseText= new StringTextComponent("");
		    		                 			baseText.appendSibling(new StringTextComponent("\u00A72"+"Sent data: "));
		    		                 			baseText.appendSibling(new StringTextComponent("\u00A7e"+toBeSent));
		    		                 			
		    		   
		    		                 			ctx.getSource().sendFeedback(baseText,false);
		    			                	}
		    			                	catch (Exception e) 
		    			                	{ 
		    			                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A7c"+"Failed to send the package"),false);
		    			                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A74Error: "+e.toString()),false);
		    			                	}
		    			                	return 1;
		    			                    }))
			                .executes(ctx -> { 			                	
			                	ctx.getSource().sendFeedback(new StringTextComponent("PRINT_ASCII_HELP"),false);
			                	return 1;
			                    }))
		            	.then(Commands.literal("BINARY")
		            			.then(playerMovement())
		            			.then(cameraMovement())
		            			.then(leftRightClick())
		            			.then(hotBar())
		            			.then(inventory())
			                .executes(ctx -> { 			                	
			                	ctx.getSource().sendFeedback(new StringTextComponent("PRINT_BINARY_HELP"),false);
			                	return 1;
			                    }))
		            .executes(ctx -> {
		            	//help
		            	ctx.getSource().sendFeedback(new StringTextComponent("PRINT_SEND_HELP"),false);
		                return 1;
		            });	
		}
	    
	       	
	    
	    
	    public static ArgumentBuilder<CommandSource, ?> playerMovement() {
	    return Commands.literal("PLAYER_MOVEMENT")
		.then(Commands.argument("WSADJCS", StringArgumentType.string())
                .executes(ctx -> { 			                	
                	String movementClues = StringArgumentType.getString(ctx, "WSADJCS");
                	try {
                		if(movementClues.length()!=7) {throw new Exception("Movement Clues must have 7 bits!");}
                		for(int i=0;i<movementClues.length();i++) {if(movementClues.charAt(i)!='0' && movementClues.charAt(i)!='1') {throw new Exception("Only ones and zeroes allowed!");}}
    	
                		BitArray[] data = {new BitArray(movementClues, false)};
                		Serial.serialInterface.echoPackage(new binaryCommunication.Package(PackageType.BINARY,OrderType.PLAYER_MOVEMENT,data));
                		
                		
                		StringTextComponent baseText= new StringTextComponent("");
             			baseText.appendSibling(new StringTextComponent("\u00A72"+"Sent movement clues: "));
             			baseText.appendSibling(new StringTextComponent("\u00A7e"+movementClues));
             			
             			ctx.getSource().sendFeedback(baseText,false);	
                		
                	}
                	catch (Exception e) 
                	{ 
                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A7c"+"Failed to send the pakage"),false);
                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A74Error: "+e.toString()),false);
                	}
                
                	return 1;
                    }))
		
        .executes(ctx -> { 
        	ctx.getSource().sendFeedback(new StringTextComponent("PRINT_PLAYER_MOVEMENT_HELP"),false);
        	return 1;
            });
	    }
	    
	    public static ArgumentBuilder<CommandSource, ?> leftRightClick() {
		    return Commands.literal("LEFT_RIGHT_CLICK")
			.then(Commands.argument("LeftClick", BoolArgumentType.bool())
					.then(Commands.argument("RightClick", BoolArgumentType.bool())
			                .executes(ctx -> { 			                	
			                	boolean leftClick =  BoolArgumentType.getBool(ctx, "LeftClick");
			                	boolean rightClick =  BoolArgumentType.getBool(ctx, "RightClick");
			                	try {
			                		BitArray[] data = {new BitArray(leftClick),new BitArray(rightClick)};
			                		System.out.print("\nDEBUG: "+data.toString());
			                		Serial.serialInterface.echoPackage(new Package(PackageType.BINARY,OrderType.LEFT_RIGHT_CLICK,data));
			                		
			                		StringTextComponent baseText= new StringTextComponent("");
			             			baseText.appendSibling(new StringTextComponent("\u00A72"+"Sent Left/RightClick clues: "));
			             			baseText.appendSibling(new StringTextComponent("\u00A7e"+Boolean.toString(leftClick)));
			             			baseText.appendSibling(new StringTextComponent("\u00A72"+","));
			             			baseText.appendSibling(new StringTextComponent("\u00A7e"+Boolean.toString(rightClick)));
			             			
			             			ctx.getSource().sendFeedback(baseText,false);	
			                		
			                	}
			                	catch (Exception e) 
			                	{ 
			                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A7c"+"Failed to send the pakage"),false);
			                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A74Error: "+e.toString()),false);
			                	}
			                
			                	return 1;
			                    }))
					
	                .executes(ctx -> { 			                	 
	                	return 1;
	                    }))
				
			
	        .executes(ctx -> { 
	        	ctx.getSource().sendFeedback(new StringTextComponent("PRINT_LEFT_RIGH_CLICK_HELP"),false);
	        	return 1;
	            });
		    }
		    
	    
	    public static ArgumentBuilder<CommandSource, ?> cameraMovement() {
		    return Commands.literal("CAMERA_MOVEMENT")
			.then(Commands.argument("Yaw", FloatArgumentType.floatArg())
					.then(Commands.argument("Pitch", FloatArgumentType.floatArg())
			
			                .executes(ctx -> { 			                	
			                	float yaw = FloatArgumentType.getFloat(ctx, "Yaw");
			                	float pitch = FloatArgumentType.getFloat(ctx, "Pitch");
			                	try {
			                	
			                		Serial.serialInterface.echoPackage(binaryCommunication.Package.createCameraMovementPackage(yaw, pitch));
			                		StringTextComponent baseText= new StringTextComponent("");
			             			baseText.appendSibling(new StringTextComponent("\u00A72"+"Sent yaw/pitch: "));
			             			baseText.appendSibling(new StringTextComponent("\u00A7e"+yaw));
			             			baseText.appendSibling(new StringTextComponent("\u00A7a"+"/"));
			             			baseText.appendSibling(new StringTextComponent("\u00A7e"+pitch));
			             			
			             			ctx.getSource().sendFeedback(baseText,false);	
			                		
			                	}
			                	catch (Exception e) 
			                	{ 
			                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A7c"+"Failed to send the pakage"),false);
			                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A74Error: "+e.toString()),false);
			                	}
			                	
			                	
			                	return 1;
			                    }))

					
	                .executes(ctx -> { 			                	
	                	float yaw = FloatArgumentType.getFloat(ctx, "Yaw");
	                	try {
	                	
	                		Serial.serialInterface.echoPackage(binaryCommunication.Package.createCameraMovementPackage(yaw, 0));
	                		StringTextComponent baseText= new StringTextComponent("");
	             			baseText.appendSibling(new StringTextComponent("\u00A72"+"Sent yaw/pitch: "));
	             			baseText.appendSibling(new StringTextComponent("\u00A7e"+yaw));
	             			baseText.appendSibling(new StringTextComponent("\u00A7a"+"/"));
	             			baseText.appendSibling(new StringTextComponent("\u00A7e"+"0.0"));
	             			
	             			ctx.getSource().sendFeedback(baseText,false);	
	                		
	                	}
	                	catch (Exception e) 
	                	{ 
	                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A7c"+"Failed to send the pakage"),false);
	                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A74Error: "+e.toString()),false);
	                	}
	                	return 1;
	                    }))
			
	        .executes(ctx -> { 
	        	ctx.getSource().sendFeedback(new StringTextComponent("PRINT_PLAYER_MOVEMENT_HELP"),false);
	        	return 1;
	            });
		    }
	    
	    
	    public static ArgumentBuilder<CommandSource, ?> hotBar() {
		    return Commands.literal("HOT_BAR")
			.then(Commands.argument("Previous/Next", BoolArgumentType.bool())
	                .executes(ctx -> { 			                	
	                	boolean hotbarClue = BoolArgumentType.getBool(ctx, "Previous/Next");
	                	try {
	                		BitArray[] data = {new BitArray(hotbarClue)};
	                		Serial.serialInterface.echoPackage(new binaryCommunication.Package(PackageType.BINARY,OrderType.HOT_BAR,data));
	                		
	                		StringTextComponent baseText= new StringTextComponent("");
	             			baseText.appendSibling(new StringTextComponent("\u00A72"+"Sent hot bar clue: "));
	             			baseText.appendSibling(new StringTextComponent("\u00A7e"+hotbarClue));
	             			
	             			ctx.getSource().sendFeedback(baseText,false);	
	                		
	                	}
	                	catch (Exception e) 
	                	{ 
	                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A7c"+"Failed to send the pakage"),false);
	                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A74Error: "+e.toString()),false);
	                	}
	                
	                	return 1;
	                    }))
			
	        .executes(ctx -> { 
	        	ctx.getSource().sendFeedback(new StringTextComponent("PRINT_PLAYER_MOVEMENT_HELP"),false);
	        	return 1;
	            });
		    }
	    public static ArgumentBuilder<CommandSource, ?> inventory() {
		    return Commands.literal("PLAYER_MOVEMENT")
			.then(Commands.argument("WSADJCS", StringArgumentType.string())
	                .executes(ctx -> { 			                	
	                	String movementClues = StringArgumentType.getString(ctx, "WSADJCS");
	                	try {
	                		if(movementClues.length()!=7) {throw new Exception("Movement Clues must have 7 bits!");}
	                		for(int i=0;i<movementClues.length();i++) {if(movementClues.charAt(i)!='0' && movementClues.charAt(i)!='1') {throw new Exception("Only ones and zeroes allowed!");}}
	    	
	                		BitArray[] data = {new BitArray(movementClues, false)};
	                		Serial.serialInterface.echoPackage(new binaryCommunication.Package(PackageType.BINARY,OrderType.PLAYER_MOVEMENT,data));
	                		
	                		
	                		StringTextComponent baseText= new StringTextComponent("");
	             			baseText.appendSibling(new StringTextComponent("\u00A72"+"Sent movement clues: "));
	             			baseText.appendSibling(new StringTextComponent("\u00A7e"+movementClues));
	             			
	             			ctx.getSource().sendFeedback(baseText,false);	
	                		
	                	}
	                	catch (Exception e) 
	                	{ 
	                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A7c"+"Failed to send the pakage"),false);
	                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A74Error: "+e.toString()),false);
	                	}
	                
	                	return 1;
	                    }))
			
	        .executes(ctx -> { 
	        	ctx.getSource().sendFeedback(new StringTextComponent("PRINT_PLAYER_MOVEMENT_HELP"),false);
	        	return 1;
	            });
		    }
	    
	    //SEND
	    public static ArgumentBuilder<CommandSource, ?> registerSend() {
	        return Commands.literal("send")
		            	.then(Commands.argument("toBeSent", StringArgumentType.string())
			                .executes(ctx -> { 			                	
			                	String toBeSent = StringArgumentType.getString(ctx, "toBeSent");
			                	try
			                	{
			                		Serial.serialInterface.sendMessage(toBeSent);
			                		StringTextComponent baseText= new StringTextComponent("");
		                 			baseText.appendSibling(new StringTextComponent("\u00A72"+"Sent data: "));
		                 			baseText.appendSibling(new StringTextComponent("\u00A7e"+toBeSent));
		                 			
		                 			baseText.appendSibling(new StringTextComponent("\n\n\u00A7f"+"Char  Binary     ASCII "));
		                 			byte[]  by = toBeSent.getBytes();
									for(int j=0;j<by.length;j++) {
										baseText.appendSibling(new StringTextComponent("\n \u00A73"+toBeSent.charAt(j)));
										baseText.appendSibling(new StringTextComponent("     \u00A7f"+BinaryByte.getBinary(by[j])));
										baseText.appendSibling(new StringTextComponent("   \u00A7e("+by[j]+")"));
									}
		                 			ctx.getSource().sendFeedback(baseText,false);
			                	}
			                	catch (SerialPortException e) 
			                	{ 
			                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A7c"+"Failed to send the message"),false);
			                		ctx.getSource().sendFeedback(new StringTextComponent("\u00A74Error: "+e.getExceptionType()),false);
			                	}
			                	return 1;
			                    }))
		            .executes(ctx -> {
		            	//help
		            	ctx.getSource().sendFeedback(new StringTextComponent("PRINT_SEND_HELP"),false);
		                return 1;
		            });	
		} 
	    
	  	
	    
	    
	    //IS CONNECTED
	    public static ArgumentBuilder<CommandSource, ?> registerIsConnected() {
	    	 return Commands.literal("isConnected") 
			            .executes(ctx -> {
			         		if(Serial.serialInterface.getConnectedPort()==null) 
			         		{
			         		 ctx.getSource().sendFeedback(new StringTextComponent("\u00A7c"+"You are NOT currently connected"),false);	
			         		 return 1;
			         		}
			         		StringTextComponent baseText= new StringTextComponent("");
			        		baseText.appendSibling(new StringTextComponent("You are currently connected to "));
                 			baseText.appendSibling(new StringTextComponent("\u00A7a"+Serial.serialInterface.getConnectedPort().getPortName()));
                 			ctx.getSource().sendFeedback(baseText,false);
			         		return 1;
			            });    
	    }
	    
	    
	    //HELP
	    public static ArgumentBuilder<CommandSource, ?> registerHelp() {
	    	 return Commands.literal("help") 
			            .executes(ctx -> {
			            	printHelp(ctx);
			                return 1;
			            });    
	    }
	    
	    private static void printHelp(CommandContext<CommandSource> ctx) { 	
        	ctx.getSource().sendFeedback(new StringTextComponent("\u00A76"+"--Help for serialMod--\n"),false);
        	
        	//print table of COMMAND    ALIAS      USAGE   
        	StringTextComponent row1= new StringTextComponent("");
        	row1.appendSibling(new StringTextComponent("\u00A73"+"COMMAND    "));
        	row1.appendSibling(new StringTextComponent("\u00A7b"+"ARGUMENT   "));
 			row1.appendSibling(new StringTextComponent("\u00A7e"+"DESCRIPTION      "));
 			ctx.getSource().sendFeedback(row1,false);
 			//PORT LIST
 			StringTextComponent row2= new StringTextComponent("");
 			row2.appendSibling(new StringTextComponent("\u00A7f"+"portList   "));
 			row2.appendSibling(new StringTextComponent("\u00A7f"+"     --    "));
 			row2.appendSibling(new StringTextComponent("\u00A7f"+"     lists availabe ports."));
 			ctx.getSource().sendFeedback(row2,false);
 			//CONNECT
 			StringTextComponent row3= new StringTextComponent("");
 			row3.appendSibling(new StringTextComponent("\u00A7f"+"connect   "));
 			row3.appendSibling(new StringTextComponent("\u00A7f"+"  <portId>"));
 			row3.appendSibling(new StringTextComponent("\u00A7f"+"    connects to a selected port."));
 			ctx.getSource().sendFeedback(row3,false);
 			//SEND
 			StringTextComponent row4= new StringTextComponent("");
 			row4.appendSibling(new StringTextComponent("\u00A7f"+"send      "));
 			row4.appendSibling(new StringTextComponent("\u00A7f"+"     <msg>  "));
 			row4.appendSibling(new StringTextComponent("\u00A7f"+"   sends <msg> to the connected port."));
 			ctx.getSource().sendFeedback(row4,false);
 			//disconnect
 			StringTextComponent row5= new StringTextComponent("");
 			row5.appendSibling(new StringTextComponent("\u00A7f"+"disconect"));
 			row5.appendSibling(new StringTextComponent("\u00A7f"+"      --    "));
 			row5.appendSibling(new StringTextComponent("\u00A7f"+"     disconects form the port."));
 			ctx.getSource().sendFeedback(row5,false);
 			//disconnect
 			StringTextComponent row6= new StringTextComponent("");
 			row6.appendSibling(new StringTextComponent("\u00A7f"+"isConected"));
 			row6.appendSibling(new StringTextComponent("\u00A7f"+"     --    "));
 			row6.appendSibling(new StringTextComponent("\u00A7f"+"   shows if a port is connected."));
 			ctx.getSource().sendFeedback(row6,false);
	    }
}


