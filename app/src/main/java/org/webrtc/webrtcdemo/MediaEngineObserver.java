/*
 *  Copyright (c) 2013 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.webrtc.webrtcdemo;

public interface MediaEngineObserver {
	void newStats(String stats);

	void onStateChanged(int inFps, int outFps, int inKbps, int outKbps, int width, int height, int packetLoss);

}