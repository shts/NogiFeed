package shts.jp.android.nogifeed.views

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_member_detail_header.view.*
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.extensions.gone
import shts.jp.android.nogifeed.models.Member
import shts.jp.android.nogifeed.utils.PicassoHelper

class ViewMemberDetailHeader @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_member_detail_header, this)
    }

    fun setup(member: Member) {
        Log.d("ViewMemberDetailHeader", "setup")
        if (TextUtils.isEmpty(member.imageUrl)) {
            profileImageView.setImageResource(R.drawable.kensyusei)
            birthdayTextView.gone()
            bloodTypeTextView.gone()
            constellationTextView.gone()
            heightTextView.gone()
            tagGroupView.gone()
        } else {
            val profileImageUrl = member.imageUrl
            PicassoHelper.loadAndCircleTransform(
                    context, profileImageView, profileImageUrl)

            nameSubTextView.text = member.nameSub

            val res = context.resources
            birthdayTextView.text = res.getString(R.string.property_name_birthday, member.birthday)
            bloodTypeTextView.text = res.getString(R.string.property_name_blood_type, member.bloodType)
            constellationTextView.text = res.getString(R.string.property_name_constellation, member.constellation)
            heightTextView.text = res.getString(R.string.property_name_height, member.height)

            val statusList = member.status
            if (statusList != null && !statusList.isEmpty()) {
                tagGroupView.setTags(member.status)
            }
        }
        nameMainTextView.text = member.nameMain
    }
}
