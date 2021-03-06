package org.g6.laas.core.format;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.g6.laas.core.exception.InputFormatException;
import org.g6.laas.core.exception.LaaSCoreRuntimeException;
import org.g6.laas.core.field.*;
import org.g6.laas.core.log.line.Line;
import org.g6.laas.core.log.line.LineAttributes;
import org.g6.laas.core.log.line.LogLine;
import org.g6.laas.core.log.result.BasicSplitResult;
import org.g6.laas.core.log.result.SplitResult;
import org.g6.util.Constants;
import org.g6.util.RegexUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Data
@NoArgsConstructor
public final class DefaultInputFormat implements InputFormat {

    private List<LineAttributes> lineAttrList;

    @Override
    public SplitResult getSplits(Line line) {
        String lineSplitRegex = null;
        List<FieldFormat> fieldFormatList = null;
        List<String> errorKeyList = new ArrayList<>();
        int counter = 0;

        for (LineAttributes lineAttr : lineAttrList) {
            String lineFormatKey = lineAttr.getKey();
            if (lineFormatKey.startsWith(Constants.REGEX_PREFIX)) { // the key is regex
                String regex = lineFormatKey.substring(Constants.REGEX_PREFIX.length());
                String matchedValue = RegexUtil.getValue(line.getContent(), regex);

                if (!StringUtils.isBlank(matchedValue)) {
                    errorKeyList.add(lineFormatKey);
                    counter++;
                    fieldFormatList = lineAttr.getFieldFormats();
                    lineSplitRegex = lineAttr.getSplitRegex();
                }
            } else {
                if (line.getContent().contains(lineFormatKey)) {
                    errorKeyList.add(lineFormatKey);
                    counter++;
                    fieldFormatList = lineAttr.getFieldFormats();
                    lineSplitRegex = lineAttr.getSplitRegex();
                }
            }
        }

        if (counter > 1) {
            StringBuffer sb = new StringBuffer();
            sb.append("Found more than one formats for the line which number is ")
                    .append(line.getLineNumber())
                    .append(" in ")
                    .append(line.getFile().getName())
                    .append(". The key words matched are: ");
            int keyCount = 1;
            for (String key : errorKeyList) {
                sb.append(key);
                if (errorKeyList.size() != keyCount) {
                    sb.append(", ");
                }
                keyCount++;
            }
            throw new InputFormatException(sb.toString());
        } else if (counter == 0) {
            log.error("No matching split format defined for " + line.getContent());
            throw new InputFormatException("No matching split format defined for " + line.getContent());
        }
        String[] fieldContents = RegexUtil.getValues(line.getContent(), lineSplitRegex);
        //in this case, the default format is used to split. But the existing format including default one
        // may not be available for it, for example, if user add some comments in the log file
        if (fieldContents == null) {
            log.warn(line.getLineNumber() + ": can not split the line : " + line.getContent() + " with the split regex : " + lineSplitRegex);
            return null;
        }
        Collection<Field> fieldList = new ArrayList<>();

        for (int i = 0; i < fieldContents.length; i++) {
            Field f = null;
            FieldFormat ff = fieldFormatList.get(i);
            String fieldFormatType = ff.getType();

            if (fieldFormatType.equals(Constants.FIELD_FORMAT_TYPE_STRING)) {
                f = new TextField(ff.getName(), fieldContents[i]);
            } else if (fieldFormatType.equals(Constants.FIELD_FORMAT_TYPE_INTEGER)) {
                f = new IntegerField(ff.getName(), fieldContents[i]);
            } else if (fieldFormatType.equals(Constants.FIELD_FORMAT_TYPE_DATETIME)) {
                f = new DateTimeField(ff.getName(), fieldContents[i], ff.getDateFormat());
            } else if (fieldFormatType.equals(Constants.FIELD_FORMAT_TYPE_DOUBLE)) {
                f = new DoubleField(ff.getName(), fieldContents[i]);
            }

            if (ff.isSortable() && (line instanceof LogLine)) {

                ((LogLine) line).setSortedField(f);
            }
            fieldList.add(f);
        }
        return new BasicSplitResult(fieldList);
    }

}
