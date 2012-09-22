package de.cellular.lib.lightlib.ui.view;

import android.text.SpannableString;

/**
 * The Class HighLightTextView.
 * 
 * @since 1.0
 * @author Chris.Z
 */
public abstract class HighLightText {
    /** The Header. */
    private String            mHeader;

    /** The Start of a word with mHeader. */
    protected int             mStart;

    /** The End. */
    protected int             mEnd;

    /** The Spannable string. */
    protected SpannableString mSpannableString;

    /**
     * Instantiates a new {@link HighLightText}
     * 
     * @param _spannableString
     *            the _spannable string
     * @param _text
     *            the _text
     * @param _header
     *            the _header
     * @since 1.0
     * @author Chris.Z
     */
    public HighLightText( SpannableString _spannableString, String _header ) {
        super();
        mSpannableString = _spannableString; 
        mHeader = _header;
    }

    /**
     * Loop the given {@link #mText} and highlight the words with {@link #mHeader}.
     * 
     * @since 1.0
     * @author Chris.Z
     */
    public void highlight() {
        char empty = ' ';
        char comal = ',';
        int offset = 0;
        String text = mSpannableString.toString();
        boolean found = false;

        do {
            found = false;
            final int start = text.indexOf( mHeader, offset );
            if( start >= 0 ) {
                found = true;
                offset = text.indexOf( empty, start );
                int p1 = text.indexOf( empty, start ) >= 0 ? text.indexOf( empty, start ) : text.length();
                int p2 = text.indexOf( comal, start ) >= 0 ? text.indexOf( comal, start ) : text.length();
                final int end = Math.min( p1, p2 );
                offset = end;
                if( end >= 0 ) {
                    doHighLight( start, end );
                }
                else {
                    found = false;
                }
            }
        }
        while( found );
    }

    /**
     * Highlight {@link #mText} with a style.
     * 
     * @param _start
     *            the _start
     * @param _end
     *            the _end
     * @since 1.0
     * @author Chris.Z
     */
    protected abstract void doHighLight( int _start, int _end );
}
