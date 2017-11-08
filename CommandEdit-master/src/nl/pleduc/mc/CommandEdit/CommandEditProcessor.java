// ------------------------------------------------------------------------ //
//                              File Reference                              //
// Author: Patrick le Duc                                                   //
// Filename: CommandEditProcessor.java                                      //
// License: GNU GPLv2                                                       //
// Description:                                                             //
// Processes commands and translates them to their alias if needed          //
//                                                                          //
// ------------------------------------------------------------------------ //

package nl.pleduc.mc.CommandEdit;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


public class CommandEditProcessor 
{
    CommandEdit m_Base;
    CommandEditFileLoader m_FileLoader;
    
    CommandEditProcessor( CommandEdit base, CommandEditFileLoader fileloader )
    {
        m_Base = base;
        m_FileLoader = fileloader;
    }
    
    boolean ProcessCommand( PlayerCommandPreprocessEvent event )
    {
        String returnCommand = event.getMessage();
        String Command = event.getMessage();
        String[] Args;
        
        // Split into command + args
        Args = Command.split( " " );

        Command = Args[0];

        if( Args.length > 1 )
        { 
            Args = Arrays.copyOfRange( Args, 1, Args.length ); 
        }
        else
        {
            Args[0] = "";
        }
        
        // Get the list of commands
        ArrayList< CommandEditCommand > a_CommandList = m_FileLoader.GetCommandList();
        
        CommandEditCommand a_Command = null;
        int index = -1;
        
        // Loop through all commands to see if we have a match
        for( int i = 0; i < a_CommandList.size(); i++ )
        {
            if( a_CommandList.get( i ).m_Command.equalsIgnoreCase( Command ) )
            {
                a_Command = a_CommandList.get( i );
                
                // Found a command which matches the patern - process it
                if( a_Command.m_CommandArgs.length == Args.length || a_Command.m_String )
                {
                    index = i;
                    break;
                }
                
            }
        }
        
        // If match found dispatch all alias commands
        if( index >= 0 )
        {
            // For each alias
            for( int k = 0; k < a_Command.m_Alias.size(); k++ )
            {
                CommandEditAlias a_CEA = a_Command.m_Alias.get( k );
                String m_Alias = a_CEA.m_Alias;
                String[] m_AliasArgs = a_CEA.m_AliasArgs;
                
                // Form new command
                returnCommand = m_Alias;
                for( int i = 0; i < m_AliasArgs.length; i++ )
                {
                    String AA = m_AliasArgs[i];

                    // If an argument matches a pre-defined variable type {$Example}
                    if( AA.matches( "(\\{)(\\$).*?(\\})") )
                    {                    
                        if( AA.equalsIgnoreCase( "{$PlayerName}" ) ){ AA = event.getPlayer().getName(); }
                        else if( AA.equalsIgnoreCase( "{$DisplayName}" ) ){ AA = event.getPlayer().getName(); }
                        else if( AA.equalsIgnoreCase( "{$WorldName}" ) ){ AA = event.getPlayer().getWorld().getName(); }
                        // Special case, add all args after this to the command
                        else if( AA.equalsIgnoreCase( "{$String}" ) )
                        {
                            int idx = -1;
                            for( int j = 0; j < a_Command.m_CommandArgs.length; j++ )
                            {
                                if( a_Command.m_CommandArgs[j].equalsIgnoreCase( AA ) )
                                {
                                    idx = j;
                                    break;
                                }
                            }
                            AA = Args[ idx ];
                            for( int j = idx + 1; j < Args.length; j++ )
                            {
                                AA += " " + Args[j];
                            }
                        }   
                    }
                    // If an argument is a variable {Example}
                    else if( AA.matches( "(\\{).*?(\\})") )
                    {
                        int idx = -1;

                        for( int j = 0; j < a_Command.m_CommandArgs.length; j++ )
                        {
                            if( a_Command.m_CommandArgs[j].equalsIgnoreCase( AA ) )
                            {
                                idx = j;
                                break;
                            }
                        }
                        if( idx < 0 )
                        {
                            m_Base.getLogger().info( "Encountered an error with Command Edit command: " + Command );
                        }

                        // Set the alias argument to the value found
                        AA = Args[ idx ];
                    }

                    // Add the alias arg to the command line
                    returnCommand += ( " " + AA );

                    // Debugging Purposesu
                    if( m_Base.isDebugging() ){ m_Base.getLogger().info( "Replaced: " + m_AliasArgs[i] + " with " + AA ); }       
                }

                // Debugging Purposes
                if( m_Base.isDebugging() ){ m_Base.getLogger().info( "Altered Command: " + returnCommand ); }
                
                // If function handle the function else dispatch the command
                if( a_CEA.m_Function )
                {
                    // Process the function
                    ProcessFunction( returnCommand, event );
                }
                else
                {
                    //Dispatch each Alias
                    returnCommand = returnCommand.replaceFirst( "/", "" );
                    m_Base.getServer().dispatchCommand( event.getPlayer(), returnCommand );
                }
            }
            
            return true;
            
        }
        
        return false;
    }
    
