package pe.edu.demo.diarioespacial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                val vm: SpaceViewModel = viewModel()
                SpaceApp(vm)
            }
        }
    }
}

private const val IMAGES = "images"
private const val ASTEROIDS = "asteroids"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceApp(vm: SpaceViewModel) {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route.orEmpty()
    val isRoot = route == IMAGES || route == ASTEROIDS
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(when {
                    route == "image-favorites" -> "Mis imágenes favoritas"
                    route.startsWith("image/") -> "Detalle de imagen"
                    route == ASTEROIDS -> "Asteroides · Alumno 2"
                    else -> "Diario espacial · APOD"
                }) },
                navigationIcon = {
                    if (!isRoot) IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        },
        bottomBar = {
            if (isRoot) NavigationBar {
                NavigationBarItem(
                    selected = route == IMAGES,
                    onClick = { nav.navigate(IMAGES) { popUpTo(IMAGES) { inclusive = false }; launchSingleTop = true } },
                    icon = { Icon(Icons.Default.Image, null) }, label = { Text("Imágenes") }
                )
                NavigationBarItem(
                    selected = route == ASTEROIDS,
                    onClick = { nav.navigate(ASTEROIDS) { popUpTo(IMAGES) { inclusive = false }; launchSingleTop = true } },
                    icon = { Icon(Icons.Default.Public, null) }, label = { Text("Asteroides") }
                )
            }
        }
    ) { insets ->
        NavHost(navController = nav, startDestination = IMAGES, modifier = Modifier.padding(insets)) {
            composable(IMAGES) { ImageList(vm, nav, favorites = false) }
            composable(ASTEROIDS) {
                Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("Esta sección la implementa el Alumno 2 en su rama y Pull Request.")
                }
            }
            composable("image-favorites") { ImageList(vm, nav, favorites = true) }
            composable("image/{date}", arguments = listOf(navArgument("date") { type = NavType.StringType })) { entry ->
                ImageDetail(vm, entry.arguments?.getString("date").orEmpty())
            }
        }
    }
}

@Composable
private fun NetworkState(loading: Boolean, error: String?, onRetry: () -> Unit) {
    if (loading) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        Text("Consultando API NASA...", style = MaterialTheme.typography.bodySmall)
    }
    if (error != null) {
        Text(error, color = MaterialTheme.colorScheme.error)
        TextButton(onClick = onRetry) { Text("Reintentar") }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ImageList(vm: SpaceViewModel, nav: NavHostController, favorites: Boolean) {
    val all by (if (favorites) vm.favoriteApods else vm.apods).collectAsState()
    val loading by vm.imageLoading.collectAsState()
    val error by vm.imageError.collectAsState()
    LaunchedEffect(favorites) { if (!favorites) vm.loadImages() }
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        if (!favorites) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("APOD · últimos 7 días", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                TextButton(onClick = { nav.navigate("image-favorites") }) { Text("Mis favoritas") }
                IconButton(onClick = { vm.loadImages(force = true) }) { Icon(Icons.Default.Refresh, "Actualizar") }
            }
            NetworkState(loading, error) { vm.loadImages(force = true) }
        }
        if (all.isEmpty() && !loading) {
            EmptyContent(if (favorites) "Aún no hay imágenes favoritas" else "No hay imágenes guardadas. Actualiza para consultar NASA.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 10.dp)) {
                items(all, key = { it.date }) { item ->
                    Card(onClick = { nav.navigate("image/${item.date}") }, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            if (item.mediaType == "image" && item.url.isNotBlank()) {
                                AsyncImage(model = item.url, contentDescription = item.title,
                                    modifier = Modifier.fillMaxWidth().height(170.dp), contentScale = ContentScale.Crop)
                            } else Text(
                                if (item.mediaType == "image") "Imagen no disponible" else "Contenido en video",
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(item.title, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.date, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                                FavoriteButton(item.favorite) { vm.toggleImage(item.date) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageDetail(vm: SpaceViewModel, date: String) {
    val image by remember(date) { vm.apod(date) }.collectAsState(initial = null)
    val item = image
    if (item == null) { EmptyContent("Imagen no disponible") ; return }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        if (item.mediaType == "image") {
            if ((item.hdurl ?: item.url).isNotBlank()) AsyncImage(
                model = item.hdurl?.takeIf { it.isNotBlank() } ?: item.url,
                contentDescription = item.title,
                modifier = Modifier.fillMaxWidth().height(280.dp), contentScale = ContentScale.Fit
            ) else Text("Imagen no disponible")
        } else Text("Contenido en video", style = MaterialTheme.typography.titleMedium)
        Text(item.title, style = MaterialTheme.typography.headlineSmall)
        Text(item.date)
        if (!item.copyright.isNullOrBlank()) Text("Autor: ${item.copyright}")
        FavoriteButton(item.favorite) { vm.toggleImage(item.date) }
        Spacer(Modifier.height(16.dp))
        Text(item.explanation.ifBlank { "Sin explicación disponible" })
    }
}

@Composable
private fun FavoriteButton(favorite: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (favorite) "Quitar favorito" else "Agregar favorito",
            tint = if (favorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun EmptyContent(message: String) {
    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(message, style = MaterialTheme.typography.bodyMedium)
    }
}


