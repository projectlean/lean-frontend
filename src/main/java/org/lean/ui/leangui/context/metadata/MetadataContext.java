package org.lean.ui.leangui.context.metadata;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.apache.hop.metadata.api.IHopMetadata;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.ui.leangui.context.IActionContextHandlersProvider;
import org.lean.ui.leangui.context.IGuiContextHandler;
import org.lean.ui.layout.LeanGuiLayout;

import java.util.ArrayList;
import java.util.List;

@VaadinSessionScope
public class MetadataContext implements IActionContextHandlersProvider {

    private LeanGuiLayout leanGuiLayout;
    private IHopMetadataProvider metadataProvider;

    public MetadataContext(LeanGuiLayout leanGuiLayout, IHopMetadataProvider metadataProvider){
        this.leanGuiLayout = leanGuiLayout;
        this.metadataProvider = metadataProvider;
    }

    @Override
    public List<IGuiContextHandler> getContextHandlers(){
        List<IGuiContextHandler> handlers = new ArrayList<>();
        List<Class<IHopMetadata>> metadataClasses = metadataProvider.getMetadataClasses();
        for(Class<IHopMetadata> metadataClass : metadataClasses){
            handlers.add(new MetadataContextHandler(leanGuiLayout, metadataProvider, metadataClass));
        }
        return handlers;
    }
}
