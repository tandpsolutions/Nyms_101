/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

/**
 *
 * @author BHAUMIK
 */
public class CompanySettingModel {

    private String ac_year;
    private String cash_ac_cd;
    private String sales_ac_cd;
    private String purchase_ac_cd;
    private String sales_ret_cd;
    private String pur_ret_cd;
    private String tax_ac_cd;
    private String default_ac_cd;
    private String disc_ac_cd;

    public String getDisc_ac_cd() {
        return disc_ac_cd;
    }

    public void setDisc_ac_cd(String disc_ac_cd) {
        this.disc_ac_cd = disc_ac_cd;
    }

    public String getDefault_ac_cd() {
        return default_ac_cd;
    }

    public void setDefault_ac_cd(String default_ac_cd) {
        this.default_ac_cd = default_ac_cd;
    }

    public String getAc_year() {
        return ac_year;
    }

    public void setAc_year(String ac_year) {
        this.ac_year = ac_year;
    }

    public String getCash_ac_cd() {
        return cash_ac_cd;
    }

    public void setCash_ac_cd(String cash_ac_cd) {
        this.cash_ac_cd = cash_ac_cd;
    }

    public String getSales_ac_cd() {
        return sales_ac_cd;
    }

    public void setSales_ac_cd(String sales_ac_cd) {
        this.sales_ac_cd = sales_ac_cd;
    }

    public String getPurchase_ac_cd() {
        return purchase_ac_cd;
    }

    public void setPurchase_ac_cd(String purchase_ac_cd) {
        this.purchase_ac_cd = purchase_ac_cd;
    }

    public String getSales_ret_cd() {
        return sales_ret_cd;
    }

    public void setSales_ret_cd(String sales_ret_cd) {
        this.sales_ret_cd = sales_ret_cd;
    }

    public String getPur_ret_cd() {
        return pur_ret_cd;
    }

    public void setPur_ret_cd(String pur_ret_cd) {
        this.pur_ret_cd = pur_ret_cd;
    }

    public String getTax_ac_cd() {
        return tax_ac_cd;
    }

    public void setTax_ac_cd(String tax_ac_cd) {
        this.tax_ac_cd = tax_ac_cd;
    }

}
