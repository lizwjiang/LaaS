package org.g6.laas.sm.task;

import lombok.Data;
import org.g6.laas.core.engine.context.SimpleAnalysisContext;
import org.g6.laas.core.engine.task.AbstractAnalysisTask;
import org.g6.laas.core.file.ILogFile;
import org.g6.laas.core.file.LogFile;
import org.g6.laas.core.format.provider.DefaultFormatProvider;
import org.g6.laas.core.format.provider.FormatProvider;
import org.g6.laas.core.log.handler.ConcreteLogHandler;
import org.g6.laas.core.log.line.Line;
import org.g6.laas.core.log.handler.LogHandler;
import org.g6.laas.core.log.result.SplitResult;
import org.g6.laas.core.rule.RegexRule;
import org.g6.laas.core.rule.Rule;
import org.g6.laas.core.rule.action.RuleAction;
import org.g6.laas.sm.exception.SMRuntimeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Johnson Jiang
 * @version 1.0
 * @since 1.0
 */
@Data
public class LoginTimeInfoTask extends AbstractAnalysisTask<Map<String, Double>> {
    List<Line> lines;
    SplitResult result;

    @Override
    protected Map<String, Double> process() {
        if (lines.size() > 1)
            throw new SMRuntimeException(new IllegalStateException("must be one record while querying login time info of SM"));

        if(!lines.isEmpty()){
            Map<String, Double> resultMap = new HashMap<>();
            resultMap.put("login_time", (Double)result.get("login_time").getValue());
            resultMap.put("rad_time", (Double)result.get("rad_time").getValue());
            resultMap.put("js_time", (Double)result.get("js_time").getValue());
            resultMap.put("log_time", (Double)result.get("log_time").getValue());
            resultMap.put("db_time", (Double)result.get("db_time").getValue());
            resultMap.put("cpu_time", (Double)result.get("cpu_time").getValue());
            return resultMap;
        }
        return null;
    }

    public LoginTimeInfoTask(String file) {
        lines = new ArrayList<>();
        ILogFile logFile = new LogFile(file);

        FormatProvider provider = new DefaultFormatProvider("SMRTE_SM_LOG");

        Rule rule = new RegexRule("RTE D Response-Total.+format:sc\\.manage\\.ToDo\\.g application:display");
        rule.addAction(new RuleAction() {
            @Override
            public void satisfied(Rule rule, Object content) {
                Line line = (Line) content;
                result = line.split();
                lines.add(line);
            }
        });

        LogHandler handler = new ConcreteLogHandler(logFile, null);

        SimpleAnalysisContext context = new SimpleAnalysisContext();
        context.setHandler(handler);
        context.setInputForm(provider.getInputFormat());
        context.getRules().add(rule);
        setContext(context);
    }
}