    void ProcessFunction( String a_CommandLine, PlayerCommandPreprocessEvent event )
    {
        String Function = a_CommandLine;
        String[] Args;
        
        // Split into command + args
        Args = Function.split( " " );

        Function = Args[0];

        if( Args.length > 1 )
        { 
            Args = Arrays.copyOfRange( Args, 1, Args.length ); 
        }
        else
        {
            Args[0] = "";
        }
        
        // Chatas {TargetPlayer} {String} + need to be OP for this command
        if( Function.equalsIgnoreCase( "[chatas]" ) && event.getPlayer().isOp() )
        { 
            // Find Target player
            Player a_TargetPlayer = m_Base.getServer().getPlayer( Args[0] );
            
            if( a_TargetPlayer != null )
            {
                // Concatenate the message
                String Message = Args[1];
                for( int i = 2; i < Args.length; i++ ){ Message += " " + Args[i]; }

                // Debugging
                if( m_Base.isDebugging() ){ m_Base.getLogger().info( "Chatas command fired, Target: " + Args[0] + ", Message: " + Message ); }

                // Run the Chat As
                a_TargetPlayer.chat( Message );
            }
            else { if( m_Base.isDebugging() ){ m_Base.getLogger().info( "Targetplayer: " + Args[0] + " not found..." );  } }
        }
        // Message
        else if( Function.equalsIgnoreCase( "[message]" ) )
        {
            // Concatenate the message
            String Message = Args[0];
            for( int i = 1; i < Args.length; i++ ){ Message += " " + Args[i]; }
            
            event.getPlayer().sendMessage( Message );
        }
        // KickSelf
        else if( Function.equalsIgnoreCase( "[kickself]" ) )
        {
            // Concatenate the message
            String Message = Args[0];
            for( int i = 1; i < Args.length; i++ ){ Message += " " + Args[i]; }
            event.getPlayer().kickPlayer( Message );
        }
        else if( Function.equalsIgnoreCase( "[kickother]" ) && event.getPlayer().isOp() )
        {
            // Find Target player
            Player a_TargetPlayer = m_Base.getServer().getPlayer( Args[0] );
            
            if( a_TargetPlayer != null )
            {
                // Concatenate the message
                String Message = Args[1];
                for( int i = 2; i < Args.length; i++ ){ Message += " " + Args[i]; }

                // Run the Chat As
                a_TargetPlayer.chat( Message );
            }
            else { if( m_Base.isDebugging() ){ m_Base.getLogger().info( "Targetplayer: " + Args[0] + " not found..." );  } }
        }
        else if( Function.equalsIgnoreCase( "[killself]" ) )
        {
            event.getPlayer().setHealth( 0 );
        }
        else if( Function.equalsIgnoreCase( "[killother]") )
        {
            // Find Target player
            Player a_TargetPlayer = m_Base.getServer().getPlayer( Args[0] );
            
            if( a_TargetPlayer != null )
            {
                a_TargetPlayer.setHealth( 0 );
            }
            else { if( m_Base.isDebugging() ){ m_Base.getLogger().info( "Targetplayer: " + Args[0] + " not found..." );  } }
        }
        
    }
}
