package org.lean.ui.plugins.perspective.console;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;
import org.apache.hop.core.gui.plugin.GuiPlugin;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.ui.hopgui.file.IHopFileType;
import org.apache.hop.ui.hopgui.file.IHopFileTypeHandler;
import org.lean.ui.leangui.context.IGuiContextHandler;
import org.lean.ui.layout.LeanGuiLayout;
import org.lean.ui.plugins.file.ILeanFileType;
import org.lean.ui.plugins.file.ILeanFileTypeHandler;
import org.lean.ui.plugins.perspective.LeanPerspectiveBase;
import org.lean.ui.plugins.perspective.ILeanPerspective;
import org.lean.ui.plugins.perspective.LeanPerspectivePlugin;

import java.util.List;

@LeanPerspectivePlugin(
        id = "LeanConsolePerspective",
        name = "LeanConsolePerspective",
        description = "Lean Console Perspective",
        image = "./frontend/images/perspectives/console.svg",
        route = "console"
)
@Route(value="console", layout = LeanGuiLayout.class)
@GuiPlugin(description = "This perspective allows you to create a home perspective")
public class ConsolePerspective extends LeanPerspectiveBase implements ILeanPerspective {

    private LeanGuiLayout leanGuiLayout;

    public ConsolePerspective(){
        super();

        getContent().add(new Label("Console Perspective"));
    }

    @Override
    public String getPluginId() {
        return "console-perspective";
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
        this.leanGuiLayout = leanGuiLayout;
    }

    @Override
    public ILeanFileTypeHandler getActiveFileTypeHandler() {
        return null;
    }

    @Override
    public void setActiveFileTypeHandler(ILeanFileTypeHandler activeFileTypeHandler) {

    }

    @Override
    public List<ILeanFileType> getSupportedLeanFileTypes() {
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
