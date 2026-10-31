package com.bloom.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bloom.app.ui.theme.BackgroundCream
import com.bloom.app.ui.theme.BloomTheme
import com.bloom.app.ui.theme.MutedGrey
import com.bloom.app.ui.theme.PrimaryBrickRed
import com.bloom.app.ui.theme.SansFont

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val navItems = listOf(
    BottomNavItem("Dashboard", Icons.Filled.Home,      Icons.Outlined.Home),
    BottomNavItem("Insights",  Icons.Filled.BarChart,  Icons.Outlined.BarChart),
    BottomNavItem("Learn",     Icons.Filled.Book,      Icons.Filled.Book),
    BottomNavItem("Settings",  Icons.Filled.Settings,  Icons.Outlined.Settings)
)

@Composable
fun BloomBottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Column(modifier = modifier) {
        HorizontalDivider(
            modifier  = Modifier.fillMaxWidth(),
            thickness = 0.5.dp,
            color     = androidx.compose.ui.graphics.Color(0xFFE8DEDD)
        )
        NavigationBar(
            containerColor = BackgroundCream,
            tonalElevation = 0.dp
        ) {
            navItems.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick  = { onItemSelected(index) },
                    icon = {
                        Icon(
                            imageVector        = if (selectedIndex == index) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label
                        )
                    },
                    label = {
                        Text(
                            text       = item.label,
                            fontFamily = SansFont
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor   = PrimaryBrickRed,
                        selectedTextColor   = PrimaryBrickRed,
                        unselectedIconColor = MutedGrey,
                        unselectedTextColor = MutedGrey,
                        indicatorColor      = BackgroundCream
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomBarPreview() {
    BloomTheme {
        var selected by rememberSaveable { mutableIntStateOf(0) }
        BloomBottomBar(selectedIndex = selected, onItemSelected = { selected = it })
    }
}
