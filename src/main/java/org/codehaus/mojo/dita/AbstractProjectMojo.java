package org.codehaus.mojo.dita;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.mojo.truezip.Fileset;
import org.codehaus.mojo.truezip.util.TrueZip;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;

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
 * Base class of all dita-maven-plugin's MOJOs
 */
public abstract class AbstractProjectMojo
    extends AbstractMojo
{

    /**
     * Skip the execution
     * 
     * @parameter expression="${dita.skip}" default-value="${false}"
     * @since 1.0-alpha-1
     */
    protected boolean skip;

    /**
     * Internal
     * 
     * @parameter expression="${project}"
     * @since 1.0-alpha-1
     */
    protected MavenProject project;

    /**
     * Internal Maven component to install/deploy the installer(s)
     * 
     * @component
     * @readonly
     */
    protected MavenProjectHelper projectHelper;

    /**
     * Internal component for archiving purposes
     * 
     * @component
     * @since alpha-1
     */
    protected TrueZip truezip;
    
    
    
    /**
     * Ant key/value pair properties. 
     * <p>Default properties for all dita's goals
     *   <ul>
     *     <li>dita.dir=${env.DITA_OT}</li>
     *   </ul>
     * </p>
     * <p>
     *   <ul>
     *   <li>basedir=${project.basedir}</li>
     *   <li>output.dir=${project.build.directory}/dita/out</li>
     *   <li>dita.temp.dir=${project.build.directory}/dita/temp</li>
     *   <li>args.logdir=${project.build.directory}/dita/log</li>
     *   <li>args.input=${project.basedir}/src/main/dita/${artifactId}.ditamap</li>
     *   <li>transtype=pdf</li>
     *   </ul>
     * </p>
     *   
     * @parameter
     * @since since 1.0-beta-1
     */
    protected Map<String, String> antProperties = new HashMap<String, String>();
    

    protected void executeCommandline( Commandline cl )
        throws MojoExecutionException
    {
        int ok;

        try
        {
            DefaultConsumer stdout = new DefaultConsumer();

            DefaultConsumer stderr = stdout;

            this.getLog().debug( cl.toString() );

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

    protected void archiveAndAttachTheOutput( File outputDirectory, String classifier, String type )
        throws MojoExecutionException
    {
        String archiveOutputFileName = this.project.getArtifactId();
        if ( ! StringUtils.isBlank( classifier ) )
        {
            archiveOutputFileName += "-" + classifier;
        }
        archiveOutputFileName += "." + type;

        File archiveOutputFile = new File( this.project.getBuild().getDirectory(), archiveOutputFileName );

        checkForDuplicateAttachArtifact( archiveOutputFile );

        Fileset fileset = new Fileset();
        fileset.setDirectory( outputDirectory.getAbsolutePath() );
        fileset.setOutputDirectory( archiveOutputFile.getAbsolutePath() );
        try
        {
            truezip.copy( fileset );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }

        attachArtifact( classifier, type, archiveOutputFile );

    }
    
    protected void attachArtifact( String classifier, String type, File file )
    {
        if ( StringUtils.isBlank( classifier ) )
        {
            projectHelper.attachArtifact( project, type, file );
        }
        else
        {
            projectHelper.attachArtifact( project, type, classifier, file );
        }
    }

    protected void checkForDuplicateAttachArtifact( File attachFile )
        throws MojoExecutionException
    {
        List attachedArtifacts = project.getAttachedArtifacts();

        Iterator iter = attachedArtifacts.iterator();

        while ( iter.hasNext() )
        {
            Artifact artifact = (Artifact) iter.next();
            if ( attachFile.equals( artifact.getFile() ) )
            {
                throw new MojoExecutionException( "Duplicate file attachment found: " + attachFile );
            }
        }
    }

}
