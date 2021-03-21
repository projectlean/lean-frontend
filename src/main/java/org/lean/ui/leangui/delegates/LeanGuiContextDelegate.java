package org.lean.ui.leangui.delegates;

import org.apache.hop.core.gui.Point;
import org.apache.hop.core.gui.plugin.action.GuiActionType;
import org.lean.ui.leangui.context.GuiContextUtil;
import org.lean.ui.layout.LeanGuiLayout;

public class LeanGuiContextDelegate {

    private LeanGuiLayout leanGuiLayout;

    public LeanGuiContextDelegate(LeanGuiLayout leanGuiLayout){
        this.leanGuiLayout = leanGuiLayout;
    }

    /**
     * Create a new file, ask which type of file or object you want created
     */
    public void fileNew(){

        int x = 50;
        int y = 50;
        GuiContextUtil.getInstance().handleActionSelection(leanGuiLayout, "Select the item to create", new Point(x, y), leanGuiLayout, GuiActionType.Create, "FileNew", true);

    }
}
