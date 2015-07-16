package org.g6.laas.core.engine.context;


import org.g6.laas.core.format.InputFormat;
import org.g6.laas.core.log.handler.LogHandler;
import org.g6.laas.core.rule.Rule;

import java.util.Collection;

public interface AnalysisContext {

    InputFormat getInputFormat();

    LogHandler getHandler();

    Collection<Rule> getRules();

    void set(Object key, Object value);

    Object get(Object key);

}
