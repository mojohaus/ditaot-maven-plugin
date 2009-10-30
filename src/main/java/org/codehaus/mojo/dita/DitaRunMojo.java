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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Execute Dita Java command line to transform dita files to desired output format.
 * 
 * @goal run
 * @requiresProject false
 */
public class DitaRunMojo
    extends AbstractDitaMojo
{
    /**
     * DITA Open Toolkit's main topic file This parameter is ignored if exists in
     * <i>ditaProperties</i> via /i property
     * 
     * @parameter expression="${dita.topicfile}"
     *            default-value="${basedir}/src/main/dita/${project.artifactId}.ditamap"
     * @since 1.0-alpha-1
     *            
     */
    private File topicfile;

    /**
     * DITA Open Toolkit's transtype
     * This parameter is ignored if exists in <i>ditaProperties</i>
     * 
     * @parameter expression="${dita.transtype}" default-value="pdf" 
     * @since 1.0-alpha-1
     * 
     */
    private String transtype;

    /**
     * key/value pairs to be used to create /key:value dita-ot java command line argument To see a
     * list of all possible key/value run mvn dita:dita-help -Dditadir=path/to/dita-ot
     * 
     * @parameter
     * @since 1.0-alpha-1
     */
    private Map<String, String> ditaProperties = new HashMap<String, String>();
    
    /**
     * Use DITA Open Toolkit's tools/ant
     * 
     * @parameter expression="${dita.useDitaAnt}" default-value="true" 
     * @since 1.0-alpha-1
     * 
     */
    private boolean useDitaAnt;

    public void execute()
        throws MojoExecutionException
    {
        initialize();

        Commandline cl = new Commandline( "java" );

        cl.setWorkingDirectory( project.getBasedir() );

        setupDitaMainClass( cl );

        setupDitaArguments( cl );

        setupClasspathEnv( cl );

        if ( useDitaAnt )
        {
            setupDitaOpenToolkitAnt( cl );
        }

        executeCommandline( cl );

    }

    private void mergeDitaProperty( String name, String value )
    {
        if ( ditaProperties.get( name ) == null )
        {
            ditaProperties.put( name, value );
        }
    }

    private void setupDitaArguments( Commandline cl )
        throws MojoExecutionException
    {
        ArrayList<String> params = new ArrayList<String>();

        mergeDitaProperty( "tempdir", this.tempdir.getAbsolutePath() );
        mergeDitaProperty( "ditadir", this.ditadir.getAbsolutePath() );
        mergeDitaProperty( "outdir", this.outdir.getAbsolutePath() );
        mergeDitaProperty( "i", this.topicfile.getAbsolutePath() );
        mergeDitaProperty( "transtype", this.transtype );

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

    private void setupDitaOpenToolkitAnt( Commandline cl )
        throws MojoExecutionException
    {
        try
        {
            File antHome = new File( this.ditadir, "tools/ant" );
            File antBin = new File( antHome, "bin" );
            
            String antPath = antHome.getCanonicalPath();
            cl.addEnvironment( "ANT_HOME", antPath );
            this.getLog().debug(  "ANT_HOME=" + antPath );

            String newPath = antBin.getCanonicalPath() + File.pathSeparator + System.getenv( "PATH" );
            cl.addEnvironment( "PATH", newPath );
            this.getLog().debug(  "PATH=" + newPath );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }
}
