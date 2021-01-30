package org.lean.ui;

public class LeanGuiImpl implements ISingletonProvider{

    private static LeanGui instance;

    public Object getInstanceInternal(){
        if(instance == null){
            instance = new LeanGui();
        }
        return instance;
    }
}
