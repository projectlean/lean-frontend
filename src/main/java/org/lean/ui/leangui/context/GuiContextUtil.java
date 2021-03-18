package org.lean.ui.leangui.context;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.apache.hop.core.gui.Point;
import org.apache.hop.core.gui.plugin.IGuiActionLambda;
import org.apache.hop.core.gui.plugin.action.GuiAction;
import org.apache.hop.core.gui.plugin.action.GuiActionType;
import org.lean.ui.core.ContextDialog;
import org.lean.ui.layout.LeanGuiLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//@VaadinSessionScope
public class GuiContextUtil {

    private static GuiContextUtil instance;
//    private static LeanGuiLayout leanGuiLayout;

    private GuiContextUtil(){

    }

    public static final GuiContextUtil getInstance() {
        if(instance == null){
            instance = new GuiContextUtil();
        }
        return instance;
    }

    public final List<GuiAction> getContextActions(IActionContextHandlersProvider provider, GuiActionType actionType, String contextId){
        return filterHandlerActions(provider.getContextHandlers(), actionType, contextId);
    }

      /**
   * Filter out the actions with the given type, return a new list.
   *
   * @param guiActions The list of actions to filter
   * @param actionType The type to filter out
   * @return A new list with only the actions of the specified type
   */
  public final List<GuiAction> filterActions(List<GuiAction> guiActions, GuiActionType actionType ) {
    List<GuiAction> filtered = new ArrayList<>();
    for ( GuiAction guiAction : guiActions ) {
      if ( guiAction.getType().equals( actionType ) ) {
        filtered.add( guiAction );
      }
    }
    return filtered;
  }

    /**
     * Ask for all the actions from the list of context handlers. Then filter out the actions of a particular type.
     *
     * @param handlers
     * @param actionType
     * @return
     */
    public final List<GuiAction> filterHandlerActions( List<IGuiContextHandler> handlers, GuiActionType actionType, String contextId ) {
        List<GuiAction> filtered = new ArrayList<>();
        for ( IGuiContextHandler handler : handlers ) {
            filtered.addAll( filterActions( handler.getSupportedActions(), actionType ) );
        }
        return filtered;
    }

    public final void handleActionSelection(LeanGuiLayout leanGuiLayout, String message, IActionContextHandlersProvider provider, GuiActionType actionType, String contextId ) {
        handleActionSelection(leanGuiLayout, message, null, provider, actionType, contextId );
    }

    public final void handleActionSelection(LeanGuiLayout leanGuiLayout, String message, Point clickLocation, IActionContextHandlersProvider provider, GuiActionType actionType, String contextId ) {
        handleActionSelection(leanGuiLayout, message, clickLocation, provider, actionType, contextId, false );
    }

    public final void handleActionSelection(LeanGuiLayout leanGuiLayout, String message, Point clickLocation, IActionContextHandlersProvider provider, GuiActionType actionType, String contextId, boolean sortByName ) {
        // Get the list of create actions in the Hop UI context...
        //
        List<GuiAction> actions = getContextActions( provider, actionType, contextId );
        if ( actions.isEmpty() ) {
            return;
        }
        if ( sortByName ) {
            Collections.sort( actions, Comparator.comparing( GuiAction::getName ) );
        }

        handleActionSelection(leanGuiLayout, message, clickLocation, new GuiContextHandler( contextId, actions ) );
    }

    public boolean handleActionSelection(LeanGuiLayout leanGuiLayout, String message, IGuiContextHandler contextHandler ) {
        return handleActionSelection(leanGuiLayout, message, null, contextHandler );
    }

/**
     * @param message
     * @param clickLocation
     * @param contextHandler
     * @return true if the action dialog lost focus
     */

    public synchronized boolean handleActionSelection(LeanGuiLayout leanGuiLayout, String message, Point clickLocation, IGuiContextHandler contextHandler ) {
        List<GuiAction> actions = contextHandler.getSupportedActions();
        if ( actions.isEmpty() ) {
            return false;
        }


//        try {
//
//            synchronized () {
                // TODO: add to leanGuiLayout dialogMap
//                ContextDialog contextDialog = new ContextDialog(leanGuiLayout, "Lean Context Dialog", clickLocation, actions, contextHandler.getContextId());

                List<String> fileTypes = new ArrayList<>();
                for ( GuiAction action : actions ) {
                    fileTypes.add( action.getType().name() + " - " + action.getName() + " : " + action.getTooltip() );
                }

                ContextDialog contextDialog = new ContextDialog(leanGuiLayout, message, clickLocation, actions, contextHandler.getContextId() );
                contextDialog.openContextDialog();
                contextDialog.addOpenedChangeListener(e -> {
                   if(!e.isOpened()){
                       GuiAction selectedAction = contextDialog.getSelectedAction();
                       if ( selectedAction != null ) {
                           IGuiActionLambda<?> actionLambda = selectedAction.getActionLambda();
                           actionLambda.executeAction( contextDialog.isShiftClicked(), contextDialog.isCtrlClicked() );
                       } else {
//                           return contextDialog.isFocusLost();
                       }
                   }
                });
//            }
//        } catch ( Exception e ) {
//            new ErrorDialog("Error", "An error occurred executing action", e );
//        }
        return false;
    }
}
