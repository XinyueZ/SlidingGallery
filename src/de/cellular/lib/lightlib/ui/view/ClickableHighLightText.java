package de.cellular.lib.lightlib.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * The Class ClickableHighLightText. 
 * @since 2.0
 * @author Chris.Z
 */
public class ClickableHighLightText extends HighLightText {

    /** The Context. */
    private Context                   mContext;

    /** The Browser type. */
    private Class<? extends Activity> mBrowserType;

    /**
     * Loop the given {@link #mText} and highlight the words with {@link #mHeader}.
     * 
     * @param the _textView that holds the spannable string
     * @param the _text 
     * @since 2.0
     * @author Chris.Z
     */
    public void highlight( TextView _textView, SpannableString _text ) {
        super.highlight();
        MovementMethod m = _textView.getMovementMethod();
        if( (m == null) || !(m instanceof LinkMovementMethod) ) {
            if( _textView.getLinksClickable() ) {
                _textView.setMovementMethod( LinkMovementMethod.getInstance() );
            }
        }
        if( !TextUtils.isEmpty( _text ) ) {
            _textView.setText( _text );
        }
        _textView.setFocusable( true );
    }

    /**
     * The listener interface for receiving onLinkParsed events.
     * The class that is interested in processing a onLinkParsed
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addOnLinkParsedListener<code> method. When
     * the onLinkParsed event occurs, that object's appropriate
     * method is invoked.
     *
     * @see OnLinkParsedEvent
     * @since 2.0
     */
    public interface OnLinkParsedListener {
        void onLinkParsed( String _urlStr );
    }

    private OnLinkParsedListener mOnLinkParsedListener;

    /**
     * Instantiates a new {@link ClickableHighLightText}
     * 
     * @param _context
     *            the _context
     * @param _browserActivity
     *            the _browser activity contains webview( that is called after a link has been parsed. )
     * @param _spannableString
     *            the _spannable string
     * @param _header
     *            the _header
     * @since 1.0
     * @author Chris.Z
     */
    public ClickableHighLightText(
            Context _context,
            Class<? extends Activity> _browserActivity,
            SpannableString _spannableString,
            String _header ) {
        super( _spannableString, _header );
        mContext = _context;
        mBrowserType = _browserActivity;
    }

    /**
     * Instantiates a new {@link ClickableHighLightText}
     * 
     * @param _context
     *            the _context
     * @param _browserActivity
     *            the OnLinkParsedListener that is called after a link has been parsed.
     * @param _spannableString
     *            the _spannable string
     * @param _header
     *            the _header
     * @since 1.0
     * @author Chris.Z
     */
    public ClickableHighLightText(
            Context _context,
            OnLinkParsedListener _listener,
            SpannableString _spannableString,
            String _header ) {
        super( _spannableString, _header );
        mContext = _context;
        mOnLinkParsedListener = _listener;
    }

    /**
     * Listener for this span. This highlight can be clicked.
     */
    private class ClickableSpanListener extends ClickableSpan {

        /** The Listener. */
        OnClickListener mListener;

        /**
         * Instantiates a new internal url span.
         * 
         * @param listener
         *            the listener
         * @since 1.0
         * @author Chris.Z
         */
        public ClickableSpanListener( OnClickListener listener ) {
            mListener = listener;
        }

        @Override
        public void onClick( View widget ) {
            mListener.onClick( widget );
        }
    }

    @Override
    protected void doHighLight( final int _start, final int _end ) {
        mSpannableString.setSpan( new ClickableSpanListener( new OnClickListener() {
            @Override
            public void onClick( View v ) {
                if( v instanceof TextView ) {
                    String text = ((TextView) v).getText().toString();
                    if( mBrowserType != null ) {
                        navigateInBrowser( text.substring( _start, _end ) );
                    }
                    if( mOnLinkParsedListener != null ) {
                        mOnLinkParsedListener.onLinkParsed( text.substring( _start, _end ) );
                    }
                }
            }
        } ), _start, _end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
    }

    /**
     * Navigate in browser.
     * 
     * @param url
     *            the url
     * @since 1.0
     * @author Chris.Z
     */
    private void navigateInBrowser( String url ) {
        Intent intent = new Intent( mContext, mBrowserType );
        intent.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
        intent.putExtra( "url", url );
        mContext.startActivity( intent );
    }
}
