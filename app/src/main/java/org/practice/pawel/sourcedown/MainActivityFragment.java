package org.practice.pawel.sourcedown;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivityFragment extends Fragment {
    public MainActivityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

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
        OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String...params) {
            Request.Builder builder = new Request.Builder();
            builder.url(params[0]);
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                return response.body().string();
            } catch (Exception e){
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            TextView textView = (TextView) getActivity().findViewById(R.id.content_view);
            textView.setText(result);
        }
    }
}
