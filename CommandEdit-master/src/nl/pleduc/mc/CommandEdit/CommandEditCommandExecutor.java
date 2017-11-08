// ------------------------------------------------------------------------ //
//                              File Reference                              //
// Author: Patrick le Duc                                                   //
// Filename: CommandEditCommandExecutor.java                                //
// License: GNU GPLv2                                                       //
// Description:                                                             //
// Processes all /commandedit * commands                                    //
//                                                                          //
// ------------------------------------------------------------------------ //

package nl.pleduc.mc.CommandEdit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class CommandEditCommandExecutor implements CommandExecutor
{
    private CommandEdit m_Base;
    
    public CommandEditCommandExecutor( CommandEdit base )
    {
        m_Base = base;
    }
    
    @Override
    public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args )
    {  
        if( cmd.getName().equalsIgnoreCase( "commandedit" ) )
        {
            if( args.length > 0 )
            {
                /* Displays the plugin menu in chat */
                if( args[0].equalsIgnoreCase( "?" ) )
                {
                    // Don't care about console or player for this command
                    sender.sendMessage( "** Command Edit by MartinK1337 **" );
                    sender.sendMessage( "/commandedit version - Displays the version" );
                }
                /* Returns the version number in chat */
                if( args[0].equalsIgnoreCase( "version" ) )
                {
                    sender.sendMessage( "Version: " + m_Base.getPluginVersion() );
                }
            }
            else
            {
                sender.sendMessage( "Usage: /commandedit ?" );
            }
            
            return true;
        }
        
        // Not handled by this plugin return
        return false;
    }
}
