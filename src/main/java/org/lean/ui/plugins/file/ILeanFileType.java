package org.lean.ui.plugins.file;

import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.file.IHasFilename;
import org.apache.hop.core.variables.IVariables;
import org.lean.ui.layout.LeanGuiLayout;
import org.lean.ui.leangui.context.IGuiContextHandler;

import java.util.List;
import java.util.Properties;

public interface ILeanFileType {

    String CAPABILITY_NEW = "New";
    String CAPABILITY_SAVE = "Save";
    String CAPABILITY_SAVE_AS = "SaveAs";
    String CAPABILITY_EXPORT_TO_SVG = "ExportToSvg";
    String CAPABILITY_START = "Start";
    String CAPABILITY_CLOSE = "Close";
    String CAPABILITY_STOP = "Stop";
    String CAPABILITY_PAUSE = "Pause";
    String CAPABILITY_PREVIEW = "Preview";
    String CAPABILITY_DEBUG = "Debug";

    String CAPABILITY_SELECT = "Select";
    String CAPABILITY_COPY = "Copy";
    String CAPABILITY_PASTE = "Paste";
    String CAPABILITY_CUT = "Cut";
    String CAPABILITY_DELETE = "Delete";

    String CAPABILITY_FILE_HISTORY = "FileHistory";

    /** @return The name of this file type */
    String getName();

    /**
     * Returns the default file extension in lowercase prefixed with dot (.xxx) for this file type.
     *
     * @return The default file extension
     */
    String getDefaultFileExtension();

    /** @return The file type extensions. */
    String[] getFilterExtensions();

    /** @return The file names (matching the extensions) */
    String[] getFilterNames();

    /** @return The capabilities of this file handler */
    Properties getCapabilities();

    /**
     * Check to see if the capability is present
     *
     * @param capability The capability to check
     * @return True if the capability is set to any non-null value
     */
    boolean hasCapability(String capability);

    /**
     * Load and display the file
     *
     * @param leanGuiLayout The hop GUI to reference
     * @param filename The filename to load
     * @param parentVariableSpace The parent variablespace to inherit from
     * @return The hop file handler
     */
    ILeanFileTypeHandler openFile(LeanGuiLayout leanGuiLayout, String filename, IVariables parentVariableSpace)
            throws HopException;

    ILeanFileTypeHandler newFile(LeanGuiLayout leanGuiLayout, IVariables parentVariableSpace) throws HopException;

    /**
     * Look at the given file and see if it's handled by this type. Usually this is done by simply
     * looking at the file extension. In rare cases we look at the content.
     *
     * @param filename The filename
     * @param checkContent True if we want to look inside the file content
     * @return true if this HopFile is handling the file
     * @throws HopException In case something goes wrong like: file doesn't exist, a permission
     *     problem, ...
     */
    boolean isHandledBy(String filename, boolean checkContent) throws HopException;

    /**
     * Checks whether or not this file type supports the given metadata class
     *
     * @param metaObject The object to verify support for
     * @return
     */
    boolean supportsFile(IHasFilename metaObject);

    /**
     * @return A list of context handlers allowing you to see all the actions that can be taken with
     *     the current file type. (CRUD, ...)
     */
    List<IGuiContextHandler> getContextHandlers(LeanGuiLayout leanGuiLayout);

    /**
     * The icon image for this file type
     *
     * @return The path to the SVG file, a logo for this file type
     */
    String getFileTypeImage();

}
