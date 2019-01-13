/*
 * Copyright 2016 The Android Open Source Project
 * Copyright 2017 Rúben Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ar.bakingapp;

import android.widget.ImageView;

import com.github.rubensousa.previewseekbar.PreviewLoader;


public class ExoPlayerManager implements PreviewLoader {

    private int[] previewThumbnailPaths = {R.drawable.first, R.drawable.second, R.drawable.third, R.drawable.fourth, R.drawable.fifth, R.drawable.sixth, R.drawable.seventh, R.drawable.eighth, R.drawable.nineth, R.drawable.tenth, R.drawable.eleventh, R.drawable.twelfth};

    private ImageView imageView;

    public ExoPlayerManager(ImageView imageView) {
        this.imageView = imageView;
    }

    /**
     *
     * @param currentPosition current playback position in milliseconds
     * @param max
     */
    @Override
    public void loadPreview(long currentPosition, long max) {

        // load preview thumbnails into the ImageView
        imageView.setImageResource(previewThumbnailPaths[(int) (currentPosition/1000)]);
    }
}


