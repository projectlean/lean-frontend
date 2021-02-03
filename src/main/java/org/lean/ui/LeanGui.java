package org.lean.ui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;
import org.lean.ui.views.MainFooter;
import org.lean.ui.views.MainHeader;

import java.util.Objects;

public class LeanGui extends Composite<Div> implements RouterLayout {

    private Div mainContent = new Div();

    private MainHeader mainHeader;
    private MainFooter mainFooter;

    public LeanGui(){
        setId("lean-gui");
        getContent().setSizeFull();
        mainHeader = new MainHeader();

        mainFooter = new MainFooter();
        mainContent.setId("lean-main-content");
        mainContent.setSizeFull();

        VerticalLayout leanGuiVL = new VerticalLayout(
          mainHeader,
          mainContent,
          mainFooter
        );
        leanGuiVL.setId("lean-gui-main-vl");
        leanGuiVL.setSizeFull();

        getContent().add(leanGuiVL);

    }

    @Override
    public void showRouterLayoutContent(HasElement hasElement) {
        Objects.requireNonNull(hasElement);
        Objects.requireNonNull(hasElement.getElement());
        mainContent.removeAll();
        mainContent.getElement().appendChild(hasElement.getElement());
    }

}
