# DataWedge-VoiceRecognition-Sample

Using your voice to control a mobile device is increasingly common in both consumer and enterprise environments, this application shows how you can use Zebra's DataWedge to recognize speech, convert it to text and capture the text in your app.

## Giving context to speech

Modern devices make the speech recognition part relatively easy, the more difficult bit is converting the text into meaningful actions, i.e. given the user wants to "Call Alice", some obvious questions that arise from that are "Alice who?", "What is Alice's number?", "Call her via PBX or some VoIP system?" etc.

For backend integration, I previously created [a simple app](https://github.com/darryncampbell/AndroidV2DialogFlow) that used the GMS Android APIs for voice recognition and showed how to integrate that with a Google DialogFlow (GDF) backend.  GDF allows you to parse the request, so speech like "I want to call Alice" can be easily recognised as a desire (Intent) to "call Alice".  The final part of the journey is actually fulfilling that desire, in this case actually placing the call to Alice, e.g. by using the dialler app.

**This application only considers the first part of the journey to give context to speech, converting the speech into text.**  Many developers will instinctively reach for the [SpeechRecognizer](https://developer.android.com/reference/android/speech/SpeechRecognizer) API but there are many reasons why you might want to consider DataWedge instead:

- You have multiple apps who all want to perform speech recognition.  DataWedge's profile mechanism makes it easy to configure each app separately without worrying about contention accessing the mic.
- You want to capture speech in the background.  As DataWedge is a system app you do not have to worry about the recent changes to how background apps can access the mic. 
- You are already using DataWedge elsewhere in your application; this is likely on Zebra devices since DW is the preferred way to capture data from the barcode scanner.
- You want to send the same speech to multiple apps.

## Running the Sample App

This sample application shows how a single DataWedge profile can be used to convert a user's text to speech and send it to a single app.  Using this principle it is possible to send speech to multiple apps (using broadcast intents) or have different apps / activities configured to recognise speech differently (using DataWedge profiles).

**This app has been designed to work with DataWedge 7.4 and higher** since that is the first version of DataWedge where speech recognition could be initiated with the PTT key.

- Launch the app

![App](https://raw.githubusercontent.com/darryncampbell/DataWedge-VoiceRecognition-Sample/master/screenshots/app_1.jpg)

- Press the PTT key and whilst holding the key, say one of the pre-defined phrases.  When a phrase is recognised the UI will update to reflect this.

![App](https://raw.githubusercontent.com/darryncampbell/DataWedge-VoiceRecognition-Sample/master/screenshots/app_2.jpg)

There are a few use cases covered by this app: Calling colleagues, assigning tasks or obtaining product info:

![App](https://raw.githubusercontent.com/darryncampbell/DataWedge-VoiceRecognition-Sample/master/screenshots/app_4.jpg)

![App](https://raw.githubusercontent.com/darryncampbell/DataWedge-VoiceRecognition-Sample/master/screenshots/app_6.jpg)

## Processing speech

Speech is received from DataWedge via an Intent Broadcast receiver:

```java
private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //  Intent action should match that specified in the DW profile
        if (action.equals(EXTRA_INTENT_ACTION))
        {
            String recognisedSpeech = intent.getStringExtra("com.symbol.datawedge.data_string");
            notifyObservers(recognisedSpeech);
        }
    }
};
```

The received speech, as a text string, is passed to the appropriate Android Fragment which will then use the [Levenshtein distance](https://en.wikipedia.org/wiki/Levenshtein_distance) to choose which sample phrase most closely matches what the user said.  In a production app this final step may be replaced by a call to some Natural Language parsing engine and integration with a back-end API to fulfill the request. 

## DataWedge configuration

This article assumes familiarity with Zebra's DataWedge tool as well as the DataWedge profile mechanism.  For an overview of DataWedge, please refer to the [DataWedge Techdocs page](https://techdocs.zebra.com/datawedge/latest/guide/overview/)

Provided your device is running DataWedge version 6.4 or higher the profile should be created automatically the first time this application is run.  If the profile is not created you can do so manually:

- Create a new Profile, e.g. "VoiceRecognitionSample"

![DataWedge](https://raw.githubusercontent.com/darryncampbell/DataWedge-VoiceRecognition-Sample/master/screenshots/dw_1.jpg)

- Associate the newly created profile with this application

![DataWedge](https://raw.githubusercontent.com/darryncampbell/DataWedge-VoiceRecognition-Sample/master/screenshots/dw_2.jpg)


- Enable the Voice input plugin and configure as follows

![DataWedge](https://raw.githubusercontent.com/darryncampbell/DataWedge-VoiceRecognition-Sample/master/screenshots/dw_3.jpg)

![DataWedge](https://raw.githubusercontent.com/darryncampbell/DataWedge-VoiceRecognition-Sample/master/screenshots/dw_4.jpg)

- Enable the Intent output plugin and configure as follows

![DataWedge](https://raw.githubusercontent.com/darryncampbell/DataWedge-VoiceRecognition-Sample/master/screenshots/dw_5.jpg)


## Troubleshooting

### DataWedge version
As stated previously, this application requires DataWedge version 7.4 or higher to work properly.  You can find your DataWedge version by launching the DataWedge app then selecting the 'About' option.

### PTT button mapping
Speech recognition will start when the Push to Talk (PTT) button is held down.  This will probably 'just work' out of the box with the device's standard PTT button but I had been previously messing about with my device and had to remap the PTT button.  You can do this with [MX](https://techdocs.zebra.com/mx/keymappingmgr/) or the on-device key programmer utility (under settings)

![Button remapping](https://raw.githubusercontent.com/darryncampbell/DataWedge-VoiceRecognition-Sample/master/screenshots/button_remap.jpg)

If you still have trouble with this (or wonder if your device even has a PTT key) then you might try the ["Using Push-to-Talk"](https://techdocs.zebra.com/emdk-for-android/latest/samples/usingptt/) official sample from Zebra.