package org.lean.ui.plugins.file;

import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.variables.IVariables;
import org.lean.ui.leangui.context.IActionContextHandlersProvider;

import java.util.Map;

public interface ILeanFileTypeHandler extends IActionContextHandlersProvider {

    /**
     * What is this handling? (pipeline, workflow, ...)
     * @return The subject being handled
     */
    Object getSubject();

    /**
     * Get the user friendly name of the file being handled
     *
     * @return The user friendly name
     */
    String getName();

    /**
     * Set the user friendly name of the underlying file
     * @param name The user friendly name
     */
    void setName(String name);

    /**
     * Get a hold of the file type details
     *
     * @return
     */
    ILeanFileType getFileType();

    /**
     * @return The filename of the hop file
     */
    String getFilename();

    /**
     * Change the filename of the hop file
     *
     * @param filename The new filename
     */
    void setFilename( String filename );

    /**
     * Save the file without asking for a new filename
     */
    void save() throws HopException;

    /**
     * Save the file after asking for a filename
     */
    void saveAs( String filename ) throws HopException;

    /**
     * Start execution
     */
    void start();

    /**
     * Stop execution
     */
    void stop();

    /**
     * Pause execution
     */
    void pause();

    /**
     * Resume execution
     */
    void resume();

    /**
     * Preview this file
     */
    void preview();

    /**
     * Debug the file
     */
    void debug();

    /**
     * Refresh the graphical file representation after model changes
     */
    void redraw();

    /**
     * Update the toolbar, menus and so on. This is needed after a file, context or capabilities changes
     */
    void updateGui();

    /**
     * Select all items in the current files
     */
    void selectAll();

    /**
     * Unselect all items in the current file
     */
    void unselectAll();

    /**
     * Copy the selected items to the clipboard
     */
    void copySelectedToClipboard();

    /**
     * Cut the selected items to the clipboard
     */
    void cutSelectedToClipboard();

    /**
     * Delete the selected items
     */
    void deleteSelected();

    /**
     * Paste items from the clipboard
     */
    void pasteFromClipboard();

    /**
     * Perform any task needed to close a file, save it if needed
     *
     * @return true if the file is ready to close. Return false if there was a problem saving or any other issue.
     */
    boolean isCloseable();

    /**
     * Actually close the file, remove it from the user interface.
     */
    void close();

    /**
     * See if there anything has been changed by the user
     *
     * @return true if there were changes worth saving, false if nothing has been changed.
     */
    boolean hasChanged();

    /**
     * Undo a change to the file
     */
    void undo();

    /**
     * Redo a change to the file
     */
    void redo();

    /**
     * Describe the state properties of the file being handled: zoomLevel, scrollX, scrollY, active, ...
     * @return The state properties
     */
    Map<String, Object> getStateProperties();

    void applyStateProperties(Map<String, Object> stateProperties);

    /**
     * The variables associated with the file. It's usually keeping internal variables and other file specific settings like active unit test settings.
     * @return The variables associated with the loaded file
     */
    IVariables getVariables();

}
