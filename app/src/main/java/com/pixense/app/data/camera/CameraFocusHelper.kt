package com.pixense.app.data.camera

import android.util.Log
import android.view.MotionEvent
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.FocusMeteringResult
import androidx.camera.core.MeteringPoint
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import java.util.concurrent.TimeUnit

private const val TAG = "CameraFocusHelper"

object CameraFocusHelper {

    /**
     * Executes tap-to-focus and tap-to-expose using a MotionEvent from an Android View / PreviewView.
     *
     * @param previewView The PreviewView displaying the live viewfinder.
     * @param camera The active Camera instance bound to lifecycle.
     * @param event The MotionEvent (typically MotionEvent.ACTION_UP).
     * @param autoCancelSeconds Duration before focus lock auto-cancels and reverts to continuous AF/AE. Defaults to 3.
     * @param onFocusResult Callback invoked with true if focus locked sharply, false if focus timed out or failed.
     */
    fun tapToFocusAndExpose(
        previewView: PreviewView,
        camera: Camera,
        event: MotionEvent,
        autoCancelSeconds: Long = 3L,
        onFocusResult: ((isSuccess: Boolean) -> Unit)? = null
    ) {
        tapToFocusAndExpose(
            previewView = previewView,
            camera = camera,
            x = event.x,
            y = event.y,
            autoCancelSeconds = autoCancelSeconds,
            onFocusResult = onFocusResult
        )
    }

    /**
     * Executes tap-to-focus and tap-to-expose using coordinate offsets (ideal for Jetpack Compose).
     *
     * @param previewView The PreviewView displaying the live viewfinder.
     * @param camera The active Camera instance bound to lifecycle.
     * @param x Touch X coordinate in PreviewView local pixel space.
     * @param y Touch Y coordinate in PreviewView local pixel space.
     * @param autoCancelSeconds Duration before focus lock auto-cancels and reverts to continuous AF/AE. Defaults to 3.
     * @param onFocusResult Callback invoked with true if focus locked sharply, false if focus timed out or failed.
     */
    fun tapToFocusAndExpose(
        previewView: PreviewView,
        camera: Camera,
        x: Float,
        y: Float,
        autoCancelSeconds: Long = 3L,
        onFocusResult: ((isSuccess: Boolean) -> Unit)? = null
    ) {
        // 1. Coordinate Conversion:
        // PreviewView.meteringPointFactory takes coordinates from View space (pixels relative to PreviewView)
        // and maps them into normalized sensor coordinate space [0.0, 1.0].
        // This automatically handles sensor orientation, front/back lens facing, ScaleType letterboxing, and aspect ratio padding.
        val factory = previewView.meteringPointFactory
        val meteringPoint: MeteringPoint = factory.createPoint(x, y)

        // 2. Build FocusMeteringAction:
        // - FLAG_AF: Sharp autofocus on touched subject.
        // - FLAG_AE: Automatic exposure metering for the touched area.
        // - setAutoCancelDuration: Reverts lock back to continuous AF/AE after specified duration (default 3 seconds).
        val action = FocusMeteringAction.Builder(
            meteringPoint,
            FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE
        )
            .setAutoCancelDuration(autoCancelSeconds, TimeUnit.SECONDS)
            .build()

        // 3. Dispatch to CameraControl
        val focusFuture = camera.cameraControl.startFocusAndMetering(action)

        // 4. Handle ListenableFuture callbacks on main executor
        Futures.addCallback(
            focusFuture,
            object : FutureCallback<FocusMeteringResult> {
                override fun onSuccess(result: FocusMeteringResult?) {
                    val isSuccess = result?.isFocusSuccessful == true
                    if (isSuccess) {
                        Log.d(TAG, "Focus & Exposure locked successfully at ($x, $y)")
                    } else {
                        Log.d(TAG, "Focus completed without lock at ($x, $y) (e.g. low contrast surface)")
                    }
                    onFocusResult?.invoke(isSuccess)
                }

                override fun onFailure(throwable: Throwable) {
                    if (throwable is CameraControl.OperationCanceledException) {
                        // Normal CameraX lifecycle: 3-second auto-cancel expired or a new tap superseded the current one
                        Log.d(TAG, "Focus & Exposure metering ended (auto-cancel or superseded): ${throwable.localizedMessage}")
                    } else {
                        Log.w(TAG, "Focus & Exposure metering failed: ${throwable.localizedMessage}", throwable)
                        onFocusResult?.invoke(false)
                    }
                }
            },
            ContextCompat.getMainExecutor(previewView.context)
        )
    }
}
