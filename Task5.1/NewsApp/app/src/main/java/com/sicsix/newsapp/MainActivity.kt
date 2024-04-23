package com.sicsix.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.sicsix.newsapp.ui.theme.NewsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NewsApp()
                }
            }
        }
    }
}

// Data model for the news articles
data class ArticleItem(
    val id: Int,
    val source: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val relatedArticleIds: List<Int>
)

// Sample data for the news articles
val topStories = listOf(1, 3, 5, 7, 9)
val articles = listOf(
    ArticleItem(
        id = 1,
        source = "AFR",
        title = "Global Markets Rally on New Trade Agreement",
        description = "Markets around the world climbed today after a breakthrough trade agreement was signed, promising reduced tariffs and bolstered exports.",
        imageUrl = "https://images.pexels.com/photos/6802049/pexels-photo-6802049.jpeg?auto=compress&cs=tinysrgb&dpr=1&fit=crop&h=200&w=280",
        relatedArticleIds = listOf(2, 3, 4)
    ),
    ArticleItem(
        id = 2,
        source = "ABC",
        title = "Tech Giants Lead the Way in Sustainable Energy",
        description = "Leading tech firms are pushing the envelope with substantial investments in renewable energy, aiming for carbon neutrality.",
        imageUrl = "https://images.pexels.com/photos/414837/pexels-photo-414837.jpeg?auto=compress&cs=tinysrgb&dpr=1&fit=crop&h=200&w=280",
        relatedArticleIds = listOf(1, 5, 6)
    ),
    ArticleItem(
        id = 3,
        source = "9news",
        title = "Healthcare Advances: New Treatments Promising",
        description = "Breakthroughs in biomedical research have introduced promising new treatments for managing chronic diseases more effectively.",
        imageUrl = "https://images.pexels.com/photos/4033148/pexels-photo-4033148.jpeg?auto=compress&cs=tinysrgb&dpr=1&fit=crop&h=200&w=280",
        relatedArticleIds = listOf(1, 7, 8)
    ),
    ArticleItem(
        id = 4,
        source = "News.com.au",
        title = "Education Reform: New Policies Introduced",
        description = "New education policies aim to overhaul the current system, focusing on student-centered learning and digital classrooms.",
        imageUrl = "https://images.pexels.com/photos/5212345/pexels-photo-5212345.jpeg?auto=compress&cs=tinysrgb&dpr=1&fit=crop&h=200&w=280",
        relatedArticleIds = listOf(1, 9, 10)
    ),
    ArticleItem(
        id = 5,
        source = "The Guardian",
        title = "Climate Change and Global Action",
        description = "Global leaders meet to discuss urgent actions required to combat climate change and its impact on ecosystems and human populations.",
        imageUrl = "https://images.pexels.com/photos/2561628/pexels-photo-2561628.jpeg?auto=compress&cs=tinysrgb&dpr=1&fit=crop&h=200&w=280",
        relatedArticleIds = listOf(2, 6, 10)
    ),
    ArticleItem(
        id = 6,
        source = "ABC",
        title = "Innovations in Artificial Intelligence",
        description = "Recent advances in AI technology promise to transform industries by enhancing automation and creating smarter solutions.",
        imageUrl = "https://images.pexels.com/photos/3861969/pexels-photo-3861969.jpeg?auto=compress&cs=tinysrgb&dpr=1&fit=crop&h=200&w=280",
        relatedArticleIds = listOf(2, 5, 7)
    ),
    ArticleItem(
        id = 7,
        source = "SMH",
        title = "Advances in Space Exploration",
        description = "New missions to Mars and beyond signal a bold future for space exploration, with improved technologies and international cooperation.",
        imageUrl = "https://images.pexels.com/photos/4355348/pexels-photo-4355348.jpeg?auto=compress&cs=tinysrgb&dpr=1&fit=crop&h=200&w=280",
        relatedArticleIds = listOf(3, 6, 8)
    ),
    ArticleItem(
        id = 8,
        source = "Herald Sun",
        title = "The Future of Transportation: Electric Vehicles",
        description = "The surge in electric vehicle adoption highlights a shift towards sustainable transportation, supported by advancements in battery technology.",
        imageUrl = "https://images.pexels.com/photos/17748317/pexels-photo-17748317.jpeg?auto=compress&cs=tinysrgb&dpr=1&fit=crop&h=200&w=280",
        relatedArticleIds = listOf(3, 7, 9)
    ),
    ArticleItem(
        id = 9,
        source = "Daily Telegraph",
        title = "Political Shifts in the European Union",
        description = "Recent elections in the European Union may signal significant political shifts as new parties gain influence and set new agendas.",
        imageUrl = "https://images.pexels.com/photos/15871440/pexels-photo-15871440.jpeg?auto=compress&cs=tinysrgb&dpr=1&fit=crop&h=200&w=280",
        relatedArticleIds = listOf(4, 8, 10)
    ),
    ArticleItem(
        id = 10,
        source = "SBS",
        title = "The Rise of Cybersecurity Threats",
        description = "As digital transformations accelerate, cybersecurity threats are becoming more frequent and sophisticated, prompting increased security measures.",
        imageUrl = "https://images.pexels.com/photos/10330108/pexels-photo-10330108.jpeg?auto=compress&cs=tinysrgb&dpr=1&fit=crop&h=200&w=280",
        relatedArticleIds = listOf(4, 5, 9)
    )
)

