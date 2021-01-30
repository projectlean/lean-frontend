package org.lean.ui.util;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.WebBrowser;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.logging.LogChannel;
import org.lean.ui.core.PropsUi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvironmentUtils {

    private static final EnvironmentUtils ENVIRONMENT_UTILS = new EnvironmentUtils();
    private static final Pattern MSIE_PATTERN = Pattern.compile( "MSIE (\\d+)" );
    private static final Pattern SAFARI_PATTERN = Pattern.compile( "AppleWebKit\\/(\\d+)" );
    private static final String SUPPORTED_DISTRIBUTION_NAME = "ubuntu";
    public static final String UBUNTU_BROWSER = "Midori";
    public static final String MAC_BROWSER = "Safari";
    public static final String WINDOWS_BROWSER = "MSIE";
    private final ILogChannel log = new LogChannel( this );

    public static synchronized EnvironmentUtils getInstance() {
        return ENVIRONMENT_UTILS;
    }

    /**
     * Checks the available browser to see if it is an unsupported one.
     *
     * @return 'true' if in a unSupported browser environment 'false' otherwise.
     */
    public synchronized boolean isUnsupportedBrowserEnvironment() {
        if ( getEnvironmentName().contains( "linux" ) ) {
            return false;
        }
        final String userAgent = getUserAgent();
        if ( userAgent == null ) {
            return true;
        }
        return checkUserAgent( MSIE_PATTERN.matcher( userAgent ), getSupportedVersion( "min.windows.browser.supported" ) )
                || checkUserAgent( SAFARI_PATTERN.matcher( userAgent ), getSupportedVersion( "min.mac.browser.supported" ) );
    }

    private boolean checkUserAgent(Matcher matcher, int version ) {
        return ( matcher.find() && Integer.parseInt( matcher.group( 1 ) ) < version );
    }

    /**
     * Ask for user Agent of the available browser.
     *
     * @return a string that contains the user agent of the browser.
     */
    protected String getUserAgent() {
        WebBrowser browser = UI.getCurrent().getSession().getBrowser();
        String userAgent = browser.getBrowserApplication();
        return userAgent;
    }

    /**
     * Ask for the Operating system name.
     *
     * @return a string that contains the current Operating System.
     */
    private String getEnvironmentName() {
        String osName = getOsName();
        if ( osName.contentEquals( "linux" ) ) {
            return osName + " " + getLinuxDistribution().toLowerCase();
        }
        return osName;
    }

    protected String getOsName() {
        return System.getProperty( "os.name" ).toLowerCase();
    }

    /**
     * Gets the supported version of the required Property.
     *
     * @param property a string with the required property.
     * @return the value of the requiredProperty.
     */
    protected int getSupportedVersion( String property ) {
        return PropsUi.getInstance().getSupportedVersion( property );
    }

    /**
     * Ask if the browsing environment checks are disabled.
     *
     * @return 'true' if disabled 'false' otherwise.
     */
    public boolean isBrowserEnvironmentCheckDisabled() {
        return PropsUi.getInstance().isBrowserEnvironmentCheckDisabled();
    }

    /**
     * Ask for the running linux distribution.
     *
     * @return a string that contains the distribution name or a empty string if it could not find the name.
     */
    private String getLinuxDistribution() {
        Process p = null;
        try {
            p = ExecuteCommand( "lsb_release -d" );
        } catch ( IOException e ) {
            log.logError( "Could not execute command", e );
            return "";
        }
        BufferedReader in = getBufferedReaderFromProcess( p );
        try {
            return in.readLine();
        } catch ( IOException e ) {
            log.logError( "Could not read the distribution name", e );
            return "";
        }
    }

    protected Process ExecuteCommand( String command ) throws IOException {
        return Runtime.getRuntime().exec( command );
    }

    protected BufferedReader getBufferedReaderFromProcess( Process p ) {
        return new BufferedReader( new InputStreamReader( p.getInputStream() ) );
    }

    /**
     * Ask for the browser name.
     *
     * @return a String that contains the browser name.
     */
    public synchronized String getBrowserName() {
        final String userAgent = getUserAgent();
        if ( userAgent == null ) {
            return "";
        }
        if ( userAgent.contains( WINDOWS_BROWSER ) ) {
            return WINDOWS_BROWSER;
        } else if ( userAgent.contains( UBUNTU_BROWSER ) ) {
            return UBUNTU_BROWSER;
        } else if ( userAgent.contains( MAC_BROWSER ) ) {
            return MAC_BROWSER;
        }
        return "";
    }

}
