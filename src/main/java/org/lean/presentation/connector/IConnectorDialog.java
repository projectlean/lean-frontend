package org.lean.presentation.connector;

import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.connector.type.ILeanConnector;

public interface IConnectorDialog {

    void openConnector(IHopMetadataProvider metadataProvider, LeanPresentation presentation, ILeanConnector connector);

    LeanConnector getConnector();
}
