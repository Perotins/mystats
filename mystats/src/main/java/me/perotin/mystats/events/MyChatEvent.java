package me.perotin.mystats.events;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.perotin.mystats.MyStats;

public class MyChatEvent implements Listener {

	/*
	 * Created by Perotin 22/06/2017
	 */
	@EventHandler
	public void chat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet results = null;
		// player not in database, kick and have them rejoin
		if(!MyStats.instance.inDatabase(player)){
			player.kickPlayer("Kicked! Rejoin to enter database.");
			return;
		}
		try {
			con = MyStats.instance.getHikariDataSource().getConnection();
			ps = con.prepareStatement("SELECT messages_sent FROM `player_data` WHERE uuid=?;");
			ps.setString(1, player.getUniqueId().toString());
			results = ps.executeQuery();
			while (results.next()){
				// get previous message count and + 1
				int messages = results.getInt("messages_sent") + 1;
				Connection con2 = null;
				con2 = MyStats.instance.getHikariDataSource().getConnection();
				PreparedStatement ps2 = null;
				ps2 = con2.prepareStatement("UPDATE `player_data` SET messages_sent = ? WHERE uuid=?");
				ps2.setInt(1, messages);
				ps2.setString(2, player.getUniqueId().toString());
				ps2.executeUpdate();
				
				ps2.close();
				con2.close();
			}
			// this would only occur if they aren't in the database, which we already checked earlier
			results.close();
			ps.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
