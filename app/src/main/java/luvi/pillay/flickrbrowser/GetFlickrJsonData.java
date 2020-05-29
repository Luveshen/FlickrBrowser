package luvi.pillay.flickrbrowser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class GetFlickrJsonData extends AsyncTask<String, Void, List<Photo>> implements GetRawData.OnDownloadComplete {

    private static final String TAG = "GetFlickrJsonData";
    private List<Photo> photoList = null;
    private String baseUrl;
    private String language;
    private boolean matchAll;

    private final OnDataAvailable callback;
    private boolean runningOnSameThread = false;

    public GetFlickrJsonData(OnDataAvailable callback, String baseUrl, String language, boolean matchAll) {
        Log.d(TAG, "GetFlickrJsonData: called");
        this.baseUrl = baseUrl;
        this.language = language;
        this.matchAll = matchAll;
        this.callback = callback;
    }

    @Override
    protected List<Photo> doInBackground(String... params) {
        Log.d(TAG, "doInBackground: starts");
        String destinationUri = createUri(params[0]);

        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(destinationUri);
        Log.d(TAG, "doInBackground: ends");

        return photoList;
    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute: starts");

        if(callback != null){
            callback.onDataAvailable(photoList, DownloadStatus.OK);
        }
    }

    void executeOnSameThread(String searchCriteria){
        runningOnSameThread = true;
        Log.d(TAG, "ExecuteOnSameThread: starts");
        String destinationUri = createUri(searchCriteria);

        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(destinationUri);
        Log.d(TAG, "ExecuteOnSameThread: ends");
    }

    private String createUri(String searchCriteria) {
        Log.d(TAG, "createUri: starts");

        return Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("tags", searchCriteria)
                .appendQueryParameter("tagmode", matchAll ? "ALL" : "ANY")
                .appendQueryParameter("lang", language)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .build()
                .toString();
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        Log.d(TAG, "onDownloadComplete: starts");
        if(status == DownloadStatus.OK){
            photoList = new ArrayList<>();

            try {
                JSONObject jsonData = new JSONObject(data);
                JSONArray itemsArray = jsonData.getJSONArray("items");

                for(int i = 0; i < itemsArray.length(); i++){
                    JSONObject jsonPhoto = itemsArray.getJSONObject(i);
                    String title = jsonPhoto.getString("title");
                    String author = jsonPhoto.getString("author");
                    String authorId = jsonPhoto.getString("author_id");
                    String tags = jsonPhoto.getString("tags");

                    JSONObject jsonMedia = jsonPhoto.getJSONObject("media");
                    String photoUrl = jsonMedia.getString("m");

                    String link = photoUrl.replaceFirst("_m.", "_b.");

                    Photo photoObject = new Photo(title, author, authorId, link, tags, photoUrl);
                    photoList.add(photoObject);

                    Log.d(TAG, "onDownloadComplete: " + photoObject.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "onDownloadComplete: Error processing Json dats " + e.getMessage());
                status = DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        if(callback != null && !runningOnSameThread){
            callback.onDataAvailable(photoList, status);
        }

        Log.d(TAG, "onDownloadComplete: ends");
    }
}
