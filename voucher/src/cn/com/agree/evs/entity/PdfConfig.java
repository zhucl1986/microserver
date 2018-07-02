package cn.com.agree.evs.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:resources/printer.properties")
@ConfigurationProperties(prefix = "pdf")
public class PdfConfig {
    private String doubleHeight;
    private String doubleWidth;
    private String doubleHW;
    private String cancelHeight;
    private String cancelWidth;
    private String userDefined_order1;
    private String userDefined_order2;
    private String userDefined_order3;
    private String userDefined_order4;
    private String userDefined_order5;
    private String cancelAllOrders;
    private String doubleHeight_font;
    private String doubleWidth_font;
    private String doubleHW_font;
    private String userDefined_order1_font;
    private String userDefined_order2_font;
    private String userDefined_order3_font;
    private String userDefined_order4_font;
    private String userDefined_order5_font;
    private String leftMargin;
    private String topMargin;
    private String chFont;
    private String enFont;
    private String col_span;
    private String row_span;
    private String doubleHeightAdval;
    private String doubleWidthAdval;
    private String doubleHWAdval;
    private String userDefined_order1_ratio;
    private String userDefined_order2_ratio;
    private String userDefined_order3_ratio;
    private String userDefined_order4_ratio;
    private String userDefined_order5_ratio;

    public String getDoubleHeight() {
        return doubleHeight;
    }

    public void setDoubleHeight(String doubleHeight) {
        this.doubleHeight = doubleHeight;
    }

    public String getDoubleWidth() {
        return doubleWidth;
    }

    public void setDoubleWidth(String doubleWidth) {
        this.doubleWidth = doubleWidth;
    }

    public String getDoubleHW() {
        return doubleHW;
    }

    public void setDoubleHW(String doubleHW) {
        this.doubleHW = doubleHW;
    }

    public String getCancelHeight() {
        return cancelHeight;
    }

    public void setCancelHeight(String cancelHeight) {
        this.cancelHeight = cancelHeight;
    }

    public String getCancelWidth() {
        return cancelWidth;
    }

    public void setCancelWidth(String cancelWidth) {
        this.cancelWidth = cancelWidth;
    }

    public String getUserDefined_order1() {
        return userDefined_order1;
    }

    public void setUserDefined_order1(String userDefined_order1) {
        this.userDefined_order1 = userDefined_order1;
    }

    public String getUserDefined_order2() {
        return userDefined_order2;
    }

    public void setUserDefined_order2(String userDefined_order2) {
        this.userDefined_order2 = userDefined_order2;
    }

    public String getUserDefined_order3() {
        return userDefined_order3;
    }

    public void setUserDefined_order3(String userDefined_order3) {
        this.userDefined_order3 = userDefined_order3;
    }

    public String getUserDefined_order4() {
        return userDefined_order4;
    }

    public void setUserDefined_order4(String userDefined_order4) {
        this.userDefined_order4 = userDefined_order4;
    }

    public String getUserDefined_order5() {
        return userDefined_order5;
    }

    public void setUserDefined_order5(String userDefined_order5) {
        this.userDefined_order5 = userDefined_order5;
    }

    public String getCancelAllOrders() {
        return cancelAllOrders;
    }

    public void setCancelAllOrders(String cancelAllOrders) {
        this.cancelAllOrders = cancelAllOrders;
    }

    public String getDoubleHeight_font() {
        return doubleHeight_font;
    }

    public void setDoubleHeight_font(String doubleHeight_font) {
        this.doubleHeight_font = doubleHeight_font;
    }

    public String getDoubleWidth_font() {
        return doubleWidth_font;
    }

    public void setDoubleWidth_font(String doubleWidth_font) {
        this.doubleWidth_font = doubleWidth_font;
    }

    public String getDoubleHW_font() {
        return doubleHW_font;
    }

    public void setDoubleHW_font(String doubleHW_font) {
        this.doubleHW_font = doubleHW_font;
    }

    public String getUserDefined_order1_font() {
        return userDefined_order1_font;
    }

    public void setUserDefined_order1_font(String userDefined_order1_font) {
        this.userDefined_order1_font = userDefined_order1_font;
    }

    public String getUserDefined_order2_font() {
        return userDefined_order2_font;
    }

    public void setUserDefined_order2_font(String userDefined_order2_font) {
        this.userDefined_order2_font = userDefined_order2_font;
    }

    public String getUserDefined_order3_font() {
        return userDefined_order3_font;
    }

    public void setUserDefined_order3_font(String userDefined_order3_font) {
        this.userDefined_order3_font = userDefined_order3_font;
    }

    public String getUserDefined_order4_font() {
        return userDefined_order4_font;
    }

    public void setUserDefined_order4_font(String userDefined_order4_font) {
        this.userDefined_order4_font = userDefined_order4_font;
    }

    public String getUserDefined_order5_font() {
        return userDefined_order5_font;
    }

    public void setUserDefined_order5_font(String userDefined_order5_font) {
        this.userDefined_order5_font = userDefined_order5_font;
    }

    public String getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(String leftMargin) {
        this.leftMargin = leftMargin;
    }

    public String getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(String topMargin) {
        this.topMargin = topMargin;
    }

    public String getChFont() {
        return chFont;
    }

    public void setChFont(String chFont) {
        this.chFont = chFont;
    }

    public String getEnFont() {
        return enFont;
    }

    public void setEnFont(String enFont) {
        this.enFont = enFont;
    }

    public String getCol_span() {
        return col_span;
    }

    public void setCol_span(String col_span) {
        this.col_span = col_span;
    }

    public String getRow_span() {
        return row_span;
    }

    public void setRow_span(String row_span) {
        this.row_span = row_span;
    }

    public String getDoubleHeightAdval() {
        return doubleHeightAdval;
    }

    public void setDoubleHeightAdval(String doubleHeightAdval) {
        this.doubleHeightAdval = doubleHeightAdval;
    }

    public String getDoubleWidthAdval() {
        return doubleWidthAdval;
    }

    public void setDoubleWidthAdval(String doubleWidthAdval) {
        this.doubleWidthAdval = doubleWidthAdval;
    }

    public String getDoubleHWAdval() {
        return doubleHWAdval;
    }

    public void setDoubleHWAdval(String doubleHWAdval) {
        this.doubleHWAdval = doubleHWAdval;
    }

    public String getUserDefined_order1_ratio() {
        return userDefined_order1_ratio;
    }

    public void setUserDefined_order1_ratio(String userDefined_order1_ratio) {
        this.userDefined_order1_ratio = userDefined_order1_ratio;
    }

    public String getUserDefined_order2_ratio() {
        return userDefined_order2_ratio;
    }

    public void setUserDefined_order2_ratio(String userDefined_order2_ratio) {
        this.userDefined_order2_ratio = userDefined_order2_ratio;
    }

    public String getUserDefined_order3_ratio() {
        return userDefined_order3_ratio;
    }

    public void setUserDefined_order3_ratio(String userDefined_order3_ratio) {
        this.userDefined_order3_ratio = userDefined_order3_ratio;
    }

    public String getUserDefined_order4_ratio() {
        return userDefined_order4_ratio;
    }

    public void setUserDefined_order4_ratio(String userDefined_order4_ratio) {
        this.userDefined_order4_ratio = userDefined_order4_ratio;
    }

    public String getUserDefined_order5_ratio() {
        return userDefined_order5_ratio;
    }

    public void setUserDefined_order5_ratio(String userDefined_order5_ratio) {
        this.userDefined_order5_ratio = userDefined_order5_ratio;
    }

}
