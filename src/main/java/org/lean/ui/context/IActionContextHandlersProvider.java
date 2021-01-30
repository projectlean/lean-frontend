package org.lean.ui.context;

import java.util.List;

public interface IActionContextHandlersProvider {

    List<IGuiContextHandler> getContextHandlers();
}
