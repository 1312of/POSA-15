package vandy.mooc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

/**
 * An Activity that downloads an image, stores it in a local file on
 * the local device, and returns a Uri to the image file.
 */
public class DownloadImageActivity extends Activity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    public static final String EXTRA_URL = "EXTRA_URL";
    public static final String EXTRA_PATH = "EXTRA_PATH";
    
    private Uri mUri;
    private DownloadImageTask mDownloadTask;
    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mUri = Uri.parse(getIntent().getStringExtra(EXTRA_URL));
        } else {
            mUri = Uri.parse(savedInstanceState.getString(EXTRA_URL));
        }
        mDownloadTask = new DownloadImageTask();
        mDownloadTask.execute(mUri);
        
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_URL, mUri.toString());
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDownloadTask != null) {
            mDownloadTask.cancel(true);
        }
    }
    
    private class DownloadImageTask extends AsyncTask<Uri, Void, Uri> {

        @Override
        protected Uri doInBackground(Uri... params) {
            return Utils.downloadImage(DownloadImageActivity.this, params[0]);
        }
        
        @Override
        protected void onPostExecute(Uri result) {
            super.onPostExecute(result);
            if (result != null && !TextUtils.isEmpty(result.toString())) {
                 new FilterImageTask().execute(result);
            } else {
                Intent intent = getIntent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
            
        }
        
    }
    
    private class FilterImageTask extends AsyncTask<Uri, Void, Uri> {

        @Override
        protected Uri doInBackground(Uri... params) {
            return Utils.grayScaleFilter(DownloadImageActivity.this, params[0]);
        }
        
        @Override
        protected void onPostExecute(Uri result) {
            Intent intent = getIntent();
            if (result != null && !TextUtils.isEmpty(result.toString())) {
                intent.putExtra(EXTRA_PATH, result.toString());
                setResult(RESULT_OK, intent);    
            } else {
                setResult(RESULT_CANCELED, intent);
            }
            finish();
            super.onPostExecute(result);
        }
        
    }
    
    
    
}
