// ------------------------------------------------------------------------ //
//                              File Reference                              //
// Author: Patrick le Duc                                                   //
// Filename: CommandEditListener.java                                       //
// License: GNU GPLv2                                                       //
// Description:                                                             //
// Listens to players firing commands, catching them and processing them    //
//                                                                          //
// ------------------------------------------------------------------------ //

package nl.pleduc.mc.CommandEdit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.Listener;


public class CommandEditListener implements Listener
{
    private CommandEdit m_Base;
    private CommandEditProcessor m_Processor;
    
    public CommandEditListener( CommandEdit base, CommandEditProcessor processor )
    {
        m_Base = base;
        m_Processor = processor;
    }
    
    @EventHandler( priority = EventPriority.HIGHEST )
    public void onPlayerCommand( PlayerCommandPreprocessEvent event )
    {
        String m = event.getMessage();
        
        // Debugging
        if( m_Base.isDebugging() ){ m_Base.getLogger().info( "Command Edit catched the following command: " + m ); }
        
        event.setCancelled( m_Processor.ProcessCommand( event ) );     
    }
        
}