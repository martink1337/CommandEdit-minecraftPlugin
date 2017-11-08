// ------------------------------------------------------------------------ //
//                              File Reference                              //
// Author: Patrick le Duc                                                   //
// Filename: CommandEditFileLoader.java                                     //
// License: GNU GPLv2                                                       //
// Description:                                                             //
// Loads the configuration file for this plugin                             //
//                                                                          //
// ------------------------------------------------------------------------ //
package nl.pleduc.mc.CommandEdit;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.configuration.file.FileConfiguration;

public class CommandEditFileLoader 
{
    private CommandEdit m_Base;
    private CustomConfig m_Config;
    
    private ArrayList< CommandEditCommand > m_CommandList;
    
    private String m_Command = "";
    private String[] m_CommandArgs = null;
    
    CommandEditFileLoader( CommandEdit base )
    {
        m_Base = base;
        m_Config = new CustomConfig( "commands.yml", base );
        
        m_CommandList = new ArrayList< CommandEditCommand >();
    }
    
    FileConfiguration getCustomConfig()
    {
        return m_Config.getConfig();
    }
    
    ArrayList< CommandEditCommand > GetCommandList()
    {
        return m_CommandList;
    }
        
    public void Load()
    {
        ArrayList< String > stringList = ( ArrayList<String> )m_Config.getConfig().getList( "commands" );
        
        for( int i = 0; i < stringList.size(); i++ )
        {
            String commandString = stringList.get( i );
            ProcessLine( commandString );
        }
    }
    
    public void Reload()
    {
        m_CommandList.clear();
        Load();
    }
    
    public CommandEditCommand ProcessLine( String a_Line )
    {
        // Variables
        CommandEditCommand a_Command = new CommandEditCommand();
        a_Command.m_Alias = new ArrayList< CommandEditAlias >();
               
        // Split the line Command = Alias | Alias | Alias
        String[] commandSplit = a_Line.split( "=" );
    
        String commandLine = "";
        String aliasLine = "";
        
        if( commandSplit[0] != null && commandSplit[1] != null )
        {   
            commandLine = commandSplit[0];
            aliasLine = commandSplit[1];
        }
       
        // Store Command
        ProcessCommand( commandLine );
               
        a_Command.m_Command = m_Command;
        a_Command.m_CommandArgs = m_CommandArgs;
              
        a_Command.m_String = ( m_CommandArgs[ m_CommandArgs.length - 1 ].equalsIgnoreCase( "{$String}" ) );
               
        // Split the line Alias | Alias | Alias
        String[] aliasSplit;
        
        if( aliasLine.contains( "|" ) )
        {
            aliasSplit = aliasLine.split( "\\|" );
        }
        else
        {
            aliasSplit = new String[1];
            aliasSplit[0] = aliasLine;
        }
        
        // Store Aliasses
        for( int i = 0; i < aliasSplit.length; i++ )
        {
            ProcessCommand( aliasSplit[i] );
            
            CommandEditAlias a_Alias = new CommandEditAlias();
       
            a_Alias.m_Alias = m_Command;
            a_Alias.m_AliasArgs = m_CommandArgs;
            
            a_Alias.m_Function = m_Command.matches( "(\\[).*?(\\])" );

            a_Command.m_Alias.add( a_Alias );
            
        }
                
        m_CommandList.add( a_Command );
        
        if( m_Base.isDebugging() )
        { 
            m_CommandList.get( m_CommandList.size() - 1 ).PrintContent( m_Base.getLogger() );
        }
        
        return a_Command;
    }
    
    private void ProcessCommand( String a_Line )
    {
        // Remove spaces at start and end ( if they are there )
        if( a_Line.indexOf( " " ) == 0 )                          a_Line = a_Line.substring( 1 ); 
        if( a_Line.lastIndexOf( " " ) == a_Line.length() - 1 )    a_Line = a_Line.substring( 0, a_Line.length() - 1 );
    
        // Split into command + args
        m_CommandArgs = a_Line.split( " " );
        
        m_Command = m_CommandArgs[0];
        
        if( m_CommandArgs.length > 1 )
        { 
            m_CommandArgs = Arrays.copyOfRange( m_CommandArgs, 1, m_CommandArgs.length ); 
        }
        else
        {
            m_CommandArgs[0] = "";
        }
    } 
}
