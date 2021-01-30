package org.lean.ui.plugins.perspective;

import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.ui.LeanGui;
import org.lean.ui.context.IActionContextHandlersProvider;
import org.lean.ui.layout.LeanGuiLayout;

public interface ILeanPerspective extends IActionContextHandlersProvider {

    String getPluginId();

    void activate();

    void perspectiveActivated();

    boolean isActive();

    void initialize(LeanGuiLayout leanGuiLayout, IHopMetadataProvider metadataProvider);
}
