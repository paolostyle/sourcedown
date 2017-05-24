package org.practice.pawel.sourcedown;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivityFragment extends Fragment {
    public MainActivityFragment() {}

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

    public static boolean isConnected() {
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://clients3.google.com/generate_204").openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();
            return (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0);
        } catch (IOException e) {
            return false;
        }
    }

    public void displayAlert(String text) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getString(R.string.alert_error));
        alertDialog.setMessage(text);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private class OkHttpHandler extends AsyncTask<String, Void, String> {
        private OkHttpClient client = new OkHttpClient();
        private boolean errorOccurred = false;

        @Override
        protected String doInBackground(String...params) {
            try {
                if(isConnected()) {
                    Request.Builder builder = new Request.Builder();
                    builder.url(params[0]);
                    Request request = builder.build();
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException(response.code() + ": " + response.message());
                    return response.body().string();
                }
                else {
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
                displayAlert(result);
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
