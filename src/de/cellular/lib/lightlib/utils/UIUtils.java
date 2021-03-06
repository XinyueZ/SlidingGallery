/**
 * Copyright (C) 2012 Cellular GmbH 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cellular.lib.lightlib.utils;

import java.io.File;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import de.cellular.lib.lightlib.log.LL;

/**
 * Define UI utils here. Some dialog, popup, progress dialog etc...
 * 
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 * 
 * 
 */
public class UIUtils
{

    public static ImageGetter sImageGetter_150x100 = new Html.ImageGetter()
                                                   {
                                                       @Override
                                                       public Drawable getDrawable( String _source )
                                                       {
                                                           Drawable drawable = null;
                                                           URL url = null;

                                                           try
                                                           {
                                                               url = new URL( _source );
                                                               drawable = Drawable.createFromStream(
                                                                       url.openStream(),
                                                                       "" );
                                                           }
                                                           catch( Exception _e )
                                                           {
                                                               LL.e( _e.toString() );
                                                           }

                                                           // Important
                                                           drawable.setBounds( 0, 0, 150, 100 );

                                                           return drawable;
                                                       }
                                                   };

    public static ImageGetter sImageGetter_90x80   = new Html.ImageGetter()
                                                   {
                                                       @Override
                                                       public Drawable getDrawable( String _source )
                                                       {
                                                           Drawable drawable = null;
                                                           URL url = null;

                                                           try
                                                           {
                                                               url = new URL( _source );
                                                               drawable = Drawable.createFromStream(
                                                                       url.openStream(),
                                                                       "" );
                                                           }
                                                           catch( Exception _e )
                                                           {
                                                               LL.e( _e.toString() );
                                                           }

                                                           // Important
                                                           drawable.setBounds( 0, 0, 90, 80 );

                                                           return drawable;
                                                       }
                                                   };

    public static void showSoftKeyboard( Context context, EditText editText ) {
        ((InputMethodManager) context.getSystemService( Context.INPUT_METHOD_SERVICE ))
                .showSoftInput( editText, InputMethodManager.SHOW_IMPLICIT );
    }

    public static void hideSoftKeyboard( Context context, EditText editText ) {
        ((InputMethodManager) context.getSystemService( Context.INPUT_METHOD_SERVICE ))
                .hideSoftInputFromWindow( editText.getWindowToken(), 0 );
    }

    /**
     * Adds the blocking progress indicator.
     * 
     * @param _context
     *            the _context
     * @param _resId
     *            the _res id
     * @return the view
     */
    @SuppressWarnings("deprecation")
    public static View addBlockingProgressIndicator( Activity _context, int _resId )
    {
        View progressView = LayoutInflater.from( _context ).inflate( _resId, null );
        _context.addContentView( progressView, new LayoutParams( LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT ) );
        progressView.setVisibility( View.GONE );
        return progressView;
    }

    // public static View addBlockingProgress( Activity _context )
    // {
    // return addBlockingProgressIndicator( _context, R.layout.activity_progress );
    // }

    /**
     * Creates the progress dialog.
     * 
     * @param _activity
     *            the _activity
     * @param _message
     *            the _message
     * @param _cancelable
     *            the _cancelable
     * @return the progress dialog
     */
    public static ProgressDialog createProgressDialog( Context _activity, int _message, boolean _cancelable )
    {
        return ProgressDialog.show( _activity, "", _activity.getString( _message ), false, _cancelable );
    }

    /**
     * Creates the progress dialog.
     * 
     * @param _activity
     *            the _activity
     * @param _message
     *            the _message
     * @param _cancelable
     *            the _cancelable
     * @return the progress dialog
     */
    public static ProgressDialog createProgressDialog( Context _activity, String _message, boolean _cancelable )
    {
        return ProgressDialog.show( _activity, "", _message, false, _cancelable );
    }

