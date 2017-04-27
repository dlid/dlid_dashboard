package se.dlid.dashboard_share;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ShareActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        HandleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        HandleIntent(intent);
    }


    private void HandleIntent(Intent receivedIntent) {

        //get the action
        String receivedAction = receivedIntent.getAction();
        //find out what we are dealing with
        String receivedType = receivedIntent.getType();
        TextView txtView = (TextView)findViewById(R.id.textView);
        TextView resourceTitle = (TextView)findViewById(R.id.resourceTitle);
        txtView.setText("Action: " + receivedAction + "\n" + "Type: " + receivedType);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar3);

        txtView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        resourceTitle.setVisibility(View.INVISIBLE);

        if (receivedType.startsWith("text/plain")) {
            txtView.setText( txtView.getText() + "\n\n" + receivedIntent.getStringExtra(Intent.EXTRA_TEXT) );
            String text = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);

            if (text.startsWith("http://") || text.startsWith(("https://"))) {
                Meh job = new Meh();
                job.execute(text);
            }
        } else {
            txtView.setText( txtView.getText() + "\n\nVa?" );
        }


    }

    private class Meh extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] params) {
            // do above Server call here
            String html = downloadWebpage(params[0]);

            return html;
        }

        @Override
        protected void onPostExecute(String message) {
            String t = "";
            TextView txtView = (TextView)findViewById(R.id.textView);
           //  txtView.setText( txtView.getText() + "\n\n" + message );
            TextView resourceTitle = (TextView)findViewById(R.id.resourceTitle);

            Pattern mPattern = Pattern.compile("<title>(.*?)</title>", Pattern.MULTILINE);
            Matcher m = mPattern.matcher(message);
            while (m.find()) {
                resourceTitle.setText(m.group(1));
                //txtView.setText( txtView.getText() + "\n\n" +m.group(1) );

            }

              txtView.setText( txtView.getText() + "\n\n" + message.length() );

            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar3);

//        txtView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            txtView.setVisibility(View.VISIBLE);
            resourceTitle.setVisibility(View.VISIBLE);

        }

        protected String downloadWebpage(String urlString) {
            URL url;
            InputStream is = null;
            BufferedReader br;
            String line = "";
            String content = "";

            try {
                url = new URL(urlString);

                is = url.openStream();  // throws an IOException
                br = new BufferedReader(new InputStreamReader(is));

                while ((line = br.readLine()) != null) {
                    if (content.contains("<body")) break;
                    System.out.println(line);
                    content += line;
                }
            } catch (MalformedURLException mue) {
                mue.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) is.close();
                } catch (IOException ioe) {
                    // nothing to see here
                }
            }
            return content;
        }
    }

}
