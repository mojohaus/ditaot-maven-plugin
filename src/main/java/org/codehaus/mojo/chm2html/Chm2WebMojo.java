package org.codehaus.mojo.chm2html;

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

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.dita.AbstractProjectMojo;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Convert htmlhelp's chm format to pure html format using chm2web utility from A!K Research Labs.
 * 
 * @goal chm2web
 * @requiresProject false
 */
public class Chm2WebMojo
    extends AbstractProjectMojo
{

    /**
     * @parameter expression="${chm2web.exe}"
     *            default-value="c:\\Program Files\\A!K Research Labs\\chm2web\\chm2web.exe"
     * @since 1.0-alpha-1
     */
    private File chm2webExe;

    /**
     * Allow skipping the conversion when chm2web is not available.
     * 
     * @parameter expression="${chm2web.ignoreIfExeNotExist}" default-value="false"
     * @since 1.0-alpha-1
     */
    private boolean ignoreIfChm2WebNotExist;

    /**
     * Chm2Web configuration file
     * @parameter expression="${chm2web.descriptor}"
     *            default-value="${basedir}/${project.artifactId}.chm2web"
     * @since 1.0-alpha-1
     */
    private File descriptor;

    public void execute()
        throws MojoExecutionException
    {
        if ( skip )
        {
            this.getLog().info( "Skipped" );
        }
        
        if ( !chm2webExe.exists() && ignoreIfChm2WebNotExist )
        {
            return;
        }

        Commandline cl = new Commandline();

        cl.setExecutable( chm2webExe.getAbsolutePath() );
        cl.createArg().setFile( descriptor );
        cl.createArg().setValue( "/q" );
        cl.createArg().setValue( "/d" );

        cl.setWorkingDirectory( project.getBasedir() );

        executeCommandline( cl );
    }

}
