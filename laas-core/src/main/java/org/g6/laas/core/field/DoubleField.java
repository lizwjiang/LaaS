package org.g6.laas.core.field;

public class DoubleField extends AbstractField<Double> {
    public DoubleField(String content) {
        super(content);
    }

    @Override
    public Double getValue() {
        return Double.valueOf(getContent());
    }

    @Override
    public int compareTo(Field o) {
        return this.getValue().compareTo((Double) o.getValue());
    }
}
