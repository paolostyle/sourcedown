package org.practice.pawel.sourcedown;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class MainFragment extends Fragment {
    public MainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final EditText urlAddress = (EditText) rootView.findViewById(R.id.urlAddress);
        urlAddress.setSelection(urlAddress.getText().length());

        Button clickButton = (Button) rootView.findViewById(R.id.download);
        clickButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getActivity().runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       OkHttpHandler handler = new OkHttpHandler();
                       handler.execute(urlAddress.getText().toString());
                   }
               });
            }
        });

        return rootView;
    }

    private class OkHttpHandler extends AsyncTask<String, Void, String> {
        private final OkHttpClient client = new OkHttpClient();
        private boolean errorOccurred = false;
        private DBHandler db = new DBHandler(getContext());

        @Override
        protected String doInBackground(String...params) {
            try {
                if(Helper.isConnected()) {
                    Request.Builder builder = new Request.Builder();
                    builder.url(params[0]);
                    Request request = builder.build();
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException(response.code() + ": " + response.message());

                    String content = response.body().string();

                    if(db.updateSource(params[0], content) == 0)
                        db.insertSource(params[0], content);

                    return content;
                }
                else {
                    Cursor cursor = db.getSource(params[0]);
                    if(cursor.moveToFirst()) {
                        String content = getString(R.string.noconnection_info);
                        return content + "\n\n" + cursor.getString(2);
                    }
                    else
                        throw new ConnectException(getString(R.string.error_connection));
                }
            } catch (Exception e){
                errorOccurred = true;
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(errorOccurred) {
                Helper.displayAlert(getActivity(), getString(R.string.alert_error), result);
            }
            else {
                InputMethodManager imm = (InputMethodManager)(getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                imm.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);
                TextView textView = (TextView) getActivity().findViewById(R.id.content_view);
                textView.setText(result);
            }
        }
    }
}
