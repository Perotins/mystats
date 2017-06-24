package me.perotin.mystats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import me.perotin.mystats.commands.MyStatsCommand;
import me.perotin.mystats.events.MyBlockEvents;
import me.perotin.mystats.events.MyChatEvent;
import me.perotin.mystats.events.MyInventoryEvent;
import me.perotin.mystats.events.MyJoinEvent;
import me.perotin.mystats.events.MyKillEvents;

/**
 * Created by Perotin 6/17/17
 *
 */
public class MyStats extends JavaPlugin {



	private HikariDataSource hikari;

	private String password;
	private String user;
	private String database;
	private String host;
	public static MyStats instance;
	private Inventory myGui;



	@Override
	public void onEnable() {

		// initalizing information to connect to db
		instance = this;
		password = getConfig().getString("password");
		user = getConfig().getString("user");
		database = getConfig().getString("name");
		host = getConfig().getString("host");

		saveDefaultConfig();
		init();
		Bukkit.getPluginManager().registerEvents(new MyJoinEvent(), this);
		Bukkit.getPluginManager().registerEvents(new MyChatEvent(), this);
		Bukkit.getPluginManager().registerEvents(new MyBlockEvents(), this);
		Bukkit.getPluginManager().registerEvents(new MyKillEvents(), this);
		Bukkit.getPluginManager().registerEvents(new MyInventoryEvent(), this);
		
		getCommand("mystats").setExecutor(new MyStatsCommand());

		new BukkitRunnable(){
			// async task
			public void run() {
				createTable();	

			}

		}.runTaskAsynchronously(this);

	}


	@Override
	public void onDisable(){
		if(getHikariDataSource() != null){
			getHikariDataSource().close();
		}
	}

	public void init(){
		// configuring HikariConfig object
		HikariConfig config;
		config = new HikariConfig();
		config.setJdbcUrl("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		config.setUsername(user);
		config.setPassword(password);
		config.addDataSourceProperty("databaseName", database);
		config.addDataSourceProperty("serverName", host);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");


		hikari = new HikariDataSource(config);

	}

	public HikariDataSource getHikariDataSource(){
		return hikari;
	}

	public void createDisplayOneLoreLine(int stat, int slot, String name, Material material){
		// method to make setting displays easier
		// one lore line
		ItemStack item = new ItemStack(material);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name + " : "+ChatColor.WHITE + stat);
		item.setItemMeta(itemMeta);
		myGui.setItem(slot, item);
		
		
	}
	public void createDisplayTwoLoreLines(int stat,int stat2, int slot, String name, String lore1, Material material){
		// method to make setting displays easier
		// one lore line
		ItemStack item = new ItemStack(material);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name + " : "+ ChatColor.WHITE+stat);
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(lore1 + " : "+ChatColor.WHITE + stat2);
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
		myGui.setItem(slot, item);
		
		
	}
	
	public void showGui(Player toShow, String stats){
		// method should only be supplied players who we know are in the database
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		myGui = Bukkit.createInventory(null, 54, stats + "'s stats");
		int logins = 0;
		int playerKills = 0;
		int mobKills = 0;
		int deaths = 0;
		int messages = 0;
		int blocksBroken = 0;
		int blocksPlaced = 0;
		String joinDate = "";
		try {
			con = getHikariDataSource().getConnection();
			ps = con.prepareStatement("SELECT * FROM `player_data` WHERE name=?;");
			ps.setString(1, stats);
			rs = ps.executeQuery();
			while(rs.next()){
				// got data we need
				logins = rs.getInt("logins");
				playerKills = rs.getInt("player_kills");
				mobKills = rs.getInt("mob_kills");
				deaths = rs.getInt("deaths");
				messages = rs.getInt("messages_sent");
				joinDate = rs.getString("join_date");
				blocksBroken = rs.getInt("blocks_broken");
				blocksPlaced = rs.getInt("blocks_placed");
			}
			rs.close();
			ps.close();
			con.close();
			ItemStack deco = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
			// 4, 11, 29, 15, 33
			for(int i = 0; i<54; i++){
				if(i == 4 || i == 11 || i == 29 || i == 15 || i == 33){
					// we don't want to put decoration in these slots
				 continue;
				}
				myGui.setItem(i, deco);
			}
			
			 ItemStack skull = new ItemStack(Material.SKULL_ITEM, (short) 1, (byte) 3);
			 
			 // head
             SkullMeta meta = (SkullMeta) skull.getItemMeta();
             meta.setOwner(stats);
             meta.setDisplayName(ChatColor.DARK_AQUA + stats);
             ArrayList<String> lore = null;
             lore = new ArrayList<String>();
             lore.add(0, ChatColor.ITALIC + "Joined " + joinDate);
             lore.add(1, ChatColor.GRAY + "(dd/mm/yyyy)");
             meta.setLore(lore);
             skull.setItemMeta(meta);			
             myGui.setItem(4, skull);
             
             // setting gui items
             createDisplayTwoLoreLines(logins, messages, 11, ChatColor.YELLOW +"Logins", ChatColor.YELLOW+"Messages sent", Material.SIGN);
             createDisplayTwoLoreLines(playerKills, mobKills, 29, ChatColor.RED + "Player kills", ChatColor.RED + "Mob kills", Material.IRON_SWORD);
             createDisplayOneLoreLine(deaths, 15, ChatColor.GOLD + "Deaths", Material.LAVA_BUCKET);
             createDisplayTwoLoreLines(blocksPlaced, blocksBroken, 33, ChatColor.DARK_GREEN + "Blocks placed", ChatColor.DARK_GREEN + "Blocks broken", Material.GRASS);
			
             toShow.openInventory(myGui);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean inDatabase(String name){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			con = getHikariDataSource().getConnection();
			ps = con.prepareStatement("SELECT * FROM `player_data` WHERE name=?");
			ps.setString(1, name);
			rs = ps.executeQuery();
			if(rs.next()){
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}
	public boolean inDatabase(Player player){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet results = null;
		boolean kick = true;

		try {
			con = getHikariDataSource().getConnection();
			ps = con.prepareStatement("SELECT name FROM `player_data` WHERE uuid=?;");
			ps.setString(1, player.getUniqueId().toString());
			results = ps.executeQuery();
			if(results.next()){
				kick = true;
				results.close();
				ps.close();
				con.close();
				return kick;
			}else{
				kick = false;
				results.close();
				ps.close();
				con.close();
				return kick;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return kick;
	}

	public void createTable() {

		// creating table
		Connection connection = null;
		try {
			connection = hikari.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PreparedStatement create = null;
		try {
			create = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `player_data` ("
					+ "uuid VARCHAR(36) NOT NULL, name VARCHAR(20) NOT NULL, logins INT, messages_sent INT, deaths INT, player_kills INT, mob_kills INT, blocks_placed INT,"
					+ "blocks_broken INT, join_date VARCHAR(15) NOT NULL, PRIMARY KEY(uuid));");
			create.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (create != null) {
				try {
					create.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}




}
