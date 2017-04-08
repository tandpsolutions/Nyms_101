/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

import java.sql.Timestamp;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 *
 * @author BHAUMIK
 */
public interface ApiService {

    @GET("/sendsms.jsp")
    Message sendSms(@Query("user") String user, @Query("password") String password, @Query("mobiles") String mobiles, @Query("sms") String sms, @Query("unicode") int unicode, @Query("senderid") String senderid, @Query("version") int version);

    @GET("/sendsms.jsp")
    Message scheduleSms(@Query("user") String user, @Query("password") String password, @Query("mobiles") String mobiles, @Query("sms") String sms, @Query("unicode") int unicode, @Query("senderid") String senderid, @Query("version") int version, @Query("scheduletime") Timestamp ts);
}
