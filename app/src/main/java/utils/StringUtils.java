package utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class StringUtils {

	public static String replaceWords(String phoneNumber) {

		String added_phoneNo = phoneNumber.replace(" ", "").replace("+", "")
				.replace("-", "").replace("(", "").replace(")", "");
		// if(added_phoneNo.length() > 10) {
		// added_phoneNo = added_phoneNo.substring(added_phoneNo.length() - 10);
		//
		// }
		return added_phoneNo;

	}
	
	/**
	 * Email verification
	 * @param paramString
	 * @return
	 */

	public static boolean verify(String paramString) {
		return paramString
				.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$");
	}

	public static boolean isValidPassword(String pass) {

		if (!pass.matches(".*[A-Z].*")) return false;

		if (!pass.matches(".*[a-z].*")) return false;

		if (!pass.matches(".*\\d.*")) return false;

		if (!pass.matches(".*[~!.......].*")) return false;

		return true;
	}
	
	/**
	 * Date Format conversion
	 * @param dateToConvert
	 * @return
	 */

	public static String DateConverter(String dateToConvert) {

		String dateConvert = dateToConvert;
		String DateConverted = "";

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		Date myDate = new Date();
		try {
			myDate = dateFormat.parse(dateConvert);
			Log.e("***** myDate *****", "" + myDate);
			SimpleDateFormat formatter = new SimpleDateFormat("MM / dd / yyyy");
			 DateConverted = formatter.format(myDate);
			Log.e("*conveted date *", "" + DateConverted);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return DateConverted;

	}

	public static boolean isAlphaNumeric(String s) {
		String pattern= "^[a-zA-Z0-9]*$";
		if(s.matches(pattern)) {
			return true;
		}
		return false;
	}

    public static void showDialog(String msg, Context context) {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(
                    context).create();


            // Setting Dialog Message
            alertDialog.setMessage(msg);

            // Setting Icon to Dialog
            //	alertDialog.setIcon(R.drawable.browse);

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

	// Convert date String from one format to another

	public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate){

		Date parsed = null;
		String outputDate = "";

		SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
		SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

		try {
			parsed = df_input.parse(inputDate);
			outputDate = df_output.format(parsed);

		} catch (ParseException e) {
			Log.e("TAG Date", "ParseException - dateFormat");
		}

		return outputDate;

	}

	// split date and time comging from mySql Db
	public static void SeparateDateTime(String dateString){
		//String date_string = "yyyy-mm-dd hh:mm:ss";
		String date_string = dateString;
		StringTokenizer tk = new StringTokenizer(date_string);

		String date = tk.nextToken();  // <---  yyyy-mm-dd
		String time = tk.nextToken();  // <---  hh:mm:ss

		Constants.TIME = time;

		String inputFormat ="yyyy-mm-dd" ;
		String outputFormat = "dd MMM yyyy";
		String inputDate = date;
		Constants.DATE_TO_SEND = inputDate;
		Constants.DATE = formateDateFromstring(inputFormat,outputFormat,inputDate);
	}

	public static String SeparateDate(String dateString){
		//String date_string = "yyyy-mm-dd hh:mm:ss";
		String date_string = dateString;
		StringTokenizer tk = new StringTokenizer(date_string);

		String date = tk.nextToken();  // <---  yyyy-mm-dd
		String time = tk.nextToken();  // <---  hh:mm:ss


		String inputFormat ="yyyy-mm-dd" ;
		String outputFormat = "dd MMM yyyy";
		String inputDate = date;
		String date_String = formateDateFromstring(inputFormat,outputFormat,inputDate);

		return date_String+" "+time;
	}



	public static String getString(EditText et){
		return et.getText().toString().trim();
	}

	/**
	 * convert bitmap to base64
	 * @param photoBitmap
	 * @return
	 */

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

}
