package org.lean.ui.plugins.perspective.database;

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
        id = "LeanDatabasePerspective",
        name = "LeanDatabasePerspective",
        description = "Lean Database Perspective",
        image = "./frontend/images/perspectives/database.svg",
        route = "database"
)
@Route(value="database", layout = LeanGuiLayout.class)
@GuiPlugin(description = "This perspective allows you to modify a presentation")
public class DatabasePerspective extends BasePerspective implements ILeanPerspective {

    private IHopMetadataProvider metadataProvider;

    public DatabasePerspective(){
        super();
        setId("database-perspective");
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
        this.metadataProvider = metadataProvider;
    }

    @Override
    public List<IGuiContextHandler> getContextHandlers() {
        return null;
    }
}
