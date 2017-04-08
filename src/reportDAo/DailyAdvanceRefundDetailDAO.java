/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reportDAo;

/**
 *
 * @author Lenovo
 */
public class DailyAdvanceRefundDetailDAO {

    private String sr_no;
    private String ref_no;
    private String v_date;
    private String particular;
    private String amount;
    private String cash;
    private String bank;
    private String card;
    private String user;
    private String pt_name;
    private String tot_amt;
    private String ipd_no;

    public String getTot_amt() {
        return tot_amt;
    }

    public void setTot_amt(String tot_amt) {
        this.tot_amt = tot_amt;
    }

    public String getPt_name() {
        return pt_name;
    }

    public void setPt_name(String pt_name) {
        this.pt_name = pt_name;
    }

    public String getSr_no() {
        return sr_no;
    }

    public void setSr_no(String sr_no) {
        this.sr_no = sr_no;
    }

    public String getRef_no() {
        return ref_no;
    }

    public void setRef_no(String ref_no) {
        this.ref_no = ref_no;
    }

    public String getV_date() {
        return v_date;
    }

    public void setV_date(String v_date) {
        this.v_date = v_date;
    }

    public String getParticular() {
        return particular;
    }

    public void setParticular(String particular) {
        this.particular = particular;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getIpd_no() {
        return ipd_no;
    }

    public void setIpd_no(String ipd_no) {
        this.ipd_no = ipd_no;
    }

}
