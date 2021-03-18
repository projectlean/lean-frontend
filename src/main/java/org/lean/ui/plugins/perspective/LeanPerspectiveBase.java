package org.lean.ui.plugins.perspective;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.ui.LeanGui;
import org.lean.ui.layout.LeanGuiLayout;

//@VaadinSessionScope
public abstract class LeanPerspectiveBase extends Composite<HorizontalLayout> implements ILeanPerspective{

    public LeanGuiLayout leanGuiLayout;

    public LeanPerspectiveBase(){
        getContent().setSizeFull();
        getContent().setMargin(false);
        getContent().setPadding(false);
        getContent().setSpacing(false);
    }

    @Override
    public String getPluginId(){
        return null;
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

}
