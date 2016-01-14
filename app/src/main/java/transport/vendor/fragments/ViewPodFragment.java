package transport.vendor.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import transport.vendor.activity.HomeActivity;
import transport.vendor.activity.R;
import utils.Constants;
import utils.Func;
import utils.NetConnection;
import utils.StringUtils;

/**
 * Created by sandeep on 12/30/15.
 */
public class ViewPodFragment extends Fragment {

    View rootView;
    Typeface face ;
    String TAG = "ViewPodFragment";
    private AsyncHttpClient client;
    private ProgressDialog dialog;
    Boolean isConnected;
    ListView listview;
    Button upload_pod_bt;
    //EditText search_edt;
    //TextInputLayout search_layout;
    MyAdapter mAdapter;
    ArrayList<HashMap<String, String>> PODList = new ArrayList<HashMap<String, String>>();
    private static final int GALLERY_IAMGE = 2;
    private static final int CAMERA_IMAGE = 3;
    LinearLayout parent_layout;

    File imageFile;
    String imagePath;
    Uri imageUri;

   // DownloadTask DownloadObj;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.view_uploaded_pod, container, false);
        face= Typeface.createFromAsset(getActivity().getAssets(), "Avenir-Book.otf");
        HomeActivity.changeTitle("VIEW UPLOADED POD", true, false);
        isConnected = NetConnection.checkInternetConnectionn(getActivity());

        init();

