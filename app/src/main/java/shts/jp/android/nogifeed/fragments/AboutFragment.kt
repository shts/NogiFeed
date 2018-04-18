package shts.jp.android.nogifeed.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.about_header.view.*
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.list_item_about_action.view.*
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.utils.IntentUtils
import shts.jp.android.nogifeed.utils.PicassoHelper

class AboutFragment : Fragment() {

    companion object {
        private val URL_ICON = "https://avatars1.githubusercontent.com/u/7928836?v=3&s=460"

        fun newInstance(): AboutFragment = AboutFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setTitle(R.string.nav_menu_about_app)
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_18dp)
        toolbar.setNavigationOnClickListener { activity.finish() }

        aboutHeaderView.developerIconView.apply {
            PicassoHelper.loadAndCircleTransform(activity, this, URL_ICON)
            setOnClickListener { IntentUtils.showDeveloper(context) }
        }

        socialShareListItem.apply {
            setOnClickListener { IntentUtils.recommendApp(context) }
            iconView.setImageResource(R.drawable.ic_action_share)
            titleTextView.setText(R.string.about_item_share)
        }

        thumbUpListItem.apply {
            setOnClickListener { IntentUtils.rateApp(context) }
            iconView.setImageResource(R.drawable.ic_action_thumb_up)
            titleTextView.setText(R.string.about_item_rate)
        }

        mentionListItem.apply {
            setOnClickListener { IntentUtils.inquiryApp(context) }
            iconView.setImageResource(R.drawable.ic_action_mode_comment)
            titleTextView.setText(R.string.about_item_mention)
        }
    }
}