    /**
     * Creates the alert.
     * 
     * @param _context
     *            the _context
     * @param _messageId
     *            the _message id
     * @param _buttonTextId
     *            the _button text id
     * @param _onOkClickedListener
     *            the _on ok clicked listener
     * @return the alert dialog
     */
    public static AlertDialog createAlert( Context _context, int _messageId, int _buttonTextId,
            DialogInterface.OnClickListener _onOkClickedListener )
    {
        return new AlertDialog.Builder( _context ).setMessage( _messageId )
                .setCancelable( false )
                .setNeutralButton( _buttonTextId, _onOkClickedListener )
                .create();
    }

    /**
     * Creates the alert.
     * 
     * @param _context
     *            the _context
     * @param _messageId
     *            the _message id
     * @param _button1TextId
     *            the _button1 text id
     * @param _button2TextId
     *            the _button2 text id
     * @param _onButton1ClickedListener
     *            the _on button1 clicked listener
     * @param _onButton2ClickedListener
     *            the _on button2 clicked listener
     * @return the alert dialog
     */
    public static AlertDialog createAlert( Context _context,
            int _messageId,
            int _button1TextId,
            int _button2TextId,
            DialogInterface.OnClickListener _onButton1ClickedListener,
            DialogInterface.OnClickListener _onButton2ClickedListener )
    {
        return new AlertDialog.Builder( _context ).setMessage( _messageId )
                .setCancelable( false )
                .setNeutralButton( _button1TextId, _onButton1ClickedListener )
                .setNegativeButton( _button2TextId, _onButton2ClickedListener )
                .create();
    }

    /**
     * Creates the alert.
     * 
     * @param _context
     *            the _context
     * @param _messageId
     *            the _message id
     * @param _button1TextId
     *            the _button1 text id
     * @param _button2TextId
     *            the _button2 text id
     * @param _button3TextId
     *            the _button3 text id
     * @param _onButton1ClickedListener
     *            the _on button1 clicked listener
     * @param _onButton2ClickedListener
     *            the _on button2 clicked listener
     * @param _onButton3ClickedListener
     *            the _on button3 clicked listener
     * @return the alert dialog
     */
    public static AlertDialog createAlert( Context _context,
            int _messageId,
            int _button1TextId,
            int _button2TextId,
            int _button3TextId,
            DialogInterface.OnClickListener _onButton1ClickedListener,
            DialogInterface.OnClickListener _onButton2ClickedListener,
            DialogInterface.OnClickListener _onButton3ClickedListener )
    {
        return new AlertDialog.Builder( _context ).setMessage( _messageId )
                .setCancelable( false )
                .setPositiveButton( _button1TextId, _onButton1ClickedListener )
                .setNeutralButton( _button2TextId, _onButton2ClickedListener )
                .setNegativeButton( _button3TextId, _onButton3ClickedListener )
                .create();
    }

    /**
     * Creates the alert.
     * 
     * @param _context
     *            the _context
     * @param _message
     *            the _message
     * @param _button1TextId
     *            the _button1 text id
     * @param _button2TextId
     *            the _button2 text id
     * @param _onButton1ClickedListener
     *            the _on button1 clicked listener
     * @param _onButton2ClickedListener
     *            the _on button2 clicked listener
     * @return the alert dialog
     */
    public static AlertDialog createAlert( Context _context,
            String _message,
            int _button1TextId,
            int _button2TextId, DialogInterface.OnClickListener _onButton1ClickedListener,
            DialogInterface.OnClickListener _onButton2ClickedListener )
    {
        return new AlertDialog.Builder( _context ).setMessage( _message )
                .setCancelable( true )
                .setNeutralButton( _button1TextId, _onButton1ClickedListener )
                .setNegativeButton( _button2TextId, _onButton2ClickedListener )
                .create();
    }

    /**
     * Creates the alert.
     * 
     * @param _context
     *            the _context
     * @param _message
     *            the _message
     * @param _btn1
     *            the _btn1
     * @param _onClickListener
     *            the _on click listener
     * @return the dialog
     */
    public static Dialog createAlert( Context _context, String _message, String _btn1,
            DialogInterface.OnClickListener _onClickListener )
    {
        return new AlertDialog.Builder( _context ).setMessage( _message )
                .setCancelable( true )
                .setNeutralButton( _btn1, _onClickListener )
                .create();
    }

