package cn.com.agree.evs.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import cn.com.agree.evs.common.BaseKeys;

import java.util.ArrayList;
import java.util.List;

@Component
public class PrintDataParser {
    @Autowired
    private PdfConfig pdfConfig;
    @Autowired
    Environment environment;
    private String record_printType = "";

    public List<SubPrintData> parsePrintData(String printData) throws Exception {
        int index = 0;
        int record_index = 0;
        boolean isRecorded = false;
        List<SubPrintData> subDataList = new ArrayList<SubPrintData>();
        SubPrintData spd = null;
        while (index < printData.length()) {
            if (printData.charAt(index) == BaseKeys.PRINT_ORDER_SIGNAL) {
                if (spd == null) {
                    spd = new SubPrintData();
                }
                if (!isRecorded) {
                    spd.setData(printData.substring(record_index, index));
                    isRecorded = true;
                    addSubData(subDataList, spd);
                    spd = null;
                    continue;
                } else {
                    index = getPrintType(subDataList, spd, printData, index);
                    record_index = index;
                    isRecorded = false;
                }
            } else if (printData.charAt(index) == '\n') {
                if (spd == null) {
                    spd = new SubPrintData();
                }
                if (!isRecorded) {
                    spd.setData(printData.substring(record_index, index));
                    isRecorded = true;
                    addSubData(subDataList, spd);
                    spd = null;
                    continue;
                } else {
                    spd.setDataType(BaseKeys.ENTER);
                    addSubData(subDataList, spd);
                    spd = null;
                    record_index = index + 1;
                    isRecorded = false;
                }
            }
            if (index == printData.length() - 1 && record_index <= index) {
                if (spd == null) {
                    spd = new SubPrintData();
                }
                spd.setData(printData.substring(record_index, index + 1));
                addSubData(subDataList, spd);
            }
            index++;
        }
        return subDataList;
    }

    public int getPrintType(List<SubPrintData> subDataList, SubPrintData spd, String printData, int index) throws Exception {
        int temp_pos = 0;
        SubPrintData enter_data = null;
        for (int i = index + 1 + 1; i < printData.length(); i++) {
            if (printData.charAt(i) != '^'
                    && printData.charAt(i) != ' ') {
                if (printData.charAt(i) == '\n') {
                    enter_data = new SubPrintData();
                    enter_data.setDataType(BaseKeys.ENTER);
                    addSubData(subDataList, enter_data);
                    temp_pos = i + 1;
                } else {
                    temp_pos = i;
                }
                break;
            } else if (printData.charAt(i) == '^') {
                i = i + 1;
                continue;
            }
        }
        String printOrder = printData.substring(index, temp_pos).replaceAll(" ", "");
        if (printData.substring(index, temp_pos).indexOf("\n") == -1) {
            StringBuffer blank = new StringBuffer();
            for (int i = 0; i < temp_pos - index - printOrder.length(); i++) {
                blank.append(" ");
            }
            enter_data = new SubPrintData();
            enter_data.setData(new String(blank));
            addSubData(subDataList, enter_data);
        }
        String print_type = "";
        if (printOrder.equals(pdfConfig.getCancelHeight() + pdfConfig.getCancelWidth())
                || printOrder.equals(pdfConfig.getCancelWidth() + pdfConfig.getCancelHeight())) {
            print_type = BaseKeys.order_cancelAllOrders;
        } else {
            if (printOrder.lastIndexOf("^") > 0 && (printOrder.startsWith(pdfConfig.getCancelWidth())
                    || printOrder.startsWith(pdfConfig.getCancelHeight()))) {
                printOrder = printOrder.substring(2);
            }
            print_type = environment.getProperty(printOrder);
        }
        if (print_type.equals(BaseKeys.order_cancelHeight)
                && record_printType.equals(BaseKeys.order_doubleHW)) {
            print_type = BaseKeys.order_doubleWidth;
        } else if (print_type.equals(BaseKeys.order_cancelWidth)
                && record_printType.equals(BaseKeys.order_doubleHW)) {
            print_type = BaseKeys.order_doubleHeight;
        }
        record_printType = print_type;
        spd.setDataType(print_type);
        return temp_pos;
    }

    public List<SubPrintData> addSubData(List<SubPrintData> subDataList, SubPrintData spd) {
        if (!spd.getData().equals("") || !spd.getDataType().equals("")) {
            subDataList.add(spd);
        }
        return subDataList;
    }
}
