// ------------------------------------------------------------------------ //
//                              File Reference                              //
// Author: Patrick le Duc                                                   //
// Filename: CommandEditConfigLoader.java                                   //
// License: GNU GPLv2                                                       //
// Description:                                                             //
// - Based on http://wiki.bukkit.org/Configuration_API_Reference            //
//                                                                          //
// ------------------------------------------------------------------------ //

package nl.pleduc.mc.CommandEdit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomConfig
{ 
    private String                  m_Filename = "default.yml";
    private FileConfiguration       m_Config = null;
    private File                    m_ConfigFile = null;
    private JavaPlugin              m_Plugin = null;
    
    CustomConfig( String a_Name, JavaPlugin a_Plugin )
    {
        m_Filename = a_Name;
        m_Plugin = a_Plugin;
    
        ReloadCustomConfig();
        
        if( !m_Plugin.getDataFolder().exists() )
        {
            SafeDefaultConfig();
        }
    }
    
    // Returns the config
    public FileConfiguration getConfig()
    {
        if( m_Config == null ) { ReloadCustomConfig(); }
        return m_Config;
    }
    
    // Load the file from the config
    private void ReloadCustomConfig()
    {
        if( m_ConfigFile == null )
        {
            m_ConfigFile = new File( m_Plugin.getDataFolder(), m_Filename);
        }
        
        m_Config = YamlConfiguration.loadConfiguration( m_ConfigFile );
        
        InputStream defaultConfigStream = m_Plugin.getResource( m_Filename );
        if( defaultConfigStream != null )
        {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration( defaultConfigStream );
            m_Config.setDefaults( defaultConfig );
        }
    }
    
    // Save the config
    public void SafeCustomConfig()
    {
        if ( m_Config == null || m_ConfigFile == null) 
        {
            return;
        }
        try 
        {
            getConfig().save( m_ConfigFile );
        } 
        catch ( IOException ex ) 
        {
            m_Plugin.getLogger().log( Level.SEVERE, "Could not save config to " + m_ConfigFile, ex);
        }
    }
    
    // Save default config
    public void SafeDefaultConfig()
    {
        if ( m_ConfigFile == null) 
        {
            m_ConfigFile = new File( m_Plugin.getDataFolder(), m_Filename );
        }
        if ( !m_ConfigFile.exists()) 
        {            
             m_Plugin.saveResource( m_Filename, false);
         }
    }
}
