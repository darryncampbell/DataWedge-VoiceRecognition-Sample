package com.darryncampbell.dwvoicerecognition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class DWUtilities {
    public static final String PROFILE_NAME = "VoiceRecognitionSample";

    public static final String EXTRA_INTENT_ACTION = "com.zebra.voicesample.ACTION";
    private static final String ACTION_DATAWEDGE = "com.symbol.datawedge.api.ACTION";
    private static final String EXTRA_CREATE_PROFILE = "com.symbol.datawedge.api.CREATE_PROFILE";
    private static final String EXTRA_SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG";
    private static final String EXTRA_DELETE_PROFILE = "com.symbol.datawedge.api.DELETE_PROFILE";

    public static void CreateDWProfile(Context context, String profileName)
    {
        sendDataWedgeIntentWithExtra(context, ACTION_DATAWEDGE, EXTRA_CREATE_PROFILE, profileName);

        //  Requires DataWedge 6.4

        //  Now configure that created profile to apply to our application
        Bundle profileConfig = new Bundle();
        profileConfig.putString("PROFILE_NAME", profileName);
        profileConfig.putString("PROFILE_ENABLED", "true"); //  Seems these are all strings
        profileConfig.putString("CONFIG_MODE", "UPDATE");

        Bundle voiceConfig = new Bundle();
        voiceConfig.putString("PLUGIN_NAME", "VOICE");
        voiceConfig.putString("RESET_CONFIG", "true"); //  This is the default but never hurts to specify
        Bundle voiceProps = new Bundle();
        voiceProps.putString("voice_input_enabled", "true");
        voiceProps.putString("voice_data_capture_start_option", "1");  //  PTT
        voiceProps.putString("voice_data_capture_waiting_tone", "true"); //  beep when expecting voice
        voiceProps.putString("voice_data_type", "0"); //  any
        voiceProps.putString("voice_offline_speech", "true");  //  allow offline speech
        voiceConfig.putBundle("PARAM_LIST", voiceProps);
        profileConfig.putBundle("PLUGIN_CONFIG", voiceConfig);
        Bundle appConfig = new Bundle();
        appConfig.putString("PACKAGE_NAME", context.getPackageName());      //  Associate the profile with this app
        appConfig.putStringArray("ACTIVITY_LIST", new String[]{"*"});
        profileConfig.putParcelableArray("APP_LIST", new Bundle[]{appConfig});
        sendDataWedgeIntentWithExtra(context, ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);
        //  You can only configure one plugin at a time, we have done the barcode input, now do the intent output
        profileConfig.remove("PLUGIN_CONFIG");
        Bundle intentConfig = new Bundle();
        intentConfig.putString("PLUGIN_NAME", "INTENT");
        intentConfig.putString("RESET_CONFIG", "true");
        Bundle intentProps = new Bundle();
        intentProps.putString("intent_output_enabled", "true");
        intentProps.putString("intent_action", EXTRA_INTENT_ACTION);
        intentProps.putString("intent_delivery", "2");
        intentConfig.putBundle("PARAM_LIST", intentProps);
        profileConfig.putBundle("PLUGIN_CONFIG", intentConfig);
        sendDataWedgeIntentWithExtra(context, ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);

        //  Disable Scanner input
        profileConfig.remove("PLUGIN_CONFIG");
        Bundle barcodeConfig = new Bundle();
        barcodeConfig.putString("PLUGIN_NAME", "BARCODE");
        barcodeConfig.putString("RESET_CONFIG", "true");
        Bundle barcodeProps = new Bundle();
        barcodeProps.putString("configure_all_scanners", "true");
        barcodeProps.putString("scanner_input_enabled", "false");
        barcodeConfig.putBundle("PARAM_LIST", barcodeProps);
        profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig);
        sendDataWedgeIntentWithExtra(context, ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);

        //  Disable keyboard output
        profileConfig.remove("PLUGIN_CONFIG");
        Bundle keystrokeConfig = new Bundle();
        keystrokeConfig.putString("PLUGIN_NAME", "KEYSTROKE");
        keystrokeConfig.putString("RESET_CONFIG", "true");
        Bundle keystrokeProps = new Bundle();
        keystrokeProps.putString("keystroke_output_enabled", "false");
        keystrokeConfig.putBundle("PARAM_LIST", keystrokeProps);
        profileConfig.putBundle("PLUGIN_CONFIG", keystrokeConfig);
        sendDataWedgeIntentWithExtra(context, ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);
    }

    public static void DeleteDWProfile(Context context, String profileName) {
        String[] values = {profileName};
        sendDataWedgeIntentWithExtra(context, ACTION_DATAWEDGE, EXTRA_DELETE_PROFILE, values);
    }

    private static void sendDataWedgeIntentWithExtra(Context context, String action, String extraKey, String extraValue)
    {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extraValue);
        context.sendBroadcast(dwIntent);
    }

    private static void sendDataWedgeIntentWithExtra(Context context, String action, String extraKey, Bundle extras)
    {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extras);
        context.sendBroadcast(dwIntent);
    }

    private static void sendDataWedgeIntentWithExtra(Context context, String action, String extraKey, String[] extraValues)
    {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extraValues);
        context.sendBroadcast(dwIntent);
    }
}
