/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

import java.util.List;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 *
 * @author BHAUMIK
 */
@Root(name = "smslist")
public class Message {

    @ElementList(inline = true)
    List<SMS> smsList;

    public List<SMS> getSmsList() {
        return smsList;
    }

    public void setSmsList(List<SMS> smsList) {
        this.smsList = smsList;
    }

}
