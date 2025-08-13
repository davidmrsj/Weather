package com.example.weather.presentation.screens.weather

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather.R
import com.example.weather.di.AppModule
import com.example.weather.domain.model.Weather
import com.example.weather.presentation.ui.theme.BluePrimary
import com.example.weather.presentation.ui.theme.GreenBlue
import com.example.weather.presentation.ui.theme.RedCloud
import com.example.weather.presentation.ui.theme.WhiteBorder
import com.example.weather.presentation.ui.theme.YellowPressure
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun WeatherRoute(
    viewModel: WeatherViewModel = viewModel(
        factory = AppModule.provideWeatherViewModelFactory(
            LocalContext.current
        )
    )
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadWeather() }
    WeatherScreen(state = state, onRetry = { viewModel.loadWeather() })
}

@Composable
fun WeatherScreen(state: WeatherUiState, onRetry: () -> Unit) {
    when (state) {
        WeatherUiState.Loading -> WeatherSkeletonScreen()
        is WeatherUiState.Error -> WeatherError(state.message, onRetry)
        is WeatherUiState.Success -> WeatherSuccess(state.weather)
    }
}


@Composable
private fun WeatherLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BluePrimary),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator(color = Color.White) }
}

@Composable
private fun WeatherError(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BluePrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRetry) { Text("Reintentar") }
        }
    }
}


@Composable
fun WeatherSuccess(weather: Weather) {
    val updated =
        SimpleDateFormat(
            "d 'de' MMMM | HH:mm",
            Locale.getDefault()
        ).format(Date(weather.lastUpdated * 1000))
    val temp = "${weather.temperature}°"
    val feelsLike = "Sensación de ${(weather.temperature - 1)}°"
    val description = weather.description.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
    val humidity = weather.humidity.coerceIn(0, 100)
    val pressure = weather.pressure
    val windSpeedKmh = (weather.windSpeed * 3.6f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BluePrimary)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TopBar(city = weather.city)

        Spacer(Modifier.height(16.dp))

        Text(updated, color = Color.White, fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.cielo_limpio),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(42.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(temp, color = Color.White, style = MaterialTheme.typography.displaySmall)
        }

        Spacer(Modifier.height(4.dp))
        Text(feelsLike, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
        Spacer(Modifier.height(6.dp))
        Text(
            description,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CurvedGaugeWithLabel(
                value = ((pressure - 950f) / 100f).coerceIn(0f, 1f),
                badgeText = "$pressure hPa",
                label = "Presión",
                progressColor = YellowPressure
            )
            CurvedGaugeWithLabel(
                value = (humidity / 100f).coerceIn(0f, 1f),
                badgeText = "${humidity}%",
                label = "Nubes",
                progressColor = RedCloud
            )
            CurvedGaugeWithLabel(
                value = (humidity / 100f).coerceIn(0f, 1f),
                badgeText = "${humidity}%",
                label = "Humedad",
                progressColor = GreenBlue
            )
        }

        Spacer(Modifier.height(18.dp))

        WindCard(
            wind = windSpeedKmh,
            gusts = windSpeedKmh * 1.8f,
            degrees = 260f,
            directionLabel = "ne"
        )
    }
}

