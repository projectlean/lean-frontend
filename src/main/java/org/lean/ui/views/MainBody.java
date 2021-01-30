package org.lean.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import org.lean.ui.plugins.perspective.ILeanPerspective;
import org.lean.ui.plugins.perspective.LeanPerspectivePlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainBody extends HorizontalLayout {

    public Div mainPerspectiveHolderDiv;

    public Tabs perspectiveTabs;
    public Map<Tab, Component> perspectiveMap;
    public Set<Component> activePerspective;

    private ILeanPerspective emptyPerspective;

    public MainBody(){
        this.setPadding(false);
        this.setMargin(false);
        this.setSpacing(false);
        this.setSizeFull();
        this.setId("main-body");
        this.add(new Label("main body"));

/*
        mainPerspectiveHolderDiv = new Div();
        mainPerspectiveHolderDiv.setSizeFull();
        mainPerspectiveHolderDiv.setId("main-perspective-holder");
        mainPerspectiveHolderDiv.add(new Label("placeholder"));

        Div placeHolderDiv = new Div();
        mainPerspectiveHolderDiv.add(placeHolderDiv);


        perspectiveMap = new HashMap<>();
        perspectiveTabs = new Tabs();
        perspectiveTabs.setOrientation(Tabs.Orientation.VERTICAL);

        activePerspective = Stream.of(placeHolderDiv).collect(Collectors.toSet());
        perspectiveTabs.addSelectedChangeListener(event -> {
            activePerspective.forEach(page -> page.setVisible(false));
            activePerspective.clear();
            Component selectedPerspective = perspectiveMap.get(perspectiveTabs.getSelectedTab());
            if(selectedPerspective != null){
                selectedPerspective.setVisible(true);
                activePerspective.add(selectedPerspective);
            }
        });

        add(mainToolbar, mainPerspectiveHolderDiv);
*/
    }

/*
    public void addTab(ILeanPerspective perspective){
        Image perspectiveIcon = new Image(perspective.getClass().getAnnotation(LeanPerspectivePlugin.class).image(), perspective.getClass().getAnnotation(LeanPerspectivePlugin.class).name());
        perspectiveIcon.setWidth("1.3vw");
        Tab perspectiveTab = new Tab(perspectiveIcon);
        perspectiveTab.setId(perspective.getClass().getAnnotation(LeanPerspectivePlugin.class).id() + "-tab");
        perspectiveTabs.add(perspectiveTab);
        perspectiveMap.put(perspectiveTab, (Component)perspective);
        ((Component) perspective).setVisible(false);
        mainPerspectiveHolderDiv.add((Component) perspective);
    }
*/
}
