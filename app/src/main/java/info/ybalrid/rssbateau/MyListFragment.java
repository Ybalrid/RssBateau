package info.ybalrid.rssbateau;

import android.app.Activity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;


public class MyListFragment extends ListFragment {

    private OnItemSelectedListener listener;
    private ArrayList<String> list;
    private ArrayList<RssItem> listContent;
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle b)
    {
        super.onCreate(b);

        String[] values = new String[] { "this", "is", "a", "test"};

        list = new ArrayList<>();
        listContent = new ArrayList<>();
        Collections.addAll(list, values);
    }

    @Override
    public void onActivityCreated(Bundle b)
    {
        super.onActivityCreated(b);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, list);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
        getListView().setOnItemClickListener(new ListView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                updateDetail(listContent.get(i));
            }
        });

        refresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rsslist_overview,
                container, false);
        Button button = view.findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });



        return view;
    }

    public void parse(InputStream s) {
        listContent.clear();
        String title = null, link = null, description = null;
        Log.d("Async", "cleared...");
        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(s, null);
            boolean isItem = false;
            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if (name == null) continue;


                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (result != null)
                    Log.d("Async", result);

                if (name.equalsIgnoreCase("title"))
                    title = result;
                else if (name.equalsIgnoreCase("link"))
                    link = result;
                else if (name.equalsIgnoreCase("description"))
                    description = result;

                if (title != null && link != null && description != null) {
                    if (isItem) {
                        RssItem rssItem = new RssItem(title, link, description);
                        listContent.add(rssItem);
                    }


                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }


            }
        } catch (XmlPullParserException e) {
            Log.e("Async", e.getMessage());
        } catch (java.io.IOException e) {
            Log.e("Async", e.getMessage());
        }
    }

    public void refresh()
    {
        Toast.makeText(getActivity(), "Rafraichissement du flux RSS...", Toast.LENGTH_SHORT).show();
        new FetchFeedTask().execute((Void)null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implemenet info.ybalrid.rssbateau.MyListFragment.OnItemSelectedListener");
        }
    }

    // May also be triggered from the Activity
    public void updateDetail(RssItem content) {
        // create fake data
        //String newTime = String.valueOf(System.currentTimeMillis());
        // Send data to Activity


        listener.onRssItemSelected(content);
    }

    public interface OnItemSelectedListener {
        void onRssItemSelected(RssItem link);
    }

    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String target = getResources().getString(R.string.default_feed);
                Log.d("MyTag", target);

                URL url = new URL(target);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setRequestProperty("Content-type", "application/xml");
                conn.connect();

                parse(conn.getInputStream());
                return true;
            } catch (java.net.MalformedURLException e) {
                //Don't care
            } catch (java.io.IOException e) {
                //Don't care either
            }


            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                list.clear();
                for (int i = 0; i < listContent.size(); i++) {
                    list.add(listContent.get(i).title);
                }

                adapter.notifyDataSetChanged();
            }
        }

    }
}
