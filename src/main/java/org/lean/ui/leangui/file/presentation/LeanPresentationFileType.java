package org.lean.ui.leangui.file.presentation;

import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.file.IHasFilename;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.ui.hopgui.file.HopFileTypeBase;
import org.apache.hop.ui.hopgui.file.HopFileTypePlugin;
import org.apache.hop.ui.hopgui.file.IHopFileTypeHandler;
import org.lean.presentation.LeanPresentation;
import org.lean.ui.layout.LeanGuiLayout;
import org.lean.ui.leangui.context.IGuiContextHandler;
import org.lean.ui.leangui.file.ILeanFileTypeHandler;
import org.lean.ui.leangui.file.LeanFileTypeBase;
import org.lean.ui.plugins.perspective.presentation.PresentationPerspective;

import java.util.List;
import java.util.Properties;


@HopFileTypePlugin(
        id = "LeanFile-Presentation-Plugin",
        description = "The Presentation file information for the Hop GUI",
        image = "./frontend/images/presentation.svg"
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
    public ILeanFileTypeHandler openFile(LeanGuiLayout leanGuiLayout, String filename, IVariables iVariables) throws HopException {
        try{
            // This file is opened in the Lean Presentation perspective
            PresentationPerspective perspective = leanGuiLayout.getPresentationPerspective();
            perspective.activate();

            // See if the same presentation isn't already open.
            // Other file types we might allow to open more than once but not presentations for now.
            // TODO: switch to presentation tab etc

            return null;
        }catch(Exception e){
            throw new HopException("Error opening presentation file '" + filename + "'", e);
        }
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
    public boolean supportsFile(IHasFilename iHasFilename) {
        return false;
    }

    @Override
    public List<IGuiContextHandler> getContextHandlers() {
        return null;
    }

    @Override
    public String getFileTypeImage() {
        return "./frontend/images/presentation.svg";
    }

}