@Composable
private fun TopBar(city: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { }) {
            Icon(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = null,
                tint = Color.White
            )
        }
        Spacer(Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(36.dp)
                .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(18.dp))
                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.near_me),
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(city, color = Color.White, modifier = Modifier.weight(1f), maxLines = 1)
                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
        Spacer(Modifier.width(4.dp))
        IconButton(onClick = { }) {
            Icon(
                painter = painterResource(id = R.drawable.share),
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}


@Composable
private fun CurvedGauge(
    progress: Float,
    size: Dp,
    strokeWidth: Dp,
    trackColor: Color,
    progressColor: Color
) {
    Canvas(modifier = Modifier.size(size)) {
        val thickness = strokeWidth.toPx()
        val diameter = size.toPx()
        val inset = thickness / 2f + 2f
        val arcRect = Rect(Offset(inset, inset), Size(diameter - inset * 2, diameter - inset * 2))

        val startAngle = 130f
        val sweep = 280f

        drawArc(
            color = trackColor,
            startAngle = startAngle,
            sweepAngle = sweep,
            useCenter = false,
            style = Stroke(width = thickness, cap = StrokeCap.Round),
            topLeft = arcRect.topLeft,
            size = arcRect.size
        )

        val sweepProgress = (sweep * progress).coerceIn(0f, sweep)
        if (sweepProgress > 0f) {
            drawArc(
                color = progressColor,
                startAngle = startAngle,
                sweepAngle = sweepProgress,
                useCenter = false,
                style = Stroke(width = thickness, cap = StrokeCap.Round),
                topLeft = arcRect.topLeft,
                size = arcRect.size
            )

            val angleRad = Math.toRadians((startAngle + sweepProgress).toDouble())
            val r = arcRect.width / 2f
            val center = arcRect.center
            val endX = center.x + r * kotlin.math.cos(angleRad).toFloat()
            val endY = center.y + r * kotlin.math.sin(angleRad).toFloat()
            drawCircle(progressColor, radius = thickness * 0.45f, center = Offset(endX, endY))
        }
    }
}

@Composable
private fun CurvedGaugeWithLabel(
    value: Float,
    badgeText: String,
    label: String,
    progressColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(70.dp), contentAlignment = Alignment.Center) {
            CurvedGauge(
                progress = value,
                size = 70.dp,
                strokeWidth = 6.dp,
                trackColor = Color.White,
                progressColor = progressColor
            )
            Surface(
                color = BluePrimary,
                shape = CircleShape,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                modifier = Modifier.size(46.dp),
            ) {}
            Text(
                badgeText.replace(" ", "\n"),
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
        Text(label, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun WindCard(
    wind: Float,
    gusts: Float,
    degrees: Float,
    directionLabel: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)
            .border(1.dp, WhiteBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Icon(
                        painter = painterResource(id = R.drawable.air),
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Viento", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        wind.toInt().toString(),
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Column {
                        Text("km/h", color = Color.White, fontSize = 12.sp)
                        Text("Viento", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = Color.White.copy(alpha = 0.35f)
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        gusts.toInt().toString(),
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Column {
                        Text("km/h", color = Color.White, fontSize = 12.sp)
                        Text("Rachas", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterVertically)
                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Compass(degrees = degrees, label = "${degrees.toInt()}º $directionLabel")
            }
        }
    }
}

@Composable
private fun Compass(
    degrees: Float,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier.size(110.dp)) {
            val radius = size.minDimension / 2f
            val center = Offset(radius, radius)

            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = radius,
                style = Stroke(width = 2.dp.toPx())
            )

            val tickWidth = 2.dp.toPx()
            val smallTick = radius * 0.08f
            val bigTick = radius * 0.14f
            val start = radius * 0.78f
            for (i in 0 until 360 step 10) {
                val r1 = start
                val r2 = start + if (i % 30 == 0) bigTick else smallTick
                val ang = Math.toRadians(i.toDouble())
                val p1 = Offset(
                    center.x + r1 * kotlin.math.cos(ang).toFloat(),
                    center.y + r1 * kotlin.math.sin(ang).toFloat()
                )
                val p2 = Offset(
                    center.x + r2 * kotlin.math.cos(ang).toFloat(),
                    center.y + r2 * kotlin.math.sin(ang).toFloat()
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.8f),
                    start = p1,
                    end = p2,
                    strokeWidth = tickWidth,
                    cap = StrokeCap.Round
                )
            }

            val needleLen = radius * 0.7f
            val ang = Math.toRadians((degrees - 90).toDouble())
            val end = Offset(
                center.x + needleLen * kotlin.math.cos(ang).toFloat(),
                center.y + needleLen * kotlin.math.sin(ang).toFloat()
            )
            drawLine(
                color = Color(0xFFFF6D00),
                start = center,
                end = end,
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round,
                pathEffect = PathEffect.cornerPathEffect(8f)
            )
            drawCircle(Color(0xFFFF6D00), radius = 6.dp.toPx(), center = center)
        }
        Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}


private fun previewWeather() = Weather(
    city = "Valladolid, Valladolid",
    description = "parcialmente nublado",
    icon = "ic_partly_cloudy",
    temperature = 20f,
    tempMin = 18.0,
    tempMax = 22.0,
    feelsLike = 19.0,
    pressure = 1018,
    humidity = 34,
    clouds = 82,
    windSpeed = 5.0f,
    windDeg = 260,
    windGust = 12.0,
    sunrise = System.currentTimeMillis() / 1000 - 6 * 60 * 60,
    sunset = System.currentTimeMillis() / 1000 + 6 * 60 * 60,
    lastUpdated = System.currentTimeMillis() / 1000
)

@Preview(name = "Pantalla completa", showSystemUi = true)
@Composable
private fun WeatherScreenPreview() {
    WeatherSuccess(weather = previewWeather())
}

@Preview(name = "Gauge Curvo")
@Composable
private fun GaugePreview() {
    Box(
        modifier = Modifier
            .background(BluePrimary)
            .padding(16.dp)
    ) {
        CurvedGaugeWithLabel(
            value = 0.82f,
            badgeText = "82%",
            label = "Nubes",
            progressColor = GreenBlue
        )
    }
}

@Preview(name = "Tarjeta Viento")
@Composable
private fun WindCardPreview() {
    Box(
        modifier = Modifier
            .background(BluePrimary)
            .padding(16.dp)
    ) {
        WindCard(
            wind = 18f,
            gusts = 37f,
            degrees = 260f,
            directionLabel = "ne"
        )
    }
}

@Preview(name = "Error")
@Composable
private fun ErrorPreview() {
    WeatherError("Error") {}
}
