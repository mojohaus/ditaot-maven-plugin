package org.codehaus.mojo.dita;

import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * Used by dita:run to check for Ant execution error
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
