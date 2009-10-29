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
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.plexus.util.cli.Arg;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;

/**
 * Executes Dita Java command line
 * 
 * @goal dita
 */
public class DitaMojo
    extends AbstractMojo
{
    /**
     * DITA Open Toolkit directory. If not given, ${env.DITA_OT} will be used
     * 
     * @parameter
     */
    private File ditadir;

    /**
     * DITA Open Toolkit's tempdir
     * 
     * @parameter default-value="${project.build.directory}/dita/temp"
     */
    private File tempdir;

    /**
     * DITA Open Toolkit's java command line argument
     * @parameter
     */
    private Map<String, String> ditaProperties = new HashMap<String, String>();

    /**
     * @parameter expression="${project.compileClasspathElements}"
     */
    private List<String> classpathElements;

    /**
     * @parameter expression="${plugin.artifacts}"
     */
    private List<Artifact> pluginArtifacts;
    
    /**
     * @parameter expression="${project}"
     */
    private MavenProject project;
    
    /**
     * Add jar file under DITA Open Toolkit's lib directory to classpath
     * @parameter default-value="true"
     */
    private boolean useDitaClasspath;
    

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

    private void initialize()
        throws MojoExecutionException
    {
        if ( ditadir == null )
        {
            String tmp = System.getenv( "DITA_OT" );
            if ( tmp != null )
            {
                ditadir = new File( tmp );
            }
        }

        if ( ditadir == null )
        {
            throw new MojoExecutionException( "ditadir or env.DITA_OT configuration not set." );
        }

        if ( !ditadir.isDirectory() )
        {
            throw new MojoExecutionException( "DITA Open Toolkit at " + ditadir + " not found. " );
        }

    }

    /**
     * setup CLASSPATH env so that ant can use it
     * @param cl
     */
    private void setupClasspathEnv( Commandline cl )
    {
        String classpath = this.buildClasspathString() ;
        cl.addEnvironment( "CLASSPATH", classpath );
        this.getLog().info(  "CLASSPATH: " + classpath );
    }
    
    /**
     * Create classpath value
     * @return
     */
    private String buildClasspathString()
    {
        
        StringBuilder classpath = new StringBuilder();
        
        if ( this.useDitaClasspath )
        {
            FileSetManager fileSetManager = new FileSetManager( this.getLog(), false );

            FileSet fileSet = new FileSet();
            fileSet.setDirectory( this.ditadir.getAbsolutePath() + "/lib" );
            ArrayList<String> includes = new ArrayList<String>();
            includes.add( "**/*.jar" );
            fileSet.setIncludes( includes );
            
            String[] files = fileSetManager.getIncludedFiles( fileSet );
            
            for ( int i = 0; i < files.length; ++i )
            {
                File jarFile = new File( fileSet.getDirectory(), files[i] );
                classpath.append( jarFile.getAbsolutePath() ).append( File.pathSeparator );
            }            
        }
        
        Iterator<String> it = classpathElements.iterator();
        while ( it.hasNext() )
        {
            String cpElement = it.next();
            classpath.append( cpElement ).append( File.pathSeparator );
        }

        
        Iterator<Artifact> iter = pluginArtifacts.iterator();

        while ( iter.hasNext() )
        {
            Artifact artifact = (Artifact) iter.next();
            classpath.append( artifact.getFile().getPath() ).append( File.pathSeparator );
        }
        
        
        return classpath.toString();
    }

    private void setupDitaMainClass( Commandline cl )
    {
        Arg arg = cl.createArg();
        arg.setValue( "org.dita.dost.invoker.CommandLineInvoker" );
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
                this.getLog().info( "Add command argument: " + param );
            }
        }
        
        cl.addArguments( params.toArray( new String[0] ) );

    }
    
    private void executeCommandline( Commandline cl )
        throws MojoExecutionException
    {
        int ok;

        try
        {
            DefaultConsumer stdout = new DefaultConsumer();

            DefaultConsumer stderr = stdout;

            this.getLog().info( cl.toString() );

            ok = CommandLineUtils.executeCommandLine( cl, stdout, stderr );
        }
        catch ( CommandLineException ecx )
        {
            throw new MojoExecutionException( "Error executing command line", ecx );
        }

        if ( ok != 0 )
        {
            throw new MojoExecutionException( "Error executing command line. Exit code:" + ok );
        }
        
    }
    
}
