package org.codehaus.mojo.dita;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Base class of all dita-maven-plugin's DITA specific MOJOs
 */
public abstract class AbstractDitaMojo
    extends AbstractProjectMojo {
    /**
     * Add jar files under DITA Open Toolkit's lib directory to execution classpath
     * 
     * @parameter property="dita.useDitaClasspath" default-value="true"
     * @since 1.0-beta-1
     */
    protected boolean useDitaClasspath;

    /**
     * Ant key/value pair properties.
     * 
     * Default properties for all dita's goals
     * <ul>
     * <li>dita.dir=${env.DITA_HOME}</li>
     * </ul>
     * Default properties for <i>dita:run</i> goal
     * <ul>
     * <li>basedir=${project.basedir}</li>
     * <li>output.dir=${project.build.directory}/dita/out</li>
     * <li>dita.temp.dir=${project.build.directory}/dita/temp</li>
     * <li>args.logdir=${project.build.directory}/dita/log</li>
     * <li>args.input=${project.basedir}/src/main/dita/${artifactId}.ditamap</li>
     * </ul>
     * 
     * @parameter
     * @since 1.0-beta-1
     */
    protected Map<String, String> antProperties = new HashMap<String, String>();

    // //////////////////////////////////////////////////////////////////////
    // internal
    // //////////////////////////////////////////////////////////////////////

    /**
     * Internal. Compile time project dependencies to be added to Ant's classpath
     * 
     * @parameter default-value="${project.compileClasspathElements}"
     * @readonly
     * @since 1.0-beta-1
     */
    private List<String> classpathElements;

    /**
     * Internal. Plugin's dependencies to be added to Ant's classpath
     * 
     * @parameter default-value="${plugin}"
     * @readonly
     * @since 1.0-beta-4
     */
    private PluginDescriptor plugin;

    protected File ditaDirectory;

    protected void setupDitaDirectory()
        throws MojoExecutionException {
        if ( antProperties.get( "dita.dir" ) == null ) {
            antProperties.put( "dita.dir", System.getenv( "DITA_HOME" ) );
        }

        if ( antProperties.get( "dita.dir" ) == null ) {
            throw new MojoExecutionException( "antProperties' dita.dir or env.DITA_HOME configuration not set." );
        }

        this.ditaDirectory = new File( antProperties.get( "dita.dir" ) );

        if ( !ditaDirectory.isDirectory() ) {
            throw new MojoExecutionException( "DITA Open Toolkit at " + ditaDirectory + " not found. " );
        }
    }

    /**
     * setup CLASSPATH env so that Ant can use it
     * 
     * @param cl
     */
    protected void setupClasspathEnv( Commandline cl ) {
        String classpath = this.buildClasspathString();
        cl.addEnvironment( "CLASSPATH", classpath );
        this.getLog().debug( "CLASSPATH: " + classpath );
    }

    /**
     * Create classpath value
     * 
     * @return String
     */
    @SuppressWarnings("unchecked")
    protected String buildClasspathString() {

        StringBuilder classpath = new StringBuilder();

        // Pick up dependency list from plugin configuration
        // Ignore this work for maven2 since it set getArtifacts() to null
        // maven 2 user must compile time dependencies instead
        if ( plugin.getArtifacts() != null ) {
            for ( Artifact artifact : (List<Artifact>) plugin.getArtifacts() ) {
                classpath.append( artifact.getFile() ).append( File.pathSeparator );
            }
        }

        // Pick up dependency list.
        Iterator<String> it = classpathElements.iterator();
        while ( it.hasNext() ) {
            String cpElement = it.next();
            if ( cpElement.endsWith( ".jar" ) ) {
                classpath.append( cpElement ).append( File.pathSeparator );
            }
        }

        // starting ditaot 1.5.4, dita.dir/lib must be on classpath to pickup configuration's files
        File ditaLibDir = new File( this.ditaDirectory.getAbsolutePath(), "lib" );
        classpath.append( ditaLibDir.getAbsolutePath() ).append( File.pathSeparator );

        if ( this.useDitaClasspath ) {
            FileSetManager fileSetManager = new FileSetManager( this.getLog(), false );

            FileSet fileSet = new FileSet();
            fileSet.setDirectory( this.ditaDirectory.getAbsolutePath() + "/lib" );
            ArrayList<String> includes = new ArrayList<String>();
            includes.add( "**/*.jar" );
            fileSet.setIncludes( includes );

            String[] files = fileSetManager.getIncludedFiles( fileSet );

            for ( int i = 0; i < files.length; ++i ) {
                File jarFile = new File( fileSet.getDirectory(), files[i] );
                classpath.append( jarFile.getAbsolutePath() ).append( File.pathSeparator );
            }
        }

        return classpath.toString();
    }

    protected void setupDitaMainClass( Commandline cl ) {
        cl.createArg().setValue( "org.dita.dost.invoker.CommandLineInvoker" );
    }

}
