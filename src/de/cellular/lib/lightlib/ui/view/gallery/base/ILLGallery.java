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
package de.cellular.lib.lightlib.ui.view.gallery.base;

import java.util.List;

import android.graphics.Bitmap;
import android.view.View;
import de.cellular.lib.lightlib.ui.view.gallery.LLGallery;
import de.cellular.lib.lightlib.ui.view.gallery.LLGallery.CommentPosition;

/**
 * Provides generic interface for a LLGallery.
 * 
 * @version 1.0
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 * 
 */
public interface ILLGallery {

    /**
     * Set item source.
     * <p>
     * <li>Not all source will be shown. Because some items haven't been loaded yet(Internet, resource...etc).
     * 
     * @since 1.0
     * @param _bitmaps
     *            the data source
     * 
     */
    void setImages( List<Bitmap> _bitmaps );

    /**
     * Set item source.
     * <p>
     * <li>Not all data source will be shown. Because some items haven't been loaded yet(Internet, resource...etc).
     * 
     * @since 1.0
     * @param _bitmaps
     *            the data source
     * @param _maxWidth
     *            the max-width that each item can be shown.
     * 
     */
    void setImages( List<Bitmap> _bitmaps, int _maxWidth );

    /**
     * Append an item
     * 
     * @since 1.0
     * @param _bmp
     *            the data source
     * @param _comment
     *            the comment text
     * 
     */
    void appendImage( Bitmap _bmp );

    /**
     * Append an item. Like {@link #setImages(List, int)} the {@link LLGallery} shows the item with associated _maxWidth.
     * 
     * @since 1.0
     * @param _bmp
     *            the data source
     * @param _maxWidth
     *            the max width
     * @param _comment
     *            the comment text
     * 
     */
    void appendImage( Bitmap _bmp, int _maxWidth, String _comment );

    /**
     * Append new comment
     * 
     * @since 1.0
     * @param _comment
     *            the comment text
     * 
     */
    void appendComment( String _comment );

    /**
     * Set item source.
     * <p>
     * <li>Not all source will be shown. Because some items haven't been loaded yet(Internet, resource...etc).
     * 
     * @deprecated use {@link #setImages(List, int)} instead of it.
     * @since 1.0
     * @param _bitmaps
     *            the data source
     * @param _maxWidth
     *            the max-width that each item can be shown.
     * 
     */
    void setImagesByWidth( List<Bitmap> _bitmaps, int _maxWidth );

    /**
     * Add an array of comments to describe each item.
     * <p>
     * <li>If the per-loaded items are null, the correspond comment will be null.
     * 
     * @see {@link #setImages(List)}
     * @see {@link #setImages(List, int)}
     * @since 1.0
     * @param _commentViewId
     *            the ID of {@link View} places comments
     * @param _comments
     *            the array of comments
     * 
     */
    void addComments( int _commentViewId, String[] _comments );

    /**
     * Add an array of comments to describe each item.
     * <p>
     * <li>If the per-loaded items are null, the correspond comment will be null.
     * 
     * @see {@link #setImages(List)}
     * @see {@link #setImages(List, int)}
     * @since 1.0
     * @param _commentViewId
     *            the ID of {@link View} places comments
     * @param _pos
     *            where is the comment {@link CommentPosition}
     * @param _comments
     *            the array of comments
     * 
     */
    void addComments( int _commentViewId, CommentPosition _pos, String[] _comments );

    /**
     * Set listener for click event.
     * 
     * @since 1.0
     * @param _listener
     * 
     */
    void setOnItemClickListener( LLGallery.OnItemClickListener _listener );

    /**
     * Set listener for scroll event.
     * 
     * @since 1.0
     * @param _listener
     * 
     */
    void setOnItemScrollListener( LLGallery.OnItemScrollListener _listener );

    /**
     * Set listener for scrolled event.
     * 
     * @since 1.0
     * @param _listener
     * 
     */
    void setOnItemScrolledListener( LLGallery.OnItemScrolledListener _listener );
}
