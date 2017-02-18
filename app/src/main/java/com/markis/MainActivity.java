package com.markis;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.markis.models.Apps;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    TextToSpeech mTextToSpeech;
    WebView mainWebView;
    FrameLayout mainAudioLayout;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

    }

    private void initViews() {
        mediaPlayer = new MediaPlayer();
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        mainWebView = (WebView) findViewById(R.id.webview);
        mainAudioLayout = (FrameLayout) findViewById(R.id.mainAudioLayout);
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                promptSpeechInput();
            }
        });
        mTextToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub


                //mTextToSpeech.speak("Hello! This is neither Siri nor Iris!! Welcome to Swathi's Application!! A Kiddish Application!!" + "Now click on the button ans speak", TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        final Context context = this;
        final GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                promptSpeechInput();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
            }
        };
    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data) {
                    playMusic(data);
                }

                break;
        }

    }

    private void playMusic(Intent data) {
        ArrayList<String> result = data
                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        txtSpeechInput.setText(result.get(0));
        mTextToSpeech.speak(result.get(0).toString(), TextToSpeech.QUEUE_FLUSH, null);

        String[] str = new String[0];
        String[] musicPath = new String[0];

        try {
            str = getMusic();
            musicPath = getMusicPath();
        } catch (Exception ae) {
        }
        String str3[] = result.get(0).split("\\s+");

        String music = str3[0] + " " + str3[1];
        String songs = "";
        String regex = "";
        for (int k = 2; k < str3.length; k++) {
            songs = songs + " " + str3[k];
            regex = regex + "(.*)" + str3[k];
        }
        String musicname = "";
        String musicpath = "";
        for (int j = 0; j < str.length; j++) {
            if (str[j].toLowerCase().matches(regex.toLowerCase() + "(.*)")) {
                musicname = str[j];
                musicpath = musicPath[j];
                Log.d("ParSongs", musicname);
                Log.d("ParSongsPath", musicpath);
                break;
            }
            Log.d("Songs", str[j]);
        }


        if (music.equalsIgnoreCase("Play music") || music.equalsIgnoreCase("Play song")) {

            mainAudioLayout.setVisibility(View.VISIBLE);
            mainWebView.setVisibility(View.INVISIBLE);
            final FloatingActionButton fabAudio = (FloatingActionButton) findViewById(R.id.audioPlay);
            fabAudio.setImageResource(R.drawable.pause_white);
            ImageView closeButton = (ImageView) findViewById(R.id.closeButton);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }

                    mainAudioLayout.setVisibility(View.GONE);
                }
            });
            fabAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        fabAudio.setImageResource(R.drawable.play_white);
                    } else {
                        mediaPlayer.start();
                        fabAudio.setImageResource(R.drawable.pause_white);
                    }

                }
            });
            TextView txtTitle = (TextView) findViewById(R.id.audioTitle);
            txtTitle.setText(musicname);
            String filePath = musicpath;
            File f = new File(filePath);
            if (f.exists()) {
                try {
                    mediaPlayer.setDataSource(filePath);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
            } else {
                mTextToSpeech.speak("Oops I Could not find this song in you device!", TextToSpeech.QUEUE_FLUSH, null);
            }

        } else if (music.equalsIgnoreCase("Pause music") || music.equalsIgnoreCase("Pause song")) {

        } else if (music.equalsIgnoreCase("Stop music") || music.equalsIgnoreCase("Stop song")) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            } else {
                mTextToSpeech.speak("No song is been played!", TextToSpeech.QUEUE_FLUSH, null);
            }
        } else if (str3[0].equalsIgnoreCase("Open")) {
            searchApp(str3[1]);
        } else if (str3[0].equalsIgnoreCase("Send")) {

            sendMessage(str3);
        } else {
            try {
                mainWebView.setVisibility(View.VISIBLE);
                mainAudioLayout.setVisibility(View.GONE);
                mainWebView.setWebViewClient(new myWebClient());

                mainWebView.getSettings().setJavaScriptEnabled(true);
                mainWebView.loadUrl("https://www.google.co.in/search?q=" + URLEncoder.encode(result.get(0), "utf-8"));
                Log.d("QueryMain", "https://www.google.co.in/search?q=" + URLEncoder.encode(result.get(0), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendMessage(String str[]) {
        if (str[1].equalsIgnoreCase("email")) {
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            emailIntent.setType("plain/text");
            emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"someone@gmail.com"});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Yo");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hi");
            startActivity(emailIntent);
        } else {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "YOUR TEXT HERE";
            waIntent.setPackage("com.whatsapp");
            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));
        }
    }

    private void searchApp(String app) {
        String packageName = "";
        Dictionary dictionary = new Dictionary();
        ArrayList<Apps> appsArrayList = dictionary.getDictionary();
        for (int i = 0; i < appsArrayList.size(); i++) {
            if (appsArrayList.get(i).getAppName().toLowerCase().contains(app.toLowerCase()))
                packageName = appsArrayList.get(i).getPackageName();
        }


        if (appIsInstalled(packageName) && !packageName.isEmpty()) {
            Intent i = getPackageManager().
                    getLaunchIntentForPackage(packageName);
            startActivity(i);
        } else {
            mTextToSpeech.speak("Oops no application" + app + " found.", TextToSpeech.QUEUE_FLUSH, null);

        }
    }

    private boolean appIsInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        boolean appInstalled = false;
        try {
            pm.getPackageInfo(packageName,
                    PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            appInstalled = false;
        }
        return appInstalled;
    }

    private String[] getMusic() {
        final Cursor mCursor = managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DISPLAY_NAME}, null, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        int count = mCursor.getCount();

        String[] songs = new String[count];
        int i = 0;
        if (mCursor.moveToFirst()) {
            do {
                songs[i] = mCursor.getString(0);
                i++;
            } while (mCursor.moveToNext());
        }

        mCursor.close();

        return songs;
    }

    private String[] getMusicPath() {
        final Cursor mCursor = managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DATA}, null, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        int count = mCursor.getCount();

        String[] songs = new String[count];
        int i = 0;
        if (mCursor.moveToFirst()) {
            do {
                songs[i] = mCursor.getString(0);
                i++;
            } while (mCursor.moveToNext());
        }

        mCursor.close();

        return songs;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            view.loadUrl(url);
            return true;

        }
    }


    /**********
     * 1. Open Apps,Send Messages & Email
     * 2. Well formed Comments.
     * 3. Full Screen access of MainScreen via double Tap on activity which enables the Google Voice Dialog.
     * 4. WebAccessfor Visual Impact People  -
     */
}
