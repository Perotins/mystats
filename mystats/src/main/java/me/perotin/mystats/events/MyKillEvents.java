package me.perotin.mystats.events;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.perotin.mystats.MyStats;

public class MyKillEvents implements Listener {


	@EventHandler
	public void onMobKill(EntityDeathEvent event){
		// mob kill event
		Entity entity = event.getEntity();
		if(entity instanceof LivingEntity){
			LivingEntity monster = (LivingEntity) entity;
			if(monster instanceof Player){
				return;
			}
			if(monster.getKiller() instanceof Player){
				Player player = (Player) monster.getKiller();
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
					ps = con.prepareStatement("SELECT mob_kills FROM `player_data` WHERE uuid=?;");
					ps.setString(1, player.getUniqueId().toString());
					results = ps.executeQuery();
					while (results.next()){
						// get previous mobs killed count and + 1
						int mobKills = results.getInt("mob_kills") + 1;
						Connection con2 = null;
						con2 = MyStats.instance.getHikariDataSource().getConnection();
						PreparedStatement ps2 = null;
						ps2 = con2.prepareStatement("UPDATE `player_data` SET mob_kills = ? WHERE uuid=?");
						ps2.setInt(1, mobKills);
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

	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		// player death event
		Player player = event.getEntity();
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
			ps = con.prepareStatement("SELECT deaths FROM `player_data` WHERE uuid=?;");
			ps.setString(1, player.getUniqueId().toString());
			results = ps.executeQuery();
			while (results.next()){
				// get previous death count and + 1
				int deaths = results.getInt("deaths") + 1;
				Connection con2 = null;
				con2 = MyStats.instance.getHikariDataSource().getConnection();
				PreparedStatement ps2 = null;
				ps2 = con2.prepareStatement("UPDATE `player_data` SET deaths = ? WHERE uuid=?");
				ps2.setInt(1, deaths);
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
	public void onPlayerKill(EntityDeathEvent event){
		if(event.getEntity().getKiller() instanceof Player && event.getEntity() instanceof Player){
			Player player = (Player) event.getEntity().getKiller();
			// they are dead
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
					ps = con.prepareStatement("SELECT player_kills FROM `player_data` WHERE uuid=?;");
					ps.setString(1, player.getUniqueId().toString());
					results = ps.executeQuery();
					while (results.next()){
						// get previous kill count and + 1
						int playerKills = results.getInt("player_kills") + 1;
						Connection con2 = null;
						con2 = MyStats.instance.getHikariDataSource().getConnection();
						PreparedStatement ps2 = null;
						ps2 = con2.prepareStatement("UPDATE `player_data` SET player_kills = ? WHERE uuid=?");
						ps2.setInt(1, playerKills);
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
}
