package org.lean.core.gui.plugin;

import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.Const;
import org.apache.hop.core.util.StringUtil;
import org.apache.hop.i18n.BaseMessages;

public class BaseGuiElements {
    protected String calculateI18n( String i18nPackage, String string, Class<?> resourceClass ) {
        if ( StringUtils.isEmpty( i18nPackage ) ) {
            return string;
        }
        if ( StringUtils.isEmpty( string ) ) {
            return null;
        }
        if (string.startsWith( Const.I18N_PREFIX )) {
            String[] parts = string.split(":");
            if (parts.length == 3) {
                String alternativePackage = Const.NVL(parts[1], i18nPackage);
                String key = parts[2];
                return BaseMessages.getString( alternativePackage, key, resourceClass );
            }
        }

        String translation = BaseMessages.getString( i18nPackage, string, resourceClass );
        if (translation.startsWith( "!" ) && translation.endsWith( "!" )) {
            // Just return the original string, we did our best
            //
            return string;
        }
        return translation;
    }

    protected String calculateI18nPackage( Class<?> i18nPackageClass, String i18nPackage, String guiPluginClass ) {
        if ( StringUtils.isNotEmpty( i18nPackage ) ) {
            return i18nPackage;
        }
        if ( Void.class.equals( i18nPackageClass ) ) {

            int lastDotIndex = guiPluginClass.lastIndexOf( "." );
            if (lastDotIndex<0) {
                return null;
            } else {
                return guiPluginClass.substring( 0, lastDotIndex );
            }
        }
        return i18nPackageClass.getPackage().getName();
    }

    protected String calculateGetterMethod(GuiWidgetElement guiElement, String fieldName ) {
        if ( StringUtils.isNotEmpty( guiElement.getterMethod() ) ) {
            return guiElement.getterMethod();
        }
        String getter = "get" + StringUtil.initCap( fieldName );
        return getter;
    }


    protected String calculateSetterMethod( GuiWidgetElement guiElement, String fieldName ) {
        if ( StringUtils.isNotEmpty( guiElement.setterMethod() ) ) {
            return guiElement.setterMethod();
        }
        String getter = "set" + StringUtil.initCap( fieldName );
        return getter;
    }
}
