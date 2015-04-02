package vandy.mooc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        // @@ TODO -- you fill in here.
        super.onCreate(savedInstanceState);
        // Get the URL associated with the Intent data.
        // @@ TODO -- you fill in here.
        mUri = Uri.parse(getIntent().getStringExtra(EXTRA_URL));
        // Download the image in the background, create an Intent that
        // contains the path to the image file, and set this as the
        // result of the Activity.

        // @@ TODO -- you fill in here using the Android "HaMeR"
        // concurrency framework.  Note that the finish() method
        // should be called in the UI thread, whereas the other
        // methods should be called in the background thread.  See
        // http://stackoverflow.com/questions/20412871/is-it-safe-to-finish-an-android-activity-from-a-background-thread
        // for more discussion about this topic.
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                final String path = getDownloadImagePath(mUri);
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        Intent intent = getIntent();
                        intent.putExtra(EXTRA_PATH, path);
                        if (!TextUtils.isEmpty(path)) {
                            setResult(RESULT_OK, intent);    
                        } else {
                            setResult(RESULT_CANCELED, intent);
                        }
                        finish();
                    }
                });
            }
            
            private String getDownloadImagePath(Uri uri) {
                String fileName = uri.getLastPathSegment();
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), fileName);
                InputStream in = null;
                FileOutputStream out = null;
                try {
                    URL url = new URL(uri.toString());
                    URLConnection urlConn = url.openConnection();
                    in = urlConn.getInputStream();
                    out = new FileOutputStream(file);
                    int c;
                    byte[] b = new byte[1024];
                    while ((c = in.read(b)) != -1)
                        out.write(b, 0, c);
                    Log.d(TAG, "file length: " + file.length());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    try {
                        if (in != null)
                            in.close();
                    } catch (Exception e2) {}
                    try {
                        if (out != null)
                            out.close();
                    } catch (Exception e2) {}
                }
                return file.getAbsolutePath();
            }
        }).start();
    }
    
    
}
