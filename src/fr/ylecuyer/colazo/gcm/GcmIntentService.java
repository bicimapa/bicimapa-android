package fr.ylecuyer.colazo.gcm;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import fr.ylecuyer.colazo.BuildConfig;
import fr.ylecuyer.colazo.R;
import fr.ylecuyer.colazo.activities.bicimapa.BiciMapaActivity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GcmIntentService extends IntentService {
	
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

	
	public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);


        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString(), null, "error");
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString(), null, "error");
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
  
            	Location alert = new Location("alert");
            	
            	double lat = Double.parseDouble(extras.getString("lat"));
            	double lng = Double.parseDouble(extras.getString("lng"));
            	String alert_type = extras.getString("alert_type");
            	String regid = extras.getString("regid");
            	
            	String current_regid = getRegistrationId(getApplicationContext());      
            	
            	
            	//If the user sent this alert
            	if (regid.equalsIgnoreCase(current_regid)) {
            		
            		
            		sendNotification(getString(R.string.alert_sent), null, alert_type);
            		
            	}
            	else {
            	
	            	if (BuildConfig.DEBUG) 
	            		Log.d("BiciMapa", "alert_type: " + alert_type + " lat: " + lat + " lng: " + lng);
	            	
	            	alert.setLatitude(lat);
	            	alert.setLongitude(lng);
            	
            	
            	
	            	if (BuildConfig.DEBUG)
	            		Log.d("BiciMapa", "Alert from " + alert.toString());
	            	
	            	Location position = getCurrentPosition();
	            	
	            	if (position != null) {
		            	if (BuildConfig.DEBUG)
		            		Log.d("BiciMapa", "Current position " + position.toString());
		            	
		            	if (BuildConfig.DEBUG)
		            		Log.d("BiciMapa", "Distance: " + position.distanceTo(alert));
	            	}
	            	
	            	if (position != null) {
		            	// If the user position is less than 1km
		            	if (position.distanceTo(alert) <= 1000) {
		            		
		            		String msg = "";
		            		
		            		if (alert_type.equalsIgnoreCase("falla_mecanica"))
		            			msg = getString(R.string.notification_falla_mecanica);
		            		else if (alert_type.equalsIgnoreCase("acidente")) 
		            			msg = getString(R.string.notification_acidente);
		            		
		                    // Post notification of received message.
		                    sendNotification(msg, alert, alert_type);
		            		
		                    //vibrate
		                    // Get instance of Vibrator from current Context
		                    Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		                     	                   
		                    //1500ms
		                    mVibrator.vibrate(1500);
		                    
		            	}
	            	}
	            	
            	}
            	
                for (String key : extras.keySet())
                	Log.d("BiciMapa key", key);
                
                Log.i("BiciMapa", "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
    
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
    
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(BiciMapaActivity.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
    
    /**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.length() == 0) {
	        Log.i("BiciMapa", "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i("BiciMapa", "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	protected Location getCurrentPosition() {
		
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
				
		if (gps_enabled)
			return lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (network_enabled)
			return lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		//No providers
		return null;
	}

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg, Location alert, String type) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, BiciMapaActivity.class);
        if (alert != null) {
        	intent.putExtra("alert_lat", alert.getLatitude());
        	intent.putExtra("alert_lng", alert.getLongitude());
        	intent.putExtra("alert_type", type);
        
        	if (BuildConfig.DEBUG)
        		Log.d("BiciMapa", "Sending intent " + alert.getLatitude() + " " + alert.getLongitude());
        }
        
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setContentTitle("Alerta bicimapa")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        if (type.equalsIgnoreCase("falla_mecanica"))
        	mBuilder.setSmallIcon(R.drawable.taller);
        else mBuilder.setSmallIcon(R.drawable.atencion);
        

        mBuilder.setContentIntent(contentIntent);
        
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
}
