package com.serial.serialmod;

import java.nio.charset.StandardCharsets;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;

public class SerialMessageInterpreter {
	private static PlayerController pc = new PlayerController();

	public static void interpret(byte data[]) {
		//send received data to player (as ASCII)
		sendToPlayer(data);
		
		
	}
	
	public static void sendToPlayer(byte data[]) {
		//convert to Ascii
		StringBuilder buffer=new StringBuilder();
        buffer.append(new String(data, StandardCharsets.UTF_8));
        String dataAsASCII= buffer.substring(0,buffer.indexOf("\r\n"));
        
        //format and send to player
		StringTextComponent baseText= new StringTextComponent("");
		baseText.appendSibling(new StringTextComponent("\u00A73"+"Data received: "));
		baseText.appendSibling(new StringTextComponent("\u00A7f"+dataAsASCII));
		Minecraft.getInstance().player.sendMessage(baseText);
	}
}
