package com.tixon.fastfb2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
    public static ArrayList<Section> chapters;
    public ArrayList<String> titles, subtitles, texts;
    int savedPosition = 0;
    int time1 = 199;
    int time2 = 299;
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

    @Override
    protected void onResume() {
        super.onResume();
        //read settings
        time1 = Integer.parseInt(preferences.getString("time1", "200"));
        time2 = Integer.parseInt(preferences.getString("time2", "300"));
        Log.d("myLogs", "in onResume: time1 = " + time1 + ", time2 = " + time2);

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary700));
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("myLogs", "in onCreate: time1 = " + time1 + ", time2 = " + time2);
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

        //Todo: savedPosition
        if(savedInstanceState != null) {
            layoutBookInfo.setVisibility(View.VISIBLE);
            layoutChapter.setVisibility(View.VISIBLE);
            isReading = savedInstanceState.getBoolean(KEY_IS_READING, false);
            isPaused = savedInstanceState.getBoolean(KEY_IS_PAUSED, false);
            if(savedInstanceState.getStringArray(KEY_WORDS) != null) {
                isReading = false;
                isPaused = true;
            }
            if(!isReading && isPaused) {
                layoutControl.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                author.setText(savedInstanceState.getString("author_info"));
                bookName.setText(savedInstanceState.getString("book_name"));
                title.setText(savedInstanceState.getString("saved_title"));
                subtitle.setText(savedInstanceState.getString("saved_subtitle"));
                chapterNumber = savedInstanceState.getInt("chapterNumber", 0);
                subtitleNumber = savedInstanceState.getInt("subtitleNumber", 0);
                words = savedInstanceState.getStringArray(KEY_WORDS);
                savedPosition = savedInstanceState.getInt(KEY_SAVED_POSITION, 0);
                textView.setText(words[savedPosition]);
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
                        i++;
                    }
                    if(i == words.length - 5) { //Debug only
                        System.out.println(1);
                    }
                    if(i == words.length - 1) { //Конец главы
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
                            if(currentPosition == words.length - 1) fab.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                            if(isNext) {
                                fab.setImageResource(R.drawable.ic_skip_next_white_48dp);
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
                        savedPosition = i;
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
                        textView.setText(words[savedPosition]);
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
                        textView.setText(words[savedPosition]);
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
                    fab.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                    progress.setText(calculateProgress(savedPosition, words.length));
                    isNext = false;
                } else {
                    if(!isReading) {
                        readThread = new Thread(runnable);
                        readThread.start();
                        isReading = true;
                        isPaused = false;
                        fab.setImageResource(R.drawable.ic_pause_white_48dp);
                    } else {
                        try {
                            readThread.interrupt();
                            isReading = false;
                            isPaused = true;
                            fab.setImageResource(R.drawable.ic_play_arrow_white_48dp);
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
            fab.setImageResource(R.drawable.ic_play_arrow_white_48dp);
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
            words = chapters.get(0).texts.get(0).split(" ");
            textView.setText("");
            layoutControl.setVisibility(View.VISIBLE);
            layoutBookInfo.setVisibility(View.VISIBLE);
            author.setText(TitleInfo.author.firstName + " " + TitleInfo.author.middleName + " " + TitleInfo.author.lastName);
            bookName.setText(TitleInfo.bookTitle);
            layoutChapter.setVisibility(View.VISIBLE);
            title.setText(chapters.get(0).title);
            subtitle.setText(chapters.get(0).subtitles.get(0));
            savedPosition = 0;
            isReading = false; isPaused = false; isNext = false;
            fab.setVisibility(View.VISIBLE);
            fab.setImageResource(R.drawable.ic_play_arrow_white_48dp);
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(KEY_WORDS, words);
        outState.putInt(KEY_SAVED_POSITION, savedPosition);
        outState.putInt("chapterNumber", chapterNumber);
        outState.putInt("subtitleNumber", subtitleNumber);
        outState.putBoolean(KEY_IS_READING, isReading);
        outState.putBoolean(KEY_IS_PAUSED, isPaused);
        outState.putString("author_info", author.getText().toString());
        outState.putString("book_name", bookName.getText().toString());
        outState.putString("saved_title", title.getText().toString());
        outState.putString("saved_subtitle", subtitle.getText().toString());
        outState.putStringArrayList("list_titles", titles);
        outState.putStringArrayList("list_subtitles", subtitles);
        outState.putStringArrayList("list_texts", texts);
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
                textsSB.append(chapters.get(i).texts.get(j).substring(0, 124)).append("...").append("\n");
            }
            subtitles.add(subtitlesSB.toString());
            texts.add(textsSB.toString());
        }
    }
}
