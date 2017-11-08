// ------------------------------------------------------------------------ //
//                              File Reference                              //
// Author: Patrick le Duc                                                   //
// Filename: CommandEdit.java                                               //
// License: GNU GPLv2                                                       //
// Description:                                                             //
// Command Edits Main Class                                                 //
//                                                                          //
// ------------------------------------------------------------------------ //

package nl.pleduc.mc.CommandEdit;

import java.io.IOException;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandEdit extends JavaPlugin
{
    private boolean m_Debugging = false;
    private String pluginName = "CommandEdit";
    private String pluginVersion = "v1.0";
    
    private CommandEditFileLoader m_Filesystem;
    private CommandEditProcessor m_Processor;
    
    @Override
    public void onEnable()
    {
        // Initialize Classes
        m_Filesystem = new CommandEditFileLoader( this );
        m_Debugging = m_Filesystem.getCustomConfig().getBoolean( "debugmode" );
        m_Filesystem.Reload();

        m_Processor = new CommandEditProcessor( this, m_Filesystem );
        
        // Catch all commands with the command edit executor for clean code purposes
        getCommand( "commandedit" ).setExecutor( new CommandEditCommandExecutor( this ) );
        
        // Register the Listeners
        this.getServer().getPluginManager().registerEvents( new CommandEditListener( this, m_Processor ), this );
        
        this.getLogger().info( pluginName + " " + pluginVersion + " succesfully loaded ! Debugmode is " + (m_Debugging?"enabled":"disabled" ) );
        
        // Metrics Test
        enableMetrics();
    }
    
    @Override
    public void onDisable()
    {
        m_Filesystem = null;
        m_Processor = null;
    }
    
    public String getPluginName()
    {
        return pluginName;
    }
    
    public String getPluginVersion()
    {
        return pluginVersion;
    }
    
    public boolean isDebugging()
    {
        return m_Debugging;
    } 
    
    private void enableMetrics()
    {
        try 
        {
                MetricsLite bml = new MetricsLite(this);
                bml.start();
        } 
        catch ( IOException e) 
        {
                e.printStackTrace();
        }
    }
}
