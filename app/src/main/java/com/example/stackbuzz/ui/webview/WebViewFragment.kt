package com.example.stackbuzz.ui.webview

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.stackbuzz.R
import com.example.stackbuzz.databinding.FragmentWebViewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val URL = "url"

class WebViewFragment : Fragment() {
    private var url: String? = null
    private var _binding: FragmentWebViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(URL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)

        val webView: WebView = binding.webView
        with(webView) {
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.domStorageEnabled = true
            webViewClient = WebViewClient()
        }
        webView.loadUrl(url!!)
        webView.visibility = View.VISIBLE

        webView.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_BACK -> if (webView.canGoBack()) {
                            webView.goBack()
                            return true
                        }
                    }
                }
                return false
            }
        })
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        val view = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (view != null) {
            view.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(url: String) =
            WebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(URL, url)
                }
            }
    }
}