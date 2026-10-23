package com.famessa.deli_antojito.core.ui.theme

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.famessa.deli_antojito.core.ui.R

enum class OrderIcon(val resource: Int) {
    Edit(R.drawable.ic_orders_edit),
    More(R.drawable.ic_orders_more),
    Back(R.drawable.ic_orders_back),
    Save(R.drawable.ic_orders_save),
    User(R.drawable.ic_orders_user),
    Calendar(R.drawable.ic_orders_calendar),
    Notes(R.drawable.ic_orders_notes),
    Trash(R.drawable.ic_orders_trash),
    Minus(R.drawable.ic_orders_minus),
    Menu(R.drawable.ic_orders_menu),
    Add(R.drawable.ic_orders_add),
    Search(R.drawable.ic_orders_search),
    Filter(R.drawable.ic_orders_filter),
    Orders(R.drawable.ic_orders_orders),
    Clock(R.drawable.ic_orders_clock),
    Check(R.drawable.ic_orders_check),
    Cancel(R.drawable.ic_orders_cancel),
    Chevron(R.drawable.ic_orders_chevron),
    Home(R.drawable.ic_orders_home),
    Box(R.drawable.ic_orders_box),
    Archive(R.drawable.ic_orders_archive),
    Settings(R.drawable.ic_orders_settings),
    Chef(R.drawable.ic_orders_chef),
}

@Composable
fun OrderUiIcon(icon: OrderIcon, description: String? = null, modifier: Modifier = Modifier, tint: Color = Color.Unspecified) {
    Icon(painterResource(icon.resource), contentDescription = description, modifier = modifier, tint = tint)
}
