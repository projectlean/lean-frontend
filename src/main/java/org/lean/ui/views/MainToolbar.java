package org.lean.ui.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class MainToolbar extends VerticalLayout {

    public MainToolbar(){
        this.setPadding(false);
        this.setMargin(false);
        this.setSpacing(false);
        this.setWidth("1.5vw");
        this.setHeightFull();
        this.setId("main-toolbar");
    }
}
