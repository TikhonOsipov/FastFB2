package com.tixon.fastfb2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Document;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class ActivityMain extends AppCompatActivity {

    private Menu myMenu;
    SharedPreferences preferences;
    DocumentBuilder builder;
    Document document;
    String[] words;
    int color;
    public static ArrayList<Section> chapters;
    public ArrayList<String> titles, subtitles, texts;
    int savedPosition = 0;
    int wordsPerSecond = 299;
    int time1, time2;
    int imagePlayResource, imagePauseResource, imageNextResource;
    public int chapterNumber = 0;
    public int subtitleNumber = 0;
    boolean isReading = false;
    boolean isPaused = false;
    boolean isNext = false;

    public static String of;

    private static final String KEY_WORDS = "key_words";
    private static final String KEY_SAVED_POSITION = "key_saved_position";
    private static final String KEY_IS_READING = "key_is_reading";
    private static final String KEY_IS_PAUSED = "key_is_paused";
    private static final String KEY_IS_NEXT = "key_is_next";
    private static final String KEY_CHAPTER_NUMBER = "chapterNumber";
    private static final String KEY_SUBTITLE_NUMBER = "subtitleNumber";
    private static final String KEY_AUTHOR_INFO = "author_info";
    private static final String KEY_BOOK_NAME = "book_name";
    private static final String KEY_SAVED_TITLE = "saved_title";
    private static final String KEY_SAVED_SUBTITLE = "saved_subtitle";
    private static final String KEY_SAVED_PROGRESS = "saved_progress";
    private static final String KEY_LIST_TITLES = "list_titles";
    private static final String KEY_LIST_SUBTITLES = "list_subtitles";
    private static final String KEY_LIST_TEXTS = "list_texts";


    private static final String KEY_TITLES = "array_list_titles";
    private static final String KEY_SUBTITLES = "array_list_subtitles";
    private static final String KEY_TEXTS = "array_list_texts";

    private static final String KEY_CHAPTER = "key_chapter";
    private static final String KEY_SUBTITLE = "key_subtitle";
    private static final int REQUEST_CODE_SECTIONS = 6;

    //Threads
    Thread readThread;
    AsyncReader asyncReader;
    //Widgets
    TextView textView, progress, author, bookName, title, subtitle;
    LinearLayout layoutControl, layoutBookInfo, layoutChapter;
    FrameLayout pageLeft, pageRight;
    Toolbar toolbar;
    FloatingActionButton fab;

    void setTextColor(int color) {
        switch(color) {
            case 0:case 1:case 2:case 3:case 4:case 5:case 6:case 8:case 14:case 15:case 17:
                //white
                toolbar.setTitleTextColor(getResources().getColor(R.color.white));
                //super.setTheme(R.style.AppThemeDark);
                break;
            default:
                //black
                toolbar.setTitleTextColor(getResources().getColor(R.color.black));
                /*imagePlayResource = R.drawable.ic_play_arrow_black_48dp;
                imagePauseResource = R.drawable.ic_pause_black_48dp;
                imageNextResource = R.drawable.ic_skip_next_black_48dp;*/
                //super.setTheme(R.style.AppThemeLight);
                break;
        }
        imagePlayResource = R.drawable.ic_play_arrow_white_48dp;
        imagePauseResource = R.drawable.ic_pause_white_48dp;
        imageNextResource = R.drawable.ic_skip_next_white_48dp;
        if(isNext) fab.setImageResource(imageNextResource);
        else fab.setImageResource(imagePlayResource);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //read settings
        color = Integer.parseInt(preferences.getString("app_color", "8"));
        wordsPerSecond = Integer.parseInt(preferences.getString("words_per_minute", "300"));
        time1 = 1000 / (wordsPerSecond / 60);
        time2 = time1 + time1 / 2;
        Log.d("myLogs", "in onResume: wps = " + wordsPerSecond + "; time1 = " + time1 + "; time2 = " + time2);

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };

        int[] colors = new int[] {
                getResources().getIntArray(R.array.colors500)[color],
                getResources().getIntArray(R.array.colors500)[color],
                getResources().getIntArray(R.array.colors500)[color],
                getResources().getIntArray(R.array.colors500)[color]
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getResources().getIntArray(R.array.colors700)[color]);
        toolbar.setBackgroundColor(getResources().getIntArray(R.array.colors500)[color]);
        setTextColor(color);
        fab.setBackgroundTintList(new ColorStateList(states, colors));
        fab.setRippleColor(getResources().getIntArray(R.array.colors300)[color]);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Log.d("myLogs", "in onCreate: wps = " + wordsPerSecond);
        of = getResources().getString(R.string.progress_word_of);

        asyncReader = new AsyncReader();

        textView = (TextView) findViewById(R.id.textView);
        progress = (TextView) findViewById(R.id.progress);
        author = (TextView) findViewById(R.id.text_view_author);
        bookName = (TextView) findViewById(R.id.text_view_book_name);
        title = (TextView) findViewById(R.id.text_view_title);
        subtitle = (TextView) findViewById(R.id.text_view_book_subtitle);

        layoutControl = (LinearLayout) findViewById(R.id.layout_control);
        layoutBookInfo = (LinearLayout) findViewById(R.id.layout_book_info);
        layoutChapter = (LinearLayout) findViewById(R.id.layout_chapter);

        pageLeft = (FrameLayout) findViewById(R.id.page_left);
        pageRight = (FrameLayout) findViewById(R.id.page_right);

        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        titles = new ArrayList<>();
        subtitles = new ArrayList<>();
        texts = new ArrayList<>();

        setTextViewPosition();

        if(savedInstanceState != null) {
            layoutBookInfo.setVisibility(View.VISIBLE);
            layoutChapter.setVisibility(View.VISIBLE);
            setTextViewPosition();
            isReading = savedInstanceState.getBoolean(KEY_IS_READING, false);
            isPaused = savedInstanceState.getBoolean(KEY_IS_PAUSED, false);
            isNext = savedInstanceState.getBoolean(KEY_IS_NEXT, false);
            words = savedInstanceState.getStringArray(KEY_WORDS);
            if(words != null) {
                isReading = false;
                isPaused = true;
                if(!isReading && isPaused) {
                    layoutControl.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
                    savedPosition = savedInstanceState.getInt(KEY_SAVED_POSITION, 0);
                    if(savedPosition > 0) savedPosition--;
                    author.setText(savedInstanceState.getString(KEY_AUTHOR_INFO));
                    bookName.setText(savedInstanceState.getString(KEY_BOOK_NAME));
                    title.setText(savedInstanceState.getString(KEY_SAVED_TITLE));
                    subtitle.setText(savedInstanceState.getString(KEY_SAVED_SUBTITLE));
                    progress.setText(savedInstanceState.getString(KEY_SAVED_PROGRESS));
                    progress.setText(calculateProgress(savedPosition, words.length));
                    chapterNumber = savedInstanceState.getInt(KEY_CHAPTER_NUMBER, 0);
                    subtitleNumber = savedInstanceState.getInt(KEY_SUBTITLE_NUMBER, 0);
                    if(savedPosition > 0) {
                        if((words[savedPosition-1].length() < 4) && (savedPosition > 5))
                            textView.setText(words[savedPosition-1] + " " + words[savedPosition]);
                        else textView.setText(words[savedPosition]);
                    }
                }
            }

            if(isNext) {
                fab.setImageResource(imageNextResource);
                textView.setText(words[words.length - 1]);
                savedPosition = words.length-1;
                progress.setText(savedInstanceState.getString(KEY_SAVED_PROGRESS));
            }
        }

        //Runnable

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String word;
                for (int i = savedPosition; i < words.length; i++) {
                    word = words[i];
                    savedPosition = i;
                    if(!isReading) {
                        //Прерывание потока: пауза
                        savedPosition = i;
                        break;
                    }
                    if((words[i].length() < 4) && (i < words.length - 5)) {
                        word += " " + words[i+1];
                        savedPosition = i;
                        i++;
                    }
                    if(i == words.length - 1) { //End of chapter
                        isReading = false;
                        isPaused = true;
                        if(chapterNumber != chapters.size() - 1) {
                            if(subtitleNumber != subtitles.size() - 1) {
                                isNext = true;
                            }
                        }
                    }
                    final int currentPosition = i;
                    final String wordToShow = word;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(wordToShow);
                            progress.setText(calculateProgress(currentPosition, words.length));
                            if(currentPosition == words.length - 1) fab.setImageResource(imagePlayResource);
                            if(isNext) {
                                fab.setImageResource(imageNextResource);
                            }
                        }
                    });
                    if(isNext) {
                        savedPosition = words.length - 1;
                        break;
                    }
                    try {
                        int tTime1, tTime2;
                        tTime1 = time1; tTime2 = time2;
                        if(word.length() > 10) {
                            tTime1 += (word.length() - 10) * time1/8;
                            tTime2 += (word.length() - 10) * time2/8;
                        }
                        if((word.contains(".")) || (word.contains(",")) ||
                                (word.contains(";")) || (word.contains(":")) ||
                                (word.contains("!")) || (word.contains("?")))
                            Thread.sleep(tTime2);
                        else Thread.sleep(tTime1);
                    } catch (InterruptedException e) {
                        isReading = false;
                        isPaused = true;
                        e.printStackTrace();
                    }
                }
            }
        };

        //End of runnable

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, 5);
            }
        });

        pageLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isReading && isPaused) {
                    if(savedPosition > 0) {
                        savedPosition -= 1;
                        if((words[savedPosition-1].length() < 4) && (savedPosition > 5)) {
                            textView.setText(words[savedPosition - 1] + " " + words[savedPosition]);
                            savedPosition--;
                        }
                        else textView.setText(words[savedPosition]);
                        progress.setText(calculateProgress(savedPosition, words.length));
                    }
                }
            }
        });

        pageRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isReading && isPaused) {
                    if(savedPosition < words.length - 1) {
                        savedPosition += 1;
                        if((words[savedPosition].length() < 4) && (savedPosition < words.length - 5)) {
                            textView.setText(words[savedPosition] + " " + words[savedPosition+1]);
                            savedPosition++;
                        }
                        else textView.setText(words[savedPosition]);
                        progress.setText(calculateProgress(savedPosition, words.length));
                    }
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                if(isNext) {
                    if(subtitleNumber == chapters.get(chapterNumber).subtitles.size()-1) {
                        chapterNumber++;
                        subtitleNumber = 0;
                    } else subtitleNumber++;
                    savedPosition = 0;
                    words = chapters.get(chapterNumber).texts.get(subtitleNumber).split(" ");
                    textView.setText(words[0]);
                    title.setText(chapters.get(chapterNumber).title);
                    subtitle.setText(chapters.get(chapterNumber).subtitles.get(subtitleNumber));
                    fab.setImageResource(imagePlayResource);
                    progress.setText(calculateProgress(savedPosition, words.length));
                    isNext = false;
                } else {
                    if(!isReading) {
                        readThread = new Thread(runnable);
                        readThread.start();
                        isReading = true;
                        isPaused = false;
                        fab.setImageResource(imagePauseResource);
                    } else {
                        try {
                            readThread.interrupt();
                            isReading = false;
                            isPaused = true;
                            fab.setImageResource(imagePlayResource);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            //readThread.interrupt();
            isReading = false;
            isPaused = true;
            fab.setImageResource(imagePlayResource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        myMenu = menu;
        if(words != null) getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        else getMenuInflater().inflate(R.menu.menu_activity_main_no_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent intentOpenSettings = new Intent(this, ActivitySettings.class);
                startActivity(intentOpenSettings);
                break;
            case R.id.action_open:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, 5);
                break;
            case R.id.action_sections:
                Intent intentSections = new Intent(this, ActivitySections.class);
                getTitlesAndSubtitles(chapters, titles, subtitles, texts);
                intentSections.putStringArrayListExtra(KEY_TITLES, titles);
                intentSections.putStringArrayListExtra(KEY_SUBTITLES, subtitles);
                intentSections.putStringArrayListExtra(KEY_TEXTS, texts);
                startActivityForResult(intentSections, REQUEST_CODE_SECTIONS);
                break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch(requestCode) {
                case 5:
                    if(data != null) {
                        String path = data.getDataString();
                        if(asyncReader.getStatus() == AsyncTask.Status.RUNNING) asyncReader.cancel(true);
                        asyncReader = new AsyncReader();
                        asyncReader.execute(path);
                        Log.d("myLogs", data.getDataString());
                    }
                    break;
                case REQUEST_CODE_SECTIONS:
                    chapterNumber = data.getIntExtra(KEY_CHAPTER, 0);
                    subtitleNumber = data.getIntExtra(KEY_SUBTITLE, 0);
                    words = chapters.get(chapterNumber).texts.get(subtitleNumber).split(" ");
                    textView.setText("");
                    title.setText(chapters.get(chapterNumber).title);
                    subtitle.setText(chapters.get(chapterNumber).subtitles.get(subtitleNumber));
                    savedPosition = 0;
                    isReading = false; isPaused = false; isNext = false;
                    progress.setText(calculateProgress(savedPosition, words.length));
                    break;
                default: break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isReading = false;
    }

    class AsyncReader extends AsyncTask<String, Void, ArrayList<Section>> {
        File sdcard, file;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sdcard = Environment.getExternalStorageDirectory().getAbsoluteFile();
            progressDialog = ProgressDialog.show(ActivityMain.this, "Parsing book", "Wait, please");
            builder = null;
        }

        @Override
        protected ArrayList<Section> doInBackground(String... params) {
            try {
                StringBuilder path = new StringBuilder();
                path.append(params[0]);
                //Log.d("myLogs", "path1 = " + path);
                path.delete(0, path.indexOf("/emulated/0") + ("/emulated/0").length());
                //Log.d("myLogs", "path2 = " + path);
                file = new File(sdcard, path.toString());
                if (builder == null) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    builder = factory.newDocumentBuilder();
                    document = builder.parse(file);
                    //code from here is in "/desktop/code.txt"
                    Fb2Parser parser = new Fb2Parser(document);
                    parser.parse();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Body.chapters;
        }

        @Override
        protected void onPostExecute(ArrayList<Section> sections) {
            super.onPostExecute(sections);
            progressDialog.dismiss();
            myMenu.clear();
            getMenuInflater().inflate(R.menu.menu_activity_main, myMenu);
            chapters = new ArrayList<>(sections);
            titles = new ArrayList<>();
            subtitles = new ArrayList<>();
            texts = new ArrayList<>();
            words = addSpaces(chapters.get(0).texts.get(0)).split(" ");
            textView.setText("");
            layoutControl.setVisibility(View.VISIBLE);
            layoutBookInfo.setVisibility(View.VISIBLE);
            author.setText(TitleInfo.author.firstName + " " + TitleInfo.author.middleName + " " + TitleInfo.author.lastName);
            bookName.setText(TitleInfo.bookTitle);
            layoutChapter.setVisibility(View.VISIBLE);
            setTextViewPosition();
            title.setText(chapters.get(0).title);
            subtitle.setText(chapters.get(0).subtitles.get(0));
            savedPosition = 0;
            isReading = false; isPaused = false; isNext = false;
            fab.setVisibility(View.VISIBLE);
            fab.setImageResource(imagePlayResource);
            progress.setVisibility(View.GONE);
        }
    }

    String addSpaces(String s) {
        //From 0 to one before last symbol
        for(int j = 0; j < s.length() - 2; j++) {
            if((s.charAt(j) == '.' || s.charAt(j) == '!' || s.charAt(j) == '?' || s.charAt(j) == '…'
            || s.charAt(j) == ',' || s.charAt(j) == ':' || s.charAt(j) == ';' || s.charAt(j) == '\n')
                    && s.charAt(j+1) != ' ' && Character.isLetter(s.charAt(j+1))) {
                s = s.substring(0, j+1) + " " + s.substring(j+1);
            }
        }
        return s;
    }

    boolean isNormal() {
        return (getResources().getConfiguration().screenLayout
        & Configuration.SCREENLAYOUT_SIZE_MASK)
                < Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    void setTextViewPosition() {
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        p.addRule(RelativeLayout.CENTER_HORIZONTAL);
        if(getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE
                && isNormal() && layoutChapter.getVisibility() == View.VISIBLE) {
            p.addRule(RelativeLayout.BELOW, R.id.layout_chapter);
        } else {
            p.addRule(RelativeLayout.CENTER_VERTICAL, R.id.textView);
        }
        textView.setLayoutParams(p);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(KEY_WORDS, words);
        outState.putInt(KEY_SAVED_POSITION, savedPosition);
        outState.putInt(KEY_CHAPTER_NUMBER, chapterNumber);
        outState.putInt(KEY_SUBTITLE_NUMBER, subtitleNumber);
        outState.putBoolean(KEY_IS_READING, isReading);
        outState.putBoolean(KEY_IS_PAUSED, isPaused);
        outState.putBoolean(KEY_IS_NEXT, isNext);
        outState.putString(KEY_AUTHOR_INFO, author.getText().toString());
        outState.putString(KEY_BOOK_NAME, bookName.getText().toString());
        outState.putString(KEY_SAVED_TITLE, title.getText().toString());
        outState.putString(KEY_SAVED_SUBTITLE, subtitle.getText().toString());
        outState.putString(KEY_SAVED_PROGRESS, progress.getText().toString());
        outState.putStringArrayList(KEY_LIST_TITLES, titles);
        outState.putStringArrayList(KEY_LIST_SUBTITLES, subtitles);
        outState.putStringArrayList(KEY_LIST_TEXTS, texts);
    }

    public String calculateProgress(int position, int length) {
        //String of = getResources().getString(R.string.progress_word_of);
        double progress = ((double) (position+1) / (double) length) * 100;
        String result = String.valueOf(progress);
        return (position+1) + " " + of + " " + length + ": " + result.substring(0, result.indexOf(".") + 2) + "%";
    }

    public void getTitlesAndSubtitles(ArrayList<Section> chapters, ArrayList<String> titles,
                                      ArrayList<String> subtitles, ArrayList<String> texts) {
        titles.clear();
        subtitles.clear();
        texts.clear();
        StringBuilder subtitlesSB, textsSB;
        for(int i = 0; i < chapters.size(); i++) {
            subtitlesSB = new StringBuilder();
            textsSB = new StringBuilder();
            titles.add(chapters.get(i).title);
            for(int j = 0; j < chapters.get(i).subtitles.size(); j++) {
                subtitlesSB.append(chapters.get(i).subtitles.get(j)).append("\n");
                if(!chapters.get(i).texts.get(j).isEmpty())
                textsSB.append(addSpaces(chapters.get(i).texts.get(j).substring(0, chapters.get(i).texts.get(j).length() > 124 ? 124 : chapters.get(i).texts.get(j).length()-1))).append("…").append("\n");
                else textsSB.append("");
            }
            subtitles.add(subtitlesSB.toString());
            texts.add(textsSB.toString());
        }
    }
}
