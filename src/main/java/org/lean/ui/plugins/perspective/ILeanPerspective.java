package org.lean.ui.plugins.perspective;

import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.ui.hopgui.file.IHopFileType;
import org.apache.hop.ui.hopgui.file.IHopFileTypeHandler;
import org.lean.ui.leangui.context.IActionContextHandlersProvider;
import org.lean.ui.layout.LeanGuiLayout;
import org.lean.ui.leangui.file.ILeanFileTypeHandler;

import java.util.List;

public interface ILeanPerspective extends IActionContextHandlersProvider {

    String getPluginId();

    void activate();

    void perspectiveActivated();

    boolean isActive();

    void initialize(LeanGuiLayout leanGuiLayout, IHopMetadataProvider metadataProvider);

    /**
     * Get the active file type handler capable of saving, executing, printing, ... a file
     *
     * @return The active file type handler
     */
    ILeanFileTypeHandler getActiveFileTypeHandler();

    /**
     * Set the focus on the given file type handler.
     *
     * @param activeFileTypeHandler
     */
    void setActiveFileTypeHandler( IHopFileTypeHandler activeFileTypeHandler );

    /**
     * Get a list of supported file types for this perspective
     *
     * @return The list of supported file types
     */
    List<IHopFileType> getSupportedHopFileTypes();

    /**
     * Remove this file type handler from the perspective
     *
     * @param typeHandler The file type handler to remove
     * @return
     */
    boolean remove( ILeanFileTypeHandler typeHandler );


}
