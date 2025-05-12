package com.carlyu.pmxv.ui.views.screens.dashboardScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carlyu.pmxv.models.data.proxmox.GuestStats
import com.carlyu.pmxv.models.data.proxmox.ResourceUsage
import com.carlyu.pmxv.models.data.proxmox.ServerDashboardInfo
import com.carlyu.pmxv.ui.theme.PmxvTheme
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@Composable
fun DashboardScreen(
    // 如果从ViewModel获取数据，则添加参数
    // viewModel: DashboardViewModel
) {
    // 使用 remember 来持有 dashboardInfo，如果它不是来自 ViewModel
    var dashboardInfoState by remember { mutableStateOf(ServerDashboardInfo()) }
    var formattedUptime by remember { mutableStateOf(formatUptime(dashboardInfoState.uptimeSeconds)) }

    LaunchedEffect(Unit) { // 使用 Unit 作为 key，只在首次组合时启动
        val initialUptime = dashboardInfoState.uptimeSeconds // 可以是从ViewModel加载的真实初始值
        while (true) {
            delay(1000) // 每秒更新一次
            dashboardInfoState =
                dashboardInfoState.copy(uptimeSeconds = dashboardInfoState.uptimeSeconds + 1)
            formattedUptime = formatUptime(dashboardInfoState.uptimeSeconds)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ServerInfoCard(
            serverName = dashboardInfoState.serverName,
            uptime = formattedUptime
        )

        ResourceMonitorCard(
            cpuUsage = dashboardInfoState.cpuUsage,
            memoryUsage = dashboardInfoState.memoryUsage,
            rootFsUsage = dashboardInfoState.rootFsUsage,
            swapUsage = dashboardInfoState.swapUsage
        )

        GuestStatusCard(
            vmStats = dashboardInfoState.vmStats,
            lxcStats = dashboardInfoState.lxcStats
        )
    }
}

fun formatUptime(totalSeconds: Long): String {
    if (totalSeconds < 0) return "N/A"
    val days = TimeUnit.SECONDS.toDays(totalSeconds)
    val hours = TimeUnit.SECONDS.toHours(totalSeconds) % 24
    val minutes = TimeUnit.SECONDS.toMinutes(totalSeconds) % 60
    val seconds = totalSeconds % 60

    return buildString {
        if (days > 0) append("$days d ")
        if (hours > 0 || days > 0) append("%02d h ".format(hours))
        if (minutes > 0 || hours > 0 || days > 0) append("%02d m ".format(minutes))
        append("%02d s".format(seconds))
    }.ifEmpty { "0 s" }
}

@Composable
fun ServerInfoCard(serverName: String, uptime: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = serverName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Uptime: $uptime",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ResourceMonitorCard(
    cpuUsage: ResourceUsage,
    memoryUsage: ResourceUsage,
    rootFsUsage: ResourceUsage,
    swapUsage: ResourceUsage
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Resource Monitor",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .align(Alignment.CenterHorizontally)
            )
            // 2x2 Grid
            // 使用嵌套的Row和Column实现固定2x2，或者LazyVerticalGrid（如果未来可能有更多项）
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ResourceUsageItem(
                        modifier = Modifier.weight(1f),
                        title = "CPU",
                        usage = cpuUsage
                    )
                    ResourceUsageItem(
                        modifier = Modifier.weight(1f),
                        title = "Memory",
                        usage = memoryUsage
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ResourceUsageItem(
                        modifier = Modifier.weight(1f),
                        title = "Root FS",
                        usage = rootFsUsage
                    )
                    ResourceUsageItem(
                        modifier = Modifier.weight(1f),
                        title = "Swap",
                        usage = swapUsage
                    )
                }
            }
            // 如果项数不固定或很多，可以使用 LazyVerticalGrid:
            /*
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(), // 高度需要合理设置或根据内容调整
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { ResourceUsageItem(title = "CPU", usage = cpuUsage) }
                item { ResourceUsageItem(title = "Memory", usage = memoryUsage) }
                item { ResourceUsageItem(title = "Root FS (/)", usage = rootFsUsage) }
                item { ResourceUsageItem(title = "Swap", usage = swapUsage) }
            }
            */
        }
    }
}

@Composable
fun ResourceUsageItem(
    modifier: Modifier = Modifier,
    title: String,
    usage: ResourceUsage
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { usage.percentage }, // Updated for Compose lambda state
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(vertical = 2.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${usage.displayUsed} / ${usage.displayTotal}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = usage.displayPercentage,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GuestStatusCard(vmStats: GuestStats, lxcStats: GuestStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Guest Status",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GuestStatItem(type = "VMs", stats = vmStats)
                GuestStatItem(type = "LXCs", stats = lxcStats)
            }
        }
    }
}

@Composable
fun GuestStatItem(type: String, stats: GuestStats) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = type, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stats.display,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Running / Total",
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
fun HomeScreenPreview() {
    PmxvTheme(darkTheme = false, dynamicColor = true) {
        DashboardScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ServerInfoCardPreview() {
    PmxvTheme(darkTheme = false, dynamicColor = true) {
        ServerInfoCard(serverName = "pve-test", uptime = formatUptime(123456))
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun ResourceMonitorCardPreview() {
    PmxvTheme(darkTheme = false, dynamicColor = true) {
        ResourceMonitorCard(
            cpuUsage = ResourceUsage(1.5f, 4.0f, "GHz", 0.375f),
            memoryUsage = ResourceUsage(10f, 16.0f, "GB", 0.625f),
            rootFsUsage = ResourceUsage(120f, 500f, "GB", 0.24f),
            swapUsage = ResourceUsage(0.1f, 2.0f, "GB", 0.05f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GuestStatusCardPreview() {
    PmxvTheme(darkTheme = false, dynamicColor = true) {
        GuestStatusCard(
            vmStats = GuestStats(running = 8, total = 10),
            lxcStats = GuestStats(running = 5, total = 5)
        )
    }
}