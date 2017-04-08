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
public class HospitalStatus {

    private String pt_name;
    private String ipd_no;
    private String ward_name;
    private String room_cd;
    private String mother_bed;

    public String getPt_name() {
        return pt_name;
    }

    public void setPt_name(String pt_name) {
        this.pt_name = pt_name;
    }

    public String getIpd_no() {
        return ipd_no;
    }

    public void setIpd_no(String ipd_no) {
        this.ipd_no = ipd_no;
    }

    public String getWard_name() {
        return ward_name;
    }

    public void setWard_name(String ward_name) {
        this.ward_name = ward_name;
    }

    public String getRoom_cd() {
        return room_cd;
    }

    public void setRoom_cd(String room_cd) {
        this.room_cd = room_cd;
    }

    public String getMother_bed() {
        return mother_bed;
    }

    public void setMother_bed(String mother_bed) {
        this.mother_bed = mother_bed;
    }

}
