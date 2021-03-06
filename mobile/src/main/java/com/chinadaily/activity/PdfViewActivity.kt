package com.chinadaily.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.chinadaily.R
import com.chinadaily.base.BaseAppActivity
import com.chinadaily.base.setTabBarTop
import com.chinadaily.http.HttpConstants
import com.github.barteksc.pdfviewer.link.DefaultLinkHandler
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Progress
import com.lzy.okserver.OkDownload
import com.lzy.okserver.download.DownloadListener
import kotlinx.android.synthetic.main.activity_pdf_view_layout.*
import kotlinx.android.synthetic.main.base_title_layout.*
import java.io.File

const val EXTRA_PDF_URL = "EXTRA_PDF_URL"

class PdfViewActivity : BaseAppActivity() {
    private lateinit var pdfUrl: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pdfUrl = intent.getStringExtra(EXTRA_PDF_URL)
        setContentView(R.layout.activity_pdf_view_layout)
        tabBar.setTabBarTop()
        baseBack.setOnClickListener { finish() }
        downloadPdf(pdfUrl)
        loadingRetry.setOnClickListener {
            pdfView.visibility = View.GONE
            loadingProgressBar.visibility = View.VISIBLE
            loadingRetry.visibility = View.GONE
            downloadPdf(pdfUrl)
        }
    }

    private fun downloadPdf(pdfUrl: String) {
        OkDownload.request(pdfUrl, OkGo.get(pdfUrl))
                .register(object : DownloadListener(pdfUrl) {
                    override fun onFinish(t: File?, progress: Progress?) {
                        pdfView.visibility = View.VISIBLE
                        loadingProgressBar.visibility = View.GONE
                        loadingRetry.visibility = View.GONE
                        pdfView.fromFile(t)
                                .defaultPage(0)
                                .enableAnnotationRendering(true)
                                .enableDoubletap(true)
                                .enableAntialiasing(true)
                                .enableSwipe(true)
                                .linkHandler(DefaultLinkHandler(pdfView))
                                .pageFitPolicy(FitPolicy.BOTH)
                                .swipeHorizontal(true)
                                .load()
                    }

                    override fun onRemove(progress: Progress?) {
                    }

                    override fun onProgress(progress: Progress?) {
                    }

                    override fun onError(progress: Progress?) {
                        pdfView.visibility = View.GONE
                        loadingProgressBar.visibility = View.GONE
                        loadingRetry.visibility = View.VISIBLE
                    }

                    override fun onStart(progress: Progress?) {
                    }
                })
                .save()
                .start()
    }

    override fun onDestroy() {
        super.onDestroy()
        OkDownload.getInstance().getTask(pdfUrl).unRegister(pdfUrl)
    }
}

fun Context.goToPDF(url: String) {
    val intent = Intent(this, PdfViewActivity::class.java)
    intent.putExtra(EXTRA_PDF_URL, "${HttpConstants.E_PAPER_SERVICE}$url")
    intent.putExtra("title", "ePaper")
    startActivity(intent)
}