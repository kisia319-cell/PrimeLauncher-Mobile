package com.prime.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF090D16)
private val Panel = Color(0xFF141C2B)
private val PanelLight = Color(0xFF1B273A)
private val Gold = Color(0xFFFFC45C)
private val Muted = Color(0xFF94A3B8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PrimeApp() }
    }
}

private enum class Tab(val title: String, val icon: ImageVector) {
    HOME("Главная", Icons.Default.Home),
    RANK("Рейтинг", Icons.Default.EmojiEvents),
    NEWS("Новости", Icons.Default.Article),
    PROFILE("Профиль", Icons.Default.Person)
}

@Composable
private fun PrimeApp() {
    var selected by remember { mutableStateOf(Tab.HOME) }
    MaterialTheme(colorScheme = darkColorScheme(
        background = Ink, surface = Panel, primary = Gold,
        onBackground = Color.White, onSurface = Color.White
    )) {
        Scaffold(
            containerColor = Ink,
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF101725), contentColor = Muted) {
                    Tab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selected == tab,
                            onClick = { selected = tab },
                            icon = { Icon(tab.icon, contentDescription = tab.title) },
                            label = { Text(tab.title, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Gold, selectedTextColor = Gold,
                                indicatorColor = Color(0xFF293247),
                                unselectedIconColor = Muted, unselectedTextColor = Muted
                            )
                        )
                    }
                }
            }
        ) { padding ->
            when (selected) {
                Tab.HOME -> HomeScreen(Modifier.padding(padding), onRank = { selected = Tab.RANK })
                Tab.RANK -> SimpleScreen(Modifier.padding(padding), "Рейтинг", "Здесь появятся реальные рейтинги игроков и история матчей.", Icons.Default.EmojiEvents)
                Tab.NEWS -> SimpleScreen(Modifier.padding(padding), "Новости", "Здесь будут публиковаться обновления PrimeLauncher и SkyRealm.", Icons.Default.Article)
                Tab.PROFILE -> SimpleScreen(Modifier.padding(padding), "Профиль", "Авторизация и синхронизация профиля будут подключены на следующем этапе.", Icons.Default.Person)
            }
        }
    }
}

@Composable
private fun HomeScreen(modifier: Modifier = Modifier, onRank: () -> Unit) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(42.dp).background(Brush.linearGradient(listOf(Gold, Color(0xFFB66B28))), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Bolt, null, tint = Ink, modifier = Modifier.size(27.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("PRIME", fontSize = 21.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                    Text("LAUNCHER MOBILE", fontSize = 9.sp, color = Muted, letterSpacing = 2.sp)
                }
                Spacer(Modifier.weight(1f))
                Surface(color = Panel, shape = CircleShape) {
                    Icon(Icons.Default.NotificationsNone, null, tint = Gold, modifier = Modifier.padding(10.dp))
                }
            }
        }
        item {
            Column(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF26364D), Color(0xFF111A2A), Color(0xFF302319))), RoundedCornerShape(25.dp)).padding(22.dp)) {
                Text("СЕЗОН 01  •  PRIME ARENA", color = Gold, fontSize = 10.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(18.dp))
                Text("Время\nсражаться.", fontSize = 34.sp, lineHeight = 38.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(8.dp))
                Text("Твой путь к вершине начинается здесь.", color = Color(0xFFCBD5E1), fontSize = 13.sp)
                Spacer(Modifier.height(22.dp))
                Button(onClick = onRank, colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink), shape = RoundedCornerShape(13.dp), modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Icon(Icons.Default.EmojiEvents, null)
                    Spacer(Modifier.width(8.dp))
                    Text("ОТКРЫТЬ PRIME РЕЖИМ", fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                }
            }
        }
        item { SectionTitle("БЫСТРЫЙ ДОСТУП") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickCard("Рейтинг", "Таблица игроков", Icons.Default.Leaderboard, Modifier.weight(1f), onRank)
                QuickCard("Матчи", "История боёв", Icons.Default.SportsEsports, Modifier.weight(1f), onRank)
            }
        }
        item { SectionTitle("СОСТОЯНИЕ СИСТЕМЫ") }
        item {
            Column(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(18.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                StatusLine("Мобильное приложение", "Первый запуск", "В разработке")
                HorizontalDivider(color = Color(0xFF293448))
                StatusLine("Сервер Prime", "Подключение", "Ожидает API")
                HorizontalDivider(color = Color(0xFF293448))
                StatusLine("Синхронизация с ПК", "Аккаунт", "Будет подключена")
            }
        }
        item { Text("PrimeLauncher Mobile • ранняя версия 0.1.0", color = Muted, fontSize = 11.sp) }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)
}

@Composable
private fun QuickCard(title: String, subtitle: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(modifier.background(Panel, RoundedCornerShape(18.dp)).clickable(onClick = onClick).padding(15.dp)) {
        Icon(icon, null, tint = Gold, modifier = Modifier.size(25.dp))
        Spacer(Modifier.height(18.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(subtitle, color = Muted, fontSize = 11.sp)
    }
}

@Composable
private fun StatusLine(title: String, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(label, color = Muted, fontSize = 11.sp)
        }
        Text(value, color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SimpleScreen(modifier: Modifier = Modifier, title: String, description: String, icon: ImageVector) {
    Column(modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(76.dp).background(PanelLight, CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Gold, modifier = Modifier.size(36.dp))
        }
        Spacer(Modifier.height(22.dp))
        Text(title, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(10.dp))
        Text(description, color = Muted, fontSize = 14.sp, lineHeight = 21.sp)
        Spacer(Modifier.height(20.dp))
        Text("РАЗДЕЛ В РАЗРАБОТКЕ", color = Gold, fontSize = 10.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)
    }
}
