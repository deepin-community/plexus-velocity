package org.codehaus.plexus.velocity;

/*
 * Copyright 2001-2016 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Enumeration;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.LogChute;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * A simple velocity component implementation.
 * <p/>
 * A typical configuration will look like this:
 * <pre>
 *      <configuration>
 *        <properties>
 *          <property>
 *            <name>resource.loader</name>
 *            <value>classpath</value>
 *          </property>
 *          <property>
 *            <name>classpath.resource.loader.class</name>
 *            <value>org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader</value>
 *          </property>
 *        </properties>
 *      </configuration>
 * </pre>
 */
public class DefaultVelocityComponent
    extends AbstractLogEnabled
    implements VelocityComponent, Initializable, LogChute
{
    private VelocityEngine engine;

    private Properties properties;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        engine = new VelocityEngine();

        // avoid "unable to find resource 'VM_global_library.vm' in any resource loader."
        engine.setProperty( RuntimeConstants.VM_LIBRARY, "" );

        engine.setProperty( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, this );

        if ( properties != null )
        {
            for ( Enumeration<?> e = properties.propertyNames(); e.hasMoreElements(); )
            {
                String key = e.nextElement().toString();

                String value = properties.getProperty( key );

                engine.setProperty( key, value );

                getLogger().debug( "Setting property: " + key + " => '" + value + "'." );
            }
        }

        try
        {
            engine.init();
        }
        catch ( Exception e )
        {
            throw new InitializationException( "Cannot start the Velocity engine", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public VelocityEngine getEngine()
    {
        return engine;
    }
    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private RuntimeServices runtimeServices;

    public void init( RuntimeServices runtimeServices )
    {
        this.runtimeServices = runtimeServices;
    }

    public void log(int level, String message)
    {
        switch ( level )
        {
            case LogChute.WARN_ID:
                getLogger().warn( message );
                break;
            case LogChute.INFO_ID:
                getLogger().info( message );
                break;
            case LogChute.DEBUG_ID:
            case LogChute.TRACE_ID:
                getLogger().debug( message );
                break;
            case LogChute.ERROR_ID:
                getLogger().error( message );
                break;
            default:
                getLogger().debug( message );
                break;
        }
    }

    public void log(int level, String message, Throwable t)
    {
        switch ( level )
        {
            case LogChute.WARN_ID:
                getLogger().warn( message, t );
                break;
            case LogChute.INFO_ID:
                getLogger().info( message, t );
                break;
            case LogChute.DEBUG_ID:
            case LogChute.TRACE_ID:
                getLogger().debug( message, t );
                break;
            case LogChute.ERROR_ID:
                getLogger().error( message, t );
                break;
            default:
                getLogger().debug( message, t );
                break;
        }
    }

    public boolean isLevelEnabled( int level )
    {
         switch ( level )
        {
            case LogChute.WARN_ID:
                return getLogger().isWarnEnabled();
            case LogChute.INFO_ID:
                return getLogger().isInfoEnabled();
            case LogChute.DEBUG_ID:
            case LogChute.TRACE_ID:
                return getLogger().isDebugEnabled();
            case LogChute.ERROR_ID:
                return getLogger().isErrorEnabled();
            default:
                return getLogger().isDebugEnabled();
        }
    }
}
