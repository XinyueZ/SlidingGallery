package de.cellular.lib.lightlib.ui.view;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

/**
 * The Class SimpleHighLightText.
 * 
 * @since 1.0
 * @author Chris.Z
 */
public class SimpleHighLightText extends HighLightText {

    /** The Color. */
    private int mColor;

    /**
     * Instantiates a new {@link SimpleHighLightText}
     * 
     * @param _spannableString
     *            the _spannable string
     * @param _header
     *            the _header
     * @param _color
     *            the _color
     * @since 1.0
     * @author Chris.Z
     */
    public SimpleHighLightText( SpannableString _spannableString, String _header, int _color ) {
        super( _spannableString, _header );
        mColor = _color;
    }

    @Override
    protected void doHighLight( int _start, int _end ) {
        mSpannableString.setSpan( new ForegroundColorSpan( mColor ), _start, _end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
    }
}
