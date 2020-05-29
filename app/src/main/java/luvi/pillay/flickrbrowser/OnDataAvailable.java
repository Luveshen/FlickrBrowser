package luvi.pillay.flickrbrowser;

import android.view.LayoutInflater;

import java.util.List;

interface OnDataAvailable {
    void onDataAvailable(List<Photo> data, DownloadStatus status);
}
