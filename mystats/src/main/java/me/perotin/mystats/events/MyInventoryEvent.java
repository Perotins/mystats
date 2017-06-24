package me.perotin.mystats.events;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.perotin.mystats.MyStats;

public class MyInventoryEvent implements Listener {


	@EventHandler
	public void on(InventoryClickEvent event){
		// cancelling if they click, pulling all names and looping through to see if inventory name is a 
		// mystats inventory 
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<String> names = null;
		names = new ArrayList<String>();

		try {
			con = MyStats.instance.getHikariDataSource().getConnection();
			ps = con.prepareStatement("SELECT name FROM player_data;");
			rs = ps.executeQuery();
			while(rs.next()){
				String name = rs.getString("name");
				names.add(name);

			}
			rs.close();
			ps.close();
			con.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String invName = event.getInventory().getName();
		for(String title : names){
			String finalString = title + "'s stats";
			if(invName.equalsIgnoreCase(finalString)){
				event.setCancelled(true);
			}
		}
	}
}
