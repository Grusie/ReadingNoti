package com.grusie.readingnoti.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import com.grusie.domain.data.tts.AlternativeData
import com.grusie.domain.data.tts.TTS_STATE
import com.grusie.readingnoti.R

class TTSUtil {
    companion object {
        fun speakContent(tts:TextToSpeech, notiTypeId: Int, content:String) {
            if(content.isEmpty()) return

            // 들어 온 문장을 대체어 처리
            val ttsContent = getAlterContent(content)

            tts.speak(ttsContent, TextToSpeech.QUEUE_FLUSH, null, "$notiTypeId")
        }

        /**
         * 들어온 문장을 대체어가 있을 경우 처리하여 리턴
         */
        private fun getAlterContent(content: String): String {
            var result = content
            val alternativeList = getAlternativeList()

            if (content.isEmpty() || alternativeList.isEmpty()) return content

            alternativeList.forEach {
                result = if (!it.isRepeatAlter) result.replace(it.originContent, it.alternativeContent)
                else {
                    // 반복된 것을 한 번에 처리하려고 한다면 정규식 사용
                    result.replace(Regex("${Regex.escape(it.originContent)}{2,}"), it.alternativeContent)
                }
            }
            return result
        }

        /**
         * 대체어 목록을 불러오는 함수
         */
        private fun getAlternativeList(): List<AlternativeData> {
            // TODO: 서버통신을 통해 대체어 목록을 불러올 수 있도록 수정 필요
            val baseAlternativeList = listOf(
                AlternativeData(
                    id = 0,
                    isRepeatAlter = true,
                    originContent = "ㅋㅋ",
                    "키킥"
                ),
            )
            return listOf(
                AlternativeData(
                    id = 0,
                    isRepeatAlter = true,
                    originContent = "ㅋㅋ",
                    "키킥"
                ),
                AlternativeData(
                    id = 0,
                    isRepeatAlter = false,
                    originContent = "아니",
                    "그래"
                )
            )
        }
    }
}

fun TTS_STATE.getNotiMsgByState(context: Context, ttsState: TTS_STATE = TTS_STATE.NONE): String {
    return when(ttsState) {
        TTS_STATE.NONE -> {
            context.getString(R.string.foreground_noti_content_none)
        }

        TTS_STATE.SPEAKING -> {
            context.getString(R.string.foreground_noti_content_speaking)
        }

        TTS_STATE.ERROR -> {
            context.getString(R.string.foreground_noti_content_error)
        }

        else -> {
            context.getString(R.string.foreground_noti_content_none)
        }
    }
}