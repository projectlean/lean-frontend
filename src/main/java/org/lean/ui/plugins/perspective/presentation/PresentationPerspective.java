package org.lean.ui.plugins.perspective.presentation;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import org.apache.hop.core.gui.plugin.GuiPlugin;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.ui.context.IGuiContextHandler;
import org.lean.ui.layout.LeanGuiLayout;
import org.lean.ui.plugins.perspective.BasePerspective;
import org.lean.ui.plugins.perspective.ILeanPerspective;
import org.lean.ui.plugins.perspective.LeanPerspectivePlugin;

import java.util.List;

@LeanPerspectivePlugin(
        id = "LeanPresentationPerspective",
        name = "LeanPresentationPerspective",
        description = "Lean Presentation Perspective",
        image = "./frontend/images/perspectives/presentation.svg",
        route = "presentation"
)
@Route(value="presentation", layout = LeanGuiLayout.class)
@GuiPlugin(description = "This perspective allows you to modify a presentation")
public class PresentationPerspective extends BasePerspective implements ILeanPerspective {

    public PresentationPerspective(){
        super();

        setId("presentation-perspective");

        Tabs presentationTabs = new Tabs();
        presentationTabs.setHeight("3vh");
        presentationTabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        presentationTabs.setWidthFull();

        getContent().add(new Label("Presentation Perspective"));
    }

    @Override
    public String getPluginId() {
        return "presentation-perspective";
    }

    @Override
    public void activate() {
        LeanGuiLayout.perspectiveManager.setActivePerspective(this);
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
    public List<IGuiContextHandler> getContextHandlers() {
        return null;
    }

}
