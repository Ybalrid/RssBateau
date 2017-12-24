package info.ybalrid.rssbateau;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class RssFeedActivity extends Activity implements
        MyListFragment.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssfeed);
    }

    @Override
    public void onRssItemSelected(RssItem item) {

        String content = item.title + "\n\n" + item.descrition;
        DetailFragment fragment = (DetailFragment) getFragmentManager()
                .findFragmentById(R.id.detailFragment);


        if (fragment != null && fragment.isInLayout()) {
            fragment.setText(content);
            fragment.setLink(item.link);
        }

        else
        {
            Intent intent = new Intent(getApplicationContext(),
                    DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_CONTENT, content);
            intent.putExtra(DetailActivity.EXTRA_URL, item.link);
            startActivity(intent);
        }
    }

}
