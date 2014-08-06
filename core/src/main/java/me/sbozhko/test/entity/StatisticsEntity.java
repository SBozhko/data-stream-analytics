package me.sbozhko.test.entity;

public class StatisticsEntity {
    private Long maxValue;
    private Long minValue;
    private double avgValue;

    public StatisticsEntity() {
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Long maxValue) {
        this.maxValue = maxValue;
    }

    public Long getMinValue() {
        return minValue;
    }

    public void setMinValue(Long minValue) {
        this.minValue = minValue;
    }

    public double getAvgValue() {
        return avgValue;
    }

    public void setAvgValue(double avgValue) {
        this.avgValue = avgValue;
    }

    @Override
    public String toString() {
        return "StatisticsEntity{" +
                "maxValue=" + maxValue +
                ", minValue=" + minValue +
                ", avgValue=" + avgValue +
                '}';
    }
}
