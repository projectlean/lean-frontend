package org.lean.ui.core;

import com.vaadin.flow.component.html.Image;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.extension.ExtensionPointHandler;
import org.apache.hop.core.extension.HopExtensionPoint;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.metadata.api.HopMetadata;
import org.apache.hop.metadata.api.IHopMetadata;
import org.apache.hop.metadata.api.IHopMetadataSerializer;
import org.apache.hop.ui.core.dialog.ErrorDialog;
import org.apache.hop.ui.core.metadata.MetadataFileTypeHandler;
import org.lean.ui.core.gui.GuiResource;
import org.lean.ui.core.metadata.IMetadataEditor;
import org.lean.ui.core.metadata.MetadataEditorDialog;
import org.lean.ui.core.metadata.MetadataManager;
import org.lean.ui.plugins.perspective.metadata.MetadataPerspective;
import org.lean.ui.util.VaadinSvgImageUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class MetadataEditor<T extends IHopMetadata> extends MetadataFileTypeHandler
    implements IMetadataEditor {

    private static final Class<?> PKG = MetadataEditorDialog.class; // For Translator

    private MetadataPerspective metadataPerspective;
    protected MetadataManager<T> manager;
    protected T metadata;

    protected String title;
    protected String toolTip;
    protected Image titleImage;
    protected Image image;
    protected boolean isChanged = false;
    protected String originalName;

    public MetadataEditor(MetadataPerspective metadataPerspective, MetadataManager<T> manager, T metadata) {
        this.metadataPerspective = metadataPerspective;
        this.manager = manager;
        this.metadata = metadata;
        this.originalName = metadata.getName();

        // Search metadata annotation
        Type superclass = getClass().getGenericSuperclass();
        ParameterizedType parameterized = (ParameterizedType) superclass;
        Class<?> managedClass = (Class<?>) parameterized.getActualTypeArguments()[0];
        HopMetadata annotation = managedClass.getAnnotation(HopMetadata.class);

        // Initialize editor
        this.setTitle(metadata.getName());
        this.setTitleToolTip(annotation.name());
//        this.setTitleImage(
//                GuiResource.getInstance()
//                        .getImage(
//                                annotation.image(),
//                                managedClass.getClassLoader(),
//                                org.apache.hop.ui.core.ConstUi.SMALL_ICON_SIZE,
//                                org.apache.hop.ui.core.ConstUi.SMALL_ICON_SIZE));

        // Use VaadinSvgImageUtil because GuiResource cache have small icon.
        this.setImage(
                VaadinSvgImageUtil.getImage(
                        managedClass.getClassLoader(),
                        annotation.image(),
                        ConstUi.LARGE_ICON_SIZE,
                        ConstUi.LARGE_ICON_SIZE));
    }

    public Image getImage() {
        return image;
    }

    protected void setImage(Image image) {
        this.image = image;
    }

    @Override
    public String getTitle() {
        return title;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Image getTitleImage() {
        return titleImage;
    }

    protected void setTitleImage(Image image) {
        this.titleImage = image;
    }

    @Override
    public String getTitleToolTip() {
        return toolTip;
    }

    protected void setTitleToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    @Override
    public boolean isChanged() {
        return isChanged;
    }

    protected void resetChanged() {
        this.isChanged = false;
    }

    protected void setChanged() {
        if (this.isChanged == false) {
            this.isChanged = true;
            metadataPerspective.updateEditor(this);
        }
    }

    /** Inline usage: copy information from the metadata onto the various widgets */
    public abstract void setWidgetsContent();

    /**
     * Inline usage: Reads the information or state of the various widgets and modifies the provided
     * metadata object.
     *
     * @param meta The metadata object to populate from the widgets
     */
    public abstract void getWidgetsContent(T meta);

    @Override
    public boolean isCloseable() {

        // Check if the metadata is saved. If not, ask for it to be saved.
        //
        if (isChanged()) {

/*
            MessageBox messageDialog =
                    new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
            messageDialog.setText(manager.getManagedName());
            messageDialog.setMessage(
                    BaseMessages.getString(
                            PKG, "MetadataEditor.WantToSaveBeforeClosing.Message", getTitle()));

            int answer = messageDialog.open();

            if ((answer & SWT.YES) != 0) {
                try {
                    save();
                } catch (Exception e) {
                    new ErrorDialog(getShell(), "Error", "Error preparing editor close", e);
                    return false;
                }
            }

            if ((answer & SWT.CANCEL) != 0) {
                return false;
            }
*/
        }

        return true;
    }

    @Override
    public void save() throws HopException {

        getWidgetsContent(metadata);
        String name = metadata.getName();

        boolean isCreated = false;
        boolean isRename = false;

        if (StringUtils.isEmpty(name)) {
            throw new HopException(BaseMessages.getString(PKG, "MetadataEditor.Error.NoName"));
        }

        if (StringUtils.isEmpty(originalName)) {
            isCreated = true;
        }
        // If rename
        //
        else if (!originalName.equals(name)) {

            // See if the name collides with an existing one...
            //
            IHopMetadataSerializer<T> serializer = manager.getSerializer();

            if (serializer.exists(name)) {
                throw new HopException(
                        BaseMessages.getString(
                                PKG, "MetadataEditor.Error.NameAlreadyExists", name));
            } else {
                isRename = true;
            }
        }

        // Save it in the metadata
        manager.getSerializer().save(metadata);

        if (isCreated)
            ExtensionPointHandler.callExtensionPoint(
                    metadataPerspective.getLog(), manager.getVariables(), HopExtensionPoint.HopGuiMetadataObjectCreated.id, metadata );
        else
            ExtensionPointHandler.callExtensionPoint(
                    metadataPerspective.getLog(), manager.getVariables(), HopExtensionPoint.HopGuiMetadataObjectUpdated.id, metadata );

        // Reset changed flag
        this.isChanged = false;
        this.title = metadata.getName();

        if (isRename) {
            manager.getSerializer().delete(originalName);
            this.originalName = metadata.getName();
        }
        metadataPerspective.updateEditor(this);
    }

    @Override
    public void saveAs(String filename) throws HopException {
        throw new HopException("Metadata editor doesn't support saveAs");
    }

/*
    @Override
    public boolean setFocus() {
        return true;
    }
*/

}
