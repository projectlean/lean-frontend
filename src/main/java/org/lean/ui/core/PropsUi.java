package org.lean.ui.core;

import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.Const;
import org.apache.hop.core.Props;
import org.apache.hop.core.util.Utils;

public class PropsUi extends Props {

    private static String OS = System.getProperty( "os.name" ).toLowerCase();

    private static final String NO = "N";

    private static final String YES = "Y";

    private static double nativeZoomFactor;

    private static final String SHOW_TOOL_TIPS = "ShowToolTips";

    private static final String SHOW_HELP_TOOL_TIPS = "ShowHelpToolTips";

    private static final String CANVAS_GRID_SIZE = "CanvasGridSize";

    private static final String DISABLE_BROWSER_ENVIRONMENT_CHECK = "DisableBrowserEnvironmentCheck";

    private static final String USE_DOUBLE_CLICK_ON_CANVAS = "UseDoubleClickOnCanvas";

    private static final String USE_GLOBAL_FILE_BOOKMARKS = "UseGlobalFileBookmarks";

    private static PropsUi instance;

    public static PropsUi getInstance() {
        if ( instance == null ) {
            instance = new PropsUi();
        }
        return instance;
    }

    private PropsUi() {
        super();

        nativeZoomFactor = 1.0;
        setDefault();
    }

    public void setDefault() {
        super.setDefault();
    }

    public void setIconSize( int size ) {
        setProperty( STRING_ICON_SIZE, "" + size );
    }

    public int getIconSize() {
        return Const.toInt( getProperty( STRING_ICON_SIZE ), Integer.valueOf(ConstUi.ICON_SIZE));
    }

    public void setZoomFactor( double factor ) {
        setProperty( STRING_ZOOM_FACTOR, Double.toString( factor ) );
    }

    public double getZoomFactor() {
        String zoomFactorString = getProperty( STRING_ZOOM_FACTOR );
        if ( StringUtils.isNotEmpty( zoomFactorString ) ) {
            return Const.toDouble( zoomFactorString, nativeZoomFactor );
        } else {
            return nativeZoomFactor;
        }
    }

    /**
     * Get the margin compensated for the zoom factor
     *
     * @return
     */
    public int getMargin() {
        return (int) Math.round( getZoomFactor() * Const.MARGIN );
    }

    public void setMaxUndo( int max ) {
        setProperty( STRING_MAX_UNDO, "" + max );
    }

    public int getMaxUndo() {
        return Const.toInt( getProperty( STRING_MAX_UNDO ), Const.MAX_UNDO );
    }

    public void setMiddlePct( int pct ) {
        setProperty( STRING_MIDDLE_PCT, "" + pct );
    }

    public int getMiddlePct() {
        return Const.toInt( getProperty( STRING_MIDDLE_PCT ), Const.MIDDLE_PCT );
    }

    public void setOpenLastFile( boolean open ) {
        setProperty( STRING_OPEN_LAST_FILE, open ? YES : NO );
    }

    public boolean openLastFile() {
        String open = getProperty( STRING_OPEN_LAST_FILE );
        return !NO.equalsIgnoreCase( open );
    }

    public void setAutoSave( boolean autosave ) {
        setProperty( STRING_AUTO_SAVE, autosave ? YES : NO );
    }

    public boolean getAutoSave() {
        String autosave = getProperty( STRING_AUTO_SAVE );
        return YES.equalsIgnoreCase( autosave ); // Default = OFF
    }

    public void setSaveConfirmation( boolean saveconf ) {
        setProperty( STRING_SAVE_CONF, saveconf ? YES : NO );
    }

    public boolean getSaveConfirmation() {
        String saveconf = getProperty( STRING_SAVE_CONF );
        return YES.equalsIgnoreCase( saveconf ); // Default = OFF
    }

    public void setAutoCollapseCoreObjectsTree( boolean autoCollapse ) {
        setProperty( STRING_AUTO_COLLAPSE_CORE_TREE, autoCollapse ? YES : NO );
    }

    public boolean getAutoCollapseCoreObjectsTree() {
        String autoCollapse = getProperty( STRING_AUTO_COLLAPSE_CORE_TREE );
        return YES.equalsIgnoreCase( autoCollapse ); // Default = OFF
    }

    public void setDefaultPreviewSize( int size ) {
        setProperty( STRING_DEFAULT_PREVIEW_SIZE, "" + size );
    }

    public int getDefaultPreviewSize() {
        return Const.toInt( getProperty( STRING_DEFAULT_PREVIEW_SIZE ), 1000 );
    }

    public void setDialogSize( String styleProperty ) {

        // TODO: Vaadin implementation

        String prop = getProperty( styleProperty );
        if ( Utils.isEmpty( prop ) ) {
            return;
        }

        String[] xy = prop.split( "," );
        if ( xy.length != 2 ) {
            return;
        }

    }

    public boolean useGlobalFileBookmarks() {
        return YES.equalsIgnoreCase( getProperty( USE_GLOBAL_FILE_BOOKMARKS, YES) );
    }

    public void setUseGlobalFileBookmarks( boolean use ) {
        setProperty( USE_GLOBAL_FILE_BOOKMARKS, use ? YES : NO );
    }


    public boolean showToolTips() {
        return YES.equalsIgnoreCase( getProperty( SHOW_TOOL_TIPS, YES ) );
    }

    public void setShowToolTips( boolean show ) {
        setProperty( SHOW_TOOL_TIPS, show ? YES : NO );
    }

    public boolean isShowingHelpToolTips() {
        return YES.equalsIgnoreCase( getProperty( SHOW_HELP_TOOL_TIPS, YES ) );
    }

    public void setShowingHelpToolTips( boolean show ) {
        setProperty( SHOW_HELP_TOOL_TIPS, show ? YES : NO );
    }

    /**
     * Gets the supported version of the requested software.
     *
     * @param property the key for the software version
     * @return an integer that represents the supported version for the software.
     */
    public int getSupportedVersion( String property ) {
        return Integer.parseInt( getProperty( property ) );
    }

    /**
     * Ask if the browsing environment checks are disabled.
     *
     * @return 'true' if disabled 'false' otherwise.
     */
    public boolean isBrowserEnvironmentCheckDisabled() {
        return "Y".equalsIgnoreCase( getProperty( DISABLE_BROWSER_ENVIRONMENT_CHECK, "N" ) );
    }

    /**
     * Gets nativeZoomFactor
     *
     * @return value of nativeZoomFactor
     */
    public static double getNativeZoomFactor() {
        return nativeZoomFactor;
    }

    /**
     * @param nativeZoomFactor The nativeZoomFactor to set
     */
    public static void setNativeZoomFactor( double nativeZoomFactor ) {
        PropsUi.nativeZoomFactor = nativeZoomFactor;
    }

}
