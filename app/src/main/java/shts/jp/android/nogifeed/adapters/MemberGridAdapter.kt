package shts.jp.android.nogifeed.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_member.view.*
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.models.Member
import shts.jp.android.nogifeed.models.Members
import shts.jp.android.nogifeed.utils.PicassoHelper

interface OnMemberClickListener {
    fun onClickMember(member: Member)
}

interface OnMemberFavoriteListener {
    fun isFavorite(member: Member): Boolean
}

class MemberGridAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val members: Members = Members()

    var clickListener: OnMemberClickListener? = null
    var favoriteListener: OnMemberFavoriteListener? = null

    fun add(members: Members) {
        this.members.addAll(members)
        this.notifyDataSetChanged()
    }

    fun clear() {
        this.members.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            MemberGridViewHolder.newInstance(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val member: Member = members[position]

        holder.itemView.apply {
            rootView.setOnClickListener { clickListener?.onClickMember(member) }
            PicassoHelper.loadAndCircleTransform(profileImageView, member.imageUrl, R.drawable.kensyusei)
//            val favorite = Favorites.exist(favoriteIconView.context, member)
            val favorite = favoriteListener?.isFavorite(member) ?: false
            favoriteIconView.visibility = if (favorite) View.VISIBLE else View.GONE
            memberNameView.text = member.nameMain
        }
    }

    override fun getItemCount(): Int = members.size
}

class MemberGridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun newInstance(parent: ViewGroup): MemberGridViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_member, parent, false)
            return MemberGridViewHolder(itemView)
        }
    }
}
