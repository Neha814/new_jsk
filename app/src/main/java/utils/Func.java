package utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;


public class Func {

//    public static String[] drawer_items() {
//        String s[] = new String[]{Constants.ChangePassword, Constants.TrackLocation};
//        return s;
//    }

    public static void set_title_to_actionbar(String Title, Context ctx, Toolbar mToolbar, Boolean value) {

        ((AppCompatActivity) ctx).setSupportActionBar(mToolbar);
        ((AppCompatActivity) ctx).getSupportActionBar().setDisplayShowHomeEnabled(value);
        ((AppCompatActivity) ctx).getSupportActionBar().setDisplayUseLogoEnabled(value);
        ((AppCompatActivity) ctx).getSupportActionBar().setDisplayHomeAsUpEnabled(value);

        if (!Title.equals(null) && Title.length() > 0) {
            SpannableStringBuilder builder = new SpannableStringBuilder();

            Spannable WordtoSpan = new SpannableString(Title.substring(0,
                    1));
            WordtoSpan.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#ffffff")), 1, 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            Spannable WordtoSpan2 = new SpannableString(Title.subSequence(
                    1, Title.length()));
            WordtoSpan2.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#ffffff")), 0,
                    WordtoSpan2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.append(WordtoSpan);
            builder.append(WordtoSpan2);

            ((AppCompatActivity) ctx).getSupportActionBar().setTitle(builder);
        }
    }

    public static void toast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    public static final int editSize(EditText edit) {
        return edit.getText().toString().trim().length();
    }

    /**
     * hide keyboard from edittext
     *
     * @param v   :View
     * @param ctx :Context
     */
    public static void hideKeyboard(View v, Context ctx) {
        if (v != null) {
            v.clearFocus();
            InputMethodManager keyboard = (InputMethodManager) ctx
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

   /* public static void Logout(Context ctx) {
        try {
            SharedPreferences sh = ctx.getSharedPreferences(
                    Constants.PreferenceData, Context.MODE_PRIVATE);
            sh.edit().clear().commit();
            ((Activity) ctx).finish();
            Intent i = new Intent(ctx, LoginActivity.class);
            ((Activity) ctx).startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

   /* public static void Show_Confirm_popup(final Context ctx, String Message, String Yes, String No) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder
                .setMessage(Message)
                .setCancelable(false)
                .setPositiveButton(Yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Logout(ctx);
                            }
                        });
        alertDialogBuilder.setNegativeButton(No,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }*/

   /* private void logout_API(final Context ctx, SharedPreferences sharedPreferences, AsyncHttpClient client, final ProgressDialog dialog) {
        try {
            RequestParams params = new RequestParams();
            params.put("user_id", sharedPreferences.getString(Constants.UserId, ""));
            params.put("token_id", sharedPreferences.getString(Constants.REG_ID, ""));
            params.put("device_id", "0");
            client.post(ctx,"", params, new JsonHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                    dialog.setMessage("Logging Out..");
                    dialog.show();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    dialog.dismiss();
                }

                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    Log.e("Logout Response", json.toString());
                    try {
                        if (json.getBoolean("ResponseCode")) {
                            Func.Logout(ctx);
                         //   ctx.stopService(new Intent(ctx, MainActivity.class));
                        } else {
                            toast(ctx, json.getString("Message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                *//*{"ResponseCode":true,"Message":"User Registered Successfully",
                        "Result":{"email":"osvinandroid@gmail.com","user_id":"22","name":"Osvin AmanAttri"}}*//*
                    // Here we want to process the json data into Java models.
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    dialog.dismiss();
                    Log.e("responseString", responseString);
                    Log.e("ERROR", throwable.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    /*public static void Done_Login(Context ctx,String type,String id,JSONObject json,SharedPreferences sharedPreferences) {
        try {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString(Constants.Username, json.getString("name"));
            edit.putString(Constants.EmailId, json.getString("email"));
            edit.putString(Constants.ProfilePic, json.getString("profile_pic"));
            edit.putString(Constants.Type_of_login, type);
            edit.putString(Constants.Id, id);
            edit.commit();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        ((Activity) ctx).startActivity(new Intent(ctx, MainActivity.class));
        ((Activity) ctx).finish();
    }*/


    /*public static Typeface Montserrat_Regular(AssetManager mgr)
    {
        return Typeface.createFromAsset(mgr, "fonts/Montserrat-Regular.ttf");

    }

    public static Typeface Montserrat_Bold(AssetManager mgr)
    {
        return Typeface.createFromAsset(mgr, "fonts/Montserrat-Bold.ttf");

    }*/

    /**
     * Returns a string that describes the number of days between dateOne and
     * dateTwo.
     */

    public static String getDateDiffString(Date dateOne, Date dateTwo) {
        long timeOne = dateOne.getTime();
        long timeTwo = dateTwo.getTime();
        long diff = timeTwo - timeOne;

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffDays > 0) {
            int year = (int) (diffDays / 365);
            int rest = (int) (diffDays % 365);
            int month = rest / 30;
            rest = rest % 30;
            int weeks = rest / 7;
            return "Active " + diffDays + "d ago";
        } else {
            diffDays *= -1;
            return "Active " + diffHours + "h " + diffMinutes + "m ago";
        }
    }

    public static String getRealPathFromURI(Context ctx, Uri contentURI) {
        String result;
        Cursor cursor = ctx.getContentResolver().query(contentURI, null, null,
                null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static final String getBase64String(Bitmap photoBitmap) {
        String photo;
        if (photoBitmap != null) {

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, bao);
            // photoBitmap.recycle();
            photo = android.util.Base64.encodeToString(bao.toByteArray(),
                    android.util.Base64.DEFAULT);
            try {
                bao.close();
                bao = null;
                photoBitmap = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            photo = "";
        }
        return photo;
    }

//    public static void setProfilePick(Context ctx, final ImageView userProfilImage, SharedPreferences pref) {
//        final int width_height[] = Ultilities
//                .getImageHeightAndWidthForDrawer(((Activity) ctx));
//        Picasso.with(ctx).load(pref.getString(Constants.ProfilePic, "/"))
//                .transform(new CircleTransform()).fit()
//                .into(userProfilImage);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width_height[1], width_height[0]);
//        userProfilImage.setLayoutParams(layoutParams);
//    }

    public static Bitmap resizeBitmap(String path) {
        Bitmap photoBitmap;

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bm = BitmapFactory.decodeFile(path, bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) 600);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) 800);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;

        photoBitmap = BitmapFactory.decodeFile(path, bmpFactoryOptions);
        int outWidth = photoBitmap.getWidth();
        int outHeight = photoBitmap.getHeight();

        try {
            ExifInterface exif = new ExifInterface(path);
            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            Log.e("orientString", orientString);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) outWidth, (float) outHeight);
            photoBitmap = Bitmap.createBitmap(photoBitmap, 0, 0, outWidth, outHeight, matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return photoBitmap;
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

//        bitmap.recycle();

        return output;
    }


    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }


}