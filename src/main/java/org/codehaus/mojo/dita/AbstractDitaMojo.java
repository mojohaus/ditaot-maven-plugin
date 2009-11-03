package org.codehaus.mojo.dita;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.plexus.util.cli.Commandline;

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

/**
 * Base class of all dita-maven-plugin's DITA specific MOJOs
 */
public abstract class AbstractDitaMojo
    extends AbstractProjectMojo
{
    /**
     * DITA Open Toolkit directory. If not given, ${env.DITA_OT} will be used
     * This parameter is ignored if exists in <i>antProperties</i> in dita:run goal under <i>dita.dir</i> property
     * 
     * @parameter expression="${dita.ditadir}"
     * @since 1.0-alpha-1
     */
    protected File ditaDirectory;
    
    /**
     * Add jar file under DITA Open Toolkit's lib directory to classpath
     * 
     * @parameter expression="${dita.useDitaClasspath}" default-value="true"
     * @since 1.0-alpha-1
     */
    protected boolean useDitaClasspath;
    
    /**
     * Internal
     * @parameter expression="${project.compileClasspathElements}"
     * @since 1.0-alpha-1
     */
    protected List<String> classpathElements;

    protected void setupDitaDirectory()
    {
        if ( ditaDirectory == null )
        {
            String tmp = System.getenv( "DITA_OT" );
            if ( tmp != null )
            {
                ditaDirectory = new File( tmp );
            }
        }
        
    }

    protected void validateDitaDirectory()
        throws MojoExecutionException
    {

        if ( ditaDirectory == null )
        {
            throw new MojoExecutionException( "ditadir or env.DITA_OT configuration not set." );
        }

        if ( !ditaDirectory.isDirectory() )
        {
            throw new MojoExecutionException( "DITA Open Toolkit at " + ditaDirectory + " not found. " );
        }

    }

    /**
     * setup CLASSPATH env so that Ant can use it
     * 
     * @param cl
     */
    protected void setupClasspathEnv( Commandline cl )
    {
        String classpath = this.buildClasspathString();
        cl.addEnvironment( "CLASSPATH", classpath );
        this.getLog().debug( "CLASSPATH: " + classpath );
    }

    /**
     * Create classpath value
     * 
     * @return String
     */
    protected String buildClasspathString()
    {

        StringBuilder classpath = new StringBuilder();

        //Pick up dependency list. 
        Iterator<String> it = classpathElements.iterator();
        while ( it.hasNext() )
        {
            String cpElement = it.next();
            classpath.append( cpElement ).append( File.pathSeparator );
        }

        if ( this.useDitaClasspath )
        {
            FileSetManager fileSetManager = new FileSetManager( this.getLog(), false );

            FileSet fileSet = new FileSet();
            fileSet.setDirectory( this.ditaDirectory.getAbsolutePath() + "/lib" );
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
        
        return classpath.toString();
    }

    protected void setupDitaMainClass( Commandline cl )
    {
        cl.createArg().setValue( "org.dita.dost.invoker.CommandLineInvoker" );
    }


}