@Composable
@Preview(showBackground = true)
fun NewsApp() {
    // Create a NavHost to handle navigation between screens
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            Main(navController = navController)
        }
        composable("story/{storyId}") { backStackEntry ->
            val storyId =
                backStackEntry.arguments?.getString("storyId")?.toIntOrNull() ?: return@composable
            Article(navController = navController, storyId = storyId)
        }
    }
}

// Main screen displaying top stories and news articles
@Composable
fun Main(navController: NavController) {
    Column {
        Text(
            text = "TOP STORIES",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(0.dp, 32.dp, 0.dp, 8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Use a LazyRow in place of RecyclerView to display a horizontal list
        LazyRow(modifier = Modifier.padding(8.dp)) {
            items(topStories) { articleId ->
                ArticleImage(
                    article = articles[articleId - 1],
                    modifier = Modifier
                        .width(LocalContext.current.resources.displayMetrics.widthPixels.dp / 9f)
                        .padding(8.dp)
                        .clickable(onClick = {
                            navController.navigate("story/${articleId}")
                        })
                )
            }
        }

        Text(
            text = "NEWS",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(12.dp, 0.dp, 0.dp, 2.dp)
        )

        // Use a LazyVerticalGrid in place of RecyclerView to display a grid of articles
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.padding(8.dp, 2.dp)) {
            items(articles) { article ->
                ArticlePreviewVertical(navController, article)
            }
        }
    }
}

// Display a vertical preview of the article
@Composable
fun ArticlePreviewVertical(navController: NavController, article: ArticleItem) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = {
                navController.navigate("story/${article.id}")
            })
    ) {
        ArticleImage(article, modifier = Modifier.fillMaxWidth())

        Text(text = article.source, style = MaterialTheme.typography.titleMedium)

        Text(text = article.title, style = MaterialTheme.typography.bodyLarge)
    }
}

// Display the image associated with the article
@Composable
fun ArticleImage(article: ArticleItem, modifier: Modifier) {
    AsyncImage(
        modifier = modifier.aspectRatio(1.4f),
        model = article.imageUrl,
        contentDescription = null,
        placeholder = BrushPainter(
            Brush.linearGradient(
                listOf(
                    Color(color = 0xFFDDDDDD),
                    Color(color = 0xFF666666),
                )
            )
        ),
    )
}

// Display the full article details
@Composable
fun Article(navController: NavController, storyId: Int) {
    val article = articles[storyId - 1]

    Column(modifier = Modifier.padding(8.dp)) {
        ArticleImage(article = article, modifier = Modifier.fillMaxWidth())

        Text(
            text = article.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 24.dp, 0.dp, 4.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = article.description,
            style = MaterialTheme.typography.bodyMedium
        )

        // Display related stories
        Text(
            text = "RELATED STORIES",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(6.dp, 32.dp, 0.dp, 4.dp)
        )

        // Use a LazyColumn in place of RecyclerView to display a vertical list
        LazyColumn(contentPadding = PaddingValues(4.dp)) {
            items(article.relatedArticleIds) { articleId ->
                ArticlePreviewHorizontal(navController, article = articles[articleId - 1])
            }
        }
    }
}

// Display a horizontal preview of the article
@Composable
fun ArticlePreviewHorizontal(navController: NavController, article: ArticleItem) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = {
                navController.navigate("story/${article.id}")
            })
    ) {
        ArticleImage(article = article, modifier = Modifier.width(120.dp))

        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = article.source, style = MaterialTheme.typography.titleMedium)

            Text(text = article.title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}