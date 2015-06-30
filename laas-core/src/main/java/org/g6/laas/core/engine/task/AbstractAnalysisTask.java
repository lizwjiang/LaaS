package org.g6.laas.core.engine.task;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.g6.laas.core.engine.AnalysisTask;
import org.g6.laas.core.engine.context.AnalysisContext;
import org.g6.laas.core.engine.context.SimpleAnalysisContext;
import org.g6.laas.core.exception.LaaSRuntimeException;
import org.g6.laas.core.log.Line;
import org.g6.laas.core.rule.Rule;
import org.g6.laas.core.rule.action.ActionCondition;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
@Data
@NoArgsConstructor
public abstract class AbstractAnalysisTask<T> implements AnalysisTask<T> {

  private AnalysisContext context;

  protected void started() {
    log.info("Task " + this.toString() + " started");
  }

  protected abstract T process();

  protected Iterator<? extends Line> openReader() {
    try {
      return context.getHandler().handle(context);
    } catch (IOException e) {
      throw new LaaSRuntimeException("open log handler failed.");
    }
  }

  protected void processRules() {
    Iterator<? extends Line> lines = openReader();
    Collection<Rule> rules = context.getRules();
    while (lines.hasNext()) {
      Line line = lines.next();
      for (Rule rule : rules) {
        if (rule.isSatisfied(line)) {
          rule.triggerAction(ActionCondition.SATISFIED);
        } else {
          rule.triggerAction(ActionCondition.NOTSATISFIED);
        }
      }
    }
  }

  protected void finished() {
    log.info("Task " + this.toString() + " finished");
  }

  public T analyze() {
    started();
    processRules();
    T result = process();
    finished();
    return result;
  }

  public AbstractAnalysisTask(AnalysisContext context){
    this.context = context;
  }

  public AbstractAnalysisTask(Collection<Rule> rules){
    this.context = new SimpleAnalysisContext();
  }
}
