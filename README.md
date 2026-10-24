# Tcslinks

Minecraft 插件，基于 Bukkit `ServerLinks` API 管理服务器链接（显示在客户端服务器菜单/ESC 菜单中的链接）。支持从配置文件加载链接、游戏内重载配置，并在重载后向所有在线玩家推送最新链接。

## 功能

- 从 `config.yml` 的 `links` 节点读取并注册服务器链接
- 支持标准链接类型（如 `WEBSITE`、`DISCORD` 等，由 `ServerLinks.Type` 决定）
- 支持自定义显示名称（未指定时使用配置中的 key）
- 支持十六进制颜色标签（`<color:#RRGGBB>`）转换为 Minecraft 颜色代码
- `/tcslinks reload` 重载配置并刷新所有在线玩家的链接

## 命令

| 命令 | 说明 |
|---|---|
| `/tcslinks reload` | 重新加载配置并刷新链接（需要权限） |

## 权限

| 权限节点 | 说明 |
|---|---|
| `tcslinks.reload` | 允许执行 `/tcslinks reload` |

无权限时提示：`§c你没有权限执行此命令！`

## 配置

首次启动会在 `plugins/Tcslinks/config.yml` 生成配置文件。链接配置位于 `links` 节点下，每个子键代表一个链接：

```yaml
links:
  website:
    url: "https://example.com"
    type: "WEBSITE"
  discord:
    url: "https://discord.gg/example"
    displayName: "<color:#5865F2>Discord 服务器"
  custom:
    url: "https://example.com/other"
    displayName: "<color:#FFAA00>自定义链接"