        return  rootView ;
    }

    private void init() {
        client = new AsyncHttpClient();
        client.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        listview = (ListView) rootView.findViewById(R.id.listview);
        upload_pod_bt = (Button) rootView.findViewById(R.id.upload_pod_bt);
        parent_layout = (LinearLayout) rootView.findViewById(R.id.parent_layout);

        upload_pod_bt.setTypeface(face);

        if(Constants.ROLE_ID.equals("1")){
            upload_pod_bt.setVisibility(View.VISIBLE);
        } else if(Constants.ROLE_ID.equals("2")) {
            upload_pod_bt.setVisibility(View.GONE);
        }

        if (isConnected) {
            CallPODListingAPI();
        } else {
            StringUtils.showDialog(Constants.NO_INTERNET, getActivity());
        }

        upload_pod_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicImageDialog("Select image from");
            }
        });
    }

    protected void showPicImageDialog(String msg) {
        final Dialog dialog;
        dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFormat(PixelFormat.TRANSLUCENT);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;

        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(0);
        dialog.getWindow().setBackgroundDrawable(d);

        Button cancel_bt, gallery_bt,camera_bt;


        dialog.setContentView(R.layout.pick_image_dialog);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        gallery_bt = (Button) dialog.findViewById(R.id.gallery_bt);
        camera_bt = (Button) dialog.findViewById(R.id.camera_bt);
        cancel_bt = (Button) dialog.findViewById(R.id.cancel_bt);


        gallery_bt.setTypeface(face);
        camera_bt.setTypeface(face);
        cancel_bt.setTypeface(face);


        dialog.show();


        gallery_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SelectImageFromGallery();
            }
        });

        camera_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SelectImageFromCamera();
            }
        });

        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void SelectImageFromCamera() {
        /*Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_IMAGE);*/

        Intent cameraIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
        startActivityForResult(cameraIntent, CAMERA_IMAGE);
    }

    private void SelectImageFromGallery() {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_IAMGE);
    }

    public Uri getTempUri() {
        // Create an image file name
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dt = sdf.format(new Date());
        imageFile = null;
        imageFile = new File(Environment.getExternalStorageDirectory()
                + "/JSK/", "Camera_" + dt + ".jpg");
        File file = new File(Environment.getExternalStorageDirectory()
                + "/JSK");
        if (!file.exists()) {
            file.mkdir();
        }

        if (!imageFile.exists()) {
            try {
                imageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imagePath = Environment.getExternalStorageDirectory() + "/JSK/"
                + "Camera_" + dt + ".jpg";
        imageUri = Uri.fromFile(imageFile);
        return imageUri;
    }

    private void CallPODListingAPI() {
        // http://phphosting.osvin.net/JSKT/API/ListPodImages.php?workorderid=1
        RequestParams params = new RequestParams();
        params.put("workorderid",Constants.WORK_ORDER_ID);

        Log.e("parameters", params.toString());
        Log.e("URL", Constants.POD_IMAGES_LIST + "?" + params.toString());
        client.post(getActivity(), Constants.POD_IMAGES_LIST, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Log.e("onsuccess", response.toString());
                    if (response.getBoolean("ResponseCode")) {

                        PODList.clear();
                        JSONArray data = response.getJSONArray("Data");
                        for (int i = 0; i < data.length(); i++) {
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("id", data.getJSONObject(i).getString("id"));
                            hashMap.put("image", data.getJSONObject(i).getString("image"));
                            hashMap.put("date", data.getJSONObject(i).getString("date"));


                            PODList.add(hashMap);
                        }

                        mAdapter = new MyAdapter(getActivity(), PODList);
                        listview.setAdapter(mAdapter);

                    } else {
                        StringUtils.showDialog(response.getString("MessageWhatHappen"), getActivity());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, responseString + "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        LayoutInflater mInflater = null;
        private ArrayList<HashMap<String, String>> mDisplayedValues;

        public MyAdapter(Context context,
                         ArrayList<HashMap<String, String>> list) {
            mInflater = LayoutInflater.from(getActivity());
            mDisplayedValues = new ArrayList<HashMap<String, String>>();
            mDisplayedValues.addAll(PODList);
        }

       /* public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());

            PODList.clear();
            if (charText.length() == 0) {

                workOrderList.addAll(mDisplayedValues);
            } else {

                for (int i = 0 ;i<mDisplayedValues.size();i++) {

                    if (mDisplayedValues.get(i).get("WO_Number").toLowerCase(Locale.getDefault())
                            .startsWith(charText)) {


                        workOrderList.add(mDisplayedValues.get(i));

                    }
                }
            }
            notifyDataSetChanged();
        }*/

        @Override
        public int getCount() {

            return PODList.size();
        }

        @Override
        public Object getItem(int position) {
            return PODList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.pod_listitem,
                        null);

                holder.detail1 = (TextView) convertView.findViewById(R.id.detail1);
                holder.view_bt = (Button) convertView.findViewById(R.id.view_bt);
                holder.download_pod_bt = (Button) convertView.findViewById(R.id.download_pod_bt);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.detail1.setTypeface(face);
            holder.view_bt.setTypeface(face);
            holder.download_pod_bt.setTypeface(face);

            holder.view_bt.setTag(position);
            holder.download_pod_bt.setTag(position);

            holder.detail1.setTypeface(face);
            holder.detail1.setText("Camera/Gallery" + " " + StringUtils.SeparateDate(PODList.get(position).get("date")));

            holder.view_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();
                    String podUrl = PODList.get(pos).get("image");
                    Uri uri = Uri.parse(podUrl); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });

            holder.download_pod_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (Integer) view.getTag();
                    String podUrl = PODList.get(pos).get("image");
                    DownloadTask DownloadObj = new DownloadTask(podUrl);
                    DownloadObj.execute();
                }
            });


            return convertView;
        }

        class ViewHolder {
            TextView detail1;
            Button view_bt,download_pod_bt;
        }

    }

    private void uploadImageToBackend(String Base64String) {
        // http://phphosting.osvin.net/JSKT/API/AddPODImage.php?workorderid=1&podimage=

        RequestParams params = new RequestParams();
       /* File file ;
        file = galleryFile;*/
        params.put("podimage", Base64String);
        params.put("workorderid", Constants.WORK_ORDER_ID);

        Log.e("parameters", params.toString());
        Log.e("URL", Constants.UPLOAD_POD_IMAGE + "?" + params.toString());
        client.post(getActivity(), Constants.UPLOAD_POD_IMAGE, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Log.e("onsuccess", response.toString());
                    if (response.getBoolean("ResponseCode")) {
                        StringUtils.showDialog(response.getString("MessageWhatHappen"), getActivity());

                        String id = response.getString("id");
                        String images = response.getString("images");
                        String datetext = response.getString("date");

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("id",id);
                        map.put("image",images);
                        map.put("date",datetext);

                        PODList.add(map);
                        mAdapter.notifyDataSetChanged();

                    } else {
                        StringUtils.showDialog(response.getString("MessageWhatHappen"), getActivity());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG+"1", responseString + "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG+"2", "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG+"3", "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case GALLERY_IAMGE:
                if(resultCode == getActivity().RESULT_OK && data != null){
                    Uri selectedImage = data.getData();
                    InputStream imageStream = null;
                    try {
                        ContentResolver appContext;
                        appContext = getActivity().getContentResolver();
                        imageStream = appContext.openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.e("Exception==", "" + e);
                    }
                    Bitmap takenImage = BitmapFactory.decodeStream(imageStream);

                    String base64String = StringUtils.getBase64String(takenImage);
                    uploadImageToBackend(base64String);

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    File galleryFile = new File(picturePath);
                    Log.e(TAG,"gallery file==>"+galleryFile);

                    // String picturePath contains the path of selected Image

                    // uploadImageToBackend(galleryFile);
                } break;

            case CAMERA_IMAGE:
             //   if(resultCode == getActivity().RESULT_OK && data != null){
                  /*  Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                    File destination = new File(Environment.getExternalStorageDirectory(),
                            System.currentTimeMillis() + ".png");
                    FileOutputStream fo;
                    try {
                        destination.createNewFile();
                        fo = new FileOutputStream(destination);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.e(TAG,"camera file==>"+destination);
                    String base64String = StringUtils.getBase64String(thumbnail);
                    uploadImageToBackend(base64String);*/
                  /*  Uri contentURI = data.getData();
                    String imagePath = Func.getRealPathFromURI(getActivity(), contentURI);
                   Bitmap bitmap = Func.resizeBitmap(imagePath);
                    String base64String = StringUtils.getBase64String(bitmap);*/

                try {
                    Bitmap bitmap = Func.resizeBitmap(imagePath);
                    String base64String = StringUtils.getBase64String(bitmap);
                    uploadImageToBackend(base64String);
                } catch(Exception e){
                    e.printStackTrace();
                }
              //  }
               break;
        }
    }

    // ****************************************** DownLoa Image **************************//

    class DownloadTask extends AsyncTask<Void, Void, Void> {
        public ProgressDialog dialog;
        String url;
        DownloadTask(String url)
        {
            this.url = url;
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Downloading...");
            dialog.setCancelable(true);
            dialog.setIndeterminate(true);
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialog.dismiss();

            Snackbar snackbar = Snackbar
                    .make(parent_layout, "Download completed.", Snackbar.LENGTH_LONG);
            snackbar.show();

           // Toast.makeText(getActivity(),"Download completed.",Toast.LENGTH_SHORT).show();

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }
        protected Void doInBackground(Void... arg0) {
            try {
                //set the download URL, a url that points to a file on the internet
                //this is the file to be downloaded
                URL url = new URL(this.url);

                //create the new connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //set up some things on the connection
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);

                //and connect!
                urlConnection.connect();

                //set the path where we want to save the file
                //in this case, going to save it on the root directory of the
                //sd card.
              //  File SDCardRoot = Environment.getExternalStorageDirectory();
                File SDCardRoot = new File(Environment.getExternalStorageDirectory()
                        + "/JSK");
                //create a new file, specifying the path, and the filename
                //which we want to save the file as.

                long CurrentTimeMillis = System.currentTimeMillis();
                File file = new File(SDCardRoot,CurrentTimeMillis+".jpg");

                //this will be used to write the downloaded data into the file we created
                FileOutputStream fileOutput = new FileOutputStream(file);

                //this will be used in reading the data from the internet
                InputStream inputStream = urlConnection.getInputStream();

                //this is the total size of the file
                int totalSize = urlConnection.getContentLength();
                //variable to store total downloaded bytes
                int downloadedSize = 0;

                //create a buffer...
                byte[] buffer = new byte[1024];
                int bufferLength = 0; //used to store a temporary size of the buffer

                //now, read through the input buffer and write the contents to the file
                while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                    //add the data in the buffer to the file in the file output stream (the file on the sd card
                    fileOutput.write(buffer, 0, bufferLength);
                    //add up the size so we know how much is downloaded
                    downloadedSize += bufferLength;
                    //this is where you would do something to report the prgress, like this maybe
                    //updateProgress(downloadedSize, totalSize);

                }
                //close the output stream when done
                fileOutput.close();

                //catch some possible errors...
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
