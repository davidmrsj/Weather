package com.example.weather.presentation.screens.weather

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather.R
import com.example.weather.core.utils.TimeFormatter
import com.example.weather.core.utils.TimeFormatter.TIME_24
import com.example.weather.core.utils.TimeFormatter.UPDATED
import com.example.weather.di.AppModule
import com.example.weather.domain.model.Weather
import com.example.weather.presentation.ui.theme.BluePrimary
import com.example.weather.presentation.ui.theme.GreenBlue
import com.example.weather.presentation.ui.theme.RedCloud
import com.example.weather.presentation.ui.theme.TextBlack
import com.example.weather.presentation.ui.theme.WhiteBorder
import com.example.weather.presentation.ui.theme.YellowPressure
import java.util.Locale
import kotlin.math.min


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
    val updated = TimeFormatter.formatMillis(weather.lastUpdated, UPDATED)

    val temp = "${weather.temperature}°"
    val feelsLike = "Sensación de ${(weather.temperature - 1)}°"
    val description = weather.description.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
    val humidity = weather.humidity.coerceIn(0, 100)
    val pressure = weather.pressure
    val windSpeedKmh = (weather.windSpeed * 3.6f)
    val rainMm: Float? = weather.rainOneHour
    val windDeg = weather.windDeg

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BluePrimary)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(6.dp))
        LocatorTrail()
        Spacer(Modifier.height(10.dp))
        TopBar(city = weather.city)

        Spacer(Modifier.height(16.dp))

        Text(updated, color = Color.White, fontSize = 14.sp)
        Spacer(Modifier.height(16.dp))
        NowHeaderRow(
            windKmh = windSpeedKmh,
            rainMm = rainMm,
            tempText = temp,
            feelsLike = feelsLike,
            description = description,
            tMax = weather.tempMax.toInt(),
            tMin = weather.tempMin.toInt()
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
            degrees = windDeg,
            directionLabel = "ne"
        )
        Spacer(Modifier.height(12.dp))

        SunCard(
            sunsetEpoch = weather.sunset,
            sunriseEpoch = weather.sunrise
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
private fun LocatorTrail() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.near_me),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(12.dp)
        )
        Spacer(Modifier.width(6.dp))
        Dot()
        Spacer(Modifier.width(6.dp))
        Dot()
    }
}

@Composable
private fun Dot() {
    Box(
        modifier = Modifier
            .size(6.dp)
            .background(Color.White, CircleShape)
    )
}

@Composable
private fun NowHeaderRow(
    windKmh: Float,
    rainMm: Float?,
    tempText: String,
    tMax: Int,
    tMin: Int,
    feelsLike: String,
    description: String,
    iconRes: Int = R.drawable.cielo_limpio
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.widthIn(min = 96.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.air), null, tint = Color.White)
                Spacer(Modifier.width(6.dp))
                Text("${windKmh.toInt()} km/h", color = Color.White, fontSize = 12.sp)
            }
            if (rainMm != null && rainMm > 0f) {
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painterResource(R.drawable.rainy),
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("${rainMm}mm", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(42.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    tempText,
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                feelsLike,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
                maxLines = 1
            )
            Spacer(Modifier.height(2.dp))
            Text(
                description,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }

        Column(
            modifier = Modifier.widthIn(min = 64.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    painter = painterResource(R.drawable.vertical_align_top),
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(Modifier.width(4.dp))
                Text("$tMax°", color = Color.White, fontSize = 14.sp)
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.vertical_align_bottom),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .width(16.dp)
                        .height(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("$tMin°", color = Color.White, fontSize = 14.sp)
            }
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
    degrees: Int,
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
    degrees: Int,
    label: String,
    bgRes: Int = R.drawable.brujula,
    markerRes: Int = R.drawable.sol
) {
    val markerSize = 16.dp
    val boxSizePx = remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .onGloballyPositioned { boxSizePx.value = it.size },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(bgRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

            if (boxSizePx.value.width > 0) {
                val w = boxSizePx.value.width.toFloat()
                val h = boxSizePx.value.height.toFloat()
                val cx = w / 2f
                val cy = h / 2f
                val r = min(cx, cy) * 0.72f

                val ang = Math.toRadians((degrees - 90f).toDouble())
                val px = cx + r * kotlin.math.cos(ang).toFloat()
                val py = cy + r * kotlin.math.sin(ang).toFloat()

                val markerHalf = with(density) { (markerSize / 2).toPx() }

                Image(
                    painter = painterResource(markerRes),
                    contentDescription = null,
                    modifier = Modifier
                        .absoluteOffset(
                            x = (px - markerHalf).toDp() / 2,
                            y = (py - markerHalf).toDp() / 2
                        )
                        .size(markerSize)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = label.substringBefore(" "),
                    color = TextBlack,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                val sub = label.substringAfter(" ", missingDelimiterValue = "").lowercase()
                if (sub.isNotEmpty()) {
                    Text(
                        text = sub,
                        color = TextBlack,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))
    }
}

@Composable
private fun SunCard(
    sunsetEpoch: Long,
    sunriseEpoch: Long,
) {
    val sunset = TimeFormatter.formatMillis(sunsetEpoch, TIME_24)
    val sunrise = TimeFormatter.formatMillis(sunriseEpoch, TIME_24)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)
            .border(1.dp, WhiteBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .border(1.dp, WhiteBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.wb_twilight),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Puesta de sol",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Row {
                            Text(
                                "Anochece ",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 14.sp
                            )
                            Text(
                                sunset,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.puesta_de_sol_mountain),
                            contentDescription = "Puesta de sol",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Row {
                            Text(
                                "Amanece ",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 14.sp
                            )
                            Text(
                                sunrise,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.puesta_de_sol),
                            contentDescription = "Amanecer",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(46.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun Float.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }



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
    lastUpdated = System.currentTimeMillis() / 1000,
    rainOneHour = 2.0f
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
            wind = 3f,
            gusts = 4f,
            degrees = 57,
            directionLabel = "ne"
        )
    }
}

@Preview(name = "Error")
@Composable
private fun ErrorPreview() {
    WeatherError("Error") {}
}
