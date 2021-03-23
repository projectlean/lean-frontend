package org.lean.ui.plugins.perspective;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.apache.hop.core.plugins.Plugin;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.ui.layout.LeanGuiLayout;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

//@VaadinSessionScope
public class LeanPerspectiveManager {

    private LeanGuiLayout leanGuiLayout;
    private ILeanPerspective activePerspective;

    private Map<Class<? extends ILeanPerspective>, ILeanPerspective> perspectivesMap;

    private final ConcurrentLinkedQueue<ILeanPerspectiveListener> listeners;

    private IHopMetadataProvider metadataProvider;

    public LeanPerspectiveManager(LeanGuiLayout leanGuiLayout, IHopMetadataProvider metadataProvider){
        this.leanGuiLayout = leanGuiLayout;
        this.metadataProvider = metadataProvider;
        this.perspectivesMap = new HashMap<>();
        this.listeners = new ConcurrentLinkedQueue<>();
    }

    public void addPerspective(ILeanPerspective perspective){
        perspectivesMap.put(perspective.getClass(), perspective);
    }

    public ILeanPerspective getDialog(Class<? extends ILeanPerspective> perspectiveClass){
        return perspectivesMap.get(perspectiveClass);
    }

    public void setActivePerspective(ILeanPerspective perspective){
        for(ILeanPerspective leanPerspective : perspectivesMap.values()){
            if(leanPerspective.getClass().equals(perspective.getClass())) {
                // TODO: cycle through perspectives instead of removing and re-adding
                leanGuiLayout.mainBody.removeAll();
                leanGuiLayout.mainBody.add((Component) perspective);
                ((Component)perspective).setVisible(true);
                activePerspective = perspective;




//            }else{
//                ((Component) perspective).setVisible(false);
/*
                Tabs perspectiveTabs = leanGuiLayout.mainBody.perspectiveTabs;
                List<Component> perspectiveTabList = perspectiveTabs.getChildren().collect(Collectors.toList());
                Iterator<Component> perspectiveIterator = perspectiveTabList.iterator();
                while(perspectiveIterator.hasNext()){
                    Tab perspectiveTab = (Tab)perspectiveIterator.next();
                    perspectiveTab.getId().ifPresent(tabId -> {
                        String pluginId = leanPerspective.getClass().getAnnotation(LeanPerspectivePlugin.class).id();
                        if(tabId.equals(pluginId + "-tab")){
                            activePerspective = leanPerspective;
                            perspectiveTabs.setSelectedTab((Tab)perspectiveTab);
                        }
                    });
                }
*/
            }
        }
    }

    public ILeanPerspective getActivePerspective(){
        return activePerspective;
    }

    public boolean isActivePerspective(ILeanPerspective perspective){
        if(perspective != null){
            if(perspective.getClass().equals(activePerspective.getClass())){
                return true;
            }
        }
        return false;
    }

    public ILeanPerspective findPerspective(Class<? extends ILeanPerspective> perspectiveClass){
        for(ILeanPerspective perspective : perspectivesMap.values()){
            if(perspective.getClass().equals(perspectiveClass)){
                return perspective;
            }
        }
        return null;
    }

    public void addPerspectiveListener(ILeanPerspectiveListener listener){
        if(listener != null){
            listeners.add(listener);
        }
    }

    public void removePerspectiveListener(ILeanPerspectiveListener listener){
        if(listener != null){
            listeners.remove(listener);
        }
    }

    public void notifyPerspectiveActivated(ILeanPerspective perspective){
        for(ILeanPerspectiveListener listener : this.listeners){
            listener.perspectiveActivated(perspective);
        }
    }

/*
    public Map<Class<? extends ILeanPerspective>, ILeanPerspective> getPerspectives(){
        return perspectivesMap;
    }
*/

    public Map<Class<? extends ILeanPerspective>, ILeanPerspective> getPerspectives(){
        if(perspectivesMap != null){
            try{
                PluginRegistry pluginRegistry = PluginRegistry.getInstance();
                List<Plugin> perspectivePlugins = pluginRegistry.getPlugins(LeanPerspectivePluginType.class);
                Collections.sort(perspectivePlugins, Comparator.comparing(p -> p.getIds()[0]));
                for(Plugin perspectivePlugin : perspectivePlugins){
                    Class<ILeanPerspective> perspectiveClass = pluginRegistry.getClass(perspectivePlugin, ILeanPerspective.class);
                    ILeanPerspective perspective = perspectiveClass.newInstance();
                    perspective.initialize(leanGuiLayout, metadataProvider);
                    perspectivesMap.put(perspective.getClass(), perspective);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return perspectivesMap;
    }
}
