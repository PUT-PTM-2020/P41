package com.serial.serialmod;

import commands.ModCommands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@Mod("serial")

public class Serial
{
    public final static String MODID ="serial";
    public static SerialInterface serialInterface= new SerialInterface();

    
    public Serial() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        
    }
    
    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent event) {
    //register all custom commands
       ModCommands.registerAll(event.getCommandDispatcher());
    }
 
}
