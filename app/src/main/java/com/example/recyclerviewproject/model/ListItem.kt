package com.example.recyclerviewproject.model

import android.R

sealed class ListItem {
    data class Header(val title: String) : ListItem()

    data class ItemType1(
        val id: Int,
        val title: String,
        val description: String,
        val iconRes: Int = R.drawable.ic_menu_send
    ) : ListItem()

    data class ItemType2(
        val id: Int,
        val name: String,
        val price: String,
        val rating: Float,
        val imageRes: Int = R.drawable.ic_menu_camera
    ) : ListItem()
}