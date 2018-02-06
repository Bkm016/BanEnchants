package me.skymc.banenchants;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import me.skymc.taboolib.fileutils.ConfigUtils;
import me.skymc.taboolib.inventory.ItemUtils;

/**
 * @author sky
 * @since 2018年2月6日 上午10:36:40
 */
public class BanEnchants extends JavaPlugin implements Listener {
	
	@Getter
	private static Plugin inst;
	
	@Getter
	private static FileConfiguration conf;
	
	@Override
	public FileConfiguration getConfig() {
		return conf;
	}
	
	@Override
	public void reloadConfig() {
		File file = new File(getDataFolder(), "config.yml");
		if (!file.exists()) {
			saveResource("config.yml", true);
		}
		conf = ConfigUtils.load(this, file);
	}
	
	@Override
	public void onLoad() {
		inst = this;
		reloadConfig();
	}
	
	@Override
	public void onEnable() {
		if (Bukkit.getPluginManager().getPlugin("TabooLib") == null) {
			Bukkit.getConsoleSender().sendMessage("§4[TabooLib - Version] §c缺少禁忌书库, 插件已关闭");
			return;
		}
		else if (Double.valueOf(Bukkit.getPluginManager().getPlugin("TabooLib").getDescription().getVersion()) < 3.43) {
			Bukkit.getConsoleSender().sendMessage("§4[TabooLib - Version] §c禁忌书库版本过低, 需要最低 3.43 版本");
			return;
		}
		
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		reloadConfig();
		sender.sendMessage("reload ok!");
		return true;
	}

	@EventHandler
	public void inter(PlayerInteractEvent e) {
		if (e.getAction() == Action.PHYSICAL || e.getPlayer().getItemInHand() == null || e.getPlayer().getItemInHand().getType().equals(Material.AIR)) {
			return;
		}
		
		// 获取物品
		ItemStack item = e.getPlayer().getItemInHand();
		boolean isChanged = false;
		
		// 检查附魔列表
		for (String ench : conf.getStringList("enchants")) {
			Enchantment enchantment = ItemUtils.asEnchantment(ench);
			if (enchantment != null && item.getItemMeta().hasEnchant(enchantment)) {
				ItemMeta meta = item.getItemMeta();
				meta.removeEnchant(enchantment);
				item.setItemMeta(meta);
				isChanged = true;
			}
		}
		
		// 更改物品
		if (isChanged) {
			e.getPlayer().setItemInHand(item);
			e.getPlayer().sendMessage(conf.getString("message").replace("&", "§"));
		}
	}
}
