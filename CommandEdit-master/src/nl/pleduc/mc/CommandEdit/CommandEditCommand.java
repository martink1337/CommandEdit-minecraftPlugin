// ------------------------------------------------------------------------ //
//                              File Reference                              //
// Author: Patrick le Duc                                                   //
// Filename: CommandEditCommand.java                                        //
// License: GNU GPLv2                                                       //
// Description:                                                             //
// Simple structure in which the command edit commands are stored           //
//                                                                          //
// ------------------------------------------------------------------------ //

package nl.pleduc.mc.CommandEdit;

import java.util.logging.Logger;
import java.util.ArrayList;

public class CommandEditCommand 
{
    public String   m_Command;
    public String[] m_CommandArgs;
    
    public ArrayList< CommandEditAlias > m_Alias;
            
    public boolean m_String;
    
    // Debugging Purposes
    void PrintContent( Logger a_Logger )
    {
        String CommandLine = m_Command;
        for( int i = 0; i < m_CommandArgs.length; i++ )
        {
            CommandLine += " " + m_CommandArgs[i];
        }
        a_Logger.info( "Registered Command: " + CommandLine ); 
        
        for( int i = 0; i < m_Alias.size(); i++ )
        {
            CommandEditAlias a_Alias = m_Alias.get( i );
            String AliasLine = a_Alias.m_Alias;
            for( int j = 0; j < a_Alias.m_AliasArgs.length; j++ )
            {
                AliasLine += " " + a_Alias.m_AliasArgs[j];
            }
            a_Logger.info( "Registered Alias: " + AliasLine + (a_Alias.m_Function?" - Function":"") );
        }
    }
}
