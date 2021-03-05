package org.lean.ui.plugins.perspective;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.ui.hopgui.file.IHopFileType;
import org.apache.hop.ui.hopgui.file.IHopFileTypeHandler;
import org.lean.ui.leangui.context.IGuiContextHandler;
import org.lean.ui.layout.LeanGuiLayout;
import org.lean.ui.leangui.file.ILeanFileTypeHandler;

import java.util.List;

@Route(value="", layout = LeanGuiLayout.class)
public class EmptyPerspective extends BasePerspective implements ILeanPerspective{

    public EmptyPerspective(){
        getContent().add(new Label("Empty Perspective"));
    }

    @Override
    public String getPluginId() {
        return "empty";
    }

    @Override
    public void activate() {

    }

    @Override
    public void perspectiveActivated() {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void initialize(LeanGuiLayout leanGuiLayout, IHopMetadataProvider metadataProvider) {

    }

    @Override
    public ILeanFileTypeHandler getActiveFileTypeHandler() {
        return null;
    }

    @Override
    public void setActiveFileTypeHandler(IHopFileTypeHandler activeFileTypeHandler) {

    }

    @Override
    public List<IHopFileType> getSupportedHopFileTypes() {
        return null;
    }

    @Override
    public boolean remove(ILeanFileTypeHandler typeHandler) {
        return false;
    }

    @Override
    public List<IGuiContextHandler> getContextHandlers() {
        return null;
    }
}
