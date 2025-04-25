package com.grusie.presentation.data

import android.content.Context
import com.grusie.presentation.R

enum class TTS_STATE {
    NONE,
    SPEAKING,
    ERROR;

    companion object {
        fun getNotiMsgByState(context: Context, ttsState: TTS_STATE = NONE): String {
            return when(ttsState) {
                NONE -> {
                    context.getString(R.string.foreground_noti_content_none)
                }

                SPEAKING -> {
                    context.getString(R.string.foreground_noti_content_speaking)
                }

                ERROR -> {
                    context.getString(R.string.foreground_noti_content_error)
                }

                else -> {
                    context.getString(R.string.foreground_noti_content_none)
                }
            }
        }
    }
}