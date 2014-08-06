package me.sbozhko.test.entity;

public class DataEntity {
    private Long value;
    private Long timestamp;

    public DataEntity() {
    }

    public DataEntity(Long value, Long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
