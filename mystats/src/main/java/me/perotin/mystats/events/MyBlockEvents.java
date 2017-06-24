package me.perotin.mystats.events;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import me.perotin.mystats.MyStats;

public class MyBlockEvents implements Listener {

	
	@EventHandler
	public void onBreak(BlockBreakEvent event){
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
			ps = con.prepareStatement("SELECT blocks_broken FROM `player_data` WHERE uuid=?;");
			ps.setString(1, player.getUniqueId().toString());
			results = ps.executeQuery();
			while (results.next()){
				// get previous blocks broken count and + 1
				int blocksBroken = results.getInt("blocks_broken") + 1;
				Connection con2 = null;
				con2 = MyStats.instance.getHikariDataSource().getConnection();
				PreparedStatement ps2 = null;
				ps2 = con2.prepareStatement("UPDATE `player_data` SET blocks_broken = ? WHERE uuid=?");
				ps2.setInt(1, blocksBroken);
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
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event){
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
			ps = con.prepareStatement("SELECT blocks_placed FROM `player_data` WHERE uuid=?;");
			ps.setString(1, player.getUniqueId().toString());
			results = ps.executeQuery();
			while (results.next()){
				// get previous blocks placed count and + 1
				int blocksPlaced = results.getInt("blocks_placed") + 1;
				Connection con2 = null;
				con2 = MyStats.instance.getHikariDataSource().getConnection();
				PreparedStatement ps2 = null;
				ps2 = con2.prepareStatement("UPDATE `player_data` SET blocks_placed = ? WHERE uuid=?");
				ps2.setInt(1, blocksPlaced);
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
