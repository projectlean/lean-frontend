package org.lean.ui.plugins.perspective.metadata;

import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.metadata.api.IHopMetadata;
import org.lean.ui.core.ConstUi;

public class MetadataTreeGridHelper {

    private MetadataPerspective metadataPerspective;
    private String displayName, imageSource;
    private Class<IHopMetadata> metadataClass;
    private Image metadataIcon ;

    public MetadataTreeGridHelper(MetadataPerspective metadataPerspective, String displayName, String imageSource, Class<IHopMetadata> metadataClass){
        this.metadataPerspective = metadataPerspective;
        this.displayName = displayName;
        this.metadataClass = metadataClass;
        this.imageSource = imageSource;
        if(StringUtils.isNotEmpty(imageSource)){
            metadataIcon = new Image(ConstUi.LEAN_FRONTEND_FOLDER + imageSource, "");
            metadataIcon.setHeight(ConstUi.SMALL_ICON_SIZE_PX);
            metadataIcon.setWidth(ConstUi.SMALL_ICON_SIZE_PX);
        }
    }

    public String getDisplayName(){
        return displayName;
    }

    public Class<IHopMetadata> getMetadataClass(){
        return metadataClass;
    }

    public Image getMetadataIcon(){
        return metadataIcon;
    }

    public HorizontalLayout getMetadataComponent(){
        HorizontalLayout metadataHL = new HorizontalLayout();
        metadataHL.setSizeFull();
        metadataHL.setId("metadata-grid-component-" + displayName.replaceAll(" ", ""));

        ContextMenu metadataContextMenu = new ContextMenu();
        metadataContextMenu.setTarget(metadataHL);
        metadataContextMenu.setOpenOnClick(true);
        metadataContextMenu.addItem("New", e -> metadataPerspective.onNewMetadata(metadataClass));


        if(StringUtils.isNotEmpty(imageSource)){
            metadataHL.add(metadataIcon);
        }else{
            metadataContextMenu.add(new Hr());
//            metadataContextMenu.addItem("Edit ", e -> System.out.println("Edit " + metadataClass.getName() + ", " + displayName));
            metadataContextMenu.addItem("Edit", e -> metadataPerspective.onEditMetadata(metadataClass, displayName));
            metadataContextMenu.addItem("Rename ", e -> System.out.println("Rename " + metadataClass.getName() + ", " + displayName));
            metadataContextMenu.addItem("Duplicate ", e -> System.out.println("Duplicate " + metadataClass.getName() + ", " + displayName));
            metadataContextMenu.addItem("Delete ", e -> System.out.println("Delete " + metadataClass.getName() + ", " + displayName));
        }
        metadataHL.add(new Label(displayName));
        return metadataHL;
    }
}
