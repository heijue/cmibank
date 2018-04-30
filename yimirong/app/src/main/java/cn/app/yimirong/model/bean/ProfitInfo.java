package cn.app.yimirong.model.bean;

import java.io.Serializable;

/**
 * Created by user on 2017/8/30.
 */

public class ProfitInfo implements Serializable {
    public String pname;
    public double income;
    public String money;

    @Override
    public String toString() {
        return "ProfitInfo{" +
                "pname='" + pname + '\'' +
                ", income=" + income +
                ", money='" + money + '\'' +
                ", balance='" + balance + '\'' +
                '}';
    }

    public String balance;

}
