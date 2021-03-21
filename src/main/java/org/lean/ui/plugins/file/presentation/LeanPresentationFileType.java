package org.lean.ui.plugins.file.presentation;

import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.file.IHasFilename;
import org.apache.hop.core.gui.plugin.action.GuiAction;
import org.apache.hop.core.gui.plugin.action.GuiActionType;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.ui.hopgui.file.HopFileTypePlugin;
import org.lean.presentation.LeanPresentation;
import org.lean.ui.core.dialog.ErrorDialog;
import org.lean.ui.layout.LeanGuiLayout;
import org.lean.ui.leangui.context.GuiContextHandler;
import org.lean.ui.leangui.context.IGuiContextHandler;
import org.lean.ui.plugins.file.ILeanFileType;
import org.lean.ui.plugins.file.ILeanFileTypeHandler;
import org.lean.ui.plugins.file.LeanFileTypeBase;
import org.lean.ui.plugins.file.LeanFileTypePlugin;
import org.lean.ui.plugins.perspective.TabItemHandler;
import org.lean.ui.plugins.perspective.presentation.PresentationPerspective;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


@LeanFileTypePlugin(
        id = "LeanFile-Presentation-Plugin",
        description = "The Presentation file information for the Hop GUI",
        image = "ui/images/presentation.svg"
)
public class LeanPresentationFileType<T extends LeanPresentation> extends LeanFileTypeBase implements ILeanFileType {

    public static final String PIPELINE_FILE_TYPE_DESCRIPTION = "Presentation";

    public LeanPresentationFileType(){
    }

    @Override
    public String getName() {
        return PIPELINE_FILE_TYPE_DESCRIPTION;
    }

    @Override
    public String getDefaultFileExtension() {
        return ".lpr";
    }

    @Override
    public String[] getFilterExtensions() {
        return new String[] {".hpl"};
    }

    @Override
    public String[] getFilterNames() {
        return new String[] {"Presentations"};
    }

    @Override
    public Properties getCapabilities() {
        Properties capabilities = new Properties();
        capabilities.setProperty( ILeanFileType.CAPABILITY_NEW, "true" );
        capabilities.setProperty( ILeanFileType.CAPABILITY_CLOSE, "true" );
        capabilities.setProperty( ILeanFileType.CAPABILITY_SAVE, "true" );
        capabilities.setProperty( ILeanFileType.CAPABILITY_SAVE_AS, "true" );
        capabilities.setProperty( ILeanFileType.CAPABILITY_EXPORT_TO_SVG, "true" );
        capabilities.setProperty( ILeanFileType.CAPABILITY_PREVIEW, "true" );
        capabilities.setProperty( ILeanFileType.CAPABILITY_DEBUG, "true" );

        capabilities.setProperty( ILeanFileType.CAPABILITY_COPY, "true" );
        capabilities.setProperty( ILeanFileType.CAPABILITY_PASTE, "true" );
        capabilities.setProperty( ILeanFileType.CAPABILITY_CUT, "true" );
        capabilities.setProperty( ILeanFileType.CAPABILITY_DELETE, "true" );

        capabilities.setProperty( ILeanFileType.CAPABILITY_FILE_HISTORY, "true" );

        return capabilities;
    }

    @Override
    public ILeanFileTypeHandler newFile(LeanGuiLayout leanGuiLayout, IVariables iVariables) throws HopException {
        try{
            // This file is opened in the Lean Presentation perspective
            PresentationPerspective perspective = leanGuiLayout.getPresentationPerspective();
            perspective.activate();

            LeanPresentation presentation = new LeanPresentation();
            presentation.setName("New Presentation");

            return perspective.addPresentation(presentation);

        }catch(Exception e){
            throw new HopException("Error create new presentation", e);
        }
    }

    @Override
    public ILeanFileTypeHandler openFile(LeanGuiLayout leanGuiLayout, String filename, IVariables iVariables) throws HopException {
        try{
            // This file is opened in the Lean Presentation perspective
            PresentationPerspective perspective = leanGuiLayout.getPresentationPerspective();
            perspective.activate();

            // See if the same presentation isn't already open.
            // Other file types we might allow to open more than once but not presentations for now.
            // TODO: check + fix
            TabItemHandler tabItemHandlerWithFilename = perspective.findTabItemHandlerWithFilename(filename);
            if(tabItemHandlerWithFilename != null){
                perspective.switchToTab(tabItemHandlerWithFilename);
                return tabItemHandlerWithFilename.getTypeHandler();
            }

            // fetch presentation + open
            ILeanFileTypeHandler typeHandler = perspective.addPresentation(null);

            return typeHandler;
        }catch(Exception e){
            throw new HopException("Error opening presentation file '" + filename + "'", e);
        }
    }

    @Override
    public boolean supportsFile(IHasFilename iHasFilename) {
        return false;
    }

    private static final String ACTION_ID_NEW_PRESENTATION = "NewPresentation";

    @Override
    public List<IGuiContextHandler> getContextHandlers(LeanGuiLayout leanGuiLayout) {

        List<IGuiContextHandler> handlers = new ArrayList<>();

        GuiAction newAction = new GuiAction(ACTION_ID_NEW_PRESENTATION, GuiActionType.Create, "Presentation", "Creates a new presentation. Visualizes your data through a collections of connectors and components.",
            "ui/images/presentation.svg",
                (shiftClicked, controlClicked, parameters) -> {
                    try {
                        newFile(leanGuiLayout, leanGuiLayout.getVariables());
                    } catch (Exception e) {
                        new ErrorDialog("Error", "Error creating new presentation.", e);
                    }
                });
        newAction.setCategory("File");
        newAction.setCategory("1");

        handlers.add(new GuiContextHandler(ACTION_ID_NEW_PRESENTATION, Arrays.asList(newAction)));
        return handlers;
    }

    @Override
    public String getFileTypeImage() {
        return "ui/images/presentation.svg";
    }

}
