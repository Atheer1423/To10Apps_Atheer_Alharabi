package com.example.to10apps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class FeedEntry {
    var title: String = ""


    override fun toString(): String {
        return """
            name = $title
           """.trimIndent()
    }
}
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    lateinit var rv: RecyclerView
    lateinit var bt: Button
    lateinit var itemsList: ArrayList<String>
    val URL =
        "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate called")

        bt = findViewById(R.id.bClick)
        bt.setOnClickListener {

            requestApi(URL)
            initRecyclerView()

        }
    }

    private fun initRecyclerView() {
        rv = findViewById(R.id.rv1)
        rv.layoutManager = LinearLayoutManager(this)
        rv.setHasFixedSize(true)
    }

    private fun downloadXML(urlPath: String?): String {
        val xmlResult = StringBuilder()

        try {
            val url = URL(urlPath)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            val reader = BufferedReader(InputStreamReader(connection.inputStream))

            val inputBuffer = CharArray(500)
            var charsRead = 0
            while (charsRead >= 0) {
                charsRead = reader.read(inputBuffer)
                if (charsRead > 0) {
                    xmlResult.append(String(inputBuffer, 0, charsRead))
                }
            }
            reader.close()
            return xmlResult.toString()

        } catch (e: MalformedURLException) {
            Log.e(TAG, "downloadXML: Invalid URL ${e.message}")
        } catch (e: IOException) {
            Log.e(TAG, "downloadXML: IO Exception reading data: ${e.message}")
        } catch (e: SecurityException) {
            e.printStackTrace()
            Log.e(TAG, "downloadXML: Security exception.  Needs permissions? ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error: ${e.message}")
        }
        return ""
    }

    private fun requestApi(url: String) {

        var listItems = ArrayList<FeedEntry>()

        CoroutineScope(Dispatchers.IO).launch {


            val rssFeed = async {

                downloadXML(url)

            }.await()

            if (rssFeed.isEmpty()) {
                Log.e(TAG, "requestApi fun: Error downloading")
            } else {

                val parseApplications = async {

                    FeedParser()

                }.await()

                parseApplications.parse(rssFeed)
                listItems = parseApplications.getParsedList()


                withContext(Dispatchers.Main) {
                    rv.adapter = FeedAdapter(listItems)


                }
            }


        }

    }
}

