package commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.serial.serialmod.Serial;
import com.serial.serialmod.SerialInterface;

import interpretation.BinaryByte;
import interpretation.SerialMessageInterpreter;
import jssc.SerialPortException;

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
	    			//the instructions are added in seperate functions
	            .then(registerConnect()) //for example "/serial connect COM12"
	            .then(registerPortList())//prints all ports for example "/serial ports"
	            .then(registerSend())  //sends a message to the connected port e.g. "/serial ABBA"
	            .then(registerSendB()) //sends a message as binary
	            .then(registerEchoB()) //sends a message as binary
	            .then(registerDisconnect()) //disconnects from the currently connected port "/serial disconnect"
	            .then(registerIsConnected()) //shows whether any ports are connected 
	            .then(registerEcho()) //acts as if written message was received
	            .then(registerHelp()) //prints help
	            .executes(ctx -> {
	            	printHelp(ctx); 
	                return 1;
	            })
	        );
	    	
	    	//register alias
	    	dispatcher.register(Commands.literal("s").redirect(allComands));
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
	    
	    //REPEAT
	    public static ArgumentBuilder<CommandSource, ?> registerEcho() {
	        return Commands.literal("echo") 
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
	    
	    public static ArgumentBuilder<CommandSource, ?> registerEchoB() {
	        return Commands.literal("echoB")
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


