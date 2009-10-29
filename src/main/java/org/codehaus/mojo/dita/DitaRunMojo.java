package org.codehaus.mojo.dita;

/*
 * Copyright 2000-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Executes Dita Java command line to transform dita files to desired output format
 * 
 * @goal run
 * @requiresProject false
 */
public class DitaRunMojo
    extends AbstractDitaMojo
{

    /**
     * key/value pairs to be used to create  /key:value dita-ot java command line argument
     * To see a list of all possible key/value run mvn dita:dita-help -Dditadir=path/to/dita-ot
     * @parameter
     */
    private Map<String, String> ditaProperties = new HashMap<String, String>();

    public void execute()
        throws MojoExecutionException
    {
        initialize();

        Commandline cl = new Commandline( "java" );
        
        cl.setWorkingDirectory( project.getBasedir() );
        
        setupDitaMainClass( cl );
        
        setupDitaArguments( cl );
        
        setupClasspathEnv( cl );
        
        executeCommandline( cl );
        
    }

    
    private void setupDitaArguments( Commandline cl )
        throws MojoExecutionException
    {
        ArrayList<String> params = new ArrayList<String>();

        try
        {
            ditaProperties.put( "tempdir", this.tempdir.getCanonicalPath() );
            ditaProperties.put( "ditadir", this.ditadir.getCanonicalPath() );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }

        for ( Iterator<String> i = ditaProperties.keySet().iterator(); i.hasNext(); )
        {
            String key = i.next();
            String value = ditaProperties.get( key );
            if ( value != null )
            {
                String param = "/" + key + ":" + value;
                params.add( param );
                this.getLog().debug( "Add command argument: " + param );
            }
        }
        
        cl.addArguments( params.toArray( new String[0] ) );

    }
    

    
}
