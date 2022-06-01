package com.giovani.game_edukasi_anak_islami

import android.view.View
import com.giovani.game_edukasi_anak_islami.data.Question

interface RecyclerViewClickListener {
    fun onItemClicked(view:View, menuItem: Question)
}