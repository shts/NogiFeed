package shts.jp.android.nogifeed.api

import android.content.Context
import android.text.TextUtils
import org.jsoup.Jsoup
import rx.Observable
import shts.jp.android.nogifeed.entities.News
import shts.jp.android.nogifeed.entities.NewsList
import java.io.IOException

class AsyncNewsClient {

    companion object {

        private val URL_NEWS = "http://www.nogizaka46.com/smph/news/"

        fun get(context: Context): Observable<NewsList> {
            return Observable.create { subscriber ->
                try {
                    val newsList = getNewsFeed(URL_NEWS)
                    newsList.filter(context)
                    newsList.sort()
                    subscriber.onNext(newsList)
                    subscriber.onCompleted()
                } catch (e: Throwable) {
                    subscriber.onError(e)
                }
            }
        }

        @Throws(IOException::class)
        private fun getNewsFeed(url: String): NewsList {
            val newsList = NewsList()

            val document = Jsoup.connect(url).get()
            val body = document.body()
            val dl = body.getElementsByTag("dl")[0]

            val length = dl.getElementsByTag("dt").size
            for (i in 0 until length) {
                var iconTypeText = dl.getElementsByTag("dt")[i].className()
                if (TextUtils.isEmpty(iconTypeText)) {
                    iconTypeText = "icon4"
                }
                val date = dl.getElementsByTag("dt")[i].text()
                val title = dl.getElementsByTag("dd")[i].text()
                val newsUrl = dl.getElementsByTag("a")[i].attr("href")
                newsList.add(News(date, iconTypeText, newsUrl, title))
            }
            return newsList
        }
    }
}