    /**
     * Creates the alert.
     * 
     * @param _context
     *            the _context
     * @param _message
     *            the _message
     * @param _btn1
     *            the _btn1
     * @param _btn2
     *            the _btn2
     * @param _onButton1ClickedListener
     *            the _on button1 clicked listener
     * @param _onButton2ClickedListener
     *            the _on button2 clicked listener
     * @return the dialog
     */
    public static Dialog createAlert( Context _context, String _message, String _btn1, String _btn2,
            DialogInterface.OnClickListener _onButton1ClickedListener,
            DialogInterface.OnClickListener _onButton2ClickedListener )
    {
        return new AlertDialog.Builder( _context ).setMessage( _message )
                .setCancelable( true )
                .setNeutralButton( _btn1, _onButton1ClickedListener )
                .setNegativeButton( _btn2, _onButton2ClickedListener )
                .create();
    }

    /**
     * Creates the alert.
     * 
     * @param _context
     *            the _context
     * @param _messageId
     *            the _message id
     * @param _buttonTextId
     *            the _button text id
     * @return the alert dialog
     */
    public static AlertDialog createAlert( Context _context, int _messageId, int _buttonTextId )
    {
        return new AlertDialog.Builder( _context ).setMessage( _messageId )
                .setCancelable( true )
                .setNeutralButton( _buttonTextId, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick( DialogInterface _dialog, int _which )
                    {

                    }
                } )
                .create();
    }

    /**
     * Creates the alter no cancel.
     * 
     * @param _context
     *            the _context
     * @param _messageId
     *            the _message id
     * @param _buttonTextId
     *            the _button text id
     * @param _listener
     *            the _listener
     * @return the alert dialog
     */
    public static AlertDialog createAlterNoCancel( Context _context, int _messageId, int _buttonTextId
            , OnClickListener _listener )
    {
        return new AlertDialog.Builder( _context ).setMessage( _messageId )
                .setCancelable( false )
                .setNeutralButton( _buttonTextId, _listener )
                .create();
    }

    /**
     * Creates the alert.
     * 
     * @param _context
     *            the _context
     * @param _message
     *            the _message
     * @param _buttonTextId
     *            the _button text id
     * @return the alert dialog
     */
    public static AlertDialog createAlert( Context _context, String _message, int _buttonTextId )
    {
        return new AlertDialog.Builder( _context ).setMessage( _message )
                .setCancelable( true )
                .setNeutralButton( _buttonTextId, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick( DialogInterface _dialog, int _which )
                    {

                    }
                } )
                .create();
    }

    /**
     * Show a long time toast with a text id.
     * 
     * @param _activity
     * @param _messageId
     */
    public static void showLongToast( Context _context, int _messageId )
    {
        Toast.makeText( _context, _context.getString( _messageId ), Toast.LENGTH_LONG ).show();
    }

    /**
     * Show a short time toast with a text id.
     * 
     * @param _activity
     * @param _messageId
     */
    public static void showShortToast( Context _context, int _messageId )
    {
        Toast.makeText( _context, _context.getString( _messageId ), Toast.LENGTH_SHORT ).show();
    }

    /**
     * Show a long time toast.
     * 
     * @param _context
     * @param _message
     */
    public static void showLongToast( Context _context, String _message )
    {
        Toast.makeText( _context, _message, Toast.LENGTH_LONG ).show();
    }

    /**
     * Show a short time toast.
     * 
     * @param _context
     * @param _message
     */
    public static void showShortToast( Context _context, String _message )
    {
        Toast.makeText( _context, _message, Toast.LENGTH_SHORT ).show();
    }

    /**
     * Create a divider
     * 
     * @return
     */
    public static View createDivider( Context _context )
    {
        View v = new View( _context );
        @SuppressWarnings("deprecation")
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, 1 );
        v.setBackgroundColor( _context.getResources().getColor( android.R.color.darker_gray ) );
        v.setLayoutParams( params );
        return v;
    }

    /**
     * DP to PX
     */
    public static int dip2px( Context _context, float _dpValue )
    {
        final float scale = _context.getResources().getDisplayMetrics().density;
        return (int) (_dpValue * scale + 0.5f);
    }

    /**
     * PX to DP
     */
    public static int px2dip( Context _context, float _pxValue )
    {
        final float scale = _context.getResources().getDisplayMetrics().density;
        return (int) (_pxValue / scale + 0.5f);
    }

    /**
     * Scale bitmap
     * 
     * @param _bitmap
     *            Image has width > height.
     * @param _newLayoutWidth
     * @return
     */
    public static Bitmap scaleImageWH( Bitmap _bitmap, int _newLayoutWidth ) throws IllegalArgumentException
    {
        Bitmap bitmap = _bitmap;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int nWidth = 0;
        int nHeight = 0;
        if( width > height )
        {
            double factor = (width * 1.0) / (height * 1.0);
            nWidth = _newLayoutWidth;
            nHeight = (int) ((nWidth * 1.0) / factor);
        }

        float scaleWidth = ((float) nWidth) / width;
        float scaleHeight = ((float) nHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale( scaleWidth, scaleHeight );

        Bitmap resizedBitmap = Bitmap.createBitmap( bitmap, 0, 0, width, height, matrix, true );
        return resizedBitmap;
    }

    /**
     * Scale bitmap
     * 
     * @param _bitmap
     *            Image has width < height.
     * @param _newLayoutWidth
     * @return
     */
    public static Bitmap scaleImageHW( Bitmap _bitmap, int _newLayoutWidth )
    {
        Bitmap bitmap = _bitmap;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int nWidth = 0;
        int nHeight = 0;
        if( height > width )
        {
            double factor = (height * 1.0) / (width * 1.0);
            nWidth = _newLayoutWidth;
            nHeight = (int) ((nWidth * 1.0) * factor);
        }

        float scaleWidth = ((float) nWidth) / width;
        float scaleHeight = ((float) nHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale( scaleWidth, scaleHeight );

        Bitmap resizedBitmap = Bitmap.createBitmap( bitmap, 0, 0, width, height, matrix, true );
        return resizedBitmap;
    }

    public static void enableViews( ViewGroup _root, boolean _enabled ) {
        for( int i = 0, cnt = _root.getChildCount(); i < cnt; i++ ) {
            View child = _root.getChildAt( i );
            child.setEnabled( _enabled );
            if( child instanceof ViewGroup ) {
                enableViews( (ViewGroup) child, _enabled );
            }
        }
    }

    public static void removeViews( ViewGroup _root ) {
        for( int i = 0, cnt = _root.getChildCount(); i < cnt; i++ ) {
            View child = _root.getChildAt( i );
            if( child instanceof ViewGroup ) {
                removeViews( (ViewGroup) child );
            }
            _root.removeView( child );
        }
    }

    public static void openSMS( Context _context, String _telNum, String _text ) {
        if( _context != null ) {
            String phoneNumber = _telNum;
            Intent intent = new Intent( Intent.ACTION_SENDTO, Uri.parse( new StringBuilder().append( "smsto:" ).append( phoneNumber ).toString() ) );
            intent.putExtra( "sms_body", _text );
            intent.putExtra( "compose_mode", true );
            _context.startActivity( intent );
        }
    }

    public static void openEmail( Context _context, String _to ) {
        if( _context != null ) {
            Intent i = new Intent( Intent.ACTION_SEND );
            i.putExtra( android.content.Intent.EXTRA_EMAIL, new String[] { _to } );
            i.setType( "text/plain" );
            _context.startActivity( i );
        }
    }

    public static void openTel( Context _context, String _to ) {
        if( _context != null ) {
            Intent intent = new Intent( Intent.ACTION_DIAL );
            intent.setData( Uri.parse( new StringBuilder().append( "tel:" ).append( _to ).toString() ) );
            _context.startActivity( intent );
        }
    }

    public static void openUrl( Context _context, String _to ) {
        if( _context != null ) {
            Intent i = new Intent( Intent.ACTION_VIEW );
            i.setData( Uri.parse( _to ) );
            _context.startActivity( i );
        }
    } 
    
    public static void opePdf( Context _context, String _to ) {
        if( _context != null ) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File( _to  );
            intent.setDataAndType( Uri.fromFile( file ), "application/pdf" );
            _context.startActivity(intent);
        }
    }   
}
