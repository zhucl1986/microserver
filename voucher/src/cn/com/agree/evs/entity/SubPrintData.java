package cn.com.agree.evs.entity;

import org.springframework.stereotype.Component;

@Component
public class SubPrintData {

    private String dataType = "";

    private String data = "";

    public SubPrintData() {
    }

    public SubPrintData(String dataType, String data) {
        this.dataType = dataType;
        this.data = data;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SubPrintData{" +
                "dataType='" + dataType + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
