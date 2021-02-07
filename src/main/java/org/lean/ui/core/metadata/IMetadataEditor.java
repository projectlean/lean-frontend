package org.lean.ui.core.metadata;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Image;
import org.apache.hop.core.exception.HopException;

public interface IMetadataEditor {

    String getTitle();

    Image getTitleImage();

    String getTitleToolTip();

    void createControl(Component var1);

    boolean isChanged();

    void save() throws HopException;

    void saveAs(String var1) throws HopException;
/*

    boolean setFocus();

    void dispose();
*/
}
