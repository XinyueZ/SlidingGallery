/*
 * Copyright (C) 2011 The Android Open Source Project
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

package de.cellular.lib.lightlib.backend;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.util.Log;

public class LLImageCache {

	public static final String DEBUG_TAG="ImageCache";
	public static final int MAX_CACHE_SLOTS=40;
	
	private static Map<String, Bitmap> sendungenCache = new ConcurrentHashMap<String, Bitmap>();
	
	public static void setSendungImage(String key, Bitmap image) {
		if(key != null && image != null){
			if (sendungenCache.size()+1>MAX_CACHE_SLOTS) {				
				@SuppressWarnings("rawtypes")
                Iterator  keyIter=sendungenCache.keySet().iterator();
				
				while (keyIter.hasNext() && sendungenCache.size()+1>MAX_CACHE_SLOTS) {
					Log.d(DEBUG_TAG,"Freeing Cache slot");
					sendungenCache.remove(keyIter.next());
				}
			}
		
			sendungenCache.put(key, image);
		}
	}
	
	public static Bitmap getSendungenImage(String key) {
		return sendungenCache.get(key);
	}
	
	public static void clear() {
		Log.d(DEBUG_TAG,"Clearing Image Cache");
		sendungenCache.clear();
	}
	
	public static boolean isEmpty() {
		return sendungenCache.isEmpty();
	}
}
