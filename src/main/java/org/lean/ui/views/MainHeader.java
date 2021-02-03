package org.lean.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.lean.ui.core.ConstUi;
import org.lean.ui.layout.LeanGuiLayout;

public class MainHeader extends HorizontalLayout {

    private LeanGuiLayout leanGuiLayout;

    public MainHeader(){

        setPadding(false);
        setMargin(false);
        setSpacing(false);
        this.setHeight(ConstUi.HBAR_HEIGHT);
        this.setWidthFull();
        this.setId("main-header");

        Div logoDiv = new Div();
        Div mainHeaderDiv = new Div();
        Div loginInfoDiv = new Div();

        logoDiv.setWidth("10%");
        mainHeaderDiv.setSizeFull();
        loginInfoDiv.setWidth("10%");

        Image leanLogo = new Image(ConstUi.LEAN_LOGO_PATH, ConstUi.LEAN_LOGO_ALT);
        leanLogo.setHeight("2.8vh");
        mainHeaderDiv.add(new Label("LEAN - Lean Enterprise ANalytics"));
        logoDiv.add(leanLogo);
        loginInfoDiv.add(new Label("Login Info"));

        add(logoDiv, mainHeaderDiv, loginInfoDiv);

    }

}
