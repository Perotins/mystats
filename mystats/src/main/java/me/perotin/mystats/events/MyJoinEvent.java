package me.perotin.mystats.events;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.perotin.mystats.MyStats;


public class MyJoinEvent implements Listener {

	/*
	 * Created 6/20/17 by Perotin
	 */

	@EventHandler
	public void join(PlayerJoinEvent event){
		Player player = event.getPlayer();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet results = null;

		try {
			con = MyStats.instance.getHikariDataSource().getConnection();
			// determine if our database contains this new player
			ps = con.prepareStatement("SELECT name, logins FROM `player_data` WHERE uuid=?;");
			ps.setString(1, player.getUniqueId().toString());
			results = ps.executeQuery();
			if(results.next()){
				// in database
				int logins = results.getInt("logins");
				Connection con2 = null;
				PreparedStatement ps2 = null;
				con2 = MyStats.instance.getHikariDataSource().getConnection();
				// adding one to logins 
				ps2 = con2.prepareStatement("UPDATE `player_data` SET logins =? WHERE uuid=?");
				ps2.setInt(1, logins + 1);
				ps2.setString(2, player.getUniqueId().toString());
				ps2.executeUpdate();
				ps2.close();
				con2.close();
				return;
			}
			results.close();
			ps.close();
			con.close();

			// not in database, insert default information to database
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			String joinDate = format.format(date);

			Connection con3 = null;
			PreparedStatement ps3 = null;
			con3 = MyStats.instance.getHikariDataSource().getConnection();
			ps3 = con3.prepareStatement("INSERT IGNORE INTO `player_data` VALUES(?,?,1,0,0,0,0,0,0,?);");
			ps3.setString(1, player.getUniqueId().toString());
			ps3.setString(2, player.getName());
			ps3.setString(3, joinDate);

			ps3.executeUpdate();




			MyStats.instance.getLogger().info("Saving data for " + player.getName());

			ps3.close();
			con3.close();
		} catch (SQLException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
