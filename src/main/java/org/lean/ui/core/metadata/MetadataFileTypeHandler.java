package org.lean.ui.core.metadata;


import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.core.variables.Variables;
import org.lean.ui.leangui.context.IGuiContextHandler;
import org.lean.ui.plugins.file.ILeanFileTypeHandler;
import org.lean.ui.plugins.file.ILeanFileType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MetadataFileTypeHandler implements ILeanFileTypeHandler {

    private static final ILeanFileType fileType = new MetadataFileType();

    public MetadataFileTypeHandler() {

    }

    @Override public Object getSubject() {
        return null;
    }

    @Override public String getName() {
        return null;
    }

    @Override public void setName( String name ) {
    }

    @Override
    public ILeanFileType getFileType(){ return fileType;}

    @Override public String getFilename() {
        return "meta";
    }

    @Override public void setFilename( String filename ) {
    }

    @Override public void save() throws HopException {
    }

    @Override public void saveAs( String filename ) throws HopException {
    }

    @Override public void start() {
    }

    @Override public void stop() {
    }

    @Override public void pause() {
    }

    @Override public void resume() {
    }

    @Override public void preview() {
    }

    @Override public void debug() {
    }

    @Override public void redraw() {
    }

    @Override public void updateGui() {
    }

    @Override public void selectAll() {
    }

    @Override public void unselectAll() {
    }

    @Override public void copySelectedToClipboard() {
    }

    @Override public void cutSelectedToClipboard() {
    }

    @Override public void deleteSelected() {
    }

    @Override public void pasteFromClipboard() {
    }

    @Override public boolean isCloseable() {
        return true;
    }

    @Override public void close() {

    }

    @Override public boolean hasChanged() {
        return false;
    }

    @Override public void undo() {
    }

    @Override public void redo() {
    }

    @Override public Map<String, Object> getStateProperties() {
        return Collections.emptyMap();
    }

    @Override public void applyStateProperties( Map<String, Object> stateProperties ) {
    }

    @Override
    public List<IGuiContextHandler> getContextHandlers(){
        List<IGuiContextHandler> handlers = new ArrayList<>();
        return handlers;
    }

    /**
     * Metadata doesn't have it's own variables.  It should take it elsewhere.
     *
     * @return An empty variables set
     */
    @Override public IVariables getVariables() {
        return new Variables();
    }
}
