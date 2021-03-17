package org.lean.ui.leangui.context.metadata;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.apache.hop.core.DbCache;
import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.core.gui.plugin.action.GuiAction;
import org.apache.hop.core.gui.plugin.action.GuiActionType;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.metadata.api.HopMetadata;
import org.apache.hop.metadata.api.IHopMetadata;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.util.HopMetadataUtil;
import org.lean.ui.leangui.context.IGuiContextHandler;
import org.lean.ui.core.metadata.MetadataManager;
import org.lean.ui.layout.LeanGuiLayout;

import java.util.ArrayList;
import java.util.List;

//@VaadinSessionScope
public class MetadataContextHandler implements IGuiContextHandler {

    private static final Class<?> PKG = MetadataContextHandler.class;

    public static final String CONTEXT_ID = "LeanGuiMetadataContext";

    private LeanGuiLayout leanGuiLayout;
    private IHopMetadataProvider metadataProvider;
    private Class<? extends IHopMetadata> metadataObjectClass;
    private MetadataManager<? extends IHopMetadata> metadataManager;

    public MetadataContextHandler(LeanGuiLayout leanGuiLayout, IHopMetadataProvider metadataProvider, Class<? extends IHopMetadata> metadataObjectClass){
        this.leanGuiLayout = leanGuiLayout;
        this.metadataProvider = metadataProvider;
        this.metadataObjectClass = metadataObjectClass;

        metadataManager = new MetadataManager<>(leanGuiLayout, leanGuiLayout.getVariables(), metadataProvider, metadataObjectClass);
        metadataManager.setClassLoader(metadataObjectClass.getClassLoader());
    }

    @Override
    public String getContextId(){
        return CONTEXT_ID;
    }

    @Override
    public List<GuiAction> getSupportedActions(){
        HopMetadata hopMetadata = HopMetadataUtil.getHopMetadataAnnotation(metadataObjectClass);

        List<GuiAction> actions = new ArrayList<>();

        GuiAction newAction = new GuiAction(
                "CREATE_" + hopMetadata.name(),
                GuiActionType.Create,
                hopMetadata.name(),
                "Creates a new " + hopMetadata.name() + " : " + hopMetadata.description(),
                hopMetadata.image(),
                (shiftClicked, controlClicked, parameters) -> metadataManager.newMetadataWithEditor() );
        newAction.setClassLoader(metadataObjectClass.getClassLoader());
        newAction.setCategory("Metadata");
        newAction.setCategoryOrder("2");
        actions.add(newAction);

        GuiAction editAction = new GuiAction(
                "EDIT_" + hopMetadata.name(),
                GuiActionType.Modify,
                hopMetadata.name(),
                "Edits a " + hopMetadata.name() + " : " + hopMetadata.description(),
                hopMetadata.image(),
                (shiftClicked, controlClicked, parameters) -> metadataManager.editMetadata());
        editAction.setClassLoader(metadataObjectClass.getClassLoader());
        editAction.setCategory("Metadata");
        editAction.setCategoryOrder("2");
        actions.add(editAction);

        GuiAction deleteAction = new GuiAction(
                "DELETE_" + hopMetadata.name(),
                GuiActionType.Delete,
                hopMetadata.name(),
                "After confirmation this deletes a " + hopMetadata.name() + " : " + hopMetadata.description(),
                hopMetadata.image(),
                (shiftClicked, controlClicked, parameters) -> metadataManager.deleteMetadata());
        deleteAction.setClassLoader(metadataObjectClass.getClassLoader());
        deleteAction.setCategory("Metadata");
        deleteAction.setCategoryOrder("2");
        actions.add(deleteAction);

        if(metadataObjectClass.isAssignableFrom(DatabaseMeta.class)){
            GuiAction databaseClearCacheAction =
                    new GuiAction("DATABASE_CLEAR_CACHE",
                            GuiActionType.Custom,
                            BaseMessages.getString(PKG, "HopGui.Context.Database.Menu.ClearDatabaseCache.Label"),
                            BaseMessages.getString(PKG, "HopGui.Context.Database.Menu.ClearDatabaseCache.Tooltip"),
                            null,
                            (shifClicked, controlClicked, parameters) ->
                                    DbCache.getInstance().clear((String)parameters[0]));
            newAction.setClassLoader(metadataObjectClass.getClassLoader());
            newAction.setCategory("Metadata");
            newAction.setCategoryOrder("3");
            actions.add(databaseClearCacheAction);
        }

        return actions;
    }
}
