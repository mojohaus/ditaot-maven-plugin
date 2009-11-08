package org.codehaus.mojo.dita;

import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * Used by dita:run to check for Ant execution error
 * This is workaround where Ant does not exit with none zero code when build fails on Windows 
 * platform.
 *
 */
public class AntOutputConsumer
    implements StreamConsumer
{
    private boolean failure;
    
    public void consumeLine( String line )
    {
        System.out.println( line );
        if ( line.contains( "BUILD FAILED" ) )
        {
            this.failure = true;
        }
    }

    public boolean isFailure()
    {
        return failure;
    }
    
    

}
