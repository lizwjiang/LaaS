package org.g6.laas.sm.task;

import com.google.common.collect.Ordering;
import org.g6.laas.core.log.line.Line;
import org.g6.laas.core.log.line.LineComparator;
import org.g6.laas.core.rule.KeywordRule;
import org.g6.laas.core.rule.Rule;
import org.g6.laas.core.rule.action.RuleAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Get top N query from SM RTE log file
 *
 * @author Johnson Jiang
 * @version 1.0
 * @since 1.0
 */
public class TopNQueryTask extends SMRTETask<List<Line>> {
    private int N = 50;
    private List<Line> lines = new ArrayList<>();

    @Override
    protected List<Line> process() {
        Ordering ordering = Ordering.from(new LineComparator());
        return ordering.leastOf(lines, N);
    }

    public TopNQueryTask(int topN, String file) {
        this.N = topN;

        Rule rule = new KeywordRule("RTE D DBQUERY");
        rule.addAction(new RuleAction() {
            @Override
            public void satisfied(Rule rule, Object content) {
                Line line = (Line) content;
                line.split();
                lines.add(line);
            }
        });

        initContext(file, rule);
    }
}
