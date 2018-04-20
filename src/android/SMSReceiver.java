package com.rjfun.cordova.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;

import com.hmkcode.android.sqlite.MySQLiteHelper;

public class SMSReceiver extends BroadcastReceiver
{
    private static String TAG = "SmsReceiver";

    public static final String BOX = "box";
    public static final String ADDRESS = "address";
    public static final String BODY = "body";
    public static final String READ = "read";
    public static final String SEEN = "seen";
    public static final String SUBJECT = "subject";
    public static final String SERVICE_CENTER = "service_center";
    public static final String DATE = "date";
    public static final String DATE_SENT = "date_sent";
    public static final String STATUS = "status";
    public static final String REPLY_PATH_PRESENT = "reply_path_present";
    public static final String TYPE = "type";
    public static final String PROTOCOL = "protocol";
    public static final int MESSAGE_TYPE_INBOX = 1;
    public static final int MESSAGE_TYPE_SENT = 2;
    public static final int MESSAGE_IS_NOT_READ = 0;
    public static final int MESSAGE_IS_READ = 1;
    public static final int MESSAGE_IS_NOT_SEEN = 0;
    public static final int MESSAGE_IS_SEEN = 1;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i(TAG, "in onReceive");

        MySQLiteHelper db = new MySQLiteHelper(context);
        List<String> phoneNumbers = db.getAllRecords("phoneNumbers", 1);
        List<String> keywords = db.getAllRecords("keywords", 2);

        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
            Log.i(TAG, "Reading Bundle");

            SmsMessage smsMessage;
            if (Build.VERSION.SDK_INT >= 19)
            { //KITKAT
                Log.i(TAG, "Version >= 19");
                SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                smsMessage = msgs[0];
            } else
            {
                Log.i(TAG, "Version < 19");
                Object pdus[] = (Object[]) bundle.get("pdus");
                smsMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
            }

            JSONObject json = getJsonFromSmsMessage(smsMessage);

            String incommingNumber = smsMessage.getOriginatingAddress();
            Log.d(TAG, "==> Incoming number: " + incommingNumber);

            for(String number : phoneNumbers)
            {
                if(checkIfNumbersMatch(number, incommingNumber))
                {
                    Log.d(TAG, "==> numbers match");
                    for(String element : keywords)
                    {
                        if (smsMessage.getMessageBody().contains(element))
                        {
                            startActivity(json, context);
                            break;
                        }
                    }
                    break;
                }
            }
            //SMSPlugin.onSMSArrive(json);
        }

        Log.i(TAG, "out onReceive");
    }

    private boolean checkIfNumbersMatch(String firstNumber, String secondNumber)
    {
        Log.d(TAG, "==> firstNumber " + firstNumber);
        Log.d(TAG, "==> secondNumber " + secondNumber);

        
        if(CheckIfPhonNumberIsString(firstNumber) || CheckIfPhonNumberIsString(secondNumber))
        {
            if(firstNumber.equals(secondNumber))
            {
                return true;
            }

            return false;
        }


        int firstInd = firstNumber.length() - 1;
        int secondInd = secondNumber.length() - 1;

        for(int i = 0; i < 8; i++)
        {
            if(firstNumber.charAt(firstInd) != secondNumber.charAt(secondInd))
            {
                return false;
            }

            firstInd--;
            secondInd--;
        }

        return true;
    }

    private boolean CheckIfPhonNumberIsString(String number)
    {
        if(number.startsWith("+"))
        {
            number = number.substring(1);
        }

        try
        {
            Integer.parseInt(number);
            return false;
        }
        catch(NumberFormat e)
        {
            return true;
        }
    }

    /**
     * Open app when received FCM message.
     *
     * @param data FCM data.
     */
    private void startActivity(JSONObject data, Context context)
    {
        Intent intent = new Intent(context, SMSPluginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("navCoords", data.toString());
        context.startActivity(intent);
    }


    private JSONObject getJsonFromSmsMessage(SmsMessage sms) {
    	JSONObject json = new JSONObject();
    	
        try {
        	json.put( ADDRESS, sms.getOriginatingAddress() );
        	json.put( BODY, sms.getMessageBody() ); // May need sms.getMessageBody.toString()
        	json.put( DATE_SENT, sms.getTimestampMillis() );
        	json.put( DATE, System.currentTimeMillis() );
        	json.put( READ, MESSAGE_IS_NOT_READ );
        	json.put( SEEN, MESSAGE_IS_NOT_SEEN );
        	json.put( STATUS, sms.getStatus() );
        	json.put( TYPE, MESSAGE_TYPE_INBOX );
        	json.put( SERVICE_CENTER, sms.getServiceCenterAddress());
        	
        } catch ( Exception e ) { 
            e.printStackTrace(); 
        }

    	return json;
    }
}