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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * <p>
 * Execute DITA Open Toolkit's Ant command line to transform dita files to desired output format.
 * </p>
 * <p>
 * Behind the scene, <i>antProperties</i> are temporarily stored under ${logDirectory}/properties.temp to be used with
 * </p>
 * <p>
 *    ant -f ${ditaDirectory}/build.xml -propertyFile ${logDirectory}/properties.temp
 * </p>
 * 
 * 
 * @goal run
 * @requiresProject false
 * @requiresDependencyResolution compile
 */
public class DitaRunMojo
    extends AbstractDitaMojo
{

    /**
     * DITA Open Toolkit's main ditamap file. This parameter is ignored if exists in
     * <i>antProperties</i> via <i>args.input</i> property
     * 
     * @parameter expression="${dita.ditamap}"
     *            default-value="${basedir}/src/main/dita/${project.artifactId}.ditamap"
     * @since 1.0-alpha-1
     * 
     */
    private File ditamap;
    
    /**
     * DITA Open Toolkit's basedir. This parameter is ignored if exists in <i>antProperties</i> via
     * <i>basedir</i> property
     * 
     * @parameter expression="${dita.basedir}" default-value="${basedir}"
     * @since 1.0-alpha-1
     * 
     */
    private File basedir;
    
    /**
     * DITA Open Toolkit's tempdir
     * This parameter is ignored if exists in <i>antProperties</i>
     * 
     * @parameter expression="${dita.tempdir}" default-value="${project.build.directory}/dita/temp"
     * @since 1.0-alpha-1
     */
    private File tempDirectory;

    /**
     * DITA Open Toolkit's outdir
     * This parameter is ignored if exists in <i>antProperties</i>
     * 
     * @parameter expression="${dita.outdir}" default-value="${project.build.directory}/dita/out"
     * @since 1.0-alpha-1
     * 
     */
    private File outputDirectory;
    
    
    /**
     * DITA Open Toolkit's transtype. This parameter is ignored if exists in <i>antProperties</i>
     * via <i>transtype</i> property
     * 
     * @parameter expression="${dita.transtype}" default-value="pdf"
     * @since since 1.0-beta-1
     * 
     */
    private String transtype;

    /**
     * DITA Open Toolkit's logdir. This parameter is ignored if exists in <i>antProperties</i> via
     * <i>args.logdir</i> property
     * 
     * @parameter expression="${dita.logdir}" default-value="${project.build.directory}/dita/log"
     * @since since 1.0-beta-1
     * 
     */
    private File logDirectory;

    /**
     * Ant key/value pair properties
     * 
     * @parameter
     * @since since 1.0-beta-1
     */
    private Map<String, String> antProperties = new HashMap<String, String>();

    /**
     * Use DITA Open Toolkit's tools/ant
     * 
     * @parameter expression="${dita.useDitaAnt}" default-value="true"
     * @since since 1.0-beta-1
     * 
     */
    private boolean useDitaAnt;

    /**
     * ANT_OPTS this parameter overrides the current env.ANT_OPTS if given
     * 
     * @parameter expression="${dita.antOpts}"
     * @since since 1.0-beta-1
     * 
     */
    private String antOpts;
    
    /**
     * Controls whether this plugin tries to archive the output directory and attach archive to the
     * project.
     * 
     * @parameter expression="${dita.attach}" default-value="false"
     * @since since 1.0-beta-1
     */
    private boolean attach = false;

    /**
     * Output file classifier to be attached to the project.
     * 
     * @parameter expression="${dita.outputDirectory}" 
     * @since since 1.0-beta-1
     */
    private String attachClassifier;

    /**
     * Output file extension to be attached to the project. When transtype is one of pdf types,
     * the attachType will be hard coded to pdf and not modifiable
     * 
     * @parameter expression="${dita.attachType}" default-value="jar"
     * @since since 1.0-beta-1
     */
    private String attachType;
    

    // /////////////////////////////////////////////////////////////////////////

    public void execute()
        throws MojoExecutionException
    {
        if ( skip )
        {
            this.getLog().info( "Skipped" );
            return;
        }

        initialize();

        Commandline cl = new Commandline();

        cl.setExecutable( getAntExecutable().getAbsolutePath() );
        cl.setWorkingDirectory( project.getBasedir() );

        setupClasspathEnv( cl );

        setupAntEnv( cl );

        setupAntArguments( cl );

        executeCommandline( cl );
        
        if ( attach )
        {
            attachTheOuput();
        }

    }

    private void initialize()
        throws MojoExecutionException
    {
        setupDitaDirectory();

        if ( antProperties.get( "dita.dir" ) != null )
        {
            this.ditaDirectory = new File( antProperties.get( "dita.dir" ) );
        }
        antProperties.put( "dita.dir", this.ditaDirectory.getAbsolutePath() );
        
        validateDitaDirectory();
        
        if ( antProperties.get( "basedir" ) != null )
        {
            this.basedir = new File( antProperties.get( "basedir" ) );
        }
        antProperties.put( "basedir", this.basedir.getAbsolutePath() );
        

        if ( antProperties.get( "output.dir" ) != null )
        {
            this.outputDirectory = new File( antProperties.get( "output.dir" ) );
        }
        antProperties.put( "output.dir", this.outputDirectory.getAbsolutePath() );

        if ( antProperties.get( "dita.temp.dir" ) != null )
        {
            this.tempDirectory = new File( antProperties.get( "dita.temp.dir" ) );
        }
        antProperties.put( "dita.temp.dir", this.tempDirectory.getAbsolutePath() );

        if ( antProperties.get( "args.logdir" ) != null )
        {
            this.logDirectory = new File( antProperties.get( "args.logdir" ) );
        }
        antProperties.put( "args.logdir", this.logDirectory.getAbsolutePath() );

        if ( antProperties.get( "args.input" ) != null )
        {
            this.ditamap = new File( antProperties.get( "args.input" ) );
        }
        antProperties.put( "args.input", this.ditamap.getAbsolutePath() );

        if ( antProperties.get( "transtype" ) != null )
        {
            this.transtype = antProperties.get( "transtype" );
        }
        antProperties.put( "transtype", this.transtype );

    }

    private void setupAntEnv( Commandline cl )
        throws MojoExecutionException
    {
        cl.addEnvironment( "ANT_HOME", this.getAntPath() );

        if ( antOpts != null )
        {
            cl.addEnvironment( "ANT_OPTS", antOpts );
            this.getLog().debug(  "ANT_OPT=" + antOpts );
        }

    }

    private void setupAntArguments( Commandline cl )
        throws MojoExecutionException
    {
        cl.createArg().setValue( "-f" );
        cl.createArg().setValue( getDitaBuildXmlPath() );

        cl.createArg().setValue( "-propertyfile" );
        cl.createArg().setValue( setupAntProperties() );
    }

    /**
     * 
     * @return canonical path to temporary properties ant file
     * @throws MojoExecutionException
     */
    private String setupAntProperties()
        throws MojoExecutionException
    {
        Properties inputProperties = new Properties();

        for ( Iterator<String> i = antProperties.keySet().iterator(); i.hasNext(); )
        {
            String key = i.next();
            String value = antProperties.get( key );
            if ( value != null )
            {
                inputProperties.put( key, value );
            }
        }

        FileOutputStream os = null;
        try
        {
            File tmpFile = new File( this.tempDirectory, "properties.temp" );
            tmpFile.getParentFile().mkdirs();

            os = new FileOutputStream( tmpFile );
            inputProperties.store( os, null );

            return tmpFile.getCanonicalPath();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        finally
        {
            IOUtil.close( os );
        }
    }

    private String getDitaBuildXmlPath()
        throws MojoExecutionException
    {
        try
        {
            return new File( ditaDirectory, "build.xml" ).getCanonicalPath();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    private String getAntPath()
        throws MojoExecutionException
    {
        File antHome = null;

        String antPath = System.getenv( "ANT_HOME" );
        if ( antPath != null )
        {
            antHome = new File( antPath );
        }

        if ( useDitaAnt )
        {
            antHome = new File( this.ditaDirectory, "tools/ant" );
        }

        try
        {
            return antHome.getCanonicalPath();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    private File getAntExecutable()
        throws MojoExecutionException
    {
        String antFileName = "ant";

        if ( Os.isFamily( "windows" ) )
        {
            antFileName += ".bat";
        }

        return new File( this.getAntPath(), "bin/" + antFileName );

    }

    protected void executeCommandline( Commandline cl )
        throws MojoExecutionException
    {
        int ok;

        try
        {
            AntOutputConsumer stdout = new AntOutputConsumer();

            AntOutputConsumer stderr = stdout;

            this.getLog().debug( cl.toString() );

            ok = CommandLineUtils.executeCommandLine( cl, stdout, stderr );

            if ( stdout.isFailure() )
            {
                throw new MojoExecutionException( "BUILD FAILED" );
            }
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
    
    private void attachTheOuput()
        throws MojoExecutionException
    {
        if ( "pdf".equals( this.transtype ) || "pdf2".equals( this.transtype ) || "legacypdf".equals( this.transtype ) )
        {
            attachSingleOuput( attachClassifier, "pdf" );
        }
        else if ( "htmlhelp".equals( this.transtype ) )
        {
            attachSingleOuput( attachClassifier, "pdf" );
        }
        else
        {
            this.archiveAndAttachTheOutput( this.outputDirectory, attachClassifier, attachType );
        }
    }
    
    private void attachSingleOuput( String classifier, String type  )
        throws MojoExecutionException
    {
        String [] tokens = StringUtils.split( ditamap.getName(), "." );
        String fileName = "";
        for ( int i = 0; i < tokens.length - 1 ; ++i )
        {
            fileName += tokens[i] + ".";
        }
        fileName += type;

        File ditaOutputFile = new File( this.outputDirectory, fileName );
        checkForDuplicateAttachArtifact( ditaOutputFile );
        attachArtifact( classifier, type, ditaOutputFile );
        
    }
    

}
