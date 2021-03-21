package org.lean.ui.core.gui.vaadin.components.toolbar;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.lean.ui.core.ConstUi;

public class LeanToolbar extends Composite<Div> {

    public Component toolbarLayout;
    private ORIENTATION orientation;

    public enum ORIENTATION {
        VERTICAL,
        HORIZONTAL
    }

    public LeanToolbar(){
        this(ORIENTATION.HORIZONTAL);
        this.getContent().setSizeFull();
    }

    public LeanToolbar(ORIENTATION orientation){
        this.setOrientation(orientation);
    }

    public void setOrientation(ORIENTATION toolbarOrientation){
        getContent().setId("lean-toolbar");

        switch(toolbarOrientation){
            case VERTICAL:
                this.orientation = toolbarOrientation;
                toolbarLayout = new VerticalLayout();
                ((VerticalLayout)toolbarLayout).setWidth(ConstUi.VBAR_WIDTH);
                ((VerticalLayout)toolbarLayout).setHeightFull();
                ((VerticalLayout)toolbarLayout).setPadding(false);
                ((VerticalLayout)toolbarLayout).setSpacing(false);
                ((VerticalLayout)toolbarLayout).setMargin(false);
                break;
            case HORIZONTAL:
                this.orientation = toolbarOrientation;
                toolbarLayout = new HorizontalLayout();
                ((HorizontalLayout)toolbarLayout).setHeight(ConstUi.HBAR_HEIGHT);
                ((HorizontalLayout)toolbarLayout).setWidthFull();
                ((HorizontalLayout)toolbarLayout).setPadding(false);
                ((HorizontalLayout)toolbarLayout).setSpacing(false);
                ((HorizontalLayout)toolbarLayout).setMargin(false);
                break;
            default:
                break;
        }
        getContent().add(toolbarLayout);
    }

    public ORIENTATION getOrientation(){
        return orientation;
    }

    public void add(Component... children){
        if(isHorizontal()){
            ((HorizontalLayout)toolbarLayout).add(children);
        }else if(isVertical()){
            ((VerticalLayout)toolbarLayout).add(children);
        }
    }

    public boolean isHorizontal(){
        if (orientation.equals(ORIENTATION.HORIZONTAL)){
            return true;
        }else{
            return false;
        }
    }

    public boolean isVertical(){
        if(orientation.equals(ORIENTATION.VERTICAL)){
            return true;
        }else{
            return false;
        }
    }
}
