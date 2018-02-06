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
 * @since 2018��2��6�� ����10:36:40
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
			Bukkit.getConsoleSender().sendMessage("��4[TabooLib - Version] ��cȱ�ٽ������, ����ѹر�");
			return;
		}
		else if (Double.valueOf(Bukkit.getPluginManager().getPlugin("TabooLib").getDescription().getVersion()) < 3.43) {
			Bukkit.getConsoleSender().sendMessage("��4[TabooLib - Version] ��c�������汾����, ��Ҫ��� 3.43 �汾");
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
		
		// ��ȡ��Ʒ
		ItemStack item = e.getPlayer().getItemInHand();
		boolean isChanged = false;
		
		// ��鸽ħ�б�
		for (String ench : conf.getStringList("enchants")) {
			Enchantment enchantment = ItemUtils.asEnchantment(ench);
			if (enchantment != null && item.getItemMeta().hasEnchant(enchantment)) {
				ItemMeta meta = item.getItemMeta();
				meta.removeEnchant(enchantment);
				item.setItemMeta(meta);
				isChanged = true;
			}
		}
		
		// ������Ʒ
		if (isChanged) {
			e.getPlayer().setItemInHand(item);
			e.getPlayer().sendMessage(conf.getString("message").replace("&", "��"));
		}
	}
}
