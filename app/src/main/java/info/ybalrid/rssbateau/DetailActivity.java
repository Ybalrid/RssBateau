package info.ybalrid.rssbateau;



import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.widget.TextView;

public class DetailActivity extends Activity {

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_CONTENT = "content";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Need to check if Activity has been switched to landscape mode
        // If yes, finished and go back to the start Activity
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }
        setContentView(R.layout.activity_detail);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String s = extras.getString(EXTRA_CONTENT);
            String l = extras.getString(EXTRA_URL);
            /*TextView view = (TextView) findViewById(R.id.detailsText);
            view.setText(s);*/
            Fragment frag = getFragmentManager().findFragmentById(R.id.detailFragment);
            if(frag != null)
            {
                if(frag instanceof DetailFragment) {
                    DetailFragment det = (DetailFragment)frag;
                    det.setText(s);
                    det.setLink(l);
                }
            }

        }
    }
}