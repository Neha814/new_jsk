package transport.vendor.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.sourceforge.zbar.Symbol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import transport.vendor.activity.HomeActivity;
import transport.vendor.activity.R;
import utils.Constants;
import utils.NetConnection;
import utils.StringUtils;

/**
 * Created by bharat on 12/22/15.
 */
public class WorkListFragment extends Fragment {


    // ********************* customer ******************************//

    View rootView;
    Typeface face;
    TextInputLayout search_layout;
    EditText search_edt;
    Button pod_qrcode_bt;
    ListView listview;
    private AsyncHttpClient client;
    private ProgressDialog dialog;
    String TAG = "WorkListFragment";
    Boolean isConnected;
    MyAdapter mAdapter;
    ArrayList<HashMap<String, String>> workOrderList = new ArrayList<HashMap<String, String>>();

    private static final int ZBAR_SCANNER_REQUEST = 0;
    private static final int ZBAR_QR_SCANNER_REQUEST = 1;
    
    private static final int GALLERY_IAMGE = 2;
    private static final int CAMERA_IMAGE = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_worklist, container, false);
        isConnected = NetConnection.checkInternetConnectionn(getActivity());
        face = Typeface.createFromAsset(getActivity().getAssets(), "Avenir-Book.otf");
        HomeActivity.changeTitle("WORK ORDER LIST", false, false);

        init();

        return rootView;
    }

    private void init() {
        client = new AsyncHttpClient();
        client.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        search_layout = (TextInputLayout) rootView.findViewById(R.id.search_layout);
        search_edt = (EditText) rootView.findViewById(R.id.search_edt);
        pod_qrcode_bt = (Button) rootView.findViewById(R.id.pod_qrcode_bt);
        listview = (ListView) rootView.findViewById(R.id.listview);

        search_edt.setTypeface(face);
        pod_qrcode_bt.setTypeface(face);

        //search_edt.setText("From zipcode");
       // search_layout.setHint("SEARCH FROM WO NUMBER");

        search_layout.setHint("SEARCH FROM WO NUMBER");
        search_edt.setText("");

        pod_qrcode_bt.setText("Find");


        pod_qrcode_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String text = search_edt.getText().toString()
                            .toLowerCase(Locale.getDefault());
                    if(text.trim().length()>0){
                        search_layout.setError(null);
                        mAdapter.filter(text);
                    }
                    else if(text.trim().length()==0){
                        mAdapter.filter("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (isConnected) {
            CallWorkListAPI();
        } else {
            StringUtils.showDialog(Constants.NO_INTERNET, getActivity());
        }
    }


    private void FindFromList() {
        String text = search_edt.getText().toString()
                .toLowerCase(Locale.getDefault());
       // if(text.length()>0){
            mAdapter.filter(text);
       /* }else {
            search_layout.setError("Please enter WO number");
        }*/
        /*search_edt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String text = search_edt.getText().toString()
                            .toLowerCase(Locale.getDefault());
                  if(text.trim().length()>0){
                      search_layout.setError(null);
                  }
                    else if(text.trim().length()==0){
                      mAdapter.filter("");
                  }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/
    }

    private void ScanBarcode() {
        if (isCameraAvailable()) {
            Intent intent = new Intent(getActivity(), ZBarScannerActivity.class);
            intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
            startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
        } else {
            Toast.makeText(getActivity(), "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isCameraAvailable() {
        PackageManager pm = getActivity().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void CallWorkListAPI() {
        // http://phphosting.osvin.net/JSKT/API/WorkOrderList.php?userid=1
        RequestParams params = new RequestParams();
        params.put("userid",Constants.USER_ID);
        Log.e("parameters", params.toString());
        Log.e("URL", Constants.WORKORDER_LIST_API + "?" + params.toString());
        client.post(getActivity(), Constants.WORKORDER_LIST_API, params, new JsonHttpResponseHandler() {

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

                        workOrderList.clear();
                        JSONArray data = response.getJSONArray("Data");
                        for (int i = 0; i < data.length(); i++) {
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("userid", data.getJSONObject(i).getString("userid"));
                            hashMap.put("WorkOrderId", data.getJSONObject(i).getString("WorkOrderId"));
                            hashMap.put("Phone", data.getJSONObject(i).getString("Phone"));
                            hashMap.put("CompanyName", data.getJSONObject(i).getString("CompanyName"));
                            hashMap.put("FirstName", data.getJSONObject(i).getString("FirstName"));
                            hashMap.put("LastName", data.getJSONObject(i).getString("LastName"));
                            hashMap.put("Email", data.getJSONObject(i).getString("Email"));
                            hashMap.put("WO_Number", data.getJSONObject(i).getString("WO_Number"));
                            hashMap.put("WO_Date", data.getJSONObject(i).getString("WO_Date"));
                            hashMap.put("Loading_Date", data.getJSONObject(i).getString("Loading_Date"));
                            hashMap.put("ContainerTypeName", data.getJSONObject(i).getString("ContainerTypeName"));
                            hashMap.put("Cargo_WeightTypeName", data.getJSONObject(i).getString("Cargo_WeightTypeName"));
                            hashMap.put("status", data.getJSONObject(i).getString("status"));
                            hashMap.put("PODImage", data.getJSONObject(i).getString("PODImage"));
                            hashMap.put("ContainerID", data.getJSONObject(i).getString("ContainerID"));

                            workOrderList.add(hashMap);
                        }

                        mAdapter = new MyAdapter(getActivity(), workOrderList);
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
            mDisplayedValues.addAll(workOrderList);
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());

            workOrderList.clear();
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
        }

        @Override
        public int getCount() {

            return workOrderList.size();
        }

        @Override
        public Object getItem(int position) {
            return workOrderList.get(position);
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
                convertView = mInflater.inflate(R.layout.work_order_listitem,
                        null);

              //  holder.detail1 = (TextView) convertView.findViewById(R.id.detail1);
                holder.view_bt = (Button) convertView.findViewById(R.id.view_bt);
                holder.track_pod_bt = (Button) convertView.findViewById(R.id.track_pod_bt);
                holder.wo_tv = (TextView) convertView.findViewById(R.id.wo_tv);
                holder.date_tv = (TextView) convertView.findViewById(R.id.date_tv);
                holder.container_tv = (TextView) convertView.findViewById(R.id.container_tv);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.wo_tv.setTypeface(face);
            holder.date_tv.setTypeface(face);
            holder.container_tv.setTypeface(face);
            holder.view_bt.setTypeface(face);
            holder.track_pod_bt.setTypeface(face);

            holder.view_bt.setTag(position);
            holder.track_pod_bt.setTag(position);

            String inputFormat ="yyyy-mm-dd" ;
            String outputFormat = "dd MMM yyyy";
            String inputDate = workOrderList.get(position).get("Loading_Date");

            String dateString = StringUtils.formateDateFromstring(inputFormat,outputFormat,inputDate);
//            holder.detail1.setText((position + 1) + ". " + workOrderList.get(position).get("WO_Number")
//                    + " " + dateString + " " +
//                    workOrderList.get(position).get("ContainerID"));

            holder.wo_tv.setText(workOrderList.get(position).get("WO_Number"));
            holder.date_tv.setText(dateString);
            holder.container_tv.setText(workOrderList.get(position).get("ContainerID"));

           /* holder.view_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (Integer) view.getTag();
                    String podUrl = workOrderList.get(pos).get("PODImage");
                    if (podUrl.length() > 4) {
                        Uri uri = Uri.parse(podUrl); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } else {
                        StringUtils.showDialog("No POD available", getActivity());
                    }
                }
            });*/

            holder.view_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();
                    String podUrl = workOrderList.get(pos).get("PODImage");
                    if (podUrl.length() > 4) {
                        Constants.WORK_ORDER_ID = workOrderList.get(pos).get("WorkOrderId");
                        GoToViewPODScreen();
                    } else {
                        StringUtils.showDialog("No POD available", getActivity());
                    }


                }
            });


            return convertView;
        }


        class ViewHolder {
           // TextView detail1;
            TextView wo_tv, date_tv, container_tv;
            Button view_bt, track_pod_bt;
        }

    }

    private void GoToViewPODScreen() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = null;
        fragment = new ViewPodFragment();
        if (fragment != null) {
            ft.replace(R.id.frame_layout, fragment);
        }
        else {
            ft.add(R.id.frame_layout, fragment);
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    protected void showPicImageDialog(String msg) {
        final Dialog dialog;
        dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFormat(PixelFormat.TRANSLUCENT);

        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(0);
        dialog.getWindow().setBackgroundDrawable(d);
        TextView static_tv;
        Button ok_bt, gallery_bt,camera_bt;
        LinearLayout cross_layout;
        ImageView cross_img;

        dialog.setContentView(R.layout.pick_image_dialog);
        static_tv = (TextView) dialog.findViewById(R.id.static_tv);
        ok_bt = (Button) dialog.findViewById(R.id.ok_bt);
        gallery_bt = (Button) dialog.findViewById(R.id.gallery_bt);
        camera_bt = (Button) dialog.findViewById(R.id.camera_bt);
        cross_layout = (LinearLayout) dialog.findViewById(R.id.cross_layout);
        cross_img = (ImageView) dialog.findViewById(R.id.cross_img);

        static_tv.setTypeface(face);
        ok_bt.setTypeface(face);
        gallery_bt.setTypeface(face);
        camera_bt.setTypeface(face);


        dialog.show();

        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        cross_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        cross_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

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

    }

    private void SelectImageFromCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_IMAGE);
    }

    private void SelectImageFromGallery() {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_IAMGE);
    }

    private void GoToAppointmnetConfirmationScreen() {
         FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = null;
            fragment = new AppointmentConfirmFragment();
            if (fragment != null) {
                ft.replace(R.id.frame_layout, fragment);
            }
            else {
                ft.add(R.id.frame_layout, fragment);
            }
            ft.addToBackStack(null);
            ft.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ZBAR_SCANNER_REQUEST:
            case ZBAR_QR_SCANNER_REQUEST:
                if (resultCode == getActivity().RESULT_OK) {
                    String contents = data.getStringExtra(ZBarConstants.SCAN_RESULT);
                    Toast.makeText(getActivity(), contents, Toast.LENGTH_LONG).show();

                } else if (resultCode == getActivity().RESULT_CANCELED && data != null) {
                    String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
                    Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                }
                break;

            case GALLERY_IAMGE:
                if(resultCode == getActivity().RESULT_OK && data != null){
                    Uri selectedImage = data.getData();
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
                } break;

            case CAMERA_IMAGE:
                if(resultCode == getActivity().RESULT_OK && data != null){
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
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

        } break;
        }
    }
}
