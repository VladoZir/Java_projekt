package hr.java.waterUsage.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Bill<T extends BigDecimal> extends IdEntity implements Serializable {

    //cijena litre vode 2,34 eura
    private String address;
    private  T totalBill;
    private T totalWaterUsed;

    public Bill(Long id, String address, T totalBill, T totalWaterUsed) {
        super(id);
        this.address = address;
        this.totalBill = totalBill;
        this.totalWaterUsed = totalWaterUsed;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public  T getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(T totalBill) {
        this.totalBill = totalBill;
    }

    public T getTotalWaterUsed() {
        return totalWaterUsed;
    }

    public void setTotalWaterUsed(T totalWaterUsed) {
        this.totalWaterUsed = totalWaterUsed;
    }

}
