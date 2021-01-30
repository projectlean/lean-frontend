package org.lean.ui.views;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.lean.ui.core.ConstUi;

public class MainFooter extends HorizontalLayout {

    public MainFooter(){
        this.setPadding(false);
        this.setMargin(false);
        this.setSpacing(false);
        this.setHeight(ConstUi.HBAR_HEIGHT);
        this.setWidthFull();
        this.setId("main-footer");

    }
}
