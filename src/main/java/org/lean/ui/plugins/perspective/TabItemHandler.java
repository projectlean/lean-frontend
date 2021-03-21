package org.lean.ui.plugins.perspective;

import com.vaadin.flow.component.tabs.Tab;
import org.lean.ui.plugins.file.ILeanFileTypeHandler;

import java.util.Objects;

public class TabItemHandler {

    private Tab tabItem;
    private ILeanFileTypeHandler typeHandler;

    public TabItemHandler(Tab tabItem, ILeanFileTypeHandler typeHandler){
        this.tabItem = tabItem;
        this.typeHandler = typeHandler;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }

        if(o == null || getClass() != o.getClass()){
            return  true;
        }
        TabItemHandler that = (TabItemHandler) o;
        return tabItem.equals(that.tabItem);
    }

    @Override
    public int hashCode(){
        return Objects.hash(tabItem);
    }

    public Tab getTabItem(){
        return tabItem;
    }

    public void setTabItem(Tab tabItem){
        this.tabItem = tabItem;
    }

    public ILeanFileTypeHandler getTypeHandler(){
        return typeHandler;
    }

    public void setTypeHandler(ILeanFileTypeHandler typeHandler){
        this.typeHandler = typeHandler;
    }
}
