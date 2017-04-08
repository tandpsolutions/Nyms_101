/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author BHAUMIK
 */
@Root(name = "sms")
public class SMS {

    @Element(name = "smsclientid")
    String smsclientid;

    @Element(name = "messageid")
    String messageid;

    @Element(name = "mobile-no")
    String moblile;

    public String getSmsclientid() {
        return smsclientid;
    }

    public void setSmsclientid(String smsclientid) {
        this.smsclientid = smsclientid;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getMoblile() {
        return moblile;
    }

    public void setMoblile(String moblile) {
        this.moblile = moblile;
    }

}
