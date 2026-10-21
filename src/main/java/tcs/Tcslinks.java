package tcs;

import org.bukkit.ServerLinks;
import org.bukkit.ServerLinks.ServerLink;
import org.bukkit.ServerLinks.Type;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tcslinks extends JavaPlugin {

    private ServerLinks serverLinks;

    private static final Pattern COLOR_PATTERN = Pattern.compile("(?i)<color:#([0-9a-f]{6})>");

    @Override
    public void onEnable() {
        serverLinks = getServer().getServerLinks();

        if (serverLinks == null) {
            getLogger().severe("§c无法获取 ServerLinks 实例，插件将禁用！");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        reloadLinksFromConfig();

        getLogger().info("§aTcslinks 已启用！");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("tcslinks.reload")) {
                sender.sendMessage("§c你没有权限执行此命令！");
                return true;
            }

            reloadConfig();
            reloadLinksFromConfig();

            sender.sendMessage("§aTcslinks 配置已重新加载！");
            getLogger().info("§eTcslinks 配置由 " + sender.getName() + " 重新加载");
            return true;
        }

        sender.sendMessage("§e用法: /tcslinks reload");
        return true;
    }

    private void reloadLinksFromConfig() {
        getLogger().info("§e开始加载并刷新 ServerLinks 链接...");

        int removedCount = 0;
        for (ServerLink link : serverLinks.getLinks()) {
            serverLinks.removeLink(link);
            removedCount++;
        }
        getLogger().info("§c移除旧链接共计: " + removedCount);

        if (!getConfig().isConfigurationSection("links")) {
            getLogger().warning("§c配置文件中没有发现 links 节点，无法加载链接！");
            return;
        }

        int addedCount = 0;
        for (String key : getConfig().getConfigurationSection("links").getKeys(false)) {
            String urlStr = getConfig().getString("links." + key + ".url");
            String typeStr = getConfig().getString("links." + key + ".type");
            String displayName = getConfig().getString("links." + key + ".displayName");

            if (urlStr == null) {
                getLogger().warning("§c链接 " + key + " 缺少 url 字段，跳过");
                continue;
            }

            try {
                URI uri = new URI(urlStr);

                if (typeStr != null) {
                    Type type = null;
                    try {
                        type = Type.valueOf(typeStr.toUpperCase());
                    } catch (IllegalArgumentException ignored) {
                        getLogger().warning("§c链接 " + key + " 的类型 '" + typeStr + "' 无效，使用普通文本链接");
                    }

                    if (type != null) {
                        serverLinks.setLink(type, uri);
                        addedCount++;
                        continue;
                    }
                }

                if (displayName != null && !displayName.isEmpty()) {
                    displayName = convertHexColors(displayName);
                    serverLinks.addLink(displayName, uri);
                } else {
                    serverLinks.addLink(key, uri);
                }
                addedCount++;

            } catch (URISyntaxException e) {
                getLogger().warning("§c链接 " + key + " 的 url '" + urlStr + "' 不是合法URI，跳过");
            }
        }

        sendLinks();

        getLogger().info("§eServerLinks 链接刷新完成！共添加链接: " + addedCount);
    }

    private void sendLinks() {
        for (Player player : getServer().getOnlinePlayers()) {
            player.sendLinks(serverLinks);
        }
        getLogger().info("§e已向所有在线玩家推送最新链接！");
    }

    private String convertHexColors(String input) {
        if (input == null) return null;
        Matcher matcher = COLOR_PATTERN.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append('§').append(Character.toUpperCase(c));
            }
            matcher.appendReplacement(sb, replacement.toString());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
